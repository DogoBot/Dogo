package io.github.dogo.core

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

class JDAListener : EventListener{
    override fun onEvent(event: Event?) {
        event?.let { io.github.dogo.core.DogoBot.Companion.eventBus.submit(it) }
    }
}