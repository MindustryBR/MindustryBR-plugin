package MindustryBR.internal;

import arc.struct.ObjectMap;
import arc.struct.StringMap;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Translate {
    public static String API = "https://translate-api.ml";

    private Translate() {
        super();
    }

    public static String translate(String text, String lang) throws IOException, InterruptedException {
        StringMap tmp = new StringMap();
        tmp.put("text", text);
        tmp.put("lang", lang);

        return Request.get("/translate", tmp);
    }

    public static String detect(String text) throws IOException, InterruptedException {
        StringMap tmp = new StringMap();
        tmp.put("text", text);

        return Request.get("/detect", tmp);
    }

    public static class Request {
        // one instance, reuse
        private static final HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        private Request() {
            super();
        }

        public static String get(String route, StringMap query) throws IOException, InterruptedException {
            StringBuilder urlStr = new StringBuilder()
                    .append(Translate.API)
                    .append(route == null ? "/" : route);

            if (query.size > 0) {
                urlStr.append("?");
                ObjectMap.Keys<String> keys = query.keys();
                for (String key : keys) {
                    urlStr.append(key)
                            .append("=")
                            .append(URLEncoder.encode(query.get(key), StandardCharsets.UTF_8))
                            .append("&");
                }
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlStr.toString()))
                    .setHeader("User-Agent", "King-BR/MindustryBR-plugin")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }
}
