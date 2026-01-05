package com.pokeskies.skieskits.config.item

import com.google.gson.annotations.JsonAdapter
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.FlexibleListAdaptorFactory
import eu.pb4.sgui.api.elements.GuiElementBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer

open class MenuItem(
    item: String = "minecraft:air",
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val slots: List<Int> = emptyList(),
    amount: Int = 1,
    name: String? = null,
    lore: List<String> = emptyList(),
    components: CompoundTag? = null,
    customModelData: Int? = null,
): GenericItem(item, amount, name, lore, components, customModelData) {
    fun createButton(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?): GuiElementBuilder {
        return GuiElementBuilder.from(createItemStack(player, kitId, kit, kitData))
    }

    override fun toString(): String {
        return "MenuItem(item='$item', slots=$slots, amount=$amount, name=$name, lore=$lore, components=$components, customModelData=$customModelData)"
    }
}
