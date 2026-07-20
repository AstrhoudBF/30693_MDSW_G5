package Vista;

import Modelo.Multa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Formulario_Multas extends JFrame implements interfaz_multas {

    private DefaultTableModel modeloTabla;
    private ArrayList<String> idsTabla = new ArrayList<>();

    // ── Campos del formulario ─────────────────────────────────────
    private JComboBox<String> comboCasa;
    private JTextField        txtCedula;
    private JTextField        txtNombre;
    private JComboBox<String> comboCategoria;
    private JTextArea         txtMotivo;
    private JSpinner          spinnerFechaInfraccion;
    private JTextField        txtMonto;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;

    // Campos de pago condicionales
    private JLabel            lblNumTx;
    private JTextField        txtNumTx;
    private JLabel            lblFechaPago;
    private JSpinner          spinnerFechaPago;

    private JTextArea         txtObservaciones;

    // ── Botones ───────────────────────────────────────────────────
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnRegresar;

    // ── Tabla ─────────────────────────────────────────────────────
    private JTable tablaMultas;

    public Formulario_Multas() {
        initComponents();
        inicializarTabla();
        agregarListeners();
    }

    private void initComponents() {
        setTitle("Gestión de Multas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 810);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        JLabel lblTit = new JLabel("Gestión de Multas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setBounds(15, 10, 350, 28);
        pnl.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ══════════════════════════════════════════════════════════
        JPanel pnlForm = seccion("Datos de la Multa", 15, 45, 1060, 380);
        pnl.add(pnlForm);

        // Fila 1 — Casa + Cédula + Residente
        addLbl(pnlForm, "N° Casa: *", 10, 22, 100, 22);
        comboCasa = new JComboBox<>();
        comboCasa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCasa.addItem("-- Seleccione --");
        for (int i = 1; i <= 20; i++) comboCasa.addItem(String.valueOf(i));
        comboCasa.setBounds(115, 22, 100, 26);
        pnlForm.add(comboCasa);

        addLbl(pnlForm, "Cédula:", 230, 22, 70, 22);
        txtCedula = campo();
        txtCedula.setEditable(false);
        txtCedula.setBackground(new Color(235, 235, 235));
        txtCedula.setBounds(305, 22, 140, 26);
        pnlForm.add(txtCedula);

        addLbl(pnlForm, "Residente:", 460, 22, 80, 22);
        txtNombre = campo();
        txtNombre.setEditable(false);
        txtNombre.setBackground(new Color(235, 235, 235));
        txtNombre.setBounds(545, 22, 490, 26);
        pnlForm.add(txtNombre);

        // Fila 2 — Categoría + Fecha infracción + Monto + Estado
        addLbl(pnlForm, "Categoría: *", 10, 62, 100, 22);
        comboCategoria = new JComboBox<>(new String[]{"Mascotas","Minga","Asamblea","Parqueaderos"});
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCategoria.setBounds(115, 62, 160, 26);
        pnlForm.add(comboCategoria);

        addLbl(pnlForm, "Fecha infracción: *", 295, 62, 145, 22);
        spinnerFechaInfraccion = mkSpinner();
        spinnerFechaInfraccion.setBounds(445, 62, 130, 26);
        pnlForm.add(spinnerFechaInfraccion);

        addLbl(pnlForm, "Monto ($): *", 595, 62, 90, 22);
        txtMonto = campo();
        txtMonto.setBounds(690, 62, 100, 26);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Estado: *", 805, 62, 75, 22);
        comboEstado = new JComboBox<>(new String[]{"Pendiente","Pagada","Anulada"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(885, 62, 140, 26);
        pnlForm.add(comboEstado);

        // Fila 3 — Forma de pago + campos condicionales
        addLbl(pnlForm, "Forma de pago:", 10, 102, 130, 22);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo","Transferencia","Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(145, 102, 150, 26);
        pnlForm.add(comboFormaPago);

        // N° Transacción (solo Transferencia/Depósito y estado Pagada)
        lblNumTx = addLblRet(pnlForm, "N° Transacción:", 315, 102, 130, 22);
        txtNumTx = campo();
        txtNumTx.setBounds(450, 102, 180, 26);
        pnlForm.add(txtNumTx);
        lblNumTx.setVisible(false); txtNumTx.setVisible(false);

        // Fecha de pago (siempre visible pero habilitada solo si Pagada)
        lblFechaPago = addLblRet(pnlForm, "Fecha de pago:", 650, 102, 125, 22);
        spinnerFechaPago = mkSpinner();
        spinnerFechaPago.setBounds(780, 102, 130, 26);
        pnlForm.add(spinnerFechaPago);
        lblFechaPago.setVisible(false); spinnerFechaPago.setVisible(false);

        // Fila 4 — Motivo
        addLbl(pnlForm, "Motivo: *", 10, 142, 100, 22);
        txtMotivo = new JTextArea();
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMotivo.setLineWrap(true); txtMotivo.setWrapStyleWord(true);
        txtMotivo.setToolTipText("Ej: Desechos en el parque infantil");
        JScrollPane scrMotivo = new JScrollPane(txtMotivo);
        scrMotivo.setBounds(115, 142, 920, 60);
        pnlForm.add(scrMotivo);

        // Fila 5 — Observaciones
        addLbl(pnlForm, "Observaciones:", 10, 213, 110, 22);
        txtObservaciones = new JTextArea();
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true); txtObservaciones.setWrapStyleWord(true);
        JScrollPane scrObs = new JScrollPane(txtObservaciones);
        scrObs.setBounds(115, 213, 920, 55);
        pnlForm.add(scrObs);

        // Botones
        btnGuardar   = mkBtn("Guardar",      Color.BLACK,              Color.WHITE);
        btnModificar = mkBtn("✏ Modificar",  new Color(30, 100, 180), Color.WHITE);
        btnEliminar  = mkBtn("🗑 Eliminar",  new Color(180, 30, 30),  Color.WHITE);
        btnRegresar  = mkBtn("← Regresar",   new Color(80, 80, 80),   Color.WHITE);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.setBounds(10,  325, 140, 32);
        btnModificar.setBounds(160, 325, 140, 32);
        btnEliminar.setBounds(310, 325, 140, 32);
        btnRegresar.setBounds(460, 325, 140, 32);
        pnlForm.add(btnGuardar); pnlForm.add(btnModificar);
        pnlForm.add(btnEliminar); pnlForm.add(btnRegresar);

        JLabel lblNota = new JLabel("Seleccione una fila para modificar o eliminar.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(100, 100, 100));
        lblNota.setBounds(620, 333, 420, 18);
        pnlForm.add(lblNota);

        // Tabla
        JLabel lblTbl = new JLabel("Registro de multas:");
        lblTbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTbl.setBounds(15, 433, 250, 22);
        pnl.add(lblTbl);

        tablaMultas = new JTable();
        tablaMultas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaMultas.setRowHeight(21);
        tablaMultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMultas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaMultas.getTableHeader().setBackground(Color.WHITE);
        tablaMultas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaMultas.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaMultas.getSelectedRow() >= 0;
            btnModificar.setEnabled(hay);
            btnEliminar.setEnabled(hay);
        });

        JScrollPane scrollTbl = new JScrollPane(tablaMultas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollTbl.setBounds(15, 458, 1060, 318);
        pnl.add(scrollTbl);
    }

    private void agregarListeners() {
        comboFormaPago.addActionListener(e -> actualizarCamposPago());
        comboEstado.addActionListener(e    -> actualizarCamposPago());
        actualizarCamposPago();
    }

    /**
     * Reglas:
     * - Si Pendiente o Anulada → campos de pago DESHABILITADOS (no fecha, no N° Tx)
     * - Si Pagada:
     *     Efectivo            → solo Fecha de pago habilitada
     *     Transferencia/Depósito → N° Transacción + Fecha de pago habilitados
     */
    private void actualizarCamposPago() {
        String fp     = (String) comboFormaPago.getSelectedItem();
        String estado = (String) comboEstado.getSelectedItem();
        boolean pagada    = "Pagada".equals(estado);
        boolean esTxODep  = "Transferencia".equals(fp) || "Depósito".equals(fp);

        // N° Transacción: visible solo si Transferencia o Depósito
        lblNumTx.setVisible(esTxODep);
        txtNumTx.setVisible(esTxODep);
        txtNumTx.setEnabled(pagada);
        if (!pagada) txtNumTx.setBackground(new Color(235, 235, 235));
        else         txtNumTx.setBackground(Color.WHITE);

        // Fecha de pago: siempre visible, habilitada solo si Pagada
        lblFechaPago.setVisible(true);
        spinnerFechaPago.setVisible(true);
        spinnerFechaPago.setEnabled(pagada);
        lblFechaPago.setForeground(pagada ? Color.BLACK : new Color(160, 160, 160));
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"Casa","Cédula","Residente","Categoría","Motivo",
                         "Fecha Infracción","Monto ($)","Estado",
                         "Forma Pago","N° Tx","Fecha Pago",
                         "Observaciones","Fecha Registro"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaMultas.setModel(modeloTabla);
        int[] w = {55,85,150,90,140,95,70,75,90,90,80,160,115};
        for (int i=0;i<w.length&&i<tablaMultas.getColumnCount();i++)
            tablaMultas.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
    }

    // ── Precarga para edición ──────────────────────────────────────
    public void precargarEdicion(Multa m) {
        comboCasa.setSelectedItem(m.getNumeroCasa());
        txtCedula.setText(ns(m.getCedulaResidente()));
        txtNombre.setText(ns(m.getNombreResidente()));
        for (int i=0;i<comboCategoria.getItemCount();i++)
            if (comboCategoria.getItemAt(i).equals(m.getCategoria())) { comboCategoria.setSelectedIndex(i); break; }
        txtMotivo.setText(ns(m.getMotivo()));
        if (m.getFechaInfraccion()!=null)
            spinnerFechaInfraccion.setValue(Date.from(m.getFechaInfraccion().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        txtMonto.setText(String.valueOf(m.getMonto()));
        for (int i=0;i<comboEstado.getItemCount();i++)
            if (comboEstado.getItemAt(i).equals(m.getEstado())) { comboEstado.setSelectedIndex(i); break; }
        // Forma de pago
        String fp = ns(m.getFormaPago());
        for (int i=0;i<comboFormaPago.getItemCount();i++)
            if (comboFormaPago.getItemAt(i).equals(fp)) { comboFormaPago.setSelectedIndex(i); break; }
        txtNumTx.setText(ns(m.getNumeroTransaccion()));
        if (m.getFechaPago()!=null)
            spinnerFechaPago.setValue(Date.from(m.getFechaPago().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        txtObservaciones.setText(ns(m.getObservaciones()));
        actualizarCamposPago();
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JPanel seccion(String t, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200,200,200)),
            t, 0, 0, new Font("Segoe UI", Font.BOLD, 12)));
        p.setBounds(x,y,w,h); return p;
    }
    private void addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x,y,w,h); p.add(l);
    }
    private JLabel addLblRet(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x,y,w,h); p.add(l); return l;
    }
    private JTextField campo() { JTextField f=new JTextField(); f.setFont(new Font("Segoe UI",Font.PLAIN,13)); return f; }
    private JSpinner mkSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        s.setEditor(new JSpinner.DateEditor(s, "dd/MM/yyyy"));
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return s;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private String ns(String s) { return s!=null?s:""; }
    private LocalDate spinnerToDate(JSpinner sp) {
        return ((Date)sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ── Implementación interfaz_multas ─────────────────────────────
    @Override public String getNumeroCasa() { Object s=comboCasa.getSelectedItem(); return s!=null?s.toString():""; }
    @Override public String getCedulaResidente()    { return txtCedula.getText().trim(); }
    @Override public String getNombreResidente()    { return txtNombre.getText().trim(); }
    @Override public String getCategoria()          { return (String) comboCategoria.getSelectedItem(); }
    @Override public String getMotivo()             { return txtMotivo.getText().trim(); }
    @Override public LocalDate getFechaInfraccion() { return spinnerToDate(spinnerFechaInfraccion); }
    @Override public double getMonto()              { return Double.parseDouble(txtMonto.getText().trim().replace(",",".")); }
    @Override public String getEstado()             { return (String) comboEstado.getSelectedItem(); }
    @Override public String getFormaPago()          { return (String) comboFormaPago.getSelectedItem(); }
    @Override public String getNumeroTransaccion()  { return txtNumTx.getText().trim(); }
    @Override public LocalDate getFechaPago()       { return spinnerFechaPago.isEnabled() ? spinnerToDate(spinnerFechaPago) : null; }
    @Override public String getObservaciones()      { return txtObservaciones.getText().trim(); }

    @Override public void setNombreResidente(String v)  { txtNombre.setText(v); }
    @Override public void setNumeroCasa(String v)       { comboCasa.setSelectedItem(v); }
    @Override public void setCedulaResidente(String v)  { txtCedula.setText(v); }

    @Override public JButton getBtnGuardar()   { return btnGuardar;   }
    @Override public JButton getBtnRegresar()  { return btnRegresar;  }
    @Override public JButton getBtnModificar() { return btnModificar; }
    @Override public JButton getBtnEliminar()  { return btnEliminar;  }
    @Override public JComboBox<String> getComboCasa() { return comboCasa; }

    @Override public String getIdSeleccionado() {
        int f = tablaMultas.getSelectedRow();
        return (f<0||f>=idsTabla.size()) ? null : idsTabla.get(f);
    }

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override public void limpiarCampos() {
        comboCasa.setSelectedIndex(0);
        txtCedula.setText(""); txtNombre.setText("");
        comboCategoria.setSelectedIndex(0);
        txtMotivo.setText("");
        spinnerFechaInfraccion.setValue(new Date());
        txtMonto.setText("");
        comboEstado.setSelectedIndex(0);
        comboFormaPago.setSelectedIndex(0);
        txtNumTx.setText(""); spinnerFechaPago.setValue(new Date());
        txtObservaciones.setText("");
        actualizarCamposPago();
        tablaMultas.clearSelection();
        btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
        btnGuardar.setText("Guardar"); btnGuardar.setBackground(Color.BLACK);
    }

    @Override public void actualizarTabla(ArrayList<Multa> lista) {
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        modeloTabla.setRowCount(0); idsTabla.clear();
        for (Multa m : lista) {
            String fi  = m.getFechaInfraccion()!=null ? m.getFechaInfraccion().format(fmtF) : "—";
            String fp  = m.getFechaPago()!=null       ? m.getFechaPago().format(fmtF)       : "—";
            String reg = m.getFechaRegistro()!=null   ? m.getFechaRegistro().format(fmtR)   : "—";
            String ntx = ns(m.getNumeroTransaccion()).isEmpty() ? "—" : m.getNumeroTransaccion();
            modeloTabla.addRow(new Object[]{
                m.getNumeroCasa(), m.getCedulaResidente(), m.getNombreResidente(),
                m.getCategoria(), m.getMotivo(), fi,
                String.format("%.2f", m.getMonto()), m.getEstado(),
                ns(m.getFormaPago()).isEmpty()?"—":m.getFormaPago(),
                ntx, fp, m.getObservaciones(), reg
            });
            idsTabla.add(m.getId());
        }
        btnModificar.setEnabled(false); btnEliminar.setEnabled(false);
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
