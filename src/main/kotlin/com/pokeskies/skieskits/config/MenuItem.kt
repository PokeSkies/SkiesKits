package com.pokeskies.skieskits.config

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.elements.GuiElementBuilder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore

class MenuItem(
    val slots: List<Int> = emptyList(),
    val item: String = "minecraft:air",
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    val nbt: CompoundTag? = null
) {
    fun createButton(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?): GuiElementBuilder {
        if (item.isEmpty()) {
            Utils.printError("Menu item $item is empty, cannot add to menu.")
            return GuiElementBuilder.from(ItemStack.EMPTY)
        }

        val stack = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(item)), amount)

        if (nbt != null) {
            DataComponentPatch.CODEC.decode(SkiesKits.INSTANCE.nbtOpts, nbt).result().ifPresent { result ->
                stack.applyComponents(result.first)
            }
        }

        val dataComponents = DataComponentPatch.builder()

        if (name != null)
            dataComponents.set(DataComponents.ITEM_NAME, Utils.deserializeText(Utils.parsePlaceholders(player, name, null, null, null)))

        if (lore.isNotEmpty()) {
            val parsedLore: MutableList<String> = mutableListOf()
            for (line in lore.stream().map { Utils.parsePlaceholders(player, it, kitId, kit, kitData) }.toList()) {
                if (line.contains("\n")) {
                    line.split("\n").forEach { parsedLore.add(it) }
                } else {
                    parsedLore.add(line)
                }
            }
            dataComponents.set(DataComponents.LORE, ItemLore(parsedLore.stream().map {
                Component.empty().withStyle { it.withItalic(false) }.append(Utils.deserializeText(it)) as Component
            }.toList()))
        }

        stack.applyComponents(dataComponents.build())

        return GuiElementBuilder.from(stack)
    }
}
