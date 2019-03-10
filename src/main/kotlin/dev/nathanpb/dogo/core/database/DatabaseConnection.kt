package dev.nathanpb.dogo.core.database

import dev.nathanpb.dogo.core.DogoBot
import org.jetbrains.exposed.sql.Database

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
 * Holds MySQL connection and shit around it.
 *
 * @author NathanPB
 * @since 3.2.0
 */
class DatabaseConnection {
    companion object {
        /**
         * Holds DB connection
         */
        var db: Database? = null

        /**
         * Connects to DB
         */
        fun connect(host: String, port: Int, dbName: String, user: String, pwd: String){
            db = Database.connect(
                    "jdbc:mysql://$host:$port/$dbName",
                    driver = "com.mysql.jdbc.Driver",
                    user = user,
                    password = pwd
            )
        }

    }
}