package Timetable;

import SpookBot.Main;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public class Request {
    public static JSONObject getTimetable(String evaNo, String date, String time) throws IOException, InterruptedException {
        // API Endpoint
        final String dbApiUrl = "https://apis.deutschebahn.com/db-api-marketplace/apis/timetables/v1/plan/";

        // Request URL
        final String requestUrl = dbApiUrl + "/" + evaNo + "/" + date + "/" + time;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Accept", "*/*")
                .header("User-Agent", "SpookBot")
                .header("DB-Client-Id", Main.dbApiKey)
                .header("DB-Api-Key", Main.dbApiSecret)
                .header("Content-Type", "application/xml")
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return XmlToJson.ToJSON(response);
    }
}
