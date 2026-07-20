package Vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.time.LocalDate;
import java.util.ArrayList;
import Modelo.Alicuota;

public interface interfaz_alicuotas {

    String    getNumeroCasa();
    String    getNombreResidente();
    String    getTelefono();
    String    getEmail();
    double    getMonto();
    String    getMontoTexto();
    String    getPeriodo();
    String    getEstado();
    String    getFormaPago();
    String    getNumeroTransaccion();
    LocalDate getFechaPago();

    void setNombreResidente(String nombre);
    void setTelefono(String tel);

    JButton getBtnGuardar();
    JButton getBtnRegresar();
    JButton getBtnActualizar();
    JButton getBtnEliminar();
    JComboBox<String> getComboCasa();

    String getIdSeleccionado();
    void   precargarEdicion(Alicuota a);

    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Alicuota> lista);
    void iniciar();
    void dispose();
}
