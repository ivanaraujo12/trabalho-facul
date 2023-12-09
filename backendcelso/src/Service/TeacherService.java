package Service;
import DAO.ConnectionDB;
import DAO.TeacherDAO;
import Domain.teacher.CreateTeacher;
import Domain.teacher.Teacher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
public class TeacherService {
    private final TeacherDAO teacherDAO;

    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
    }

    // Método para criar um professor
    public void createTeacher(CreateTeacher createTeacher) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            teacherDAO.createTeacher(connection, createTeacher);
        }
    }

    // Método para obter um professor pelo ID
    public Teacher getTeacherById(int teacherId) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            return teacherDAO.getTeacherById(connection, teacherId);
        }
    }

    // Método para obter todos os professores
    public List<Teacher> getAllTeachers() throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            return teacherDAO.getAllTeachers(connection);
        }
    }

    // Método para atualizar informações de um professor
    public void updateTeacher(int teacherId, CreateTeacher createTeacher) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            teacherDAO.updateTeacher(connection, teacherId, createTeacher);
        }
    }

    // Método para excluir um professor pelo ID
    public void deleteTeacher(int teacherId) throws SQLException {
        try (Connection connection = ConnectionDB.getConnection()) {
            teacherDAO.deleteTeacher(connection, teacherId);
        }
    }
}
