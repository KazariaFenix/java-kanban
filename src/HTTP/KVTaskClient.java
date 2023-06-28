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

    public KVTaskClient(URI uri) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_token = response.body();
        System.out.println("Получен API_TOKEN: " + API_token);
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        HttpRequest requestSave = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("http://localhost:8081/save/" + key + "/?API_TOKEN=" + key))
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
                .uri(URI.create("http://localhost:8081/load/" + key + "/?API_TOKEN=" + key))
                .build();
        HttpResponse<String> responseLoad = client.send(requestLoad, HttpResponse.BodyHandlers.ofString());
        return responseLoad.body();
    }

    public String getAPI_token() {
        return API_token;
    }
}
