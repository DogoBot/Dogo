package io.github.dogo.minigames.tictactoe

class TwoPlayersTTT(onWin: (Player) -> Unit) : TicTacToe(onWin) {
    var current : Player = Player.P1

    fun play(index: Int)= play(index, current).let {togglePlayer()}

    fun togglePlayer(){
        current = if(current == Player.P1) Player.P2 else Player.P1
    }

}