package com.pokeskies.skieskits.gui

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.MenuType

class KitsMenu(
    player: ServerPlayer
) : SimpleGui(getTypeFromSize(SkiesKits.INSTANCE.configManager.menuConfig.size), player, false) {
    private val config = SkiesKits.INSTANCE.configManager.menuConfig

    init {
        refresh()
    }

    private fun refresh() {
        for ((_, item) in config.items) {
            val button = item.createButton(player, null, null, null)
            for (slot in item.slots) {
                setSlot(slot, button.build())
            }
        }

        for ((kitId, options) in config.kits) {
            val kit = ConfigManager.KITS[kitId]
            if (kit == null) {
                Utils.printError("Menu references a kit named $kitId but the kit was not found!")
                continue
            }

            val userdata = SkiesKits.INSTANCE.storage?.getUser(player.uuid) ?: return
            val kitData = if (userdata.kits.containsKey(kitId)) userdata.kits[kitId]!! else KitData()

            if (!kit.hasPermission(player)) {
                for (slot in options.slots) {
                    setSlot(slot, options.noPermission.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            if (!kitData.checkUsage(kit.maxUses)) {
                for (slot in options.slots) {
                    setSlot(slot, options.maxUses.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            if (!kitData.checkCooldown(kit.cooldown)) {
                for (slot in options.slots) {
                    setSlot(slot, options.onCooldown.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            var passed = true
            for ((_, requirement) in kit.requirements.requirements) {
                if (!requirement.passesRequirements(player, kitId, kit, kitData)) {
                    passed = false
                }
            }

            if (!passed) {
                for (slot in options.slots) {
                    setSlot(slot, options.failedRequirements.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            for (slot in options.slots) {
                setSlot(slot, options.available.createButton(player, kitId, kit, kitData)
                    .setCallback { ctx ->
                        if (!kit.hasPermission(player)) {
                            player.sendMessage(Utils.deserializeText(
                                SkiesKits.INSTANCE.configManager.config.messages.kitNoPermission.replace("%kit_name%", kitId)
                            ))
                            return@setCallback
                        }

                        kit.claim(kitId, player)
                        close()
                    }
                    .build())
            }
        }
    }

    override fun getTitle(): Component {
        return Utils.deserializeText(Utils.parsePlaceholders(player, config.title, null, null, null))
    }

    companion object {
        private fun getTypeFromSize(size: Int): MenuType<*> {
            return when (size) {
                1 -> MenuType.GENERIC_9x1
                2 -> MenuType.GENERIC_9x2
                3 -> MenuType.GENERIC_9x3
                4 -> MenuType.GENERIC_9x4
                5 -> MenuType.GENERIC_9x5
                6 -> MenuType.GENERIC_9x6
                else -> MenuType.GENERIC_9x6
            }
        }
    }
}
