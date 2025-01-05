package ro.csie.gestiunesali.observer;

public class ConsoleLogger implements ObserverSali {
    @Override
    public void update(String message) {
        System.out.println("[LOG] " + message);
    }
}

