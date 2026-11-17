import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassementPartage {
    private final List<Voiture> ordreArrivee = new ArrayList<>();

    // Enregistre une voiture à son arrivée. Renvoie false si elle est déjà enregistrée.
    public synchronized boolean enregistrer(Voiture voiture) {
        for (Voiture v : ordreArrivee) {
            if (v.getId() == voiture.getId()) {
                return false; // pas d'enregistrement multiple
            }
        }
        ordreArrivee.add(voiture);
        return true;
    }

    public synchronized int taille() {
        return ordreArrivee.size();
    }

    public synchronized List<Voiture> getOrdreArrivee() {
        return Collections.unmodifiableList(new ArrayList<>(ordreArrivee));
    }
}