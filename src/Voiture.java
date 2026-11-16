public class Voiture {
    private final int id;
    private String nom;
    private String marque;
    private double vitesse; // en km/tour

    public Voiture(int id, String nom, String marque, double vitesse) {
        verifierTexte(nom, "Le nom");
        verifierTexte(marque, "La marque");
        verifierVitesse(vitesse);
        this.id = id;
        this.nom = nom.trim();
        this.marque = marque.trim();
        this.vitesse = vitesse;
    }

    private static void verifierTexte(String valeur, String champ) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException(champ + " ne peut pas être vide.");
        }
    }

    private static void verifierVitesse(double vitesse) {
        if (vitesse <= 0) {
            throw new IllegalArgumentException("La vitesse doit être strictement positive.");
        }
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getMarque() { return marque; }
    public double getVitesse() { return vitesse; }

    public void setNom(String nom) {
        verifierTexte(nom, "Le nom");
        this.nom = nom.trim();
    }

    public void setMarque(String marque) {
        verifierTexte(marque, "La marque");
        this.marque = marque.trim();
    }

    public void setVitesse(double vitesse) {
        verifierVitesse(vitesse);
        this.vitesse = vitesse;
    }

    @Override
    public String toString() {
        return "Voiture " + id + " : " + nom + " (" + marque + ") - " + vitesse + " km/tour";
    }
}
