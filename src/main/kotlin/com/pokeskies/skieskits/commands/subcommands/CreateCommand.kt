package com.pokeskies.skieskits.commands.subcommands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.item.KitItem
import com.pokeskies.skieskits.utils.SubCommand
import com.pokeskies.skieskits.utils.Utils
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import kotlin.jvm.optionals.getOrNull

class CreateCommand : SubCommand {
    override fun build(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("create")
            .then(Commands.argument("name", StringArgumentType.word())
                .requires(Permissions.require("skieskits.command.create", 2))
                .executes(Companion::create)
            )
            .build()
    }

    companion object {
        fun create(ctx: CommandContext<CommandSourceStack>): Int {
            val player = ctx.source.player
            if (player == null) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must be a player to use this command!"))
                return 1
            }

            val kitId = StringArgumentType.getString(ctx, "name")
            if (kitId.isNullOrEmpty()) {
                ctx.source.sendMessage(Utils.deserializeText("<red>You must provide a name for the kit!"))
                return 0
            }

            if (ConfigManager.KITS.containsKey(kitId)) {
                ctx.source.sendMessage(Utils.deserializeText("<red>There is already a kit with the name $kitId!"))
                return 0
            }

            val items = (0 until player.inventory.containerSize)
                .map { player.inventory.getItem(it) }
                .filter { !it.isEmpty }
                .map {
                    val dataResult = DataComponentPatch.CODEC.encodeStart(SkiesKits.INSTANCE.nbtOpts, it.componentsPatch)
                    KitItem(
                        item = BuiltInRegistries.ITEM.getKey(it.item).toString(),
                        amount = it.count,
                        components = if (dataResult.isSuccess) dataResult.result().getOrNull() as CompoundTag else null,
                    )
                }

            val kit = Kit(
                displayName = kitId,
                permission = "skieskits.kit.$kitId",
                items = items
            )

            if (!ConfigManager.saveFile("kits/$kitId.json", kit)) {
                ctx.source.sendMessage(Utils.deserializeText("<red>There was an error saving the kit file! Not sure what happened..."))
                return 0
            }

            ConfigManager.KITS[kitId] = kit

            ctx.source.sendMessage(Utils.deserializeText(
                "<green>Successfully created the kit $kitId!"
            ))

            return 1
        }
    }
}
