package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class GiveItem(
    type: ActionType = ActionType.GIVE_XP,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: String = "",
    val amount: Int = 1,
    val nbt: CompoundTag? = null
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        val newItem = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(item))
        if (newItem.isEmpty) {
            Utils.printDebug("[ACTION - ${type.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return
        }
        val itemStack = ItemStack(newItem.get(), amount)

        var nbtCopy = nbt?.copy()

        if (nbtCopy != null) {
            DataComponentPatch.CODEC.decode(SkiesKits.INSTANCE.nbtOpts, nbtCopy).result().ifPresent { result ->
                itemStack.applyComponents(result.first)
            }
        }

        Utils.printDebug("[ACTION - ${type.name}] Player(${player.gameProfile.name}), ItemStack(${itemStack}: $this")

        player.addItem(itemStack)
    }

    override fun toString(): String {
        return "GiveItem(item=$item, amount=$amount, nbt=$nbt)"
    }

}
