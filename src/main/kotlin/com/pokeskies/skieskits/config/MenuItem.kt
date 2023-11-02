package com.pokeskies.skieskits.config

import ca.landonjw.gooeylibs2.api.button.GooeyButton
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class MenuItem(
    val slots: List<Int> = emptyList(),
    val item: Item = Items.AIR,
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: NbtCompound? = null
) {
    fun createButton(player: ServerPlayerEntity, kitId: String?, kit: Kit?, kitData: KitData?): GooeyButton.Builder {
        val stack = ItemStack(item, amount)

        if (nbt != null) {
            stack.nbt = nbt
        }

        val builder = GooeyButton.builder().display(stack)

        if (name != null)
            builder.title(Utils.deserializeText(Utils.parsePlaceholders(player, name, null, null, null)))

        if (lore.isNotEmpty()) {
            builder.lore(Text::class.java, lore.stream().map { Utils.deserializeText(Utils.parsePlaceholders(player, it, kitId, kit, kitData)) }.toList())
        }

        return builder
    }
}