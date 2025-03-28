package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore

class KitItem(
    val item: Item = Items.AIR,
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    @SerializedName("components", alternate = ["nbt"])
    val components: CompoundTag? = null
) {
    fun giveItem(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        val itemStack = ItemStack(item)

        if (components != null) {
            DataComponentPatch.CODEC.decode(SkiesKits.INSTANCE.nbtOpts, components).result().ifPresent { result ->
                itemStack.applyComponents(result.first)
            }
        }

        val dataComponents = DataComponentPatch.builder()

        if (name != null) {
            dataComponents.set(DataComponents.ITEM_NAME, Utils.deserializeText(Utils.parsePlaceholders(player, name, kitId, kit, kitData)))
        }

        if (lore.isNotEmpty()) {
            val parsedLore: MutableList<String> = mutableListOf()
            for (line in lore.stream().map { Utils.parsePlaceholders(player, it, kitId, kit, kitData) }.toList()) {
                if (line.contains("\n")) {
                    line.split("\n").forEach { parsedLore.add(it) }
                } else {
                    parsedLore.add(line)
                }
            }
            dataComponents.set(DataComponents.LORE, ItemLore(parsedLore.stream().map { Utils.deserializeText(it) }.toList()))
        }

        itemStack.applyComponents(dataComponents.build())

        itemStack.count = amount

        if (!player.addItem(itemStack)) {
            player.serverLevel().addFreshEntity(ItemEntity(player.serverLevel(), player.x, player.y, player.z, itemStack))
        }
    }
}
