package io.github.dogo.core.permissions

import io.github.dogo.core.entities.DogoGuild
import net.dv8tion.jda.core.entities.Role

/**
 * A permgroup that is not stored on database. Used to define GUILD_OWNER and DEFAULT ones.
 */
class TransientPermGroup(role: Role?, guild: DogoGuild?) : PermGroup(role, guild) {
    override val include = mutableListOf<String>()
    override val exclude = mutableListOf<String>()
    override var isDefault = false
}