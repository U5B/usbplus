package net.usbwire.usbplus.features

import gg.essential.universal.wrappers.message.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.passive.VillagerEntity
import net.usbwire.usbplus.USBPlus
import net.usbwire.usbplus.config.Config
import net.usbwire.usbplus.util.Util
import net.usbwire.usbplus.util.chat.ChatUtil
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.PlayerInventory

/**
 * Debugging for future NPC scraper
 * TODO: actually create NPC scraper
 */
object Debug {
	var clicked = false
	fun onWorldTick() {
		if (Config.debugEnabled == false) return
		val click = USBPlus.mc.mouse.wasMiddleButtonClicked()
		if (click == true && clicked == false) {
			if (USBPlus.mc.targetedEntity != null) {
				printEntityInfo(USBPlus.mc.targetedEntity)
			} else {
				printNearbyPlayers()
			}
		}
		clicked = click
	}

	fun printNearbyPlayers() {
		val entities = USBPlus.mc.world!!.entities
		if (entities == null) return
		for (entity in entities) {
			if (entity == USBPlus.mc.player) continue
			if (entity.distanceTo(USBPlus.mc.player) >= 5.0f) continue
			printEntityInfo(entity)
		}
	}

	fun printEntityInfo(entity: Entity?) {
		if (entity == null) return
		val textCustomName = entity.customName
		Util.chat("Entity Debug Information:")
		val name = UMessage(UTextComponent(entity.name)).unformattedText
		// val displayName = UMessage(UTextComponent(entity.displayName)).unformattedText
		val customName = if (textCustomName == null) "null" else UMessage(UTextComponent(textCustomName)).unformattedText
		val entityType = UMessage(UTextComponent(entity.type.untranslatedName)).unformattedText
		Util.chat("Name: ${name}")
		// Util.chat("DisplayName: ${displayName}")
		Util.chat("CustomName: ${customName}")
		Util.chat("Type: ${entityType}")
		Util.chat("Pos: (${entity.x}, ${entity.y}, ${entity.z})")
	}

	fun openScreen (client: MinecraftClient, screen: Screen) {
		if (!Config.debugContainer) return
		if (screen !is HandledScreen<*>) return
		if (screen !is GenericContainerScreen && screen !is ShulkerBoxScreen) return
		var container = screen.getScreenHandler()
		var strings = "```\n"
		val lineSeperator = System.getProperty("line.separator")
		var itemList = container.slots.stream()
		.filter { slot -> run { slot.hasStack() && slot.inventory !is PlayerInventory } }
		.map(Slot::getStack)
		.toList()
		var itemMap = condenseItems(itemList);
		var lastItem = ""
		for (mappedItem in itemMap) {
			val item = mappedItem.key;
			val count = mappedItem.value;
			val name = UMessage(item.name).unformattedText
			val line = """+${count} ${name}"""
			lastItem = line
			strings += "${line} ${lineSeperator}"
		}
		strings += "```"
		Util.chat(ChatUtil.clipboardBuilder("Container with \"${lastItem}\"", strings))
	}

	fun condenseItems(list: List<ItemStack>) : Map<ItemStack, Int> {
		val map = mutableMapOf<ItemStack, Int>()
		for (newStack in list) {
			var owo = false
			val newStackCopy = newStack.copy()
			newStackCopy.setCount(1);
			for (otherStack in map.keys) {
				if (ItemStack.canCombine(otherStack, newStackCopy)) {
					val newCount = map.getOrDefault(otherStack, 0) + newStack.count
					map.remove(otherStack)
					map.set(otherStack, newCount)
					owo = true
					break;
				}
			}
			if (!owo) {
				map.set(newStackCopy, newStack.count)
			}
		}
		return map;
	}
}
