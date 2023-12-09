package DAO;

import Domain.discipline.CreateDiscipline;
import Domain.discipline.Discipline;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplineDAO {
    public static void createTableDiscipline(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS discipline (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) NOT NULL" +
                ")";
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
        }
    }

    public void createDiscipline(Connection connection, CreateDiscipline createDiscipline) throws SQLException{

        createTableDiscipline(connection);

        String sql = "INSERT INTO discipline (name) VALUES (?)";

        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, createDiscipline.name());

            int affectedRows = statement.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Falha ao criar a disciplina, nenhuma linha afetada");
            }

            try(ResultSet generetadKeys = statement.getGeneratedKeys()){
                if(generetadKeys.next()){
                    int generatedID = generetadKeys.getInt(1);
                    Discipline createdDiscipline = new Discipline(generatedID, createDiscipline.name());
                }else {
                    throw new SQLException("Falha ao criar a disciplina, nenhum ID obtido");
                }
            }
        }
    }

    public Discipline getDisciplineById(Connection connection, int disciplineId) throws SQLException{
        String sql = "SELECT * FROM discipline WHERE id = ?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, disciplineId);

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return mapResultSetToDiscipline(resultSet);
                }
            }
        }
        return null;
    }


    public List<Discipline> getAllDisciplines(Connection connection) throws SQLException{
        String sql = "SELECT * FROM discipline";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            List<Discipline> discplines = new ArrayList<>();

            while (resultSet.next()) {
                Discipline discipline = mapResultSetToDiscipline(resultSet);
                discplines.add(discipline);
            }

            return discplines;
        }
    }

    public void updateDiscipline(Connection connection, int disciplineId, CreateDiscipline createDiscipline) throws SQLException {
        String sql = "UPDATE discipline SET name = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, createDiscipline.name());
            statement.setInt(2, disciplineId);  // Corrigido para índice 2

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar a disciplina, nenhuma disciplina encontrada com o ID: " + disciplineId);
            }
        }
    }



    public void deleteDiscipline(Connection connection, int disciplineId) throws SQLException {
        String sql = "DELETE FROM discipline WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, disciplineId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao excluir a disciplina, nenhuma disciplina encontrada com o ID: " + disciplineId);
            }
        }
    }


    private Discipline mapResultSetToDiscipline(ResultSet resultSet) throws SQLException{
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");

        return new Discipline(id, name);
    }
}
