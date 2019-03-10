package dev.nathanpb.dogo.server

import dev.nathanpb.dogo.core.DogoBot
import io.ktor.http.HttpStatusCode
import org.json.JSONObject

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * The processors from routes in [APIServer].
 *
 * @param[data] the data to output as response body.
 * @param[run] the code to execute when the route is accessed.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class APIRequestProcessor(val data: JSONObject = JSONObject(), run: (JSONObject)->Unit) {
    /**
     * The HTTP Status Code
     */
    var code = HttpStatusCode.OK

    init {
        try {
            run(data)
        } catch (ex: APIException) {
            code = ex.httpCode
            data.put("message", ex.message)
        } catch (ex: Exception) {
            //todo report
            dev.nathanpb.dogo.core.DogoBot.logger.error("An error occurred while processing API Request", ex)
            code = HttpStatusCode.InternalServerError
            data.put("message", "unknown error")
        }
    }

    /**
     * Formats the output adding [*code*][code] and [*description*][HttpStatusCode.description].
     *
     * @return the response as JSON.
     */
    override fun toString(): String {
        return data.put("code", code.value).put("description", code.description).toString()
    }
}