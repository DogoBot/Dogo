package cf.nathanpb.dogo.core.cmdHandler

import cf.nathanpb.dogo.lang.LanguageEntry

enum class CommandCategory() {
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