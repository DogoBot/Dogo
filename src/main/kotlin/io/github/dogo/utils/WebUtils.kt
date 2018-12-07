package io.github.dogo.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WebUtils {
    companion object {
        fun get(url: String, args: Array<Pair<String, String>> = emptyArray(), headers: Array<Pair<String, String>> = emptyArray()): String {
            var url = url
            if (args.isNotEmpty()) {
                url += args.map {
                    Pair(URLEncoder.encode(it.first, "UTF-8"), URLEncoder.encode(it.second, "UTF-8"))
                }.joinToString(separator = "&", prefix = "&")
            }
            val response = StringBuilder()
            (URL(url).openConnection() as HttpURLConnection).let {
                it.requestMethod = "GET"
                headers.forEach { h ->
                    it.setRequestProperty(h.first, h.second)
                }
                it.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")


                it.inputStream.also {
                    BufferedReader(InputStreamReader(it)).also {
                        do {
                            val line = it.readLine()?.let { response.append("$it\n") }
                        } while (line != null)
                    }.close()
                }.close()
            }
            return response.toString()
        }
    }
}