package cf.dogo.lang

import java.util.concurrent.ConcurrentLinkedDeque

class LanguageEntry constructor(val registry : String){
    private val default = LanguageEntry::class.java.getResource("/assets/lang/en_US.lang").readText().toLang()


    fun getText(lang : String, entry : String, vararg args : Any) : String{
        var text = LanguageEntry::class.java.getResource("/assets/lang/${lang.split("_")[0].toLowerCase()}_${lang.split("_")[1].toUpperCase()}.lang")?.readText()?.toLang()
        if(text == null){
           text = default
        }
        return if(text.containsKey("$registry.$entry")){
            java.lang.String.format(text["$registry.$entry"] as String, *args).replace("\\n", "\n")
        } else {
            "$registry.$entry"
        }
    }

    /*
    extension shit
     */
    private fun String.toLang() : HashMap<String, String> {
        val hm = HashMap<String, String>()
        var array = ConcurrentLinkedDeque<String>(this.split("=", "\n"))
        array = ConcurrentLinkedDeque(array.filter { t -> t.isNotEmpty() && !t.equals("\r") && !t.startsWith("#")}.map { t -> t.replace("\r", "") })
        while (array.size >= 2) {
            hm[array.poll()] = array.poll()
        }
        return hm
    }
}
