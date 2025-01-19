package ro.csie.gestiunesali.db;

import ro.csie.gestiunesali.enums.TipSala;
import ro.csie.gestiunesali.model.Laborator;
import ro.csie.gestiunesali.model.Sala;
import ro.csie.gestiunesali.model.SalaConferinte;
import ro.csie.gestiunesali.model.SalaCurs;
import ro.csie.gestiunesali.singletone.DatabaseConnection;
import ro.csie.gestiunesali.specification.AndSpecification;
import ro.csie.gestiunesali.specification.CapacitateMinimaSpec;
import ro.csie.gestiunesali.specification.EsteLiberaSpec;
import ro.csie.gestiunesali.specification.Specification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SalaRepository {
    private volatile static SalaRepository instance;
    private final List<Sala> sali = new ArrayList<>();

    private SalaRepository() {
        loadFromDatabase();
    }

    public static SalaRepository getInstance() {
        if (instance == null) {
            synchronized (RezervariRepository.class) {
                if (instance == null) {
                    instance = new SalaRepository();
                }
            }
        }
        return instance;
    }

    private void loadFromDatabase() {
        sali.clear();

        String sql = "SELECT nume, capacitate, tip, are_echipamente FROM sali";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String nume = resultSet.getString("nume");
                int capacitate = resultSet.getInt("capacitate");
                String tipString = resultSet.getString("tip");
                boolean areEchipamente = resultSet.getBoolean("are_echipamente");

                TipSala tipSala;
                try {
                    tipSala = TipSala.fromDbValue(tipString);
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipul salii necunoscut in baza de date: " + tipString);
                    continue;
                }

                Sala sala = switch (tipSala) {
                    case SALA_CURS -> new SalaCurs(nume, capacitate, areEchipamente);
                    case LABORATOR -> new Laborator(nume, capacitate, areEchipamente);
                    case SALA_CONFERINTE -> new SalaConferinte(nume, capacitate, areEchipamente);
                };

                sali.add(sala);
            }

            System.out.println("Datele despre sali au fost incarcate din baza de date.");

        } catch (SQLException e) {
            System.err.println("Eroare la incarcarea salilor din baza de date: " + e.getMessage());
        }
    }

    public void saveSala(Sala sala) {
        sali.add(sala);

        String sql = "INSERT INTO sali (nume, capacitate, tip, are_echipamente) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, sala.getNume());
            preparedStatement.setInt(2, sala.getCapacitate());
            preparedStatement.setString(3, sala.getTip());
            preparedStatement.setBoolean(4, sala.areEchipamente());

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Sala a fost salvata cu succes.");
            }

        } catch (SQLException e) {
            System.err.println("Eroare la salvarea salii: " + e.getMessage());
        }
    }

    public void removeSala(Sala sala) {
        sali.remove(sala);
        RezervariRepository.getInstance().getRezervari().removeIf(rezervare -> rezervare.getSala().equals(sala));

        String sql = "DELETE FROM sali WHERE nume = ?";
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, sala.getNume());

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Sala " + sala.getNume() + " a fost stearsa si toate rezervarile asociate au fost anulate.");
            } else {
                System.out.println("Sala " + sala.getNume() + " nu a fost gasita.");
            }

        } catch (SQLException e) {
            System.err.println("Eroare la stergerea salii: " + e.getMessage());
        }
    }

    public Optional<Sala> gasesteSalaLiberaCuCapacitateMinima(int capacitateMinima, LocalDateTime inceput, LocalDateTime sfarsit) {
        Specification<Sala> liberaSiCapacitateSpec = new AndSpecification<>(
                new EsteLiberaSpec(inceput, sfarsit),
                new CapacitateMinimaSpec(capacitateMinima)
        );

        return sali.stream()
                .filter(liberaSiCapacitateSpec::isSatisfiedBy)
                .findFirst();
    }

    public List<Sala> getSali() {
        return sali;
    }
}
