package io.github.dogo.core.data

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

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
 * Dogo configurations.
 *
 * @author NathanPB
 * @since 3.1.0
 */
@ConfigSerializable
class DogoData {

    /**
     * The bot token, from Discord.
     */
    @Setting
    var BOT_TOKEN = "BOT_TOKEN"

    /**
     * The list of acceptable command prefixes.
     */
    @Setting
    val COMMAND_PREFIX = mutableListOf("dg!")

    /**
     * Are you debugging?
     */
    @Setting
    var DEBUG_PROFILE = false

    /**
     * Path to save the logs.
     */
    @Setting
    var LOGGER_PATH = "logs"

    /**
     * Timeouts configurations.
     * @see [Timeouts]
     */
    @Setting
    val TIMEOUTS = Timeouts()

    /**
     * Tables configurations.
     * @see [Database]
     */
    @Setting
    val DB = Database()

    /**
     * API configurations.
     * @see [API]
     */
    @Setting
    val API = API()

    /**
     * Jenkins.
     * @see [Jenkins]
     */
    @Setting
    val JENKINS = Jenkins()

    /**
     * Dumps logging.
     */
    @Setting
    val DUMPS = DumpLog()

}