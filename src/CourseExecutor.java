import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CourseExecutor {
    private static final long DELAI_MS = 500;
    private static final int TAILLE_POOL = 3;

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
        System.out.println("====== COURSE CONCURRENTE (POOL DE " + TAILLE_POOL + " THREADS) ======");
        System.out.println("Distance : " + course.getDistanceTotale() + " km");

        ExecutorService pool = Executors.newFixedThreadPool(TAILLE_POOL);

        for (Participation p : course.getParticipations()) {
            pool.submit(new VoitureRunnable(p, course.getDistanceTotale(), DELAI_MS, classement));
        }

        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
                System.out.println("Délai dépassé : arrêt forcé du pool.");
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
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