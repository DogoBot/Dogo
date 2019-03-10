package dev.nathanpb.dogo.core.command

open class ReferencedCommand(val reference: CommandReference, val command: CommandContext.()->Unit)