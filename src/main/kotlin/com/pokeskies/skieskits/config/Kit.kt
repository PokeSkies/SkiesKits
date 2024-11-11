package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.actions.ActionOptions
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.network.ServerPlayerEntity

class Kit(
    @SerializedName("display_name")
    val displayName: String? = null,
    val permission: String? = null,
    val cooldown: Long = -1,
    @SerializedName("max_uses")
    val maxUses: Int = -1,
    @SerializedName("on_join")
    val onJoin: Boolean = false,
    val notifications: Boolean = true,
    val items: List<KitItem> = emptyList(),
    val requirements: RequirementOptions = RequirementOptions(),
    val actions: ActionOptions = ActionOptions(),
) {
    fun claim(kitId: String, player: ServerPlayerEntity, bypassChecks: Boolean = false, bypassRequirements: Boolean = false) {
        Utils.printDebug("Attempting to claim kit $kitId for player ${player.name.string}! BypassChecks=$bypassChecks, BypassRequirements=$bypassRequirements")
        if (SkiesKits.INSTANCE.storage == null) {
            player.sendMessage(Utils.deserializeText("<red>There was an error with the storage system! Please check the console..."))
            Utils.printError("Storage system is null! Cannot claim kit $kitId for player ${player.name.string}!")
            return
        }

        val userdata = SkiesKits.INSTANCE.storage?.getUser(player.uuid)

        if (userdata == null) {
            player.sendMessage(Utils.deserializeText("<red>There was an error with the storage system! Please check the console..."))
            Utils.printError("Returned player data is null! Cannot claim kit $kitId for player ${player.name.string}!")
            return
        }

        val kitData = if (userdata.kits.containsKey(kitId)) userdata.kits[kitId]!! else KitData()

        if (!bypassChecks) {
            if (!kitData.checkUsage(maxUses)) {
                Utils.printDebug("Player ${player.name.string} has reached the maximum uses for kit $kitId! Kit uses=${kitData.uses} and maxUses=$maxUses")
                actions.executeUsesActions(player, kitId, this, kitData)
                if (notifications) {
                    player.sendMessage(Utils.deserializeText(
                        SkiesKits.INSTANCE.configManager.config.messages.kitFailedUses
                            .replace("%kit_name%", getDisplayName(kitId))
                            .replace("%kit_uses%", kitData.uses.toString())
                            .replace("%kit_max_uses%", maxUses.toString())
                    ))
                }
                return
            }

            if (!kitData.checkCooldown(cooldown)) {
                Utils.printDebug("Player ${player.name.string} is still on cooldown for kit $kitId! Kit cooldown=${kitData.getTimeRemaining(cooldown)}")
                actions.executeCooldownActions(player, kitId, this, kitData)
                if (notifications) {
                    player.sendMessage(
                        Utils.deserializeText(
                            SkiesKits.INSTANCE.configManager.config.messages.kitFailedCooldown
                                .replace("%kit_name%", getDisplayName(kitId))
                                .replace("%kit_cooldown%", Utils.getFormattedTime(kitData.getTimeRemaining(cooldown)))
                        )
                    )
                }
                return
            }
        }

        if (!bypassRequirements) {
            var success = true
            for ((id, requirement) in requirements.requirements) {
                if (requirement.passesRequirements(player, kitId, this, kitData)) {
                    requirement.executeSuccessActions(player, kitId, this, kitData)
                } else {
                    success = false
                    requirement.executeDenyActions(player, kitId, this, kitData)
                }
            }

            if (!success) {
                Utils.printDebug("Player ${player.name.string} failed the requirements for kit $kitId!")
                requirements.executeDenyActions(player, kitId, this, kitData)
                actions.executeRequirementsActions(player, kitId, this, kitData)
                if (notifications) {
                    player.sendMessage(
                        Utils.deserializeText(
                            SkiesKits.INSTANCE.configManager.config.messages.kitFailedRequirements
                                .replace("%kit_name%", getDisplayName(kitId))
                        )
                    )
                }
                return
            }

            requirements.executeSuccessActions(player, kitId, this, kitData)
        }

        kitData.uses += 1
        kitData.lastUse = System.currentTimeMillis()
        userdata.kits[kitId] = kitData

        if (!SkiesKits.INSTANCE.storage!!.saveUser(player.uuid, userdata)) {
            player.sendMessage(Utils.deserializeText("<red>There was an error with the storage system! Please check the console..."))
            Utils.printError("Failed to save player data for ${player.name.string} while claiming kit $kitId!")
            return
        }

        for (item in items) {
            item.giveItem(player, kitId, this, kitData)
        }

        Utils.printDebug("Player ${player.name.string} successfully claimed kit $kitId!")

        actions.executeClaimedActions(player, kitId, this, kitData)

        if (notifications) {
            player.sendMessage(
                Utils.deserializeText(
                    SkiesKits.INSTANCE.configManager.config.messages.kitReceived
                        .replace("%kit_name%", getDisplayName(kitId))
                )
            )
        }
    }

    fun getDisplayName(id: String): String {
        return if (displayName.isNullOrBlank()) id else displayName
    }

    fun hasPermission(player: ServerPlayerEntity): Boolean {
        return if (!permission.isNullOrEmpty()) Permissions.check(player, permission) else true
    }

    override fun toString(): String {
        return "Kit(cooldown=$cooldown, max_uses=$maxUses, on_join=$onJoin, display_name=$displayName, permission=$permission, items=$items, requirements=$requirements, actions=$actions)"
    }
}
