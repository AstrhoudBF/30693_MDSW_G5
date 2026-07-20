package Vista;

import Controlador.controlador_arriendos;
import Modelo.Arriendo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Formulario_Arriendos extends javax.swing.JFrame implements interfaz_arriendos {

    private DefaultTableModel modeloTabla;

    // ── Componentes ───────────────────────────────────────────────
    private JPanel        pnlPrincipal;
    private JComboBox<String> comboTipo;
    private JTextField    txtEspacio;
    private JTextField    txtNombre;
    private JComboBox<String> comboTipoArr;
    private JTextField    txtContacto;
    private JTextField    txtMonto;
    private JTextField    txtPeriodo;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;
    private JButton       btnGuardar;

    private JButton       btnHistorial;
    private JTable        tablaArriendos;
    private JScrollPane   scrollTabla;

    public Formulario_Arriendos() {
        initComponents();
        inicializarTabla();
    }

    private void initComponents() {
        setTitle("Registro de Arriendos — Locales y Parqueaderos");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        pnlPrincipal = new JPanel(null);
        pnlPrincipal.setBackground(Color.WHITE);
        getContentPane().add(pnlPrincipal);

        // ── Título ────────────────────────────────────────────────
        JLabel lblTitulo = makeLabel("Arriendos — Locales y Parqueaderos", 18);
        lblTitulo.setBounds(20, 14, 500, 30);
        pnlPrincipal.add(lblTitulo);

        // ── Panel formulario ──────────────────────────────────────
        JPanel pnlForm = new JPanel(null);
        pnlForm.setBackground(new Color(245, 245, 245));
        pnlForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Datos del Contrato Mensual",
            0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlForm.setBounds(15, 52, 782, 270);
        pnlPrincipal.add(pnlForm);

        int lx = 15, fx = 190, rH = 36, y = 22, lw = 170, fw = 180, fh = 27;

        // Fila 1 — Tipo de espacio
        addLabel(pnlForm, "Tipo de espacio:", lx, y, lw, fh);
        comboTipo = new JComboBox<>(new String[]{"Local", "Parqueadero"});
        comboTipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipo.setBounds(fx, y, 140, fh);
        pnlForm.add(comboTipo);

        // Fila 1 — Número / nombre del espacio
        addLabel(pnlForm, "N° / Nombre:", 380, y, 130, fh);
        txtEspacio = makeField();
        txtEspacio.setToolTipText("Ej: Local 3  o  Parqueadero 7");
        txtEspacio.setBounds(515, y, 240, fh);
        pnlForm.add(txtEspacio);

        // Fila 2 — Arrendatario
        y += rH;
        addLabel(pnlForm, "Nombre arrendatario:", lx, y, lw, fh);
        txtNombre = makeField();
        txtNombre.setBounds(fx, y, fw, fh);
        pnlForm.add(txtNombre);

        addLabel(pnlForm, "Tipo arrendatario:", 380, y, 130, fh);
        comboTipoArr = new JComboBox<>(new String[]{"Residente", "Externo"});
        comboTipoArr.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoArr.setBounds(515, y, 150, fh);
        pnlForm.add(comboTipoArr);

        // Fila 3 — Contacto
        y += rH;
        addLabel(pnlForm, "Contacto:", lx, y, lw, fh);
        txtContacto = makeField();
        txtContacto.setToolTipText("Teléfono o email");
        txtContacto.setBounds(fx, y, fw, fh);
        pnlForm.add(txtContacto);

        // Fila 4 — Monto y Período
        y += rH;
        addLabel(pnlForm, "Monto mensual ($):", lx, y, lw, fh);
        txtMonto = makeField();
        txtMonto.setBounds(fx, y, 110, fh);
        pnlForm.add(txtMonto);

        addLabel(pnlForm, "Período:", 380, y, 90, fh);
        txtPeriodo = makeField();
        txtPeriodo.setToolTipText("Ej: Junio 2026");
        txtPeriodo.setBounds(475, y, 160, fh);
        pnlForm.add(txtPeriodo);

        // Fila 5 — Estado y Forma de pago
        y += rH;
        addLabel(pnlForm, "Estado:", lx, y, lw, fh);
        comboEstado = new JComboBox<>(new String[]{"Activo", "Pagado", "Pendiente", "Cancelado"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(fx, y, 140, fh);
        pnlForm.add(comboEstado);

        addLabel(pnlForm, "Forma de pago:", 380, y, 130, fh);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(515, y, 150, fh);
        pnlForm.add(comboFormaPago);

        // ── Botones ───────────────────────────────────────────────
        y += rH + 8;
        btnGuardar   = makeBtn("Guardar",         new Color(0,0,0),       Color.WHITE);
        btnHistorial = makeBtn("Ver Historial ▸", new Color(30,100,180),  Color.WHITE);
        btnGuardar.setBounds(fx,       y, 130, 30);
        btnHistorial.setBounds(fx+145, y, 150, 30);
        pnlForm.add(btnGuardar);
        pnlForm.add(btnHistorial);

        // ── Tabla ─────────────────────────────────────────────────
        JLabel lblHist = makeLabel("Historial de arriendos registrados", 13);
        lblHist.setBounds(15, 330, 350, 22);
        pnlPrincipal.add(lblHist);

        tablaArriendos = new JTable();
        tablaArriendos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaArriendos.setRowHeight(21);
        tablaArriendos.setEnabled(false);
        tablaArriendos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaArriendos.getTableHeader().setBackground(Color.WHITE);

        scrollTabla = new JScrollPane(tablaArriendos);
        scrollTabla.setBounds(15, 356, 782, 250);
        pnlPrincipal.add(scrollTabla);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"Tipo", "Espacio", "Arrendatario", "Tipo Arr.", "Contacto",
                         "Monto ($)", "Período", "Estado", "Forma Pago", "Fecha Registro"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaArriendos.setModel(modeloTabla);
    }

    // ── Helpers UI ────────────────────────────────────────────────
    private JLabel makeLabel(String t, int size) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, size));
        return l;
    }
    private void addLabel(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x, y, w, h);
        p.add(l);
    }
    private JTextField makeField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }
    private JButton makeBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Implementación interfaz_arriendos ─────────────────────────
    @Override public String getTipoEspacio()      { return (String) comboTipo.getSelectedItem(); }
    @Override public String getNumeroEspacio()    { return txtEspacio.getText(); }
    @Override public String getNombreArrendatario(){ return txtNombre.getText(); }
    @Override public String getTipoArrendatario() { return (String) comboTipoArr.getSelectedItem(); }
    @Override public String getContacto()         { return txtContacto.getText(); }
    @Override public double getMontoMensual()     { return Double.parseDouble(txtMonto.getText().trim().replace(",",".")); }
    @Override public String getMesPeriodo()       { return txtPeriodo.getText(); }
    @Override public String getEstado()           { return (String) comboEstado.getSelectedItem(); }
    @Override public String getFormaPago()        { return (String) comboFormaPago.getSelectedItem(); }
    @Override public JButton getBtnGuardar()      { return btnGuardar; }

    @Override public JButton getBtnHistorial()    { return btnHistorial; }

    @Override
    public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        comboTipo.setSelectedIndex(0);
        txtEspacio.setText(""); txtNombre.setText("");
        comboTipoArr.setSelectedIndex(0); txtContacto.setText("");
        txtMonto.setText(""); txtPeriodo.setText("");
        comboEstado.setSelectedIndex(0); comboFormaPago.setSelectedIndex(0);
    }

    @Override
    public void actualizarTabla(ArrayList<Arriendo> lista) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        modeloTabla.setRowCount(0);
        for (Arriendo a : lista) {
            String fecha = a.getFechaRegistro() != null ? a.getFechaRegistro().format(fmt) : "—";
            modeloTabla.addRow(new Object[]{
                a.getTipoEspacio(), a.getNumeroEspacio(),
                a.getNombreArrendatario(), a.getTipoArrendatario(),
                a.getContacto(), String.format("%.2f", a.getMontoMensual()),
                a.getMesPeriodo(), a.getEstado(), a.getFormaPago(), fecha
            });
        }
    }

    @Override public void iniciar() { this.setVisible(true); }
}
