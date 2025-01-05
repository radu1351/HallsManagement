package ro.csie.gestiunesali.observer;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private final List<ObserverSali> observers = new ArrayList<>();

    public void addObserver(ObserverSali observer) {
        observers.add(observer);
    }

    public void removeObserver(ObserverSali observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (ObserverSali observer : observers) {
            observer.update(message);
        }
    }
}

