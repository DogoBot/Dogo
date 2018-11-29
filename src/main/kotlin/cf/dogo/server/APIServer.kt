package cf.dogo.server

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoGuild
import cf.dogo.core.entities.DogoUser
import cf.dogo.exceptions.APIException
import cf.dogo.server.token.Token
import cf.dogo.server.token.TokenFinder
import cf.dogo.utils.DiscordAPI
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.host
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject
import java.util.*

class APIServer {

    val tokensHash = mutableListOf<String>()

    val server = embeddedServer(Netty, port= DogoBot.data.API.PORT){
        routing {
            route(DogoBot.data.API.ROUTE){
                route("token"){
                    route("add"){
                        get("fromdiscord") {
                            var allow = true
                            val rand = Random().nextLong().let { if(it<0) it*(-1) else it}.toString()
                            val pro = APIRequestProcessor {
                                if(DogoBot.data.API.ALLOWED_TOKEN_ADD.contains("${call.request.host()}")) {
                                    tokensHash.add(rand)
                                } else {
                                    allow = false
                                    throw APIException(HttpStatusCode.Forbidden, "${call.request.host()} is not authorized to add tokens")
                                }
                            }
                            if(allow) {
                                call.respondText(
                                        APIServer::class.java.getResource("/assets/api/token-redirect.html")
                                                ?.readText()
                                                .orEmpty()
                                                .replace("%redirect%", "${DogoBot.data.API.ROUTE}token/add/")
                                                .replace("%rand%", rand),
                                        ContentType.Text.Html
                                )
                            } else call.respondText(pro.toString())
                        }
                        get {
                            APIRequestProcessor { data ->
                                if(tokensHash.contains(call.parameters["hash"].orEmpty())){
                                    tokensHash.remove(call.parameters["hash"].orEmpty())

                                    if(!call.parameters.contains("hash")) throw APIException(HttpStatusCode.BadRequest, "hash not provided")

                                    arrayOf("access_token", "token_type", "expires_in", "scope")
                                            .forEach {
                                                if(!call.parameters.contains(it)){
                                                    DogoBot.logger.error("Required argument $it not provided by ${call.request.host()}")
                                                    throw APIException(HttpStatusCode.BadRequest, "required argument $it not provided")
                                                }
                                            }

                                    if(TokenFinder().apply { token = call.parameters["access_token"].orEmpty() }.count() != 0L){
                                        throw APIException(HttpStatusCode.Conflict, "token already exists")
                                    }

                                    val fetch = DiscordAPI.fetchUser(call.parameters["access_token"].orEmpty(), call.parameters["token_type"].orEmpty())
                                    if(fetch.has("id")){
                                        Token(
                                                call.parameters["access_token"].orEmpty(),
                                                DogoUser(fetch.getString("id")),
                                                call.parameters["scope"].orEmpty().split(" ").toTypedArray(),
                                                Date(),
                                                Date(Date().time + (call.parameters["expires_in"].orEmpty().toLong() * 1000)),
                                                call.parameters["token_type"].orEmpty()
                                        ).also {
                                            it.update()
                                            it.export().let {
                                                it.keys.forEach { k -> data.put(k, it[k]) }
                                            }
                                        }
                                    } else {
                                        throw APIException(HttpStatusCode.Unauthorized, "token or token type is not valid")
                                    }
                                } else {
                                    throw APIException(HttpStatusCode.Forbidden, "${call.request.host()} is not authorized to add tokens")
                                }
                            }.also { call.respondText(it.toString()) }
                        }
                    }
                }
                route("user"){
                    route("{id}") {
                        get {
                            APIRequestProcessor {data ->
                                val user = DogoUser(call.parameters["id"].orEmpty())
                                val auth: Token? = getAuthorization(call,  "identify")

                                data.put("id", user.id)
                                user.usr?.let {
                                    data.put("username", it.name)
                                        .put("discriminator", it.discriminator)
                                        .put("avatar", it.avatarUrl)
                                }
                                auth?.let {
                                    DiscordAPI.fetchUser(it).let {
                                        it.keySet().forEach { k ->
                                            if(!data.has(k)) data.put(k, it[k])
                                        }
                                    }
                                    it.owner.usr?.mutualGuilds.orEmpty().let { from ->
                                        user.usr?.mutualGuilds?.filter { from.contains(it) }
                                                .orEmpty().map { it.id }
                                                .let { data.put("mutual_guilds", it) }
                                    }
                                }
                            }.let { call.respondText (it.toString()) }
                        }
                    }
                }
                route("guild"){
                    route("{id}") {
                        get {
                            APIRequestProcessor { data ->
                                val target = DogoGuild(call.parameters["id"].orEmpty())
                                val auth = getAuthorization(call, "guilds")

                                data.put("id", target.id)
                                target.g?.let {
                                    data.put("name", it.name)
                                    data.put("icon", it.iconUrl)
                                    data.put("invites", it.invites.complete().map { it.code }.toTypedArray())
                                    data.put("owner", it.ownerId)
                                    data.put("creationDate", it.creationTime.toInstant().toEpochMilli())
                                }
                            }.let{ call.respondText(it.toString()) }
                        }
                    }
                }
            }
        }
    }

    companion object {
        @Throws(APIException::class)
        fun getAuthorization(call: ApplicationCall, vararg validScopes: String) : Token? {
            return if(call.request.headers.contains("Authorization")){
                val auth = call.request.headers["Authorization"].orEmpty().split(" ")
                if(auth.size != 2) throw APIException(HttpStatusCode.BadRequest, "invalid authorization")

                TokenFinder().apply {
                    type = auth[0]
                    token = auth[1]
                }.find()?.let {
                    if(it.scopes.any { validScopes.contains(it) } && it.isValid()) it else null
                }
            } else null
        }
    }

}