package com.nombreempresa.estudioagenda.modelos;
public class LoginResponse {
    private String token;
    private String mensaje;

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}