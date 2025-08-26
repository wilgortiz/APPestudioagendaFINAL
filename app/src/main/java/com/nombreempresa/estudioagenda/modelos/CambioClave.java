package com.nombreempresa.estudioagenda.modelos;

public class CambioClave {
    private String claveActual;
    private String claveNueva;

    public CambioClave(String claveActual, String claveNueva) {
        this.claveActual = claveActual;
        this.claveNueva = claveNueva;
    }

    public String getClaveActual() { return claveActual; }
    public String getClaveNueva() { return claveNueva; }
}
