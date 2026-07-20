package Vista;

import Modelo.Arriendo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Formulario_Arriendos extends JFrame implements interfaz_arriendos {

    private DefaultTableModel modeloTabla;
    private ArrayList<String> idsTabla = new ArrayList<>();

    // ── Campos formulario ─────────────────────────────────────────
    private JComboBox<String> comboTipo;
    private JTextField        txtNombreEspacio;
    private JTextField        txtNombre;
    private JComboBox<String> comboTipoArr;
    private JLabel            lblCasa;
    private JTextField        txtCasa;
    private JTextField        txtTelefono;
    private JTextField        txtEmail;
    private JTextField        txtMonto;
    private JTextField        txtPeriodo;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;
    private JLabel            lblNumTx;
    private JTextField        txtNumTx;
    private JLabel            lblFechaPago;
    private JSpinner          spinnerFechaPago;

    // ── Botones ───────────────────────────────────────────────────
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnHistorial;

    // ── Tabla ─────────────────────────────────────────────────────
    private JTable tablaArriendos;

    public Formulario_Arriendos() {
        initComponents();
        inicializarTabla();
        agregarListeners();
    }

    private void initComponents() {
        setTitle("Arriendos — Locales y Parqueaderos");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 740);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTit = lbl("Arriendos — Locales y Parqueaderos", 18);
        lblTit.setBounds(15, 10, 500, 28);
        pnl.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ══════════════════════════════════════════════════════════
        JPanel pnlForm = seccion("Datos del Contrato Mensual", 15, 45, 945, 390);
        pnl.add(pnlForm);

        int lx = 12, fx = 215, rH = 36, y = 20, lw = 198, fh = 27;

        // Fila 1 — Tipo espacio + Nombre del local/parqueadero
        addLbl(pnlForm, "Tipo de espacio: *", lx, y, lw, fh);
        comboTipo = new JComboBox<>(new String[]{"Local", "Parqueadero"});
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipo.setBounds(fx, y, 130, fh);
        pnlForm.add(comboTipo);

        addLbl(pnlForm, "Nombre del local/parqueadero: *", 365, y, 235, fh);
        txtNombreEspacio = field();
        txtNombreEspacio.setToolTipText("Ej: Local Norte, Parqueadero 7");
        txtNombreEspacio.setBounds(603, y, 320, fh);
        pnlForm.add(txtNombreEspacio);

        // Fila 2 — Arrendatario + Tipo arrendatario
        y += rH;
        addLbl(pnlForm, "Nombre arrendatario: *", lx, y, lw, fh);
        txtNombre = field();
        txtNombre.setBounds(fx, y, 210, fh);
        pnlForm.add(txtNombre);

        addLbl(pnlForm, "Tipo arrendatario: *", 445, y, 155, fh);
        comboTipoArr = new JComboBox<>(new String[]{"Residente", "Externo"});
        comboTipoArr.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoArr.setBounds(603, y, 140, fh);
        pnlForm.add(comboTipoArr);

        // Fila 3 — N° Casa (solo si Residente)
        y += rH;
        lblCasa = addLbl(pnlForm, "N° Casa residente:", lx, y, lw, fh);
        txtCasa = field();
        txtCasa.setToolTipText("Número de vivienda del residente");
        txtCasa.setBounds(fx, y, 100, fh);
        pnlForm.add(txtCasa);
        lblCasa.setVisible(false); txtCasa.setVisible(false);

        // Fila 4 — Teléfono + Email
        y += rH;
        addLbl(pnlForm, "Teléfono: *", lx, y, lw, fh);
        txtTelefono = field();
        txtTelefono.setToolTipText("Solo números");
        txtTelefono.setBounds(fx, y, 150, fh);
        pnlForm.add(txtTelefono);

        addLbl(pnlForm, "Email: *", 385, y, 70, fh);
        txtEmail = field();
        txtEmail.setToolTipText("Debe contener @");
        txtEmail.setBounds(460, y, 260, fh);
        pnlForm.add(txtEmail);

        // Fila 5 — Monto + Período
        y += rH;
        addLbl(pnlForm, "Monto mensual ($): *", lx, y, lw, fh);
        txtMonto = field();
        txtMonto.setBounds(fx, y, 120, fh);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Período: *", 358, y, 80, fh);
        txtPeriodo = field();
        txtPeriodo.setToolTipText("Formato: MES-AÑO  Ej: JUNIO-2026");
        txtPeriodo.setBounds(443, y, 160, fh);
        pnlForm.add(txtPeriodo);

        JLabel lblPeriodoHint = new JLabel("(MES-AÑO en mayúsculas)");
        lblPeriodoHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblPeriodoHint.setForeground(new Color(120,120,120));
        lblPeriodoHint.setBounds(612, y + 5, 200, 18);
        pnlForm.add(lblPeriodoHint);

        // Fila 6 — Estado + Forma de pago
        y += rH;
        addLbl(pnlForm, "Estado: *", lx, y, lw, fh);
        comboEstado = new JComboBox<>(new String[]{"Pendiente", "Pagado", "Cancelado"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(fx, y, 140, fh);
        pnlForm.add(comboEstado);

        addLbl(pnlForm, "Forma de pago:", 375, y, 130, fh);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(510, y, 150, fh);
        pnlForm.add(comboFormaPago);

        // Fila 7 — Datos de pago condicionales
        y += rH;
        lblNumTx = addLbl(pnlForm, "N° Transacción:", lx, y, lw, fh);
        txtNumTx = field();
        txtNumTx.setBounds(fx, y, 180, fh);
        pnlForm.add(txtNumTx);
        lblNumTx.setVisible(false); txtNumTx.setVisible(false);

        lblFechaPago = addLbl(pnlForm, "Fecha de pago:", 415, y, 130, fh);
        spinnerFechaPago = new JSpinner(new SpinnerDateModel());
        spinnerFechaPago.setEditor(new JSpinner.DateEditor(spinnerFechaPago, "dd/MM/yyyy"));
        spinnerFechaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerFechaPago.setBounds(548, y, 140, fh);
        pnlForm.add(spinnerFechaPago);
        lblFechaPago.setVisible(false); spinnerFechaPago.setVisible(false);

        // ── Botones ────────────────────────────────────────────────
        y += rH + 8;
        btnGuardar   = mkBtn("Guardar",              Color.BLACK,              Color.WHITE);
        btnEditar    = mkBtn("✏ Editar seleccionado", new Color(30, 100, 180), Color.WHITE);
        btnEliminar  = mkBtn("🗑 Eliminar",           new Color(180, 30, 30),  Color.WHITE);
        btnHistorial = mkBtn("Ver Historial ▸",       new Color(60, 60, 60),   Color.WHITE);

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.setBounds(fx,        y, 145, 30);
        btnEditar.setBounds(fx+155,     y, 185, 30);
        btnEliminar.setBounds(fx+350,   y, 130, 30);
        btnHistorial.setBounds(fx+490,  y, 155, 30);
        pnlForm.add(btnGuardar);
        pnlForm.add(btnEditar);
        pnlForm.add(btnEliminar);
        pnlForm.add(btnHistorial);

        JLabel lblNota = new JLabel("Seleccione una fila de la tabla para editar o eliminar.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(110,110,110));
        lblNota.setBounds(lx, y + 34, 700, 18);
        pnlForm.add(lblNota);

        // ══════════════════════════════════════════════════════════
        // TABLA  (solo scroll, sin edición)
        // ══════════════════════════════════════════════════════════
        JLabel lblHist = lbl("Historial de arriendos:", 13);
        lblHist.setBounds(15, 443, 300, 22);
        pnl.add(lblHist);

        tablaArriendos = new JTable();
        tablaArriendos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaArriendos.setRowHeight(21);
        tablaArriendos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaArriendos.setSelectionBackground(new Color(200, 220, 255));
        tablaArriendos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaArriendos.getTableHeader().setBackground(Color.WHITE);
        tablaArriendos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // scroll horizontal

        tablaArriendos.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaArriendos.getSelectedRow() >= 0;
            btnEditar.setEnabled(hay);
            btnEliminar.setEnabled(hay);
        });

        JScrollPane scroll = new JScrollPane(tablaArriendos,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(15, 468, 945, 238);
        pnl.add(scroll);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"Tipo", "Nombre Espacio", "Arrendatario", "Tipo Arr.", "Casa",
                         "Teléfono", "Email", "Monto ($)", "Período",
                         "Estado", "Forma Pago", "N° Tx", "Fecha Pago", "Fecha Registro"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaArriendos.setModel(modeloTabla);
        // Anchos de columnas
        int[] w = {75, 130, 140, 80, 55, 90, 160, 70, 90, 80, 90, 90, 80, 110};
        for (int i = 0; i < w.length && i < tablaArriendos.getColumnCount(); i++)
            tablaArriendos.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
    }

    private void agregarListeners() {
        // Tipo arrendatario → mostrar/ocultar campo casa
        comboTipoArr.addActionListener(e -> {
            boolean esRes = "Residente".equals(comboTipoArr.getSelectedItem());
            lblCasa.setVisible(esRes); txtCasa.setVisible(esRes);
        });
        // Forma de pago + Estado → campos de pago condicionales
        comboFormaPago.addActionListener(e -> actualizarCamposPago());
        comboEstado.addActionListener(e    -> actualizarCamposPago());
        actualizarCamposPago();
    }

    private void actualizarCamposPago() {
        String fp     = (String) comboFormaPago.getSelectedItem();
        String estado = (String) comboEstado.getSelectedItem();
        boolean esTxODep   = "Transferencia".equals(fp) || "Depósito".equals(fp);
        // Fecha de pago accesible solo cuando estado = Pagado
        boolean fechaHabil  = "Pagado".equals(estado);

        lblNumTx.setVisible(esTxODep); txtNumTx.setVisible(esTxODep);
        lblFechaPago.setVisible(true);  spinnerFechaPago.setVisible(true);
        spinnerFechaPago.setEnabled(fechaHabil);
        lblFechaPago.setForeground(fechaHabil ? Color.BLACK : new Color(180, 180, 180));
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JLabel lbl(String t, int size) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, size)); return l;
    }
    private JLabel addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x,y,w,h); p.add(l); return l;
    }
    private JPanel seccion(String titulo, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 12)));
        p.setBounds(x,y,w,h); return p;
    }
    private JTextField field() {
        JTextField f = new JTextField(); f.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return f;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private LocalDate spinnerToDate() {
        Date d = (Date) spinnerFechaPago.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    private String s(String v) { return v != null ? v : ""; }

    // ── Implementación interfaz_arriendos ──────────────────────────
    @Override public String    getTipoEspacio()          { return (String) comboTipo.getSelectedItem(); }
    @Override public String    getNombreEspacio()        { return txtNombreEspacio.getText().trim(); }
    @Override public String    getNombreArrendatario()   { return txtNombre.getText().trim(); }
    @Override public String    getTipoArrendatario()     { return (String) comboTipoArr.getSelectedItem(); }
    @Override public String    getNumeroCasaResidente()  { return txtCasa.getText().trim(); }
    @Override public String    getTelefono()             { return txtTelefono.getText().trim(); }
    @Override public String    getEmail()                { return txtEmail.getText().trim(); }
    @Override public double    getMontoMensual()         { return Double.parseDouble(txtMonto.getText().trim().replace(",",".")); }
    @Override public String    getMesPeriodo()           { return txtPeriodo.getText().trim(); }
    @Override public String    getEstado()               { return (String) comboEstado.getSelectedItem(); }
    @Override public String    getFormaPago()            { return (String) comboFormaPago.getSelectedItem(); }
    @Override public String    getNumeroTransaccion()    { return txtNumTx.getText().trim(); }
    @Override public LocalDate getFechaPago()            { return spinnerFechaPago.isEnabled() ? spinnerToDate() : null; }
    @Override public JButton   getBtnGuardar()           { return btnGuardar;   }
    @Override public JButton   getBtnEditar()            { return btnEditar;    }
    @Override public JButton   getBtnEliminar()          { return btnEliminar;  }
    @Override public JButton   getBtnHistorial()         { return btnHistorial; }

    @Override
    public String getIdSeleccionado() {
        int fila = tablaArriendos.getSelectedRow();
        if (fila < 0 || fila >= idsTabla.size()) return null;
        return idsTabla.get(fila);
    }

    @Override
    public void precargarEdicion(Arriendo a) {
        comboTipo.setSelectedItem(a.getTipoEspacio());
        txtNombreEspacio.setText(s(a.getNombreEspacio()));
        txtNombre.setText(s(a.getNombreArrendatario()));
        comboTipoArr.setSelectedItem(a.getTipoArrendatario());
        boolean esRes = "Residente".equals(a.getTipoArrendatario());
        lblCasa.setVisible(esRes); txtCasa.setVisible(esRes);
        txtCasa.setText(s(a.getNumeroCasaResidente()));
        txtTelefono.setText(s(a.getTelefono()));
        txtEmail.setText(s(a.getEmail()));
        txtMonto.setText(String.valueOf(a.getMontoMensual()));
        txtPeriodo.setText(s(a.getMesPeriodo()));
        comboEstado.setSelectedItem(a.getEstado());
        comboFormaPago.setSelectedItem(a.getFormaPago());
        txtNumTx.setText(s(a.getNumeroTransaccion()));
        if (a.getFechaPago() != null)
            spinnerFechaPago.setValue(Date.from(a.getFechaPago().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        actualizarCamposPago();
    }

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        comboTipo.setSelectedIndex(0);
        txtNombreEspacio.setText(""); txtNombre.setText("");
        comboTipoArr.setSelectedIndex(0);
        lblCasa.setVisible(false); txtCasa.setText(""); txtCasa.setVisible(false);
        txtTelefono.setText(""); txtEmail.setText("");
        txtMonto.setText(""); txtPeriodo.setText("");
        comboEstado.setSelectedIndex(0); comboFormaPago.setSelectedIndex(0);
        txtNumTx.setText(""); spinnerFechaPago.setValue(new Date());
        actualizarCamposPago();
        tablaArriendos.clearSelection();
        btnEditar.setEnabled(false); btnEliminar.setEnabled(false);
        btnGuardar.setText("Guardar"); btnGuardar.setBackground(Color.BLACK);
    }

    @Override
    public void actualizarTabla(ArrayList<Arriendo> lista) {
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modeloTabla.setRowCount(0);
        idsTabla.clear();
        for (Arriendo a : lista) {
            String reg = a.getFechaRegistro() != null ? a.getFechaRegistro().format(fmtR) : "—";
            String fp  = a.getFechaPago()     != null ? a.getFechaPago().format(fmtF)     : "—";
            String ntx = s(a.getNumeroTransaccion()).isEmpty() ? "—" : a.getNumeroTransaccion();
            String casa = s(a.getNumeroCasaResidente()).isEmpty() ? "—" : a.getNumeroCasaResidente();
            modeloTabla.addRow(new Object[]{
                a.getTipoEspacio(), s(a.getNombreEspacio()),
                a.getNombreArrendatario(), a.getTipoArrendatario(), casa,
                s(a.getTelefono()), s(a.getEmail()),
                String.format("%.2f", a.getMontoMensual()),
                s(a.getMesPeriodo()), a.getEstado(), a.getFormaPago(),
                ntx, fp, reg
            });
            idsTabla.add(a.getId());
        }
        btnEditar.setEnabled(false); btnEliminar.setEnabled(false);
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
