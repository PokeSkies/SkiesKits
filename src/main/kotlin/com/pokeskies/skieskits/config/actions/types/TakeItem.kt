package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.commands.CommandSourceStack
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class TakeItem(
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: String = "",
    val amount: Int = 1,
    val nbt: CompoundTag? = null,
    val strict: Boolean = true
) : Action(ActionType.TAKE_ITEM, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?, gui: SimpleGui?, commandSourceOverride: CommandSourceStack?) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        var removed = 0
        for (i in 0 until player.inventory.containerSize) {
            val stack = player.inventory.getItem(i)
            if (!stack.isEmpty) {
                if (isItem(stack)) {
                    val stackSize = stack.count
                    if (removed + stackSize >= amount) {
                        stack.shrink(amount - removed)
                        player.inventory.setItem(i, stack)
                        break
                    } else {
                        stack.shrink(stackSize)
                        player.inventory.setItem(i, stack)
                    }
                    removed += stackSize
                }
            }
        }
    }

    private fun isItem(checkItem: ItemStack): Boolean {
        val newItem = BuiltInRegistries.ITEM.getOptional(Identifier.parse(item))
        if (newItem.isEmpty()) {
            Utils.printDebug("[ACTION - ${type.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return false
        }
        if (!checkItem.item.equals(newItem.get())) {
            return false
        }

        val nbtCopy = nbt?.copy()

        if (strict && nbtCopy != null) {
            val checkNBT = DataComponentPatch.CODEC.encodeStart(SkiesKits.INSTANCE.nbtOpts, checkItem.componentsPatch)
                .resultOrPartial { error ->
                    Utils.printError("Failed to encode TakeItem components for item '$item': $error")
                }.getOrNull() ?: return false

            if (checkNBT != nbtCopy)
                return false
        }

        return true
    }

    override fun toString(): String {
        return "TakeItem(item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}

