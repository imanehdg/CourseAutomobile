public class Participation {
    private final Voiture voiture;
    private double distanceParcourue; // repart de 0 à chaque course
    private int tourArrivee;          // 0 = pas encore arrivée

    public Participation(Voiture voiture) {
        if (voiture == null) {
            throw new IllegalArgumentException("La voiture ne peut pas être nulle.");
        }
        this.voiture = voiture;
        this.distanceParcourue = 0;
        this.tourArrivee = 0;
    }

    public Voiture getVoiture() { return voiture; }
    public double getDistanceParcourue() { return distanceParcourue; }
    public int getTourArrivee() { return tourArrivee; }

    public boolean estArrivee() {
        return tourArrivee > 0;
    }

    public void avancer(double distanceTotale, int tour) {
        if (estArrivee()) {
            return;
        }
        distanceParcourue += voiture.getVitesse();
        if (distanceParcourue >= distanceTotale) {
            distanceParcourue = distanceTotale; // distance plafonnée
            tourArrivee = tour;
        }
    }

    public double getTempsTheorique(double distanceTotale) {
        return distanceTotale / voiture.getVitesse();
    }
}
