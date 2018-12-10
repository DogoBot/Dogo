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
        {
            var friend = msg.mentionedUsers.firstOrNull() ?: DogoBot.jda!!.selfUser
            if(friend.id == sender.id) friend = DogoBot.jda!!.selfUser

            if(!friend.isBot && !friend.isFake) {
                reply("inviting", friend.asMention, preset = true)
                SimpleReactionMenu(this).also {
                    it.target = friend.id
                    it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                    it.embed = EmbedBuilder().setColor(ThemeColor.PRIMARY).setTitle(langEntry.getText(lang, "title", sender.formatName(guild)))
                    val refuse = {
                        it.end(true)
                        reply("refused", DogoUser(friend).formatName(guild), preset = true)
                    }
                    it.addAction(EmoteReference.WHITE_CHECK_MARK, langEntry.getText(lang, "accept")){
                        TicTacToeImp(this, sender, DogoUser(friend))
                        it.end(true)
                    }
                    it.addAction(EmoteReference.NEGATIVE_SQUARED_CROSS_MARK, langEntry.getText(lang, "deny"), refuse)
                }.build().send()
            } else TicTacToeImp(this, sender, DogoUser(friend))
        }
)