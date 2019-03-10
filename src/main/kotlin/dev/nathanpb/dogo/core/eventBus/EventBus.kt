package dev.nathanpb.dogo.core.eventBus

import java.util.*
import java.util.concurrent.Executors
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

class EventBus {

    @SuppressWarnings("private")
    val executor = Executors.newSingleThreadExecutor()

    private val listeners = LinkedList<EventBus.EventListener>()

    fun submit(element : Any) {
        executor.execute {
            submitSync(element)
        }
    }

    @SuppressWarnings("private")
    fun submitSync(element : Any) {
        listeners
            .filter { it.func.javaMethod!!.parameterTypes.first().isAssignableFrom(element::class.java) }
            .forEach { it.func.call(element) }
    }

    fun register(vararg functions: KFunction<Any?>, priority: Int = 0) {
        functions.forEach {
            if(it.parameters.size == 1) {
                listeners += EventBus.EventListener(it, priority)
                listeners.sortBy { l -> l.priority }
            }
        }
    }

    fun unregister(vararg functions: KFunction<Any?>) {
        listeners.removeAll { functions.any { f -> f == it.func } }
    }

    private data class EventListener(
            val func : KFunction<Any?>,
            val priority: Int
    )
}