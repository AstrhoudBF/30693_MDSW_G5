package Controlador;

import Modelo.login;
import Vista.Formulario_Menu;
import Vista.interfaz_login;
import Vista.interfaz_menu;

public class controlador_login {

    private login modelo;
    private interfaz_login vista;
    private int intentos = 0;
    private static final int MAX_INTENTOS = 3;

    public controlador_login(login modelo, interfaz_login vista) {
        this.modelo = modelo;
        this.vista  = vista;
        
        // El botón debe validar PRIMERO, y solo si es true, ir al menú.
        this.vista.getBtnAcc().addActionListener(l -> {
            if (validarLogin()) {
                irMenu();
                // Opcional pero recomendado: cerrar u ocultar la ventana de login
                // this.vista.dispose(); o this.vista.setVisible(false);
                ((javax.swing.JFrame) this.vista).dispose();
            }
        });
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
    
    public void irMenu(){
        interfaz_menu vistaM = new Formulario_Menu();
        controlador_menu ctrM = new controlador_menu(vistaM);
        ctrM.abrirMenu();
    }

    
    public void iniciar(){
        this.vista.iniciar();
    
    }
}