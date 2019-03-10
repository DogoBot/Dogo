package dev.nathanpb.dogo.minigames.tictactoe.discord

import dev.nathanpb.dogo.core.database.Tables
import dev.nathanpb.dogo.core.DogoBot
import dev.nathanpb.dogo.core.command.CommandContext
import dev.nathanpb.dogo.discord.formatName
import dev.nathanpb.dogo.discord.menus.SimpleReactionMenu
import dev.nathanpb.dogo.minigames.tictactoe.ITTTImp
import dev.nathanpb.dogo.minigames.tictactoe.OnePlayerTTT
import dev.nathanpb.dogo.minigames.tictactoe.TTTPlayer
import dev.nathanpb.dogo.minigames.tictactoe.TwoPlayersTTT
import dev.nathanpb.dogo.utils._static.EmoteReference
import dev.nathanpb.dogo.utils._static.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

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
 * The Discord implementation of [dev.nathanpb.dogo.minigames.tictactoe.TicTacToe]
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TicTacToeImp(context: CommandContext, val p1: User, val p2: User) : SimpleReactionMenu(context) {

    /**
     * Runs when someone wins.
     */
    private val onWin: (TTTPlayer)->Unit = {
        this.build(
                createEmbed()
                        .appendDescription("\n"+when(it){
                            TTTPlayer.ENVIRONMENT -> context.langEntry.getText("tie")
                            else -> context.langEntry.getText("winner", (if(it == TTTPlayer.P1) p1 else p2).formatName(context.guild))
                        })
        )
        this.send()
        this.end(false)
        dumbSetStatistics(it)
    }

    /**
     * The game container.
     *
     * It will be [OnePlayerTTT] if one of [p1] or [p2] are bots, or [TwoPlayersTTT] if the two players are humans.
     */
    val ttt: ITTTImp = when {
        p1.isntHuman() -> OnePlayerTTT(onWin, TTTPlayer.P2)
        p2.isntHuman() -> OnePlayerTTT(onWin, TTTPlayer.P1)
        else -> TwoPlayersTTT(onWin)
    }

    /**
     * Sets the player statistics.
     */
    private fun dumbSetStatistics(winner: TTTPlayer) {
        transaction {
            Tables.TICTACTOESTATISTICS.run {
                insert {
                    it[table] = ttt.table
                }[id]?.let { id ->
                    Tables.TTTPlayers.run {
                        arrayOf(p1, p2).forEach { p ->
                            insert {
                                it[statistic] = id
                                it[user] = p.id
                                it[slot] = p2.id == p.id
                                it[this.winner] = p.id == winner.id
                            }.let {} //just to return Unit
                        }
                    }
                }
            }
        }
    }

    init {
        this.build(createEmbed())
        this.send()
    }

    /**
     * Builds the embed with the table.
     *
     * @return the embed.
     */
    private fun createEmbed() : EmbedBuilder {
        return EmbedBuilder()
            .setTitle("Tic Tac Toe!")
            .setColor(ThemeColor.PRIMARY)
            .also {
                if(!(ttt is OnePlayerTTT || ttt.hasWinner)){
                    it.appendDescription(context.langEntry.getText("turn", getCurrentPlayer().formatName(this.context.guild))+"\n\n")
                }
                it.appendDescription(
                        getTableEmoted().mapIndexed { i, it ->
                            if((i+1)%3 == 0) "${it.getAsMention()}\n" else it.getAsMention()
                        }.joinToString("")
                )
            }
    }

    /**
     * Formats the table to an user-friendly interface.
     *
     * @return the table formatted with Discord emotes.
     */
    private fun getTableEmoted() : List<EmoteReference> {
        return ttt.table.toCharArray().mapIndexed { i, it ->
            when (it) {
                '1' -> EmoteReference.X
                '2' -> EmoteReference.O
                else -> EmoteReference.getRegional(i.toString()[0])
            }
        }
    }

    /**
     * Gets the current player.
     *
     * @return [p1] or [p2]
     */
    private fun getCurrentPlayer() = if(this.ttt.currentTTTPlayer == TTTPlayer.P1) p1 else p2

    /**
     * Sends the table and sets its actions.
     *
     * @see SimpleReactionMenu
     * @see SimpleReactionMenu.Action
     * @see SimpleReactionMenu.send
     */
    override fun send(){
        dev.nathanpb.dogo.core.DogoBot.eventBus.unregister(this::onReact)
        this.actions.clear()
        if(!ttt.hasWinner){
            getTableEmoted()
                    .filter { !(it == EmoteReference.O || it == EmoteReference.X) }
                    .forEach {
                        this.addAction(it,  "") {
                            this.ttt.play(it.equivalentChar.toString().toInt())
                            if(!ttt.hasWinner){
                                this.build(createEmbed())
                                this.target = getCurrentPlayer().id
                                this.send()
                            }
                        }
                    }
            this.addAction(EmoteReference.OCTAGONAL_SIGN, "") {
                ttt.forceWin(if(ttt.currentTTTPlayer == TTTPlayer.P1) TTTPlayer.P2 else TTTPlayer.P1)
            }
        }
        super.send()
    }

    /*
     Extension Shit
     */
    /**
     * Checks if the user is a bot or a fake user.
     */
    fun User.isntHuman() = isBot || isFake
}