package com.pokeskies.skieskits.storage.database

import com.google.gson.reflect.TypeToken
import com.mongodb.ConnectionString
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.config.MainConfig
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.IStorage
import com.pokeskies.skieskits.utils.Utils
import org.bson.Document
import java.lang.reflect.Type
import java.util.*


class MongoStorage(config: MainConfig.Storage) : IStorage {
    private var mongoClient: MongoClient? = null
    private var mongoDatabase: MongoDatabase? = null
    private var userdataCollection: MongoCollection<Document>? = null

    init {
        val connectionString = ConnectionString("mongodb://${config.username}:${config.password}@${config.host}:${config.port}")
        try {
            this.mongoClient = MongoClients.create(connectionString)
            this.mongoDatabase = mongoClient!!.getDatabase(config.database)
            this.userdataCollection = this.mongoDatabase!!.getCollection("listings")
        } catch (e: Exception) {
            Utils.error("Error while attempting to setup Mongo Database: $e")
        }

    }

    override fun getUser(uuid: UUID): UserData {
        if (mongoDatabase == null) {
            Utils.error("There was an error while attempting to fetch data from the Mongo database!")
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

    override fun saveUser(uuid: UUID, userData: UserData) {
        if (mongoDatabase == null) {
            Utils.error("There was an error while attempting to save data to the Mongo database!")
            return
        }
        val query = Filters.eq("uuid", uuid.toString())
        var doc: Document? = userdataCollection?.find(query)?.first()
        if (doc == null) {
            doc = Document()
        }
        doc["uuid"] = uuid.toString()
        doc["data"] = SkiesKits.INSTANCE.gson.toJson(userData.kits)
        this.userdataCollection?.replaceOne(query, doc, ReplaceOptions().upsert(true))
    }

    override fun close() {
        mongoClient?.close()
    }
}