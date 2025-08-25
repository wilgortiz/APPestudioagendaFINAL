package com.nombreempresa.estudioagenda.modelos;

import java.io.Serializable;
import java.util.Date;

public class Calificaciones implements Serializable {

        private int idCalificacion;

        private int idEstudiante;

        private Estudiante estudiante;

        private int idMateria;

        private Materia materia;

        private String tipoEvaluacion;

        private float calificacion;

        private Date fecha;

        // Constructores
        public Calificaciones() {}

        public Calificaciones(int idCalificacion, int idEstudiante, int idMateria, String tipoEvaluacion, float calificacion, Date fecha) {
            this.idCalificacion = idCalificacion;
            this.idEstudiante = idEstudiante;
            this.idMateria = idMateria;
            this.tipoEvaluacion = tipoEvaluacion;
            this.calificacion = calificacion;
            this.fecha = fecha;
        }

        // Getters y setters
        public int getIdCalificacion() {
            return idCalificacion;
        }

        public void setIdCalificacion(int idCalificacion) {
            this.idCalificacion = idCalificacion;
        }

        public int getIdEstudiante() {
            return idEstudiante;
        }

        public void setIdEstudiante(int idEstudiante) {
            this.idEstudiante = idEstudiante;
        }

        public Estudiante getEstudiante() {
            return estudiante;
        }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
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

        public String getTipoEvaluacion() {
            return tipoEvaluacion;
        }

        public void setTipoEvaluacion(String tipoEvaluacion) {
            this.tipoEvaluacion = tipoEvaluacion;
        }

        public float getCalificacion() {
            return calificacion;
        }

        public void setCalificacion(float calificacion) {
            this.calificacion = calificacion;
        }

        public Date getFecha() {
            return fecha;
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }

    @Override
    public String toString() {
        return "Calificaciones{" +
                "idCalificacion=" + idCalificacion +
                ", idEstudiante=" + idEstudiante +
                ", estudiante=" + estudiante +
                ", idMateria=" + idMateria +
                ", materia=" + materia +
                ", tipoEvaluacion='" + tipoEvaluacion + '\'' +
                ", calificacion=" + calificacion +
                ", fecha=" + fecha +
                '}';
    }
}

