package Controller;

import Domain.discipline.CreateDiscipline;
import Domain.discipline.Discipline;
import Domain.teacher.CreateTeacher;
import Service.DisciplineService;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class DisciplineController implements HttpHandler{

    private final DisciplineService disciplineService;

    public DisciplineController(DisciplineService disciplineService){
        this.disciplineService = disciplineService;
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
        if(path.equals("/api/disciplines")){
            List<Discipline> disciplines = disciplineService.getAllDisciplines();

            Gson gson = new Gson();
            String response = gson.toJson(disciplines);

            sendResponse(exchange, response);
        } else {
            int disciplineId = extractIdFromPath(path);
            if(disciplineId != -1){
                Discipline discipline = disciplineService.getDisciplineById(disciplineId);
                if(discipline != null){
                    Gson gson = new Gson();
                    String response = gson.toJson(discipline);

                    sendResponse(exchange, response);
                } else {
                    sendResponse(exchange, "Discipline não encontrada ou cadastrada");
                }
            } else {
                sendResponse(exchange, "Solicitação inválida");
            }
        }
    }

    private void handlePostRequest(String path, HttpExchange exchange) throws IOException, SQLException{
        if (path.equals("/api/disciplines")) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateDiscipline createDiscipline = parseCreateDisciplineFromRequest(requestBody);

            disciplineService.createDiscipline(createDiscipline);
            sendResponse(exchange, "Disciplina criado com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }

    private void handlePutRequest(String path, HttpExchange exchange) throws IOException, SQLException {

        int disciplineId = extractIdFromPath(path);
        if (disciplineId != -1) {

            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            CreateDiscipline createDiscipline = parseCreateDisciplineFromRequest(requestBody);

            disciplineService.updateDiscipline(disciplineId, createDiscipline);
            sendResponse(exchange, "Disciplina atualizada com sucesso");
        } else {
            sendResponse(exchange, "Solicitação inválida", 400);
        }
    }
    private void handleDeleteRequest(String path, HttpExchange exchange) throws IOException, SQLException {

        int disciplineId = extractIdFromPath(path);
        if (disciplineId != -1) {
            disciplineService.deleteDiscipline(disciplineId);
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

    private CreateDiscipline parseCreateDisciplineFromRequest(String requestBody) {
        Gson gson = new Gson();
        return gson.fromJson(requestBody, CreateDiscipline.class);
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
