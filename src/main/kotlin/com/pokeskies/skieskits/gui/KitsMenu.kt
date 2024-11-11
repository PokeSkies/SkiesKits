package com.pokeskies.skieskits.gui

import ca.landonjw.gooeylibs2.api.UIManager
import ca.landonjw.gooeylibs2.api.data.UpdateEmitter
import ca.landonjw.gooeylibs2.api.page.Page
import ca.landonjw.gooeylibs2.api.template.Template
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class KitsMenu(
    private val player: ServerPlayerEntity
) : UpdateEmitter<Page?>(), Page {
    private val config = SkiesKits.INSTANCE.configManager.menuConfig

    private val template: ChestTemplate =
        ChestTemplate.Builder(config.size)
            .build()

    init {
        refresh()
    }

    private fun refresh() {
        for ((id, item) in config.items) {
            val button = item.createButton(player, null, null, null)
            for (slot in item.slots) {
                template.set(slot, button.build())
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
                    template.set(slot, options.noPermission.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            if (!kitData.checkUsage(kit.maxUses)) {
                for (slot in options.slots) {
                    template.set(slot, options.maxUses.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            if (!kitData.checkCooldown(kit.cooldown)) {
                for (slot in options.slots) {
                    template.set(slot, options.onCooldown.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            var passed = true
            for ((id, requirement) in kit.requirements.requirements) {
                if (!requirement.passesRequirements(player, kitId, kit, kitData)) {
                    passed = false
                }
            }

            if (!passed) {
                for (slot in options.slots) {
                    template.set(slot, options.failedRequirements.createButton(player, kitId, kit, kitData).build())
                }
                continue
            }

            for (slot in options.slots) {
                template.set(slot, options.available.createButton(player, kitId, kit, kitData)
                    .onClick { ctx ->
                        if (!kit.hasPermission(player)) {
                            player.sendMessage(Utils.deserializeText(
                                SkiesKits.INSTANCE.configManager.config.messages.kitNoPermission.replace("%kit_name%", kitId)
                            ))
                            return@onClick
                        }

                        kit.claim(kitId, player)
                        UIManager.closeUI(player)
                    }
                    .build())
            }
            continue
        }
    }

    override fun getTemplate(): Template {
        return template
    }

    override fun getTitle(): Text {
        return Utils.deserializeText(Utils.parsePlaceholders(player, config.title, null, null, null))
    }
}
