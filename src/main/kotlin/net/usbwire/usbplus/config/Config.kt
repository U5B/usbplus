package net.usbwire.usbplus.config

import com.terraformersmc.modmenu.api.*
import gg.essential.vigilance.Vigilant
import net.minecraft.client.gui.screen.Screen
import net.usbwire.usbplus.USBPlus
import net.usbwire.usbplus.features.*
import net.usbwire.usbplus.features.health.*
import net.usbwire.usbplus.util.Util
import java.awt.Color
import java.io.File
import java.nio.file.Path

val configFile = "${USBPlus.configPath}/config.toml"

object Config : Vigilant(File(configFile)) {
	// *POI*
	var poiEnabled = true
	var poiUrl = "https://raw.githubusercontent.com/U5B/Monumenta/main/out/pois.json" // github url

	// *Compass*
	var compassEnabled = true

	// *Common Health*
	// Health Whitelist
	var healthWhitelistEnabled = false
	var healthWhitelist = ""
	var healthUpdateTicks = 1

	// *Health Color Toggles & Percentages*
	var healthHurtEnabled = false
	var healthEffectEnabled = false
	var healthGoodPercent = 9.0f
	var healthLowPercent = 0.7f
	var healthCriticalPercent = 0.4f
	var healthFillPercent = 0.0f

	// *Health Colors*
	var healthBaseColor = Color.LIGHT_GRAY
	var healthGoodColor = Color.GREEN
	var healthLowColor = Color.YELLOW
	var healthCriticalColor = Color.RED
	var healthHurtColor = Color.ORANGE
	var healthEffectColor = Color.GRAY

	// *BoxHealth*
	var healthEnabled = true
	var healthGlowingEnabled = true

	// *DrawHealth*
	var healthDrawEnabled = true
	var healthDrawX = 0.0f
	var healthDrawY = 0.0f
	var healthDrawAlign = 0.0f
	var healthDrawAlignExtraRight = false
	var healthDrawScale = 1.0f
	var healthDrawDamageEnabled = false
	var healthDrawDamageDelay = 10
	var healthDrawSort = 0

	// *Pickup*
	var pickupEnabled = false
	var pickupDelay = 20
	var pickupMode = 0

	// *Debug*
	var debugEnabled = false
	var debugContainer = false

	init {
		Util.createDirectory(Path.of(configFile))

		category("POI") {
			switch(::poiEnabled, "Toggle POI", "Type /poi to get started!", triggerActionOnInitialization = false) { Poi.configChanged(it) }
			text(::poiUrl, "Internal POI URL", "Should not be changed unless you know what you are doing!")
			button("Refresh POIs", "Fetches from ${poiUrl} for the latest data", triggerActionOnInitialization = false) { Poi.fetchPoiData() }
		}

		category("Compass") {
			switch(::compassEnabled, "Toggle Compass", "Trigger by left clicking with a compass.", triggerActionOnInitialization = false) { Compass.configChanged(it) }
		}

		category("Health Colors") {
			subcategory("Color") {
				color(::healthBaseColor, "Base HP color", "Alpha under 10 doesn't show", allowAlpha = true)
				color(::healthGoodColor, "Good HP color", allowAlpha = true)
				percentSlider(::healthGoodPercent, "Good HP percent", "100%% HP")
				color(::healthLowColor, "Low HP color", allowAlpha = true)
				percentSlider(::healthLowPercent, "Low HP percent", "70%% HP")
				color(::healthCriticalColor, "Critical HP color", allowAlpha = true)
				percentSlider(::healthCriticalPercent, "Critical HP percent", "40%% HP")
				color(::healthHurtColor, "Hurt color", allowAlpha = true)
				switch(::healthHurtEnabled, "Hurt Color Toggle")
				color(::healthEffectColor, "Fire Color", allowAlpha = true)
				switch(::healthEffectEnabled, "Fire Color Toggle")
			}
		}

		category("Health Draw") {
			subcategory("Draw") {
				switch(::healthDrawEnabled, "Toggle DrawHealth")
				percentSlider(::healthDrawX, "X Position Percent", triggerActionOnInitialization = false) {
					HUD.xPos.set(it)
					Base.configDirty = true
				}
				percentSlider(::healthDrawY, "Y Position Percent", triggerActionOnInitialization = false) {
					HUD.yPos.set(it)
					Base.configDirty = true
				}
				percentSlider(::healthDrawAlign, "Text Alignment Percent", triggerActionOnInitialization = false) {
					HUD.alignPos.set(it)
					Base.configDirty = true
				}
				decimalSlider(
					::healthDrawScale,
					"Text Scale",
					min = 0.5f,
					max = 4.0f,
					decimalPlaces = 1,
					triggerActionOnInitialization = false
				) {
					HUD.textSize.set(it)
					Base.configDirty = true
				}
				switch(::healthDrawDamageEnabled, "Display Recent Damage")
				slider(::healthDrawDamageDelay, "Damage Hide Delay in Ticks", min = 1, max = 60)
				switch(::healthDrawAlignExtraRight, "Recent Damage Alignment", triggerActionOnInitialization = false) {
					HUD.alignRightExtra.set(it)
					Base.configDirty = true
				}
				selector(::healthDrawSort, "Sort player list by", options = listOf("alphabetical", "health (low to high)", "health (high to low)", "time"))
			}
		}

		category("Health General") {
			subcategory("Hitbox") {
				switch(::healthEnabled, "Toggle BoxHealth")
				switch(::healthGlowingEnabled, "Color Glowing Players!")
				percentSlider(::healthFillPercent, "Alpha Percentage of Inside Box", "Set to 0 to disable.")
			}
			subcategory("General") {
				slider(::healthUpdateTicks, "Update Rate In Ticks", min = 1, max = 20)
				switch(::healthWhitelistEnabled, "Toggle Whitelist")
				paragraph(::healthWhitelist, "Player names separated by spaces to allow")
			}
		}

		category("Misc") {
			switch(::pickupEnabled, "Sneak to toggle between pickup states")
			slider(::pickupDelay, "Sneaking delay in ticks", min = 1, max = 40)
			selector(::pickupMode, "Pickup Mode", options = listOf("interesting", "lore", "tiered"))
		}

		category("_Debug") {
			switch(::debugEnabled, "Toggle Debug")
			switch(::debugContainer, "Print contents of opened chests")
		}

		initialize() // this needs to be called for whatever reason so that configs actually save
	}
}

class ModMenu : ModMenuApi {
	override fun getModConfigScreenFactory() = ConfigScreenFactory<Screen> { Config.gui() }
}
