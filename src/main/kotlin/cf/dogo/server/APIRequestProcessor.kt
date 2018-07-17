package cf.dogo.server

import java.net.Socket

interface APIRequestProcessor {
    fun proccess(data : RequestResponse, sck : Socket, tk : Token?, api : APIServer) : RequestResponse
}