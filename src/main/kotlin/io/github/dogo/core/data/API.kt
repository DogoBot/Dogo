package io.github.dogo.core.data

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
class API {

    @Setting
    val PORT = 4676

    @Setting
    val ROUTE = "/api/"

    @Setting
    val ALLOWED_TOKEN_ADD = listOf("discordapp.com")
}