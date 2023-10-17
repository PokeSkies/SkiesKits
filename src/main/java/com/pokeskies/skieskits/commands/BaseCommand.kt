package com.pokeskies.skieskits.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.commands.subcommands.ClaimCommand
import com.pokeskies.skieskits.commands.subcommands.DebugCommand
import com.pokeskies.skieskits.commands.subcommands.GiveCommand
import com.pokeskies.skieskits.commands.subcommands.ReloadCommand
import com.pokeskies.skieskits.config.ConfigManager
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class BaseCommand {
    private val aliases = listOf("skieskits", "kits", "kit")

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val rootCommands: List<LiteralCommandNode<ServerCommandSource>> = aliases.map {
            CommandManager.literal(it)
                .requires(Permissions.require("skieskits.command.base", 4))
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

        val subCommands: List<LiteralCommandNode<ServerCommandSource>> = listOf(
            ClaimCommand().build(),
            DebugCommand().build(),
            GiveCommand().build(),
            ReloadCommand().build(),
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }
}