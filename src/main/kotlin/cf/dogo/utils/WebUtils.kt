package cf.dogo.utils

import java.net.URLEncoder

class WebUtils {
    companion object {
        operator fun get(url: String, vararg args: Pair<String, Any>): String {
            var url = url
            if (!url.endsWith("/")) url += "/"
            if(args.isNotEmpty()){
                url += args
                        .map {
                            Pair(URLEncoder.encode(it.first, "UTF-8"), URLEncoder.encode(it.second.toString(), "UTF-8"))
                        }
                        .joinToString(separator = "&", prefix = "&")
            }
            return url
        }
    }
}