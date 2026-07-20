package Vista;

import Controlador.controlador_residentes;
import Modelo.Residentes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Formulario_Residentes extends javax.swing.JFrame implements interfaz_residentes {

    private controlador_residentes controlador;
    private DefaultTableModel modeloTabla;

    // ── Panel de vehículos ────────────────────────────────────────
    private DefaultTableModel modeloVehiculos;
    private JTable            tablaVehiculos;
    private JTextField        txtPlaca;
    private JComboBox<String> comboTipoVeh;
    private JButton           btnAgregarVeh;
    private JButton           btnEliminarVeh;
    private JSpinner          spinnerNumVeh;   // "0" cuando no tiene

    // ── Campos del formulario ────────────────────────────────────
    public javax.swing.JButton       BtnGuardar1;
    public javax.swing.JComboBox<String> box_tipoRes;
    public javax.swing.JComboBox<String> box_estadoRes;
    public javax.swing.JComboBox<String> box_vivienda;
    public javax.swing.JButton       btn_regresar;
    public javax.swing.JRadioButton  jRadioButton1;   // Sí mascotas
    public javax.swing.JRadioButton  jRadioButton2;   // No mascotas
    public javax.swing.JTable        table_residentes;
    public javax.swing.JTextField    txt_apellidosp;
    public javax.swing.JTextField    txt_cedula;
    public javax.swing.JTextField    txt_nombresp;
    public javax.swing.JTextField    txt_telefonoc;
    public javax.swing.JTextField    txt_telefonom;

    private javax.swing.JPanel      jPanel1;
    private javax.swing.JScrollPane jScrollPane1;

    public Formulario_Residentes() {
        initComponents();
        inicializarTablaResidentes();
    }

    // ─────────────────────────────────────────────────────────────
    // UI PRINCIPAL
    // ─────────────────────────────────────────────────────────────
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de Residentes");
        setSize(1260, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        jPanel1 = new JPanel(null);
        jPanel1.setBackground(Color.WHITE);
        getContentPane().add(jPanel1);

        // ── Título ────────────────────────────────────────────────
        JLabel lblTitulo = lbl("Registro de Residentes", 18);
        lblTitulo.setBounds(10, 10, 300, 28);
        jPanel1.add(lblTitulo);

        // ═════════════════════════════════════════════════════════
        // SECCIÓN 1 — DATOS PERSONALES
        // ═════════════════════════════════════════════════════════
        JPanel pnlDatos = seccion("Datos Personales", 10, 45, 820, 265);
        jPanel1.add(pnlDatos);

        // Fila 1 — Nombres / Apellidos
        addLbl(pnlDatos, "Nombres: *",   10,  22, 120, 22);
        txt_nombresp = field(); txt_nombresp.setBounds(135, 22, 260, 26); pnlDatos.add(txt_nombresp);

        addLbl(pnlDatos, "Apellidos: *", 415, 22, 100, 22);
        txt_apellidosp = field(); txt_apellidosp.setBounds(520, 22, 260, 26); pnlDatos.add(txt_apellidosp);

        // Fila 2 — Cédula / Tel. Móvil / Tel. Convencional
        addLbl(pnlDatos, "Cédula: *",            10,  60, 120, 22);
        txt_cedula = field(); txt_cedula.setBounds(135, 60, 200, 26); pnlDatos.add(txt_cedula);

        addLbl(pnlDatos, "Tel. Móvil: *",        360, 60, 120, 22);
        txt_telefonom = field(); txt_telefonom.setBounds(485, 60, 150, 26); pnlDatos.add(txt_telefonom);

        JLabel lblTelC = addLbl(pnlDatos, "Tel. Convencional:", 660, 60, 145, 22);
        txt_telefonoc = field(); txt_telefonoc.setBounds(660, 85, 145, 26); pnlDatos.add(txt_telefonoc);

        // Nota asterisco
        JLabel lblNota = new JLabel("* Al menos un teléfono es obligatorio");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(120,120,120));
        lblNota.setBounds(360, 90, 290, 18);
        pnlDatos.add(lblNota);

        // Fila 3 — N° Vivienda / Tipo de residente / Mascotas
        addLbl(pnlDatos, "N° Vivienda: *",       10,  125, 130, 22);
        box_vivienda = new JComboBox<>(viviendas());
        box_vivienda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box_vivienda.setBounds(145, 125, 80, 26);
        pnlDatos.add(box_vivienda);

        addLbl(pnlDatos, "Tipo de residente: *", 245, 125, 155, 22);
        box_tipoRes = new JComboBox<>(new String[]{"Propietario", "Residente"});
        box_tipoRes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box_tipoRes.setBounds(405, 125, 140, 26);
        pnlDatos.add(box_tipoRes);

        addLbl(pnlDatos, "¿Tiene mascotas?",     570, 125, 140, 22);
        jRadioButton1 = new JRadioButton("Sí");
        jRadioButton2 = new JRadioButton("No");
        jRadioButton1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jRadioButton2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jRadioButton1.setBackground(Color.WHITE);
        jRadioButton2.setBackground(Color.WHITE);
        jRadioButton2.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(jRadioButton1); bg.add(jRadioButton2);
        jRadioButton1.setBounds(715, 123, 50, 26);
        jRadioButton2.setBounds(770, 123, 50, 26);
        pnlDatos.add(jRadioButton1);
        pnlDatos.add(jRadioButton2);

        // Fila 4 — Estado del residente
        addLbl(pnlDatos, "Estado residente: *", 10, 168, 160, 22);
        box_estadoRes = new JComboBox<>(new String[]{"Activo", "Cancelado"});
        box_estadoRes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box_estadoRes.setBounds(175, 168, 130, 26);
        pnlDatos.add(box_estadoRes);

        // ═════════════════════════════════════════════════════════
        // SECCIÓN 2 — VEHÍCULOS
        // ═════════════════════════════════════════════════════════
        JPanel pnlVeh = seccion("Vehículos  (deje en 0 si no tiene)", 840, 45, 400, 230);
        jPanel1.add(pnlVeh);

        addLbl(pnlVeh, "N° de vehículos:", 10, 22, 140, 22);
        spinnerNumVeh = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spinnerNumVeh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerNumVeh.setBounds(155, 22, 60, 26);
        pnlVeh.add(spinnerNumVeh);
        // Al cambiar el spinner, ajustar la tabla
        spinnerNumVeh.addChangeListener(e -> ajustarFilasVehiculos());

        addLbl(pnlVeh, "Placa:", 10, 60, 60, 22);
        txtPlaca = field();
        txtPlaca.setToolTipText("Ej: ABC-1234");
        txtPlaca.setBounds(75, 60, 110, 26);
        pnlVeh.add(txtPlaca);

        addLbl(pnlVeh, "Tipo:", 195, 60, 45, 22);
        comboTipoVeh = new JComboBox<>(new String[]{"Liviano", "Pesado", "Buseta"});
        comboTipoVeh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoVeh.setBounds(245, 60, 100, 26);
        pnlVeh.add(comboTipoVeh);

        btnAgregarVeh = btn("+ Agregar", new Color(0,120,0), Color.WHITE);
        btnAgregarVeh.setBounds(10, 96, 110, 26);
        btnAgregarVeh.addActionListener(e -> agregarVehiculo());
        pnlVeh.add(btnAgregarVeh);

        btnEliminarVeh = btn("✕ Eliminar", new Color(180,0,0), Color.WHITE);
        btnEliminarVeh.setBounds(130, 96, 110, 26);
        btnEliminarVeh.addActionListener(e -> eliminarVehiculo());
        pnlVeh.add(btnEliminarVeh);

        // Mini-tabla de vehículos
        modeloVehiculos = new DefaultTableModel(
            new String[]{"Placa", "Tipo"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVehiculos.setRowHeight(20);
        tablaVehiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVehiculos.getTableHeader().setBackground(new Color(240,240,240));
        JScrollPane scrollVeh = new JScrollPane(tablaVehiculos);
        scrollVeh.setBounds(10, 130, 375, 85);
        pnlVeh.add(scrollVeh);

        // ═════════════════════════════════════════════════════════
        // BOTONES PRINCIPALES
        // ═════════════════════════════════════════════════════════
        BtnGuardar1 = btn("GUARDAR", new Color(0,0,0), Color.WHITE);
        BtnGuardar1.setBounds(840, 285, 120, 32);
        jPanel1.add(BtnGuardar1);

        btn_regresar = btn("REGRESAR", new Color(80,80,80), Color.WHITE);
        btn_regresar.setBounds(975, 285, 120, 32);
        jPanel1.add(btn_regresar);

        // ═════════════════════════════════════════════════════════
        // TABLA DE RESIDENTES REGISTRADOS
        // ═════════════════════════════════════════════════════════
        JLabel lblTabla = lbl("Residentes registrados", 14);
        lblTabla.setBounds(10, 290, 300, 22);
        jPanel1.add(lblTabla);

        table_residentes = new JTable();
        table_residentes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table_residentes.setRowHeight(21);
        table_residentes.setEnabled(false);
        table_residentes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table_residentes.getTableHeader().setBackground(Color.WHITE);
        table_residentes.getTableHeader().setForeground(Color.BLACK);

        jScrollPane1 = new JScrollPane(table_residentes);
        jScrollPane1.setBounds(10, 318, 1230, 440);
        jPanel1.add(jScrollPane1);

        pack();
        setSize(1260, 800);
    }

    // ─────────────────────────────────────────────────────────────
    // Lógica de vehículos
    // ─────────────────────────────────────────────────────────────
    private void agregarVehiculo() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        String tipo  = (String) comboTipoVeh.getSelectedItem();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la placa del vehículo.");
            return;
        }
        // Verificar duplicados
        for (int i = 0; i < modeloVehiculos.getRowCount(); i++) {
            if (placa.equals(modeloVehiculos.getValueAt(i, 0))) {
                JOptionPane.showMessageDialog(this, "La placa " + placa + " ya fue ingresada.");
                return;
            }
        }
        modeloVehiculos.addRow(new Object[]{placa, tipo});
        // Sincronizar spinner
        spinnerNumVeh.setValue(modeloVehiculos.getRowCount());
        txtPlaca.setText("");
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la lista para eliminarlo.");
            return;
        }
        modeloVehiculos.removeRow(fila);
        spinnerNumVeh.setValue(modeloVehiculos.getRowCount());
    }

    /** Cuando el spinner baja a 0, limpia la tabla. Si sube, no agrega filas automáticamente. */
    private void ajustarFilasVehiculos() {
        int n = (int) spinnerNumVeh.getValue();
        if (n == 0) {
            modeloVehiculos.setRowCount(0);
        }
        // Si sube, el usuario usa el botón Agregar manualmente
    }

    // ─────────────────────────────────────────────────────────────
    // Tabla de residentes
    // ─────────────────────────────────────────────────────────────
    private void inicializarTablaResidentes() {
        modeloTabla = new DefaultTableModel(
            new String[]{
                "Nombres", "Apellidos", "Cédula",
                "Tel. Móvil", "Tel. Convencional",
                "N° Vivienda", "Tipo Residente", "Estado",
                "Mascotas", "N° Vehículos", "Vehículos (Placa / Tipo)",
                "Fecha de Registro"
            }, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table_residentes.setModel(modeloTabla);
        table_residentes.getTableHeader().setBackground(Color.WHITE);
        table_residentes.getTableHeader().setForeground(Color.BLACK);
    }

    // ─────────────────────────────────────────────────────────────
    // Helpers UI
    // ─────────────────────────────────────────────────────────────
    private JLabel lbl(String t, int size) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, size));
        return l;
    }
    private JLabel addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = lbl(t, 13);
        l.setBounds(x, y, w, h);
        p.add(l);
        return l;
    }
    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }
    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    private JPanel seccion(String titulo, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        p.setBounds(x, y, w, h);
        return p;
    }
    private String[] viviendas() {
        String[] arr = new String[20];
        for (int i = 0; i < 20; i++) arr[i] = String.valueOf(i + 1);
        return arr;
    }

    // ─────────────────────────────────────────────────────────────
    // Implementación interfaz_residentes
    // ─────────────────────────────────────────────────────────────
    @Override public String getNombres()              { return txt_nombresp.getText(); }
    @Override public String getApellidos()            { return txt_apellidosp.getText(); }
    @Override public String getCedula()               { return txt_cedula.getText(); }
    @Override public String getTelefonoMovil()        { return txt_telefonom.getText(); }
    @Override public String getTelefonoConvencional() { return txt_telefonoc.getText(); }
    @Override public String getNumeroVivienda()       { return (String) box_vivienda.getSelectedItem(); }
    @Override public String getTipoResidente()        { return (String) box_tipoRes.getSelectedItem(); }
    @Override public String getEstadoResidente()      { return (String) box_estadoRes.getSelectedItem(); }
    @Override public boolean getTieneMascotas()       { return jRadioButton1.isSelected(); }

    @Override
    public List<String[]> getVehiculos() {
        List<String[]> lista = new ArrayList<>();
        for (int i = 0; i < modeloVehiculos.getRowCount(); i++) {
            lista.add(new String[]{
                (String) modeloVehiculos.getValueAt(i, 0),
                (String) modeloVehiculos.getValueAt(i, 1)
            });
        }
        return lista;
    }

    @Override public JButton getBtnGuardar()  { return BtnGuardar1;  }
    @Override public JButton getBtnRegresar() { return btn_regresar; }

    @Override
    public void actualizarTabla(ArrayList<Residentes> lista) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        modeloTabla.setRowCount(0);
        for (Residentes r : lista) {
            String fecha = r.getFechaRegistro() != null
                    ? r.getFechaRegistro().format(fmt)
                    : "—";
            modeloTabla.addRow(new Object[]{
                r.getNombres(),
                r.getApellidos(),
                r.getCedula(),
                r.getTelefonoMovil(),
                r.getTelefonoConvencional() != null ? r.getTelefonoConvencional() : "",
                r.getNumeroVivienda(),
                r.getTipoResidente(),
                r.getEstadoResidente(),
                r.isTieneMascotas() ? "Sí" : "No",
                r.getCantidadVehiculos(),
                r.getVehiculosResumen(),
                fecha
            });
        }
    }

    @Override
    public void limpiarCampos() {
        txt_nombresp.setText("");
        txt_apellidosp.setText("");
        txt_cedula.setText("");
        txt_telefonom.setText("");
        txt_telefonoc.setText("");
        box_vivienda.setSelectedIndex(0);
        box_tipoRes.setSelectedIndex(0);
        box_estadoRes.setSelectedIndex(0);
        jRadioButton2.setSelected(true);
        txtPlaca.setText("");
        comboTipoVeh.setSelectedIndex(0);
        modeloVehiculos.setRowCount(0);
        spinnerNumVeh.setValue(0);
    }

    @Override
    public void mostrarMensaje(String mensaje) { JOptionPane.showMessageDialog(this, mensaje); }

    @Override public void iniciar()  { this.setVisible(true); }
    @Override public void dispose()  { super.dispose(); }
}
