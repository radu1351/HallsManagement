package ro.csie.gestiunesali.model;
import java.time.LocalDateTime;

public class Rezervare {
    private Profesor profesor;
    private Sala sala;
    private LocalDateTime inceput;
    private LocalDateTime sfarsit;

    public Rezervare(Profesor profesor, Sala sala, LocalDateTime inceput, LocalDateTime sfarsit) {
        this.profesor = profesor;
        this.sala = sala;
        this.inceput = inceput;
        this.sfarsit = sfarsit;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getInceput() {
        return inceput;
    }

    public LocalDateTime getSfarsit() {
        return sfarsit;
    }

    @Override
    public String toString() {
        return "Rezervare{" +
                "profesor=" + profesor.getNume() +
                ", sala=" + sala.getNume() +
                ", inceput=" + inceput +
                ", sfarsit=" + sfarsit +
                '}';
    }
}

