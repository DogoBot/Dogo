package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.minigames.tictactoe.discord.TicTacToeImp
import io.github.dogo.utils.EmoteReference
import io.github.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

class TicTacToe : ReferencedCommand(
        CommandReference("tictactoe", aliases = "ttt", usage = "@MyFriend", category = CommandCategory.FUN),
        { cmd ->
            var friend = cmd.msg.mentionedUsers.firstOrNull() ?: DogoBot.jda!!.selfUser
            if(friend.id == cmd.sender.id) friend = DogoBot.jda!!.selfUser

            if(!friend.isBot && !friend.isFake) {
                cmd.reply("inviting", friend.asMention, preset = true)
                SimpleReactionMenu(cmd).also {
                    it.target = friend.id
                    it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                    it.embed = EmbedBuilder().setColor(ThemeColor.PRIMARY).setTitle(cmd.langEntry.getText(cmd.lang, "title", cmd.sender.formatName(cmd.guild)))
                    val refuse = {
                        it.end(true)
                        cmd.reply("refused", DogoUser(friend).formatName(cmd.guild), preset = true)
                    }
                    it.addAction(EmoteReference.WHITE_CHECK_MARK, cmd.langEntry.getText(cmd.lang, "accept")){
                        TicTacToeImp(cmd, cmd.sender, DogoUser(friend))
                        it.end(true)
                    }
                    it.addAction(EmoteReference.NEGATIVE_SQUARED_CROSS_MARK, cmd.langEntry.getText(cmd.lang, "deny"), refuse)
                }.build().send()
            } else TicTacToeImp(cmd, cmd.sender, DogoUser(friend))
        }
)