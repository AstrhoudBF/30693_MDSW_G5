package Controlador;

import Modelo.ArriendoSede;
import Modelo.AlmacenarArriendosSede;
import Vista.Formulario_Arriendos_Sede;
import Vista.Formulario_Historial_Sede;
import Vista.interfaz_arriendos_sede;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_arriendos_sede {

    private final interfaz_arriendos_sede  vista;
    private final AlmacenarArriendosSede   repo;
    private final JFrame                   menuPadre;
    private ArrayList<ArriendoSede>        listaLocal = new ArrayList<>();

    public controlador_arriendos_sede(interfaz_arriendos_sede vista, JFrame menuPadre) {
        this.vista     = vista;
        this.menuPadre = menuPadre;
        this.repo      = new AlmacenarArriendosSede();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTablaReservas(listaLocal);
        vista.actualizarTablaProximas(repo.reservasProximas());

        // Guardar / Actualizar: detecta si viene de una edición vía historial
        vista.getBtnGuardar().addActionListener(e -> {
            String idExt = idEnEdicionExterno();
            if (idExt != null) ejecutarActualizacion(idExt);
            else               guardar();
        });

        // Ambos botones (Editar seleccionada + Historial) abren la pantalla de historial
        vista.getBtnEditar().addActionListener(e    -> abrirHistorialParaEditar());
        vista.getBtnHistorial().addActionListener(e -> abrirHistorialParaEditar());

        // Restaurar menú al cerrar con la X
        if (vista instanceof JFrame) {
            ((JFrame) vista).addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) {
                    if (menuPadre != null) menuPadre.setVisible(true);
                }
            });
        }
    }

    // ── Helper: obtiene el ID externo del formulario si existe ────
    private String idEnEdicionExterno() {
        if (vista instanceof Formulario_Arriendos_Sede)
            return ((Formulario_Arriendos_Sede) vista).getIdEnEdicionExterno();
        return null;
    }

    // ── Abrir historial para seleccionar y editar ─────────────────
    private void abrirHistorialParaEditar() {
        if (!(vista instanceof Formulario_Arriendos_Sede)) return;
        Formulario_Arriendos_Sede formSede = (Formulario_Arriendos_Sede) vista;
        Formulario_Historial_Sede historial = new Formulario_Historial_Sede(formSede);
        formSede.setVisible(false);
        historial.setVisible(true);
    }

    // ── Guardar nueva reserva ─────────────────────────────────────
    private void guardar() {
        ArriendoSede s = construirYValidar(null);
        if (s == null) return;
        try {
            repo.guardar(s);
            refrescarLista();
            vista.mostrarMensaje("Reserva de sede registrada correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    // ── Actualizar reserva existente (viene del historial) ─────────
    private void ejecutarActualizacion(String idEdicion) {
        ArriendoSede s = construirYValidar(idEdicion);
        if (s == null) return;
        // Conservar fecha de registro original
        ArriendoSede original = buscarEnRepo(idEdicion);
        if (original != null) s.setFechaRegistro(original.getFechaRegistro());
        try {
            repo.actualizar(idEdicion, s);
            if (vista instanceof Formulario_Arriendos_Sede)
                ((Formulario_Arriendos_Sede) vista).setIdEnEdicionExterno(null);
            vista.getBtnGuardar().setText("Guardar");
            vista.getBtnGuardar().setBackground(java.awt.Color.BLACK);
            refrescarLista();
            vista.mostrarMensaje("Reserva actualizada correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al actualizar: " + ex.getMessage());
        }
    }

    // ── Construir y validar objeto ArriendoSede desde la vista ────
    private ArriendoSede construirYValidar(String idExcluir) {
        String nombre = vista.getNombreSolicitante();
        if (nombre.isEmpty()) { vista.mostrarMensaje("Debe ingresar el nombre del solicitante."); return null; }

        String tipoSol = vista.getTipoSolicitante();
        String casa    = "";
        if ("Residente".equals(tipoSol)) {
            casa = vista.getNumeroCasaResidente();
            if (casa.isEmpty()) { vista.mostrarMensaje("Debe ingresar el número de casa del residente."); return null; }
        }

        String tel = vista.getTelefono();
        if (tel.isEmpty())         { vista.mostrarMensaje("Debe ingresar el teléfono."); return null; }
        if (!tel.matches("\\d+")) { vista.mostrarMensaje("El teléfono solo debe contener números."); return null; }

        String email = vista.getEmail();
        if (email.isEmpty())       { vista.mostrarMensaje("Debe ingresar el email."); return null; }
        if (!email.contains("@")) { vista.mostrarMensaje("El email debe contener '@'."); return null; }

        LocalDate fechaReserva = vista.getFechaReserva();
        if (fechaReserva == null) { vista.mostrarMensaje("Debe seleccionar la fecha de reserva."); return null; }
        if (idExcluir == null && fechaReserva.isBefore(LocalDate.now())) {
            vista.mostrarMensaje("La fecha de reserva no puede ser en el pasado."); return null;
        }

        String ocupadoPor = repo.verificarChoqueExcluyendo(fechaReserva, idExcluir);
        if (ocupadoPor != null) {
            vista.mostrarMensaje("⚠ La sede ya está reservada el " + fechaReserva
                + " por: " + ocupadoPor + ".\nElige otra fecha."); return null;
        }

        String modalidad  = vista.getModalidad();
        String horaInicio = "";
        String horaFin    = "";
        if ("Por Horas".equals(modalidad)) {
            horaInicio = vista.getHoraInicio();
            horaFin    = vista.getHoraFin();
            if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                vista.mostrarMensaje("Debe ingresar hora de inicio y fin."); return null;
            }
            if (horaInicio.compareTo(horaFin) >= 0) {
                vista.mostrarMensaje("La hora de inicio debe ser anterior a la hora de fin."); return null;
            }
        }

        double monto;
        try {
            monto = vista.getMonto();
            if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return null; }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El monto ingresado no es válido."); return null;
        }

        String motivo = vista.getMotivo();
        if (motivo.isEmpty()) { vista.mostrarMensaje("Debe ingresar el motivo del evento."); return null; }

        String estado    = vista.getEstado();
        String formaPago = vista.getFormaPago();

        String numTx = "";
        if ("Transferencia".equals(formaPago) || "Depósito".equals(formaPago)) {
            numTx = vista.getNumeroTransaccion();
            if (numTx.isEmpty() && !"Pendiente".equals(estado)) {
                vista.mostrarMensaje("Debe ingresar el número de transacción para " + formaPago + "."); return null;
            }
        }

        LocalDate fechaPago = vista.getFechaPago();

        return new ArriendoSede(nombre, tipoSol, casa, tel, email,
                fechaReserva, modalidad, horaInicio, horaFin,
                monto, estado, formaPago, numTx, fechaPago,
                motivo, LocalDateTime.now());
    }

    private ArriendoSede buscarEnRepo(String id) {
        for (ArriendoSede s : repo.obtenerTodas()) { if (id.equals(s.getId())) return s; }
        return null;
    }

    private void refrescarLista() {
        listaLocal = repo.obtenerTodas();
        vista.actualizarTablaReservas(listaLocal);
        vista.actualizarTablaProximas(repo.reservasProximas());
    }

    public void iniciar() { vista.iniciar(); }
}
