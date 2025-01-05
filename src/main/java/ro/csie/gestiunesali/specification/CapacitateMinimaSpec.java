package ro.csie.gestiunesali.specification;

import ro.csie.gestiunesali.model.Sala;

public class CapacitateMinimaSpec implements Specification<Sala> {
    private final int capacitateMinima;

    public CapacitateMinimaSpec(int capacitateMinima) {
        this.capacitateMinima = capacitateMinima;
    }

    @Override
    public boolean isSatisfiedBy(Sala sala) {
        return sala.getCapacitate() >= capacitateMinima;
    }
}


