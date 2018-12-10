package io.github.dogo.utils

import java.io.File
import java.io.InputStream
import java.util.*

/**
 * Utility static methods about the File System
 *
 * @author NathanPB
 * @since 3.1.0
 */
class FileUtils {
    companion object {

        /**
         * Reads a file content from a [File] object.
         *
         * @param[file] the File to read.
         *
         * @return all chars on [file].
         */
        fun getChars(file : File) : String {
            var result = ""
            Scanner(file).use {
                while(it.hasNextLine()) result+="${it.nextLine()}\n"
            }
            return result
        }

        /**
         * Reads a [InputStream] object.
         *
         * @param[input] the [InputStream] to read.
         *
         * @return all chars on [input].
         */
        fun getChars(input : InputStream) : String {
            var result = ""
            Scanner(input).use {
                while(it.hasNextLine()) result+="${it.nextLine()}\n"
            }
            return result
        }
    }
}