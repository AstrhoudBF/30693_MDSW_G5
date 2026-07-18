package Modelo;


public class login {
    
    private String usuario;
    private String contrasena;

    public login(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
    
    public boolean validar(String u, String p) {
        return this.usuario.equals(u) && this.contrasena.equals(p);
    }

}
