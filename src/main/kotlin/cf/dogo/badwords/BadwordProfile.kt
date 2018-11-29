package cf.dogo.badwords

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoGuild
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.lang.Exception

data class BadwordProfile(val id: Int, val guild: DogoGuild, val badwords: List<String>) {

    companion object {
        val col = DogoBot.db?.getCollection("badwords") as MongoCollection

        fun parse(doc: Document) : BadwordProfile? {
            return try {
                BadwordProfile(doc.getInteger("id"), DogoGuild(doc.getString("guild")), doc["badwords"] as List<String>)
            }catch (ex: Exception) { null }
        }
    }

    fun update(){
        if(col.find(Document().append("id", id)).count() >= 1) {
            col.updateOne(Document().append("id", id), export())
        } else {
            col.insertOne(export())
        }
    }

    fun remove(){
        col.deleteOne(Document().append("id", id))
    }

    fun export() : Document {
        return Document().append("id", id).append("guild", guild.id).append("badwords", badwords)
    }
}