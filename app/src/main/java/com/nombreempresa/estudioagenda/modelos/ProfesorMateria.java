package com.nombreempresa.estudioagenda.modelos;

public class ProfesorMateria {

        private int idProfesorMateria;

        private int idProfesor;

        private Profesor profesor;

        private int idMateria;

        private Materia materia;

        // Constructores
        public ProfesorMateria() {}

        public ProfesorMateria(int idProfesorMateria, int idProfesor, int idMateria) {
            this.idProfesorMateria = idProfesorMateria;
            this.idProfesor = idProfesor;
            this.idMateria = idMateria;
        }

        // Getters y setters
        public int getIdProfesorMateria() {
            return idProfesorMateria;
        }

        public void setIdProfesorMateria(int idProfesorMateria) {
            this.idProfesorMateria = idProfesorMateria;
        }

        public int getIdProfesor() {
            return idProfesor;
        }

        public void setIdProfesor(int idProfesor) {
            this.idProfesor = idProfesor;
        }

        public Profesor getProfesor() {
            return profesor;
        }

        public void setProfesor(Profesor profesor) {
            this.profesor = profesor;
        }

        public int getIdMateria() {
            return idMateria;
        }

        public void setIdMateria(int idMateria) {
            this.idMateria = idMateria;
        }

        public Materia getMateria() {
            return materia;
        }

        public void setMateria(Materia materia) {
            this.materia = materia;
        }
}
