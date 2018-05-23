package cf.nathanpb.dogo.core.eventBus

import cf.nathanpb.dogo.core.DogoBot
import cf.nathanpb.dogo.core.queue.DogoQueue
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

class EventBus(name : String) : DogoQueue(name), EventListener {
    private val listeners = LinkedHashMap<Long, EventBus.EventListener>()

    fun submit(element : Any) {
        submit {
            submitSink(element)
        }
    }

    fun submitSink(element : Any) {
        for(listener in listeners){
            val parameter = listener.value.func.parameters.first()
            try{
                listener.value.func.call(listener.value.instance, element)
            }catch (ex : IllegalArgumentException) {}
        }
    }

    fun register(element: Any) : List<Long> {
        val funcs = element::class.functions
                .filter { m -> m.parameters.size == 2 }
                .filter { m -> m.annotations.any { a -> a.annotationClass.equals(EventBus.Listener::class) } }
        val ids = ArrayList<Long>()
        for(func in funcs){
            val el = EventBus.EventListener(func, this, element)
            listeners[el.id] = el
            ids.add(el.id)
        }
        return ids
    }

    fun unregister(vararg ids : Long) {
        for(id in ids){
            listeners.remove(id)
        }
    }

    fun unregister(instance : Any) {
        LinkedHashMap<Long, EventBus.EventListener>(listeners)
                .forEach {
                    t, u -> if(u.instance.equals(instance))  unregister(t)
                }
    }

    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.FUNCTION)
    annotation class Listener(val value : Int)

    private class EventListener constructor( func : KFunction<Any?>, bus : EventBus, instance : Any) {
        val func = func
        var id = 0L
        var priotity = 0
        val instance = instance

        init {
            do {
                id = Random().nextLong()
            } while (bus.listeners.containsKey(id))

            if(func.annotations.filter { a -> a.annotationClass.equals(Listener::class) }.isNotEmpty()){
                priotity = (func.annotations.filter { a -> a.annotationClass.equals(Listener::class) }.first() as Listener).value
            }
        }

        override fun toString(): String {
            return "EventListener@"+id
        }
    }

    override fun onEvent(event: Event?) {
        if(event != null) submit(event)
    }
}