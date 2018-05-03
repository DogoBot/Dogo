package cf.nathanpb.dogo.lang

import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.collections.HashMap

class LanguageEntry constructor(registry : String){
    private val registry = registry
    private val default =LanguageEntry::class.java.getResource("/assets/lang/en_US.lang").readText().toLang()


    fun getText(lang : String, entry : String) : String{
        var text = LanguageEntry::class.java.getResource("/assets/lang/${lang.split("_")[0].toLowerCase()}_${lang.split("_")[1].toUpperCase()}.lang")?.readText()?.toLang()
        if(text == null){
           text = default
        }
        if(text.containsKey(entry)){
            return text.get(entry) as String
        } else {
            return "NO_TEXT"
        }
    }

    /*
    extension shit
     */
    private fun String.toLang() : HashMap<String, String> {
        val hm = HashMap<String, String>()
        if(this != null) {
            val array = ConcurrentLinkedDeque<String>(this.split("="))
            while (array.size >= 2) {
                hm.put(array.poll(), array.poll())
            }
        }
        return hm
    }
}
