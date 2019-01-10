package io.github.dogo.core.eventBus

import io.github.dogo.core.DogoBot
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

class EventBus {
    private val listeners = LinkedList<EventBus.EventListener>()

    fun submit(element : Any) {
        DogoBot.eventBusThread.execute {
            submitSync(element)
        }
    }

    fun submitSync(element : Any) {
        listeners
            .filter { it.func.parameters.first().type.isSubtypeOf(element::class.createType()) }
            .forEach { it.func.call(element) }
    }

    fun register(vararg functions: KFunction<Any?>, priority: Int = 0) {
        functions.forEach {
            if(it.parameters.size == 1) {
                listeners += EventBus.EventListener(it, this, priority)
                listeners.sortBy { it.priority }
            }
        }
    }

    fun unregister(vararg functions: KFunction<Any?>) {
        listeners.removeAll { functions.any { f -> f == it.func } }
    }

    private data class EventListener(
            val func : KFunction<Any?>,
            val bus : EventBus,
            val priority: Int
    ) {
        override fun toString(): String {
            return "EventBus.Listener@${func.hashCode()}"
        }
    }
}