package com.pokeskies.skieskits.config.requirements.types.internal

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity

class ItemRequirement(
    type: RequirementType = RequirementType.ITEM,
    comparison: ComparisonType = ComparisonType.EQUALS,
    val item: Item = Items.BARRIER,
    val amount: Int? = null,
    val nbt: NbtCompound? = null,
    val strict: Boolean = true
) : Requirement(type, comparison) {
    override fun passesRequirements(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData): Boolean {
        if (!checkComparison())
            return false

        val targetAmount = amount ?: 1
        var amountFound = 0

        for (itemStack in player.inventory.main) {
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
        if (!checkItem.item.equals(item)) {
            return false
        }

        if (strict && nbt != null) {
            val checkNBT = checkItem.nbt ?: return false

            if (checkNBT != nbt)
                return false
        }

        return true
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return ComparisonType.values().toList()
    }

    override fun toString(): String {
        return "ItemRequirement(comparison=$comparison, item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}
