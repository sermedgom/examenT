package org.vaadin.example;

public class MensajeFormatter {

    public static String formatMensaje(String mensaje, String nombre, String fecha) {
        String formattedMensaje = mensaje.substring(0, Math.min(mensaje.length(), 50));
        formattedMensaje += (mensaje.length() > 50) ? "..." : "";
        return formattedMensaje + "\n\n-- " + nombre + ". " + fecha;
    }

}
