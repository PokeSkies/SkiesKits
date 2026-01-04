package com.pokeskies.skieskits.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.commands.subcommands.*
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.gui.KitsMenuGui
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider

class BaseCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val rootCommands: List<LiteralCommandNode<CommandSourceStack>> = ConfigManager.CONFIG.commands.map {
            Commands.literal(it)
                .requires(Permissions.require("skieskits.command.base", 2))
                .then(Commands.argument("kit", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.claim", 2))
                    .suggests { ctx, builder ->
                        SharedSuggestionProvider.suggest(ConfigManager.KITS.filter { entry ->
                            return@filter !(ctx.source.isPlayer && !entry.value.hasPermission(ctx.source.player!!))
                        }.keys, builder)
                    }
                    .requires { obj: CommandSourceStack -> obj.isPlayer }
                    .executes(ClaimCommand::claim)
                )
                .executes(Companion::execute)
                .build()
        }

        val subCommands: List<LiteralCommandNode<CommandSourceStack>> = listOf(
            ReloadCommand().build(),
            DebugCommand().build(),
            ClaimCommand().build(),
            GiveCommand().build(),
            ResetUsageCommand().build(),
            ResetCooldownCommand().build(),
            CreateCommand().build(),
            PreviewCommand().build(),
            MenuCommand().build(),
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }

    companion object {
        fun execute(ctx: CommandContext<CommandSourceStack>): Int {
            val player = ctx.source.player
            if (player == null) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must be a player to run this command!"))
                return 0
            }

            val menuConfig = ConfigManager.MENUS[ConfigManager.CONFIG.defaultMenu] ?: run {
                ctx.source.sendMessage(Utils.deserializeText(
                    Utils.parsePlaceholders(
                        player,
                        ConfigManager.CONFIG.messages.kitMenuError
                    )
                ))
                Utils.printError("Default menu '${ConfigManager.CONFIG.defaultMenu}' not found as a valid configuration menu.")
                return 0
            }

            try {
                KitsMenuGui(player, menuConfig).open()
            } catch (e: Exception) {
                Utils.printError("An error occurred while opening the Kits menu for player ${player.name.string}: ${e.message}")
                ctx.source.sendMessage(Utils.deserializeText(
                    Utils.parsePlaceholders(
                        player,
                        ConfigManager.CONFIG.messages.kitMenuError
                    )
                ))
                return 0
            }
            return 1
        }
    }
}
