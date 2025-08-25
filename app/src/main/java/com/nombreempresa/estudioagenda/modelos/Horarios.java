package com.nombreempresa.estudioagenda.modelos;

import java.io.Serializable;
import java.util.List;
public class Horarios implements Serializable {
    private int idHorario;
    private int idEstudiante;
    private int idMateria;
    private Materia materia;  // Relación ManyToOne
    private String diaSemana;
    private String horaInicio;  // Cambiado de String a LocalTime
    private String horaFin;     // Cambiado de String a LocalTime
    private List<EstudiantesHorarios> estudiantesHorarios;  // Relación OneToMany

    public Horarios() {
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
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

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public List<EstudiantesHorarios> getEstudiantesHorarios() {
        return estudiantesHorarios;
    }

    public void setEstudiantesHorarios(List<EstudiantesHorarios> estudiantesHorarios) {
        this.estudiantesHorarios = estudiantesHorarios;
    }

    @Override
    public String toString() {
        return "Horarios{" +
                "idHorario=" + idHorario +
                ", idEstudiante=" + idEstudiante +
                ", idMateria=" + idMateria +
                ", materia=" + materia +
                ", diaSemana='" + diaSemana + '\'' +
                ", horaInicio='" + horaInicio + '\'' +
                ", horaFin='" + horaFin + '\'' +
                ", estudiantesHorarios=" + estudiantesHorarios +
                '}';
    }
}