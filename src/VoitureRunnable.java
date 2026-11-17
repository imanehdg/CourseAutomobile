public class VoitureRunnable implements Runnable {
    private final Participation participation;
    private final double distanceTotale;
    private final long delaiMs;

    public VoitureRunnable(Participation participation, double distanceTotale, long delaiMs) {
        this.participation = participation;
        this.distanceTotale = distanceTotale;
        this.delaiMs = delaiMs;
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
                System.out.println(nom + " franchit la ligne d'arrivée !");
                break;
            }
            try {
                Thread.sleep(delaiMs); // délai simulé entre deux déplacements
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}