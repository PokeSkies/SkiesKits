package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class GiveItem(
    type: ActionType = ActionType.GIVE_XP,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: NbtCompound? = null
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        val itemStack = ItemStack(item, amount)
        if (nbt != null) {
            itemStack.nbt = nbt
        }

        player.giveItemStack(itemStack)
    }

    override fun toString(): String {
        return "GiveItem(item=$item, amount=$amount, nbt=$nbt)"
    }

}