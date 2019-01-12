package io.github.dogo.utils._static

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
 * Emotes from Discord.
 *
 * @param[id] the emote ID or name.
 * @param[external] must be true if the emote belongs to a guild (optional).
 * @param[equivalentChar] some equivalent char to this emote (optional).
 *
 * @author NathanPB
 * @since 3.1.0
 */
enum class EmoteReference(val id : String, val external : Boolean = false, val equivalentChar: Char? = '?') {

    /*
        EXTERNAL
     */
    DOGOCPU("410953416475541508", true),
    DOGORAM("410953417884827648", true),
    DOGOHELP("453210353392680972", true),
    NATHANBB("390267731846627329", true),

    /*
       REGIONAL
    */
    ZERO("0⃣", equivalentChar = '0'),
    ONE("1⃣", equivalentChar = '1'),
    TWO("2⃣", equivalentChar = '2'),
    THREE("3⃣", equivalentChar = '3'),
    FOUR("4⃣", equivalentChar = '4'),
    FIVE("5⃣", equivalentChar = '5'),
    SIX("6⃣", equivalentChar = '6'),
    SEVEN("7⃣", equivalentChar = '7'),
    EIGHT("8⃣", equivalentChar = '8'),
    NINE("9⃣", equivalentChar = '9'),

    REGIONAL_INDICATOR_A("\uD83C\uDDE6"),
    REGIONAL_INDICATOR_B("\uD83C\uDDE7"),
    REGIONAL_INDICATOR_C("\uD83C\uDDE8"),
    REGIONAL_INDICATOR_D("\uD83C\uDDE9"),
    REGIONAL_INDICATOR_E("\uD83C\uDDEB"),
    REGIONAL_INDICATOR_F("\uD83C\uDDEA"),
    REGIONAL_INDICATOR_G("\uD83C\uDDEC"),
    REGIONAL_INDICATOR_H("\uD83C\uDDED"),
    REGIONAL_INDICATOR_I("\uD83C\uDDEE"),
    REGIONAL_INDICATOR_J("\uD83C\uDDEF"),
    REGIONAL_INDICATOR_K("\uD83C\uDDF0"),
    REGIONAL_INDICATOR_L("\uD83C\uDDF1"),
    REGIONAL_INDICATOR_M("\uD83C\uDDF2"),
    REGIONAL_INDICATOR_N("\uD83C\uDDF3"),
    REGIONAL_INDICATOR_O("\uD83C\uDDF4"),
    REGIONAL_INDICATOR_P("\uD83C\uDDF5"),
    REGIONAL_INDICATOR_Q("\uD83C\uDDF6"),
    REGIONAL_INDICATOR_R("\uD83C\uDDF7"),
    REGIONAL_INDICATOR_S("\uD83C\uDDF8"),
    REGIONAL_INDICATOR_T("\uD83C\uDDF9"),
    REGIONAL_INDICATOR_U("\uD83C\uDDFA"),
    REGIONAL_INDICATOR_V("\uD83C\uDDFB"),
    REGIONAL_INDICATOR_W("\uD83C\uDDFC"),
    REGIONAL_INDICATOR_X("\uD83C\uDDFD"),
    REGIONAL_INDICATOR_Y("\uD83C\uDDFE"),
    REGIONAL_INDICATOR_Z("\uD83C\uDDFF"),
    REPEAT_ONE("\uD83D\uDD02"),

    /*
        SYMBOLS
     */

    WHITE_CHECK_MARK("✅"),
    NEGATIVE_SQUARED_CROSS_MARK("❎"),
    OCTAGONAL_SIGN("\uD83D\uDED1"),

    ARROW_UP("⬆"),
    ARROW_DOUBLE_UP("⏫"),

    ARROW_DOWN("⬇"),
    ARROW_DOUBLE_DOWN("⏬"),

    ARROW_FORWARD("▶"),
    ARROW_BACKWARD("◀"),

    TRACK_PREVIOUS("⏮"),
    TRACK_NEXT("⏭"),

    ARROW_COUNTERCLOCKWISE("\uD83D\uDD04"),

    WARNING("⚠"),

    OK_HAND("\uD83D\uDC4C"),
    NEW("\uD83C\uDD95"),
    RECYCLE("♻"),

    X("❌"),
    O("⭕"),

    /*
        OBJECTS
     */
    KEYBOARD("⌨"),

    MONEYBAG("\uD83D\uDCB0"),
    NEWSPAPER("\uD83D\uDCF0"),
    BOOK("\uD83D\uDCD6"),
    PAGE_WITH_CURL("\uD83D\uDCC3"),
    MILK("\uD83E\uDD5B"),

    TADA("\uD83C\uDF89"),
    HEART("❤"),
    PAGER("\uD83D\uDCDF"),

    /*
        FACES
     */
    WORRIED("\uD83D\uDE1F"),
    NEUTRAL_FACE("\uD83D\uDE10"),
    RAGE("\uD83D\uDE21");


    /**
     * @return the [EmoteReference] name, as declared in the enum, but in lowercase.
     */
    fun getName() : String {
        return name.toLowerCase()
    }

    /**
     * @return the emote as mention, ready to be printed on a [net.dv8tion.jda.core.entities.TextChannel]
     */
    fun getAsMention() : String {
        return if(external) {
            "<:${getName()}:$id>"
        } else {
            ":${getName()}:"
        }
    }

    companion object {
        /**
         * Gets the equivalent [EmoteReference] for a [Char]
         */
        fun getRegional(char: Char) = EmoteReference.values().firstOrNull { it.equivalentChar == char } ?: DOGOHELP
    }
}