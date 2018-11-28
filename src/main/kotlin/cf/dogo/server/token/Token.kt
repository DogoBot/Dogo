package cf.dogo.server.token

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoUser
import cf.dogo.statistics.Statistic
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.util.*

data class Token(val token: String, val owner: DogoUser, val scopes: Array<String>, val authTime: Date, val expiresIn: Date, val type: String){
    companion object {
        val col = DogoBot.db?.getCollection("tokens") as MongoCollection

        fun parse(doc: Document) = Token(
                doc.getString("token"),
                DogoUser(doc.getString("owner")),
                (doc["scopes"] as List<String>).toTypedArray(),
                doc.getDate("authTime"),
                doc.getDate("expiresIn"),
                doc.getString("type")
        )
    }

    fun update() {
        Token.col.insertOne(export())
    }

    fun export() = Document()
            .append("token", token)
            .append("owner", owner.id)
            .append("scopes", scopes.toList())
            .append("authTime", authTime)
            .append("expiresIn", expiresIn)
            .append("type", type)
}