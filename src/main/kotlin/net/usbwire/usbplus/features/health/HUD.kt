package net.usbwire.usbplus.features.health

import gg.essential.elementa.*
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.*
import gg.essential.universal.UMatrixStack
import gg.essential.universal.wrappers.message.*
import net.usbwire.usbplus.config.Config
import net.usbwire.usbplus.hud.CustomCenterConstraint
import net.usbwire.usbplus.features.health.Base
import java.awt.Color
import java.text.DecimalFormat

val DEC = DecimalFormat("0.0")

object HUD {
	val xPos: State<Float> = BasicState(Config.healthDrawX)
	val yPos: State<Float> = BasicState(Config.healthDrawY)
	val alignPos: State<Float> = BasicState(Config.healthDrawAlign)
	val textSize: State<Number> = BasicState(Config.healthDrawScale)

	val alignRightExtra: State<Boolean> = BasicState(Config.healthDrawAlignExtraRight)

	val window by Window(ElementaVersion.V2, 60)
	val container = UIContainer().constrain {
		x = CustomCenterConstraint(xPos)
		y = CustomCenterConstraint(yPos)
		width = ChildBasedMaxSizeConstraint()
		height = ChildBasedSizeConstraint()
	} childOf window

	fun createPlayer (name: String): Base.PlayerHPDraw {
		// create states
		val nameS = BasicState("${name}:")
		val healthS = BasicState("0/0 ❤")
		val absorptionS = BasicState("")
		val damageS = BasicState("")
		val hpColorS = BasicState(Color.WHITE)
		val damageColorS = BasicState(Color.WHITE)

		// root container (contains everything)
		val rootC = UIContainer().constrain {
			x = CustomCenterConstraint(alignPos)
			y = SiblingConstraint(2f + textSize.get().toFloat())
			width = ChildBasedSizeConstraint()
			height = ChildBasedMaxSizeConstraint()
		}
		rootC.componentName = name

		val nameC = UIText().bindText(nameS).constrain {
			x = SiblingConstraint("l".width(textSize.get().toFloat()))
			color = hpColorS.constraint
			textScale = textSize.pixels
		}
		nameC.componentName = "name"
		val healthC = UIText().bindText(healthS).constrain {
			x = SiblingConstraint("l".width(textSize.get().toFloat()))
			color = hpColorS.constraint
			textScale = textSize.pixels
		}
		healthC.componentName = "health"
		val absorptionC = UIText().bindText(absorptionS).constrain {
			x = SiblingConstraint("l".width(textSize.get().toFloat()))
			color = Color.ORANGE.toConstraint()
			textScale = textSize.pixels
		}
		absorptionC.componentName = "absorption"
		val damageC = UIText().bindText(damageS).constrain {
			x = SiblingConstraint("l".width(textSize.get().toFloat()))
			color = damageColorS.constraint
			textScale = textSize.pixels
		}
		damageC.componentName = "damage"
		if (alignRightExtra.get()) { // reverse order
			rootC.addChild(damageC)
			rootC.addChild(nameC)
			rootC.addChild(healthC)
			rootC.addChild(absorptionC)
		} else {
			rootC.addChild(nameC)
			rootC.addChild(healthC)
			rootC.addChild(absorptionC)
			rootC.addChild(damageC)
		}
		val states = Base.PlayerHPStates(nameS, healthS, absorptionS, damageS, hpColorS, damageColorS)
		return Base.PlayerHPDraw(rootC, states)
	}

	fun updatePlayer (player: Base.PlayerHP, hp: Base.HealthData) {
		// set color
		player.draw!!.states.healthColor.set(hp.color)

		// health
		val maxHp = DEC.format(hp.max)
		val currentHp = DEC.format(hp.current)
		player.draw!!.states.health.set("${currentHp}/${maxHp} ❤")

		// absorption
		if (hp.absorption > 0) {
			val abHp = DEC.format(hp.absorption)
			player.draw!!.states.absorption.set("+${abHp}")
		} else {
			player.draw!!.states.absorption.set("")
		}

		// damage/heal change
		if (Config.healthDrawDamageEnabled == true) {
			if (player.health.current != hp.current) {
				val changeHp = (hp.current + hp.absorption) - (player.health.current + player.health.absorption)
				val damageHp = DEC.format(changeHp)
				if (changeHp > 0.1) {
					player.draw!!.states.damage.set("+${damageHp}")
					player.draw!!.states.damageColor.set(Config.healthGoodColor)
					player.draw!!.damageTick = -1
				} else if (changeHp < -0.1) {
					player.draw!!.states.damage.set("${damageHp}") // no negative sign needed
					player.draw!!.states.damageColor.set(Config.healthCriticalColor)
					player.draw!!.damageTick = -1
				}
			} else if (player.draw!!.damageTick > Config.healthDrawDamageDelay) {
				player.draw!!.states.damage.set("")
			} else {
				player.draw!!.damageTick++
			}
		} else {
			player.draw!!.states.damage.set("")
		}
	}

	fun add (player: Base.PlayerHPDraw) {
		if (player.root.hasParent == false) {
			container.addChild(player.root)
		}
	}

	fun remove (player: Base.PlayerHPDraw) {
		container.removeChild(player.root)
	}

	fun sort (playerMap: MutableMap<String, Base.PlayerHP>) {
		// sort container by max health and percent
		if (Config.healthDrawSort == 0) { // name
			container.children.sortBy { it.componentName }
		} else if (Config.healthDrawSort == 1) { // health (low to high)
			container.children.sortBy { playerMap[it.componentName]!!.health.percent }
		} else if (Config.healthDrawSort == 2) { // health (high to low)
			container.children.sortByDescending { playerMap[it.componentName]!!.health.percent }
		} // time
	}

	fun clear () {
		if (container.children.size > 0) container.clearChildren()
	}

	fun draw(matrix: UMatrixStack) {
		if (Config.healthDrawEnabled == false) return
		if (container.children.size > 0) window.draw(matrix)
	}
}