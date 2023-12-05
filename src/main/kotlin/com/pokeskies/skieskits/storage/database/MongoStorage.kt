package com.pokeskies.skieskits.storage.database

import com.google.gson.reflect.TypeToken
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.connection.ClusterSettings
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.utils.Utils
import org.bson.Document
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


class MongoStorage(config: MainConfig.Storage) : IStorage {
    private var mongoClient: MongoClient? = null
    private var mongoDatabase: MongoDatabase? = null
    private var userdataCollection: MongoCollection<Document>? = null

    init {
        try {
            val credential = MongoCredential.createCredential(
                config.username,
                config.username,
                config.password.toCharArray()
            )
            val settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings { builder: ClusterSettings.Builder ->
                    builder.hosts(listOf(ServerAddress(config.host, config.port)))
                }
                .build()
            this.mongoClient = MongoClients.create(settings)
            this.mongoDatabase = mongoClient!!.getDatabase(config.database)
            this.userdataCollection = this.mongoDatabase!!.getCollection("userdata")
        } catch (e: Exception) {
            throw IOException("Error while attempting to setup Mongo Database: $e")
        }

    }

    override fun getUser(uuid: UUID): UserData {
        if (mongoDatabase == null) {
            Utils.printError("There was an error while attempting to fetch data from the Mongo database!")
            return UserData()
        }
        val doc: Document? = userdataCollection?.find(Filters.eq("uuid", uuid.toString()))?.first()
        return if (doc != null) {
            val mapType: Type = object : TypeToken<HashMap<String, KitData>>() {}.type
            UserData(SkiesKits.INSTANCE.gson.fromJson(doc.getString("kits"), mapType))
        } else {
            UserData()
        }
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        if (mongoDatabase == null) {
            Utils.printError("There was an error while attempting to save data to the Mongo database!")
            return false
        }
        val query = Filters.eq("uuid", uuid.toString())
        var doc: Document? = userdataCollection?.find(query)?.first()
        if (doc == null) {
            doc = Document()
        }
        doc["uuid"] = uuid.toString()
        doc["kits"] = SkiesKits.INSTANCE.gson.toJson(userData.kits)
        val result = this.userdataCollection?.replaceOne(query, doc, ReplaceOptions().upsert(true))

        return result?.wasAcknowledged() ?: false
    }

    override fun close() {
        mongoClient?.close()
    }
}