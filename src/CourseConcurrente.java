import java.util.ArrayList;
import java.util.List;

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

        List<Thread> threads = new ArrayList<>();
        for (Participation p : course.getParticipations()) {
            Thread t = new Thread(
                    new VoitureRunnable(p, course.getDistanceTotale(), DELAI_MS, classement),
                    p.getVoiture().getNom());
            threads.add(t);
            t.start();
        }

        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Course interrompue.");
            return;
        }

        course.setEtat(EtatCourse.TERMINEE);

        System.out.println("============== CLASSEMENT ==============");
        int rang = 1;
        for (Voiture v : classement.getOrdreArrivee()) {
            System.out.println(rang + ". " + v.getNom());
            rang++;
        }
    }
}