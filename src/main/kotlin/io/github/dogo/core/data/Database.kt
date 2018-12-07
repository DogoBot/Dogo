package io.github.dogo.core.data

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class Database {

    @Setting
    var HOST = "localhost"

    @Setting
    var PORT = 27017

    @Setting
    var NAME = "Dogo"

    @Setting
    var USER = "root"

    @Setting
    var PWD = "root"

}