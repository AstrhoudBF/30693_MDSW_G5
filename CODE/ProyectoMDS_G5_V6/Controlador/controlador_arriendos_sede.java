package Controlador;

import Modelo.ArriendoSede;
import Modelo.AlmacenarArriendosSede;
import Vista.interfaz_arriendos_sede;
import Vista.Formulario_Historial_Arriendos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_arriendos_sede {

    private final interfaz_arriendos_sede  vista;
    private final AlmacenarArriendosSede   repo;
    private final JFrame                   menuPadre;
    private final ArrayList<ArriendoSede>  listaLocal = new ArrayList<>();

    public controlador_arriendos_sede(interfaz_arriendos_sede vista, JFrame menuPadre) {
        this.vista      = vista;
        this.menuPadre  = menuPadre;
        this.repo       = new AlmacenarArriendosSede();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTablaReservas(listaLocal);
        vista.actualizarTablaProximas(repo.reservasProximas());

        vista.getBtnGuardar().addActionListener(e   -> guardar());
        vista.getBtnHistorial().addActionListener(e -> abrirHistorial());

        // Al cerrar la ventana de sede, mostrar el menú de vuelta
        if (vista instanceof JFrame) {
            ((JFrame) vista).addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    if (menuPadre != null) menuPadre.setVisible(true);
                }
            });
        }
    }

    private void guardar() {
        try {
            String nombre = vista.getNombreSolicitante().trim();
            if (nombre.isEmpty()) { vista.mostrarMensaje("Debe ingresar el nombre del solicitante."); return; }
            String contacto = vista.getContacto().trim();
            if (contacto.isEmpty()) { vista.mostrarMensaje("Debe ingresar un contacto."); return; }
            LocalDate fecha = vista.getFechaReserva();
            if (fecha == null) { vista.mostrarMensaje("Debe seleccionar la fecha de reserva."); return; }
            if (fecha.isBefore(LocalDate.now())) {
                vista.mostrarMensaje("La fecha de reserva no puede ser en el pasado."); return;
            }
            String ocupadoPor = repo.verificarChoque(fecha);
            if (ocupadoPor != null) {
                vista.mostrarMensaje("⚠ La sede ya está reservada el " + fecha
                    + " por: " + ocupadoPor + ".\nElige otra fecha."); return;
            }
            String modalidad  = vista.getModalidad();
            String horaInicio = "";
            String horaFin    = "";
            if ("Por Horas".equals(modalidad)) {
                horaInicio = vista.getHoraInicio().trim();
                horaFin    = vista.getHoraFin().trim();
                if (horaInicio.isEmpty() || horaFin.isEmpty()) {
                    vista.mostrarMensaje("Debe ingresar la hora de inicio y fin."); return;
                }
                if (horaInicio.compareTo(horaFin) >= 0) {
                    vista.mostrarMensaje("La hora de inicio debe ser anterior a la hora de fin."); return;
                }
            }
            double monto;
            try {
                monto = vista.getMonto();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return;
            }
            String motivo = vista.getMotivo().trim();
            if (motivo.isEmpty()) { vista.mostrarMensaje("Debe describir el motivo o evento."); return; }

            ArriendoSede s = new ArriendoSede(nombre, vista.getTipoSolicitante(), contacto,
                    fecha, modalidad, horaInicio, horaFin, monto,
                    vista.getEstado(), vista.getFormaPago(), motivo, LocalDateTime.now());
            repo.guardar(s);
            listaLocal.add(s);
            vista.actualizarTablaReservas(listaLocal);
            vista.actualizarTablaProximas(repo.reservasProximas());
            vista.mostrarMensaje("Reserva de sede registrada correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    private void abrirHistorial() {
        new Formulario_Historial_Arriendos().setVisible(true);
    }

    public void iniciar() { vista.iniciar(); }
}
