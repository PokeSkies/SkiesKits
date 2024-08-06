package com.pokeskies.skieskits.commands.subcommands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.utils.SubCommand
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.commands.Commands
import net.minecraft.commands.CommandSourceStack

class DebugCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("debug")
            .requires(Permissions.require("skieskits.command.debug", 4))
            .executes(Companion::debug)
            .build()
    }

    companion object {
        fun debug(ctx: CommandContext<CommandSourceStack>): Int {
            val newMode = !SkiesKits.INSTANCE.configManager.config.debug
            SkiesKits.INSTANCE.configManager.config.debug = newMode
            SkiesKits.INSTANCE.saveFile("config.json", SkiesKits.INSTANCE.configManager.config)

            if (newMode) {
                ctx.source.sendMessage(Component.text("Debug mode has been enabled!").color(NamedTextColor.GREEN))
            } else {
                ctx.source.sendMessage(Component.text("Debug mode has been disabled!").color(NamedTextColor.RED))
            }
            return 1
        }
    }
}
