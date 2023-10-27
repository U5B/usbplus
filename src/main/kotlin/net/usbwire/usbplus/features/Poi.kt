package net.usbwire.usbplus.features

import gg.essential.api.EssentialAPI
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import net.usbwire.usbplus.USBPlus
import net.usbwire.usbplus.commands.PoiCommand
import net.usbwire.usbplus.commands.parsers.*
import net.usbwire.usbplus.config.Config
import net.usbwire.usbplus.util.Util
import net.usbwire.usbplus.util.chat.Coordinates
import java.io.FileNotFoundException
import java.net.URL
import java.nio.file.*
import gg.essential.universal.wrappers.message.UMessage

/**
 * A really convulted file that returns POI data from a file.
 * Ported directly from U5B/jsmacros
 */
object Poi {
	@Serializable
	data class JsonPoi(
		val name: String,
		val shard: String,
		val region: String?,
		val subregion: String?,
		val coordinates: Coordinates.Coordinates?
	)

	val poiPath = Path.of("${USBPlus.configPath}/pois.json")
	var poiMap: Map<String, JsonPoi> = emptyMap()
	var poiSuggestions: List<String> = emptyList()

	val mapShardToDimension = mapOf(
		"King's Valley" to "monumenta:valley",
		"Celsian Isles" to "monumenta:isles",
		"Architect's Ring" to "monumenta:ring",
	)

	fun fetchPoiData() {
		try {
			URL(Config.poiUrl).openStream().use {
				val project = Json.decodeFromStream<Map<String, JsonPoi>>(it) // read JSON from a URL
				updatePoiData(project)
				savePoiData()
			}
		} catch (e: FileNotFoundException) {
			USBPlus.logger.error { "Invalid URL: ${Config.poiUrl}" }
			USBPlus.logger.error { "Resetting URL to default!" }
			Config.poiUrl = "https://raw.githubusercontent.com/U5B/Monumenta/main/out/pois.json"
		}
	}

	fun loadPoiData() {
		if (Files.notExists(poiPath) == true) return fetchPoiData() // don't use file if it doesn't exist
		Files.newInputStream(poiPath).use {
			val project = Json.decodeFromStream<Map<String, JsonPoi>>(it) // read JSON from file
			updatePoiData(project)
		}
	}

	private fun updatePoiData(project: Map<String, JsonPoi>) {
		poiMap = project
		makeCommandSuggestions()
	}

	private fun savePoiData() {
		if (poiMap.isEmpty() == true) return
		Util.createPath(poiPath)
		Files.newOutputStream(poiPath).use { Json.encodeToStream(poiMap, it) }
	}

	var storedDimension: String = ""
	fun makeCommandSuggestions(): List<String> {
		storedDimension = Util.getDimension()
		val suggestions = ArrayList<String>()
		val isInShard = mapShardToDimension.values.contains(Util.getDimension())
		for (poi in poiMap.values) {
			// Region Name may change in the future: don't break if it does
			val dimension = mapShardToDimension[poi.shard] ?: Util.getDimension()
			// check to see if we are on the same shard BUT allow it to be used on plots and other shards
			if (isInShard && dimension != Util.getDimension()) continue
			suggestions.add(poi.name)
		}
		poiSuggestions = suggestions.toList()
		return poiSuggestions
	}

	fun getCommandSuggestions(): List<String> {
		if (poiSuggestions.isEmpty() || storedDimension != Util.getDimension()) return makeCommandSuggestions()
		return makeCommandSuggestions()
	}

	fun searchPoi(input: String): ArrayList<JsonPoi>? {
		// acutal logic
		val response = ArrayList<JsonPoi>()
		// exact match
		poiMap.forEach { poi ->
			if (input == poi.value.name) {
				response.add(poi.value)
				return response
			}
		}
		// if tag matches
		poiMap.forEach { poi ->
			val tags = Util.trimString(poi.value.name).split(' ')
			if (tags.contains(Util.trimString(input)) == true) response.add(poi.value)
		}
		// otherwise check if those letters are contained in any poi
		if (response.size == 0) {
			poiMap.forEach { poi ->
				if (Util.cleanString(poi.value.name).contains(Util.cleanString(input)) == true)
					response.add(poi.value)
			}
		}
		if (response.size > 0) return response
		return null
	}

	fun responsePoi(poi: JsonPoi) {
		if (poi.coordinates == null) {
			Util.chat("'${poi.name}': No coordinates for POI found.")
			return
		}
		val dimension = mapShardToDimension[poi.shard] ?: Util.getDimension()
		val name = poi.name
		val x = poi.coordinates.x
		val y = poi.coordinates.y
		val z = poi.coordinates.z
		val message = Coordinates.coordinateBuilder(name, x, y, z, dimension)
		Util.chat(message)
	}

	var firstRun = true
	fun configChanged(value: Boolean = Config.poiEnabled) {
		if (value == true && firstRun == true) {
			EssentialAPI.getCommandRegistry().registerParser(PoiName::class.java, PoiParser())
			firstRun = false
		}
		if (value == true) {
			loadPoiData()
			PoiCommand.register()
		} else if (value == false) {
			EssentialAPI.getCommandRegistry().unregisterCommand(PoiCommand)
		}
	}

	val bountyRegex = """^§r§fYour bounty for today is §r§b(.*)§r§f!$""".toRegex()
	fun onChat(message: UMessage): Boolean {
		if (!Config.poiEnabled || !bountyRegex.matches(message.formattedText)) return false
		val result = bountyRegex.matchEntire(message.formattedText)
		val (poiString) = result!!.destructured
		val searchPoi = searchPoi(poiString)
		if (searchPoi == null) {
			Util.chat("'$poiString': No POI found!")
			return false
		}
		for (poi in searchPoi) {
			responsePoi(poi)
		}
		return true
	}
}
