package com.pokeskies.skieskits.config.item

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity

class KitItem(
    item: String = "minecraft:air",
    amount: Int = 1,
    name: String? = null,
    lore: List<String> = emptyList(),
    components: CompoundTag? = null,
    customModelData: Int? = null,
): GenericItem(item, amount, name, lore, components, customModelData) {
    fun giveItem(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData?) {
        val itemStack = createItemStack(player, kitId, kit, kitData) ?: return

        if (!player.addItem(itemStack)) {
            player.level().addFreshEntity(ItemEntity(player.level(), player.x, player.y, player.z, itemStack))
        }
    }
}
