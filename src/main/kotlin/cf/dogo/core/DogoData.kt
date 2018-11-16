package cf.dogo.core

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class DogoData {

    @Setting
    val COMMAND_PREFIX = mutableListOf("dg!")

    @Setting
    var BOT_TOKEN = "BOT_TOKEN"

    @Setting
    var OWNER_ID = "214173547965186048"

    @Setting
    var DB_HOST = "localhost"

    @Setting
    var DB_PORT = 27017

    @Setting
    var DB_NAME = "Dogo"

    @Setting
    var DB_USER = "root"

    @Setting
    var DB_PWD = "root"

    @Setting
    var DEBUG_PROFILE = false

    @Setting
    var LOGGER_PATH = "logs"


}