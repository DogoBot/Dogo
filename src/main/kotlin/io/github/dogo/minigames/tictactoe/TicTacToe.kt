package io.github.dogo.minigames.tictactoe

open class TicTacToe(val onWin: (Player) -> Unit) {
    companion object {
        val wins = arrayOf(
                "111000000", "100100100",
                "000111000", "000000111",
                "010010010", "001001001",
                "100010001", "001010100"
        )
    }

    var table = "000000000"

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

    private fun copyTableWith(index: Int, player: Player, table: String) = "${table.substring(0, index)}${player.id}${table.substring(index + 1)}"

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

    private fun countEmptySpaces(table: String): Int {
        return table.toCharArray()
                .filter { c ->
                    !arrayOf(Player.P1, Player.P2).map { it.id }.any { "$c" == it }
                }.count()
    }

    private fun nextEmptySpace(table: String) = table.indexOf(Player.ENVIROMENT.id)


}