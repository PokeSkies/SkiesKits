package com.pokeskies.skieskits.config

class KitMenuConfig(
    val title: String = "Kits",
    val size: Int = 6,
    val kits: Map<String, KitMenuOptions> = emptyMap(),
    val items: Map<String, MenuItem> = emptyMap()
) {
    override fun toString(): String {
        return "KitMenuConfig(title='$title', size=$size, kits=$kits, items=$items)"
    }
}