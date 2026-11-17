import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionVoitures {
    private final List<Voiture> voitures = new ArrayList<>();
    private final GestionCourse gestionCourse;

    public GestionVoitures(GestionCourse gestionCourse) {
        this.gestionCourse = gestionCourse;
    }

    public void ajouter(int id, String nom, String marque, double vitesse) {
        if (rechercher(id) != null) {
            throw new IllegalArgumentException("Identifiant déjà utilisé : " + id);
        }
        voitures.add(new Voiture(id, nom, marque, vitesse)); 
    }

    public List<Voiture> lister() {
        return Collections.unmodifiableList(voitures);
    }

    public Voiture rechercher(int id) {
        for (Voiture v : voitures) {
            if (v.getId() == id) {
                return v;
            }
        }
        return null;
    }

    public void modifier(int id, String nom, String marque, double vitesse) {
        Voiture v = voitureExistante(id);
        verifierNonEngagee(id, "modifiée");
        new Voiture(id, nom, marque, vitesse); 
        v.setNom(nom);
        v.setMarque(marque);
        v.setVitesse(vitesse);
    }

    public void supprimer(int id) {
        Voiture v = voitureExistante(id);
        verifierNonEngagee(id, "supprimée");
        voitures.remove(v);
    }

    private Voiture voitureExistante(int id) {
        Voiture v = rechercher(id);
        if (v == null) {
            throw new IllegalArgumentException("Aucune voiture avec l'identifiant " + id);
        }
        return v;
    }

    private void verifierNonEngagee(int id, String action) {
        if (gestionCourse.voitureEngagee(id)) {
            throw new IllegalStateException(
                    "La voiture " + id + " est inscrite à une course non terminée : elle ne peut pas être " + action + ".");
        }
    }
}