package io.github.dogo.minigames.tictactoe

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
 * Two Players implementation of [TicTacToe]
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TwoPlayersTTT(onWin: (Player) -> Unit) : TicTacToe(onWin), ITTTImp {

    /**
     * The currentPlayer turn owner.
     */
    override var currentPlayer : Player = Player.P1

    /**
     * runs [TicTacToe.play] with [currentPlayer] as player.
     */
     override fun play(index: Int)= play(index, currentPlayer).let {togglePlayer()}

    /**
     * Toggles the currentPlayer player
     */
    fun togglePlayer(){
        currentPlayer = if(currentPlayer == Player.P1) Player.P2 else Player.P1
    }

    override fun forceWin(player: Player)= onWin(player)

}