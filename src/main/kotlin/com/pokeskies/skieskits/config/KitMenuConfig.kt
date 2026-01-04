package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.gui.GenericClickType
import com.pokeskies.skieskits.gui.InventoryType

class KitMenuConfig(
    val title: String = "Kits",
    @SerializedName("type", alternate = ["size"])
    val type: InventoryType = InventoryType.GENERIC_9x6,
    val click: ClickOptions = ClickOptions(),
    val kits: Map<String, KitMenuOptions> = emptyMap(),
    val items: Map<String, MenuItem> = emptyMap()
) {
    class ClickOptions(
        val claim: List<GenericClickType> = listOf(GenericClickType.ANY_LEFT_CLICK),
        val preview: List<GenericClickType> = listOf(GenericClickType.ANY_RIGHT_CLICK)
    ) {
        override fun toString(): String {
            return "ClickOptions(claim=$claim, preview=$preview)"
        }
    }

    override fun toString(): String {
        return "KitMenuConfig(title='$title', type=$type, click=$click, kits=$kits, items=$items)"
    }
}