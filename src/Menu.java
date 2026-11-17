import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final GestionCourse gestionCourse = new GestionCourse();
    private final GestionVoitures gestionVoitures = new GestionVoitures(gestionCourse);
    private final Simulateur simulateur = new Simulateur();

    public static void main(String[] args) {
        Menu menu = new Menu();
        if (args.length > 0 && args[0].equals("--auto")) {
            menu.lancerAuto();
        } else {
            menu.lancer();
        }
    }

    public void lancerAuto() {
        gestionVoitures.ajouter(1, "Ferrari", "Ferrari", 20);
        gestionVoitures.ajouter(2, "BMW", "BMW", 15);
        gestionVoitures.ajouter(3, "Renault", "Renault", 10);
        Course c = gestionCourse.creerCourse(1, "Course automatique", 100);
        for (Voiture v : gestionVoitures.lister()) {
            c.inscrire(v);
        }
        simulateur.demarrer(c);
        afficherResultats();
    }

    public void lancer() {
        int choix;
        do {
            afficherMenu();
            choix = lireEntier("Votre choix : ");
            try {
                switch (choix) {
                    case 1 -> ajouterVoiture();
                    case 2 -> afficherVoitures();
                    case 3 -> rechercherVoiture();
                    case 4 -> modifierVoiture();
                    case 5 -> supprimerVoiture();
                    case 6 -> creerCourse();
                    case 7 -> demarrerCourse();
                    case 8 -> afficherResultats();
                    case 0 -> System.out.println("Au revoir !");
                    default -> System.out.println("Erreur : choix invalide (0 à 8).");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        } while (choix != 0);
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("       GESTION DE COURSES AUTOMOBILES");
        System.out.println("=========================================");
        System.out.println("1. Ajouter une voiture");
        System.out.println("2. Afficher les voitures");
        System.out.println("3. Rechercher une voiture");
        System.out.println("4. Modifier une voiture");
        System.out.println("5. Supprimer une voiture");
        System.out.println("6. Créer et configurer une course");
        System.out.println("7. Démarrer une course");
        System.out.println("8. Consulter les résultats");
        System.out.println("0. Quitter");
        System.out.println("=========================================");
    }

    private void ajouterVoiture() {
        int id = lireEntier("Identifiant : ");
        String nom = lireTexte("Nom : ");
        String marque = lireTexte("Marque : ");
        double vitesse = lireDouble("Vitesse (km/tour) : ");
        gestionVoitures.ajouter(id, nom, marque, vitesse);
        System.out.println("Voiture ajoutée avec succès.");
    }

    private void afficherVoitures() {
        List<Voiture> liste = gestionVoitures.lister();
        if (liste.isEmpty()) {
            System.out.println("Aucune voiture enregistrée.");
            return;
        }
        for (Voiture v : liste) {
            System.out.println(v);
        }
    }

    private void rechercherVoiture() {
        int id = lireEntier("Identifiant à rechercher : ");
        Voiture v = gestionVoitures.rechercher(id);
        System.out.println(v != null ? v.toString() : "Aucune voiture avec l'identifiant " + id);
    }

    private void modifierVoiture() {
        int id = lireEntier("Identifiant de la voiture à modifier : ");
        String nom = lireTexte("Nouveau nom : ");
        String marque = lireTexte("Nouvelle marque : ");
        double vitesse = lireDouble("Nouvelle vitesse (km/tour) : ");
        gestionVoitures.modifier(id, nom, marque, vitesse);
        System.out.println("Voiture modifiée.");
    }

    private void supprimerVoiture() {
        int id = lireEntier("Identifiant de la voiture à supprimer : ");
        gestionVoitures.supprimer(id);
        System.out.println("Voiture supprimée.");
    }

    private void creerCourse() {
        if (gestionVoitures.lister().size() < 2) {
            throw new IllegalStateException("Il faut d'abord enregistrer au moins 2 voitures.");
        }
        int id = lireEntier("Identifiant de la course : ");
        String nom = lireTexte("Nom de la course : ");
        double distance = lireDouble("Distance totale (km) : ");
        Course course = gestionCourse.creerCourse(id, nom, distance);

        afficherVoitures();
        System.out.println("Inscrivez les voitures (identifiant), ou 0 pour terminer.");
        while (true) {
            int idVoiture = lireEntier("Identifiant de la voiture (0 = terminer) : ");
            if (idVoiture == 0) {
                if (course.getParticipations().size() < 2) {
                    System.out.println("Erreur : il faut au moins 2 voitures.");
                    continue;
                }
                break;
            }
            Voiture v = gestionVoitures.rechercher(idVoiture);
            if (v == null) {
                System.out.println("Erreur : aucune voiture avec l'identifiant " + idVoiture);
                continue;
            }
            try {
                course.inscrire(v);
                System.out.println(v.getNom() + " inscrite.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
        System.out.println("Course configurée : " + course.getNom() + ", "
                + course.getDistanceTotale() + " km, "
                + course.getParticipations().size() + " voitures.");
    }

    private void demarrerCourse() {
        simulateur.demarrer(gestionCourse.getCourseCourante());
        afficherResultats();
    }

    private void afficherResultats() {
        Course c = gestionCourse.getCourseCourante();
        if (c == null || c.getEtat() != EtatCourse.TERMINEE) {
            System.out.println("Aucun résultat disponible : aucune course terminée.");
            return;
        }
        List<Participation> classement = c.calculerClassement();
        System.out.println();
        System.out.println("============== CLASSEMENT ==============");
        int rang = 1;
        for (Participation p : classement) {
            System.out.println(rang + ". " + p.getVoiture().getNom()
                    + " - " + p.getDistanceParcourue() + " km"
                    + " - arrivée au tour " + p.getTourArrivee());
            rang++;
        }
        System.out.println("Gagnant : " + classement.get(0).getVoiture().getNom());
        System.out.println("Nombre total de tours : " + c.getNombreToursTotal());
    }

    // ---------- Lecture sécurisée des saisies ----------
    private String lireTexte(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    private int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Erreur : veuillez saisir un nombre entier.");
            }
        }
    }

    private double lireDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Erreur : veuillez saisir un nombre (ex. 12.5).");
            }
        }
    }
}