package com.pokeskies.skieskits.storage.file

import com.pokeskies.skieskits.SkiesKits
import com.pokeskies.skieskits.data.UserData
import com.pokeskies.skieskits.storage.IStorage
import java.util.*

class FileStorage : IStorage {
    private var fileData: FileData = SkiesKits.INSTANCE.loadFile(STORAGE_FILENAME, FileData(), true)

    companion object {
        private const val STORAGE_FILENAME = "storage.json"
    }

    override fun getUser(uuid: UUID): UserData {
        val userData = fileData.userdata[uuid]
        return userData ?: UserData()
    }

    override fun saveUser(uuid: UUID, userData: UserData): Boolean {
        fileData.userdata[uuid] = userData
        return SkiesKits.INSTANCE.saveFile(STORAGE_FILENAME, fileData)
    }

    override fun isConnected(): Boolean {
        return true
    }
}