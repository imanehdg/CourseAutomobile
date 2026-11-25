public class TestsCourse {
    private static int total = 0;
    private static int echecs = 0;

    public static void main(String[] args) {
        testCreationVoiture();
        testProgression();
        testDetectionArrivee();
        testValidationDonnees();
        testCoherenceClassement();
        testVoitureEngagee();

        System.out.println();
        System.out.println(total + " verifications, " + echecs + " echec(s)");
        if (echecs > 0) {
            System.exit(1); // fait échouer le pipeline Jenkins
        }
    }

    // ---------- Outils ----------
    private static void verifier(String nom, boolean condition) {
        total++;
        if (condition) {
            System.out.println("[OK]    " + nom);
        } else {
            echecs++;
            System.out.println("[ECHEC] " + nom);
        }
    }

    private static boolean leve(Class<? extends Exception> type, Runnable action) {
        try {
            action.run();
            return false;
        } catch (Exception e) {
            return type.isInstance(e);
        }
    }

    // Fait avancer toutes les participations jusqu'à l'arrivée de tous (comme le Simulateur)
    private static void courir(Course course) {
        course.setEtat(EtatCourse.EN_COURS);
        int tour = 0;
        boolean fini = false;
        while (!fini) {
            tour++;
            fini = true;
            for (Participation p : course.getParticipations()) {
                p.avancer(course.getDistanceTotale(), tour);
                if (!p.estArrivee()) {
                    fini = false;
                }
            }
        }
        course.setNombreToursTotal(tour);
        course.setEtat(EtatCourse.TERMINEE);
    }

    // ---------- Test 1 : création d'une voiture ----------
    private static void testCreationVoiture() {
        Voiture v = new Voiture(1, "Ferrari", "Ferrari", 20);
        verifier("T1 creation : id", v.getId() == 1);
        verifier("T1 creation : nom", v.getNom().equals("Ferrari"));
        verifier("T1 creation : vitesse", v.getVitesse() == 20);

        GestionCourse gc = new GestionCourse();
        GestionVoitures gv = new GestionVoitures(gc);
        gv.ajouter(1, "BMW", "BMW", 15);
        verifier("T1 creation : ajout dans le catalogue", gv.rechercher(1) != null);
        verifier("T1 creation : voiture inexistante = null", gv.rechercher(99) == null);
    }

    // ---------- Test 2 : progression ----------
    private static void testProgression() {
        Participation p = new Participation(new Voiture(1, "A", "A", 20));
        verifier("T2 progression : distance initiale = 0", p.getDistanceParcourue() == 0);
        p.avancer(100, 1);
        verifier("T2 progression : 20 km apres 1 tour", p.getDistanceParcourue() == 25);
        p.avancer(100, 2);
        verifier("T2 progression : 40 km apres 2 tours", p.getDistanceParcourue() == 40);
        verifier("T2 progression : pas encore arrivee", !p.estArrivee());
    }

    // ---------- Test 3 : détection de l'arrivée ----------
    private static void testDetectionArrivee() {
        Participation p = new Participation(new Voiture(1, "A", "A", 20));
        for (int tour = 1; tour <= 5; tour++) {
            p.avancer(100, tour);
        }
        verifier("T3 arrivee : detectee apres 5 tours", p.estArrivee());
        verifier("T3 arrivee : tour d'arrivee = 5", p.getTourArrivee() == 5);
        p.avancer(100, 6);
        verifier("T3 arrivee : distance plafonnee a 100", p.getDistanceParcourue() == 100);
        verifier("T3 arrivee : tour d'arrivee inchange", p.getTourArrivee() == 5);

        Participation q = new Participation(new Voiture(2, "B", "B", 20));
        for (int tour = 1; tour <= 5; tour++) {
            q.avancer(90, tour);
        }
        verifier("T3 arrivee : plafonnement a 90 (distance non multiple)", q.getDistanceParcourue() == 90);
    }

    // ---------- Test 4 : validation des données ----------
    private static void testValidationDonnees() {
        verifier("T4 validation : vitesse 0 refusee",
                leve(IllegalArgumentException.class, () -> new Voiture(1, "A", "A", 0)));
        verifier("T4 validation : vitesse negative refusee",
                leve(IllegalArgumentException.class, () -> new Voiture(1, "A", "A", -5)));
        verifier("T4 validation : distance de course 0 refusee",
                leve(IllegalArgumentException.class, () -> new Course(1, "C", 0)));
        verifier("T4 validation : distance de course negative refusee",
                leve(IllegalArgumentException.class, () -> new Course(1, "C", -10)));

        GestionVoitures gv = new GestionVoitures(new GestionCourse());
        gv.ajouter(1, "A", "A", 10);
        verifier("T4 validation : identifiant en double refuse",
                leve(IllegalArgumentException.class, () -> gv.ajouter(1, "B", "B", 12)));

        Course c = new Course(1, "C", 100);
        Voiture v = new Voiture(1, "A", "A", 10);
        c.inscrire(v);
        verifier("T4 validation : inscription multiple refusee",
                leve(IllegalArgumentException.class, () -> c.inscrire(v)));
        verifier("T4 validation : course a 1 voiture non prete", !c.estPrete());
    }

    // ---------- Test 5 : cohérence du classement ----------
    private static void testCoherenceClassement() {
        // Classement simple par vitesse
        Course c1 = new Course(1, "C1", 100);
        c1.inscrire(new Voiture(3, "Renault", "Renault", 10));
        c1.inscrire(new Voiture(1, "Ferrari", "Ferrari", 20));
        c1.inscrire(new Voiture(2, "BMW", "BMW", 15));
        courir(c1);
        var cl1 = c1.calculerClassement();
        verifier("T5 classement : 1er = Ferrari", cl1.get(0).getVoiture().getId() == 1);
        verifier("T5 classement : 2e = BMW", cl1.get(1).getVoiture().getId() == 2);
        verifier("T5 classement : 3e = Renault", cl1.get(2).getVoiture().getId() == 3);
        verifier("T5 classement : 10 tours au total", c1.getNombreToursTotal() == 10);

        // Même tour d'arrivée : départage par temps théorique (distance / vitesse)
        Course c2 = new Course(2, "C2", 100);
        c2.inscrire(new Voiture(1, "Lente", "X", 26));   // arrive au tour 4, temps théorique 3,85
        c2.inscrire(new Voiture(2, "Rapide", "X", 30));  // arrive au tour 4, temps théorique 3,33
        courir(c2);
        var cl2 = c2.calculerClassement();
        verifier("T5 classement : meme tour, depart par temps theorique",
                cl2.get(0).getVoiture().getId() == 2);

        // Égalité parfaite : départage par identifiant
        Course c3 = new Course(3, "C3", 100);
        c3.inscrire(new Voiture(5, "E", "X", 20));
        c3.inscrire(new Voiture(2, "B", "X", 20));
        courir(c3);
        var cl3 = c3.calculerClassement();
        verifier("T5 classement : egalite parfaite, depart par identifiant",
                cl3.get(0).getVoiture().getId() == 2);
    }

    // ---------- Test 6 : voiture engagée non modifiable ----------
    private static void testVoitureEngagee() {
        GestionCourse gc = new GestionCourse();
        GestionVoitures gv = new GestionVoitures(gc);
        gv.ajouter(1, "A", "A", 10);
        gv.ajouter(2, "B", "B", 12);
        Course c = gc.creerCourse(1, "C", 100);
        c.inscrire(gv.rechercher(1));
        c.inscrire(gv.rechercher(2));
        verifier("T6 engagee : modification refusee",
                leve(IllegalStateException.class, () -> gv.modifier(1, "A", "A", 11)));
        verifier("T6 engagee : suppression refusee",
                leve(IllegalStateException.class, () -> gv.supprimer(1)));
        courir(c);
        gv.modifier(1, "A", "A", 11);
        verifier("T6 engagee : modification acceptee apres la course", gv.rechercher(1).getVitesse() == 11);
    }
}