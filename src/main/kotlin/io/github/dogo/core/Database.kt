package io.github.dogo.core

import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

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
 * Maps all the available tables on database.
 *
 * @author NathanPB
 * @since 1.0.0
 */
class Database {
    object USERS: Table() {
        val id = varchar("id", 32).primaryKey()
        val lang = varchar("lang", 5).default("en_US")
    }
    object GUILDS: Table() {
        val id = varchar("id", 32).primaryKey()
        val defaultHook = varchar("default_hook", 32).nullable()
    }
    object LOCALPREFIXES: Table() {
        val prefix = varchar("prefix", 32)
        val guild = varchar("guild", 32) references GUILDS.id
    }
    object BADWORDS: Table() {
        val id = integer("id").autoIncrement().primaryKey()
        val word = varchar("word", 256)
        val guild = varchar("guild", 32) references GUILDS.id
        val active = bool("active").default(true)
    }
    object BADWORDPUNISHMENT: Table() {
        val badword = integer("id") references BADWORDS.id
        val user = varchar("duser", 32) references USERS.id
        val date = datetime("date").default(DateTime.now())
    }
    object STATISTICS: Table() {
        val id = integer("id").autoIncrement().primaryKey()
        val date = datetime("date")
    }
    object TICTACTOESTATISTICS: Table() {
        val id = integer("id").primaryKey() references STATISTICS.id
        val table = varchar("thetable", 9)
    }
    object TTTPlayers: Table() {
        val statistic = integer("statistic") references TICTACTOESTATISTICS.id
        val user = varchar("duser", 32) references USERS.id
        val slot = bool("slot").default(false)
        val winner = bool("winner").default(true)
    }
    object PERMISSIONS: Table() {
        val guild = varchar("guild", 32).references(GUILDS.id).primaryKey().nullable()
        val role = varchar("role", 32).primaryKey().nullable()
        val permission = varchar("permission", 128)
        val type =  bool("type").default(false)
        val isDefault = bool("is_default").default(false)
    }
    object TOKENS: Table() {
        val token = varchar("token", 256).primaryKey()
        val user = varchar("duser", 32) references USERS.id
        val type = varchar("type", 16)
        val expiresIn = datetime("expires_in")
        val authTime = date("auth_time")
    }
    object TOKENSCOPES: Table() {
        val token = varchar("token", 256) references TOKENS.token
        val scope = varchar("scope", 32)
    }
}