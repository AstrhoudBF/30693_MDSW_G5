package Vista;


import Modelo.ArriendoSede;
import javax.swing.JButton;
import java.time.LocalDate;
import java.util.ArrayList;

public interface interfaz_arriendos_sede {

    // ── Getters del formulario ────────────────────────────────────
    String    getNombreSolicitante();
    String    getTipoSolicitante();
    String    getNumeroCasaResidente();
    String    getTelefono();
    String    getEmail();
    LocalDate getFechaReserva();
    String    getModalidad();
    String    getHoraInicio();
    String    getHoraFin();
    double    getMonto();
    String    getMontoTexto();
    String    getEstado();
    String    getFormaPago();
    String    getNumeroTransaccion();
    LocalDate getFechaPago();
    String    getMotivo();

    // ── Selección en tabla para editar ────────────────────────────
    String getIdSeleccionado();

    // ── Precarga para edición ─────────────────────────────────────
    void precargarEdicion(ArriendoSede s);

    // ── Botones ───────────────────────────────────────────────────
    JButton getBtnGuardar();
    JButton getBtnAnular();

    // ── UI ────────────────────────────────────────────────────────
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTablaReservas(ArrayList<ArriendoSede> lista);
    void actualizarTablaProximas(ArrayList<ArriendoSede> lista);
    void setHorasVisible(boolean visible);
    void iniciar();
    void dispose();
}
