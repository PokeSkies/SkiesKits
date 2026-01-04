package com.pokeskies.skieskits.gui

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.PreviewConfig
import com.pokeskies.skieskits.utils.Utils
import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

class PreviewGui(
    player: ServerPlayer,
    val preview: PreviewConfig,
    val kit: Kit
) : SimpleGui(preview.type.type, player, false) {
    private var page = 0
    private var pages = 1

    private var items = mutableListOf<GuiElementBuilder>()

    init {
        kit.items.forEach { item ->
            item.createItemStack(player, kit.id, kit, null).let {
                items.add(GuiElementBuilder(it))
            }
        }

        kit.preview.extras.forEach { item ->
            items.add(item.createButton(player, kit.id, kit, null))
        }

        if (!kit.items.isEmpty()) {
            pages = (items.size - 1) / preview.slots.size + 1
        }

        refresh()
    }

    fun refresh() {
        for ((_, item) in preview.items) {
            val button = item.createButton(player, null, null, null)
            for (slot in item.slots) {
                setSlot(slot, button
                    .setCallback { clickType ->
                        item.actions.forEach { (_, action) ->
                            action.executeAction(player, kitId = kit.id, kit = kit, kitData = null, gui = this)
                        }
                    }.build())
            }
        }

        renderPage()
    }

    private fun renderPage() {
        preview.slots.forEach { slot ->
            this.clearSlot(slot)
        }

        var index = 0
        for (item in items.toList().subList(preview.slots.size * page, minOf(preview.slots.size * (page + 1), items.size))) {
            if (index < preview.slots.size) {
                this.setSlot(preview.slots[index++], item)
            }
        }
    }

    fun nextPage() {
        if (page < pages - 1) {
            page++
            refresh()
        }
    }

    fun previousPage() {
        if (page > 0) {
            page--
            refresh()
        }
    }

    fun lastPage() {
        page = pages - 1
        refresh()
    }

    fun firstPage() {
        page = 0
        refresh()
    }

    override fun getTitle(): Component {
        return Utils.deserializeText(Utils.parsePlaceholders(player, preview.title, kit.id, kit, null))
    }
}
