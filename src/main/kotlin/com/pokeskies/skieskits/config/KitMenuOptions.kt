package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName

class KitMenuOptions(
    val slots: List<Int> = emptyList(),
    val available: MenuItem = MenuItem(),
    @SerializedName("on_cooldown")
    val onCooldown: MenuItem = MenuItem(),
    @SerializedName("no_permission")
    val noPermission: MenuItem = MenuItem(),
    @SerializedName("max_uses")
    val maxUses: MenuItem = MenuItem(),
    @SerializedName("failed_requirements")
    val failedRequirements: MenuItem = MenuItem()
) {
    override fun toString(): String {
        return "KitMenuOptions(slots=$slots, available=$available, onCooldown=$onCooldown, noPermission=$noPermission, maxUses=$maxUses, failedRequirements=$failedRequirements)"
    }
}