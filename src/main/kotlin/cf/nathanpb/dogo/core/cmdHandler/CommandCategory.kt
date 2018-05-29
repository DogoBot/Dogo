package cf.nathanpb.dogo.core.cmdHandler

enum class CommandCategory(display : String) {
    UTILITY(":hammer_pick: Utility"),
    FUN(":video_game: Fun"),
    BOT(":desktop: Bot"),
    USER(":bust_in_silhouette: User"),
    MUSIC(":musical_keyboard: Music"),
    GUILD_ADMINISTRATION(":newspaper: Guild Administration"),
    NSFW(":ok_hand: NSFW"),
    MINIGAMES(":black_joker: Minigames");
    val display = display
}