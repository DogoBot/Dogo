package cf.dogo.server

import cf.dogo.core.DogoBot
import cf.dogo.core.entities.DogoUser
import cf.dogo.exceptions.APIException
import cf.dogo.server.token.Token
import cf.dogo.utils.DiscordAPI
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.host
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.json.JSONObject
import java.util.*

class APIServer {

    val tokensHash = mutableListOf<String>()

    val ROUTE = DogoBot.data.API.ROUTE

    val server = embeddedServer(Netty, port= DogoBot.data.API.PORT){
        routing {
            get("$ROUTE/token/add/fromdiscord") {
                var allow = true
                val rand = Random().nextLong().let { if(it<0) it*(-1) else it}.toString()
                APIRequestProcessor {
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
                                    .replace("%redirect%", "${ROUTE}token/add/")
                                    .replace("%rand%", rand),
                            ContentType.Text.Html
                    )
                }
            }
            get("$ROUTE/token/add/"){
                val data = JSONObject()
                APIRequestProcessor(data) {
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
                        val fetch = DiscordAPI.fetchUser(call.parameters["access_token"].orEmpty(), call.parameters["token_type"].orEmpty())
                        if(fetch.has("id")){
                            Token(
                                    call.parameters["access_token"].orEmpty(),
                                    DogoUser(fetch.getString("id")),
                                    call.parameters["scope"].orEmpty().split(" ").toTypedArray(),
                                    Date(),
                                    Date(Date().time + call.parameters["expires_in"].orEmpty().toLong()),
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
}