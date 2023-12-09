package Controller;

import Domain.teacher.CreateTeacher;
import Domain.teacher.Teacher;
import Service.TeacherService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import com.google.gson.Gson;


public class TeacherController implements HttpHandler {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {

        this.teacherService = teacherService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET":
                    handleGetRequest(path, exchange);
                    break;
                case "POST":
                    handlePostRequest(path, exchange);
                    break;
                case "PUT":
                    handlePutRequest(path, exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(path, exchange);
                    break;
                default:
                    sendResponse(exchange, "Método não suportado", 405);
            }
        } catch (SQLException e) {
            sendResponse(exchange,  e.toString(), 500);
        }
    }

    private void handleGetRequest(String path, HttpExchange exchange) throws IOException, SQLException {
        if (path.equals("/api/teachers")) {

            List<Teacher> teachers = teacherService.getAllTeachers();


            Gson gson = new Gson();
            String response = gson.toJson(teachers);

            sendResponse(exchange, response);
        } else {
            int teacherId = extractIdFromPath(path);
            if (teacherId != -1) {
                Teacher teacher = teacherService.getTeacherById(teacherId);
                if (teacher != null) {
                    Gson gson = new Gson();
                    String response = gson.toJson(teacher);

                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Professor não encontrado", 404);
                }
            } else {
                sendResponse(exchange, "Solicitação inválida", 400);
            }
        }
    }


    private void handlePostRequest(String path, HttpExchange exchange) throws IOException, SQLException {
        if (path.equals("/api/teachers")) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateTeacher createTeacher = parseCreateTeacherFromRequest(requestBody);

            teacherService.createTeacher(createTeacher);
            sendResponse(exchange, "Professor criado com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }

    private void handlePutRequest(String path, HttpExchange exchange) throws IOException, SQLException {
        // Atualizar um professor por ID
        int teacherId = extractIdFromPath(path);
        if (teacherId != -1) {
            // Exemplo: Analisar os parâmetros do corpo da solicitação para atualizar o professor
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateTeacher createTeacher = parseCreateTeacherFromRequest(requestBody);

            teacherService.updateTeacher(teacherId, createTeacher);
            sendResponse(exchange, "Professor atualizado com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }

    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException, SQLException {
        // Excluir um professor por ID
        int teacherId = extractIdFromPath(path);
        if (teacherId != -1) {
            teacherService.deleteTeacher(teacherId);
            sendResponse(exchange, "Professor excluído com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }

    private int extractIdFromPath(String path) {
        try {
            String[] segments = path.split("/");
            return Integer.parseInt(segments[segments.length - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    private CreateTeacher parseCreateTeacherFromRequest(String requestBody) {
        Gson gson = new Gson();
        return gson.fromJson(requestBody, CreateTeacher.class);
    }


    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }
}
