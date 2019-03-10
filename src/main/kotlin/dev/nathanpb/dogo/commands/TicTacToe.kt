package dev.nathanpb.dogo.commands

import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.CommandCategory
import dev.nathanpb.dogo.core.command.CommandReference
import dev.nathanpb.dogo.core.command.ReferencedCommand
import dev.nathanpb.dogo.discord.DiscordManager
import dev.nathanpb.dogo.discord.formatName
import dev.nathanpb.dogo.discord.menus.SimpleReactionMenu
import dev.nathanpb.dogo.minigames.tictactoe.discord.TicTacToeImp
import dev.nathanpb.dogo.utils._static.EmoteReference
import dev.nathanpb.dogo.utils._static.ThemeColor
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
            var friend = msg.mentionedUsers.firstOrNull() ?: DiscordManager.jda!!.selfUser
            if(friend.id == sender.id) friend = DiscordManager.jda!!.selfUser

            if(!friend.isBot && !friend.isFake) {
                reply("inviting", friend.asMention, preset = true)
                SimpleReactionMenu(this).also {
                    it.target = friend.id
                    it.timeout = dev.nathanpb.dogo.core.DogoBot.data.TIMEOUTS.GENERAL
                    it.embed = EmbedBuilder().setColor(ThemeColor.PRIMARY).setTitle(langEntry.getText("title", sender.formatName(guild)))
                    val refuse = {
                        it.end(true)
                        reply("refused", friend.formatName(guild), preset = true)
                    }
                    it.addAction(EmoteReference.WHITE_CHECK_MARK, langEntry.getText("accept")){
                        TicTacToeImp(this, sender, friend)
                        it.end(true)
                    }
                    it.addAction(EmoteReference.NEGATIVE_SQUARED_CROSS_MARK, langEntry.getText("deny"), refuse)
                    it.build()
                }.send()
            } else TicTacToeImp(this, sender, friend)
        }
)