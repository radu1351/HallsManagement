package ro.csie.gestiunesali.enums;

public enum TipSala {
    SALA_CURS("Sala de curs"),
    LABORATOR("Laborator"),
    SALA_CONFERINTE("Sala de conferinte");

    private final String dbValue;

    TipSala(String descriere) {
        this.dbValue = descriere;
    }

    public String getDescriere() {
        return dbValue;
    }

    public static TipSala fromDbValue(String dbValue) {
        for (TipSala tip : values()) {
            if (tip.dbValue.equalsIgnoreCase(dbValue)) {
                return tip;
            }
        }
        throw new IllegalArgumentException("Tip sala necunoscut: " + dbValue);
    }
}

