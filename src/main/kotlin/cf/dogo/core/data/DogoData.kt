package cf.dogo.core.data

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class DogoData {

    @Setting
    var BOT_TOKEN = "BOT_TOKEN"

    @Setting
    var OWNER_ID = "214173547965186048"

    @Setting
    val COMMAND_PREFIX = mutableListOf("dg!")

    @Setting
    var DEBUG_PROFILE = false

    @Setting
    var LOGGER_PATH = "logs"

    @Setting
    val TIMEOUTS = Timeouts()

    @Setting
    val DB = Database()

    @Setting
    val API = API()

}