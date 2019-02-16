package io.github.dogo.server

import io.github.dogo.core.Database
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.utils.BoundList
import io.github.dogo.utils._static.DiscordAPI
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
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
 * @param[authTime] the authentication time (timestamp in seconds).
 * @param[expiresIn] the expiration time (timestamp in seconds).
 * @param[type] the token type.
 *
 * @author NathanPB
 * @since 3.1.0
 */
data class Token(val token: String, val owner: DogoUser, val authTime: Date, val expiresIn: Date, val type: String){

    /**
     * The token scopes.
     */
    val scopes = BoundList(
            { thescope ->
                transaction {
                    Database.TOKENSCOPES.run {
                        insert {
                            it[token] = this@Token.token
                            it[scope] = thescope
                        }
                    }
                }
            },
            { thescope ->
                transaction {
                    Database.TOKENSCOPES.run {
                        deleteWhere {
                            (token eq this@Token.token) and (scope eq thescope)
                        }
                    }
                }
            },
            {
                transaction {
                    return@transaction Database.TOKENSCOPES.run {
                        slice(scope).select { token eq this@Token.token }
                    }.map { it[Database.TOKENSCOPES.scope] }
                }
            }
    )

    /**
     * Checks if the token is valid.
     * A token is valid when:
     *
     * - The [expiresIn] isn't greater than the current date.
     * - Discord returns a JSON with the *id* field when fetched with its API.
     *
     * @return true if the token is valid. false if it isn't.
     */
    fun isValid() = Date().before(expiresIn) && DiscordAPI.fetchUser(this).has("id")
}