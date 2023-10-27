package net.usbwire.usbplus.features

import gg.essential.universal.wrappers.message.*
import gg.essential.universal.wrappers.UPlayer
import gg.essential.universal.UChat
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.passive.VillagerEntity
import net.usbwire.usbplus.USBPlus
import net.usbwire.usbplus.config.Config
import net.usbwire.usbplus.util.Util

/**
 * Pickup Filter toggle when sneaking like RLCraft
 */
object Pickup {
	var ticksSneaking = 0
	var isSneaking = false;
	fun onWorldTick() {
		val player = USBPlus.mc.player;
		if (player == null || !Config.pickupEnabled) return
		// velocity check
		val velocity = player.velocity
		if (!(player.isSpectator || player.isCreative) &&
		player.isSneaking &&
		player.isOnGround &&
		player.lastRenderPitch >= 55.0 &&
		(Math.abs(velocity.x) <= 0.05 && Math.abs(velocity.z) <= 0.05)
		) {
			// turn on pickup
			ticksSneaking++
			if (isSneaking == false && ticksSneaking > Config.pickupDelay) {
				Util.say("/pu all")
				isSneaking = true
			}
		} else if (!player.isSneaking) { // we only want this code to trigger when they stop sneaking
			// turn off pickup
			if (ticksSneaking > Config.pickupDelay) {
				val pickupMode = getMode(Config.pickupMode)
				Util.say("/pu ${pickupMode}")
			}
			isSneaking = false
			ticksSneaking = 0
		}
	}

	fun getMode (mode: Int) : String {
		return when (mode) {
			0 -> "interesting"
			1 -> "lore"
			2 -> "tiered"
			else -> "interesting"
		}
	}
}