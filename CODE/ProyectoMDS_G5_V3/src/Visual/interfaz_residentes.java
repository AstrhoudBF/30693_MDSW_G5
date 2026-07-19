package Vista;

import Modelo.Residentes;
import javax.swing.JButton;
import java.util.ArrayList;
import javax.swing.JTextField;

public interface interfaz_residentes {
    String getNombres();
    String getApellidos();
    String getCedula();
    String getTelefonoMovil();
    String getTelefonoConvencional();
    String getNumeroVivienda();
    boolean getTieneMascotas();
    String getNumeroVehiculos();
    String getTipoResidente();
    
    JButton getBtnGuardar();
    JButton getBtnRegresar();
  
    void actualizarTabla(ArrayList<Residentes> lista);
    void limpiarCampos();
    void mostrarMensaje(String mensaje);
    void iniciar();
    void dispose();
}