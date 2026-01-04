package com.pokeskies.skieskits.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.utils.SubCommand
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer

class PreviewCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("preview")
                .then(Commands.argument("kit", StringArgumentType.word())
                    .requires(Permissions.require("skieskits.command.preview", 2))
                    .suggests { _, builder ->
                        SharedSuggestionProvider.suggest(ConfigManager.KITS.keys.stream(), builder)
                    }
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes { ctx ->
                            preview(ctx, EntityArgument.getPlayer(ctx, "player"))
                        }
                    )
                    .executes { ctx ->
                        preview(ctx, ctx.source.playerOrException)
                    }
                )
                .build()
    }

    companion object {
        fun preview(ctx: CommandContext<CommandSourceStack>, player: ServerPlayer): Int {
            val kitId = StringArgumentType.getString(ctx, "kit")

            val kit = ConfigManager.KITS[kitId]
            if (kit == null) {
                ctx.source.sendMessage(Utils.deserializeText(
                    Utils.parsePlaceholders(
                        player,
                        ConfigManager.CONFIG.messages.kitNotFound,
                        kitId,
                        null,
                        null
                    )
                ))
                return 1
            }

            val preview = kit.createPreview(player) ?: run {
                ctx.source.sendMessage(Utils.deserializeText(
                    Utils.parsePlaceholders(
                        player,
                        ConfigManager.CONFIG.messages.kitNoPreview,
                        kitId,
                        kit,
                        null
                    )
                ))
                return 1
            }

            preview.open()

            return 1
        }
    }
}
