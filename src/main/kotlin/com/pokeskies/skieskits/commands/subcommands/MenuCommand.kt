package com.pokeskies.skieskits.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.gui.KitsMenuGui
import com.pokeskies.skieskits.utils.SubCommand
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

class MenuCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("menu")
                .then(Commands.argument("menu", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.menu", 2))
                    .suggests { _, builder ->
                        SharedSuggestionProvider.suggest(ConfigManager.MENUS.keys.stream(), builder)
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { ctx ->
                            execute(ctx, EntityArgument.getPlayer(ctx, "player"))
                        }
                    )
                    .executes { ctx ->
                        execute(ctx, ctx.source.playerOrException)
                    }
                )
                .build()
    }

    companion object {
        fun execute(ctx: CommandContext<CommandSourceStack>, player: ServerPlayer): Int {
            val menuId = StringArgumentType.getString(ctx, "menu")

            val menuConfig = ConfigManager.MENUS[menuId]
            if (menuConfig == null) {
                ctx.source.sendMessage(Utils.deserializeText(
                    Utils.parsePlaceholders(
                        player,
                        ConfigManager.CONFIG.messages.kitMenuError
                    )
                ))
                return 1
            }

            KitsMenuGui(player, menuConfig).open()

            return 1
        }
    }
}
