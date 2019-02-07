package io.github.dogo.core.eventBus

import io.github.dogo.core.DogoBot
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

class EventBus {
    private val listeners = LinkedList<EventBus.EventListener>()

    fun submit(element : Any) {
        DogoBot.eventBusThread.execute {
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