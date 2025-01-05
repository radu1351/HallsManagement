package ro.csie.gestiunesali.specification;

import ro.csie.gestiunesali.db.RezervariRepository;
import ro.csie.gestiunesali.model.Sala;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

public class EsteLiberaSpec implements Specification<Sala> {
    private final LocalDateTime inceput;
    private final LocalDateTime sfarsit;

    public EsteLiberaSpec(LocalDateTime inceput, LocalDateTime sfarsit) {
        this.inceput = inceput;
        this.sfarsit = sfarsit;
    }

    @Override
    public boolean isSatisfiedBy(Sala sala) {
        return !RezervariRepository.getInstance().esteSalaRezervataInInterval(sala, inceput, sfarsit);
    }
}


