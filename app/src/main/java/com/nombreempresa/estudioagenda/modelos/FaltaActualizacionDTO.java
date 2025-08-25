package com.nombreempresa.estudioagenda.modelos;

import com.google.gson.annotations.SerializedName;
public class FaltaActualizacionDTO {
    public int IdEstudiante;

    public int IdFalta;

    public int idMateria;

    public int Cantidad;


    public FaltaActualizacionDTO(int idFalta, int idMateria, int cantidad) {
        IdFalta = idFalta;
        this.idMateria = idMateria;
        Cantidad = cantidad;
    }

    public FaltaActualizacionDTO(int idFalta, int cantidad) {
        IdFalta = idFalta;
        Cantidad = cantidad;
    }

    public int getIdEstudiante() {
        return IdEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        IdEstudiante = idEstudiante;
    }

    public int getIdFalta() {
        return IdFalta;
    }

    public void setIdFalta(int idFalta) {
        IdFalta = idFalta;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }
}