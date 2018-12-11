package io.github.dogo.server.token

import io.github.dogo.core.entities.DogoUser
import io.github.dogo.interfaces.IFinder
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
 * The finder for [Token] objects on database.
 *
 * @see[IFinder]
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TokenFinder : Document(), IFinder<Token> {

    /**
     * The token to look for.
     */
    var token: String
        get() = getString("token")
        set(it) = set("token", it)

    /**
     * The token owner.
     */
    var owner: DogoUser
        get() = DogoUser(getString("owner"))
        set(it) = set("owner", it)

    /**
     * The scopes.
     */
    var scopes: List<String>
       get() = get("scopes") as List<String>
       set(it) = set("scopes", it)

    /**
     * The authentication time (timestamp in seconds).
     */
    var authTime: Date
        get() = getDate("authTime")
        set(it) = set("authTime", it)

    /**
     * The expiration time (timestamp in seconds).
     */
    var expiresIn: Date
        get() = getDate("expiresIn")
        set(it) = set("expiresIn", it)

    /**
     * The token tipe.
     */
    var type: String
        get() = getString("type")
        set(it) = set("type", it)

    override fun col() = Token.col
    override fun query() = this
    override fun map(doc: Document) = Token.parse(doc)

}