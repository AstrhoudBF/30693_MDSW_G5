package Vista;

import Modelo.Arriendo;
import javax.swing.JButton;
import java.time.LocalDate;
import java.util.ArrayList;

public interface interfaz_arriendos {

    String    getTipoEspacio();
    String    getNombreEspacio();
    String    getNombreArrendatario();
    String    getTipoArrendatario();
    String    getNumeroCasaResidente();
    String    getTelefono();
    String    getEmail();
    double    getMontoMensual();
    String    getMontoMensualTexto();
    String    getMesPeriodo();
    String    getEstado();
    String    getFormaPago();
    String    getNumeroTransaccion();
    LocalDate getFechaPago();

    JButton getBtnGuardar();
    JButton getBtnEditar();
    JButton getBtnEliminar();
    JButton getBtnHistorial();

    String getIdSeleccionado();
    void   precargarEdicion(Arriendo a);

    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Arriendo> lista);
    void iniciar();
    void dispose();
}
