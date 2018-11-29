package cf.dogo.server.token

import cf.dogo.core.entities.DogoUser
import org.bson.Document
import java.util.*

class TokenFinder : Document() {

    var token: String
        get() = getString("token")
        set(it) = set("token", it)

    var owner: DogoUser
        get() = DogoUser(getString("owner"))
        set(it) = set("owner", it)

    var scopes: List<String>
       get() = get("scopes") as List<String>
       set(it) = set("scopes", it)

    var authTime: Date
        get() = getDate("authTime")
        set(it) = set("authTime", it)

    var expiresIn: Date
        get() = getDate("expiresIn")
        set(it) = set("expiresIn", it)

    var type: String
        get() = getString("type")
        set(it) = set("type", it)

    fun find(): Token? {
       return Token.col.find(this)?.firstOrNull()?.let {
           Token.parse(it)
       }
    }

    fun findAll() : List<Token> {
        return (Token.col.find(this)?.toList() ?: emptyList<Document>())
                .map { Token.parse(it) }
    }

    fun count() = Token.col.count(this)
}