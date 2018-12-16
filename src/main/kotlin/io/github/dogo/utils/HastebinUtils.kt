package io.github.dogo.utils

import com.mashape.unirest.http.Unirest
import org.json.JSONObject


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
 * Utility about Hastebin.com
 *
 * @author NathanPB
 * @since 3.1.0
 */
class HastebinUtils {
    companion object {
        const val URL = "https://hastebin.com/"

        /**
         * Uploads a text to Hastebin.com
         *
         * @param[text] text to upload.
         *
         * @return the document token.
         */
        fun upload(text: String) = Unirest.post("https://hastebin.com/documents")
                        .header("User-Agent", "PCBRecBot")
                        .header("Content-Type", "text/plain")
                        .body(text)
                        .asJson()
                        .getBody()
                        .getObject()
                        .getString("key")

        /**
         * Reads a text from Hastebin.com
         *
         * @param[id] the upload token.
         * @return the text.
         */
        fun download(id: String) = WebUtils.get("${URL}raw/$id")
    }
}