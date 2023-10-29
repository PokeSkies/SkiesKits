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

class GiveCommand : SubCommand {
    override fun build(): LiteralCommandNode<ServerCommandSource> {
        return CommandManager.literal("give")
                .then(CommandManager.argument("kit", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.give", 4))
                    .suggests { _, builder ->
                        CommandSource.suggestMatching(ConfigManager.KITS.keys.stream(), builder)
                    }
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(GiveCommand::give)
                    )
                )
                .build()
    }

    companion object {
        fun give(ctx: CommandContext<ServerCommandSource>): Int {
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
                kit.claim(kitId, player)
            }

            return 1
        }
    }
}