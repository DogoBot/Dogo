package io.github.dogo.core

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

/**
 * Listens to JDA Event Bus and redirect it to Dogo [io.github.dogo.core.eventBus.EventBus]
 * @author NathanPB
 * @since 3.1.0
 */
class JDAListener : EventListener {

    /**
     * Listens to JDA Events and redirect it if the event is not null
     */
    override fun onEvent(event: Event?) {
        event?.let { DogoBot.eventBus.submit(it) }
    }
}