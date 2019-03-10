package dev.nathanpb.dogo.statistics

import net.dv8tion.jda.core.entities.User

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
 * [Statistic] subclass for Tic Tac Toe game Statistics
 * @see dev.nathanpb.dogo.minigames.tictactoe
 *
 * @param[table] the game table. Must be a String with exactly 9 characters, only '0', '1' and '2' are allowed.
 * @param[p1] the player 1 of the game.
 * @param[p2] the player 2 of the game.
 * @param[winner] the winner of the game. Must be the same as [p1], [p2] or null (if the game has tied)
 *
 * @author NathanPB
 * @since 3.1.0
 */
class TicTacToeStatistics(val table: String, val p1: User, val p2: User, val winner: User?) : Statistic() {
    init {
        if(winner != null && !(p1 == winner || p2 == winner)){
            throw RuntimeException("Winner ${winner.id} is neither p1(${p1.id}) or p2(${p2.id})")
        }
    }
}