package com.pokeskies.skieskits.config.item

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.FlexibleListAdaptorFactory
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

open class GenericItem(
    val item: String = "minecraft:air",
    val amount: Int = 1,
    val name: String? = null,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val lore: List<String> = emptyList(),
    @SerializedName("components", alternate = ["nbt"])
    val components: CompoundTag? = null,
    @SerializedName("custom_model_data")
    val customModelData: Int? = null,
) {
    fun createItemStack(player: ServerPlayer, kitId: String?, kit: Kit?, kitData: KitData?): ItemStack? {
        val stack = getBaseItem(player) ?: return null

        if (components != null) {
            val decoded = DataComponentPatch.CODEC.decode(SkiesKits.INSTANCE.nbtOpts, components)
                .resultOrPartial { error ->
                    Utils.printError("Failed to decode item components for item '$item': $error | components=$components")
                }
            decoded.ifPresent { result ->
                stack.applyComponents(result.first)
            }
        }

        val dataComponents = DataComponentPatch.builder()

        if (customModelData != null) {
            dataComponents.set(
                DataComponents.CUSTOM_MODEL_DATA,
                CustomModelData(listOf(customModelData.toFloat()), emptyList(), emptyList(), emptyList())
            )
        }

        if (name != null)
            dataComponents.set(DataComponents.ITEM_NAME, Utils.deserializeNativeText(Utils.parsePlaceholders(player, name, null, null, null)))

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
                Component.empty().withStyle { style -> style.withItalic(false) }.append(Utils.deserializeNativeText(it)) as Component
            }.toList()))
        }

        stack.applyComponents(dataComponents.build())

        return stack
    }

    private fun getBaseItem(player: ServerPlayer): ItemStack? {
        if (item.isEmpty()) return null

        val parsedItem = Utils.parsePlaceholders(player, item)

        // Handles player head parsing
        if (parsedItem.startsWith("playerhead", true)) {
            val headStack = ItemStack(Items.PLAYER_HEAD, amount)

            var uuid: UUID? = null
            if (parsedItem.contains("-")) {
                val arg = parsedItem.replace("playerhead-", "")
                if (arg.isNotEmpty()) {
                    if (arg.contains("-")) {
                        // CASE: UUID format
                        try {
                            uuid = UUID.fromString(arg)
                        } catch (_: Exception) {}
                    } else if (arg.length <= 16) {
                        // CASE: Player name format
                        val targetPlayer = SkiesKits.INSTANCE.server.playerList?.getPlayerByName(arg)
                        if (targetPlayer != null) {
                            uuid = targetPlayer.uuid
                        }
                    } else {
                        // CASE: Game Profile format
                        val profile = GameProfile(UUID.randomUUID(), "skieskits_head")
                        profile.properties().put("textures", Property("textures", arg))
                        headStack.applyComponents(DataComponentPatch.builder()
                            .set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile))
                            .build())
                        return headStack
                    }
                }
            } else {
                // CASE: Only "playerhead" is provided, use the viewing player's UUID
                uuid = player.uuid
            }

            if (uuid != null) {
                headStack.applyComponents(DataComponentPatch.builder()
                    .set(DataComponents.PROFILE, ResolvableProfile.createUnresolved(uuid))
                    .build())
                return headStack
            }

            Utils.printError("Error while attempting to parse Player Head: $parsedItem")
            return headStack
        }

        val newItem = BuiltInRegistries.ITEM.getOptional(Identifier.parse(parsedItem))

        if (newItem.isEmpty()) {
            Utils.printError("Error while getting Item, defaulting to AIR: $parsedItem")
            return ItemStack(Items.AIR, amount)
        }

        return ItemStack(newItem.get(), amount)
    }

    override fun toString(): String {
        return "MenuItem(item='$item', amount=$amount, name=$name, lore=$lore, components=$components, customModelData=$customModelData)"
    }
}
