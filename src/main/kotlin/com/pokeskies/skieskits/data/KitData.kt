package com.pokeskies.skieskits.data

class KitData(
    var uses: Int = 0,
    var lastUse: Long = 0
) {
    // Checks if the kit can be claimed based on the maximum uses allowed.
    // Returns true if the kit can be used, false otherwise.
    fun canClaimUses(maxUses: Int): Boolean {
        if (maxUses <= 0) return true // If maxUses is 0 or negative, it means unlimited uses.
        return uses < maxUses
    }

    // Checks if the kit can be claimed based on the cooldown time.
    // Returns true if the kit can be used, false otherwise.
    fun canClaimCooldown(cooldown: Long): Boolean {
        return !(cooldown > 0 && System.currentTimeMillis() < (lastUse + (cooldown * 1000)))
    }

    // Gets the remaining cooldown time in seconds before the kit can be used again.
    fun getTimeRemaining(cooldown: Long): Long {
        return if (cooldown > 0) ((lastUse + (cooldown * 1000)) - System.currentTimeMillis()) / 1000 else 0
    }

    override fun toString(): String {
        return "KitData(uses=$uses, lastUse=$lastUse)"
    }
}