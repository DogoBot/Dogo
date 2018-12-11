package io.github.dogo.badwords

import com.mongodb.client.MongoCollection
import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
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
 * Data Class that holds Badwords from guilds.
 *
 * @param[guild] the guild.
 * @param[badwords] the badwords.
 *
 * @author NathanPB
 * @since 3.1.0
 */
data class BadwordProfile(val guild: DogoGuild, val badwords: MutableList<String>) {

    companion object {
        val col = DogoBot.db?.getCollection("badwords") as MongoCollection
        val listener = BadwordListener()

        /**
         * Every single guild instance has a cached data once its [BadwordProfile] is accessed. The cache is stored here.
         */
        private val cache = mutableMapOf<String, BadwordProfile>()

        /**
         * Parses a single [Document] into a [BadwordProfile] (nullable)
         * Required Fields:
         * -guild: [String]
         * -badwords: [List]<[String]>
         *
         * @param[doc] the document to parse.
         * @return the [BadwordProfile] (nullable).
         */
        fun parse(doc: Document) : BadwordProfile? {
            return try {
               BadwordProfile(DogoGuild(doc.getString("guild")), (doc["badwords"] as List<String>).toMutableList())
            } catch (ex: Exception) { null }
        }

        /**
         * Clears the badwords cache ([cache])
         */
        fun clearCache() = BadwordProfile.cache.clear()

        /**
         * Gets a cached entry for a guild. If its not found, automatically creates a blank [BadwordProfile] and stores it on database (also automatically cached).
         *
         * @param[guild] the guild to get the profile.
         *
         * @return the profile for the supplied guild.
         */
        fun cachedEntry(guild: DogoGuild) = BadwordProfile.cache[guild.id] ?: BadwordFinder().also{it.guild = guild}.find() ?: BadwordProfile(guild, mutableListOf()).also { it.update() }
    }

    /**
     * Inserts the current profile into database (updates if it already exists).
     * Also automatically cache the profile (or update the cache if it already exists)
     */
    fun update(){
        if(BadwordFinder().also{ it.guild = guild}.count() >= 1) {
            BadwordProfile.col.updateOne(Document().append("guild", guild.id), Document("\$set", export()))
        } else {
            BadwordProfile.col.insertOne(export())
        }
        if(BadwordProfile.cache.containsKey(guild.id)) BadwordProfile.cache.remove(guild.id)
        BadwordProfile.cache[guild.id] = this
    }

    /**
     * Removes the current profile from database. Also remove from cache.
     */
    fun remove(){
        col.deleteOne(Document().append("guild", guild.id))
        if(BadwordProfile.cache.containsKey(guild.id)) BadwordProfile.cache.remove(guild.id)
    }

    /**
     * Exports the current profile for a [Document] instance, ready to be inserted into MongoDB.
     */
    fun export() = Document().append("guild", guild.id).append("badwords", badwords)
}