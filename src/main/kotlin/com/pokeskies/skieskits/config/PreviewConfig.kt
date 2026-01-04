package com.pokeskies.skieskits.config

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.gui.InventoryType
import com.pokeskies.skieskits.utils.FlexibleListAdaptorFactory

class PreviewConfig(
    val title: String = "Kit Preview - %kit_name%",
    @SerializedName("type", alternate = ["size"])
    val type: InventoryType = InventoryType.GENERIC_9x6,
    @JsonAdapter(FlexibleListAdaptorFactory::class)
    val slots: List<Int> = emptyList(),
    val items: Map<String, ActionMenuItem> = emptyMap()
) {
    override fun toString(): String {
        return "PreviewConfig(title='$title', type=$type, slots=$slots, items=$items)"
    }
}