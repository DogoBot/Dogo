package io.github.dogo.core.permissions

import io.github.dogo.core.Database
import io.github.dogo.core.DogoBot
import io.github.dogo.core.entities.DogoGuild
import io.github.dogo.core.entities.DogoUser
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A list of [PermGroup]s.
 *
 * @see PermGroup
 */
open class PermGroupSet(elements: List<PermGroup> = mutableListOf()) : ArrayList<PermGroup>(){

    companion object {

        /**
         * Finds the [PermGroupSet] from [user] in global scope.
         * If no [user] is provided, it searches for all global scopes [PermGroupSet]s. Excluding the GUILD_OWNER one.
         * Note: If the user is invalid, returns an empty permgroup.
         * Note: The DEFAULT permgroup is included.
         */
        fun findGlobalScope(user: DogoUser? = null): PermGroupSet {
            return if(user != null && user.usr == null){
                PermGroupSet()
            } else {
                Database.PERMISSIONS.run {
                    transaction {
                        return@transaction slice(this@run.role)
                        .select {
                            guild.isNull() and role.isNotNull()
                        }.mapNotNull {
                            DogoBot.jda?.getRoleById(it[role]!!)
                        }.map {
                            PermGroup(it, null)
                        }.filter {
                            if(user != null)
                                it.role?.id in (it.role?.guild?.getMember(user.usr)?.roles?.map { it.id } ?: emptyList())
                            else true
                        }.let { PermGroupSet(it).also { it.add(PermGroup.DEFAULT) } }
                    }
                }
            }
        }

        /**
         * Finds the [PermGroupSet] from [user] in local scope.
         * If no [user] is providade, it searches for all local scopes [PermGroupSet]s.
         * Note: The default permgroup is not included.
         * Note: If the user isn't null and is invalid, returns an empty permgroup.
         */
        fun findLocalScope(guild: DogoGuild, user: DogoUser? = null): PermGroupSet {
            return if(user != null && user.usr == null){
                PermGroupSet()
            } else {
                Database.PERMISSIONS.run {
                    transaction {
                        return@transaction slice(this@run.guild, this@run.role)
                        .select {
                            this@run.guild eq guild.id
                        }.mapNotNull {
                            DogoBot.jda?.getRoleById(it[role])?.let { role ->
                                PermGroup(role, guild)
                            }
                        }.let {
                            if(user != null){
                                it.filter {
                                   it.role?.id in (guild.g?.getMember(user.usr)?.roles?.map { it.id } ?: emptyList())
                                }
                            } else it
                        }.let {
                            PermGroupSet(it).also { if(user != null && guild.g?.ownerId == user.id) it.add(PermGroup.GUILD_OWNER) }
                        }
                    }
                }
            }
        }

        /**
         * Gets permgroups within a context.
         *
         * -No [user], no [guild] = Returns all the permgroups registered.
         * -With [user], no [guild] = Returns the permgroups from [user] on global scope. Guild ones not included.
         * -No [user], with [guild] = Returns all the local permgroups from [guild] and the permgroups on global scope.
         * -With [user], with [guild] = Returns the permgroup from [user] in local and global scope.
         */
        fun find(user: DogoUser? = null, guild: DogoGuild? = null): PermGroupSet {
            val globalScope = findGlobalScope(user)
            return if(guild == null){
                globalScope
            } else {
                findLocalScope(guild, user).also { it.merge(globalScope) }
            }
        }
    }

    init {
        addAll(elements)
    }

    /**
     * Sorts the set matching its level and role position on Discord.
     */
    fun sort() = sortWith(Comparator { a, b ->
        when {
            a.designation == Designations.GUILD_LOCAL && b.designation == a.designation -> (a.role?.position ?: 0) - (b.role?.position ?: 0)
            a.designation == Designations.ADMINS && b.designation == a.designation -> (a.role?.position ?: 0) - (b.role?.position ?: 0)
            else -> -(a.designation.priority - b.designation.priority)
        }
    })

    /**
     * Checks if a user has access to a permission.
     *
     * @param[perm] the permission.
     */
    fun can(perm : String) : Boolean {
        sort()
        var b = false
        forEach {
            if(it.hasIncluded(perm)) b = true
            if(it.hasExcluded(perm)) b = false
         }
        return b
    }

    /**
     * Merges with one or more [PermGroupSet]s.
     */
    fun merge(vararg permGroupSet: PermGroupSet){
        permGroupSet.forEach { addAll(it) }
        sort()
    }

    override fun toString() = let { sort(); super.toString() }
}