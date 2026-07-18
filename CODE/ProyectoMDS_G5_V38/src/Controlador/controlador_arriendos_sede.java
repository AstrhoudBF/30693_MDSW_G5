package Controlador;

import Modelo.ArriendoSede;
import Modelo.AlmacenarArriendosSede;
import Modelo.validaciones;
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

        vista.getBtnGuardar().addActionListener(e -> guardar());
        vista.getBtnAnular().addActionListener(e   -> anularDesdeHistorial());

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

    // ── Anular desde historial ────────────────────────────────────
    private void anularDesdeHistorial() {
        // Abre el historial en modo anulación
        if (!(vista instanceof Vista.Formulario_Arriendos_Sede)) return;
        Vista.Formulario_Arriendos_Sede formSede = (Vista.Formulario_Arriendos_Sede) vista;
        Vista.Formulario_Historial_Sede historial = new Vista.Formulario_Historial_Sede(formSede);
        historial.setModoAnulacion(true);
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
        ArrayList<String> vacios = new ArrayList<>();

        String nombre = vista.getNombreSolicitante();
        if (nombre.isEmpty()) vacios.add("Nombre del solicitante");

        String tipoSol = vista.getTipoSolicitante();
        String casa    = "";
        if ("Residente".equals(tipoSol)) {
            casa = vista.getNumeroCasaResidente();
            if (casa.isEmpty()) vacios.add("N° Casa del residente");
        }

        String tel = vista.getTelefono();
        if (tel.isEmpty()) vacios.add("Teléfono");

        String email = vista.getEmail();
        if (email.isEmpty()) vacios.add("Email");

        // Fecha reserva: el spinner siempre tiene valor, pero validamos no-pasado
        LocalDate fechaReserva = vista.getFechaReserva();
        if (fechaReserva == null) {
            vacios.add("Fecha de reserva");
        } else if (idExcluir == null && fechaReserva.isBefore(LocalDate.now())) {
            vista.mostrarMensaje("La fecha de reserva no puede ser en el pasado."); return null;
        }

        String modalidad  = vista.getModalidad();
        String horaInicio = "";
        String horaFin    = "";
        if ("Por Horas".equals(modalidad)) {
            horaInicio = vista.getHoraInicio();
            horaFin    = vista.getHoraFin();
            if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                vacios.add("Hora inicio / Hora fin");
            }
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

        String motivo = vista.getMotivo();
        if (motivo.isEmpty()) vacios.add("Motivo / Evento");

        // Si hay campos vacíos, mostrar un solo mensaje consolidado
        if (!vacios.isEmpty()) {
            vista.mostrarMensaje("Debe completar los siguientes campos obligatorios:\n• "
                + String.join("\n• ", vacios));
            return null;
        }

        // Validaciones de formato (después de confirmar que no hay vacíos)
        if (!tel.matches("\\d+")) { vista.mostrarMensaje("El teléfono solo debe contener números."); return null; }
        if (tel.length() != 10)   { vista.mostrarMensaje("El teléfono debe tener exactamente 10 dígitos."); return null; }
        if (!validaciones.validarCelularEcuatoriano(tel)) {
            vista.mostrarMensaje("El teléfono debe ser un celular ecuatoriano válido (debe iniciar con '09', p. ej. 09XXXXXXXX).");
            return null;
        }
        if (!validaciones.validarEmailDominiosPopulares(email)) {
            vista.mostrarMensaje("El email debe tener un dominio válido (@gmail.com, @hotmail.com, @outlook.com o @yahoo.com).");
            return null;
        }

        // Verificar choque de fecha (considerando modalidad y rangos horarios)
        if (fechaReserva != null) {
            String ocupadoPor = repo.verificarChoqueExcluyendo(
                fechaReserva, modalidad, horaInicio, horaFin, idExcluir);
            if (ocupadoPor != null) {
                vista.mostrarMensaje("⚠ La sede ya está reservada el " + fechaReserva
                    + " por: " + ocupadoPor + ".\nElige otra fecha u otro horario."); return null;
            }
        }

        // Validar horas si modalidad = Por Horas
        if ("Por Horas".equals(modalidad) && horaInicio.compareTo(horaFin) >= 0) {
            vista.mostrarMensaje("La hora de inicio debe ser anterior a la hora de fin."); return null;
        }

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
