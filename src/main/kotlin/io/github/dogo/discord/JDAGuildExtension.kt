package io.github.dogo.discord

import io.github.dogo.core.Database
import io.github.dogo.security.PermGroupSet
import io.github.dogo.utils.BoundList
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Webhook
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
 *
 * @author NathanPB
 * @since 3.2.0
 */

private fun Guild.createColumn(){
    transaction {
        Database.GUILDS.run {
            slice(id).select { id eq this@createColumn.id }.count()
                .let {
                    if(it == 0){
                        insert { it[id] = this@createColumn.id }
                    }
                }
        }
    }
}

val Guild.prefixes
        get() = BoundList(
        { value ->
            transaction {
                Database.LOCALPREFIXES.insert {
                    it[guild] = id
                    it[prefix] = value
                }
            }
        },
        { value ->
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


val Guild.badwords
        get() = BoundList(
        { theword ->
            val t = this
            transaction {
                val query: SqlExpressionBuilder.() -> Op<Boolean> = { (Database.BADWORDS.word eq theword.toLowerCase()) and (Database.BADWORDS.guild eq id)}
                Database.BADWORDS.slice(Database.BADWORDS.id).select(query).also { result ->
                    if(result.count() == 0){
                        Database.BADWORDS.insert {
                            it[word] = theword.toLowerCase()
                            it[guild] = t.id
                        }
                    } else if(!result.first()[Database.BADWORDS.active]){
                        Database.BADWORDS.update(query){
                            it[active] = true
                        }
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

var Guild.newsWebhook: Webhook?
    get() = transaction {
        return@transaction (Database.GUILDS.slice(Database.GUILDS.defaultHook).select {
            Database.GUILDS.id eq this@newsWebhook.id
        }.first()[Database.GUILDS.defaultHook])?.let { hookId ->
            this@newsWebhook.webhooks.complete().firstOrNull { it.id == hookId }
        }
    }
    set(wh) {
        this.createColumn()
        transaction {
            Database.GUILDS.update({ Database.GUILDS.id eq id }){
                it[defaultHook] = wh?.id
            }
        }
    }

val Guild.permgroups: PermGroupSet
    get() = PermGroupSet.find(guild = this)