package net.usbwire.usbplus.util

import gg.essential.universal.UChat
import gg.essential.universal.wrappers.UPlayer
import gg.essential.universal.wrappers.message.*
import net.usbwire.usbplus.USBPlus
import java.nio.file.*

/**
 * Random utilities for debugging
 */
object Util {
	fun createPath(file: Path): Boolean {
		createDirectory(file)
		if (Files.exists(file) == false) {
			Files.createFile(file)
			return true
		}
		return false
	}

	fun createDirectory(file: Path): Boolean {
		if (Files.exists(file.parent) == false) {
			Files.createDirectories(file.parent)
			return true
		}
		return false
	}

	fun cleanString(str: String): String {
		var string = str.replace(Regex("/'/g"), "")
		string = string.replace(Regex("/\\n/g"), "")
		string = string.replace(Regex("/ /g"), "")
		string = trimString(string)
		return string
	}

	fun trimString(str: String): String {
		var string = str.trim()
		string = string.lowercase()
		return string
	}

	fun chat(str: String) {
		UChat.chat(UChat.addColor("§7[§a${USBPlus.name}§7]§r ${str}"))
	}

	fun chat(message: UMessage) {
		val prefix = UTextComponent(UChat.addColor("§7[§a${USBPlus.name}§7]§r "))
		message.addTextComponent(0, prefix)
		message.chat()
	}

	fun say(str: String) {
		if (str.startsWith("/")) {
			UPlayer.getPlayer()!!.networkHandler.sendCommand(str.substring(1));
		} else {
			UPlayer.getPlayer()!!.networkHandler.sendChatMessage(str);
		}
	}

	fun getDimension (): String {
		if (USBPlus.mc.world == null) {
			return "";
		}
		return USBPlus.mc.world!!.registryKey.value.toString()
	}

	object Color {
		val xaero =
			mapOf(
				"black" to "0",
				"dark_blue" to "1",
				"dark_green" to "2",
				"dark_aqua" to "3",
				"dark_red" to "4",
				"dark_purple" to "5",
				"gold" to "6",
				"gray" to "7",
				"dark_gray" to "8",
				"blue" to "9",
				"green" to "10",
				"aqua" to "11",
				"red" to "12",
				"light_purple" to "13",
				"yellow" to "14",
				"white" to "15"
			)
		val minecraft =
			mapOf(
				"black" to "§0",
				"dark_blue" to "§1",
				"dark_green" to "§2",
				"dark_aqua" to "§3",
				"dark_red" to "§4",
				"dark_purple" to "§5",
				"gold" to "§6",
				"gray" to "§7",
				"dark_gray" to "§8",
				"blue" to "§9",
				"green" to "§a",
				"aqua" to "§b",
				"red" to "§c",
				"light_purple" to "§d",
				"yellow" to "§e",
				"white" to "§f"
			)
	}
}
