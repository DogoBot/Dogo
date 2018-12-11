package io.github.dogo.statistics

import io.github.dogo.core.entities.DogoUser
import org.bson.Document

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
 * @see io.github.dogo.minigames.tictactoe
 *
 * @param[table] the game table. Must be a String with exactly 9 characters, only '0', '1' and '2' are allowed.
 * @param[p1] the player 1 of the game.
 * @param[p2] the player 2 of the game.
 * @param[winner] the winner of the game. Must be the same as [p1], [p2] or null (if the game has tied)
 *
 * @author NathanPB
 * @since 3.1.0
 */
data class TicTacToeStatistics(val table: String, val p1: DogoUser, val p2: DogoUser, val winner: DogoUser?) : Statistic({
    if(winner != null && !(p1 == winner || p2 == winner)){
        throw RuntimeException("Winner ${winner.id} is neither p1(${p1.id}) or p2(${p2.id})")
    }
    Document().append("table", table).append("p1", p1.id).append("p2", p2.id).append("winner", winner?.id)
}()){

    /**
     * Creates a [TicTacToeStatistics] instance from a [Document].
     *
     * The [doc] must contains the following fields:
     * - *table*: String
     * - *p1*: String
     * - *p2*: Sring
     * - *winner*: String or null
     *
     * @param[doc] the document to parse.
     */
    constructor(doc: Document) : this(doc.getString("table"), DogoUser(doc.getString("p1")), DogoUser(doc.getString("p2")), DogoUser(doc.getString("winner")))

    class Finder : StatisticsFinder<TicTacToeStatistics>() {
        override fun map(doc: Document) = try { TicTacToeStatistics(doc) } catch (ex: Exception) { null }

        /**
         * The first player of the game.
         */
        var p1 : DogoUser
            get() = DogoUser(getString("p1"))
            set(it){append("p1", it.id)}

        /**
         * The second player of the game.
         */
        var p2: DogoUser
            get() = DogoUser(getString("p2"))
            set(it){append("p2", it.id)}

        /**
         * The winner player of the game. Set to null to match only tied games.
         */
        var winner: DogoUser?
            get() = DogoUser(getString("winner"))
            set(it){append("winner", it?.id)}

        /**
         * The table of the game.
         */
        var table: String
            get() = getString("table")
            set(it){append("table", it)}

    }
}