package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent

class PlaySound(
    type: ActionType = ActionType.PLAYSOUND,
    requirements: RequirementOptions? = RequirementOptions(),
    private val sound: SoundEvent? = null,
    private val volume: Float = 1.0F,
    private val pitch: Float = 1.0F
) : Action(type, requirements) {
    override fun execute(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.debug("Attempting to execute a ${type.identifier} Action: $this")
        if (sound == null) {
            Utils.error("There was an error while executing a Sound Action for player ${player.name}: Sound was somehow null?")
            return
        }
        player.playSound(sound, SoundCategory.MASTER, volume, pitch)
    }

    override fun toString(): String {
        return "PlaySound(type=$type, requirements=$requirements, sound=$sound, volume=$volume, pitch=$pitch)"
    }
}