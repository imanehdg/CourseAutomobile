public class Simulateur {

    public void demarrer(Course course) {
        if (course == null) {
            throw new IllegalStateException("Aucune course configurée.");
        }
        if (!course.estPrete()) {
            throw new IllegalStateException(
                    "La course ne peut pas démarrer : il faut au moins 2 voitures et qu'elle n'ait pas déjà été lancée.");
        }

        course.setEtat(EtatCourse.EN_COURS);
        System.out.println("===== DÉPART : " + course.getNom()
                + " (" + course.getDistanceTotale() + " km) =====");

        int tour = 0;
        while (!toutesArrivees(course)) {
            tour++;
            executerTour(course, tour);
            afficherProgression(course, tour);
        }

        course.setNombreToursTotal(tour);
        course.setEtat(EtatCourse.TERMINEE);
        System.out.println("===== COURSE TERMINÉE en " + tour + " tours =====");
    }

    private void executerTour(Course course, int tour) {
        for (Participation p : course.getParticipations()) {
            p.avancer(course.getDistanceTotale(), tour);
        }
    }

    private boolean toutesArrivees(Course course) {
        for (Participation p : course.getParticipations()) {
            if (!p.estArrivee()) {
                return false;
            }
        }
        return true;
    }

    private void afficherProgression(Course course, int tour) {
        System.out.println("--- Tour " + tour + " ---");
        for (Participation p : course.getParticipations()) {
            System.out.println("[" + p.getVoiture().getNom() + "] "
                    + p.getDistanceParcourue() + " / " + course.getDistanceTotale() + " km"
                    + (p.getTourArrivee() == tour ? "  -> franchit la ligne d'arrivée !" : ""));
        }
    }
}