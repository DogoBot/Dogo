package io.github.dogo.utils._static

import com.mashape.unirest.http.Unirest
import io.github.dogo.server.token.Token
import org.json.JSONObject

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
            return JSONObject(
                    Unirest.get("https://discordapp.com/api/v6/users/@me")
                            .header("Authorization", "$type $auth")
                            .asString().body
            )
        }
    }
}