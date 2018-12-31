package io.github.dogo.utils

import net.dv8tion.jda.core.EmbedBuilder
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.lang.Exception
import java.net.URL
import javax.swing.text.html.HTML

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
 * Utility methods about Facebook
 *
 * @author NathanPB
 * @since 3.1.0
 */
class FacebookUtils {
    companion object {

        /**
         * Builds a embed representation of the latest post from a page.
         *
         * @param[pagename] the page id.
         *
         * @return the embed representation.
         */
        fun getLastPostEmbed(pagename: String) = EmbedBuilder().setColor(ThemeColor.PRIMARY)
                .setAuthor(
                        FacebookUtils.getName(pagename),
                        "https://facebook.com/$pagename",
                        FacebookUtils.getProfilePic(pagename)
                )
                .also {embed ->
                    FacebookUtils.getLastPost(pagename).let {
                        if(it.keySet().contains("text"))
                            embed.setDescription(
                                    it.getString("text")
                                            .let {
                                                if(it.length > 1024) {
                                                    it.substring(0, 1021)+"..."
                                                } else it
                                            }
                            )

                        if(it.keySet().contains("preview"))
                            embed.setImage(it.getString("preview"))
                    }
                }

        /**
         * Fetch the latest post from a page.
         *
         * @param[pagename] the page id.
         *
         * @return a [JSONObject] with data about the latest post.
         */
        fun getLastPost(pagename: String) : JSONObject {
            return JSONObject().also { json ->
                try {
                    val post = Jsoup.parse(URL("https://facebook.com/$pagename"), 12000).getElementsByClass("_1dwg").first()
                    try {
                        json.put("text", post.getElementsByAttributeValue("data-ad-preview", "message").text())
                    } catch (ex: Exception){}

                    try {
                        val elements = post.getElementsByTag("img")
                        if(elements.size > 1)
                            json.put("preview", elements[1].attr("src"))
                    } catch (ex: Exception){}
                } catch (ex: Exception) {
                    throw Exception("Profile https://facebook.com/$pagename could not be found")
                }
            }
        }

        /**
         * The profile picture from a page.
         * The method to obtain the picture is fetching it from the latest post. So the page must have at least one post.
         *
         * @param[pagename] the page id.
         *
         * @return the profile picture.
         */
        fun getProfilePic(pagename: String) : String {
            return try {
                Jsoup.parse(URL("https://facebook.com/$pagename"), 12000)
                        .getElementsByClass("_5xib")[0].attr("src")
            } catch (ex: Exception) {
                throw Exception("Profile https://facebook.com/$pagename could not be found")
            }
        }

        /**
         * The page name page.
         * The method to obtain the name is fetching it from the latest post. So the page must have at least one post.
         *
         * @param[pagename] the page id.
         *
         * @return the page name.
         */
        fun getName(pagename: String) : String {
            return try {
                Jsoup.parse(URL("https://facebook.com/$pagename"), 12000)
                        .getElementsByClass("fwb")[0].text()
            } catch (ex: Exception) {
                throw Exception("Profile https://facebook.com/$pagename could not be found")
            }
        }
    }
}