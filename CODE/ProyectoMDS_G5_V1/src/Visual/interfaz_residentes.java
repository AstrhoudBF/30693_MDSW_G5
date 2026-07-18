package Vista;
import Modelo.Residentes;
import java.util.ArrayList;
import javax.swing.JButton;

public interface interfaz_residentes {
    
    String getNombres();
    String getApellidos();
    String getCedula();
    String getTelefonoMovil();
    String getTelefonoConvencional();
    String getNumeroVivienda();
    String getDireccion();
    boolean getTieneMascotas();
    String getNumeroVehiculos();
    String getNumeroPersonas();
    String getNombresResidentes();
    String getApellidosResidentes();
    void limpiarCampos();
    void mostrarMensaje(String mensaje);
    void actualizarTabla(ArrayList<Residentes> lista);
    JButton getBtnGuardar();
}
