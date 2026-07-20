package Vista;

import Modelo.ArriendoSede;
import javax.swing.JButton;
import java.time.LocalDate;
import java.util.ArrayList;

public interface interfaz_arriendos_sede {

    // ── Getters del formulario ────────────────────────────────────
    String    getNombreSolicitante();
    String    getTipoSolicitante();
    String    getContacto();
    LocalDate getFechaReserva();
    String    getModalidad();
    String    getHoraInicio();
    String    getHoraFin();
    double    getMonto();
    String    getEstado();
    String    getFormaPago();
    String    getMotivo();

    // ── Botones ───────────────────────────────────────────────────
    JButton getBtnGuardar();
    JButton getBtnHistorial();

    // ── UI ────────────────────────────────────────────────────────
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTablaReservas(ArrayList<ArriendoSede> lista);
    void actualizarTablaProximas(ArrayList<ArriendoSede> lista);
    void setHorasVisible(boolean visible);
    void iniciar();
    void dispose();
}
