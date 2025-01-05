package ro.csie.gestiunesali.observer;

public class EmailNotifier implements ObserverSali {
    @Override
    public void update(String message) {
        System.out.println("[EMAIL] Notificare trimisa: " + message);
    }
}
