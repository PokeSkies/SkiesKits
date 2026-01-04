package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.component.ItemLore

class KitItem(
    val item: String = "minecraft:air",
    val amount: Int = 1,
    val name: String? = null,
    val lore: List<String> = emptyList(),
    @SerializedName("components", alternate = ["nbt"])
    val components: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
) {
    fun giveItem(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData?) {
        val itemStack = createItemStack(player, kitId, kit, kitData) ?: return

        if (!player.addItem(itemStack)) {
            player.serverLevel().addFreshEntity(ItemEntity(player.serverLevel(), player.x, player.y, player.z, itemStack))
        }
    }

    fun createItemStack(player: ServerPlayer, kitId: String, kit: Kit, kitData: KitData?): ItemStack? {
        if (item.isEmpty()) {
            Utils.printError("Item for kit $kitId is empty, cannot give item to player ${player.name.string}.")
            return null
        }

        val itemStack = ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(item)))

        if (components != null) {
            DataComponentPatch.CODEC.decode(SkiesKits.INSTANCE.nbtOpts, components).result().ifPresent { result ->
                itemStack.applyComponents(result.first)
            }
        }

        val dataComponents = DataComponentPatch.builder()

        if (customModelData != null) {
            dataComponents.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(customModelData))
        }

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

        return itemStack
    }
}
