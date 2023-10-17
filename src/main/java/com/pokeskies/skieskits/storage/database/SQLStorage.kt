package com.pokeskies.skieskits.storage.database

import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.IStorage
import java.util.*

class SQLStorage : IStorage {
    override fun getUser(uuid: UUID): UserData {
        TODO("Not yet implemented")
    }

    override fun saveUser(uuid: UUID, userData: UserData) {
        TODO("Not yet implemented")
    }
}