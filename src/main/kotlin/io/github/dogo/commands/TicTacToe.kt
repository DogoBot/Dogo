package io.github.dogo.commands

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandCategory
import io.github.dogo.core.command.CommandReference
import io.github.dogo.core.command.ReferencedCommand
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.minigames.tictactoe.discord.TicTacToeImp
import io.github.dogo.utils._static.EmoteReference
import io.github.dogo.utils._static.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder

/*
Copyright 2019 Nathan Bombana

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/**
 * @author NathanPB
 * @since 3.1.0
 */
class TicTacToe : ReferencedCommand(
        CommandReference("tictactoe", aliases = "ttt", usage = "@MyFriend", category = CommandCategory.FUN, permission = "command"),
        {
            var friend = msg.mentionedUsers.firstOrNull() ?: DogoBot.jda!!.selfUser
            if(friend.id == sender.id) friend = DogoBot.jda!!.selfUser

            if(!friend.isBot && !friend.isFake) {
                reply("inviting", friend.asMention, preset = true)
                SimpleReactionMenu(this).also {
                    it.target = friend.id
                    it.timeout = DogoBot.data.TIMEOUTS.GENERAL
                    it.embed = EmbedBuilder().setColor(ThemeColor.PRIMARY).setTitle(langEntry.getText("title", sender.formatName(guild)))
                    val refuse = {
                        it.end(true)
                        reply("refused", DogoUser.from(friend).formatName(guild), preset = true)
                    }
                    it.addAction(EmoteReference.WHITE_CHECK_MARK, langEntry.getText("accept")){
                        TicTacToeImp(this, sender, DogoUser.from(friend))
                        it.end(true)
                    }
                    it.addAction(EmoteReference.NEGATIVE_SQUARED_CROSS_MARK, langEntry.getText("deny"), refuse)
                    it.build()
                }.send()
            } else TicTacToeImp(this, sender, DogoUser.from(friend))
        }
)