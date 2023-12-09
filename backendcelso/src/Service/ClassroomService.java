package Service;

import DAO.ClassroomDAO;
import DAO.ConnectionDB;
import Domain.classroom.Classroom;
import Domain.classroom.CreateClassroom;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ClassroomService {
    private final ClassroomDAO classroomDAO;

    public ClassroomService() {
        this.classroomDAO = new ClassroomDAO();
    }

    // Método para criar uma sala de aula
    public void createClassroom(CreateClassroom createClassroom) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            classroomDAO.createClassroom(connection, createClassroom);
        }
    }

    // Método para obter uma sala de aula pelo ID
    public Classroom getClassroomById(int classroomId) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            return classroomDAO.getClassroomById(connection, classroomId);
        }
    }

    // Método para obter todas as salas de aula
    public List<Classroom> getAllClassrooms() throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            return classroomDAO.getAllClassrooms(connection);
        }
    }

    // Método para atualizar informações de uma sala de aula
    public void updateClassroom(int classroomId, CreateClassroom createClassroom) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            classroomDAO.updateClassroom(connection, classroomId, createClassroom);
        }
    }

    // Método para excluir uma sala de aula pelo ID
    public void deleteClassroom(int classroomId) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            classroomDAO.deleteClassroom(connection, classroomId);
        }
    }
}
