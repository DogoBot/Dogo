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
 * Simple interface to TicTacToe implementations.
 *
 * @author NathanPB
 * @since 3.1.0
 */
interface ITTTImp {
    fun play(index: Int)

    /**
     * The currentPlayer turn's owner.
     */
    var currentPlayer: Player

    /**
     * True if the game has a winner ([Player.ENVIROMENT] is included)
     */
    var hasWinner: Boolean

    /**
     * The table
     */
    var table: String

    /**
     * Forces someone to win the game.
     */
    fun forceWin(player: Player)
}