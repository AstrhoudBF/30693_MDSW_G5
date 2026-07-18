
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
        this.vista = vista;
    }
    
    public boolean validarLogin() {
        String usuario = vista.getUsuario();
        String contrasena = vista.getContrasena();

        if (modelo.validar(usuario, contrasena)) {
            intentos = 0;
            return true;
        } else {
            intentos++;
            int restantes = MAX_INTENTOS - intentos;

            if (intentos >= MAX_INTENTOS) {
                intentos = 0;
                vista.mostrarError("Usuario o contraseña incorrectos.");
                vista.bloquearBoton(25);
            } else {
                vista.mostrarError("Usuario o contraseña incorrectos. Le quedan " + restantes + " intento(s).");
            }
            return false;
        }
    }
    
}

