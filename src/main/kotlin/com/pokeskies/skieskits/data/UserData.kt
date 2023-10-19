package com.pokeskies.skieskits.data

class UserData(
    val kits: MutableMap<String, KitData> = mutableMapOf()
) {
    override fun toString(): String {
        return "UserData(kits=$kits)"
    }
}