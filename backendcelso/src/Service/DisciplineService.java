package Service;

import DAO.ConnectionDB;
import DAO.DisciplineDAO;
import Domain.discipline.CreateDiscipline;
import Domain.discipline.Discipline;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DisciplineService {
    private final DisciplineDAO disciplineDAO;

    public DisciplineService(){
        this.disciplineDAO = new DisciplineDAO();
    }

    //Metodo para criar uma disciplina

    public void createDiscipline(CreateDiscipline createDiscipline) throws SQLException {
        try(Connection connection = ConnectionDB.getConnection()){
            disciplineDAO.createDiscipline(connection, createDiscipline);
        }
    }

    //Metodo para obter uma disciplina por ID
    public Discipline getDisciplineById(int disciplineId) throws SQLException{
        try(Connection connection = ConnectionDB.getConnection()){
            return disciplineDAO.getDisciplineById(connection, disciplineId);
        }
    }

    //Metodo para obter todas as disciplinas
    public List<Discipline> getAllDisciplines() throws SQLException{
        try(Connection connection = ConnectionDB.getConnection()){
            return disciplineDAO.getAllDisciplines(connection);
        }
    }

    //Metodo para atualizar informações de uma disciplina
    public void updateDiscipline(int disciplineId, CreateDiscipline createDiscipline) throws SQLException{
        try(Connection connection = ConnectionDB.getConnection()){
            disciplineDAO.updateDiscipline(connection, disciplineId, createDiscipline);
        }
    }

    //Metodo para excluir uma disciplina pelo id
    public void deleteDiscipline(int disciplineId) throws SQLException{
        try(Connection connection = ConnectionDB.getConnection()){
            disciplineDAO.deleteDiscipline(connection, disciplineId);
        }
    }

}
