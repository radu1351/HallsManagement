package ro.csie.gestiunesali.model;

public class Profesor {
    private String id;
    private String nume;

    public Profesor(String id, String nume) {
        this.id = id;
        this.nume = nume;
    }

    public String getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "id='" + id + '\'' +
                ", nume='" + nume + '\'' +
                '}';
    }
}

