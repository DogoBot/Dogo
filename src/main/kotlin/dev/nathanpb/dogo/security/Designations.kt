package dev.nathanpb.dogo.security

/**
 * The permgroup designation.
 *
 * -ADMINS = Applied globally, cannot be override.
 * -GUILD_OWNER = Applied to the guild owner on his guild.
 * -GUILD_LOCAL = Applied to the guild roles.
 * -DEFAULT = Applied everywhere, can be override by anything else.
 * -INVALID = Just invalid, an ignored condition.
 */
enum class Designations(val priority: Int) {
    ADMINS(0),
    GUILD_OWNER(1),
    GUILD_LOCAL(2),
    DEFAULT(3),
    INVALID(4)
}