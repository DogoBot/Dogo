package cf.dogo.server.token

import cf.dogo.core.entities.DogoUser
import cf.dogo.interfaces.IFinder
import org.bson.Document
import java.util.*

class TokenFinder : Document(), IFinder<Token> {

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

    override fun col() = Token.col
    override fun query() = this
    override fun map(doc: Document) = Token.parse(doc)

}