package Vista;

import Modelo.Arriendo;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.ArrayList;

public interface interfaz_arriendos {

    // ── Getters del formulario ────────────────────────────────────
    String getTipoEspacio();
    String getNumeroEspacio();
    String getNombreArrendatario();
    String getTipoArrendatario();
    String getContacto();
    double getMontoMensual();
    String getMesPeriodo();
    String getEstado();
    String getFormaPago();

    // ── Botones ───────────────────────────────────────────────────
    JButton getBtnGuardar();
    JButton getBtnHistorial();

    // ── UI ────────────────────────────────────────────────────────
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void actualizarTabla(ArrayList<Arriendo> lista);
    void iniciar();
    void dispose();
}
