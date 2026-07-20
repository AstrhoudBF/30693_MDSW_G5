package Vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.util.ArrayList;
import Modelo.Alicuota;

public interface interfaz_alicuotas {

    // ── Getters de campos ─────────────────────────────────────────
    String getNumeroCasa();
    String getNombreResidente();
    double getMonto();
    String getPeriodo();
    String getEstado();
    String getFormaPago();

    // ── Setter para autocompletar residente ───────────────────────
    void setNombreResidente(String nombre);

    // ── Botones ───────────────────────────────────────────────────
    JButton getBtnGuardar();
    JButton getBtnRegresar();
    JButton getBtnActualizar();
    JButton getBtnEliminar();
    JComboBox<String> getComboCasa();

    // ── Selección en tabla ────────────────────────────────────────
    /** Retorna el ID (hex ObjectId) de la alícuota seleccionada, o null si ninguna. */
    String getIdSeleccionado();

    // ── Precargar datos para edición ──────────────────────────────
    void precargarEdicion(Alicuota a);

    // ── Utilidades de UI ──────────────────────────────────────────
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Alicuota> lista);
    void iniciar();
    void dispose();
}
