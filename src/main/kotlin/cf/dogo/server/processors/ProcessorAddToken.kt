package cf.dogo.server.processors

import cf.dogo.server.APIRequestProcessor
import cf.dogo.server.APIServer
import cf.dogo.server.RequestResponse
import cf.dogo.server.Token
import java.net.Socket

class ProcessorAddToken : APIRequestProcessor{
    override fun proccess(data : RequestResponse, sck: Socket, tk: Token?, api : APIServer): RequestResponse {
        if(tk != null && tk.getPermGroups().can("web.token.add")) {
            val body = data.getJSONObject("body")
            if(!api.hasToken(body.getString("authtoken"))){
                api.addToken(body.getString("authtoken"))
            }
            return RequestResponse()
        }
        return RequestResponse().setDesc("permission required: 'web.token.add'", 403)
    }
}