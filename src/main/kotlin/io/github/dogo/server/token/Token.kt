package io.github.dogo.server.token

import com.mongodb.client.MongoCollection
import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.utils._static.DiscordAPI
import org.bson.Document
import java.util.*

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
 * Stores information about a Discord oAuth2 Token.
 *
 * @param[token] the token.
 * @param[owner] the token owner.
 * @param[scopes] the scopes.
 * @param[authTime] the authentication time (timestamp in seconds).
 * @param[expiresIn] the expiration time (timestamp in seconds).
 * @param[type] the token type.
 *
 * @author NathanPB
 * @since 3.1.0
 */
data class Token(val token: String, val owner: DogoUser, val scopes: Array<String>, val authTime: Date, val expiresIn: Date, val type: String){
    companion object {
        val col = DogoBot.db?.getCollection("tokens") as MongoCollection

        /**
         * Parses a [Document] to [Token].
         *
         * # Required Fields:
         * - token: String
         * - owner: String
         * - scopes: List<String>
         * - authTime: Date
         * - expiresIn: Date
         * - type: String
         */
        fun parse(doc: Document) = Token(
                doc.getString("token"),
                DogoUser(doc.getString("owner")),
                (doc["scopes"] as List<String>).toTypedArray(),
                doc.getDate("authTime"),
                doc.getDate("expiresIn"),
                doc.getString("type")
        )
    }

    /**
     * Checks if the token is valid.
     * A token is valid when:
     *
     * - The [expiresIn] isn't greater than the current date.
     * - Discord returns a JSON with the *id* field when fetched with its API.
     *
     * @return true if the token is valid. false if it isn't.
     */
    fun isValid() : Boolean {
        return Date().before(expiresIn) && DiscordAPI.fetchUser(this).has("id")
    }


    /**
     * Inserts a token on database.
     *
     * # Note
     * JUST INSERT, it DOES NOT update an existing token.
     */
    fun update() {
        Token.col.insertOne(export())
    }

    /**
     * Parses a [Token] into a [Document] object.
     *
     * @return the document.
     */
    fun export() = Document()
            .append("token", token)
            .append("owner", owner.id)
            .append("scopes", scopes.toList())
            .append("authTime", authTime)
            .append("expiresIn", expiresIn)
            .append("type", type)
}