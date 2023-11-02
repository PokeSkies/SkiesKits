package com.pokeskies.skieskits.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.utils.SubCommand
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class ResetUsageCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("resetusage")
            .then(CommandManager.argument("player", EntityArgumentType.players())
                .then(CommandManager.argument("kit", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.resetusage", 4))
                    .suggests { _, builder ->
                        CommandSource.suggestMatching(ConfigManager.KITS.keys.stream(), builder)
                    }
                    .executes(Companion::resetSpecific)
                )
                .requires(Permissions.require("skieskits.command.resetusage", 4))
                .executes(Companion::resetAll)
            )
            .build()
    }

    companion object {
        fun resetSpecific(ctx: CommandContext<ServerCommandSource>): Int {
            val players = EntityArgumentType.getPlayers(ctx, "player")
            if (players.isNullOrEmpty()) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must provide a target player!"))
                return 1
            }

            val kitId = StringArgumentType.getString(ctx, "kit")

            val kit = ConfigManager.KITS[kitId]
            if (kit == null) {
                ctx.source.sendMessage(Utils.deserializeText(
                    SkiesKits.INSTANCE.configManager.config.messages.kitNotFound.replace("%kit_name%", kitId)
                ))
                return 1
            }

            for (player in players) {
                val userdata = SkiesKits.INSTANCE.storage.getUser(player.uuid)
                val kitData = userdata.kits[kitId]
                if (kitData != null) {
                    kitData.uses = 0
                    userdata.kits[kitId] = kitData
                    SkiesKits.INSTANCE.storage.saveUser(player.uuid, userdata)
                }
            }

            ctx.source.sendMessage(Utils.deserializeText(
                "<green>Reset uses for the kit <b>$kitId</b> for <b>${players.size}</b> player(s)!"
            ))

            return 1
        }

        fun resetAll(ctx: CommandContext<ServerCommandSource>): Int {
            val players = EntityArgumentType.getPlayers(ctx, "player")
            if (players.isNullOrEmpty()) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must provide a target player!"))
                return 1
            }

            for (player in players) {
                val userdata = SkiesKits.INSTANCE.storage.getUser(player.uuid)
                for ((kitId, kit) in ConfigManager.KITS) {
                    if (userdata.kits.containsKey(kitId)) {
                        val kitData = userdata.kits[kitId]!!
                        kitData.uses = 0
                        userdata.kits[kitId] = kitData
                    }
                }
                SkiesKits.INSTANCE.storage.saveUser(player.uuid, userdata)
            }

            ctx.source.sendMessage(Utils.deserializeText(
                "<green>Reset uses for all kits for <b>${players.size}</b> player(s)!"
            ))

            return 1
        }
    }
}