package ro.csie.gestiunesali.model;

public final class SalaCurs implements Sala {
    private final String nume;
    private final int capacitate;
    private final boolean areEchipamente;

    public SalaCurs(String nume, int capacitate, boolean areEchipamente) {
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
        return "Sala de Curs";
    }

    @Override
    public boolean areEchipamente() {
        return areEchipamente;
    }
}
