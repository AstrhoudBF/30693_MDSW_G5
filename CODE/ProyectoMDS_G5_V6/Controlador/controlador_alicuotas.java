package Controlador;

import Modelo.Alicuota;
import Modelo.AlmacenarAlicuotas;
import Vista.interfaz_alicuotas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_alicuotas {

    private final interfaz_alicuotas  vista;
    private final AlmacenarAlicuotas  repo;
    private final JFrame              menuPadre;
    private ArrayList<Alicuota>       listaLocal = new ArrayList<>();
    private String                    idEnEdicion = null;

    public controlador_alicuotas(interfaz_alicuotas vista, JFrame menuPadre) {
        this.vista       = vista;
        this.menuPadre   = menuPadre;
        this.repo        = new AlmacenarAlicuotas();

        listaLocal.addAll(repo.obtenerTodas());
        vista.actualizarTabla(listaLocal);

        vista.getComboCasa().addActionListener(e  -> buscarResidente());
        vista.getBtnGuardar().addActionListener(e -> {
            if (idEnEdicion != null) ejecutarActualizacion();
            else                     guardar();
        });
        vista.getBtnActualizar().addActionListener(e -> cargarParaEditar());
        vista.getBtnEliminar().addActionListener(e   -> eliminar());
        vista.getBtnRegresar().addActionListener(e   -> regresar());
    }

    private void buscarResidente() {
        String casa = vista.getNumeroCasa();
        if (casa == null || casa.trim().isEmpty() || casa.equals("-- Seleccione --")) {
            vista.setNombreResidente(""); return;
        }
        String res = repo.obtenerResidentePorCasa(casa.trim());
        vista.setNombreResidente((res != null && !res.trim().isEmpty()) ? res.trim() : "Sin residente asignado");
    }

    private void guardar() {
        try {
            String casa = vista.getNumeroCasa();
            if (casa == null || casa.trim().isEmpty() || casa.equals("-- Seleccione --")) {
                vista.mostrarMensaje("Debe seleccionar un número de casa."); return;
            }
            String residente = vista.getNombreResidente();
            if (residente == null || residente.trim().isEmpty() || residente.equals("Sin residente asignado")) {
                vista.mostrarMensaje("La casa seleccionada no tiene un residente asignado."); return;
            }
            double monto;
            try {
                monto = vista.getMonto();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return;
            }
            String periodo = vista.getPeriodo();
            if (periodo == null || periodo.trim().isEmpty()) {
                vista.mostrarMensaje("Debe ingresar el período (ej: Junio 2026)."); return;
            }
            String aviso = repo.generarAvisoMultas(casa.trim());
            if (aviso != null) {
                javax.swing.JOptionPane.showMessageDialog(null, aviso,
                    "⚠ Multas pendientes del residente", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            Alicuota a = new Alicuota(casa.trim(), residente.trim(), monto,
                    periodo.trim(), vista.getEstado(), vista.getFormaPago(), LocalDateTime.now());
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
        if (sel == null) { vista.mostrarMensaje("No se pudo obtener los datos de la alícuota seleccionada."); return; }
        vista.precargarEdicion(sel);
        idEnEdicion = id;
        vista.getBtnGuardar().setText("Actualizar");
        vista.getBtnGuardar().setBackground(new java.awt.Color(30, 100, 180));
        vista.mostrarMensaje("Alícuota cargada para edición.\nModifique los campos y presione 'Actualizar'.");
    }

    private void eliminar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione una alícuota de la tabla para eliminar."); return; }
        Alicuota sel = buscarPorId(id);
        String desc = sel != null ? "Casa " + sel.getNumeroCasa() + " — " + sel.getPeriodo() : id;
        int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de eliminar la alícuota de:\n" + desc + "?\n\nEsta acción no se puede deshacer.",
            "Confirmar eliminación", javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.eliminar(id);
            refrescarLista();
            vista.mostrarMensaje("Alícuota eliminada correctamente.");
            vista.limpiarCampos();
            cancelarEdicion();
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

    private void cancelarEdicion() {
        idEnEdicion = null;
        vista.getBtnGuardar().setText("Guardar");
        vista.getBtnGuardar().setBackground(java.awt.Color.BLACK);
    }

    private void regresar() {
        vista.dispose();
        if (menuPadre != null) menuPadre.setVisible(true);
    }

    public void iniciar() { vista.iniciar(); }

    public void ejecutarActualizacion() {
        if (idEnEdicion == null) { guardar(); return; }
        try {
            String casa = vista.getNumeroCasa();
            if (casa == null || casa.trim().isEmpty() || casa.equals("-- Seleccione --")) {
                vista.mostrarMensaje("Debe seleccionar un número de casa."); return;
            }
            String residente = vista.getNombreResidente();
            if (residente == null || residente.trim().isEmpty() || residente.equals("Sin residente asignado")) {
                vista.mostrarMensaje("La casa seleccionada no tiene un residente asignado."); return;
            }
            double monto;
            try {
                monto = vista.getMonto();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return;
            }
            String periodo = vista.getPeriodo();
            if (periodo == null || periodo.trim().isEmpty()) {
                vista.mostrarMensaje("Debe ingresar el período."); return;
            }
            Alicuota original = buscarPorId(idEnEdicion);
            LocalDateTime fechaOriginal = original != null ? original.getFechaRegistro() : LocalDateTime.now();
            Alicuota actualizada = new Alicuota(casa.trim(), residente.trim(), monto,
                    periodo.trim(), vista.getEstado(), vista.getFormaPago(), fechaOriginal);
            repo.actualizar(idEnEdicion, actualizada);
            refrescarLista();
            vista.mostrarMensaje("Alícuota actualizada correctamente.");
            vista.limpiarCampos();
            cancelarEdicion();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al actualizar: " + ex.getMessage());
        }
    }
}
