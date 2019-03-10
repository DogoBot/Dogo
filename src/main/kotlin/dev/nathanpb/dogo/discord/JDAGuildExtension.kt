package dev.nathanpb.dogo.discord

import dev.nathanpb.dogo.core.database.Tables
import dev.nathanpb.dogo.security.PermGroupSet
import dev.nathanpb.dogo.utils.BoundList
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
        Tables.GUILDS.run {
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
                Tables.LOCALPREFIXES.insert {
                    it[guild] = id
                    it[prefix] = value
                }
            }
        },
        { value ->
            transaction {
                Tables.LOCALPREFIXES.deleteWhere {
                    (Tables.LOCALPREFIXES.guild) eq id and (Tables.LOCALPREFIXES.prefix eq value)
                }
            }
        },
        {
            transaction {
                return@transaction Tables.LOCALPREFIXES.slice(Tables.LOCALPREFIXES.prefix).select {
                    Tables.LOCALPREFIXES.guild eq id
                }.map { it[Tables.LOCALPREFIXES.prefix] }
            }
        }
)


val Guild.badwords
        get() = BoundList(
        { theword ->
            val t = this
            transaction {
                val query: SqlExpressionBuilder.() -> Op<Boolean> = { (Tables.BADWORDS.word eq theword.toLowerCase()) and (Tables.BADWORDS.guild eq id)}
                Tables.BADWORDS.slice(Tables.BADWORDS.id).select(query).also { result ->
                    if(result.count() == 0){
                        Tables.BADWORDS.insert {
                            it[word] = theword.toLowerCase()
                            it[guild] = t.id
                        }
                    } else if(!result.first()[Tables.BADWORDS.active]){
                        Tables.BADWORDS.update(query){
                            it[active] = true
                        }
                    }
                }
            }
        },
        { theword ->
            transaction {
                val query: SqlExpressionBuilder.() -> Op<Boolean> = { (Tables.BADWORDS.word eq theword.toLowerCase()) and (Tables.BADWORDS.guild eq id)}
                Tables.BADWORDS.slice(Tables.BADWORDS.id).select(query).let { result ->
                    result.firstOrNull()?.let {
                        if(it[Tables.BADWORDS.active]){
                            Tables.BADWORDS.update(query){
                                it[active] = false
                            }
                        }
                    }
                }
            }
        },
        {
            transaction {
                return@transaction Tables.BADWORDS.slice(Tables.BADWORDS.word).select {
                    (Tables.BADWORDS.guild eq id) and (Tables.BADWORDS.active eq true)
                }.map { it[Tables.BADWORDS.word] }
            }
        }
)

var Guild.newsWebhook: Webhook?
    get() = transaction {
        return@transaction (Tables.GUILDS.slice(Tables.GUILDS.defaultHook).select {
            Tables.GUILDS.id eq this@newsWebhook.id
        }.first()[Tables.GUILDS.defaultHook])?.let { hookId ->
            this@newsWebhook.webhooks.complete().firstOrNull { it.id == hookId }
        }
    }
    set(wh) {
        this.createColumn()
        transaction {
            Tables.GUILDS.update({ Tables.GUILDS.id eq id }){
                it[defaultHook] = wh?.id
            }
        }
    }

val Guild.permgroups: PermGroupSet
    get() = PermGroupSet.find(guild = this)