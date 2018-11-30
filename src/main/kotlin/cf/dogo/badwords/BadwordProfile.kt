package cf.dogo.badwords

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoGuild
import com.mongodb.client.MongoCollection
import org.bson.Document
import java.lang.Exception

data class BadwordProfile(val guild: DogoGuild, val badwords: MutableList<String>) {

    companion object {
        val col = DogoBot.db?.getCollection("badwords") as MongoCollection
        val listener = BadwordListener()

        private val cache = mutableMapOf<String, BadwordProfile>()

        fun parse(doc: Document) : BadwordProfile? {
            return try {
                BadwordProfile(DogoGuild(doc.getString("guild")), (doc["badwords"] as List<String>).toMutableList())
            }catch (ex: Exception) { null }
        }

        fun clearCache() = cache.clear()
        fun cachedEntry(guild: DogoGuild) = cache[guild.id] ?: BadwordFinder().also{it.guild = guild}.find() ?: BadwordProfile(guild, mutableListOf()).also { it.update() }
    }

    fun update(){
        if(BadwordFinder().also{ it.guild = guild}.count() >= 1) {
            col.updateOne(Document().append("guild", guild.id), export())
        } else {
            col.insertOne(export())
        }
        if(cache.containsKey(guild.id)) cache.remove(guild.id)
        cache[guild.id] = this
    }

    fun remove(){
        col.deleteOne(Document().append("guild", guild.id))
    }

    fun export() : Document {
        return Document().append("guild", guild.id).append("badwords", badwords.toList())
    }
}