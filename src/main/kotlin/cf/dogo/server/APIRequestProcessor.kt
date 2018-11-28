package cf.dogo.server

import cf.dogo.exceptions.APIException
import io.ktor.application.ApplicationCallPipeline
import io.ktor.http.HttpStatusCode
import org.json.JSONObject

class APIRequestProcessor( val data: JSONObject = JSONObject(), run: ()->Unit) {
    var code = HttpStatusCode.OK

    init {
        try {
            run()
        } catch (ex: APIException) {
            code = ex.httpCode
            data.put("message", ex.message)
        } catch (ex: Exception) {
            code = HttpStatusCode.InternalServerError
            data.put("message", "unknown error")
        }
    }

    override fun toString(): String {
        return data.put("code", code.value).put("description", code.description).toString()
    }
}