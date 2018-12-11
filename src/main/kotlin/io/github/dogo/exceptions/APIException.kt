package io.github.dogo.exceptions

import io.ktor.http.HttpStatusCode

class APIException(val httpCode: HttpStatusCode, override val message: String) : Exception(message)