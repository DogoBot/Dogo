package io.github.dogo.utils

import java.io.InputStreamReader
import java.io.BufferedReader

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
 * Utility about OS
 *
 * @author NathanPB
 * @since 3.1.0
 */
class SystemUtils {
    companion object {

        /**
         * Executes a CMD/Terminal command
         *
         * @param[command] the command.
         *
         * @return the command output.
         */
        fun exec(command: String) = Runtime.getRuntime().exec(command)
                .also { it.waitFor() }
                .let { BufferedReader(InputStreamReader(it.inputStream)).readLines().joinToString("\n") }
    }
}