package io.github.dogo.exceptions

import java.lang.Exception

class DiscordException(override val message: String) : Exception(message)