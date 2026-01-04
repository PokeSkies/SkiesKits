package com.pokeskies.skieskits.config.actions

import com.google.gson.*
import com.pokeskies.skieskits.config.actions.types.*
import java.lang.reflect.Type

enum class ActionType(val identifier: String, val clazz: Class<*>) {
    COMMAND_CONSOLE("command_console", CommandConsole::class.java),
    COMMAND_PLAYER("command_player", CommandPlayer::class.java),
    MESSAGE("message", MessagePlayer::class.java),
    BROADCAST("broadcast", MessageBroadcast::class.java),
    PLAYSOUND("playsound", PlaySound::class.java),
    GIVE_XP("give_xp", GiveXP::class.java),
    CURRENCY_DEPOSIT("currency_deposit", CurrencyDeposit::class.java),
    CURRENCY_WITHDRAW("currency_withdraw", CurrencyWithdraw::class.java),
    CURRENCY_SET("currency_set", CurrencySet::class.java),
    GIVE_ITEM("give_item", GiveItem::class.java),
    TAKE_ITEM("take_item", TakeItem::class.java),
    NEXT_PAGE("next_page", NextPage::class.java),
    PREVIOUS_PAGE("previous_page", PreviousPage::class.java),
    LAST_PAGE("last_page", LastPage::class.java),
    FIRST_PAGE("first_page", FirstPage::class.java),
    PREVIEW_KIT("preview_kit", PreviewKit::class.java),
    CLOSE("close", Close::class.java),
    OPEN_MENU("open_menu", OpenMenu::class.java),;

    companion object {
        fun valueOfAnyCase(name: String): ActionType? {
            for (type in entries) {
                if (name.equals(type.identifier, true)) return type
            }
            return null
        }
    }

    internal class Adapter : JsonSerializer<Action>, JsonDeserializer<Action> {
        override fun serialize(src: Action, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return context.serialize(src, src::class.java)
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Action {
            val jsonObject: JsonObject = json.getAsJsonObject()
            val type: ActionType? = ActionType.valueOfAnyCase(jsonObject.get("type").asString)
            return try {
                context.deserialize(json, type!!.clazz)
            } catch (e: NullPointerException) {
                throw JsonParseException("Could not deserialize action type: $type", e)
            }
        }
    }
}
