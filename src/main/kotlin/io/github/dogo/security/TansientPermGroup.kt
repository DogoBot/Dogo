package io.github.dogo.security

import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Role

/**
 * A permgroup that is not stored on database. Used to define GUILD_OWNER and DEFAULT ones.
 */
class TransientPermGroup(role: Role?, guild: Guild?) : PermGroup(role, guild) {
    override val include = mutableListOf<String>()
    override val exclude = mutableListOf<String>()
    override var isDefault = false
}