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
 * The Tic Tac Toe game implementation.
 *
 * @param onWin block executed when a player won the game.
 *
 * @see [Player]
 *
 * @author NathanPB
 * @since 3.1.0
 */
open class TicTacToe(val onWin: (Player) -> Unit) {
    companion object {
        /**
         * Template of all wins available. (1 means the player you are checking, 0 means everything else (including the player itself)).
         */
        val wins = arrayOf(
                "111000000", "100100100",
                "000111000", "000000111",
                "010010010", "001001001",
                "100010001", "001010100"
        )
    }

    /**
     * The game table.
     *
     * Must be a 9 characters [String]. The following characters are allowed:
     * - *0* means empty.
     * - *1* means [Player.P1]
     * - *2* means [Player.P2]
     */
    var table = "000000000"

    /**
     * Plays the game (I lost).
     *
     * It also looks for winners and executes [onWin] if someone was found.
     *
     * @param[index] the index on table that the player changed.
     * @param[player] the player.
     */
    fun play(index: Int, player: Player) {
        table = copyTableWith(index, player, table)
        (hasWinner(table) ?: {
            if (countEmptySpaces(table) <= 2) {
                var returner : Player? = Player.ENVIROMENT
                arrayOf("12", "21")
                        .map { it.toCharArray().map { it.toString() } }
                        .forEach {
                            var workingOn = table
                            it.forEach {
                                workingOn = copyTableWith(
                                        nextEmptySpace(workingOn),
                                        if (it == "1") Player.P1 else Player.P2,
                                        workingOn
                                )
                            }
                            if (hasWinner(workingOn) != null) returner = null
                        }
                returner
            } else null
        }())?.let { onWin(it) }
    }

    /**
     * Replaces a specified index in a [String] with a player mark.
     *
     * @param[index] the index to replace.
     * @param[player] the player to mark.
     * @param[table] the table to analyze.
     *
     * @return the replaced table.
     */
    private fun copyTableWith(index: Int, player: Player, table: String) = "${table.substring(0, index)}${player.id}${table.substring(index + 1)}"

    /**
     * Checks if a table has a winner.
     *
     * @param[table] the table to analyze.
     *
     * @return [Player.P1] if the first player won; [Player.P2] if the second player won. [Player.ENVIROMENT] if the game tied and null if the game didn't ended.
     */
    private fun hasWinner(table: String): Player? {
        return arrayOf(Player.P1, Player.P2)
                .firstOrNull {
                    val copy = table.replace((if (it == Player.P1) Player.P2 else Player.P1).id, "0").replace("2", "1")
                    wins.any { win ->
                        var match = true
                        win.toCharArray().forEachIndexed { index, it ->  if(it == '1' && copy[index] != it) match = false}
                        match
                    }
                }
    }

    /**
     * Count how many zeroes the [table] contains.
     *
     * @param[table] the table to analyze.
     *
     * @return how many zeroes the [table] contains.
     */
    private fun countEmptySpaces(table: String): Int {
        return table.toCharArray()
                .filter { c ->
                    !arrayOf(Player.P1, Player.P2).map { it.id }.any { "$c" == it }
                }.count()
    }

    /**
     * Finds the next *0* on [table].
     *
     * @param [table] the table to analyze.
     *
     * @return the index of the first *0*. -1 if not found.
     */
    private fun nextEmptySpace(table: String) = table.indexOf(Player.ENVIROMENT.id)


}