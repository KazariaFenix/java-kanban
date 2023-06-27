package HTTP;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    String API_token;
    Gson gson = new Gson();
    HttpClient client;
    URI kvUri;
    public KVTaskClient(URI uri) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/register"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_token = response.body();
        kvUri = uri;
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest requestSave = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(kvUri + "/save/" + key + "?API_TOKEN="))
                .header("Content-Type", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        HttpResponse<String> responseLoad = client.send(requestSave, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        HttpRequest requestLoad = HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .uri(URI.create(kvUri + "/load/" + key + "?API_TOKEN="))
                .build();
        HttpResponse<String> responseLoad = client.send(requestLoad, HttpResponse.BodyHandlers.ofString());
        return responseLoad.body();
    }

    public String getAPI_token() {
        return API_token;
    }
}
