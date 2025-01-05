package ro.csie.gestiunesali.factory;

import ro.csie.gestiunesali.enums.TipSala;
import ro.csie.gestiunesali.model.Laborator;
import ro.csie.gestiunesali.model.Sala;
import ro.csie.gestiunesali.model.SalaConferinte;
import ro.csie.gestiunesali.model.SalaCurs;

public class SalaFactory {
    public Sala createSala(TipSala tip, String nume, int capacitate, boolean areEchipamente) {
        switch (tip) {
            case SALA_CURS:
                return new SalaCurs(nume, capacitate, areEchipamente);
            case LABORATOR:
                return new Laborator(nume, capacitate, areEchipamente);
            case SALA_CONFERINTE:
                return new SalaConferinte(nume, capacitate, areEchipamente);
            default:
                throw new IllegalArgumentException("Tip sala necunoscut: " + tip);
        }
    }
}


