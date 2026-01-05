package com.pokeskies.skieskits.config.item

import com.pokeskies.skieskits.config.actions.Action
import net.minecraft.nbt.CompoundTag

class ActionMenuItem(
    item: String = "minecraft:air",
    slots: List<Int> = emptyList(),
    amount: Int = 1,
    name: String? = null,
    lore: List<String> = emptyList(),
    components: CompoundTag? = null,
    customModelData: Int? = null,
    val actions: Map<String, Action> = emptyMap(),
): MenuItem(item, slots, amount, name, lore, components, customModelData) {
    override fun toString(): String {
        return "MenuItem(item='$item', slots=$slots, amount=$amount, name=$name, lore=$lore, " +
                "components=$components, customModelData=$customModelData, actions=$actions)"
    }
}