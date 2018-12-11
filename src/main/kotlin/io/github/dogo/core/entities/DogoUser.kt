package io.github.dogo.core.entities

import io.github.dogo.core.profiles.PermGroup
import io.github.dogo.core.profiles.PermGroupSet
import io.github.dogo.exceptions.DiscordException
import io.github.dogo.server.token.TokenFinder
import io.github.dogo.utils.DiscordAPI
import net.dv8tion.jda.core.entities.User
import org.bson.Document
import org.json.JSONObject

data class DogoUser (val id : String){
    companion object {
        val col = io.github.dogo.core.DogoBot.db?.getCollection("users")
    }

    constructor(usr : User) : this(usr.id)
    val usr = io.github.dogo.core.DogoBot.jda?.getUserById(id)

    init {
        if((DogoGuild.col?.count(Document("ID", id)) as Long) < 1){
            DogoGuild.col.insertOne(Document("ID", id))
        }
    }

    var lang : String
        get() {
            val doc = find()
            return if(doc.containsKey("lang")) doc.getString("lang") else "en_US"
        }
        set(value) = update(Document("\$set", Document("lang", value)))

    fun fetchUser() : JSONObject {
        val dogo = this
        return TokenFinder().apply {
            owner = dogo
        }.findAll().filter { it.isValid() }
                .firstOrNull { it.scopes.contains("identify") || it.scopes.contains("email")}
                ?.let {
                    DiscordAPI.fetchUser(it)
                } ?: throw DiscordException("Invalid or Unknown Token")
    }

    fun find() : Document {
        return DogoGuild.col?.find(Document("ID", id))?.first() as Document
    }

    fun update(replace : Document) {
        DogoGuild.col?.findOneAndUpdate(Document("ID", id), replace)
    }

    fun getPermGroups() : PermGroupSet {
        return PermGroupSet(
                PermGroup.col.find()
                        .map { PermGroup(it.getString("ID")) }
                        .filter { (it.id as String).toLong() <= 0 }
                        .filter { it.affectsEveryone() || it.applyTo.contains(id) }
        )
    }

    fun formatName(g: DogoGuild? = null) : String {
        return if(g?.g?.isMember(this.usr) == true){
            g.g.getMember(this.usr).effectiveName
        } else {
            this.usr?.name
        }+"#${this.usr?.discriminator}"
    }

}