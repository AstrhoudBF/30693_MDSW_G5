package Controlador;

import Modelo.Arriendo;
import Modelo.AlmacenarArriendos;
import Modelo.validaciones;
import Vista.interfaz_arriendos;
import Vista.Formulario_Arriendos;
import Vista.Formulario_Historial_Arriendos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

    // Meses válidos para el campo Período (en mayúsculas).
    private static final Set<String> MESES_VALIDOS = new HashSet<>(Arrays.asList(
        "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
        "JULIO", "AGOSTO", "SEPTIEMBRE", "SETIEMBRE", "OCTUBRE",
        "NOVIEMBRE", "DICIEMBRE"
    ));

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
        ArrayList<String> vacios = new ArrayList<>();

        String tipo = vista.getTipoEspacio();

        String nombreEspacio = vista.getNombreEspacio();
        if (nombreEspacio.isEmpty()) vacios.add("Nombre del local/parqueadero");

        String nombre = vista.getNombreArrendatario();
        if (nombre.isEmpty()) vacios.add("Nombre del arrendatario");

        String tipoArr = vista.getTipoArrendatario();
        String casa    = "";
        if ("Residente".equals(tipoArr)) {
            casa = vista.getNumeroCasaResidente();
            if (casa.isEmpty()) vacios.add("N° Casa del residente");
        }

        // Teléfono: solo números y 10 dígitos
        String tel = vista.getTelefono();
        if (tel.isEmpty()) vacios.add("Teléfono");

        // Email: debe tener @
        String email = vista.getEmail();
        if (email.isEmpty()) vacios.add("Email");

        // Monto
        double monto = 0;
        if (vista.getMontoMensualTexto().isEmpty()) {
            vacios.add("Monto mensual");
        } else {
            try {
                monto = vista.getMontoMensual();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return null; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return null;
            }
        }

        // Período: formato MES-AÑO en mayúsculas y mes válido
        String periodo = vista.getMesPeriodo().trim().toUpperCase();
        if (periodo.isEmpty()) {
            vacios.add("Período");
        } else if (!PERIODO_PATRON.matcher(periodo).matches()) {
            vista.mostrarMensaje("El período debe tener el formato MES-AÑO en mayúsculas.\nEjemplo: JUNIO-2026"); return null;
        } else {
            String mes = periodo.substring(0, periodo.indexOf('-'));
            if (!MESES_VALIDOS.contains(mes)) {
                vista.mostrarMensaje("El mes del período no es válido.\nMeses permitidos: ENERO, FEBRERO, MARZO, ABRIL, MAYO, JUNIO, JULIO, AGOSTO, SEPTIEMBRE, OCTUBRE, NOVIEMBRE, DICIEMBRE."); return null;
            }
        }

        // Si hay campos vacíos, mostrar un solo mensaje consolidado
        if (!vacios.isEmpty()) {
            vista.mostrarMensaje("Debe completar los siguientes campos obligatorios:\n• "
                + String.join("\n• ", vacios));
            return null;
        }

        // Validaciones de formato adicionales (teléfono ecuatoriano y email)
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

    // ── Anular (cambia estado a Cancelado, NO elimina) ─────────────
    private void eliminar() {
        String id = vista.getIdSeleccionado();
        if (id == null) { vista.mostrarMensaje("Seleccione un arriendo de la tabla para anular."); return; }
        Arriendo sel = buscarPorId(id);
        String desc = sel != null ? sel.getNombreArrendatario() + " — " + sel.getMesPeriodo() : id;
        if ("Cancelado".equals(sel != null ? sel.getEstado() : "")) {
            vista.mostrarMensaje("Este arriendo ya está anulado."); return;
        }
        int confirm = javax.swing.JOptionPane.showConfirmDialog(null,
            "¿Está seguro de ANULAR el arriendo de:\n" + desc + "?\n\nEl registro permanecerá en el historial con estado 'Cancelado'.",
            "Confirmar anulación",
            javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE);
        if (confirm != javax.swing.JOptionPane.YES_OPTION) return;
        try {
            repo.anular(id);
            if (id.equals(idEnEdicion)) { idEnEdicion = null; }
            refrescarLista();
            vista.limpiarCampos();
            vista.mostrarMensaje("Arriendo anulado correctamente. El registro permanece en el historial.");
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al anular: " + ex.getMessage());
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
