package cf.dogo.core.cmdHandler

import cf.dogo.lang.LanguageEntry


enum class CommandCategory {
    UTILITY,
    FUN,
    BOT,
    USER,
    MUSIC,
    GUILD_ADMINISTRATION,
    NSFW,
    MINIGAMES;

    fun getDisplay(lang : String) : String {
        return LanguageEntry("cmdcategory").getText(lang, name.toLowerCase())
    }
}