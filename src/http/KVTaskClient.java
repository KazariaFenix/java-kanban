package http;

import com.google.gson.Gson;
import manager.exception.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String API_token;
    private Gson gson = new Gson();
    private HttpClient client;
    private final String uri = "http://localhost:8081/";

    public KVTaskClient(URI uri) {
        client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Код ответа сервера " + response.statusCode()
                        + ". Произошла ошибка при регистрации менеджера");
            }
            API_token = response.body();
            System.out.println("Получен API_TOKEN: " + API_token);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при регистрации менеджера");
        }
    }

    public void put(String key, String json) {
        HttpRequest requestSave = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(uri + "save/" + key + "/?API_TOKEN=" + API_token))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> responseSave = client.send(requestSave, HttpResponse.BodyHandlers.ofString());
            if (responseSave.statusCode() != 200) {
                throw new ManagerSaveException("Код ответа сервера " + responseSave.statusCode()
                        + ". Произошла ошибка при сохранении данных");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }

    public String load(String key) {
        HttpRequest requestLoad = HttpRequest.newBuilder()
                .GET()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(uri + "load/" + key + "/?API_TOKEN=" + API_token))
                .build();
        try {
            HttpResponse<String> responseLoad = client.send(requestLoad, HttpResponse.BodyHandlers.ofString());
            if (responseLoad.statusCode() != 200) {
                throw new ManagerSaveException("Код ответа сервера " + responseLoad.statusCode()
                        + ". Произошла ошибка при загрузке данных");
            }
            return responseLoad.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных");
        }
    }
}
