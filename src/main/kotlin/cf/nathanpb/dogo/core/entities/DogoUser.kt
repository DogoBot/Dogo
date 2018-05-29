package cf.nathanpb.dogo.core.entities

import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.profiles.PermGroup
import cf.nathanpb.dogo.core.profiles.PermGroupSet
import net.dv8tion.jda.core.entities.User
import org.bson.Document

class DogoUser (id : String){
    val id = id
    companion object {
        val col = DogoBot.db?.getCollection("USERS")
    }

    constructor(usr : User) : this(usr.id)
    val usr = DogoBot.jda?.getUserById(id)

    init {
        if((DogoGuild.col?.count(Document("ID", id)) as Long) < 1){
            DogoGuild.col?.insertOne(Document("ID", id))
        }
    }

    var lang : String
        get() {
            val doc = find()
            return if(doc.containsKey("lang")) doc.getString("lang") else "en_US"
        }
        set(value) = update(Document("\$set", Document("lang", value)))

    fun find() : Document {
        return DogoGuild.col?.find(Document("ID", id))?.first() as Document
    }

    fun update(replace : Document) {
        DogoGuild.col?.findOneAndUpdate(Document("ID", id), replace)
    }

    fun getPermGroups() : PermGroupSet {
        return PermGroupSet(
                PermGroup.col.find()
                        .map { g -> PermGroup(g.getString("ID")) }
                        .filter { g -> (g.id as String).toLong() <= 0 }
                        .filter { g -> g.affectsEveryone() || g.applyTo.contains(id) }
        )
    }

}