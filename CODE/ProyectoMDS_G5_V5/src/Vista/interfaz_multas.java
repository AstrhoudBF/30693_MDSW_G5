package Vista;

import Modelo.Multa;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.time.LocalDate;
import java.util.ArrayList;

public interface interfaz_multas {

    String    getNumeroCasa();
    String    getCedulaResidente();
    String    getNombreResidente();
    String    getCategoria();
    String    getMotivo();
    LocalDate getFechaInfraccion();
    double    getMonto();
    String    getEstado();
    String    getFormaPago();
    String    getNumeroTransaccion();
    LocalDate getFechaPago();
    String    getObservaciones();

    void setNombreResidente(String nombre);
    void setNumeroCasa(String casa);
    void setCedulaResidente(String cedula);

    JButton getBtnGuardar();
    JButton getBtnRegresar();
    JButton getBtnModificar();
    JButton getBtnEliminar();

    JComboBox<String> getComboCasa();

    String getIdSeleccionado();

    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Multa> lista);
    void iniciar();
    void dispose();
}
