package Controlador;

import Modelo.login;
import Vista.interfaz_login;

public class controlador_login {

    private login modelo;
    private interfaz_login vista;
    private int intentos = 0;
    private static final int MAX_INTENTOS = 3;

    public controlador_login(login modelo, interfaz_login vista) {
        this.modelo = modelo;
        this.vista  = vista;
    }

    public boolean validarLogin() {
        String usuario    = vista.getUsuario().trim();
        String contrasena = vista.getContrasena().trim();

        // Credenciales correctas
        if (modelo.validar(usuario, contrasena)) {
            intentos = 0;
            return true;
        }

        // Credenciales incorrectas
        intentos++;

        if (intentos == 1) {
            vista.mostrarError("Credenciales incorrectas. Le quedan 2 intento(s).");
        } else if (intentos == 2) {
            vista.mostrarError("Credenciales incorrectas. Le queda 1 intento(s).");
        } else if (intentos >= 3) {
            intentos = 0;
            vista.bloquearBoton(25);
        }

        return false;
    }
}