package ro.csie.gestiunesali.db;

import ro.csie.gestiunesali.model.Profesor;
import ro.csie.gestiunesali.model.Rezervare;
import ro.csie.gestiunesali.model.Sala;
import ro.csie.gestiunesali.observer.ConsoleLogger;
import ro.csie.gestiunesali.observer.EmailNotifier;
import ro.csie.gestiunesali.observer.Subject;
import ro.csie.gestiunesali.singletone.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RezervariRepository extends Subject {
    private volatile static RezervariRepository instance;
    private final List<Rezervare> rezervari = new ArrayList<>();

    private RezervariRepository() {
        loadFromDatabase();
        addObserver(new EmailNotifier());
        addObserver(new ConsoleLogger());
    }

    public static RezervariRepository getInstance() {
        if (instance == null) {
            synchronized (RezervariRepository.class) {
                if (instance == null) {
                    instance = new RezervariRepository();
                }
            }
        }
        return instance;
    }

    private void loadFromDatabase() {
        rezervari.clear();

        String sql = "SELECT profesor_id, sala_nume, inceput, sfarsit FROM rezervari";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String profesorId = resultSet.getString("profesor_id");
                String salaNume = resultSet.getString("sala_nume");
                LocalDateTime inceput = resultSet.getTimestamp("inceput").toLocalDateTime();
                LocalDateTime sfarsit = resultSet.getTimestamp("sfarsit").toLocalDateTime();

                Profesor profesor = ProfesoriRepository.getInstance().getProfesori().stream()
                        .filter(p -> p.getId().equals(profesorId))
                        .findFirst()
                        .orElse(null);

                Sala sala = SalaRepository.getInstance().getSali().stream()
                        .filter(s -> s.getNume().equals(salaNume))
                        .findFirst()
                        .orElse(null);

                if (profesor != null && sala != null) {
                    Rezervare rezervare = new Rezervare(profesor, sala, inceput, sfarsit);
                    rezervari.add(rezervare);
                }
            }

            System.out.println("Datele despre rezervari au fost incarcate din baza de date.");

        } catch (SQLException e) {
            System.err.println("Eroare la incarcarea rezervarilor din baza de date: " + e.getMessage());
        }
    }

    public void adaugaRezervare(Rezervare rezervare) {
        if (esteSalaRezervataInInterval(rezervare.getSala(), rezervare.getInceput(), rezervare.getSfarsit())) {
            System.out.println("Sala " + rezervare.getSala().getNume() + " este deja rezervata in acest interval.");
            return;
        }

        rezervari.add(rezervare);

        String sql = "INSERT INTO rezervari (profesor_id, sala_nume, inceput, sfarsit) VALUES (?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, rezervare.getProfesor().getId());
            preparedStatement.setString(2, rezervare.getSala().getNume());
            preparedStatement.setString(3, rezervare.getInceput().format(formatter));
            preparedStatement.setString(4, rezervare.getSfarsit().format(formatter));

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Rezervare adaugata cu succes in baza de date: " + rezervare);
                notifyObservers("Rezervarea a fost adaugata cu succes.");
            } else {
                System.out.println("Rezervarea nu a putut fi adaugata in baza de date.");
                rezervari.remove(rezervare);
            }

        } catch (SQLException e) {
            System.err.println("Eroare la adaugarea rezervarii in baza de date: " + e.getMessage());
            rezervari.remove(rezervare);
        }
    }

    public void elibereazaRezervare(Rezervare rezervare) {
        rezervari.remove(rezervare);

        // stergere din baza de date
        String sql = "DELETE FROM rezervari WHERE profesor_id = ? AND sala_nume = ? AND inceput = ? AND sfarsit = ?";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, rezervare.getProfesor().getId());
            preparedStatement.setString(2, rezervare.getSala().getNume());
            preparedStatement.setString(3, rezervare.getInceput().format(formatter));
            preparedStatement.setString(4, rezervare.getSfarsit().format(formatter));

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Rezervare stearsa cu succes din baza de date: " + rezervare);
            } else {
                System.out.println("Rezervarea nu a fost gasita in baza de date.");
            }

        } catch (SQLException e) {
            System.err.println("Eroare la stergerea rezervarii din baza de date: " + e.getMessage());
        }
    }

    public boolean esteSalaRezervataInInterval(Sala sala, LocalDateTime inceput, LocalDateTime sfarsit) {
        return rezervari.stream().anyMatch(rezervare ->
                rezervare.getSala().equals(sala) &&
                        rezervare.getInceput().isBefore(sfarsit) &&
                        rezervare.getSfarsit().isAfter(inceput));
    }

    public List<Rezervare> getRezervari() {
        return rezervari;
    }

    public List<Rezervare> getRezervariPentruSala(Sala sala) {
        return rezervari.stream()
                .filter(rezervare -> rezervare.getSala().getNume().equals(sala.getNume()))
                .collect(Collectors.toList());
    }

    public List<Rezervare> getRezervariPentruProfesor(Profesor profesor) {
        return rezervari.stream()
                .filter(rezervare -> rezervare.getProfesor().getId().equals(profesor.getId()))
                .collect(Collectors.toList());
    }
}

