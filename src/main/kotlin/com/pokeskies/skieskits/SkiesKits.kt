package com.pokeskies.skieskits

import ca.landonjw.gooeylibs2.api.tasks.Task
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.pokeskies.skieskits.commands.BaseCommand
import com.pokeskies.skieskits.config.ConfigManager
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.ComparisonType
import com.pokeskies.skieskits.config.requirements.Requirement
import com.pokeskies.skieskits.config.requirements.RequirementType
import com.pokeskies.skieskits.economy.EconomyType
import com.pokeskies.skieskits.economy.IEconomyService
import com.pokeskies.skieskits.placeholders.PlaceholderManager
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.storage.StorageType
import com.pokeskies.skieskits.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
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
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

class SkiesKits : ModInitializer {
    companion object {
        lateinit var INSTANCE: SkiesKits
        val LOGGER: Logger = LogManager.getLogger("skieskits")
    }

    lateinit var configDir: File
    lateinit var configManager: ConfigManager
    var storage: IStorage? = null

    var economyService: IEconomyService? = null
    lateinit var placeholderManager: PlaceholderManager

    var adventure: FabricServerAudiences? = null
    lateinit var server: MinecraftServer
    lateinit var nbtOpts: RegistryOps<Tag>

    lateinit var graalEngine: Engine

    var gson: Gson = GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.ActionTypeAdaptor())
        .registerTypeAdapter(Requirement::class.java, RequirementType.RequirementTypeAdaptor())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
        .registerTypeAdapter(EconomyType::class.java, EconomyType.EconomyTypeAdaptor())
        .registerTypeAdapter(StorageType::class.java, StorageType.StorageTypeAdaptor())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(BuiltInRegistries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(BuiltInRegistries.SOUND_EVENT))
        .registerTypeHierarchyAdapter(CompoundTag::class.java, Utils.CodecSerializer(CompoundTag.CODEC))
        .create()

    var gsonPretty: Gson = gson.newBuilder().setPrettyPrinting().create()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skieskits")
        this.configManager = ConfigManager(configDir)
        try {
            this.storage = IStorage.load(configManager.config.storage)
        } catch (e: IOException) {
            Utils.printError(e.message)
            this.storage = null
        }

        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)
        this.placeholderManager = PlaceholderManager()

        this.graalEngine = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer ->
            this.adventure = FabricServerAudiences.of(
                server
            )
            this.server = server
            this.nbtOpts = server.registryAccess().createSerializationContext(NbtOps.INSTANCE)
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
            Task.builder().execute { ctx ->
                val player = event.player
                Utils.printDebug("Player ${player.name.string} joined the server! Checking ${ConfigManager.KITS.size} kits...")
                if (!player.hasDisconnected()) {
                    for ((id, kit) in ConfigManager.KITS) {
                        Utils.printDebug("Checking kit $id! Kit onJoin=${kit.onJoin}, Player hasPermission=${kit.hasPermission(player)}")
                        if (kit.onJoin && kit.hasPermission(player)) {
                            kit.claim(id, player)
                        }
                    }
                }
            }
            .delay(20L)
            .build()
        }
    }

    fun reload() {
        this.storage?.close()

        this.configManager.reload()
        try {
            this.storage = IStorage.load(configManager.config.storage)
        } catch (e: IOException) {
            Utils.printError(e.message)
            this.storage = null
        }
        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(configDir.toPath())
            if (file.exists()) {
                FileReader(file, Charsets.UTF_8).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}
