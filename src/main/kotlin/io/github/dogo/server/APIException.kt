package io.github.dogo.server

import io.ktor.http.HttpStatusCode

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
 * Thrown when something occurs bad on [API Server][io.github.dogo.server.APIServer]
 *
 * @param[httpCode] the http status code.
 * @param[message] describing the error occurred error.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class APIException(val httpCode: HttpStatusCode, override val message: String) : Exception(message)