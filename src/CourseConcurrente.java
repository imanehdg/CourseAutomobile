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
        System.out.println("========== COURSE CONCURRENTE ==========");
        System.out.println("Distance : " + course.getDistanceTotale() + " km");

        // Un thread par voiture
        for (Participation p : course.getParticipations()) {
            Thread t = new Thread(
                    new VoitureRunnable(p, course.getDistanceTotale(), DELAI_MS),
                    p.getVoiture().getNom());
            t.start();
        }
    }
}