public class GestionCourse {
    private Course courseCourante; 

    
    public Course creerCourse(int id, String nom, double distance) {
        courseCourante = new Course(id, nom, distance);
        return courseCourante;
    }

    public Course getCourseCourante() {
        return courseCourante;
    }

    public boolean voitureEngagee(int idVoiture) {
        return courseCourante != null
                && courseCourante.getEtat() != EtatCourse.TERMINEE
                && courseCourante.contientVoiture(idVoiture);
    }
}