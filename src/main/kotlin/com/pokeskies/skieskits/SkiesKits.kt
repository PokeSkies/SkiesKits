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
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.utils.Utils
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStopped
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.sound.SoundEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
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

    private lateinit var configDir: File
    lateinit var configManager: ConfigManager
    lateinit var storage: IStorage

    var economyService: IEconomyService? = null

    var adventure: FabricServerAudiences? = null
    var server: MinecraftServer? = null

    var gson: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
        .registerTypeAdapter(Action::class.java, ActionType.ActionTypeAdaptor())
        .registerTypeAdapter(Requirement::class.java, RequirementType.RequirementTypeAdaptor())
        .registerTypeAdapter(ComparisonType::class.java, ComparisonType.ComparisonTypeAdaptor())
        .registerTypeAdapter(EconomyType::class.java, EconomyType.EconomyTypeAdaptor())
        .registerTypeHierarchyAdapter(Item::class.java, Utils.RegistrySerializer(Registries.ITEM))
        .registerTypeHierarchyAdapter(SoundEvent::class.java, Utils.RegistrySerializer(Registries.SOUND_EVENT))
        .registerTypeHierarchyAdapter(NbtCompound::class.java, Utils.CodecSerializer(NbtCompound.CODEC))
        .create()

    override fun onInitialize() {
        INSTANCE = this

        this.configDir = File(FabricLoader.getInstance().configDirectory, "skieskits")
        this.configManager = ConfigManager(configDir)
        this.storage = IStorage.load(configManager.config.storage)

        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer? ->
            this.adventure = FabricServerAudiences.of(
                server!!
            )
            this.server = server
        })
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerStopped { server: MinecraftServer? ->
            this.adventure = null
            this.storage.close()
        })
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            BaseCommand().register(
                dispatcher
            )
        }

        ServerPlayConnectionEvents.JOIN.register { event, _, _ ->
            Task.builder().execute { ctx ->
                val player = event.player
                if (!player.isDisconnected) {
                    for ((id, kit) in ConfigManager.KITS) {
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
        this.configManager.reload()
        this.storage = IStorage.load(configManager.config.storage)
        this.economyService = IEconomyService.getEconomyService(configManager.config.economy)
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = gson.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(gson.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T) {
        val file = File(configDir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(gson.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
