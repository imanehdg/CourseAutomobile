import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Course {
    private final int id;
    private final String nom;
    private final double distanceTotale;
    private EtatCourse etat;
    private int nombreToursTotal;
    private final List<Participation> participations = new ArrayList<>();

    public Course(int id, String nom, double distanceTotale) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom de la course ne peut pas être vide.");
        }
        if (distanceTotale <= 0) {
            throw new IllegalArgumentException("La distance de la course doit être strictement positive.");
        }
        this.id = id;
        this.nom = nom.trim();
        this.distanceTotale = distanceTotale;
        this.etat = EtatCourse.CONFIGUREE;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getDistanceTotale() { return distanceTotale; }
    public EtatCourse getEtat() { return etat; }
    public void setEtat(EtatCourse etat) { this.etat = etat; }
    public int getNombreToursTotal() { return nombreToursTotal; }
    public void setNombreToursTotal(int n) { this.nombreToursTotal = n; }

    public List<Participation> getParticipations() {
        return Collections.unmodifiableList(participations);
    }

    public boolean contientVoiture(int idVoiture) {
        for (Participation p : participations) {
            if (p.getVoiture().getId() == idVoiture) {
                return true;
            }
        }
        return false;
    }

    public void inscrire(Voiture voiture) {
        if (etat != EtatCourse.CONFIGUREE) {
            throw new IllegalStateException("Impossible d'inscrire une voiture : la course a déjà démarré.");
        }
        if (contientVoiture(voiture.getId())) {
            throw new IllegalArgumentException("La voiture " + voiture.getId() + " est déjà inscrite à cette course.");
        }
        participations.add(new Participation(voiture));
    }

    public boolean estPrete() {
        return etat == EtatCourse.CONFIGUREE && participations.size() >= 2;
    }

    public List<Participation> calculerClassement() {
        List<Participation> classement = new ArrayList<>(participations);
        classement.sort(
            Comparator.comparingInt(Participation::getTourArrivee)
                .thenComparingDouble(p -> p.getTempsTheorique(distanceTotale))
                .thenComparingInt(p -> p.getVoiture().getId())
        );
        return classement;
    }
}
