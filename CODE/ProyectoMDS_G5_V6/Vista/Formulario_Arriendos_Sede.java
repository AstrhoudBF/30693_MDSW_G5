package Vista;

import Controlador.controlador_arriendos_sede;
import Modelo.ArriendoSede;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Formulario_Arriendos_Sede extends javax.swing.JFrame implements interfaz_arriendos_sede {

    private DefaultTableModel modeloReservas;
    private DefaultTableModel modeloProximas;

    // ── Componentes del formulario ────────────────────────────────
    private JPanel          pnlPrincipal;
    private JTextField      txtNombre;
    private JComboBox<String> comboTipoSol;
    private JTextField      txtContacto;
    private JSpinner        spinnerFecha;
    private JComboBox<String> comboModalidad;
    private JLabel          lblHoraInicio, lblHoraFin;
    private JTextField      txtHoraInicio, txtHoraFin;
    private JTextField      txtMonto;
    private JTextField      txtMotivo;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboFormaPago;
    private JButton         btnGuardar;

    private JButton         btnHistorial;
    private JTable          tablaReservas;
    private JTable          tablaProximas;

    public Formulario_Arriendos_Sede() {
        initComponents();
        inicializarTablas();
        // Listener de modalidad
        comboModalidad.addActionListener(e ->
            setHorasVisible("Por Horas".equals(comboModalidad.getSelectedItem()))
        );
        setHorasVisible(false);
    }

    private void initComponents() {
        setTitle("Reservas de Sede Social");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(860, 720);
        setLocationRelativeTo(null);
        setResizable(false);

        pnlPrincipal = new JPanel(null);
        pnlPrincipal.setBackground(Color.WHITE);
        getContentPane().add(pnlPrincipal);

        // ── Título ────────────────────────────────────────────────
        JLabel lblTit = new JLabel("Reservas de Sede Social");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTit.setBounds(20, 12, 400, 30);
        pnlPrincipal.add(lblTit);

        // ═══════════════════════════════════════════════════════════
        // PANEL FORMULARIO
        // ═══════════════════════════════════════════════════════════
        JPanel pnlForm = new JPanel(null);
        pnlForm.setBackground(new Color(245, 245, 245));
        pnlForm.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            "Nueva Reserva", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlForm.setBounds(15, 48, 826, 290);
        pnlPrincipal.add(pnlForm);

        int lx = 12, fx = 200, rH = 35, y = 20, lw = 183, fh = 27, fw = 200;

        // Fila 1 — Solicitante
        addLbl(pnlForm, "Nombre del solicitante:", lx, y, lw, fh);
        txtNombre = mkField();
        txtNombre.setBounds(fx, y, fw, fh);
        pnlForm.add(txtNombre);

        addLbl(pnlForm, "Tipo solicitante:", 415, y, 130, fh);
        comboTipoSol = new JComboBox<>(new String[]{"Residente", "Externo"});
        comboTipoSol.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboTipoSol.setBounds(550, y, 150, fh);
        pnlForm.add(comboTipoSol);

        // Fila 2 — Contacto
        y += rH;
        addLbl(pnlForm, "Contacto (tel/email):", lx, y, lw, fh);
        txtContacto = mkField();
        txtContacto.setBounds(fx, y, fw, fh);
        pnlForm.add(txtContacto);

        // Fila 3 — Fecha de reserva
        y += rH;
        addLbl(pnlForm, "Fecha de reserva:", lx, y, lw, fh);
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(dateEditor);
        spinnerFecha.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinnerFecha.setBounds(fx, y, 140, fh);
        pnlForm.add(spinnerFecha);

        // Fila 3 — Modalidad
        addLbl(pnlForm, "Modalidad:", 415, y, 110, fh);
        comboModalidad = new JComboBox<>(new String[]{"Día Completo", "Por Horas"});
        comboModalidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboModalidad.setBounds(530, y, 140, fh);
        pnlForm.add(comboModalidad);

        // Fila 4 — Horas (solo visibles si modalidad = Por Horas)
        y += rH;
        lblHoraInicio = addLbl(pnlForm, "Hora inicio (HH:mm):", lx, y, lw, fh);
        txtHoraInicio = mkField();
        txtHoraInicio.setToolTipText("Ej: 09:00");
        txtHoraInicio.setBounds(fx, y, 100, fh);
        pnlForm.add(txtHoraInicio);

        lblHoraFin = addLbl(pnlForm, "Hora fin (HH:mm):", 320, y, 140, fh);
        txtHoraFin = mkField();
        txtHoraFin.setToolTipText("Ej: 13:00");
        txtHoraFin.setBounds(465, y, 100, fh);
        pnlForm.add(txtHoraFin);

        // Fila 5 — Monto y Motivo
        y += rH;
        addLbl(pnlForm, "Monto ($):", lx, y, lw, fh);
        txtMonto = mkField();
        txtMonto.setBounds(fx, y, 110, fh);
        pnlForm.add(txtMonto);

        addLbl(pnlForm, "Motivo / Evento:", 330, y, 130, fh);
        txtMotivo = mkField();
        txtMotivo.setToolTipText("Ej: Reunión HOA, Cumpleaños, Asamblea...");
        txtMotivo.setBounds(465, y, 340, fh);
        pnlForm.add(txtMotivo);

        // Fila 6 — Estado y Forma de pago
        y += rH;
        addLbl(pnlForm, "Estado:", lx, y, lw, fh);
        comboEstado = new JComboBox<>(new String[]{"Confirmada", "Pendiente", "Cancelada"});
        comboEstado.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboEstado.setBounds(fx, y, 140, fh);
        pnlForm.add(comboEstado);

        addLbl(pnlForm, "Forma de pago:", 415, y, 130, fh);
        comboFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Depósito"});
        comboFormaPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFormaPago.setBounds(550, y, 150, fh);
        pnlForm.add(comboFormaPago);

        // ── Botones ───────────────────────────────────────────────
        y += rH + 8;
        btnGuardar   = mkBtn("Guardar",          new Color(0,0,0),      Color.WHITE);
        btnHistorial = mkBtn("Ver Historial ▸",  new Color(30,100,180), Color.WHITE);
        btnGuardar.setBounds(fx,       y, 130, 30);
        btnHistorial.setBounds(fx+145, y, 155, 30);
        pnlForm.add(btnGuardar);
        pnlForm.add(btnHistorial);

        // ═══════════════════════════════════════════════════════════
        // PANEL RESERVAS PRÓXIMAS  (alerta de choques)
        // ═══════════════════════════════════════════════════════════
        JPanel pnlProx = new JPanel(null);
        pnlProx.setBackground(new Color(255, 248, 220));
        pnlProx.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220,180,0)),
            "⚠ Reservas próximas (consultar antes de agendar)", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlProx.setBounds(15, 345, 826, 130);
        pnlPrincipal.add(pnlProx);

        tablaProximas = new JTable();
        tablaProximas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProximas.setRowHeight(20);
        tablaProximas.setEnabled(false);
        tablaProximas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProximas.getTableHeader().setBackground(new Color(255,248,220));
        JScrollPane scrollProx = new JScrollPane(tablaProximas);
        scrollProx.setBounds(8, 20, 808, 100);
        pnlProx.add(scrollProx);

        // ═══════════════════════════════════════════════════════════
        // TABLA HISTORIAL COMPLETO
        // ═══════════════════════════════════════════════════════════
        JLabel lblHist = new JLabel("Historial completo de reservas");
        lblHist.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblHist.setBounds(15, 482, 300, 20);
        pnlPrincipal.add(lblHist);

        tablaReservas = new JTable();
        tablaReservas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReservas.setRowHeight(20);
        tablaReservas.setEnabled(false);
        tablaReservas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaReservas.getTableHeader().setBackground(Color.WHITE);
        JScrollPane scrollRes = new JScrollPane(tablaReservas);
        scrollRes.setBounds(15, 505, 826, 190);
        pnlPrincipal.add(scrollRes);
    }

    private void inicializarTablas() {
        // Tabla próximas
        modeloProximas = new DefaultTableModel(
            new String[]{"Fecha Reserva", "Solicitante", "Tipo", "Modalidad",
                         "Hora Inicio", "Hora Fin", "Motivo", "Estado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaProximas.setModel(modeloProximas);

        // Tabla historial
        modeloReservas = new DefaultTableModel(
            new String[]{"Fecha Reserva", "Solicitante", "Tipo", "Contacto",
                         "Modalidad", "H.Inicio", "H.Fin", "Monto ($)",
                         "Estado", "Forma Pago", "Motivo", "Registrado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaReservas.setModel(modeloReservas);
    }

    // ── Helpers UI ────────────────────────────────────────────────
    private JLabel addLbl(JPanel p, String t, int x, int y, int w, int h) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setBounds(x, y, w, h);
        p.add(l);
        return l;
    }
    private JTextField mkField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setBackground(bg); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Implementación interfaz_arriendos_sede ────────────────────
    @Override
    public String getNombreSolicitante() { return txtNombre.getText(); }

    @Override
    public String getTipoSolicitante() { return (String) comboTipoSol.getSelectedItem(); }

    @Override
    public String getContacto() { return txtContacto.getText(); }

    @Override
    public LocalDate getFechaReserva() {
        java.util.Date d = (java.util.Date) spinnerFecha.getValue();
        return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public String getModalidad() { return (String) comboModalidad.getSelectedItem(); }

    @Override
    public String getHoraInicio() { return txtHoraInicio.getText(); }

    @Override
    public String getHoraFin() { return txtHoraFin.getText(); }

    @Override
    public double getMonto() {
        return Double.parseDouble(txtMonto.getText().trim().replace(",", "."));
    }

    @Override
    public String getEstado() { return (String) comboEstado.getSelectedItem(); }

    @Override
    public String getFormaPago() { return (String) comboFormaPago.getSelectedItem(); }

    @Override
    public String getMotivo() { return txtMotivo.getText(); }

    @Override
    public JButton getBtnGuardar()   { return btnGuardar; }

    @Override
    public JButton getBtnHistorial() { return btnHistorial; }

    @Override
    public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    @Override
    public void limpiarCampos() {
        txtNombre.setText("");
        comboTipoSol.setSelectedIndex(0);
        txtContacto.setText("");
        spinnerFecha.setValue(new java.util.Date());
        comboModalidad.setSelectedIndex(0);
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        txtMonto.setText("");
        txtMotivo.setText("");
        comboEstado.setSelectedIndex(0);
        comboFormaPago.setSelectedIndex(0);
        setHorasVisible(false);
    }

    @Override
    public void setHorasVisible(boolean visible) {
        lblHoraInicio.setVisible(visible);
        txtHoraInicio.setVisible(visible);
        lblHoraFin.setVisible(visible);
        txtHoraFin.setVisible(visible);
    }

    @Override
    public void actualizarTablaProximas(ArrayList<ArriendoSede> lista) {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        modeloProximas.setRowCount(0);
        for (ArriendoSede s : lista) {
            String fr = s.getFechaReserva() != null ? s.getFechaReserva().format(fmtFecha) : "—";
            modeloProximas.addRow(new Object[]{
                fr, s.getNombreSolicitante(), s.getTipoSolicitante(),
                s.getModalidad(),
                s.getHoraInicio() != null && !s.getHoraInicio().isEmpty() ? s.getHoraInicio() : "—",
                s.getHoraFin()    != null && !s.getHoraFin().isEmpty()    ? s.getHoraFin()    : "—",
                s.getMotivo(), s.getEstado()
            });
        }
    }

    @Override
    public void actualizarTablaReservas(ArrayList<ArriendoSede> lista) {
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        modeloReservas.setRowCount(0);
        for (ArriendoSede s : lista) {
            String fr  = s.getFechaReserva()  != null ? s.getFechaReserva().format(fmtF)  : "—";
            String reg = s.getFechaRegistro() != null ? s.getFechaRegistro().format(fmtR) : "—";
            modeloReservas.addRow(new Object[]{
                fr, s.getNombreSolicitante(), s.getTipoSolicitante(), s.getContacto(),
                s.getModalidad(),
                s.getHoraInicio() != null && !s.getHoraInicio().isEmpty() ? s.getHoraInicio() : "—",
                s.getHoraFin()    != null && !s.getHoraFin().isEmpty()    ? s.getHoraFin()    : "—",
                String.format("%.2f", s.getMonto()),
                s.getEstado(), s.getFormaPago(), s.getMotivo(), reg
            });
        }
    }

    @Override
    public void iniciar() { this.setVisible(true); }
}
