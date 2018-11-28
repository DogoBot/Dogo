package cf.dogo.exceptions

import io.ktor.http.HttpStatusCode
import java.lang.Exception

class APIException(val httpCode: HttpStatusCode, override val message: String) : Exception(message)