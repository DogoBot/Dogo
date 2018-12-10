package io.github.dogo.badwords

import com.mongodb.client.MongoCollection
import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import org.bson.Document

data class BadwordProfile(val guild: DogoGuild, val badwords: MutableList<String>) {

    companion object {
        val col = DogoBot.db?.getCollection("badwords") as MongoCollection
        val listener = io.github.dogo.badwords.BadwordListener()

        private val cache = mutableMapOf<String, io.github.dogo.badwords.BadwordProfile>()

        fun parse(doc: Document) : io.github.dogo.badwords.BadwordProfile? {
            return try {
                io.github.dogo.badwords.BadwordProfile(DogoGuild(doc.getString("guild")), (doc["badwords"] as List<String>).toMutableList())
            }catch (ex: Exception) { null }
        }

        fun clearCache() = io.github.dogo.badwords.BadwordProfile.Companion.cache.clear()
        fun cachedEntry(guild: DogoGuild) = io.github.dogo.badwords.BadwordProfile.Companion.cache[guild.id] ?: io.github.dogo.badwords.BadwordFinder().also{it.guild = guild}.find() ?: io.github.dogo.badwords.BadwordProfile(guild, mutableListOf()).also { it.update() }
    }

    fun update(){
        if(io.github.dogo.badwords.BadwordFinder().also{ it.guild = guild}.count() >= 1) {
            io.github.dogo.badwords.BadwordProfile.Companion.col.updateOne(Document().append("guild", guild.id), Document("\$set", export()))
        } else {
            io.github.dogo.badwords.BadwordProfile.Companion.col.insertOne(export())
        }
        if(io.github.dogo.badwords.BadwordProfile.Companion.cache.containsKey(guild.id)) io.github.dogo.badwords.BadwordProfile.Companion.cache.remove(guild.id)
        io.github.dogo.badwords.BadwordProfile.Companion.cache[guild.id] = this
    }

    fun remove(){
        io.github.dogo.badwords.BadwordProfile.Companion.col.deleteOne(Document().append("guild", guild.id))
    }

    fun export() : Document {
        return Document().append("guild", guild.id).append("badwords", badwords)
    }
}