package com.pokeskies.skieskits.utils

import com.mojang.brigadier.tree.LiteralCommandNode
import net.minecraft.server.command.ServerCommandSource

interface SubCommand {
    fun build(): LiteralCommandNode<ServerCommandSource>
}