package ro.csie.gestiunesali.specification;

import java.time.LocalDateTime;

@FunctionalInterface
public interface Specification<T> {
    boolean isSatisfiedBy(T item);
}


