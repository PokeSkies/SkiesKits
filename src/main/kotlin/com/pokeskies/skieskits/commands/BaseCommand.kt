package com.pokeskies.skieskits.commands

import ca.landonjw.gooeylibs2.api.UIManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.commands.subcommands.*
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.gui.KitsMenu
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider

class BaseCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val rootCommands: List<LiteralCommandNode<CommandSourceStack>> = SkiesKits.INSTANCE.configManager.config.commands.map {
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
                .executes(Companion::openMenu)
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
        )

        rootCommands.forEach { root ->
            subCommands.forEach { sub -> root.addChild(sub) }
            dispatcher.root.addChild(root)
        }
    }

    companion object {
        fun openMenu(ctx: CommandContext<CommandSourceStack>): Int {
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
