package com.nombreempresa.estudioagenda.modelos;


public class RegistroResponse {
    private String mensaje;

    public RegistroResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}