package Controller;

import Domain.classroom.Classroom;
import Domain.classroom.CreateClassroom;
import Domain.discipline.CreateDiscipline;
import Domain.discipline.Discipline;
import Service.ClassroomService;
import Service.DisciplineService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class ClassroomController implements HttpHandler {
    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService){
        this.classroomService = classroomService;
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

    private void handleGetRequest(String path, HttpExchange exchange) throws IOException, SQLException{
        if(path.equals("/api/classrooms")){
            List<Classroom> classrooms = classroomService.getAllClassrooms();

            Gson gson = new Gson();
            String response = gson.toJson(classrooms);

            sendResponse(exchange, response);
        } else {
            int classroomId = extractIdFromPath(path);
            if(classroomId != -1){
                Classroom classroom = classroomService.getClassroomById(classroomId);
                if(classroom != null){
                    Gson gson = new Gson();
                    String response = gson.toJson(classroom);

                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Sala não encontrada ou cadastrada");
                }
            } else {
                sendResponse(exchange, "Solicitação inválida");
            }
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException, SQLException{
        if (path.equals("/api/classrooms")) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateClassroom createClassroom = parseCreateClassroomFromRequest(requestBody);

            classroomService.createClassroom(createClassroom);
            sendResponse(exchange, "Disciplina criado com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }

    private void handlePutRequest(String path, HttpExchange exchange) throws IOException, SQLException {

        int classroomId = extractIdFromPath(path);
        if (classroomId != -1) {

            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateClassroom createClassroom = parseCreateClassroomFromRequest(requestBody);

            classroomService.updateClassroom(classroomId, createClassroom);
            sendResponse(exchange, "Disciplina atualizada com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }
    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException, SQLException {

        int classroomId = extractIdFromPath(path);
        if (classroomId != -1) {
            classroomService.deleteClassroom(classroomId);
            sendResponse(exchange, "Disciplina excluída com sucesso");
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

    private CreateClassroom parseCreateClassroomFromRequest(String requestBody) {
        Gson gson = new Gson();
        return gson.fromJson(requestBody, CreateClassroom.class);
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
