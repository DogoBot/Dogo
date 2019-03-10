package dev.nathanpb.dogo.discord

import dev.nathanpb.dogo.core.database.Tables
import dev.nathanpb.dogo.security.PermGroupSet
import dev.nathanpb.dogo.utils._static.DiscordAPI
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.json.JSONObject

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

private fun User.createColumn(){
    transaction {
        Tables.USERS.run {
            slice(id).select { id eq this@createColumn.id }.count()
                    .let {
                        if(it == 0){
                            insert { it[id] = this@createColumn.id }
                        }
                    }
        }
    }
}

var User.lang : String
    get() = transaction {
        createColumn()
        return@transaction Tables.USERS.run {
            slice(lang).select { id eq this@lang.id }.first()[lang]
        }
    }
    set(newLang) = createColumn().also {
        transaction {
            Tables.USERS.run {
                Tables.USERS.update({ id eq this@lang.id }){
                    it[lang] = newLang
                }
            }
        }
    }

fun User.fetchUser() : JSONObject {
    return transaction {
        Tables.TOKENS.run {
            (this innerJoin Tables.TOKENCOPES)
                    .slice(token, type)
                    .select {
                        (user eq this@fetchUser.id) and
                                (expiresIn greater DateTime.now()) and
                                (Tables.TOKENCOPES.token inList(listOf("email", "identify")))
                    }.firstOrNull()
                    ?.let { DiscordAPI.fetchUser(it[token], it[type])} ?: throw DiscordException("Invalid or Unknown Token")
        }
    }
}

fun User.formatName(g: Guild? = null) : String {
    return "${g?.getMember(this)?.effectiveName ?: this.name}#${this@formatName.discriminator}"
}

val User.permgroups: PermGroupSet
    get() = PermGroupSet.find(this@permgroups)