package Controlador;

import Modelo.Alicuota;
import Modelo.AlmacenarAlicuotas;
import Vista.interfaz_alicuotas;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JFrame;

public class controlador_alicuotas {

    private final interfaz_alicuotas  vista;
    private final AlmacenarAlicuotas  repo;
    private final JFrame              menuPadre;
    private ArrayList<Alicuota>       listaLocal = new ArrayList<>();
    private String                    idEnEdicion = null;

    private static final Pattern PERIODO_PATRON =
        Pattern.compile("^[A-ZÁÉÍÓÚÑ]+-\\d{4}$");

    public controlador_alicuotas(interfaz_alicuotas vista, JFrame menuPadre) {
        this.vista     = vista;
        this.menuPadre = menuPadre;
        this.repo      = new AlmacenarAlicuotas();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTabla(listaLocal);

        vista.getComboCasa().addActionListener(e -> autocompletar());
        vista.getBtnGuardar().addActionListener(e -> {
            if (idEnEdicion != null) ejecutarActualizacion();
            else                     guardar();
        });
        vista.getBtnActualizar().addActionListener(e -> cargarParaEditar());
        vista.getBtnEliminar().addActionListener(e   -> eliminar());
        vista.getBtnRegresar().addActionListener(e   -> regresar());
    }

    // ── Autocompletar nombre y teléfono al elegir casa ────────────
    private void autocompletar() {
        String casa = vista.getNumeroCasa();
        if (casa == null || casa.equals("-- Seleccione --") || casa.trim().isEmpty()) {
            vista.setNombreResidente(""); vista.setTelefono(""); return;
        }
        String[] datos = repo.obtenerDatosResidentePorCasa(casa.trim());
        if (datos != null) {
            vista.setNombreResidente(datos[0].trim());
            vista.setTelefono(datos[1]);
        } else {
            vista.setNombreResidente("Sin residente asignado");
            vista.setTelefono("");
        }
    }

    // ── Construir y validar Alicuota ──────────────────────────────
    private Alicuota construirYValidar() {
        String casa = vista.getNumeroCasa();
        if (casa == null || casa.equals("-- Seleccione --") || casa.trim().isEmpty()) {
            vista.mostrarMensaje("Debe seleccionar un número de casa."); return null;
        }
        String residente = vista.getNombreResidente();
        if (residente == null || residente.trim().isEmpty() || residente.equals("Sin residente asignado")) {
            vista.mostrarMensaje("La casa seleccionada no tiene un residente asignado."); return null;
        }

        // Teléfono — solo números
        String tel = vista.getTelefono();
        if (tel.isEmpty()) { vista.mostrarMensaje("Debe ingresar el teléfono."); return null; }
        if (!tel.matches("\\d+")) { vista.mostrarMensaje("El teléfono solo debe contener números."); return null; }

        // Email — debe tener @
        String email = vista.getEmail();
        if (email.isEmpty()) { vista.mostrarMensaje("Debe ingresar el email."); return null; }
        if (!email.contains("@")) { vista.mostrarMensaje("El email debe contener '@'."); return null; }

        double monto;
        try {
            monto = vista.getMonto();
            if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return null; }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El monto ingresado no es válido."); return null;
        }

        // Período formato MES-AÑO mayúsculas
        String periodo = vista.getPeriodo().trim().toUpperCase();
        if (periodo.isEmpty()) { vista.mostrarMensaje("Debe ingresar el período."); return null; }
        if (!PERIODO_PATRON.matcher(periodo).matches()) {
            vista.mostrarMensaje("El período debe tener el formato MES-AÑO en mayúsculas.\nEjemplo: JUNIO-2026"); return null;
        }

        String estado    = vista.getEstado();
        String formaPago = vista.getFormaPago();

        // N° Transacción obligatorio para Transferencia/Depósito si estado = Pagado
        String numTx = "";
        if ("Transferencia".equals(formaPago) || "Depósito".equals(formaPago)) {
            numTx = vista.getNumeroTransaccion();
            if (numTx.isEmpty() && "Pagado".equals(estado)) {
                vista.mostrarMensaje("Debe ingresar el número de transacción para " + formaPago + "."); return null;
            }
        }

        LocalDate fechaPago = vista.getFechaPago();

        return new Alicuota(casa.trim(), residente.trim(), tel, email,
                monto, periodo, estado, formaPago, numTx, fechaPago, LocalDateTime.now());
    }

    private void guardar() {
        Alicuota a = construirYValidar();
        if (a == null) return;
        // Aviso de multas pendientes
        String aviso = repo.generarAvisoMultas(a.getNumeroCasa());
        if (aviso != null) {
            javax.swing.JOptionPane.showMessageDialog(null, aviso,
                "⚠ Multas pendientes del residente", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
        try {
            repo.guardar(a);
            refrescarLista();
            vista.mostrarMensaje("Alícuota registrada correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    private void cargarParaEditar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una alícuota de la tabla para modificar."); return; }
        Alicuota sel = buscarPorId(id);
        if (sel == null) { vista.mostrarMensaje("No se pudo obtener los datos de la alícuota."); return; }
        vista.precargarEdicion(sel);
        idEnEdicion = id;
        vista.getBtnGuardar().setText("Actualizar");
        vista.getBtnGuardar().setBackground(new java.awt.Color(30, 100, 180));
        vista.mostrarMensaje("Alícuota cargada para edición.\nModifique los campos y presione 'Actualizar'.");
    }

    private void ejecutarActualizacion() {
        Alicuota a = construirYValidar();
        if (a == null) return;
        Alicuota original = buscarPorId(idEnEdicion);
        if (original != null) a.setFechaRegistro(original.getFechaRegistro());
        try {
            repo.actualizar(idEnEdicion, a);
            idEnEdicion = null;
            vista.getBtnGuardar().setText("Guardar");
            vista.getBtnGuardar().setBackground(java.awt.Color.BLACK);
            refrescarLista();
            vista.mostrarMensaje("Alícuota actualizada correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al actualizar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una alícuota de la tabla para eliminar."); return; }
        Alicuota sel = buscarPorId(id);
        String desc = sel != null ? "Casa " + sel.getNumeroCasa() + " — " + sel.getPeriodo() : id;
        int c = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de eliminar la alícuota de:\n" + desc + "?\n\nEsta acción no se puede deshacer.",
            "Confirmar eliminación", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.eliminar(id);
            if (id.equals(idEnEdicion)) idEnEdicion = null;
            refrescarLista();
            vista.limpiarCampos();
            vista.mostrarMensaje("Alícuota eliminada correctamente.");
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al eliminar: " + ex.getMessage());
        }
    }

    private Alicuota buscarPorId(String id) {
        for (Alicuota a : listaLocal) { if (id.equals(a.getId())) return a; }
        return null;
    }

    private void refrescarLista() {
        listaLocal = repo.obtenerTodas();
        vista.actualizarTabla(listaLocal);
    }

    private void regresar() {
        vista.dispose();
        if (menuPadre != null) menuPadre.setVisible(true);
    }

    public void iniciar() { vista.iniciar(); }
}
