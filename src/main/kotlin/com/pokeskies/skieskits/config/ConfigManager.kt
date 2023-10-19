package com.pokeskies.skieskits.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.utils.Utils
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption


class ConfigManager(private val configDir: File) {
    lateinit var config: MainConfig

    companion object {
        var KITS: BiMap<String, Kit> = HashBiMap.create()
    }

    init {
        reload()
    }

    fun reload() {
        copyDefaults()
        config = SkiesKits.INSTANCE.loadFile("config.json", MainConfig())
        loadKits()
    }

    fun copyDefaults() {
        val classLoader = SkiesKits::class.java.classLoader

        configDir.mkdirs()

        // Main Config
        val configFile = configDir.resolve("config.json")
        if (!configFile.exists()) {
            try {
                val inputStream: InputStream = classLoader.getResourceAsStream("assets/skieskits/config.json")
                Files.copy(inputStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.error("Failed to copy the default config file: $e - ${e.message}")
            }
        }

        // If the 'kits' directory does not exist, create it and copy the default example GUI
        val guiDirectory = configDir.resolve("kits")
        if (!guiDirectory.exists()) {
            guiDirectory.mkdirs()
            val file = guiDirectory.resolve("example_kit.json")
            try {
                val resourceFile: Path =
                    Path.of(classLoader.getResource("assets/skieskits/kits/example_kit.json").toURI())
                Files.copy(resourceFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.error("Failed to copy the default kit file: " + e.message)
            }
        }
    }

    private fun loadKits() {
        KITS.clear()

        val dir = configDir.resolve("kits")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            KITS[id] = SkiesKits.INSTANCE.gson.fromJson(JsonParser.parseReader(jsonReader), Kit::class.java)
                            Utils.info("Successfully read and loaded the file $fileName!")
                        } catch (ex: Exception) {
                            Utils.error("Error while trying to parse the file $fileName as a GUI!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.error("File $fileName is either not a file or is not a .json file!")
                    }
                }
            }
        } else {
            Utils.error("The 'kits' directory either does not exist or is not a directory!")
        }
    }
}