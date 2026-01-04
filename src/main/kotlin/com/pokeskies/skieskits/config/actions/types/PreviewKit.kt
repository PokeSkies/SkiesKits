package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.server.level.ServerPlayer

class PreviewKit(
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val id: String = "",
) : Action(ActionType.PREVIEW_KIT, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?, gui: SimpleGui?) {
        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}) $this")

        val kit = ConfigManager.KITS[id]
        if (kit == null) {
            Utils.printError("The kit $id was not a valid kit id.")
            return
        }

        val preview = kit.createPreview(player) ?: run {
            Utils.printError("The kit $id does not have a preview menu configured or the preview id was invalid.")
            return
        }

        preview.open()
    }

    override fun toString(): String {
        return "PreviewKit(id=$id)"
    }
}
