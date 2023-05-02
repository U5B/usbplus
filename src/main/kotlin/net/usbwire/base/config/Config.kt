package net.usbwire.base.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import gg.essential.vigilance.Vigilant
import java.awt.Color
import java.io.File
import java.nio.file.Path
import net.minecraft.client.gui.screen.Screen
import net.usbwire.base.BaseMod
import net.usbwire.base.features.Poi
import net.usbwire.base.features.HealthHud
import net.usbwire.base.util.Util

val configFile = "${BaseMod.configPath}/config.toml"

object Config : Vigilant(File(configFile)) {
  // *POI*
  var poiEnabled = false
  var poiUrl = "https://raw.githubusercontent.com/U5B/Monumenta/main/out/pois.json" // github url

  // *Common Health*
  var healthUpdateTicks = 1
  var healthBaseColor = Color.WHITE
  var healthGoodColor = Color.GREEN
  var healthGoodPercent = 1.0f
  var healthLowColor = Color.YELLOW
  var healthLowPercent = 0.7f
  var healthCriticalColor = Color.RED
  var healthCriticalPercent = 0.4f

  var healthHurtEnabled = false
  var healthHurtColor = Color.PINK
  var healthEffectEnabled = false
  var healthEffectColor = Color.DARK_GRAY

  // *GlowHealth*
  var healthEnabled = false

  // *DrawHealth*
  var healthDrawEnabled = false
  var healthDrawX = 0.0f
  var healthDrawY = 0.0f
  var healthDrawAlign = 0

  init {
    Util.createDirectory(Path.of(configFile))

    category("POI") {
      checkbox(::poiEnabled, "Toggle POI", "", true) { Poi.changeState(poiEnabled) }
      button(
          "Refresh POIs",
          """
        Fetches from ${poiUrl} for the latest data.
        """.trimIndent(),
          "",
          true,
      ) { Poi.fetchPoiData() }
      text(::poiUrl, "Internal POI URL", hidden = true)
    }

    category("Health") {
      subcategory("Hitbox") { checkbox(::healthEnabled, "Toggle GlowHealth") }
      subcategory("Draw") {
        checkbox(::healthDrawEnabled, "Toggle DrawHealth")
        percentSlider(::healthDrawX, "X Position", "", true) { HealthHud.xPos.set(healthDrawX) }
        percentSlider(::healthDrawY, "Y Position", "", true) { HealthHud.yPos.set(healthDrawY) }
        selector(::healthDrawAlign, "Text Alignment", options = listOf("left", "center", "right"))
      }
      subcategory("General") {
        slider(::healthUpdateTicks, "Update Rate In Ticks", min = 1, max = 300) // really? 15 seconds?
        percentSlider(::healthGoodPercent, "Good HP percent", "100% HP")
        percentSlider(::healthLowPercent, "Low HP percent", "70% HP")
        percentSlider(::healthCriticalPercent, "Critical HP percent", "40% HP")
      }
      subcategory("Color") {
        color(::healthBaseColor, "Base HP color", "White (#ffffff) doesn't show.")
        color(::healthGoodColor, "Good HP color")
        color(::healthLowColor, "Low HP color")
        color(::healthCriticalColor, "Critical HP color")
        color(::healthHurtColor, "Hurt color")
        color(::healthEffectColor, "Negative effect (Wither/Poison) color")
      }
    }

    initialize() // this needs to be called for whatever reason so that configs actually save
  }
}

class ModMenu : ModMenuApi {
  override fun getModConfigScreenFactory() = ConfigScreenFactory<Screen> { Config.gui() }
}
