package Controlador;

import Modelo.Arriendo;
import Modelo.AlmacenarArriendos;
import Vista.interfaz_arriendos;
import Vista.Formulario_Arriendos;
import Vista.Formulario_Historial_Arriendos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.JFrame;

public class controlador_arriendos {

    private final interfaz_arriendos  vista;
    private final AlmacenarArriendos  repo;
    private final JFrame              menuPadre;
    private ArrayList<Arriendo>       listaLocal = new ArrayList<>();
    private String                    idEnEdicion = null;

    // Patrón: LETRAS_MAYUSCULAS-NÚMERO (ej: JUNIO-2026)
    private static final Pattern PERIODO_PATRON =
        Pattern.compile("^[A-ZÁÉÍÓÚÑ]+-\\d{4}$");

    public controlador_arriendos(interfaz_arriendos vista, JFrame menuPadre) {
        this.vista      = vista;
        this.menuPadre  = menuPadre;
        this.repo       = new AlmacenarArriendos();

        listaLocal.addAll(repo.obtenerTodos());
        vista.actualizarTabla(listaLocal);

        vista.getBtnGuardar().addActionListener(e -> {
            if (idEnEdicion != null) ejecutarActualizacion();
            else                     guardar();
        });
        vista.getBtnEditar().addActionListener(e    -> cargarParaEditar());
        vista.getBtnEliminar().addActionListener(e  -> eliminar());
        vista.getBtnHistorial().addActionListener(e -> new Formulario_Historial_Arriendos().setVisible(true));

        if (vista instanceof JFrame) {
            ((JFrame) vista).addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) {
                    if (menuPadre != null) menuPadre.setVisible(true);
                }
            });
        }
    }

    // ── Validar y construir Arriendo desde la vista ───────────────
    private Arriendo construirYValidar(String idExcluir) {
        String tipo = vista.getTipoEspacio();

        String nombreEspacio = vista.getNombreEspacio();
        if (nombreEspacio.isEmpty()) {
            vista.mostrarMensaje("Debe ingresar el nombre del local o parqueadero."); return null;
        }

        String nombre = vista.getNombreArrendatario();
        if (nombre.isEmpty()) {
            vista.mostrarMensaje("Debe ingresar el nombre del arrendatario."); return null;
        }

        String tipoArr = vista.getTipoArrendatario();
        String casa    = "";
        if ("Residente".equals(tipoArr)) {
            casa = vista.getNumeroCasaResidente();
            if (casa.isEmpty()) {
                vista.mostrarMensaje("Debe ingresar el número de casa del residente."); return null;
            }
        }

        // Teléfono: solo números
        String tel = vista.getTelefono();
        if (tel.isEmpty()) { vista.mostrarMensaje("Debe ingresar el teléfono."); return null; }
        if (!tel.matches("\\d+")) { vista.mostrarMensaje("El teléfono solo debe contener números."); return null; }

        // Email: debe tener @
        String email = vista.getEmail();
        if (email.isEmpty()) { vista.mostrarMensaje("Debe ingresar el email."); return null; }
        if (!email.contains("@")) { vista.mostrarMensaje("El email debe contener '@'."); return null; }

        double monto;
        try {
            monto = vista.getMontoMensual();
            if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return null; }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El monto ingresado no es válido."); return null;
        }

        // Período: formato MES-AÑO en mayúsculas
        String periodo = vista.getMesPeriodo().trim().toUpperCase();
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
                vista.mostrarMensaje("Debe ingresar el número de transacción para " + formaPago + "."); 
                return null;
            }
        }

        LocalDate fechaPago = vista.getFechaPago(); // null si no es Pagado

        return new Arriendo(tipo, nombreEspacio, nombre, tipoArr, casa,
                tel, email, monto, periodo, estado, formaPago,
                numTx, fechaPago, LocalDateTime.now());
    }

    // ── Guardar nuevo ─────────────────────────────────────────────
    private void guardar() {
        Arriendo a = construirYValidar(null);
        if (a == null) return;
        try {
            repo.guardar(a);
            refrescarLista();
            vista.mostrarMensaje("Arriendo registrado correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    // ── Cargar seleccionado para editar ───────────────────────────
    private void cargarParaEditar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione un arriendo de la tabla para modificar."); return; }
        Arriendo sel = buscarPorId(id);
        if (sel == null) { vista.mostrarMensaje("No se pudo obtener los datos del arriendo."); return; }
        vista.precargarEdicion(sel);
        idEnEdicion = id;
        vista.getBtnGuardar().setText("Actualizar");
        vista.getBtnGuardar().setBackground(new java.awt.Color(30, 100, 180));
        vista.mostrarMensaje("Arriendo cargado para edición.\nModifique los campos y presione 'Actualizar'.");
    }

    // ── Actualizar ────────────────────────────────────────────────
    private void ejecutarActualizacion() {
        Arriendo a = construirYValidar(idEnEdicion);
        if (a == null) return;
        Arriendo original = buscarPorId(idEnEdicion);
        if (original != null) a.setFechaRegistro(original.getFechaRegistro());
        try {
            repo.actualizar(idEnEdicion, a);
            idEnEdicion = null;
            vista.getBtnGuardar().setText("Guardar");
            vista.getBtnGuardar().setBackground(java.awt.Color.BLACK);
            refrescarLista();
            vista.mostrarMensaje("Arriendo actualizado correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al actualizar: " + ex.getMessage());
        }
    }

    // ── Eliminar ──────────────────────────────────────────────────
    private void eliminar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione un arriendo de la tabla para eliminar."); return; }
        Arriendo sel = buscarPorId(id);
        String desc = sel != null ? sel.getNombreArrendatario() + " — " + sel.getMesPeriodo() : id;
        int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de eliminar el arriendo de:\n" + desc + "?\n\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.eliminar(id);
            if (id.equals(idEnEdicion)) { idEnEdicion = null; }
            refrescarLista();
            vista.limpiarCampos();
            vista.mostrarMensaje("Arriendo eliminado correctamente.");
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al eliminar: " + ex.getMessage());
        }
    }

    private Arriendo buscarPorId(String id) {
        for (Arriendo a : listaLocal) { if (id.equals(a.getId())) return a; }
        return null;
    }

    private void refrescarLista() {
        listaLocal = repo.obtenerTodos();
        vista.actualizarTabla(listaLocal);
    }

    public void iniciar() { vista.iniciar(); }
}
