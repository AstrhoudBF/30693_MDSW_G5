package Vista;

import Modelo.Residentes;
import javax.swing.JButton;
import java.util.ArrayList;

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
    String getNombresYApellidosResidentes(); // campo combinado

    JButton getBtnGuardar();
    JButton getBtnRegresar();

    void actualizarTabla(ArrayList<Residentes> lista);
    void limpiarCampos();
    void mostrarMensaje(String mensaje);
    void dispose();
}