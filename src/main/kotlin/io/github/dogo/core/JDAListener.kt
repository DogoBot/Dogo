package io.github.dogo.core

import io.github.dogo.core.eventBus.EventBus
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

/**
 * Listens to JDA Event Bus and redirect it to Dogo [io.github.dogo.core.eventBus.EventBus]
 *
 * @param[bus] the bus to redirect the events.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class JDAListener(val bus: EventBus) : EventListener {

    /**
     * Listens to JDA Events and redirect it if the event is not null
     */
    override fun onEvent(event: Event?) {
        if(event != null && DogoBot.ready) bus.submit(event)
    }
}