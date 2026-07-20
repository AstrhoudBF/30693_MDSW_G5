package Controlador;

import Modelo.AlmacenarMultas;
import Modelo.Multa;
import Vista.interfaz_multas;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_multas {

    private final interfaz_multas   vista;
    private final AlmacenarMultas   repo;
    private final JFrame            menuPadre;
    private ArrayList<Multa>        listaLocal = new ArrayList<>();
    private String                  idEnEdicion = null;

    public controlador_multas(interfaz_multas vista, JFrame menuPadre) {
        this.vista     = vista;
        this.menuPadre = menuPadre;
        this.repo      = new AlmacenarMultas();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTabla(listaLocal);

        vista.getComboCasa().addActionListener(e   -> autocompletarPorCasa());
        vista.getBtnGuardar().addActionListener(e  -> guardar());
        vista.getBtnModificar().addActionListener(e -> cargarParaEditar());
        vista.getBtnEliminar().addActionListener(e  -> anular());
        vista.getBtnRegresar().addActionListener(e  -> regresar());
    }

    // ── Autocompletar nombre y cédula al elegir casa ──────────────
    private void autocompletarPorCasa() {
        String casa = vista.getNumeroCasa();
        if (casa == null || casa.equals("-- Seleccione --") || casa.trim().isEmpty()) {
            vista.setNombreResidente(""); vista.setCedulaResidente(""); return;
        }
        String[] datos = repo.obtenerResidentePorCasa(casa.trim());
        if (datos != null) {
            vista.setNombreResidente(datos[0]);
            vista.setCedulaResidente(datos[1]);
        } else {
            vista.setNombreResidente("Sin residente asignado");
            vista.setCedulaResidente("");
        }
    }

    // ── Construir y validar Multa desde la vista ──────────────────
    private Multa construirYValidar() {
        ArrayList<String> vacios = new ArrayList<>();

        String casa = vista.getNumeroCasa();
        if (casa == null || casa.equals("-- Seleccione --") || casa.trim().isEmpty()) {
            vacios.add("N° de Casa");
        }

        String nombre = vista.getNombreResidente().trim();
        boolean residenteValido = !nombre.isEmpty() && !nombre.equals("Sin residente asignado");

        String motivo = vista.getMotivo().trim();
        if (motivo.isEmpty()) vacios.add("Motivo");

        LocalDate fechaInf = vista.getFechaInfraccion();
        if (fechaInf == null) {
            vacios.add("Fecha de infracción");
        }

        // Monto
        double monto = 0;
        if (vista.getMontoTexto().isEmpty()) {
            vacios.add("Monto");
        } else {
            try {
                monto = vista.getMonto();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return null; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return null;
            }
        }

        // Si hay campos vacíos, mostrar un solo mensaje consolidado
        if (!vacios.isEmpty()) {
            vista.mostrarMensaje("Debe completar los siguientes campos obligatorios:\n• "
                + String.join("\n• ", vacios));
            return null;
        }

        // Residente sin asignar
        if (!residenteValido) {
            vista.mostrarMensaje("La casa seleccionada no tiene un residente asignado."); return null;
        }

        // Fecha de infracción: no puede ser anterior a hoy.
        // (La fecha de pago, en cambio, queda libre y puede ser pasada o futura.)
        if (fechaInf.isBefore(LocalDate.now())) {
            vista.mostrarMensaje("La fecha de infracción no puede ser anterior a la fecha actual."); return null;
        }

        String estado    = vista.getEstado();
        String formaPago = vista.getFormaPago();

        // Validar campos de pago solo cuando estado = "Pagada"
        String numTx    = "";
        LocalDate fechaPago = null;

        if ("Pagada".equals(estado)) {
            // N° Transacción obligatorio para Transferencia/Depósito
            if ("Transferencia".equals(formaPago) || "Depósito".equals(formaPago)) {
                numTx = vista.getNumeroTransaccion();
                if (numTx.isEmpty()) {
                    vista.mostrarMensaje("Debe ingresar el número de transacción para " + formaPago + "."); return null;
                }
            }
            fechaPago = vista.getFechaPago();
            if (fechaPago == null) {
                vista.mostrarMensaje("Debe ingresar la fecha de pago."); return null;
            }
        }

        return new Multa(
            casa.trim(), vista.getCedulaResidente().trim(), nombre,
            vista.getCategoria(), motivo, fechaInf, monto,
            estado, formaPago, numTx, fechaPago,
            vista.getObservaciones().trim(), LocalDateTime.now()
        );
    }

    // ── Guardar nueva multa ───────────────────────────────────────
    private void guardar() {
        Multa m = construirYValidar();
        if (m == null) return;
        try {
            if (idEnEdicion == null) {
                repo.guardar(m);
                vista.mostrarMensaje("Multa registrada correctamente.");
            } else {
                Multa original = buscarPorId(idEnEdicion);
                if (original != null) m.setFechaRegistro(original.getFechaRegistro());
                repo.actualizar(idEnEdicion, m);
                idEnEdicion = null;
                vista.getBtnGuardar().setText("Guardar");
                vista.getBtnGuardar().setBackground(java.awt.Color.BLACK);
                vista.mostrarMensaje("Multa actualizada correctamente.");
            }
            refrescarLista();
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    // ── Cargar multa seleccionada para editar ─────────────────────
    private void cargarParaEditar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una multa de la tabla para modificar."); return; }
        Multa sel = buscarPorId(id);
        if (sel == null) { vista.mostrarMensaje("No se pudo obtener los datos de la multa."); return; }
        if (vista instanceof Vista.Formulario_Multas) {
            ((Vista.Formulario_Multas) vista).precargarEdicion(sel);
        }
        idEnEdicion = id;
        vista.getBtnGuardar().setText("Actualizar");
        vista.getBtnGuardar().setBackground(new java.awt.Color(30, 100, 180));
    }

    // ── Anular multa ──────────────────────────────────────────────
    private void anular() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una multa de la tabla para anular."); return; }
        Multa sel = buscarPorId(id);
        String desc = sel != null ? sel.getNombreResidente() + " — " + sel.getMotivo() : id;
        int c = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de anular la multa de:\n" + desc +
            "?\n\nSu estado cambiará a 'Anulada'.",
            "Confirmar anulación",
            javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (c != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.anular(id);
            if (id.equals(idEnEdicion)) { idEnEdicion = null; }
            refrescarLista();
            vista.mostrarMensaje("Multa anulada correctamente. Estado: Anulada.");
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al anular: " + ex.getMessage());
        }
    }

    private Multa buscarPorId(String id) {
        for (Multa m : listaLocal) { if (id.equals(m.getId())) return m; }
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
