package com.pokeskies.skieskits.config

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.utils.Utils
import java.io.*
import java.nio.file.Files
import java.nio.file.StandardCopyOption


object ConfigManager {
    private var assetPackage = "assets/${SkiesKits.MOD_ID}"

    lateinit var CONFIG: MainConfig

    var KITS: BiMap<String, Kit> = HashBiMap.create()
    var PREVIEWS: BiMap<String, PreviewConfig> = HashBiMap.create()
    var MENUS: BiMap<String, KitMenuConfig> = HashBiMap.create()

    fun load() {
        copyDefaults()

        CONFIG = loadFile("config.json", MainConfig())

        loadKits()
        loadPreviews()
        loadMenus()
    }

    fun copyDefaults() {
        val classLoader = SkiesKits::class.java.classLoader

        SkiesKits.INSTANCE.configDir.mkdirs()

        attemptDefaultFileCopy(classLoader, "config.json")
        attemptDefaultFileCopy(classLoader, "kits/example_kit.json")
        attemptDefaultFileCopy(classLoader, "previews/example_preview.json")
        attemptDefaultFileCopy(classLoader, "menus/example_menu.json")
    }

    fun loadKits() {
        KITS.clear()

        val dir = SkiesKits.INSTANCE.configDir.resolve("kits")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            val kit = SkiesKits.INSTANCE.gsonPretty.fromJson(JsonParser.parseReader(jsonReader), Kit::class.java)
                            kit.id = id
                            KITS[id] = kit
                            Utils.printInfo("Successfully read and loaded the Kit file $fileName!")
                        } catch (ex: Exception) {
                            Utils.printError("Error while trying to parse the file $fileName as a Kit!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.printError("File $fileName is either not a file or is not a .json file!")
                    }
                }
            }
        } else {
            Utils.printError("The 'kits' directory either does not exist or is not a directory!")
        }
    }

    fun loadPreviews() {
        PREVIEWS.clear()

        val dir = SkiesKits.INSTANCE.configDir.resolve("previews")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            PREVIEWS[id] = SkiesKits.INSTANCE.gsonPretty.fromJson(
                                JsonParser.parseReader(jsonReader),
                                PreviewConfig::class.java
                            )
                            Utils.printInfo("Successfully read and loaded the Preview Menu file $fileName!")
                        } catch (ex: Exception) {
                            Utils.printError("Error while trying to parse the file $fileName as a Preview Menu!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.printError("File $fileName is either not a file or is not a .json file!")
                    }
                }
            }
        } else {
            Utils.printError("The 'previews' directory either does not exist or is not a directory!")
        }
    }

    fun loadMenus() {
        MENUS.clear()

        val dir = SkiesKits.INSTANCE.configDir.resolve("menus")
        if (dir.exists() && dir.isDirectory) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    val fileName = file.name
                    if (file.isFile && fileName.contains(".json")) {
                        val id = fileName.substring(0, fileName.lastIndexOf(".json"))
                        val jsonReader = JsonReader(InputStreamReader(FileInputStream(file), Charsets.UTF_8))
                        try {
                            MENUS[id] = SkiesKits.INSTANCE.gsonPretty.fromJson(
                                JsonParser.parseReader(jsonReader),
                                KitMenuConfig::class.java
                            )
                            Utils.printInfo("Successfully read and loaded the Kit Menu file $fileName!")
                        } catch (ex: Exception) {
                            Utils.printError("Error while trying to parse the file $fileName as a Kit Menu!")
                            ex.printStackTrace()
                        }
                    } else {
                        Utils.printError("File $fileName is either not a file or is not a .json file!")
                    }
                }
            }
        } else {
            Utils.printError("The 'menus' directory either does not exist or is not a directory!")
        }
    }

    fun <T : Any> loadFile(filename: String, default: T, create: Boolean = false): T {
        val file = File(SkiesKits.INSTANCE.configDir, filename)
        var value: T = default
        try {
            Files.createDirectories(SkiesKits.INSTANCE.configDir.toPath())
            if (file.exists()) {
                FileReader(file).use { reader ->
                    val jsonReader = JsonReader(reader)
                    value = SkiesKits.INSTANCE.gsonPretty.fromJson(jsonReader, default::class.java)
                }
            } else if (create) {
                Files.createFile(file.toPath())
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(SkiesKits.INSTANCE.gsonPretty.toJson(default))
                    fileWriter.flush()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return value
    }

    fun <T> saveFile(filename: String, `object`: T): Boolean {
        val dir = SkiesKits.INSTANCE.configDir
        val file = File(dir, filename)
        try {
            FileWriter(file).use { fileWriter ->
                fileWriter.write(SkiesKits.INSTANCE.gsonPretty.toJson(`object`))
                fileWriter.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun attemptDefaultFileCopy(classLoader: ClassLoader, fileName: String) {
        val file = SkiesKits.INSTANCE.configDir.resolve(fileName)
        if (!file.exists()) {
            try {
                file.parentFile?.mkdirs()
                val stream = classLoader.getResourceAsStream("${assetPackage}/$fileName")
                    ?: throw NullPointerException("File not found $fileName")

                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                Utils.printError("Failed to copy the default file '$fileName': $e")
            }
        }
    }
}
