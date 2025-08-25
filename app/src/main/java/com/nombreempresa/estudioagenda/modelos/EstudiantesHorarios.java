package com.nombreempresa.estudioagenda.modelos;

public class EstudiantesHorarios {
    private int idEstudianteHorario;

    private int idEstudianteFK;

    private int idHorario;

    private Estudiante estudiante;

    private Horarios horario;

    // Constructores
    public EstudiantesHorarios() {}

    public EstudiantesHorarios(int idEstudianteHorario, int idEstudianteFK, int idHorario) {
        this.idEstudianteHorario = idEstudianteHorario;
        this.idEstudianteFK = idEstudianteFK;
        this.idHorario = idHorario;
    }

    // Getters y setters
    public int getIdEstudianteHorario() {
        return idEstudianteHorario;
    }

    public void setIdEstudianteHorario(int idEstudianteHorario) {
        this.idEstudianteHorario = idEstudianteHorario;
    }

    public int getIdEstudianteFK() {
        return idEstudianteFK;
    }

    public void setIdEstudianteFK(int idEstudianteFK) {
        this.idEstudianteFK = idEstudianteFK;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Horarios getHorario() {
        return horario;
    }

    public void setHorario(Horarios horario) {
        this.horario = horario;
    }
}
