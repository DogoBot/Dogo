package cf.nathanpb.dogo.core.eventBus

import cf.nathanpb.dogo.core.queue.DogoQueue
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.KTypeParameter

class EventBus(name : String) : DogoQueue(name){
    private val listeners = LinkedHashMap<Long, EventBus.EventListener>()



    fun submit(element : Any) {
        submit {
            for(listener in listeners){
                //todo continuar daqui
            }
        }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class Listener(val value : Int)

    private class EventListener (func : KFunction<Any>, bus : EventBus) {
        val func = func
        var id = 0L
        var priotity = 0

        init {
            do {
                id = Random().nextLong()
            } while (bus.listeners.containsKey(id))

            if(func.annotations.filter { a -> a.annotationClass.equals(Listener::class) }.isNotEmpty()){
                priotity = (func.annotations.filter { a -> a.annotationClass.equals(Listener::class) }.first() as Listener).value
            }
        }
    }
}