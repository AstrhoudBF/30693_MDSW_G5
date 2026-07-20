package Vista;

import Controlador.controlador_alicuotas;
import Modelo.Alicuota;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Formulario_Alicuotas extends JFrame implements interfaz_alicuotas {

    private DefaultTableModel        modeloTabla;
    private ArrayList<String>        idsTabla = new ArrayList<>();
    private controlador_alicuotas    controlador;

    // ── Campos ────────────────────────────────────────────────────
    private JComboBox<String> comboCasa;
    private JTextField        txtResidente;
    private JTextField        txtMonto;
    private JTextField        txtPeriodo;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;

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
    }

    private void initComponents() {
        setTitle("Registro de Alícuotas");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(900, 680);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTit = new JLabel("Registro de Alícuotas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setBounds(15, 12, 300, 28);
        pnl.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ══════════════════════════════════════════════════════════
        JPanel pnlForm = seccion("Datos de la Alícuota", 15, 46, 860, 240);
        pnl.add(pnlForm);

        // Fila 1 — Casa / Residente
        addLbl(pnlForm, "N° de Casa: *",  10, 22, 115, 24);
        comboCasa = new JComboBox<>();
        comboCasa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCasa.addItem("-- Seleccione --");
        for (int i = 1; i <= 20; i++) comboCasa.addItem(String.valueOf(i));
        comboCasa.setBounds(130, 22, 110, 26);
        pnlForm.add(comboCasa);

        addLbl(pnlForm, "Residente:",     260, 22, 90, 24);
        txtResidente = campo();
        txtResidente.setEditable(false);
        txtResidente.setBackground(new Color(235, 235, 235));
        txtResidente.setBounds(355, 22, 480, 26);
        pnlForm.add(txtResidente);

        // Fila 2 — Monto / Período
        addLbl(pnlForm, "Monto ($): *",   10, 62, 115, 24);
        txtMonto = campo();
        txtMonto.setBounds(130, 62, 120, 26);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Período: *",     270, 62, 80, 24);
        txtPeriodo = campo();
        txtPeriodo.setToolTipText("Ej: Junio 2026");
        txtPeriodo.setBounds(355, 62, 200, 26);
        pnlForm.add(txtPeriodo);

        // Fila 3 — Estado / Forma de pago
        addLbl(pnlForm, "Estado: *",      10, 102, 115, 24);
        comboEstado = new JComboBox<>(new String[]{"Pendiente", "Pagado", "Atrasado"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(130, 102, 150, 26);
        pnlForm.add(comboEstado);

        addLbl(pnlForm, "Forma de pago:", 300, 102, 120, 24);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(425, 102, 150, 26);
        pnlForm.add(comboFormaPago);

        // ── Botones ────────────────────────────────────────────────
        btnGuardar    = mkBtn("Guardar",               Color.BLACK,             Color.WHITE);
        btnActualizar = mkBtn("✏ Modificar seleccionada", new Color(30,100,180), Color.WHITE);
        btnEliminar   = mkBtn("🗑 Eliminar seleccionada", new Color(180,30,30),  Color.WHITE);
        btnRegresar   = mkBtn("← Regresar",            new Color(80,80,80),     Color.WHITE);

        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.setBounds(10,    150, 145, 32);
        btnActualizar.setBounds(165, 150, 210, 32);
        btnEliminar.setBounds(385,  150, 210, 32);
        btnRegresar.setBounds(605,  150, 130, 32);

        pnlForm.add(btnGuardar);
        pnlForm.add(btnActualizar);
        pnlForm.add(btnEliminar);
        pnlForm.add(btnRegresar);

        JLabel lblNota = new JLabel("Seleccione una fila para modificar o eliminar.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(110,110,110));
        lblNota.setBounds(10, 190, 840, 18);
        pnlForm.add(lblNota);

        // ══════════════════════════════════════════════════════════
        // TABLA
        // ══════════════════════════════════════════════════════════
        JLabel lblHist = new JLabel("Historial de Alícuotas:");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHist.setBounds(15, 295, 250, 22);
        pnl.add(lblHist);

        tablaAlicuotas = new JTable();
        tablaAlicuotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAlicuotas.setRowHeight(22);
        tablaAlicuotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlicuotas.setSelectionBackground(new Color(200, 220, 255));
        tablaAlicuotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaAlicuotas.getTableHeader().setBackground(Color.WHITE);
        tablaAlicuotas.getTableHeader().setForeground(Color.BLACK);

        // Habilitar botones al seleccionar fila
        tablaAlicuotas.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaAlicuotas.getSelectedRow() >= 0;
            btnActualizar.setEnabled(hay);
            btnEliminar.setEnabled(hay);
        });

        JScrollPane scroll = new JScrollPane(tablaAlicuotas);
        scroll.setBounds(15, 322, 860, 320);
        pnl.add(scroll);

        setSize(900, 680);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{"Casa", "Residente", "Monto ($)", "Período",
                         "Estado", "Forma de Pago", "Fecha de Registro"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaAlicuotas.setModel(modeloTabla);
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JPanel seccion(String titulo, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 12)));
        p.setBounds(x,y,w,h); return p;
    }
    private void addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x,y,w,h); p.add(l);
    }
    private JTextField campo() {
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

    // ── Implementación interfaz_alicuotas ──────────────────────────
    @Override public String getNumeroCasa() {
        Object s = comboCasa.getSelectedItem();
        return s != null ? s.toString() : "";
    }
    @Override public String getNombreResidente()    { return txtResidente.getText(); }
    @Override public void   setNombreResidente(String n) { txtResidente.setText(n); }
    @Override public double getMonto() {
        return Double.parseDouble(txtMonto.getText().trim().replace(",", "."));
    }
    @Override public String getPeriodo()             { return txtPeriodo.getText(); }
    @Override public String getEstado()              { return (String) comboEstado.getSelectedItem(); }
    @Override public String getFormaPago()           { return (String) comboFormaPago.getSelectedItem(); }
    @Override public JButton getBtnGuardar()         { return btnGuardar;    }
    @Override public JButton getBtnActualizar()      { return btnActualizar; }
    @Override public JButton getBtnEliminar()        { return btnEliminar;   }
    @Override public JButton getBtnRegresar()        { return btnRegresar;   }
    @Override public JComboBox<String> getComboCasa(){ return comboCasa;     }

    @Override
    public String getIdSeleccionado() {
        int fila = tablaAlicuotas.getSelectedRow();
        if (fila < 0 || fila >= idsTabla.size()) return null;
        return idsTabla.get(fila);
    }

    @Override
    public void precargarEdicion(Alicuota a) {
        comboCasa.setSelectedItem(a.getNumeroCasa());
        txtResidente.setText(a.getNombreResidente() != null ? a.getNombreResidente() : "");
        txtMonto.setText(String.valueOf(a.getMonto()));
        txtPeriodo.setText(a.getPeriodo() != null ? a.getPeriodo() : "");
        for (int i = 0; i < comboEstado.getItemCount(); i++) {
            if (comboEstado.getItemAt(i).equals(a.getEstado())) {
                comboEstado.setSelectedIndex(i); break;
            }
        }
        for (int i = 0; i < comboFormaPago.getItemCount(); i++) {
            if (comboFormaPago.getItemAt(i).equals(a.getFormaPago())) {
                comboFormaPago.setSelectedIndex(i); break;
            }
        }
    }

    @Override
    public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        comboCasa.setSelectedIndex(0);
        txtResidente.setText("");
        txtMonto.setText("");
        txtPeriodo.setText("");
        comboEstado.setSelectedIndex(0);
        comboFormaPago.setSelectedIndex(0);
        tablaAlicuotas.clearSelection();
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        // Resetear botón guardar por si estaba en modo edición
        btnGuardar.setText("Guardar");
        btnGuardar.setBackground(Color.BLACK);
    }

    @Override
    public void actualizarTabla(ArrayList<Alicuota> lista) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        modeloTabla.setRowCount(0);
        idsTabla.clear();
        for (Alicuota a : lista) {
            String fecha = a.getFechaRegistro() != null ? a.getFechaRegistro().format(fmt) : "—";
            modeloTabla.addRow(new Object[]{
                a.getNumeroCasa(),
                a.getNombreResidente(),
                String.format("%.2f", a.getMonto()),
                a.getPeriodo(),
                a.getEstado(),
                a.getFormaPago(),
                fecha
            });
            idsTabla.add(a.getId());
        }
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
