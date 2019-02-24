package io.github.dogobot.core

import io.github.nathanpb.bb.BootManager
import io.github.nathanpb.bb.phases.Phase

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
 * Custom [BootManager] to edit some messages.
 *
 * @author NathanPB
 * @since 4.0.0
 */
class DogoBootManager : BootManager(){

    override fun onStartupStart() {
        logger.info { "Starting Dogo v${DogoCore.version} on PID ${DogoCore.pid}" }
        logger.info {
            "\n  _____                    ____        _   \n" +
                    " |  __ \\                  |  _ \\      | |  \n" +
                    " | |  | | ___   __ _  ___ | |_) | ___ | |_ \n" +
                    " | |  | |/ _ \\ / _  |/ _ \\|  _ < / _ \\| __|\n" +
                    " | |__| | (_) | (_| | (_) | |_) | (_) | |_ \n" +
                    " |_____/ \\___/ \\__, |\\___/|____/ \\___/ \\__|\n" +
                    "                __/ |                      \n" +
                    "               |___/                    \n" +
                    "By NathanPB - https://github.com/DogoBot/Dogo"
        }
    }

    override fun onStartupEnd(initTime: Long) {
        //todo human readable format on ms
        logger.info { "Boot done in ${System.currentTimeMillis() - initTime}ms" }
    }

    override fun beforeExecution(phase: Phase) {
        logger.info { phase.formatDisplay(Phase.DEFAULT_FORMAT) }
    }

    override fun afterExecution(phase: Phase, initTime: Long) {
        logger.info { "${phase.formatDisplay(Phase.INDENTATION)}${phase.formatDisplay(Phase.INDEXED)} Done in ${System.currentTimeMillis() - initTime}ms" }
    }
}