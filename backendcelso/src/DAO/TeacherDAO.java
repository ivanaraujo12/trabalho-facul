package DAO;
import Domain.teacher.CreateTeacher;
import Domain.teacher.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class TeacherDAO {

    public static void createTeacherTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS teacher (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) NOT NULL," +
                "email VARCHAR(255) NOT NULL" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }
    public void createTeacher(Connection connection, CreateTeacher createTeacher) throws SQLException {
        createTeacherTable(connection);

        String sql = "INSERT INTO teacher (name, email) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, createTeacher.name());
            statement.setString(2, createTeacher.email());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar o professor, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    Teacher createdTeacher = new Teacher(generatedId, createTeacher.name(), createTeacher.email());
                    // faça o que precisar com o objeto Teacher criado
                } else {
                    throw new SQLException("Falha ao criar o professor, nenhum ID obtido.");
                }
            }

        }
    }


    public Teacher getTeacherById(Connection connection, int teacherId) throws SQLException {
        String sql = "SELECT * FROM teacher WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teacherId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToTeacher(resultSet);
                }
            }
        }

        return null;
    }


    public List<Teacher> getAllTeachers(Connection connection) throws SQLException {
        String sql = "SELECT * FROM teacher";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<Teacher> teachers = new ArrayList<>();

            while (resultSet.next()) {
                Teacher teacher = mapResultSetToTeacher(resultSet);
                teachers.add(teacher);
            }

            return teachers;
        }
    }


    public void updateTeacher(Connection connection, int teacherId, CreateTeacher createTeacher) throws SQLException {
        String sql = "UPDATE teacher SET name = ?, email = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, createTeacher.name());
            statement.setString(2, createTeacher.email());
            statement.setInt(3, teacherId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar o professor, nenhum professor encontrado com o ID: " + teacherId);
            }
        }
    }


    public void deleteTeacher(Connection connection, int teacherId) throws SQLException {
        String sql = "DELETE FROM teacher WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, teacherId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao excluir o professor, nenhum professor encontrado com o ID: " + teacherId);
            }
        }
    }

    // Método auxiliar para mapear um ResultSet para um objeto Teacher
    private Teacher mapResultSetToTeacher(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");

        return new Teacher(id, name, email);
    }

}
