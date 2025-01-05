package ro.csie.gestiunesali;

import ro.csie.gestiunesali.db.ProfesoriRepository;
import ro.csie.gestiunesali.db.RezervariRepository;
import ro.csie.gestiunesali.db.SalaRepository;
import ro.csie.gestiunesali.enums.TipSala;
import ro.csie.gestiunesali.factory.SalaFactory;
import ro.csie.gestiunesali.model.Profesor;
import ro.csie.gestiunesali.model.Rezervare;
import ro.csie.gestiunesali.model.Sala;
import ro.csie.gestiunesali.model.SalaCurs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        SalaRepository salaRepository = SalaRepository.getInstance();
        ProfesoriRepository profesoriRepository = ProfesoriRepository.getInstance();
        RezervariRepository rezervariRepository = RezervariRepository.getInstance();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Meniu ===");
            System.out.println("1. Adauga sala");
            System.out.println("2. Sterge sala dupa nume");
            System.out.println("3. Adauga profesor");
            System.out.println("4. Sterge profesor");
            System.out.println("5. Adauga rezervare");
            System.out.println("6. Elibereaza rezervare");
            System.out.println("7. Gaseste o sala libera cu capacitate minima");
            System.out.println("8. Afiseaza toate salile");
            System.out.println("9. Afiseaza toti profesorii");
            System.out.println("10. Afiseaza toate rezervarile");
            System.out.println("11. Afiseaza rezervarile pentru o sala");
            System.out.println("12. Afiseaza rezervarile pentru un profesor");
            System.out.println("13. Iesire");
            System.out.print("Alege o optiune: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaSala(scanner, salaRepository);
                case 2 -> stergeSala(scanner, salaRepository);
                case 3 -> adaugaProfesor(scanner, profesoriRepository);
                case 4 -> stergeProfesor(scanner, profesoriRepository);
                case 5 -> adaugaRezervare(scanner, salaRepository, profesoriRepository, rezervariRepository);
                case 6 -> elibereazaRezervare(scanner, rezervariRepository);
                case 7 -> gasesteSalaLibera(scanner, salaRepository);
                case 8 -> afiseazaSali(salaRepository);
                case 9 -> afiseazaProfesori(profesoriRepository);
                case 10 -> afiseazaRezervari(rezervariRepository);
                case 11 -> afiseazaRezervariPentruSala(scanner, salaRepository, rezervariRepository);
                case 12 -> afiseazaRezervariPentruProfesor(scanner, profesoriRepository, rezervariRepository);
                case 13 -> {
                    System.out.println("Iesire...");
                    return;
                }
                default -> System.out.println("Optiune invalida.");
            }
        }
    }

    private static void adaugaSala(Scanner scanner, SalaRepository salaRepository) {
        SalaFactory salaFactory = new SalaFactory();

        System.out.print("Introdu tipul salii (1: Sala de curs, 2: Laborator, 3: Sala de conferinte): ");
        int tipSala = scanner.nextInt();
        scanner.nextLine();

        TipSala tip = switch (tipSala) {
            case 1 -> TipSala.SALA_CURS;
            case 2 -> TipSala.LABORATOR;
            case 3 -> TipSala.SALA_CONFERINTE;
            default -> null;
        };

        if (tip == null) {
            System.out.println("Tipul salii nu este valid.");
            return;
        }

        System.out.print("Introdu numele salii: ");
        String nume = scanner.nextLine();

        System.out.print("Introdu capacitatea: ");
        int capacitate = scanner.nextInt();

        System.out.print("Are echipamente? (true/false): ");
        boolean areEchipamente = scanner.nextBoolean();

        Sala sala = salaFactory.createSala(tip, nume, capacitate, areEchipamente);

        if (sala != null) {
            salaRepository.saveSala(sala);
            System.out.println("Sala a fost adaugata cu succes: " + sala.getNume());
        } else {
            System.out.println("Eroare la crearea salii.");
        }
    }

    private static void stergeSala(Scanner scanner, SalaRepository salaRepository) {
        afiseazaSali(salaRepository);

        System.out.print("Introdu numele salii de sters: ");
        String nume = scanner.nextLine();
        Optional<Sala> salaDeSters = salaRepository.getSali().stream()
                .filter(s -> s.getNume().equals(nume))
                .findFirst();

        if (salaDeSters.isPresent()) {
            salaRepository.removeSala(salaDeSters.get());
        } else {
            System.out.println("Sala cu numele " + nume + " nu exista.");
        }
    }

    private static void adaugaProfesor(Scanner scanner, ProfesoriRepository profesoriRepository) {
        System.out.print("Introdu ID-ul profesorului: ");
        String id = scanner.nextLine();

        System.out.print("Introdu numele profesorului: ");
        String nume = scanner.nextLine();

        Profesor profesor = new Profesor(id, nume);
        profesoriRepository.adaugaProfesor(profesor);
    }

    private static void stergeProfesor(Scanner scanner, ProfesoriRepository profesoriRepository) {
        afiseazaProfesori(profesoriRepository);

        System.out.print("Introdu ID-ul profesorului de sters: ");
        String id = scanner.nextLine();

        profesoriRepository.stergeProfesor(id);
    }

    private static void adaugaRezervare(Scanner scanner, SalaRepository salaRepository, ProfesoriRepository profesoriRepository, RezervariRepository rezervariRepository) {
        afiseazaProfesori(profesoriRepository);
        System.out.print("Introdu ID-ul profesorului: ");
        String profesorId = scanner.nextLine();

        Optional<Profesor> profesor = profesoriRepository.getProfesori().stream()
                .filter(p -> p.getId().equals(profesorId))
                .findFirst();

        if (profesor.isEmpty()) {
            System.out.println("Profesorul cu ID-ul " + profesorId + " nu exista.");
            return;
        }

        afiseazaSali(salaRepository);
        System.out.print("Introdu numele salii: ");
        String salaNume = scanner.nextLine();

        Optional<Sala> sala = salaRepository.getSali().stream()
                .filter(s -> s.getNume().equals(salaNume))
                .findFirst();

        if (sala.isEmpty()) {
            System.out.println("Sala cu numele " + salaNume + " nu exista.");
            return;
        }

        System.out.print("Introdu ora de inceput (YYYY-MM-DDTHH:MM): ");
        LocalDateTime inceput = LocalDateTime.parse(scanner.nextLine());

        System.out.print("Introdu ora de sfarsit (YYYY-MM-DDTHH:MM): ");
        LocalDateTime sfarsit = LocalDateTime.parse(scanner.nextLine());

        Rezervare rezervare = new Rezervare(profesor.get(), sala.get(), inceput, sfarsit);
        rezervariRepository.adaugaRezervare(rezervare);
    }

    private static void elibereazaRezervare(Scanner scanner, RezervariRepository rezervariRepository) {
        List<Rezervare> rezervari = rezervariRepository.getRezervari();

        if (rezervari.isEmpty()) {
            System.out.println("Nu exista rezervari disponibile.");
            return;
        }

        System.out.println("Rezervari disponibile:");
        for (int i = 0; i < rezervari.size(); i++) {
            Rezervare rezervare = rezervari.get(i);
            System.out.println((i + 1) + ". Profesor: " + rezervare.getProfesor().getNume() +
                    ", Sala: " + rezervare.getSala().getNume() +
                    ", Interval: " + rezervare.getInceput() + " - " + rezervare.getSfarsit());
        }

        System.out.print("Introdu numarul rezervarii pe care doresti sa o eliberezi: ");
        int index = scanner.nextInt();
        scanner.nextLine();

        if (index < 1 || index > rezervari.size()) {
            System.out.println("Numarul introdus nu este valid.");
            return;
        }

        Rezervare rezervareDeEliberat = rezervari.get(index - 1);
        rezervariRepository.elibereazaRezervare(rezervareDeEliberat);

        System.out.println("Rezervarea a fost eliberata cu succes.");
    }

    private static void gasesteSalaLibera(Scanner scanner, SalaRepository salaRepository) {
        System.out.print("Introdu capacitatea minima: ");
        int capacitateMinima = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Introdu ora de inceput (YYYY-MM-DDTHH:MM): ");
        LocalDateTime inceput = LocalDateTime.parse(scanner.nextLine());

        System.out.print("Introdu ora de sfarsit (YYYY-MM-DDTHH:MM): ");
        LocalDateTime sfarsit = LocalDateTime.parse(scanner.nextLine());

        Optional<Sala> salaGasita = salaRepository.gasesteSalaLiberaCuCapacitateMinima(capacitateMinima, inceput, sfarsit);

        if (salaGasita.isPresent()) {
            System.out.println("Sala gasita: " + salaGasita.get().getNume());
        } else {
            System.out.println("Nu exista o sala libera cu cel putin " + capacitateMinima + " locuri intre " +
                    inceput + " si " + sfarsit + ".");
        }
    }

    private static void afiseazaSali(SalaRepository salaRepository) {
        salaRepository.getSali().forEach(sala -> System.out.println(" - " + sala.getNume() +
                ", capacitate: " + sala.getCapacitate() +
                ", tip: " + sala.getTip()));
    }

    private static void afiseazaProfesori(ProfesoriRepository profesoriRepository) {
        profesoriRepository.getProfesori().forEach(profesor -> {
            System.out.println(" - " + profesor.getId() + ": " + profesor.getNume());
        });
    }

    private static void afiseazaRezervari(RezervariRepository rezervariRepository) {
        rezervariRepository.getRezervari().forEach(rezervare -> {
            System.out.println(" - Profesor: " + rezervare.getProfesor().getNume() +
                    ", Sala: " + rezervare.getSala().getNume() +
                    ", Interval: " + rezervare.getInceput() + " - " + rezervare.getSfarsit());
        });
    }

    private static void afiseazaRezervariPentruSala(Scanner scanner, SalaRepository salaRepository, RezervariRepository rezervariRepository) {
        afiseazaSali(salaRepository);
        System.out.print("Introdu numele salii: ");
        String salaNume = scanner.nextLine();

        List<Rezervare> rezervariSala = rezervariRepository.getRezervariPentruSala(new SalaCurs(salaNume, 0, false));
        if (rezervariSala.isEmpty()) {
            System.out.println("Nu exista rezervari pentru sala " + salaNume);
            return;
        }

        System.out.println("Rezervari pentru sala " + salaNume + ":");
        rezervariSala.forEach(r -> System.out.println(" - Profesor: " + r.getProfesor().getNume() +
                ", Interval: " + r.getInceput() + " - " + r.getSfarsit()));
    }

    private static void afiseazaRezervariPentruProfesor(Scanner scanner, ProfesoriRepository profesoriRepository, RezervariRepository rezervariRepository) {
        afiseazaProfesori(profesoriRepository);
        System.out.print("Introdu ID-ul profesorului: ");
        String profesorId = scanner.nextLine();

        List<Rezervare> rezervariProfesor = rezervariRepository.getRezervariPentruProfesor(new Profesor(profesorId, ""));

        if (rezervariProfesor.isEmpty()) {
            System.out.println("Nu exista rezervari pentru profesorul cu ID-ul " + profesorId);
            return;
        }

        System.out.println("Rezervari pentru profesorul cu ID-ul " + profesorId + ":");
        rezervariProfesor.forEach(r -> System.out.println(" - Sala: " + r.getSala().getNume() +
                ", Interval: " + r.getInceput() + " - " + r.getSfarsit()));
    }
}
