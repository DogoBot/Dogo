package io.github.dogo.core.command

import io.github.dogo.lang.LanguageEntry


enum class CommandCategory {
    UTILITY,
    FUN,
    BOT,
    USER,
    MUSIC,
    GUILD_ADMINISTRATION,
    NSFW,
    MINIGAMES,
    HIDDEN,
    OWNER;

    fun getDisplay(lang : String) : String {
        return LanguageEntry("cmdcategory").getTextIn(lang, name.toLowerCase())
    }
}