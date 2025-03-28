package com.pokeskies.skieskits.commands

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.commands.subcommands.*
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.gui.KitsMenu
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.command.CommandSource
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class BaseCommand {
    private val aliases = listOf("skieskits", "kits", "kit")

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val rootCommands: List<LiteralCommandNode<ServerCommandSource>> = aliases.map {
            CommandManager.literal(it)
                .requires(Permissions.require("skieskits.command.base", 2))
                .then(CommandManager.argument("kit", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.claim", 2))
                    .suggests { ctx, builder ->
                        CommandSource.suggestMatching(ConfigManager.KITS.filter { entry ->
                            return@filter !(ctx.source.isExecutedByPlayer && !entry.value.hasPermission(ctx.source.player!!))
                        }.keys, builder)
                    }
                    .requires { obj: ServerCommandSource -> obj.isExecutedByPlayer }
                    .executes(ClaimCommand::claim)
                )
                .executes(Companion::openMenu)
                .build()
        }

        val subCommands: List<LiteralCommandNode<ServerCommandSource>> = listOf(
            ReloadCommand().build(),
            DebugCommand().build(),
            ClaimCommand().build(),
            GiveCommand().build(),
            ResetUsageCommand().build(),
            ResetCooldownCommand().build(),
            CreateCommand().build(),
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }

    companion object {
        fun openMenu(ctx: CommandContext<ServerCommandSource>): Int {
            val player = ctx.source.player
            if (player == null) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must be a player to run this command!"))
                return 1
            }

            UIManager.openUIForcefully(player, KitsMenu(player))
            return 1
        }
    }
}
