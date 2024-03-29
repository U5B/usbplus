package net.usbwire.usbplus.features.health

import gg.essential.elementa.*
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.*
import gg.essential.universal.UMatrixStack
import gg.essential.universal.wrappers.message.*
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.usbwire.usbplus.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.usbwire.usbplus.util.RenderUtil
import java.awt.Color

object Glow {
	fun onRenderTick(context: WorldRenderContext) {
		if (!Config.healthEnabled && !Config.healthGlowingEnabled) return
		val camera = context.camera()
		for (player in context.world().players) {
			if (player == camera.focusedEntity && !camera.isThirdPerson) continue // don't render hitbox on yourself if in third person

			val name = UMessage(UTextComponent(player.name)).unformattedText
			if (Base.playerMap[name] == null) {
				EntityHelper.resetGlowingColor(player)
				continue
			}

			if (Config.healthWhitelistEnabled && Base.playerMap[name]!!.glow == true && !Config.healthWhitelist.lowercase().contains(name.lowercase())) {
				Base.playerMap[name]!!.glow = false
				EntityHelper.resetGlowingColor(player)
				continue
			}

			val color = Base.playerMap[name]!!.health.color
			if (color.alpha > 10) {
				if (Config.healthGlowingEnabled && player.isGlowing) {
					Base.playerMap[name]!!.glow  = true
					EntityHelper.setGlowingColor(player, color)
				} else if (Config.healthEnabled) {
					RenderUtil.drawEntityBox(player, color, context, true, true) // only draw box if player is already not glowing and glowing isn't forced
				}
			}
		}
	}
}