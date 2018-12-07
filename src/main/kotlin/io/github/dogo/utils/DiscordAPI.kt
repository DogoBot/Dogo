package io.github.dogo.utils

import io.github.dogo.server.token.Token
import org.json.JSONObject

class DiscordAPI {
    companion object {

        fun fetchUser(token: Token) = fetchUser(token.token, token.type)
        fun fetchUser(auth: String, type: String): JSONObject {
            return JSONObject(WebUtils.get(
                    "https://discordapp.com/api/v6/users/@me",
                    headers = arrayOf(Pair("Authorization", "$type $auth"))
            ))
        }
    }
}