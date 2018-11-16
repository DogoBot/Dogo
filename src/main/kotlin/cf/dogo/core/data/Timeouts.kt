package cf.dogo.core.data

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class Timeouts {

    @Setting
    var GENERAL : Long = 60*1000

}