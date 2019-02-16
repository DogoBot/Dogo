package io.github.dogo.core.entities

import io.github.dogo.core.Database
import io.github.dogo.core.DogoBot
import io.github.dogo.core.permissions.PermGroup
import io.github.dogo.core.permissions.PermGroupSet
import io.github.dogo.exceptions.DiscordException
import io.github.dogo.utils._static.DiscordAPI
import net.dv8tion.jda.core.entities.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.json.JSONObject
import java.util.*

class DogoUser private constructor(val id : String){
    companion object {
        val cache = LinkedList<DogoUser>()

        fun from(id: String) = cache.firstOrNull { it.id == id } ?: DogoUser(id).also {
            it.usr = DogoBot.jda?.getUserById(id)
        }

        fun from(usr: User) = cache.firstOrNull { it.id == usr.id } ?: DogoUser(usr.id).also {
            it.usr = usr
        }
    }

    init {
        transaction {
            if(Database.USERS.selectAll().count() == 0) {
                Database.USERS.insert {
                    it[id] = this@DogoUser.id
                }
            }
        }
        cache.add(this)
    }


    var usr: User? = null

    var lang : String
        get() = transaction {
            return@transaction Database.USERS.run {
                Database.USERS.slice(lang).select { id eq this@DogoUser.id }.first()[lang]
            }
        }
        set(newLang) = transaction {
            Database.USERS.run {
                Database.USERS.update({ id eq this@DogoUser.id }){
                    it[lang] = newLang
                }
            }
        }

    fun fetchUser() : JSONObject {
        return transaction {
            Database.TOKENS.run {
                (this innerJoin Database.TOKENSCOPES)
                    .slice(token, type)
                    .select {
                        (user eq this@DogoUser.id) and
                        (expiresIn greater DateTime.now()) and
                        (Database.TOKENSCOPES.token inList(listOf("email", "identify")))
                    }.firstOrNull()
                    ?.let { DiscordAPI.fetchUser(it[token], it[type])} ?: throw DiscordException("Invalid or Unknown Token")
            }
        }
    }

    fun formatName(g: DogoGuild? = null) : String {
        return if(g?.g?.isMember(this.usr) == true){
            g.g!!.getMember(this.usr).effectiveName
        } else {
            this.usr?.name
        }+"#${this.usr?.discriminator}"
    }

    val permgroups: PermGroupSet
        get() = PermGroupSet.find(this)

}