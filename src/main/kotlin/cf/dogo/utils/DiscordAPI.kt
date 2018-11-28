package cf.dogo.utils

import org.json.JSONObject

class DiscordAPI {
    companion object {
        fun fetchUser(auth: String, type: String): JSONObject {
            return JSONObject(WebUtils.get(
                    "https://discordapp.com/api/v6/users/@me",
                    headers = arrayOf(Pair("Authorization", "$type $auth"))
            ))
        }
    }
}