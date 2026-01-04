package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.gui.KitsMenuGui
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.server.level.ServerPlayer

class OpenMenu(
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val id: String = ""
) : Action(ActionType.FIRST_PAGE, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?, gui: SimpleGui?) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}) $this")

        val menuConfig = ConfigManager.MENUS[id]
        if (menuConfig == null) {
            Utils.printError("The menu $id was not a valid menu id.")
            return
        }

        KitsMenuGui(player, menuConfig).open()
    }

    override fun toString(): String {
        return "OpenMenu()"
    }
}
