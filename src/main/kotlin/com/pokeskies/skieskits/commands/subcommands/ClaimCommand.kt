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
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class ClaimCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("claim")
            .then(CommandManager.argument("kit", StringArgumentType.word())
                .requires(Permissions.require("skieskits.command.claim", 4))
                .suggests { ctx, builder ->
                    CommandSource.suggestMatching(ConfigManager.KITS.filter { entry ->
                        return@filter !(ctx.source.isExecutedByPlayer && !entry.value.hasPermission(ctx.source.player!!))
                    }.keys, builder)
                }
                .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                .executes(ClaimCommand::claim)
            )
            .build()
    }

    companion object {
        fun claim(ctx: CommandContext<ServerCommandSource>): Int {
            val player = ctx.source.player
            if (player == null) {
                ctx.source.sendMessage(Utils.deseralizeText("<red>You must be a player to use this command!"))
                return 1
            }

            val kitId = StringArgumentType.getString(ctx, "kit")

            val kit = ConfigManager.KITS[kitId]
            if (kit == null) {
                player.sendMessage(Utils.deseralizeText(
                    SkiesKits.INSTANCE.configManager.config.messages.kitNotFound.replace("%kit_name%", kitId)
                ))
                return 1
            }

            if (!kit.hasPermission(player)) {
                player.sendMessage(Utils.deseralizeText(
                    SkiesKits.INSTANCE.configManager.config.messages.kitNoPermission.replace("%kit_name%", kitId)
                ))
                return 1
            }

            kit.claim(kitId, player)

            return 1
        }
    }
}