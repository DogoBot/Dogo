package io.github.dogo.core.permissions

import io.github.dogo.core.Database
import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.permissions.mapper.PermissionNode
import io.github.dogo.utils.BoundList
import net.dv8tion.jda.core.entities.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.regex.Pattern

/**
 * Additional permissions bound to Discord Roles.
 *
 * @param[role] the role. Null means the permgroup belongs to the guild owner
 * @param[guild] the guild. Null means the permgroup is applied to every guild.
 */
open class PermGroup(val role: Role?, val guild: DogoGuild?) {

    companion object {

        //todo this shit is hardcoded and it should not. Default permgroups must be configurable at runtime

        /**
         * Permgroup applied to all the guild owners.
         */
        val GUILD_OWNER = TransientPermGroup(null, null).apply {
            include += "*.guildonwer"
        }

        /**
         * Default permgroup, applied everywhere, but overridable on guild levels
         */
        val DEFAULT = TransientPermGroup(null, null).apply {
            include += "*"
            exclude += "*.admin"
            exclude += "*.guildowner"
            isDefault = true
        }

        /**
         * Roles already cached to speed up the searching.
         */
        private val cached = mutableListOf<PermGroup>()

        /**
         * Searches a permgroup on cache, creates a new one if doesn't exists.
         */
        fun from(role: Role?, guild: DogoGuild?) = cached.firstOrNull { it.role?.id == role?.id && guild?.id == guild?.id } ?: PermGroup(role, guild).also { cached += it }
    }

    /**
     * If a permgroup is the default one.
     */
    open var isDefault: Boolean
        get() = transaction {
            Database.PERMISSIONS.run {
                slice(Database.PERMISSIONS.isDefault)
                    .select {
                        (guild eq this@PermGroup.guild?.id) and (role eq this@PermGroup.role?.id)
                    }.firstOrNull()?.get(Database.PERMISSIONS.isDefault) ?: false
            }
        }
        set(value) = transaction {
            Database.PERMISSIONS.run {
                update({ (guild eq this@PermGroup.guild?.id) and (role eq this@PermGroup.role?.id)}){
                    it[isDefault] = value
                }
            }
        }

    /**
     * All the included permissions from a role.
     */
    open val include: MutableList<String> = BoundList(
            { permname ->
                transaction {
                    Database.PERMISSIONS.run {
                        insert {
                            it[guild] = this@PermGroup.guild?.id
                            it[role] = this@PermGroup.role?.id
                            it[type] = true
                            it[permission] = permname
                        }
                    }
                }
            },
            { permname ->
                transaction {
                    Database.PERMISSIONS.run {
                        deleteWhere {
                            (guild eq this@PermGroup.guild?.id) and
                            (role eq this@PermGroup.role?.id) and
                            (type eq true) and
                            (permission eq permname)
                        }
                    }
                }
            },
            {
                transaction {
                    return@transaction Database.PERMISSIONS.run {
                        slice(permission)
                        .select {
                            (guild eq this@PermGroup.guild?.id) and
                            (role eq this@PermGroup.role?.id) and
                            (type eq true)
                        }.map { it[permission] }
                    }
                }
            }
    )

    /**
     * All the excluded permissions from a role.
     */
    open val exclude: MutableList<String> = BoundList(
            { permname ->
                transaction {
                    Database.PERMISSIONS.run {
                        insert {
                            it[guild] = this@PermGroup.guild?.id
                            it[role] = this@PermGroup.role?.id
                            it[type] = false
                            it[permission] = permname
                        }
                    }
                }
            },
            { permname ->
                transaction {
                    Database.PERMISSIONS.run {
                        deleteWhere {
                            (guild eq this@PermGroup.guild?.id) and
                            (role eq this@PermGroup.role?.id) and
                            (permission eq permname) and
                            (type eq false)
                        }
                    }
                }
            },
            {
                transaction {
                    return@transaction Database.PERMISSIONS.run {
                        slice(permission)
                                .select {
                                    (guild eq this@PermGroup.guild?.id) and
                                    (role eq this@PermGroup.role?.id) and
                                    (type eq false)
                                }.map { it[permission] }
                    }
                }
            }
    )

    /**
     * Checks if the permgroup has a included permission.
     */
    fun hasIncluded(perm: String) : Boolean {
        for(s in include){
            if(PermissionNode("fakenode").apply {
                children.addAll(DogoBot.permissionManager.permissions.findFamily(s))
            }.findFamily(perm).containsAll(DogoBot.permissionManager.permissions.findFamily(s))) return true
        }
        return false
    }

    /**
     * Checks if the permgroup has a excluded permission.
     */
    fun hasExcluded(perm : String) : Boolean {
        for(s in exclude){
            if(PermissionNode("fakenode").apply {
                children.addAll(DogoBot.permissionManager.permissions.findFamily(s))
            }.findFamily(perm).containsAll(DogoBot.permissionManager.permissions.findFamily(s))) return true
        }
        return false
    }

    /**
     * Gets the PermGroup designation.
     */
    val designation
        get() = when {
        isDefault || this == DEFAULT                                     -> Designations.DEFAULT
        guild == null && role != null                                    -> Designations.ADMINS
        (guild == null && role == null) || this == PermGroup.GUILD_OWNER -> Designations.GUILD_OWNER
        guild != null && role != null                                    -> Designations.GUILD_LOCAL
        else                                                             -> Designations.INVALID
    }

    override fun toString() = "{role:${role?.id} | guild:${guild?.id} | include:$include | exclude:$exclude}"
}