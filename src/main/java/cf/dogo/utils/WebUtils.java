package cf.dogo.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebUtils {
    public static String get(String url, HashMap<String, Object> args){
        if(!url.endsWith("/")) url+="/";
        args.keySet().forEach(k -> {
            try {
                args.replace(k, URLEncoder.encode(args.get(k).toString(), "UTF-8"));
            }catch (Exception ex){}
        });
        if(!args.isEmpty()){
            url+="?";
            for(Map.Entry<String, Object> arg : args.entrySet()){
                url+=arg.getKey()+"="+arg.getValue()+"&";
            }
            url = url.substring(0, url.length()-1);
        }
        return url;
    }
}
