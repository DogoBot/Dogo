package io.github.dogo.minigames.tictactoe.discord

import io.github.dogo.core.DogoBot
import io.github.dogo.core.command.CommandContext
import io.github.dogo.core.entities.DogoUser
import io.github.dogo.menus.SimpleReactionMenu
import io.github.dogo.minigames.tictactoe.ITTTImp
import io.github.dogo.minigames.tictactoe.OnePlayerTTT
import io.github.dogo.minigames.tictactoe.Player
import io.github.dogo.minigames.tictactoe.TwoPlayersTTT
import io.github.dogo.statistics.TicTacToeStatistics
import io.github.dogo.utils.EmoteReference
import io.github.dogo.utils.ThemeColor
import net.dv8tion.jda.core.EmbedBuilder
import java.util.*

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
 * The Discord implementation of [io.github.dogo.minigames.tictactoe.TicTacToe]
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TicTacToeImp(context: CommandContext, val p1: DogoUser, val p2: DogoUser) : SimpleReactionMenu(context) {

    /**
     * Runs when someone wins.
     */
    private val onWin: (Player)->Unit = {
        hasWinner = true
        this.build(
                createEmbed()
                        .appendDescription("\n"+when(it){
                            Player.ENVIROMENT -> context.langEntry.getText(context.lang, "tie")
                            else -> context.langEntry.getText(context.lang, "winner", getCurrentPlayer().formatName(context.guild))
                        })
        )
        this.send(true)
        this.end(false)
        dumbSetStatistics(it)
    }

    /**
     * True if someone won the game.
     */
    var hasWinner = false

    /**
     * The game container.
     *
     * It will be [OnePlayerTTT] if one of [p1] or [p2] are bots, or [TwoPlayersTTT] if the two players are humans.
     */
    val ttt: ITTTImp = when {
        p1.isBot() -> OnePlayerTTT(onWin, Player.P2)
        p2.isBot() -> OnePlayerTTT(onWin, Player.P1)
        else -> TwoPlayersTTT(onWin)
    }

    /**
     * Sets the player statistics.
     */
    private fun dumbSetStatistics(winner: Player) {
        TicTacToeStatistics(ttt.table, p1, p2, if(winner != Player.ENVIROMENT) getCurrentPlayer() else null).update()
    }

    init {
        this.build(createEmbed())
        this.send(false)
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
                if(!(ttt is OnePlayerTTT || hasWinner)){
                    it.appendDescription(context.langEntry.getText(context.lang, "turn", getCurrentPlayer().formatName(this.context.guild))+"\n\n")
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
    private fun getCurrentPlayer() : DogoUser = if(this.ttt.currentPlayer == Player.P1) p1 else p2

    /**
     * Sends the table and sets its actions.
     *
     * @see SimpleReactionMenu
     * @see SimpleReactionMenu.Action
     * @see SimpleReactionMenu.send
     */
    fun send(end: Boolean){
    DogoBot.eventBus.unregister(this)
        this.actions.clear()
        if(!end){
            getTableEmoted()
                    .filter { !(it == EmoteReference.O || it == EmoteReference.X) }
                    .forEach {
                        this.addAction(it,  "") {
                            this.ttt.play(it.equivalentChar.toString().toInt())
                            if(!hasWinner){
                                this.build(createEmbed())
                                this.target = getCurrentPlayer().id
                                this.send(false)
                            }
                        }
                    }
            this.addAction(EmoteReference.OCTAGONAL_SIGN, "") {
                ttt.forceWin(if(ttt.currentPlayer == Player.P1) Player.P1 else Player.P2)
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
    fun DogoUser.isBot() = this.usr?.isBot == true || this.usr?.isFake == true
}