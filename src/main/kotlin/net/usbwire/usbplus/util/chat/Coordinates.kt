package net.usbwire.usbplus.util.chat

import gg.essential.universal.wrappers.message.*
import kotlinx.serialization.Serializable
import net.minecraft.text.*
import net.usbwire.usbplus.util.Util

/**
 * Creates a message with coordinates and buttons to 
 * copy coordinates or add a JM/Xaero waypoint if available.
 * TODO: split each compoment into multiple functions
 * TODO: directly create the waypoint if possible
 */
object Coordinates {
	var supportsXaero = true
	var supportsJourneymap = true
	fun coordinateBuilder(
		name: String,
		x: Int,
		y: Int,
		z: Int,
		dimension: String,
		message: UMessage = UMessage()
	): UMessage {

		// prefix
		val baseComponent = UTextComponent("'${name}':")
		message.addTextComponent(baseComponent)

		// copy
		val copyComponent = UTextComponent(" §a(${x}, ${y}, ${z})§r")
		copyComponent.setClick(ClickEvent.Action.COPY_TO_CLIPBOARD, "${x} ${y} ${z}")
		copyComponent.setHover(HoverEvent.Action.SHOW_TEXT, UTextComponent("§aClick to copy coordinates to clipboard!§r"))
		message.addTextComponent(copyComponent)

		// xaero minimap support
		if (supportsXaero == true) {
			val xaeroComponent = xaeroBuilder(name, x, y, z, dimension)
			if (xaeroComponent == null) {
				supportsXaero = false
			} else {
				message.addTextComponent(xaeroComponent)
			}
		}

		// journeymap support
		if (supportsJourneymap == true) {
			val journeymapComponent = journeymapBuilder(name, x, y, z, dimension)
			if (journeymapComponent == null) {
				supportsJourneymap = false
			} else {
				message.addTextComponent(journeymapComponent)
			}
		}
		return message
	}

	private fun xaeroBuilder(
		name: String,
		x: Int,
		y: Int,
		z: Int,
		dimension: String
	): UTextComponent? {
		try { // https://github.com/U5B/jsmacros/blob/eb9e5aafa2ac56fef6cd74c432b3b8ac07840d25/scripts/lib/xaero.ts#L69
			Class.forName("xaero.common.XaeroMinimapSession")
			// technically dimension but who cares
			// TODO: map poi.region to dimension and set waypoint in correct dimension
			val currentDimension = dimension.replace(":", "$")
			val xaeroColor = Util.Color.xaero["dark_red"]
			val minecraftColor = Util.Color.minecraft["dark_red"]
			val xaeroComponent = UTextComponent(" ${minecraftColor}[XAERO]§r")
			val waypoint =
				"/xaero_waypoint_add:${name}:${name[0].uppercase()}:${x}:${y}:${z}:${xaeroColor}:false:0:Internal_dim%${currentDimension}_waypoints"
			// val shareableWaypoint =
			// "/xaero-waypoint:${poi.name}:${poi.name[0].uppercase()}:${poi.coordinates.x}:${poi.coordinates.y}:${poi.coordinates.z}:${xaeroColor}:false:0:Internal-dim%${currentWorld}-waypoints"
			xaeroComponent.setClick(ClickEvent.Action.RUN_COMMAND, waypoint)
			xaeroComponent.setHover(HoverEvent.Action.SHOW_TEXT, UTextComponent("${minecraftColor}Click to create a new xaero waypoint!§r"))
			return xaeroComponent
		} catch (e: Exception) {
			return null
		}
	}

	private fun journeymapBuilder(
		name: String,
		x: Int,
		y: Int,
		z: Int,
		dimension: String
	): UTextComponent? {
		try {
			Class.forName("journeymap.client.JourneymapClient")
			val minecraftColor = Util.Color.minecraft["aqua"]
			val journeymapComponent = UTextComponent(" ${minecraftColor}[JM]§r")
			val waypoint = "/jm wpedit [name:\"${name}\", x:${x}, y:${y}, z:${z}, dim:${dimension}]"
			journeymapComponent.setClick(ClickEvent.Action.RUN_COMMAND, waypoint)
			journeymapComponent.setHover(HoverEvent.Action.SHOW_TEXT, UTextComponent("${minecraftColor}Click to create a new journey map waypoint!§r"))
			return journeymapComponent
		} catch (e: Exception) {
			return null
		}
	}

	@Serializable
	data class Coordinates(val x: Int, val y: Int, val z: Int)
}
