package com.pokeskies.skieskits

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pokeskies.skieskits.commands.BaseCommand
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.economy.EconomyType
import com.pokeskies.skieskits.economy.IEconomyService
import com.pokeskies.skieskits.gui.GenericClickType
import com.pokeskies.skieskits.gui.InventoryType
import com.pokeskies.skieskits.placeholders.PlaceholderManager
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.storage.StorageType
import com.pokeskies.skieskits.utils.Scheduler
import com.pokeskies.skieskits.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.modcommon.MinecraftServerAudiences
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.server.MinecraftServer
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.graalvm.polyglot.Engine
import java.io.File
import java.io.IOException

class SkiesKits : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesKits

        var MOD_ID = "skieskits"
        var MOD_NAME = "SkiesKits"

        val LOGGER: Logger = LogManager.getLogger(MOD_ID)
    }

    lateinit var configDir: File
    var storage: IStorage? = null

    var economyService: IEconomyService? = null
    lateinit var placeholderManager: PlaceholderManager

    var adventure: MinecraftServerAudiences? = null
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    lateinit var graalEngine: Engine

    var gson: Gson = GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.Adapter())
        .registerTypeAdapter(Requirement::class.java, RequirementType.Adapter())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.Adapter())
        .registerTypeAdapter(EconomyType::class.java, EconomyType.Adapter())
        .registerTypeAdapter(StorageType::class.java, StorageType.Adapter())
        .registerTypeAdapter(GenericClickType::class.java, GenericClickType.Adapter())
        .registerTypeAdapter(InventoryType::class.java, InventoryType.Adapter())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(BuiltInRegistries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(BuiltInRegistries.SOUND_EVENT))
        .registerTypeHierarchyAdapter(CompoundTag::class.java, Utils.CodecSerializer(CompoundTag.CODEC))
        .create()

    var gsonPretty: Gson = gson.newBuilder().setPrettyPrinting().create()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skieskits")
        ConfigManager.load()
        try {
            this.storage = IStorage.load(ConfigManager.CONFIG.storage)
        } catch (e: IOException) {
            Utils.printError(e.message)
            this.storage = null
        }

        this.economyService = IEconomyService.getEconomyService(ConfigManager.CONFIG.economy)
        this.placeholderManager = PlaceholderManager()

        this.graalEngine = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer ->
            this.adventure = MinecraftServerAudiences.of(
                server
            )
            this.server = server
            this.nbtOpts = server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
            Scheduler.start()
        })
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server: MinecraftServer ->
            ConfigManager.loadKits()
        })
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerStopped { server: MinecraftServer ->
            this.adventure = null
            this.storage?.close()
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommand().register(
                dispatcher
            )
        }

        ServerPlayConnectionEvents.JOIN.register { event, _, _ ->
            Scheduler.scheduleTask(20, Scheduler.DelayedAction({
                val player = event.player
                Utils.printDebug("Player ${player.name.string} joined the server! Checking ${ConfigManager.KITS.size} kits...")
                if (!player.hasDisconnected()) {
                    for ((id, kit) in ConfigManager.KITS) {
                        Utils.printDebug("Checking kit $id! Kit onJoin=${kit.onJoin}, Player hasPermission=${kit.hasPermission(player)}")
                        if (kit.onJoin && kit.hasPermission(player)) {
                            kit.claim(id, player, silent = true)
                        }
                    }
                }
            }))
        }
    }

    fun reload() {
        this.storage?.close()

        ConfigManager.load()
        try {
            this.storage = IStorage.load(ConfigManager.CONFIG.storage)
        } catch (e: IOException) {
            Utils.printError(e.message)
            this.storage = null
        }
        this.economyService = IEconomyService.getEconomyService(ConfigManager.CONFIG.economy)
    }
}
