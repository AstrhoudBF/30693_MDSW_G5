package Vista;

import Controlador.controlador_busqueda;
import Modelo.AlmacenarResidentes;
import Modelo.Residentes;
import Modelo.validaciones;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulario de edición de residente.
 * Se abre desde el módulo de Búsqueda con todos los datos precargados.
 * Al guardar, actualiza directamente el documento en MongoDB usando
 * la cédula original como clave de búsqueda.
 */
public class Formulario_Editar_Residente extends JFrame {

    private final Residentes           residenteOriginal;
    private final String               cedulaOriginal;
    private final controlador_busqueda ctrlBusqueda;
    private final AlmacenarResidentes  repo = new AlmacenarResidentes();

    // ── Panel de vehículos ─────────────────────────────────────────
    private DefaultTableModel modeloVehiculos;
    private JTable            tablaVehiculos;
    private JTextField        txtPlaca;
    private JComboBox<String> comboTipoVeh;
    private JSpinner          spinnerNumVeh;

    // ── Campos del formulario ──────────────────────────────────────
    private JTextField        txtNombres;
    private JTextField        txtApellidos;
    private JTextField        txtCedula;
    private JTextField        txtTelMovil;
    private JTextField        txtTelConv;
    private JComboBox<String> comboVivienda;
    private JComboBox<String> comboTipoRes;
    private JComboBox<String> comboEstadoRes;
    private JRadioButton      rdoSiMascota;
    private JRadioButton      rdoNoMascota;
    private JButton           btnGuardar;
    private JButton           btnCancelar;

    public Formulario_Editar_Residente(Residentes residente,
                                       String cedulaOriginal,
                                       controlador_busqueda ctrlBusqueda) {
        this.residenteOriginal = residente;
        this.cedulaOriginal    = cedulaOriginal;
        this.ctrlBusqueda      = ctrlBusqueda;

        initComponents();
        cargarDatos(residente);

        btnGuardar.addActionListener(e  -> guardarCambios());
        btnCancelar.addActionListener(e -> dispose());
    }

    // ── Construcción del UI ────────────────────────────────────────
    private void initComponents() {
        setTitle("Modificar Residente — " + residenteOriginal.getNombres()
                 + " " + residenteOriginal.getApellidos());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1050, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTit = new JLabel("Modificar datos del residente");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setBounds(15, 10, 450, 28);
        pnl.add(lblTit);

        // ══════════════════════════════════════════════════════════
        // SECCIÓN 1 — DATOS PERSONALES
        // ══════════════════════════════════════════════════════════
        JPanel pnlDatos = seccion("Datos Personales", 15, 45, 610, 235);
        pnl.add(pnlDatos);

        // Fila 1 — Nombres / Apellidos
        addLbl(pnlDatos, "Nombres: *",   10, 22, 115, 22);
        txtNombres = campo(); txtNombres.setBounds(130, 22, 210, 26); pnlDatos.add(txtNombres);

        addLbl(pnlDatos, "Apellidos: *", 360, 22, 100, 22);
        txtApellidos = campo(); txtApellidos.setBounds(465, 22, 130, 26); pnlDatos.add(txtApellidos);

        // Fila 2 — Cédula / Tel. Móvil
        addLbl(pnlDatos, "Cédula: *",       10,  60, 115, 22);
        txtCedula = campo(); txtCedula.setBounds(130, 60, 160, 26); pnlDatos.add(txtCedula);

        addLbl(pnlDatos, "Tel. Móvil: *",  310,  60, 120, 22);
        txtTelMovil = campo(); txtTelMovil.setBounds(435, 60, 155, 26); pnlDatos.add(txtTelMovil);

        // Fila 3 — Tel. Convencional (opcional)
        addLbl(pnlDatos, "Tel. Convencional:", 10, 98, 155, 22);
        txtTelConv = campo(); txtTelConv.setBounds(170, 98, 155, 26); pnlDatos.add(txtTelConv);
        JLabel lblOpc = new JLabel("(opcional)");
        lblOpc.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblOpc.setForeground(new Color(120,120,120));
        lblOpc.setBounds(335, 98, 80, 22); pnlDatos.add(lblOpc);

        // Fila 4 — Vivienda / Tipo residente / Mascotas
        addLbl(pnlDatos, "N° Vivienda: *",      10, 136, 130, 22);
        comboVivienda = new JComboBox<>(viviendas());
        comboVivienda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboVivienda.setBounds(145, 136, 70, 26); pnlDatos.add(comboVivienda);

        addLbl(pnlDatos, "Tipo residente: *", 230, 136, 145, 22);
        comboTipoRes = new JComboBox<>(new String[]{"Propietario", "Residente"});
        comboTipoRes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoRes.setBounds(380, 136, 130, 26); pnlDatos.add(comboTipoRes);

        addLbl(pnlDatos, "¿Mascotas?", 10, 175, 115, 22);
        rdoSiMascota = new JRadioButton("Sí");
        rdoNoMascota = new JRadioButton("No");
        rdoSiMascota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdoNoMascota.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdoSiMascota.setBackground(new Color(248,248,248));
        rdoNoMascota.setBackground(new Color(248,248,248));
        ButtonGroup bg = new ButtonGroup(); bg.add(rdoSiMascota); bg.add(rdoNoMascota);
        rdoSiMascota.setBounds(130, 173, 50, 26); pnlDatos.add(rdoSiMascota);
        rdoNoMascota.setBounds(185, 173, 50, 26); pnlDatos.add(rdoNoMascota);

        // Estado del residente (misma fila que mascotas, a la derecha)
        addLbl(pnlDatos, "Estado: *", 260, 175, 70, 22);
        comboEstadoRes = new JComboBox<>(new String[]{"Activo", "Cancelado"});
        comboEstadoRes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstadoRes.setBounds(335, 173, 130, 26); pnlDatos.add(comboEstadoRes);

        // ══════════════════════════════════════════════════════════
        // SECCIÓN 2 — VEHÍCULOS
        // ══════════════════════════════════════════════════════════
        JPanel pnlVeh = seccion("Vehículos  (0 = sin vehículos)", 640, 45, 390, 235);
        pnl.add(pnlVeh);

        addLbl(pnlVeh, "N° vehículos:", 10, 22, 130, 22);
        spinnerNumVeh = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
        spinnerNumVeh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerNumVeh.setBounds(145, 22, 55, 26); pnlVeh.add(spinnerNumVeh);
        spinnerNumVeh.addChangeListener(e -> { if ((int)spinnerNumVeh.getValue()==0) modeloVehiculos.setRowCount(0); });

        addLbl(pnlVeh, "Placa:", 10, 60, 55, 22);
        txtPlaca = campo();
        txtPlaca.setToolTipText("Ej: ABC-1234");
        txtPlaca.setBounds(70, 60, 110, 26); pnlVeh.add(txtPlaca);

        addLbl(pnlVeh, "Tipo:", 190, 60, 45, 22);
        comboTipoVeh = new JComboBox<>(new String[]{"Liviano", "Pesado", "Buseta"});
        comboTipoVeh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoVeh.setBounds(240, 60, 100, 26); pnlVeh.add(comboTipoVeh);

        JButton btnAgrVeh = btn("+ Agregar",  new Color(0,120,0),   Color.WHITE);
        JButton btnElmVeh = btn("✕ Eliminar", new Color(180,0,0),   Color.WHITE);
        btnAgrVeh.setBounds(10, 96, 110, 26); pnlVeh.add(btnAgrVeh);
        btnElmVeh.setBounds(130, 96, 110, 26); pnlVeh.add(btnElmVeh);
        btnAgrVeh.addActionListener(e -> agregarVehiculo());
        btnElmVeh.addActionListener(e -> eliminarVehiculo());

        modeloVehiculos = new DefaultTableModel(new String[]{"Placa","Tipo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVehiculos = new JTable(modeloVehiculos);
        tablaVehiculos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVehiculos.setRowHeight(20);
        tablaVehiculos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaVehiculos.getTableHeader().setBackground(new Color(240,240,240));
        JScrollPane sv = new JScrollPane(tablaVehiculos);
        sv.setBounds(10, 130, 365, 90); pnlVeh.add(sv);

        // ── Botones principales ────────────────────────────────────
        btnGuardar  = btn("GUARDAR CAMBIOS", new Color(0,0,0),     Color.WHITE);
        btnCancelar = btn("CANCELAR",        new Color(80,80,80),  Color.WHITE);
        btnGuardar.setBounds(15,  295, 180, 34); pnl.add(btnGuardar);
        btnCancelar.setBounds(210, 295, 130, 34); pnl.add(btnCancelar);

        // Nota informativa
        JLabel lblNota = new JLabel(" ");
        lblNota.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblNota.setForeground(new Color(120,120,120));
        lblNota.setBounds(360, 303, 500, 18); pnl.add(lblNota);
    }

    // ── Cargar datos del residente en los campos ───────────────────
    private void cargarDatos(Residentes r) {
        txtNombres.setText(r.getNombres());
        txtApellidos.setText(r.getApellidos());
        txtCedula.setText(r.getCedula());
        txtTelMovil.setText(r.getTelefonoMovil() != null ? r.getTelefonoMovil() : "");
        txtTelConv.setText(r.getTelefonoConvencional() != null ? r.getTelefonoConvencional() : "");

        // Seleccionar vivienda
        String viv = r.getNumeroVivienda();
        if (viv != null) {
            for (int i = 0; i < comboVivienda.getItemCount(); i++) {
                if (comboVivienda.getItemAt(i).equals(viv)) { comboVivienda.setSelectedIndex(i); break; }
            }
        }

        // Seleccionar tipo de residente
        String tipo = r.getTipoResidente();
        if ("Propietario".equalsIgnoreCase(tipo)) comboTipoRes.setSelectedIndex(0);
        else                                       comboTipoRes.setSelectedIndex(1);

        // Mascotas
        if (r.isTieneMascotas()) rdoSiMascota.setSelected(true);
        else                     rdoNoMascota.setSelected(true);

        // Estado del residente
        String est = r.getEstadoResidente();
        comboEstadoRes.setSelectedItem(est != null ? est : "Activo");

        // Vehículos
        modeloVehiculos.setRowCount(0);
        if (r.getVehiculos() != null) {
            for (String[] v : r.getVehiculos()) {
                modeloVehiculos.addRow(new Object[]{v[0], v[1]});
            }
        }
        spinnerNumVeh.setValue(modeloVehiculos.getRowCount());
    }

    // ── Guardar cambios en MongoDB ─────────────────────────────────
    private void guardarCambios() {
        String nombres   = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String cedula    = txtCedula.getText().trim();
        String telMovil  = txtTelMovil.getText().trim();
        String telConv   = txtTelConv.getText().trim();

        // Validaciones básicas
        if (nombres.isEmpty())   { msg("Debe ingresar los nombres."); return; }
        if (apellidos.isEmpty()) { msg("Debe ingresar los apellidos."); return; }
        if (cedula.isEmpty())    { msg("Debe ingresar la cédula."); return; }
        if (telMovil.isEmpty() && telConv.isEmpty()) {
            msg("Debe ingresar al menos un teléfono (Móvil o Convencional)."); return;
        }

        // Recolectar vehículos
        List<String[]> vehiculos = new ArrayList<>();
        for (int i = 0; i < modeloVehiculos.getRowCount(); i++) {
            String placa = (String) modeloVehiculos.getValueAt(i, 0);
            String tipov = (String) modeloVehiculos.getValueAt(i, 1);
            if (placa == null || placa.trim().isEmpty()) {
                msg("El vehículo #" + (i+1) + " tiene la placa vacía."); return;
            }
            vehiculos.add(new String[]{placa.trim().toUpperCase(), tipov});
        }

        // Construir residente actualizado (conservar la fecha original de registro)
        Residentes actualizado = new Residentes(
            nombres, apellidos, cedula,
            telMovil.isEmpty() ? telConv : telMovil,
            telConv,
            (String) comboVivienda.getSelectedItem(),
            (String) comboTipoRes.getSelectedItem(),
            rdoSiMascota.isSelected(),
            vehiculos
        );
        actualizado.setFechaRegistro(residenteOriginal.getFechaRegistro());
        String nuevoEstado = (String) comboEstadoRes.getSelectedItem();
        actualizado.setEstadoResidente(nuevoEstado);

        // ── Validaciones de formato ────────────────────────────────
        String error = validaciones.validarResidente(actualizado);
        if (error != null) { msg(error); return; }

        // ── Validaciones de unicidad (excluyendo la cédula original) ──

        // 1. Si cambia la cédula, verificar que la nueva no exista
        if (!cedula.equals(cedulaOriginal) && repo.cedulaExiste(cedula, cedulaOriginal)) {
            msg("La cédula " + cedula + " ya está registrada en otro residente."); return;
        }

        // 2. Verificar casa disponible:
        //    - Si el estado es "Activo" y la casa cambió (o se mantiene), la casa debe estar libre
        //    - Si el estado es "Cancelado", la casa queda libre (no bloquear)
        String nuevaVivienda = (String) comboVivienda.getSelectedItem();
        if ("Activo".equals(nuevoEstado)) {
            // Verificar que la casa no esté ocupada por OTRO residente activo
            String ocupadaPor = repo.casaOcupadaPorActivo(nuevaVivienda, cedulaOriginal);
            if (ocupadaPor != null) {
                msg("No se puede activar este residente en la Casa N° " + nuevaVivienda
                    + " porque ya está asignada a:\n" + ocupadaPor
                    + "\n\nOpciones:\n  • Cambie primero el estado del otro residente a 'Cancelado'\n  • O asigne una casa diferente a este residente.");
                return;
            }
        }

        // 3. Teléfono móvil duplicado (excluyendo el propio residente)
        if (!telMovil.isEmpty()) {
            String duploMovil = repo.telefonoMovilExiste(telMovil, cedulaOriginal);
            if (duploMovil != null) {
                msg("El teléfono móvil " + telMovil + " ya está registrado en:\n" + duploMovil); return;
            }
        }

        // 4. Teléfono convencional duplicado (excluyendo el propio residente)
        if (!telConv.isEmpty()) {
            String duploConv = repo.telefonoConvExiste(telConv, cedulaOriginal);
            if (duploConv != null) {
                msg("El teléfono convencional " + telConv + " ya está registrado en:\n" + duploConv); return;
            }
        }

        // 5. Validar placas duplicadas (excluyendo el propio residente)
        for (String[] v : vehiculos) {
            String placa = v[0].trim().toUpperCase();
            if (!placa.isEmpty()) {
                String duploPlaca = repo.placaExiste(placa, cedulaOriginal);
                if (duploPlaca != null) {
                    msg("La placa " + placa + " ya está registrada en:\n" + duploPlaca); return;
                }
            }
        }

        // 6. Si se cambia a "Cancelado": verificar que no tenga deudas pendientes
        if ("Cancelado".equals(nuevoEstado) && !"Cancelado".equals(residenteOriginal.getEstadoResidente())) {
            Modelo.AlmacenarAlicuotas repoAlic = new Modelo.AlmacenarAlicuotas();
            Modelo.AlmacenarMultas    repoMult = new Modelo.AlmacenarMultas();
            String casa = (String) comboVivienda.getSelectedItem();
            StringBuilder deudas = new StringBuilder();
            double alicPend = 0, multPend = 0;
            for (Modelo.Alicuota a : repoAlic.obtenerTodas()) {
                if (casa.equals(a.getNumeroCasa()) &&
                   ("Pendiente".equals(a.getEstado()) || "Atrasado".equals(a.getEstado())))
                    alicPend += a.getMonto();
            }
            for (Modelo.Multa m : repoMult.obtenerTodas()) {
                if (casa.equals(m.getNumeroCasa()) && "Pendiente".equals(m.getEstado()))
                    multPend += m.getMonto();
            }
            if (alicPend > 0 || multPend > 0) {
                String msj = "No se puede cancelar este residente porque tiene deudas pendientes:\n\n";
                if (alicPend > 0) msj += "  • Alícuotas pendientes/atrasadas: $" + String.format("%.2f", alicPend) + "\n";
                if (multPend > 0) msj += "  • Multas pendientes: $" + String.format("%.2f", multPend) + "\n";
                msj += "\nRegularice los pagos antes de cancelar el residente.";
                msg(msj); return;
            }
        }

        try {
            repo.actualizar(cedulaOriginal, actualizado);
            msg("Residente actualizado correctamente en la base de datos.");
            ctrlBusqueda.refrescarBusqueda();
            dispose();
        } catch (Exception ex) {
            msg("Error al actualizar en MongoDB: " + ex.getMessage());
        }
    }

    // ── Vehículos ──────────────────────────────────────────────────
    private void agregarVehiculo() {
        String placa = txtPlaca.getText().trim().toUpperCase();
        String tipo  = (String) comboTipoVeh.getSelectedItem();
        if (placa.isEmpty()) { msg("Ingrese la placa del vehículo."); return; }
        for (int i = 0; i < modeloVehiculos.getRowCount(); i++) {
            if (placa.equals(modeloVehiculos.getValueAt(i, 0))) {
                msg("La placa " + placa + " ya fue ingresada."); return;
            }
        }
        modeloVehiculos.addRow(new Object[]{placa, tipo});
        spinnerNumVeh.setValue(modeloVehiculos.getRowCount());
        txtPlaca.setText("");
    }

    private void eliminarVehiculo() {
        int fila = tablaVehiculos.getSelectedRow();
        if (fila < 0) { msg("Seleccione un vehículo de la lista para eliminarlo."); return; }
        modeloVehiculos.removeRow(fila);
        spinnerNumVeh.setValue(modeloVehiculos.getRowCount());
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private void addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x,y,w,h); p.add(l);
    }
    private JTextField campo() {
        JTextField f = new JTextField(); f.setFont(new Font("Segoe UI", Font.PLAIN, 13)); return f;
    }
    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b;
    }
    private JPanel seccion(String titulo, int x, int y, int w, int h) {
        JPanel p = new JPanel(null);
        p.setBackground(new Color(248,248,248));
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            titulo, 0, 0, new Font("Segoe UI", Font.BOLD, 12)));
        p.setBounds(x,y,w,h); return p;
    }
    private String[] viviendas() {
        String[] a = new String[20]; for (int i=0;i<20;i++) a[i]=String.valueOf(i+1); return a;
    }
    private void msg(String m) { JOptionPane.showMessageDialog(this, m); }
}
