package cf.dogo.commands

import cf.dogo.core.cmdHandler.CommandCategory
import cf.dogo.core.cmdHandler.CommandContext
import cf.dogo.core.cmdHandler.CommandFactory
import cf.dogo.core.cmdHandler.DogoCommand
import cf.dogo.core.entities.DogoUser
import cf.dogo.minigames.tictactoe.discord.TicTacToeImp

class TicTacToe(factory: CommandFactory) : DogoCommand("tictactoe", factory){
    override val minArgumentsSize = 0
    override val usage = "\n@MyFriend"
    override val aliases = "ttt"
    override val category = CommandCategory.MINIGAMES

    override fun execute(cmd: CommandContext) {
        TicTacToeImp(cmd, cmd.sender, DogoUser(cmd.msg.mentionedMembers.firstOrNull()!!.user))
    }
}