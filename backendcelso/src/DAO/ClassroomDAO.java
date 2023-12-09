package DAO;

import Domain.classroom.Classroom;
import Domain.classroom.CreateClassroom;
import Domain.discipline.Discipline;
import Domain.enums.Days;
import Domain.enums.Shift;
import Domain.teacher.Teacher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDAO {


    public static void createClassroomTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS classroom (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "days_of_week VARCHAR(10) NOT NULL," +
                "shift VARCHAR(10) NOT NULL," +
                "schedule VARCHAR(255) NOT NULL," +
                "teacher_id INT NOT NULL," +
                "discipline_id INT NOT NULL," +
                "FOREIGN KEY (teacher_id) REFERENCES teacher(id)," +
                "FOREIGN KEY (discipline_id) REFERENCES discipline(id)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }


    public void createClassroom(Connection connection, CreateClassroom createClassroom) throws SQLException {
        // Chama o método para criar a tabela, se ainda não existir
        createClassroomTable(connection);

        String sql = "INSERT INTO classroom (days_of_week, shift, schedule, teacher_id, discipline_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, createClassroom.daysOfWeek().toString());
            statement.setString(2, createClassroom.shift().toString());
            statement.setString(3, createClassroom.schedule());
            statement.setInt(4, createClassroom.teacher().getId());
            statement.setInt(5, createClassroom.discipline().getId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar a sala de aula, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("Sala de aula criada com o ID:" +generatedId);
                    Classroom createdClassroom = new Classroom(generatedId, createClassroom.daysOfWeek(), createClassroom.shift(),
                            createClassroom.schedule(), createClassroom.teacher().getId(), createClassroom.discipline().getId());

                } else {
                    throw new SQLException("Falha ao criar a sala de aula, nenhum ID obtido.");
                }
            }
        }
    }

    // Método para obter uma sala de aula pelo ID
    public Classroom getClassroomById(Connection connection, int classroomId) throws SQLException {
        String sql = "SELECT classroom.*, teacher.name AS teacher_name, discipline.name AS discipline_name " +
                "FROM classroom " +
                "JOIN teacher ON classroom.teacher_id = teacher.id " +
                "JOIN discipline ON classroom.discipline_id = discipline.id " +
                "WHERE classroom.id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classroomId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToClassroom(resultSet);
                }
            }
        }

        return null;
    }

    // Método para obter todas as salas de aula
    public List<Classroom> getAllClassrooms(Connection connection) throws SQLException {
        String sql = "SELECT classroom.*, teacher.name AS teacher_name, discipline.name AS discipline_name " +
                "FROM classroom " +
                "JOIN teacher ON classroom.teacher_id = teacher.id " +
                "JOIN discipline ON classroom.discipline_id = discipline.id";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<Classroom> classrooms = new ArrayList<>();

            while (resultSet.next()) {
                Classroom classroom = mapResultSetToClassroom(resultSet);
                classrooms.add(classroom);
            }

            return classrooms;
        }
    }


    // Método para atualizar informações de uma sala de aula
    public void updateClassroom(Connection connection, int classroomId, CreateClassroom createClassroom) throws SQLException {
        String sql = "UPDATE classroom SET days_of_week = ?, shift = ?, schedule = ?, teacher_id = ?, discipline_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, createClassroom.daysOfWeek().toString());
            statement.setString(2, createClassroom.shift().toString());
            statement.setString(3, createClassroom.schedule());
            statement.setInt(4, createClassroom.teacher().getId());
            statement.setInt(5, createClassroom.discipline().getId());
            statement.setInt(6, classroomId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar a sala de aula, nenhuma sala de aula encontrada com o ID: " + classroomId);
            }
        }
    }

    // Método para excluir uma sala de aula pelo ID
    public void deleteClassroom(Connection connection, int classroomId) throws SQLException {
        String sql = "DELETE FROM classroom WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, classroomId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao excluir a sala de aula, nenhuma sala de aula encontrada com o ID: " + classroomId);
            }
        }
    }

    // Método auxiliar para mapear um ResultSet para um objeto Classroom
    private Classroom mapResultSetToClassroom(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Days daysOfWeek = Days.valueOf(resultSet.getString("days_of_week"));
        Shift shift = Shift.valueOf(resultSet.getString("shift"));
        String schedule = resultSet.getString("schedule");
        int teacherId = resultSet.getInt("teacher_id");
        int disciplineId = resultSet.getInt("discipline_id");

        Teacher teacher = new TeacherDAO().getTeacherById(ConnectionDB.getConnection(), teacherId);
        Discipline discipline = new DisciplineDAO().getDisciplineById(ConnectionDB.getConnection(), disciplineId);

        return new Classroom(id, daysOfWeek, shift, schedule, teacherId, disciplineId);
    }
}
