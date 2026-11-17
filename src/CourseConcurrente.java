public class CourseConcurrente {
    private static final long DELAI_MS = 500;

    public void demarrer(Course course) {
        if (course == null) {
            throw new IllegalStateException("Aucune course configurée.");
        }
        if (!course.estPrete()) {
            throw new IllegalStateException(
                    "La course ne peut pas démarrer : il faut au moins 2 voitures et qu'elle n'ait pas déjà été lancée.");
        }
        course.setEtat(EtatCourse.EN_COURS);
        ClassementPartage classement = new ClassementPartage();
        System.out.println("========== COURSE CONCURRENTE ==========");
        System.out.println("Distance : " + course.getDistanceTotale() + " km");

        for (Participation p : course.getParticipations()) {
            Thread t = new Thread(
                    new VoitureRunnable(p, course.getDistanceTotale(), DELAI_MS, classement),
                    p.getVoiture().getNom());
            t.start();
        }
        
        System.out.println("(Classement provisoire : " + classement.taille() + " voiture(s) arrivée(s) pour l'instant)");
    }
}