import HTTP.HttpTaskServer;
import HTTP.KVServer;
import com.google.gson.Gson;
import manager.taskmanager.HttpTaskManager;
import model.EpicTask;
import model.SimpleTask;
import model.StatusTask;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;
    HttpTaskServer server;
    HttpClient client;
    Gson gson;


    @BeforeEach
    public void createServers() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.start();
        client = HttpClient.newHttpClient();
        gson = new Gson();
        taskManager = server.getTaskManager();
    }

    @AfterEach
    public void closeServers() {
        kvServer.stop();
        server.stop();
    }

    @Test
    public void postNewSimple() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSimpleTask().size(), 1);

        json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(response1.body(), "Задача пересекается по времени с другими и не может быть добавлена");
    }

    @Test
    public void postNewEpicTask() throws IOException, InterruptedException {
        URI url8 = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("NEW0", "NEW", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url8).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 1, "Эпик не добавился");
        assertEquals(taskManager.getEpicTask().get(0).getStatusTask(), StatusTask.NEW,
                "Не верный расчет статуса Эпика");
    }

    @Test
    public void postNewSubtask() throws IOException, InterruptedException {
        URI url4 = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        URI url5 = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSubtask().size(), 1, "Подзадача не добавилась");

        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body15 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request15 = HttpRequest.newBuilder().uri(url5).POST(body15).build();
        HttpResponse<String> response15 = client.send(request15, HttpResponse.BodyHandlers.ofString());

        assertEquals(response15.body(), "Задача пересекается по времени с другими и не может быть добавлена");
    }

    @Test
    public void endpointDeleteAllSimple() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15)));
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSimpleTask().size(), 0, "Задачи не удалились");
        assertEquals(response2.body(), "Простые задачи очищены", "Неверный запрос");
    }

    @Test
    public void endpointDeleteAllEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 0, "Многосоставные задачи не удалились");
        assertEquals(taskManager.getSubtask().size(), 0, "Задачи не удалились");
        assertEquals(response.body(), "Многосоставные задачи очищены", "Неверный запрос");
    }

    @Test
    public void endpointDeleteAllSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 1, "Многосоставные задачи удалились");
        assertEquals(taskManager.getSubtask().size(), 0, "Задачи не удалились");
        assertEquals(response.body(), "Подзадачи успешно очищены", "Неверный запрос");
    }

    @Test
    public void endpointGetSimpleTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15)));
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSimpleTask().size(), 2, "Задачи не добавились");
        assertEquals(response2.body(), gson.toJson(taskManager.getIdSimple(2)), "Не верный возврат задачи");

        url = URI.create("http://localhost:8080/tasks/simple/?id=3");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "null", "Неверный возврат задачи");
    }

    @Test
    public void endpointGetEpicTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 1, "Задачи не добавились");
        assertEquals(response2.body(), gson.toJson(taskManager.getIdEpic(1)),
                "Не верный возврат задачи");

        url = URI.create("http://localhost:8080/tasks/epic/?id=5");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "null", "Неверный возврат задачи");
    }

    @Test
    public void endpointGetSubTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 1));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSubtask().size(), 2, "Задачи не добавились");
        assertEquals(response2.body(), gson.toJson(taskManager.getIdSub(2)),
                "Не верный возврат задачи");

        url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "null", "Неверный возврат задачи");
    }

    @Test
    public void endpointDeleteSimpleTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15)));
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSimpleTask().size(), 1, "Задачи не удалились");

        url = URI.create("http://localhost:8080/tasks/simple/?id=3");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "Вы пытались удалить задачу с неверным идентификатором",
                "Неверное удаление задачи");
    }

    @Test
    public void endpointDeleteEpicTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 0, "Многосоставная задача не удалились");
        assertEquals(taskManager.getSubtask().size(), 0, "Задачи не удалились");

        url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "Вы пытались удалить задачу с неверным идентификатором",
                "Неверное удаление задачи");
    }

    @Test
    public void endpointDeleteSubTaskById() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 1));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 1, "Многосоставная задача не удалились");
        assertEquals(taskManager.getSubtask().size(), 1, "Задачи не удалились");

        url = URI.create("http://localhost:8080/tasks/subtask/?id=5");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), "Вы пытались удалить задачу с неверным идентификатором",
                "Неверное удаление задачи");
    }

    @Test
    public void endpointGetSubOfEpic() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 1));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        assertEquals(response3.body(), gson.toJson(taskManager.getSubOfEpicTask(1)));
    }

    @Test
    public void endpointUpdateSimpleTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/");
        String json1 = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSimpleTask().size(), 1, "Неверное обновление задачи");
        assertEquals(taskManager.getSimpleTask().get(0).getNameTask(), "NEW0",
                "Неверное обновление задачи");
    }

    @Test
    public void endpointUpdateEpicTask() throws IOException, InterruptedException {
        URI url4 = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url4).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getEpicTask().size(), 1, "Неверное обновление задачи");
        assertEquals(taskManager.getEpicTask().get(0).getNameTask(), "NEWEPIC0",
                "Неверное обновление задачи");
    }

    @Test
    public void endpointUpdateSubTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 1));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        assertEquals(taskManager.getSubtask().size(), 1, "Неверное обновление задачи");
        assertEquals(taskManager.getSubtask().get(0).getNameTask(), "1",
                "Неверное обновление задачи");
    }

    @Test
    public void endpointGetSimpleTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15)));
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());


        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(response2.body(), gson.toJson(taskManager.getSimpleTask()), "Задачи не совпадают");
    }

    @Test
    public void endpointGetEpicTask() throws IOException, InterruptedException {
        URI url4 = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 9, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url4).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(response2.body(), gson.toJson(taskManager.getEpicTask()), "Задачи не совпадают");
    }

    @Test
    public void endpointGetSubTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 1));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        System.out.println(response5.body());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 1));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(response2.body(), gson.toJson(taskManager.getSubtask()), "Задачи не совпадают");
    }

    @Test
    public void endpointGetPrioritizedTask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        URI url7 = URI.create("http://localhost:8080/tasks/");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());

        assertEquals(response7.body(), gson.toJson(taskManager.getPrioritizedTasks()), "Задачи не совпадают");
    }

    @Test
    public void endpointGetHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/?id=57");
        HttpRequest request10 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response10 = client.send(request10, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request11 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());

        assertEquals(response11.body(), gson.toJson(taskManager.getHistoryManager()),
                "Неверная история вызовов задач");
    }

    @Test
    public void saveAndLoadHttpTaskManagerNormal() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/?id=1");
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/simple/?id=57");
        HttpRequest request10 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response10 = client.send(request10, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request11 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());

        HttpTaskManager newManager = HttpTaskManager
                .load(taskManager.getKvTaskClient().getAPI_token(),
                        new HttpTaskManager("http://localhost:8081/register"));
        assertEquals(newManager, taskManager);
    }

    @Test
    public void saveAndLoadHttpTaskManagerWithoutHistory() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask/");
        json = gson.toJson(new Subtask("0", "SUB0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(7), 2));
        final HttpRequest.BodyPublisher body5 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request5 = HttpRequest.newBuilder().uri(url).POST(body5).build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new Subtask("1", "SUB1", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(15), 2));
        final HttpRequest.BodyPublisher body6 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request6 = HttpRequest.newBuilder().uri(url).POST(body6).build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());

        HttpTaskManager newManager = HttpTaskManager
                .load(taskManager.getKvTaskClient().getAPI_token(),
                        new HttpTaskManager("http://localhost:8081/register"));
        assertEquals(newManager, taskManager);
    }

    @Test
    public void saveAndLoadHttpTaskManagerWithoutSubtask() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/simple/");
        String json = gson.toJson(new SimpleTask("Test0", "TestDesc0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new SimpleTask("NEW0", "NEW", 1, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body8 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request8 = HttpRequest.newBuilder().uri(url).POST(body8).build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());

        json = gson.toJson(new EpicTask("EPIC0", "EPIC0", 0, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body4 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url).POST(body4).build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic/");
        json = gson.toJson(new EpicTask("NEWEPIC0", "NEWEPIC0", 2, StatusTask.NEW,
                Duration.ofMinutes(5), LocalDateTime.now()));
        final HttpRequest.BodyPublisher body9 = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request9 = HttpRequest.newBuilder().uri(url).POST(body9).build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());

        HttpTaskManager newManager = HttpTaskManager
                .load(taskManager.getKvTaskClient().getAPI_token(),
                        new HttpTaskManager("http://localhost:8081/register"));
        assertEquals(newManager, taskManager);
    }

    @Test
    public void saveAndLoadWrongWay() {
        IllegalArgumentException exp = assertThrows(
                IllegalArgumentException.class,
                () -> HttpTaskManager
                        .load(taskManager.getKvTaskClient().getAPI_token(),
                                new HttpTaskManager("fhjdfhdfjjdfj"))
        );
    }
}
