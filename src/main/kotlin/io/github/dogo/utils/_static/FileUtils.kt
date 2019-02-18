package io.github.dogo.utils._static

import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import io.github.dogo.core.DogoBot
import java.io.File
import java.io.InputStream
import java.util.*

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