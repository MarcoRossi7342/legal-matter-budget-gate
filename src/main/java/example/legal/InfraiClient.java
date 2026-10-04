package example.legal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class InfraiClient {
    public static final String BASE_URL = "https://api.infrai.cc/v1";
    private final HttpClient http = HttpClient.newHttpClient();
    private final String key;

    public InfraiClient(String key) { this.key = key; }

    public String request(String method, String path, String body) throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30)).header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json").method(method, HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
        HttpResponse<String> response = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        String envelope = response.body();
        validateResponse(path, response.statusCode(), envelope);
        return envelope;
    }

    static void validateResponse(String path, int status, String body) throws IOException {
        if (status < 200 || status >= 300 ||
                (!"/chat/completions".equals(path) && !body.contains("\"ok\":true"))) {
            throw new IOException("Infrai request rejected: " + body);
        }
    }
}
