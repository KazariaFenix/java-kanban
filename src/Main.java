import HTTP.HttpTaskServer;
import HTTP.KVServer;
import com.google.gson.Gson;
import manager.Managers;
import manager.taskmanager.HttpTaskManager;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        new HttpTaskServer();
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();

        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().headers("Content-Type", "application/json").version(HttpClient.Version.HTTP_2)
                .uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url8 = URI.create("http://localhost:8080/tasks/simple/");
        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url8).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        URI url4 = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        URI url9 = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url9).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        URI url5 = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        URI url6 = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());


        URI url1 = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        URI url3 = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        URI url7 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());

        System.out.println(response6.body());
        System.out.println(response1.body());
        System.out.println(response2.body());
        System.out.println(response3.body());
        System.out.println(response7.body());


    }
}

