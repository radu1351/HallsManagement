package ro.csie.gestiunesali.model;

public final class Laborator implements Sala {
    private final String nume;
    private final int capacitate;
    private final boolean areEchipamente;

    public Laborator(String nume, int capacitate, boolean areEchipamente) {
        this.nume = nume;
        this.capacitate = capacitate;
        this.areEchipamente = areEchipamente;
    }

    @Override
    public String getNume() {
        return nume;
    }

    @Override
    public int getCapacitate() {
        return capacitate;
    }

    @Override
    public String getTip() {
        return "Laborator";
    }

    @Override
    public boolean areEchipamente() {
        return areEchipamente;
    }

}

