package Vista;

import Modelo.Alicuota;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Formulario_Alicuotas extends JFrame implements interfaz_alicuotas {

    private DefaultTableModel     modeloTabla;
    private ArrayList<String>     idsTabla = new ArrayList<>();

    // ── Campos ────────────────────────────────────────────────────
    private JComboBox<String> comboCasa;
    private JTextField        txtResidente;
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
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnRegresar;

    // ── Tabla ─────────────────────────────────────────────────────
    private JTable tablaAlicuotas;

    public Formulario_Alicuotas() {
        initComponents();
        inicializarTabla();
        agregarListeners();
    }

    private void initComponents() {
        setTitle("Registro de Alícuotas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        JLabel lblTit = lbl("Registro de Alícuotas", 18);
        lblTit.setBounds(15, 10, 300, 28);
        pnl.add(lblTit);

        JPanel pnlForm = seccion("Datos de la Alícuota", 15, 46, 960, 360);
        pnl.add(pnlForm);

        int lx = 12, fx = 210, rH = 36, y = 20, lw = 193, fh = 27;

        // Fila 1 — Casa + Residente (autocompletado)
        addLbl(pnlForm, "N° de Casa: *",    lx, y, lw, fh);
        comboCasa = new JComboBox<>();
        comboCasa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCasa.addItem("-- Seleccione --");
        for (int i = 1; i <= 20; i++) comboCasa.addItem(String.valueOf(i));
        comboCasa.setBounds(fx, y, 110, fh);
        pnlForm.add(comboCasa);

        addLbl(pnlForm, "Residente:", 340, y, 90, fh);
        txtResidente = campo();
        txtResidente.setEditable(false);
        txtResidente.setBackground(new Color(235,235,235));
        txtResidente.setBounds(435, y, 500, fh);
        pnlForm.add(txtResidente);

        // Fila 2 — Teléfono + Email
        y += rH;
        addLbl(pnlForm, "Teléfono: *", lx, y, lw, fh);
        txtTelefono = campo();
        txtTelefono.setToolTipText("Solo números — se completa al elegir la casa");
        txtTelefono.setBounds(fx, y, 160, fh);
        pnlForm.add(txtTelefono);

        addLbl(pnlForm, "Email: *", 390, y, 70, fh);
        txtEmail = campo();
        txtEmail.setToolTipText("Debe contener @");
        txtEmail.setBounds(465, y, 260, fh);
        pnlForm.add(txtEmail);

        // Fila 3 — Monto + Período
        y += rH;
        addLbl(pnlForm, "Monto ($): *", lx, y, lw, fh);
        txtMonto = campo();
        txtMonto.setBounds(fx, y, 120, fh);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Período: *", 352, y, 80, fh);
        txtPeriodo = campo();
        txtPeriodo.setToolTipText("Formato: MES-AÑO  Ej: JUNIO-2026");
        txtPeriodo.setBounds(437, y, 160, fh);
        pnlForm.add(txtPeriodo);
        JLabel lblHint = new JLabel("(MES-AÑO en mayúsculas)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(120,120,120));
        lblHint.setBounds(606, y+5, 200, 18);
        pnlForm.add(lblHint);

        // Fila 4 — Estado + Forma de pago
        y += rH;
        addLbl(pnlForm, "Estado: *", lx, y, lw, fh);
        comboEstado = new JComboBox<>(new String[]{"Pendiente","Pagado","Atrasado","Cancelado"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(fx, y, 140, fh);
        pnlForm.add(comboEstado);

        addLbl(pnlForm, "Forma de pago:", 370, y, 130, fh);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo","Transferencia","Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(505, y, 150, fh);
        pnlForm.add(comboFormaPago);

        // Fila 5 — Campos de pago condicionales
        y += rH;
        lblNumTx = addLbl(pnlForm, "N° Transacción:", lx, y, lw, fh);
        txtNumTx = campo();
        txtNumTx.setBounds(fx, y, 180, fh);
        pnlForm.add(txtNumTx);
        lblNumTx.setVisible(false); txtNumTx.setVisible(false);

        lblFechaPago = addLbl(pnlForm, "Fecha de pago:", 415, y, 130, fh);
        spinnerFechaPago = new JSpinner(new SpinnerDateModel());
        spinnerFechaPago.setEditor(new JSpinner.DateEditor(spinnerFechaPago, "dd/MM/yyyy"));
        spinnerFechaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerFechaPago.setBounds(550, y, 140, fh);
        pnlForm.add(spinnerFechaPago);
        lblFechaPago.setVisible(false); spinnerFechaPago.setVisible(false);

        // ── Botones ────────────────────────────────────────────────
        y += rH + 8;
        btnGuardar    = mkBtn("Guardar",               Color.BLACK,              Color.WHITE);
        btnActualizar = mkBtn("✏ Modificar seleccionada", new Color(30,100,180), Color.WHITE);
        btnEliminar   = mkBtn("🚫 Anular seleccionada",   new Color(200,140,0),  Color.WHITE);
        btnRegresar   = mkBtn("← Regresar",            new Color(80,80,80),      Color.WHITE);

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.setBounds(fx,       y, 145, 30);
        btnActualizar.setBounds(fx+155, y, 210, 30);
        btnEliminar.setBounds(fx+375,  y, 210, 30);
        btnRegresar.setBounds(fx+595,  y, 130, 30);
        pnlForm.add(btnGuardar);
        pnlForm.add(btnActualizar);
        pnlForm.add(btnEliminar);
        pnlForm.add(btnRegresar);

        JLabel lblNota = new JLabel("Seleccione una fila para modificar o anular.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(110,110,110));
        lblNota.setBounds(lx, y+34, 700, 18);
        pnlForm.add(lblNota);

        // ── Tabla con scroll ───────────────────────────────────────
        JLabel lblHist = lbl("Historial de Alícuotas:", 13);
        lblHist.setBounds(15, 414, 300, 22);
        pnl.add(lblHist);

        tablaAlicuotas = new JTable();
        tablaAlicuotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAlicuotas.setRowHeight(21);
        tablaAlicuotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlicuotas.setSelectionBackground(new Color(200,220,255));
        tablaAlicuotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaAlicuotas.getTableHeader().setBackground(Color.WHITE);
        tablaAlicuotas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tablaAlicuotas.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaAlicuotas.getSelectedRow() >= 0;
            btnActualizar.setEnabled(hay);
            btnEliminar.setEnabled(hay);
        });

        JScrollPane scroll = new JScrollPane(tablaAlicuotas,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBounds(15, 440, 960, 250);
        pnl.add(scroll);
    }

    private void agregarListeners() {
        comboFormaPago.addActionListener(e -> actualizarCamposPago());
        comboEstado.addActionListener(e    -> actualizarCamposPago());
        actualizarCamposPago();
    }

    private void actualizarCamposPago() {
        String fp     = (String) comboFormaPago.getSelectedItem();
        String estado = (String) comboEstado.getSelectedItem();
        boolean esTxODep   = "Transferencia".equals(fp) || "Depósito".equals(fp);
        boolean fechaHabil  = "Pagado".equals(estado);

        lblNumTx.setVisible(esTxODep); txtNumTx.setVisible(esTxODep);
        lblFechaPago.setVisible(true);  spinnerFechaPago.setVisible(true);
        spinnerFechaPago.setEnabled(fechaHabil);
        lblFechaPago.setForeground(fechaHabil ? Color.BLACK : new Color(180,180,180));
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"Casa","Residente","Teléfono","Email","Monto ($)",
                         "Período","Estado","Forma Pago","N° Tx","Fecha Pago","Fecha Registro"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaAlicuotas.setModel(modeloTabla);
        int[] w = {55,160,90,160,70,90,75,90,90,80,120};
        for (int i=0; i<w.length && i<tablaAlicuotas.getColumnCount(); i++)
            tablaAlicuotas.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JLabel lbl(String t, int sz) { JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,sz)); return l; }
    private JLabel addLbl(JPanel p,String t,int x,int y,int w,int h) {
        JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,13)); l.setBounds(x,y,w,h); p.add(l); return l;
    }
    private JPanel seccion(String t,int x,int y,int w,int h) {
        JPanel p=new JPanel(null); p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200,200,200)),
            t,0,0,new Font("Segoe UI",Font.BOLD,12)));
        p.setBounds(x,y,w,h); return p;
    }
    private JTextField campo() { JTextField f=new JTextField(); f.setFont(new Font("Segoe UI",Font.PLAIN,13)); return f; }
    private JButton mkBtn(String t,Color bg,Color fg) {
        JButton b=new JButton(t); b.setFont(new Font("Segoe UI",Font.BOLD,12));
        b.setBackground(bg); b.setForeground(fg); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private String s(String v) { return v!=null?v:""; }

    // ── Implementación de interfaz_alicuotas ───────────────────────
    @Override public String getNumeroCasa() { Object o=comboCasa.getSelectedItem(); return o!=null?o.toString():""; }
    @Override public String getNombreResidente()   { return txtResidente.getText(); }
    @Override public String getTelefono()          { return txtTelefono.getText().trim(); }
    @Override public String getEmail()             { return txtEmail.getText().trim(); }
    @Override public double getMonto()             { return Double.parseDouble(txtMonto.getText().trim().replace(",",".")); }
    @Override public String getMontoTexto()        { return txtMonto.getText().trim(); }
    @Override public String getPeriodo()           { return txtPeriodo.getText().trim(); }
    @Override public String getEstado()            { return (String) comboEstado.getSelectedItem(); }
    @Override public String getFormaPago()         { return (String) comboFormaPago.getSelectedItem(); }
    @Override public String getNumeroTransaccion() { return txtNumTx.getText().trim(); }
    @Override public LocalDate getFechaPago()      { return spinnerFechaPago.isEnabled() ? ((Date)spinnerFechaPago.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null; }
    @Override public void setNombreResidente(String n) { txtResidente.setText(n); }
    @Override public void setTelefono(String t)        { txtTelefono.setText(t); }
    @Override public JButton getBtnGuardar()       { return btnGuardar;    }
    @Override public JButton getBtnRegresar()      { return btnRegresar;   }
    @Override public JButton getBtnActualizar()    { return btnActualizar; }
    @Override public JButton getBtnEliminar()      { return btnEliminar;   }
    @Override public JComboBox<String> getComboCasa() { return comboCasa; }

    @Override public String getIdSeleccionado() {
        int f=tablaAlicuotas.getSelectedRow();
        return (f<0||f>=idsTabla.size())?null:idsTabla.get(f);
    }

    @Override public void precargarEdicion(Alicuota a) {
        comboCasa.setSelectedItem(a.getNumeroCasa());
        txtResidente.setText(s(a.getNombreResidente()));
        txtTelefono.setText(s(a.getTelefono()));
        txtEmail.setText(s(a.getEmail()));
        txtMonto.setText(String.valueOf(a.getMonto()));
        txtPeriodo.setText(s(a.getPeriodo()));
        for (int i=0;i<comboEstado.getItemCount();i++)
            if (comboEstado.getItemAt(i).equals(a.getEstado())) { comboEstado.setSelectedIndex(i); break; }
        for (int i=0;i<comboFormaPago.getItemCount();i++)
            if (comboFormaPago.getItemAt(i).equals(a.getFormaPago())) { comboFormaPago.setSelectedIndex(i); break; }
        txtNumTx.setText(s(a.getNumeroTransaccion()));
        if (a.getFechaPago()!=null)
            spinnerFechaPago.setValue(Date.from(a.getFechaPago().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        actualizarCamposPago();
    }

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this,msg); }

    @Override public void limpiarCampos() {
        comboCasa.setSelectedIndex(0); txtResidente.setText("");
        txtTelefono.setText(""); txtEmail.setText("");
        txtMonto.setText(""); txtPeriodo.setText("");
        comboEstado.setSelectedIndex(0); comboFormaPago.setSelectedIndex(0);
        txtNumTx.setText(""); spinnerFechaPago.setValue(new Date());
        actualizarCamposPago();
        tablaAlicuotas.clearSelection();
        btnActualizar.setEnabled(false); btnEliminar.setEnabled(false);
        btnGuardar.setText("Guardar"); btnGuardar.setBackground(Color.BLACK);
    }

    @Override public void actualizarTabla(ArrayList<Alicuota> lista) {
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modeloTabla.setRowCount(0); idsTabla.clear();
        for (Alicuota a : lista) {
            String reg = a.getFechaRegistro()!=null ? a.getFechaRegistro().format(fmtR) : "—";
            String fp  = a.getFechaPago()!=null     ? a.getFechaPago().format(fmtF)     : "—";
            String ntx = s(a.getNumeroTransaccion()).isEmpty() ? "—" : a.getNumeroTransaccion();
            modeloTabla.addRow(new Object[]{
                a.getNumeroCasa(), a.getNombreResidente(),
                s(a.getTelefono()), s(a.getEmail()),
                String.format("%.2f",a.getMonto()),
                s(a.getPeriodo()), a.getEstado(), a.getFormaPago(),
                ntx, fp, reg
            });
            idsTabla.add(a.getId());
        }
        btnActualizar.setEnabled(false); btnEliminar.setEnabled(false);
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
