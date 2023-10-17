package com.pokeskies.skieskits.config

import com.pokeskies.skieskits.utils.Utils
import net.minecraft.entity.ItemEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class KitItem(
    val item: Item = Items.AIR,
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: NbtCompound? = null
) {
    fun giveItem(player: ServerPlayerEntity) {
        val itemStack = ItemStack(item)

        if (nbt != null) {
            itemStack.nbt = nbt
        }

        if (name != null) {
            itemStack.setCustomName(Utils.deseralizeText(name))
        }

        if (lore.isNotEmpty()) {
            val displayNBT = itemStack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY)
            val nbtLore = NbtList()
            for (line in lore) {
                nbtLore.add(NbtString.of(Text.Serializer.toJson(Utils.deseralizeText(line))))
            }
            displayNBT.put(ItemStack.LORE_KEY, nbtLore)
            itemStack.setSubNbt(ItemStack.DISPLAY_KEY, displayNBT)
        }

        itemStack.count = amount

        if (!player.giveItemStack(itemStack)) {
            player.world.spawnEntity(ItemEntity(player.world, player.x, player.y, player.z, itemStack))
        }
    }
}