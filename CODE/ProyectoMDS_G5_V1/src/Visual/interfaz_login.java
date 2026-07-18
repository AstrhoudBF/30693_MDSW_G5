
package Vista;

public interface interfaz_login {
    
    String getUsuario();
    String getContrasena();
    void mostrarError(String mensaje);
    void bloquearBoton(int segundos);
}
