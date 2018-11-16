package cf.dogo.core

import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.hooks.EventListener

class JDAListener : EventListener{
    override fun onEvent(event: Event?) {
        event?.let { DogoBot.eventBus.submit(it) }
    }
}