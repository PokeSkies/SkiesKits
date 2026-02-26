package com.pokeskies.skieskits.config

import com.google.gson.annotations.SerializedName
import com.pokeskies.skieskits.economy.EconomyType
import com.pokeskies.skieskits.storage.StorageType

class MainConfig(
    var debug: Boolean = false,
    var commands: List<String> = listOf("skieskits", "kits", "kit"),
    var economy: EconomyType = EconomyType.PEBBLES,
    @SerializedName("default_menu")
    var defaultMenu: String = "",
    var storage: Storage = Storage(),
    val messages: Messages = Messages()
) {
    class Storage(
        val type: StorageType = StorageType.JSON,
        val host: String = "",
        val port: Int = 3306,
        val database: String = "skieskits",
        val username: String = "root",
        val password: String = "",
        val properties: Map<String, String> = mapOf("useUnicode" to "true", "characterEncoding" to "utf8"),
        @SerializedName("pool_settings")
        val poolSettings: StoragePoolSettings = StoragePoolSettings(),
        @SerializedName("url_override")
        val urlOverride: String = ""
    ) {
        override fun toString(): String {
            return "Storage(type=$type, host='$host', port=$port, database='$database', username='$username', " +
                    "password='$password', properties=$properties, poolSettings=$poolSettings, urlOverride='$urlOverride')"
        }
    }

    class StoragePoolSettings(
        @SerializedName("maximum_pool_size")
        val maximumPoolSize: Int = 10,
        @SerializedName("minimum_idle")
        val minimumIdle: Int = 10,
        @SerializedName("keepalive_time")
        val keepaliveTime: Long = 0,
        @SerializedName("connection_timeout")
        val connectionTimeout: Long = 30000,
        @SerializedName("idle_timeout")
        val idleTimeout: Long = 600000,
        @SerializedName("max_lifetime")
        val maxLifetime: Long = 1800000
    ) {
        override fun toString(): String {
            return "StoragePoolSettings(maximumPoolSize=$maximumPoolSize, minimumIdle=$minimumIdle," +
                    " keepaliveTime=$keepaliveTime, connectionTimeout=$connectionTimeout," +
                    " idleTimeout=$idleTimeout, maxLifetime=$maxLifetime)"
        }
    }

    class Messages(
        @SerializedName("kit_not_found")
        val kitNotFound: String = "<red>Could not find a kit named %kit_name%!",
        @SerializedName("kit_received")
        val kitReceived: String = "<green>You have received the kit %kit_name%!",
        @SerializedName("kit_failed_uses")
        val kitFailedUses: String = "<red>You cannot claim the kit %kit_name% because you have reached the max uses!",
        @SerializedName("kit_failed_cooldown")
        val kitFailedCooldown: String = "<red>You cannot claim the kit %kit_name% because it's still on cooldown for another %kit_cooldown%!",
        @SerializedName("kit_failed_requirements")
        val kitFailedRequirements: String = "<red>You cannot claim the kit %kit_name% because you have not met the requirements!",
        @SerializedName("kit_no_permission")
        val kitNoPermission: String = "<red>You cannot claim the kit %kit_name% because you do not have permission!",
        @SerializedName("kit_no_preview")
        val kitNoPreview: String = "<red>The kit %kit_name% does not have a preview set up!",
        @SerializedName("kit_menu_error")
        val kitMenuError: String = "<red>Could not open the kits menu! Please contact an administrator.",
    ) {
        override fun toString(): String {
            return "Messages(kit_not_found='$kitNotFound', kit_received='$kitReceived', kit_failed_uses='$kitFailedUses'," +
                    " kit_failed_cooldown='$kitFailedCooldown', kit_failed_requirements='$kitFailedRequirements'," +
                    " kit_no_permission='$kitNoPermission', kit_no_preview='$kitNoPreview', kit_menu_error='$kitMenuError')"
        }
    }

    override fun toString(): String {
        return "MainConfig(debug=$debug, commands=$commands, economy=$economy, storage=$storage, messages=$messages)"
    }
}
