package Vista;

import Modelo.Multa;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.time.LocalDate;
import java.util.ArrayList;

public interface interfaz_multas {

    // ── Getters del formulario ────────────────────────────────────
    String    getNumeroCasa();
    String    getCedulaResidente();
    String    getNombreResidente();
    String    getCategoria();
    String    getMotivo();
    LocalDate getFechaInfraccion();
    double    getMonto();
    String    getEstado();
    String    getObservaciones();

    // ── Setters para autocompletar ────────────────────────────────
    void setNombreResidente(String nombre);
    void setNumeroCasa(String casa);
    void setCedulaResidente(String cedula);

    // ── Botones ───────────────────────────────────────────────────
    JButton getBtnGuardar();
    JButton getBtnRegresar();
    JButton getBtnModificar();
    JButton getBtnEliminar();

    // ── Combo casa (para listener) ────────────────────────────────
    JComboBox<String> getComboCasa();

    // ── Selección en tabla ────────────────────────────────────────
    String getIdSeleccionado();

    // ── UI ────────────────────────────────────────────────────────
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Multa> lista);
    void iniciar();
    void dispose();
}
