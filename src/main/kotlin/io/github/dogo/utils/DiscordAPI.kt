package io.github.dogo.utils

import io.github.dogo.server.token.Token
import org.json.JSONObject

/**
 * Utility static methods about Discord.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class DiscordAPI {
    companion object {

        /**
         * Fetches a user from Discord API.
         *
         * @param[token] the [Token] object.
         *
         * @return the data provided by Discord.
         * @throws [java.io.IOException]
         */
        fun fetchUser(token: Token) = fetchUser(token.token, token.type)

        /**
         * Fetches a user from Discord API.
         *
         * @param[auth] the token.
         * @param[type] the token type.
         *
         * @return The data provided by Discord
         * @throws [java.io.IOException]
         */
        fun fetchUser(auth: String, type: String): JSONObject {
            return JSONObject(WebUtils.get(
                    "https://discordapp.com/api/v6/users/@me",
                    headers = arrayOf(Pair("Authorization", "$type $auth"))
            ))
        }
    }
}