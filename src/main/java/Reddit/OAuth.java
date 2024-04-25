package Reddit;

import SpookBot.Main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth {
    private static String authToken = null;
    public static String getAuthToken(String _userName, String _passWord) throws IOException, InterruptedException {
        // Check, if a OAuthToken has already been requested
        if (authToken != null) {
            Main.loggingService.info("OAuthToken already loaded");
            return authToken;
        }

        // Set HTTP Request Parameter
        final Map<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("username", _userName);
        params.put("password", _passWord);

        // Building the API Endpoint URL
        final var method = "api/v1/access_token";
        final var requestUrl = String.format("%s/%s", Client.AUTH_URL, method);

        // Building the Request Form
        final String form = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        // Building the HTTP Request
        final HttpRequest request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .headers("User-Agent", "java:com.evolinox.SpookBot.reddit:v1.1 (by /u/Evolinox)")
                .header("Authorization", basicAuth(_userName, _passWord))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .uri(URI.create(requestUrl))
                .timeout(Duration.ofSeconds(Client.timeoutSeconds))
                .build();

        final HttpClient client = Client.getHttpClient();

        final HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return null;
    }

    private static String basicAuth(final String username, final String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
