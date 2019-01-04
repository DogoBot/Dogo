package io.github.dogo.core.entities

import io.github.dogo.core.DogoBot
import io.github.dogo.core.profiles.PermGroup
import io.github.dogo.core.profiles.PermGroupSet
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Webhook
import org.bson.Document
import java.util.*
import kotlin.collections.ArrayList

class DogoGuild (val id : String){
    companion object {
        val col = DogoBot.db?.getCollection("guilds")
    }

    constructor(g : Guild) : this(g.id)
    val g = io.github.dogo.core.DogoBot.jda?.getGuildById(id)

    init {
        if(col?.count(Document("ID", id)) == 0L){
            col?.insertOne(Document("ID", id))
        }
    }

    var prefix : ArrayList<String>
        get() {
            val doc = find()
            return if(doc.containsKey("prefix")) doc["prefix"] as ArrayList<String> else ArrayList()
        }
        set(value) = update(Document("\$set", Document("prefix", value)))

    var permgroups : PermGroupSet
        get() {
            val doc = find()
            if(!doc.containsKey("permgroups")) update(Document("\$set", Document("permgroups", ArrayList<String>())))
            val list = PermGroupSet()
            for(s in find().get("permgroups") as ArrayList<String>){
                list.add(PermGroup(s))
            }
            return list
        }
        set(value) {
            //todo fix that gambiarra
            val maped = ArrayList(Arrays.asList(value.map { v -> v.id }.toTypedArray()))
            update(Document("\$set", Document("permgroups", maped)))
        }

    val badwords: io.github.dogo.badwords.BadwordProfile
        get() = io.github.dogo.badwords.BadwordProfile.cachedEntry(this)

    var newsWebhook: Webhook?
        get() = g?.webhooks?.complete()?.firstOrNull {
            val doc = find()
            if(!doc.containsKey("newsWebhook") || doc["newsWebhook"] != null){
                it.id == doc.getString("newsWebhook")
            } else false
        }
        set(it) = update(Document("\$set", Document("newsWebhook", it?.id)))

    fun find() : Document {
        return col?.find(Document("ID", id))?.first() as Document
    }

    fun update(replace : Document) {
        col?.findOneAndUpdate(Document("ID", id), replace)
    }
}