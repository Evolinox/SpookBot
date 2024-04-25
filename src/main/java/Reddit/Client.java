package Reddit;

import java.net.http.HttpClient;
import java.time.Duration;

public class Client {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(Client.timeoutSeconds))
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    final static String AUTH_URL = "https://www.reddit.com";

    final static int timeoutSeconds = 15;

    public static HttpClient getHttpClient() {
        return httpClient;
    }
}
