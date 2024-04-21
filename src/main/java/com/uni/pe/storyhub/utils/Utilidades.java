package com.uni.pe.storyhub.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Utilidades {

    // Función para incrementar el id de una alerta
    private static AtomicInteger alertIdCounter = new AtomicInteger(0);

    public static int getNextAlertId() {
        return alertIdCounter.incrementAndGet();
    }

    // Función para extraer el primer nombre
    public String extraerPrimerNombre(String nombreCompleto) {
        return nombreCompleto.split(" ")[0].toLowerCase();
    }

    // Función para validar si el 'email' tiene un formato válido
    public boolean isValidEmailFormat(String email) {
        // Expresión regular para validar el formato de un correo electrónico
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        return email.matches(emailRegex);
    }

    public static String generarSlug(String texto) {
        // Eliminar espacios adicionales
        String textoSinEspaciosExtras = texto.trim().replaceAll("\\s+", " ");
        // Reemplazar espacios con guiones
        String slug = textoSinEspaciosExtras.replaceAll("\\s", "-");
        // Agregar un identificador único al final del slug
        slug += "-" + UUID.randomUUID().toString();
        return slug;
    }


    public static String cleanText(String texto) {
        // Eliminar espacios adicionales y espacios al inicio y al final
        String textoProcesado = texto.trim().replaceAll("\\s+", " ");
        // Reemplazar espacios con guiones
        return textoProcesado;
    }
}
