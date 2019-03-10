package dev.nathanpb.dogo.minigames.tictactoe

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
class TwoPlayersTTT(onWin: (TTTPlayer) -> Unit) : TicTacToe(onWin), ITTTImp {

    /**
     * The turn owner.
     */
    override var currentTTTPlayer : TTTPlayer = TTTPlayer.P1

    /**
     * Runs [TicTacToe.play] with [currentTTTPlayer] as player.
     */
     override fun play(index: Int)= play(index, currentTTTPlayer).let {togglePlayer()}

    /**
     * Toggles the currentTTTPlayer player.
     */
    fun togglePlayer(){
        currentTTTPlayer = if(currentTTTPlayer == TTTPlayer.P1) TTTPlayer.P2 else TTTPlayer.P1
    }

    override fun forceWin(TTTPlayer: TTTPlayer)= onWin(TTTPlayer)

}