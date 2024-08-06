package com.pokeskies.skieskits.config

import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class KitItem(
    val item: Item = Items.AIR,
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: CompoundTag? = null
) {
    fun giveItem(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData) {
        val itemStack = ItemStack(item)

        if (nbt != null) {
            itemStack.nbt = nbt
        }

        if (name != null) {
            itemStack.setCustomName(Utils.deserializeText(Utils.parsePlaceholders(player, name, kitId, kit, kitData)))
        }

        if (lore.isNotEmpty()) {
            val displayNBT = itemStack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY)
            val nbtLore = ListTag()
            for (line in lore) {
                nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(
                    Utils.deserializeText(Utils.parsePlaceholders(player, line, kitId, kit, kitData))
                )))
            }
            displayNBT.put(ItemStack.LORE_KEY, nbtLore)
            itemStack.setSubNbt(ItemStack.DISPLAY_KEY, displayNBT)
        }

        itemStack.count = amount

        if (!player.addItem(itemStack)) {
            player.serverLevel().addFreshEntity(ItemEntity(player.serverLevel(), player.x, player.y, player.z, itemStack))
        }
    }
}
