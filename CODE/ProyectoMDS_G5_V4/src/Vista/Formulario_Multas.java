package Vista;

import Controlador.controlador_multas;
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

    // ── IDs guardados para edit/delete ────────────────────────────
    private ArrayList<String> idsTabla = new ArrayList<>();

    // ── Formulario ────────────────────────────────────────────────
    private JComboBox<String> comboCasa;
    private JTextField        txtCedula;
    private JTextField        txtNombre;
    private JComboBox<String> comboCategoria;
    private JTextArea         txtMotivo;
    private JSpinner          spinnerFecha;
    private JTextField        txtMonto;
    private JComboBox<String> comboEstado;
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
    }

    private void initComponents() {
        setTitle("Gestión de Multas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTit = new JLabel("Gestión de Multas");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setBounds(15, 10, 350, 28);
        pnl.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ══════════════════════════════════════════════════════════
        JPanel pnlForm = seccion("Datos de la Multa", 15, 45, 1060, 320);
        pnl.add(pnlForm);

        // ── Fila 1: Casa + Cédula ─────────────────────────────────
        addLbl(pnlForm, "N° Casa: *",          10, 22, 100, 22);
        comboCasa = new JComboBox<>();
        comboCasa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCasa.addItem("-- Seleccione --");
        for (int i = 1; i <= 20; i++) comboCasa.addItem(String.valueOf(i));
        comboCasa.setBounds(115, 22, 100, 26);
        pnlForm.add(comboCasa);

        addLbl(pnlForm, "Cédula:",             230, 22, 70, 22);
        txtCedula = campo();
        txtCedula.setToolTipText("Se completa automáticamente al elegir la casa");
        txtCedula.setEditable(false);
        txtCedula.setBackground(new Color(235, 235, 235));
        txtCedula.setBounds(305, 22, 140, 26);
        pnlForm.add(txtCedula);

        addLbl(pnlForm, "Residente:",          460, 22, 80, 22);
        txtNombre = campo();
        txtNombre.setEditable(false);
        txtNombre.setBackground(new Color(235, 235, 235));
        txtNombre.setBounds(545, 22, 290, 26);
        pnlForm.add(txtNombre);

        // ── Fila 2: Categoría + Fecha infracción ──────────────────
        addLbl(pnlForm, "Categoría: *",        10, 62, 100, 22);
        comboCategoria = new JComboBox<>(new String[]{"Mascotas", "Minga", "Asamblea", "Parqueaderos"});
        comboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCategoria.setBounds(115, 62, 160, 26);
        pnlForm.add(comboCategoria);

        addLbl(pnlForm, "Fecha infracción: *", 295, 62, 145, 22);
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor de = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(de);
        spinnerFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerFecha.setBounds(445, 62, 130, 26);
        pnlForm.add(spinnerFecha);

        addLbl(pnlForm, "Monto ($): *",        595, 62, 90, 22);
        txtMonto = campo();
        txtMonto.setBounds(690, 62, 100, 26);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Estado: *",           805, 62, 75, 22);
        comboEstado = new JComboBox<>(new String[]{"Pendiente", "Pagada", "Anulada"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(885, 62, 140, 26);
        pnlForm.add(comboEstado);

        // ── Fila 3: Motivo (textarea) ─────────────────────────────
        addLbl(pnlForm, "Motivo: *",           10, 102, 100, 22);
        txtMotivo = new JTextArea();
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setToolTipText("Ej: Desechos en el parque infantil");
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setBounds(115, 102, 910, 70);
        pnlForm.add(scrollMotivo);

        // ── Fila 4: Observaciones (textarea) ─────────────────────
        addLbl(pnlForm, "Observaciones:",      10, 183, 110, 22);
        txtObservaciones = new JTextArea();
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setToolTipText("Ej: Reportado por el guardia Pérez, se adjunta foto en archivo físico");
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        scrollObs.setBounds(115, 183, 910, 65);
        pnlForm.add(scrollObs);

        // ── Botones ───────────────────────────────────────────────
        btnGuardar   = mkBtn("Guardar",           new Color(0, 0, 0),      Color.WHITE);
        btnModificar = mkBtn("✏ Modificar",       new Color(30, 100, 180), Color.WHITE);
        btnEliminar  = mkBtn("🗑 Eliminar",       new Color(180, 30, 30),  Color.WHITE);
        btnRegresar  = mkBtn("← Regresar",        new Color(80, 80, 80),   Color.WHITE);

        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        btnGuardar.setBounds(10,   265, 140, 32);
        btnModificar.setBounds(165, 265, 140, 32);
        btnEliminar.setBounds(320,  265, 140, 32);
        btnRegresar.setBounds(475,  265, 140, 32);

        pnlForm.add(btnGuardar);
        pnlForm.add(btnModificar);
        pnlForm.add(btnEliminar);
        pnlForm.add(btnRegresar);

        // ── Nota ───────────────────────────────────────────────────
        JLabel lblNota = new JLabel("Seleccione una fila de la tabla para modificar o eliminar una multa existente.");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(100, 100, 100));
        lblNota.setBounds(635, 272, 410, 18);
        pnlForm.add(lblNota);

        // ══════════════════════════════════════════════════════════
        // TABLA
        // ══════════════════════════════════════════════════════════
        JLabel lblTabla = new JLabel("Registro de multas:");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTabla.setBounds(15, 372, 250, 22);
        pnl.add(lblTabla);

        tablaMultas = new JTable();
        tablaMultas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaMultas.setRowHeight(21);
        tablaMultas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaMultas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaMultas.getTableHeader().setBackground(Color.WHITE);
        // Activar botones al seleccionar fila
        tablaMultas.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaMultas.getSelectedRow() >= 0;
            btnModificar.setEnabled(hay);
            btnEliminar.setEnabled(hay);
        });

        JScrollPane scrollTabla = new JScrollPane(tablaMultas);
        scrollTabla.setBounds(15, 397, 1060, 330);
        pnl.add(scrollTabla);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{
                "Casa", "Cédula", "Residente", "Categoría",
                "Motivo", "Fecha Infracción", "Monto ($)",
                "Estado", "Observaciones", "Fecha Registro"
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaMultas.setModel(modeloTabla);
    }

    // ── Precarga de datos para edición (llamado por el controlador) ─
    public void precargarEdicion(Multa m) {
        comboCasa.setSelectedItem(m.getNumeroCasa());
        txtCedula.setText(m.getCedulaResidente() != null ? m.getCedulaResidente() : "");
        txtNombre.setText(m.getNombreResidente() != null ? m.getNombreResidente() : "");

        // Categoría
        for (int i = 0; i < comboCategoria.getItemCount(); i++) {
            if (comboCategoria.getItemAt(i).equals(m.getCategoria())) {
                comboCategoria.setSelectedIndex(i); break;
            }
        }

        txtMotivo.setText(m.getMotivo() != null ? m.getMotivo() : "");

        // Fecha infracción
        if (m.getFechaInfraccion() != null) {
            Date d = Date.from(m.getFechaInfraccion().atStartOfDay(ZoneId.systemDefault()).toInstant());
            spinnerFecha.setValue(d);
        }

        txtMonto.setText(String.valueOf(m.getMonto()));

        for (int i = 0; i < comboEstado.getItemCount(); i++) {
            if (comboEstado.getItemAt(i).equals(m.getEstado())) {
                comboEstado.setSelectedIndex(i); break;
            }
        }

        txtObservaciones.setText(m.getObservaciones() != null ? m.getObservaciones() : "");
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JPanel seccion(String titulo, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248, 248, 248));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        p.setBounds(x, y, w, h);
        return p;
    }
    private void addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x, y, w, h);
        p.add(l);
    }
    private JTextField campo() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Implementación interfaz_multas ─────────────────────────────
    @Override
    public String getNumeroCasa() {
        Object s = comboCasa.getSelectedItem();
        return s != null ? s.toString() : "";
    }
    @Override public String getCedulaResidente()    { return txtCedula.getText().trim(); }
    @Override public String getNombreResidente()    { return txtNombre.getText().trim(); }
    @Override public String getCategoria()          { return (String) comboCategoria.getSelectedItem(); }
    @Override public String getMotivo()             { return txtMotivo.getText().trim(); }
    @Override public LocalDate getFechaInfraccion() {
        Date d = (Date) spinnerFecha.getValue();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    @Override public double getMonto() {
        return Double.parseDouble(txtMonto.getText().trim().replace(",", "."));
    }
    @Override public String getEstado()         { return (String) comboEstado.getSelectedItem(); }
    @Override public String getObservaciones()  { return txtObservaciones.getText().trim(); }

    @Override public void setNombreResidente(String v)  { txtNombre.setText(v); }
    @Override public void setNumeroCasa(String v)       { comboCasa.setSelectedItem(v); }
    @Override public void setCedulaResidente(String v)  { txtCedula.setText(v); }

    @Override public JButton getBtnGuardar()   { return btnGuardar;   }
    @Override public JButton getBtnRegresar()  { return btnRegresar;  }
    @Override public JButton getBtnModificar() { return btnModificar; }
    @Override public JButton getBtnEliminar()  { return btnEliminar;  }
    @Override public JComboBox<String> getComboCasa() { return comboCasa; }

    @Override
    public String getIdSeleccionado() {
        int fila = tablaMultas.getSelectedRow();
        if (fila < 0 || fila >= idsTabla.size()) return null;
        return idsTabla.get(fila);
    }

    @Override
    public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        comboCasa.setSelectedIndex(0);
        txtCedula.setText("");
        txtNombre.setText("");
        comboCategoria.setSelectedIndex(0);
        txtMotivo.setText("");
        spinnerFecha.setValue(new Date());
        txtMonto.setText("");
        comboEstado.setSelectedIndex(0);
        txtObservaciones.setText("");
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaMultas.clearSelection();
    }

    @Override
    public void actualizarTabla(ArrayList<Multa> lista) {
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        modeloTabla.setRowCount(0);
        idsTabla.clear();
        for (Multa m : lista) {
            String fechaInf = m.getFechaInfraccion() != null ? m.getFechaInfraccion().format(fmtF) : "—";
            String fechaReg = m.getFechaRegistro()   != null ? m.getFechaRegistro().format(fmtR)   : "—";
            modeloTabla.addRow(new Object[]{
                m.getNumeroCasa(),
                m.getCedulaResidente(),
                m.getNombreResidente(),
                m.getCategoria(),
                m.getMotivo(),
                fechaInf,
                String.format("%.2f", m.getMonto()),
                m.getEstado(),
                m.getObservaciones(),
                fechaReg
            });
            idsTabla.add(m.getId());
        }
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
