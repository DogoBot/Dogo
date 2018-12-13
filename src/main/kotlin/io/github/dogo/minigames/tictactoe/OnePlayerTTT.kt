package io.github.dogo.minigames.tictactoe

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
 * Single Player Tic Tac Toe. Human Player plays against IA
 *
 * @author NathanPB
 * @since 3.1.0
 */
class OnePlayerTTT(onWin: (Player) -> Unit, override var currentPlayer: Player) : TicTacToe(onWin), ITTTImp {

    /**
     * runs [TicTacToe.play] with [player] as player.
     */
    override fun play(index: Int) {
        play(index, currentPlayer)
        if(!hasWinner) {
            this.play({
                val empty = table.toCharArray().mapIndexed { index, it ->
                    if (it == '0') index else -1
                }.filter { it > -1 }
                empty[Random().nextInt(empty.size)]
            }(), if (currentPlayer == Player.P1) Player.P2 else Player.P1)
        }
    }

    override fun forceWin(player: Player)= onWin(player)
}