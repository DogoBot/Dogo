package io.github.dogo.server

import io.github.dogo.core.DogoBot
import io.github.dogo.exceptions.APIException
import io.ktor.application.ApplicationCallPipeline
import io.ktor.http.HttpStatusCode
import org.json.JSONObject

class APIRequestProcessor( val data: JSONObject = JSONObject(), run: (JSONObject)->Unit) {
    var code = HttpStatusCode.OK

    init {
        try {
            run(data)
        } catch (ex: APIException) {
            code = ex.httpCode
            data.put("message", ex.message)
        } catch (ex: Exception) {
            //todo report
            DogoBot.logger.error("An error occurred while processing API Request", ex)
            code = HttpStatusCode.InternalServerError
            data.put("message", "unknown error")
        }
    }

    override fun toString(): String {
        return data.put("code", code.value).put("description", code.description).toString()
    }
}