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
        this.vista      = vista;
        this.menuPadre  = menuPadre;
        this.repo       = new AlmacenarMultas();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTabla(listaLocal);

        vista.getComboCasa().addActionListener(e   -> autocompletarPorCasa());
        vista.getBtnGuardar().addActionListener(e  -> guardar());
        vista.getBtnModificar().addActionListener(e -> cargarParaEditar());
        vista.getBtnEliminar().addActionListener(e  -> eliminar());
        vista.getBtnRegresar().addActionListener(e  -> regresar());
    }

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

    private void guardar() {
        try {
            String casa = vista.getNumeroCasa();
            if (casa == null || casa.equals("-- Seleccione --") || casa.trim().isEmpty()) {
                vista.mostrarMensaje("Debe seleccionar el número de casa."); return;
            }
            String nombre = vista.getNombreResidente().trim();
            if (nombre.isEmpty() || nombre.equals("Sin residente asignado")) {
                vista.mostrarMensaje("La casa seleccionada no tiene un residente asignado."); return;
            }
            String motivo = vista.getMotivo().trim();
            if (motivo.isEmpty()) { vista.mostrarMensaje("Debe ingresar el motivo de la multa."); return; }
            LocalDate fechaInf = vista.getFechaInfraccion();
            if (fechaInf == null) { vista.mostrarMensaje("Debe seleccionar la fecha de infracción."); return; }
            double monto;
            try {
                monto = vista.getMonto();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return;
            }

            Multa m = new Multa(casa.trim(), vista.getCedulaResidente().trim(), nombre,
                    vista.getCategoria(), motivo, fechaInf, monto,
                    vista.getEstado(), vista.getObservaciones().trim(), LocalDateTime.now());

            if (idEnEdicion == null) {
                repo.guardar(m);
                vista.mostrarMensaje("Multa registrada correctamente.");
            } else {
                repo.actualizar(idEnEdicion, m);
                idEnEdicion = null;
                vista.getBtnGuardar().setText("Guardar");
                vista.mostrarMensaje("Multa actualizada correctamente.");
            }
            refrescarLista();
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

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
    }

    private void eliminar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una multa de la tabla para eliminar."); return; }
        Multa sel = buscarPorId(id);
        String desc = sel != null ? sel.getNombreResidente() + " — " + sel.getMotivo() : id;
        int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de eliminar la multa de:\n" + desc + "?\n\nEsta acción no se puede deshacer.",
            "Confirmar eliminación", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.eliminar(id);
            refrescarLista();
            vista.mostrarMensaje("Multa eliminada correctamente.");
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al eliminar: " + ex.getMessage());
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
