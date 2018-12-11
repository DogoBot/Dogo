package io.github.dogo.utils

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
 * Utility static methods used to data formatting.
 *
 * @author NathanPB
 * @since 3.1.0
 */
class DisplayUtils {
    companion object {

        /**
         * Formats a timestamp to human readable format.
         *
         * @param[duration] the timestamp to format.
         *
         * @return a human readable [String]
         */
        fun formatTimeSimple(duration : Long) : String {
            val years = duration / 31104000000L
            val months = duration / 2592000000L % 12
            val days = duration / 86400000L % 30
            val hours = duration / 3600000L % 24
            val minutes = duration / 60000L % 60
            val seconds = duration / 1000L % 60
            var ata = ""
            if (years > 1) ata += pad(years, 2) + ":"
            if (months > 1) ata += pad(months, 2) + ":"
            if (days > 1) ata += pad(days, 2) + ":"
            return ata + pad(hours, 2) + ":" + pad(minutes, 2) + ":" + pad(seconds, 2)
        }

        /**
         * Pads a [String] to left with a [Char] until its length reaches the specified value.
         *
         * @param[str] the string to be padded.
         * @param[length] the length that [str] should have.
         * @param[padChar] the char to pad the empty spaces (optional). Default value is '0'
         *
         * @return The padded [String]
         */
        fun pad(str: Any, length: Int, padChar: Char = '0'): String {
            var ata = str.toString() + ""
            while (ata.length < length) {
                ata = "$padChar$ata"
            }
            return ata
        }
    }
}