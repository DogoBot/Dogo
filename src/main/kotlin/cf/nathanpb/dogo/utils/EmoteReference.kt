package cf.nathanpb.dogo.utils

enum class EmoteReference(id : String, external : Boolean = false) {
    DOGOCPU("410953416475541508"),
    DOGORAM("410953417884827648"),
    DOGOHELP("453210353392680972"),
    NATHANBB("390267731846627329");

    val id : String
    val external = external
    init{
        this.id = id;
    }

    fun getName() : String {
        return name.toLowerCase()
    }

    fun getAsMention() : String {
        return if(external) {
            "<:${getName()}:$id>"
        } else {
            ":${getName()}:"
        }
    }
}