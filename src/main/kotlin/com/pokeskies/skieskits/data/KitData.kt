package com.pokeskies.skieskits.data

class KitData(
    var uses: Int = 0,
    var lastUse: Long = 0
) {
    fun checkUsage(maxUses: Int): Boolean {
        return maxUses !in 1..uses
    }

    fun checkCooldown(cooldown: Long): Boolean {
        return !(cooldown > 0 && System.currentTimeMillis() < (lastUse + (cooldown * 1000)))
    }

    fun getTimeRemaining(cooldown: Long): Long {
        return if (cooldown > 0) ((lastUse + (cooldown * 1000)) - System.currentTimeMillis()) / 1000 else 0
    }

    override fun toString(): String {
        return "KitData(uses=$uses, lastUse=$lastUse)"
    }
}