package com.pokeskies.skieskits.storage.file

import com.pokeskies.skieskits.data.UserData
import java.util.*

class FileData {
    var userdata: HashMap<UUID, UserData> = HashMap()
    override fun toString(): String {
        return "FileData(userdata=$userdata)"
    }
}