package ro.csie.gestiunesali.db;

import ro.csie.gestiunesali.model.Profesor;
import ro.csie.gestiunesali.singletone.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesoriRepository {
    private volatile static ProfesoriRepository instance;
    private final List<Profesor> profesori = new ArrayList<>();

    private ProfesoriRepository() {
        loadFromDatabase();
    }

    public static ProfesoriRepository getInstance() {
        if (instance == null) {
            synchronized (ProfesoriRepository.class) {
                if (instance == null) {
                    instance = new ProfesoriRepository();
                }
            }
        }
        return instance;
    }

    private void loadFromDatabase() {
        profesori.clear();
        String sql = "SELECT id, nume FROM profesori";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String nume = resultSet.getString("nume");

                Profesor profesor = new Profesor(id, nume);
                profesori.add(profesor);
            }

            System.out.println("Datele despre profesori au fost incarcate din baza de date.");

        } catch (SQLException e) {
            System.err.println("Eroare la incarcarea profesorilor din baza de date: " + e.getMessage());
        }
    }

    public void adaugaProfesor(Profesor profesor) {
        if (profesori.stream().anyMatch(p -> p.getId().equals(profesor.getId()))) {
            System.out.println("Profesorul cu ID-ul " + profesor.getId() + " exista deja.");
            return;
        }

        profesori.add(profesor);

        String sql = "INSERT INTO profesori (id, nume) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, profesor.getId());
            preparedStatement.setString(2, profesor.getNume());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Profesorul a fost adaugat cu succes in baza de date: " + profesor);
            } else {
                System.out.println("Profesorul nu a putut fi adaugat in baza de date.");
                profesori.remove(profesor); 
            }

        } catch (SQLException e) {
            System.err.println("Eroare la adaugarea profesorului in baza de date: " + e.getMessage());
            profesori.remove(profesor); 
        }
    }

    public void stergeProfesor(String profesorId) {
        Optional<Profesor> profesorDeSters = profesori.stream()
                .filter(p -> p.getId().equals(profesorId))
                .findFirst();

        if (profesorDeSters.isEmpty()) {
            System.out.println("Profesorul cu ID-ul " + profesorId + " nu exista.");
            return;
        }

        profesori.remove(profesorDeSters.get());

        String sql = "DELETE FROM profesori WHERE id = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, profesorId);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Profesorul cu ID-ul " + profesorId + " a fost sters din baza de date.");
            } else {
                System.out.println("Profesorul nu a fost gasit in baza de date.");
            }

        } catch (SQLException e) {
            System.err.println("Eroare la stergerea profesorului din baza de date: " + e.getMessage());
        }
    }

    public List<Profesor> getProfesori() {
        return new ArrayList<>(profesori);
    }
}
