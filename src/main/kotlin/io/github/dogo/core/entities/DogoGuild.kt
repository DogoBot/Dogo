package io.github.dogo.core.entities

import io.github.dogo.core.Database
import io.github.dogo.core.DogoBot
import io.github.dogo.core.permissions.PermGroup
import io.github.dogo.core.permissions.PermGroupSet
import io.github.dogo.utils.BoundList
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Webhook
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class DogoGuild private constructor(val id : String){

    companion object {
        val cache = LinkedList<DogoGuild>()

        fun from(id: String) = cache.firstOrNull { it.id == id } ?: DogoGuild(id).also {
            it.g = DogoBot.jda?.getGuildById(id)
        }

        fun from(instance: Guild) = cache.firstOrNull { it.id == instance.id } ?: DogoGuild(instance.id).also {
            it.g = instance
        }
    }

    init {
        transaction {
            if(Database.GUILDS.selectAll().count() == 0) {
                Database.GUILDS.insert {
                    it[id] = this@DogoGuild.id
                }
            }
        }
        cache.add(this)
    }

    var  g: Guild? = null

    val prefixes = BoundList(
            {value ->
                transaction {
                    Database.LOCALPREFIXES.insert {
                        it[guild] = id
                        it[prefix] = value
                    }
                }
            },
            {value ->
                transaction {
                    Database.LOCALPREFIXES.deleteWhere {
                        (Database.LOCALPREFIXES.guild) eq id and (Database.LOCALPREFIXES.prefix eq value)
                    }
                }
            },
            {
                transaction {
                    return@transaction Database.LOCALPREFIXES.slice(Database.LOCALPREFIXES.prefix).select {
                        Database.LOCALPREFIXES.guild eq id
                    }.map { it[Database.LOCALPREFIXES.prefix] }
                }
            }
    )


    val badwords = BoundList(
            { theword ->
                transaction {
                    val query: SqlExpressionBuilder.() -> Op<Boolean> = { (Database.BADWORDS.word eq theword.toLowerCase()) and (Database.BADWORDS.guild eq id)}
                    Database.BADWORDS.slice(Database.BADWORDS.id).select(query).let { result ->
                        when {
                            result.count() == 0 -> {
                                Database.BADWORDS.insert {
                                    it[word] = theword.toLowerCase()
                                    it[guild] = this@DogoGuild.id
                                }
                            }
                            !result.first()[Database.BADWORDS.active] -> {
                                Database.BADWORDS.update(query){
                                    it[active] = true
                                }
                            }
                            else -> {}
                        }
                    }
                }
            },
            { theword ->
                transaction {
                    val query: SqlExpressionBuilder.() -> Op<Boolean> = { (Database.BADWORDS.word eq theword.toLowerCase()) and (Database.BADWORDS.guild eq id)}
                    Database.BADWORDS.slice(Database.BADWORDS.id).select(query).let { result ->
                        result.firstOrNull()?.let {
                            if(it[Database.BADWORDS.active]){
                                Database.BADWORDS.update(query){
                                    it[active] = false
                                }
                            }
                        }
                    }
                }
            },
            {
                transaction {
                    return@transaction Database.BADWORDS.slice(Database.BADWORDS.word).select {
                        (Database.BADWORDS.guild eq id) and (Database.BADWORDS.active eq true)
                    }.map { it[Database.BADWORDS.word] }
                }
            }
    )

    var newsWebhook: Webhook?
        get() = transaction {
            return@transaction (Database.GUILDS.slice(Database.GUILDS.defaultHook).select {
                Database.GUILDS.id.eq(id)
            }.first()[Database.GUILDS.defaultHook])?.let {hookId ->
                g?.webhooks?.complete()?.firstOrNull { it.id == hookId }
            }
        }
        set(wh) {
            transaction {
                Database.GUILDS.update({ Database.GUILDS.id eq id }){
                    it[Database.GUILDS.defaultHook] = wh?.id
                }
            }
        }

    val permgroups: PermGroupSet
        get() = PermGroupSet.find(guild = this)
}