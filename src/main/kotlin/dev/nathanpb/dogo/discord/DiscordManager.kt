package dev.nathanpb.dogo.discord

import dev.nathanpb.dogo.core.DogoBot
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import java.util.concurrent.Executors

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
 *
 * @author NathanPB
 * @since 3.2.0
 */
class DiscordManager {
    companion object {

        /**
         * JDA Conenction
         */
        var jda: JDA? = null

        /**
         * The JDA Output queue.
         */
        val jdaOutputThread = Executors.newSingleThreadExecutor()


        /**
         * Connects to JDA.
         * @param[token] the token to authenticate.
         */
        fun connect(token: String){
            jda = JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setGame(Game.watching("myself starting"))
                    .addEventListener(JDAListener(dev.nathanpb.dogo.core.DogoBot.eventBus))
                    .build().awaitReady()
        }
    }
}