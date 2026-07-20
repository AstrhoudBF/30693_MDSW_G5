package Vista;

import Modelo.Residentes;
import javax.swing.JButton;
import java.util.ArrayList;
import java.util.List;

public interface interfaz_residentes {
    String getNombres();
    String getApellidos();
    String getCedula();
    String getTelefonoMovil();
    String getTelefonoConvencional();
    String getNumeroVivienda();
    String getTipoResidente();
    String getEstadoResidente();   // "Activo" | "Cancelado"
    boolean getTieneMascotas();
    // Vehículos: lista de pares {placa, tipo}
    List<String[]> getVehiculos();

    JButton getBtnGuardar();
    JButton getBtnRegresar();

    void actualizarTabla(ArrayList<Residentes> lista);
    void limpiarCampos();
    void mostrarMensaje(String mensaje);
    void iniciar();
    void dispose();
}
