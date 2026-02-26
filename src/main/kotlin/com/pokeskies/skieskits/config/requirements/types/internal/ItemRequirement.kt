package com.pokeskies.skieskits.config.requirements.types.internal

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class ItemRequirement(
    type: RequirementType = RequirementType.ITEM,
    comparison: ComparisonType = ComparisonType.EQUALS,
    val item: String = "",
    val amount: Int? = null,
    val nbt: CompoundTag? = null,
    val strict: Boolean = true
) : Requirement(type, comparison) {
    override fun passesRequirements(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        val targetAmount = amount ?: 1
        var amountFound = 0

        for (i in 0 until player.inventory.containerSize) {
            val itemStack = player.inventory.getItem(i)
            if (!itemStack.isEmpty) {
                if (isItem(itemStack)) {
                    amountFound += itemStack.count
                }
            }
        }

        Utils.printDebug("Checking a ${type?.identifier} Requirement with items found='$amountFound': $this")

        return when (comparison) {
            ComparisonType.EQUALS -> {
                if (amount != null) {
                    return amountFound == amount
                } else {
                    return amountFound >= 1
                }
            }
            ComparisonType.NOT_EQUALS -> {
                if (amount != null) {
                    return amountFound != amount
                } else {
                    return amountFound == 0
                }
            }
            ComparisonType.GREATER_THAN -> amountFound > targetAmount
            ComparisonType.LESS_THAN -> amountFound < targetAmount
            ComparisonType.GREATER_THAN_OR_EQUALS -> amountFound >= targetAmount
            ComparisonType.LESS_THAN_OR_EQUALS -> amountFound <= targetAmount
        }
    }

    private fun isItem(checkItem: ItemStack): Boolean {
        val newItem = BuiltInRegistries.ITEM.getOptional(Identifier.parse(item))
        if (newItem.isEmpty()) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to an empty or invalid item ID. Item ID: $item, returned: $newItem")
            return false
        }
        if (!checkItem.item.equals(newItem.get())) {
            Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to item not matching. Looking for: ${newItem.get()}, but found: ${checkItem.item}")
            return false
        }

        val nbtCopy = nbt?.copy()

        if (strict && nbtCopy != null) {
            val checkNBT = DataComponentPatch.CODEC.encodeStart(SkiesKits.INSTANCE.nbtOpts, checkItem.componentsPatch)
                .resultOrPartial { error ->
                    Utils.printError("Failed to encode ItemRequirement components for item '$item': $error")
                }.getOrNull() ?: return false

            if (checkNBT != nbtCopy) {
                Utils.printDebug("[REQUIREMENT - ${type?.name}] Failed due to NBT not matching. Looking for: $nbtCopy, but found: $checkNBT")
                return false
            }
        }

        return true
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.entries
    }

    override fun toString(): String {
        return "ItemRequirement(comparison=$comparison, item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}
