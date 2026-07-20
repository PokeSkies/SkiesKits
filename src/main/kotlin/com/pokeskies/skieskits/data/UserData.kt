package com.pokeskies.skieskits.data

import kotlin.jvm.Transient

class UserData(
    val kits: MutableMap<String, KitData> = mutableMapOf()
) {
    @Transient
    var isDirty: Boolean = false

    override fun toString(): String {
        return "UserData(kits=$kits)"
    }
}