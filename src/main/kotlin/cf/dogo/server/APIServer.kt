package cf.dogo.server

import cf.dogo.core.DogoBot
import cf.dogo.server.bridge.Server
import cf.dogo.server.processors.ProcessorAddToken
import cf.dogo.server.processors.ProcessorGuildMultiple
import cf.dogo.server.processors.ProcessorGuildSingle
import org.json.JSONObject
import java.net.Socket

class APIServer : Server(4676, "API Server"){
    val tokens = ArrayList<Token>()
    val processors = HashMap<String, APIRequestProcessor>()

    init {
        processors["addtoken"] = ProcessorAddToken()
        processors["guild"] = ProcessorGuildSingle()
        processors["guilds"] = ProcessorGuildMultiple()
        //processors["miner"] = ProcessorAuthMiner()
    }

    override fun onRequest(reqid: Int, data: JSONObject, sck: Socket) : JSONObject {
        return if(!data.has("iwant")){
            JSONObject().put("error", "missing instruction").put("status", 422)
        } else if(!processors.containsKey(data.getString("iwant"))){
            JSONObject().put("error", "missing implementation").put("status", 503)
        } else {
            try {
                val tk : Token? = if(data.has("token")) {
                    if(tokens.filter { it.token.equals(data.getString("token")) }.isEmpty()){
                        addToken(data.getString("token"))
                    }
                    tokens.filter { it.token.equals(data.getString("token")) }.firstOrNull()
                } else {
                    null
                }
                processors[data.getString("iwant")]?.proccess(RequestResponse(data, reqid), sck, tk, this) as JSONObject
            }catch (ex : Exception) {
                //TODO report when reporters are done
                DogoBot.logger?.error("An error occurs while processing Bridge request: ${ex.message}")
                return JSONObject().put("error", "unknown error").put("status", 500)
            }
        }
    }

    fun addToken(token : String){
        val t = Token(token)
        if(t.isValid()) tokens.add(t)
        checkTokens()
    }

    fun removeToken(token: String){
        tokens.removeAll(tokens.filter { t -> t.token.equals(token) })
        checkTokens()
    }

    fun hasToken(token: String) : Boolean{
        checkTokens()
        return tokens.any { t -> t.token.equals(token) }
    }

    fun checkTokens(){
        tokens.removeAll(tokens.filter { t -> !t.isValid() })
    }






}