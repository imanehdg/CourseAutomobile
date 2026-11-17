public class VoitureRunnable implements Runnable {
    private final Participation participation;
    private final double distanceTotale;
    private final long delaiMs;
    private final ClassementPartage classement;

    public VoitureRunnable(Participation participation, double distanceTotale,
                           long delaiMs, ClassementPartage classement) {
        this.participation = participation;
        this.distanceTotale = distanceTotale;
        this.delaiMs = delaiMs;
        this.classement = classement;
    }

    @Override
    public void run() {
        String nom = participation.getVoiture().getNom();
        int etape = 0;
        while (!participation.estArrivee()) {
            etape++;
            participation.avancer(distanceTotale, etape);
            System.out.println("[" + nom + "] " + participation.getDistanceParcourue()
                    + " / " + distanceTotale + " km");
            if (participation.estArrivee()) {
                classement.enregistrer(participation.getVoiture()); // section protégée
                System.out.println(nom + " franchit la ligne d'arrivée !");
                break;
            }
            try {
                Thread.sleep(delaiMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}