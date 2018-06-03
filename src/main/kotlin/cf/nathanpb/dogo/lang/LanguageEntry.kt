package cf.nathanpb.dogo.lang

import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.collections.HashMap

class LanguageEntry constructor(registry : String){
    private val registry = registry
    private val default =LanguageEntry::class.java.getResource("/assets/lang/en_US.lang").readText().toLang()


    fun getText(lang : String, entry : String, vararg args : Any) : String{
        var text = LanguageEntry::class.java.getResource("/assets/lang/${lang.split("_")[0].toLowerCase()}_${lang.split("_")[1].toUpperCase()}.lang")?.readText()?.toLang()
        if(text == null){
           text = default
        }
        if(text.containsKey("$registry.$entry")){
            return String.format(text["$registry.$entry"] as String, *args).replace("\\n", "\n")
        } else {
            return "$registry.$entry"
        }
    }

    /*
    extension shit
     */
    private fun String.toLang() : HashMap<String, String> {
        val hm = HashMap<String, String>()
        if(this != null) {

            var array = ConcurrentLinkedDeque<String>(this.split("=", "\n"))
            array = ConcurrentLinkedDeque(array.filter { t -> t.isNotEmpty() && !t.equals("\r") && !t.startsWith("#")}.map { t -> t.replace("\r", "") })
            while (array.size >= 2) {
                hm[array.poll()] = array.poll()
            }
        }
        return hm
    }
}
