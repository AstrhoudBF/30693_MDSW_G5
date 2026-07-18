package Vista;

import Modelo.ArriendoSede;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Formulario_Arriendos_Sede extends JFrame implements interfaz_arriendos_sede{


    private DefaultTableModel modeloProximas;

    // ── Campos fila 1 ─────────────────────────────────────────────
    private JTextField        txtNombre;
    private JComboBox<String> comboTipoSol;

    // ── Campos fila 2: casa (solo visible si Residente) ───────────
    private JLabel            lblCasa;
    private JTextField        txtCasa;

    // ── Campos fila 3: teléfono + email ──────────────────────────
    private JTextField        txtTelefono;
    private JTextField        txtEmail;

    // ── Campos fila 4: fecha + modalidad ─────────────────────────
    private JSpinner          spinnerFechaReserva;
    private JComboBox<String> comboModalidad;

    // ── Campos fila 5: horas (solo Por Horas) ────────────────────
    private JLabel            lblHoraInicio, lblHoraFin;
    private JTextField        txtHoraInicio, txtHoraFin;

    // ── Campos fila 6: monto + motivo ────────────────────────────
    private JTextField        txtMonto;
    private JTextField        txtMotivo;

    // ── Campos fila 7: estado + forma de pago ────────────────────
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;

    // ── Campos fila 8: pago (condicionales) ──────────────────────
    private JLabel            lblNumTx;
    private JTextField        txtNumTx;
    private JLabel            lblFechaPago;
    private JSpinner          spinnerFechaPago;

    // ── Botones ───────────────────────────────────────────────────
    private JButton           btnGuardar;
    private JButton           btnAnular;
    private JButton           btnHistorial;

    // ── Tablas ────────────────────────────────────────────────────
    private JTable            tablaProximas;

    // Panel formulario (guardado para resize dinámico)
    private JPanel            pnlForm;

    public Formulario_Arriendos_Sede() {
        initComponents();
        inicializarTablas();
        agregarListeners();
    }

    // ─────────────────────────────────────────────────────────────
    private void initComponents() {
        setTitle("Reservas de Sede Social");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 820);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnlPrincipal = new JPanel(null);
        pnlPrincipal.setBackground(Color.WHITE);
        getContentPane().add(pnlPrincipal);

        JLabel lblTit = new JLabel("Reservas de Sede Social");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setBounds(15, 10, 400, 28);
        pnlPrincipal.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ══════════════════════════════════════════════════════════
        pnlForm = new JPanel(null);
        pnlForm.setBackground(new Color(247, 247, 247));
        pnlForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Nueva Reserva", 0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlForm.setBounds(15, 45, 860, 430);
        pnlPrincipal.add(pnlForm);

        int lx = 12, fx = 210, rH = 36, y = 20, lw = 193, fh = 27;

        // ── Fila 1: Nombre + Tipo solicitante ─────────────────────
        lbl(pnlForm, "Nombre solicitante: *", lx, y, lw, fh);
        txtNombre = field();
        txtNombre.setBounds(fx, y, 220, fh);
        pnlForm.add(txtNombre);

        lbl(pnlForm, "Tipo solicitante: *", 450, y, 145, fh);
        comboTipoSol = new JComboBox<>(new String[]{"Residente", "Externo"});
        comboTipoSol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoSol.setBounds(598, y, 140, fh);
        pnlForm.add(comboTipoSol);

        // ── Fila 2: N° Casa (solo visible si Residente) ───────────
        y += rH;
        lblCasa = lbl(pnlForm, "N° Casa: *", lx, y, lw, fh);
        txtCasa = field();
        txtCasa.setToolTipText("Número de vivienda del residente");
        txtCasa.setBounds(fx, y, 100, fh);
        pnlForm.add(txtCasa);
        // por defecto oculto hasta que se elija "Residente"
        lblCasa.setVisible(false);
        txtCasa.setVisible(false);

        // ── Fila 3: Teléfono + Email ──────────────────────────────
        y += rH;
        lbl(pnlForm, "Teléfono: *", lx, y, lw, fh);
        txtTelefono = field();
        txtTelefono.setToolTipText("Solo números");
        txtTelefono.setBounds(fx, y, 150, fh);
        pnlForm.add(txtTelefono);

        lbl(pnlForm, "Email: *", 380, y, 70, fh);
        txtEmail = field();
        txtEmail.setToolTipText("Debe contener @");
        txtEmail.setBounds(455, y, 280, fh);
        pnlForm.add(txtEmail);

        // ── Fila 4: Fecha reserva + Modalidad ────────────────────
        y += rH;
        lbl(pnlForm, "Fecha de reserva: *", lx, y, lw, fh);
        spinnerFechaReserva = makeSpinner();
        spinnerFechaReserva.setBounds(fx, y, 140, fh);
        pnlForm.add(spinnerFechaReserva);

        lbl(pnlForm, "Modalidad:", 380, y, 100, fh);
        comboModalidad = new JComboBox<>(new String[]{"Día Completo", "Por Horas"});
        comboModalidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboModalidad.setBounds(485, y, 150, fh);
        pnlForm.add(comboModalidad);

        // ── Fila 5: Horas (ocultas por defecto) ──────────────────
        y += rH;
        lblHoraInicio = lbl(pnlForm, "Hora inicio (HH:mm):", lx, y, lw, fh);
        txtHoraInicio = field();
        txtHoraInicio.setToolTipText("Ej: 09:00");
        txtHoraInicio.setBounds(fx, y, 100, fh);
        pnlForm.add(txtHoraInicio);

        lblHoraFin = lbl(pnlForm, "Hora fin (HH:mm):", 330, y, 140, fh);
        txtHoraFin = field();
        txtHoraFin.setToolTipText("Ej: 13:00");
        txtHoraFin.setBounds(475, y, 100, fh);
        pnlForm.add(txtHoraFin);

        lblHoraInicio.setVisible(false); txtHoraInicio.setVisible(false);
        lblHoraFin.setVisible(false);    txtHoraFin.setVisible(false);

        // ── Fila 6: Monto + Motivo ────────────────────────────────
        y += rH;
        lbl(pnlForm, "Monto ($): *", lx, y, lw, fh);
        txtMonto = field();
        txtMonto.setBounds(fx, y, 110, fh);
        pnlForm.add(txtMonto);

        lbl(pnlForm, "Motivo / Evento: *", 340, y, 140, fh);
        txtMotivo = field();
        txtMotivo.setToolTipText("Ej: Cumpleaños, Asamblea, Reunión...");
        txtMotivo.setBounds(485, y, 360, fh);
        pnlForm.add(txtMotivo);

        // ── Fila 7: Estado + Forma de pago ───────────────────────
        y += rH;
        lbl(pnlForm, "Estado: *", lx, y, lw, fh);
        comboEstado = new JComboBox<>(new String[]{"Pendiente", "Confirmada", "Cancelada"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(fx, y, 140, fh);
        pnlForm.add(comboEstado);

        lbl(pnlForm, "Forma de pago:", 380, y, 130, fh);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(515, y, 150, fh);
        pnlForm.add(comboFormaPago);

        // ── Fila 8: Datos de pago (condicionales) ─────────────────
        y += rH;
        lblNumTx = lbl(pnlForm, "N° Transacción:", lx, y, lw, fh);
        txtNumTx = field();
        txtNumTx.setBounds(fx, y, 180, fh);
        pnlForm.add(txtNumTx);
        lblNumTx.setVisible(false); txtNumTx.setVisible(false);

        lblFechaPago = lbl(pnlForm, "Fecha de pago:", 400, y, 130, fh);
        spinnerFechaPago = makeSpinner();
        spinnerFechaPago.setBounds(535, y, 140, fh);
        pnlForm.add(spinnerFechaPago);
        lblFechaPago.setVisible(false); spinnerFechaPago.setVisible(false);

        // ── Botones ───────────────────────────────────────────────
        y += rH + 10;
        btnGuardar = mkBtn("Guardar",          Color.BLACK,            Color.WHITE);
        btnAnular  = mkBtn("🚫 Anular reserva", new Color(150, 80, 0),  Color.WHITE);

        btnGuardar.setBounds(fx,      y, 130, 30);
        btnAnular.setBounds(fx+145,   y, 160, 30);
        pnlForm.add(btnGuardar);
        pnlForm.add(btnAnular);

        JLabel lblNota = new JLabel("Anular: abre el historial para seleccionar y anular una reserva.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(110, 110, 110));
        lblNota.setBounds(fx + 320, y + 8, 500, 18);
        pnlForm.add(lblNota);

        // ── Panel próximas ────────────────────────────────────────
        JPanel pnlProx = new JPanel(null);
        pnlProx.setBackground(new Color(255, 248, 220));
        pnlProx.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 180, 0)),
            "⚠ Reservas próximas (consultar antes de agendar)", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlProx.setBounds(15, 482, 860, 125);
        pnlPrincipal.add(pnlProx);

        tablaProximas = buildTable();
        tablaProximas.setEnabled(false);
        tablaProximas.getTableHeader().setBackground(new Color(255, 248, 220));
        JScrollPane scrollProx = new JScrollPane(tablaProximas);
        scrollProx.setBounds(8, 20, 842, 95);
        pnlProx.add(scrollProx);

        setSize(900, 640);
    }

    private JSpinner makeSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy"));
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return s;
    }

    private JTable buildTable() {
        JTable t = new JTable();
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(20);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return t;
    }

    private void inicializarTablas() {
        modeloProximas = new DefaultTableModel(
            new String[]{"Fecha", "Solicitante", "Tipo", "Modalidad",
                         "H.Inicio", "H.Fin", "Motivo", "Estado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaProximas.setModel(modeloProximas);
    }

    private void agregarListeners() {
        // Tipo solicitante → mostrar/ocultar campo casa
        comboTipoSol.addActionListener(e -> {
            boolean esResidente = "Residente".equals(comboTipoSol.getSelectedItem());
            lblCasa.setVisible(esResidente);
            txtCasa.setVisible(esResidente);
        });
        // N° Casa → autocompletar datos del residente (solo si Tipo = Residente)
        txtCasa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { autocompletarPorCasa(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { autocompletarPorCasa(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { autocompletarPorCasa(); }
        });
        // Modalidad → mostrar/ocultar horas
        comboModalidad.addActionListener(e ->
            setHorasVisible("Por Horas".equals(comboModalidad.getSelectedItem()))
        );
        // Forma de pago + Estado → campos condicionales de pago
        comboFormaPago.addActionListener(e -> actualizarCamposPago());
        comboEstado.addActionListener(e    -> actualizarCamposPago());

        // Estado inicial
        actualizarCamposPago();
    }

    /** Autocompleta nombre y teléfono del residente a partir del N° de Casa. */
    private void autocompletarPorCasa() {
        if (!"Residente".equals(comboTipoSol.getSelectedItem())) return;
        String casa = txtCasa.getText().trim();
        if (casa.isEmpty()) {
            txtNombre.setText("");
            txtTelefono.setText("");
            return;
        }
        Modelo.AlmacenarArriendosSede repo = new Modelo.AlmacenarArriendosSede();
        String[] datos = repo.obtenerDatosResidentePorCasa(casa);
        if (datos != null) {
            txtNombre.setText(datos[0]);
            txtTelefono.setText(datos[1]);
        } else {
            txtNombre.setText("Sin residente activo asignado");
            txtTelefono.setText("");
            JOptionPane.showMessageDialog(this,
                "La casa N° " + casa + " no tiene un residente activo registrado.\n" +
                "Ingrese los datos manualmente o registre al residente primero.",
                "Sin residente activo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /** Muestra/oculta los campos N° Transacción y Fecha de Pago según forma de pago y estado. */
    private void actualizarCamposPago() {
        String fp     = (String) comboFormaPago.getSelectedItem();
        String estado = (String) comboEstado.getSelectedItem();
        boolean esPendiente = "Pendiente".equals(estado);
        boolean esTxODep    = "Transferencia".equals(fp) || "Depósito".equals(fp);

        // N° Transacción: solo para Transferencia/Depósito
        lblNumTx.setVisible(esTxODep);
        txtNumTx.setVisible(esTxODep);

        // Fecha de pago: para todos, pero deshabilitada si estado=Pendiente
        lblFechaPago.setVisible(true);
        spinnerFechaPago.setVisible(true);
        spinnerFechaPago.setEnabled(!esPendiente);
        lblFechaPago.setForeground(esPendiente ? new Color(180, 180, 180) : Color.BLACK);
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JLabel lbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x, y, w, h);
        p.add(l); return l;
    }
    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return f;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private LocalDate spinnerToDate(JSpinner sp) {
        Date d = (Date) sp.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    private String nulo(String s) { return s != null ? s : ""; }

    // ── Implementación interfaz_arriendos_sede ─────────────────────
    @Override public String    getNombreSolicitante()   { return txtNombre.getText().trim(); }
    @Override public String    getTipoSolicitante()     { return (String) comboTipoSol.getSelectedItem(); }
    @Override public String    getNumeroCasaResidente() { return txtCasa.getText().trim(); }
    @Override public String    getTelefono()            { return txtTelefono.getText().trim(); }
    @Override public String    getEmail()               { return txtEmail.getText().trim(); }
    @Override public LocalDate getFechaReserva()        { return spinnerToDate(spinnerFechaReserva); }
    @Override public String    getModalidad()           { return (String) comboModalidad.getSelectedItem(); }
    @Override public String    getHoraInicio()          { return txtHoraInicio.getText().trim(); }
    @Override public String    getHoraFin()             { return txtHoraFin.getText().trim(); }
    @Override public double    getMonto() {
        return Double.parseDouble(txtMonto.getText().trim().replace(",", "."));
    }
    @Override public String    getMontoTexto()           { return txtMonto.getText().trim(); }
    @Override public String    getEstado()              { return (String) comboEstado.getSelectedItem(); }
    @Override public String    getFormaPago()           { return (String) comboFormaPago.getSelectedItem(); }
    @Override public String    getNumeroTransaccion()   { return txtNumTx.getText().trim(); }
    @Override public LocalDate getFechaPago() {
        return spinnerFechaPago.isEnabled() ? spinnerToDate(spinnerFechaPago) : null;
    }
    @Override public String    getMotivo()              { return txtMotivo.getText().trim(); }

    // ID en edición gestionado por el controlador
    private String idEnEdicionExterno = null;
    public void setIdEnEdicionExterno(String id) { this.idEnEdicionExterno = id; }
    public String getIdEnEdicionExterno()         { return idEnEdicionExterno; }

    @Override
    public String getIdSeleccionado() {
        // La selección ahora se gestiona en Formulario_Historial_Sede
        return null;
    }

    @Override
    public void precargarEdicion(ArriendoSede s) {
        txtNombre.setText(nulo(s.getNombreSolicitante()));

        // Tipo solicitante
        comboTipoSol.setSelectedItem(s.getTipoSolicitante());
        boolean esRes = "Residente".equals(s.getTipoSolicitante());
        lblCasa.setVisible(esRes); txtCasa.setVisible(esRes);
        txtCasa.setText(nulo(s.getNumeroCasaResidente()));

        txtTelefono.setText(nulo(s.getTelefono()));
        txtEmail.setText(nulo(s.getEmail()));

        // Fecha reserva
        if (s.getFechaReserva() != null)
            spinnerFechaReserva.setValue(Date.from(s.getFechaReserva().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        comboModalidad.setSelectedItem(s.getModalidad());
        setHorasVisible("Por Horas".equals(s.getModalidad()));
        txtHoraInicio.setText(nulo(s.getHoraInicio()));
        txtHoraFin.setText(nulo(s.getHoraFin()));
        txtMonto.setText(String.valueOf(s.getMonto()));
        txtMotivo.setText(nulo(s.getMotivo()));
        comboEstado.setSelectedItem(s.getEstado());
        comboFormaPago.setSelectedItem(s.getFormaPago());
        txtNumTx.setText(nulo(s.getNumeroTransaccion()));

        if (s.getFechaPago() != null)
            spinnerFechaPago.setValue(Date.from(s.getFechaPago().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        actualizarCamposPago();
    }

    @Override public JButton getBtnGuardar()   { return btnGuardar;   }
    @Override public JButton getBtnAnular()    { return btnAnular;    }

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        txtNombre.setText("");
        comboTipoSol.setSelectedIndex(0);
        lblCasa.setVisible(false); txtCasa.setText(""); txtCasa.setVisible(false);
        txtTelefono.setText(""); txtEmail.setText("");
        spinnerFechaReserva.setValue(new Date());
        comboModalidad.setSelectedIndex(0);
        setHorasVisible(false);
        txtHoraInicio.setText(""); txtHoraFin.setText("");
        txtMonto.setText(""); txtMotivo.setText("");
        comboEstado.setSelectedIndex(0);
        comboFormaPago.setSelectedIndex(0);
        txtNumTx.setText("");
        spinnerFechaPago.setValue(new Date());
        actualizarCamposPago();
        idEnEdicionExterno = null;
        btnGuardar.setText("Guardar");
        btnGuardar.setBackground(Color.BLACK);
    }

    @Override
    public void setHorasVisible(boolean v) {
        lblHoraInicio.setVisible(v); txtHoraInicio.setVisible(v);
        lblHoraFin.setVisible(v);    txtHoraFin.setVisible(v);
    }

    @Override
    public void actualizarTablaProximas(ArrayList<ArriendoSede> lista) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modeloProximas.setRowCount(0);
        for (ArriendoSede s : lista) {
            modeloProximas.addRow(new Object[]{
                s.getFechaReserva()  != null ? s.getFechaReserva().format(fmt) : "—",
                s.getNombreSolicitante(), s.getTipoSolicitante(), s.getModalidad(),
                nulo(s.getHoraInicio()).isEmpty() ? "—" : s.getHoraInicio(),
                nulo(s.getHoraFin()).isEmpty()    ? "—" : s.getHoraFin(),
                s.getMotivo(), s.getEstado()
            });
        }
    }

    @Override
    public void actualizarTablaReservas(ArrayList<ArriendoSede> lista) {
        // La tabla de historial se muestra en Formulario_Historial_Sede.
        // Este método no hace nada en el formulario principal.
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
