package cf.dogo.core.entities

import cf.dogo.core.profiles.PermGroup
import cf.dogo.core.profiles.PermGroupSet
import cf.dogo.exceptions.DiscordException
import cf.dogo.server.token.Token
import cf.dogo.server.token.TokenFinder
import cf.dogo.utils.DiscordAPI
import net.dv8tion.jda.core.entities.User
import org.bson.Document
import org.json.JSONObject
import java.lang.Exception

data class DogoUser (val id : String){
    companion object {
        val col = cf.dogo.core.DogoBot.db?.getCollection("users")
    }

    constructor(usr : User) : this(usr.id)
    val usr = cf.dogo.core.DogoBot.jda?.getUserById(id)

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