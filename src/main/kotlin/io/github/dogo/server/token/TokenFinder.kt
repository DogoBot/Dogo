package io.github.dogo.server.token

import io.github.dogo.finder.FinderField
import io.github.dogo.finder.IFinder
import org.bson.Document

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
class TokenFinder : IFinder<Token>() {

    /**
     * The token to look for.
     */
    val token = FinderField()

    /**
     * The token owner.
     */
    var owner = FinderField()

    /**
     * The scopes.
     */
    var scopes = FinderField()

    /**
     * The authentication time (timestamp in seconds).
     */
    var authTime = FinderField()

    /**
     * The expiration time (timestamp in seconds).
     */
    var expiresIn = FinderField()

    /**
     * The token tipe.
     */
    var type = FinderField()

    override val col = Token.col
    override fun map(doc: Document) = Token.parse(doc)

}