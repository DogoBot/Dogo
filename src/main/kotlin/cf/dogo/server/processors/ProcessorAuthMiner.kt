package cf.dogo.server.processors

import cf.dogo.server.APIRequestProcessor
import cf.dogo.server.APIServer
import cf.dogo.server.RequestResponse
import cf.dogo.server.Token
import java.net.Socket
import java.util.*

class ProcessorAuthMiner : APIRequestProcessor{

    override fun proccess(data: RequestResponse, sck: Socket, tk: Token?, api: APIServer): RequestResponse {
        val resp = RequestResponse()
        if(data.has("user")){
            val rand = Random().nextLong();
            resp
        } else {
            resp.setDesc("user not provided", 422)
        }
        return resp
    }
}