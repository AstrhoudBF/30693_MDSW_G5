package Vista;

import Modelo.AlmacenarArriendosSede;
import Modelo.ArriendoSede;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Pantalla dedicada al historial completo de reservas de la Sede Social.
 * Permite buscar por teléfono, editar una reserva seleccionada (reabre
 * Formulario_Arriendos_Sede) y regresar a dicho formulario.
 */
public class Formulario_Historial_Sede extends JFrame {

    private final Formulario_Arriendos_Sede vistaPadre;
    private final AlmacenarArriendosSede    repo = new AlmacenarArriendosSede();

    private ArrayList<ArriendoSede> listaCompleta = new ArrayList<>();
    private ArrayList<String>       idsTabla       = new ArrayList<>();

    private DefaultTableModel modeloTabla;
    private JTable            tablaHistorial;
    private JTextField        txtBusqueda;
    private JButton           btnBuscar;
    private JButton           btnEditar;
    private JButton           btnAnular;
    private JButton           btnRegresar;
    private JLabel            lblConteo;

    public Formulario_Historial_Sede(Formulario_Arriendos_Sede vistaPadre) {
        this.vistaPadre = vistaPadre;
        initComponents();
        cargarDatos();
    }

    // ─────────────────────────────────────────────────────────────
    private void initComponents() {
        setTitle("Historial Completo de Reservas — Sede Social");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 680);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        getContentPane().add(pnl);

        // ── Encabezado ────────────────────────────────────────────
        JPanel pnlTop = new JPanel(null);
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setPreferredSize(new Dimension(1170, 72));
        pnl.add(pnlTop, BorderLayout.NORTH);

        JLabel lblTit = new JLabel("Historial de Reservas — Sede Social");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setBounds(0, 0, 500, 28);
        pnlTop.add(lblTit);

        // ── Barra de búsqueda ──────────────────────────────────────
        JPanel pnlBusq = new JPanel(null);
        pnlBusq.setBackground(new Color(247, 247, 247));
        pnlBusq.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Buscar por teléfono", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlBusq.setBounds(0, 30, 1170, 68);
        pnlTop.add(pnlBusq);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTel.setBounds(12, 22, 90, 26);
        pnlBusq.add(lblTel);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.setToolTipText("Ingrese el número de teléfono a buscar");
        txtBusqueda.setBounds(106, 22, 250, 26);
        txtBusqueda.addActionListener(e -> buscar()); // Enter también busca
        pnlBusq.add(txtBusqueda);

        btnBuscar = mkBtn("🔍 Buscar", new Color(0, 0, 0), Color.WHITE);
        btnBuscar.setBounds(368, 22, 120, 26);
        btnBuscar.addActionListener(e -> buscar());
        pnlBusq.add(btnBuscar);

        JButton btnLimpiar = mkBtn("✕ Limpiar", new Color(100, 100, 100), Color.WHITE);
        btnLimpiar.setBounds(500, 22, 110, 26);
        btnLimpiar.addActionListener(e -> {
            txtBusqueda.setText("");
            cargarDatos();
        });
        pnlBusq.add(btnLimpiar);

        // ── Centro: tabla ──────────────────────────────────────────
        modeloTabla = new DefaultTableModel(
            new String[]{"Fecha Reserva", "Solicitante", "Tipo", "Casa",
                         "Teléfono", "Email", "Modalidad", "H.Inicio", "H.Fin",
                         "Monto ($)", "Estado", "Forma Pago",
                         "N° Tx", "Fecha Pago", "Motivo", "Registrado"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaHistorial.setRowHeight(22);
        tablaHistorial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaHistorial.setSelectionBackground(new Color(200, 220, 255));
        tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaHistorial.getTableHeader().setBackground(Color.WHITE);
        tablaHistorial.getTableHeader().setForeground(Color.BLACK);
        tablaHistorial.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Ancho de columnas
        int[] anchos = {95, 160, 80, 55, 90, 170, 90, 65, 60, 70, 85, 95, 90, 80, 130, 120};
        for (int i = 0; i < anchos.length && i < tablaHistorial.getColumnCount(); i++) {
            tablaHistorial.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        // Habilitar botón Editar al seleccionar fila
        tablaHistorial.getSelectionModel().addListSelectionListener(e -> {
            boolean hay = tablaHistorial.getSelectedRow() >= 0;
            btnEditar.setEnabled(hay);
            btnAnular.setEnabled(hay);
        });

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnl.add(scrollTabla, BorderLayout.CENTER);

        // ── Panel inferior: conteo + botones ───────────────────────
        JPanel pnlBot = new JPanel(null);
        pnlBot.setBackground(Color.WHITE);
        pnlBot.setPreferredSize(new Dimension(1170, 42));
        pnl.add(pnlBot, BorderLayout.SOUTH);

        lblConteo = new JLabel("0 registro(s)");
        lblConteo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblConteo.setForeground(new Color(100, 100, 100));
        lblConteo.setBounds(0, 8, 200, 22);
        pnlBot.add(lblConteo);

        btnEditar = mkBtn("✏ Editar reserva seleccionada", new Color(30, 100, 180), Color.WHITE);
        btnEditar.setEnabled(false);
        btnEditar.setBounds(220, 5, 240, 30);
        btnEditar.addActionListener(e -> abrirEdicion());
        pnlBot.add(btnEditar);

        btnAnular = mkBtn("🚫 Anular seleccionada", new Color(150, 80, 0), Color.WHITE);
        btnAnular.setEnabled(false);
        btnAnular.setBounds(470, 5, 200, 30);
        btnAnular.addActionListener(e -> anularSeleccionada());
        pnlBot.add(btnAnular);

        btnRegresar = mkBtn("← Regresar a Reservas", new Color(80, 80, 80), Color.WHITE);
        btnRegresar.setBounds(680, 5, 200, 30);
        btnRegresar.addActionListener(e -> regresar());
        pnlBot.add(btnRegresar);

        // Al cerrar con la X también regresa
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                vistaPadre.setVisible(true);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Cargar todos los datos
    // ─────────────────────────────────────────────────────────────
    private void cargarDatos() {
        listaCompleta = repo.obtenerTodas();
        llenarTabla(listaCompleta);
    }

    // ─────────────────────────────────────────────────────────────
    // Buscar por teléfono — los resultados aparecen al inicio
    // ─────────────────────────────────────────────────────────────
    private void buscar() {
        String filtro = txtBusqueda.getText().trim();
        if (filtro.isEmpty()) {
            cargarDatos();
            return;
        }

        // Separar coincidencias y no-coincidencias
        ArrayList<ArriendoSede> coinciden = new ArrayList<>();
        ArrayList<ArriendoSede> resto     = new ArrayList<>();
        for (ArriendoSede s : listaCompleta) {
            String tel = s.getTelefono() != null ? s.getTelefono() : "";
            if (tel.contains(filtro)) coinciden.add(s);
            else                      resto.add(s);
        }

        if (coinciden.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron reservas con el teléfono: " + filtro,
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            llenarTabla(listaCompleta); // mostrar todo igual
            return;
        }

        // Primero las coincidencias, luego el resto
        ArrayList<ArriendoSede> ordenada = new ArrayList<>();
        ordenada.addAll(coinciden);
        ordenada.addAll(resto);
        llenarTabla(ordenada);

        // Seleccionar automáticamente la primera coincidencia
        if (!coinciden.isEmpty()) {
            tablaHistorial.setRowSelectionInterval(0, 0);
            tablaHistorial.scrollRectToVisible(tablaHistorial.getCellRect(0, 0, true));
        }
        lblConteo.setText(coinciden.size() + " coincidencia(s) al inicio — " + listaCompleta.size() + " total");
    }

    // ─────────────────────────────────────────────────────────────
    // Llenar tabla con una lista dada
    // ─────────────────────────────────────────────────────────────
    private void llenarTabla(ArrayList<ArriendoSede> lista) {
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        modeloTabla.setRowCount(0);
        idsTabla.clear();
        btnEditar.setEnabled(false);

        for (ArriendoSede s : lista) {
            String fr  = s.getFechaReserva()  != null ? s.getFechaReserva().format(fmtF)   : "—";
            String fp  = s.getFechaPago()      != null ? s.getFechaPago().format(fmtF)      : "—";
            String reg = s.getFechaRegistro()  != null ? s.getFechaRegistro().format(fmtR)  : "—";
            String ni  = s.getHoraInicio() != null && !s.getHoraInicio().isEmpty() ? s.getHoraInicio() : "—";
            String nf  = s.getHoraFin()    != null && !s.getHoraFin().isEmpty()    ? s.getHoraFin()    : "—";
            String ntx = s.getNumeroTransaccion() != null && !s.getNumeroTransaccion().isEmpty()
                         ? s.getNumeroTransaccion() : "—";
            String casa = s.getNumeroCasaResidente() != null && !s.getNumeroCasaResidente().isEmpty()
                         ? s.getNumeroCasaResidente() : "—";

            modeloTabla.addRow(new Object[]{
                fr,
                s.getNombreSolicitante(),
                s.getTipoSolicitante(),
                casa,
                s.getTelefono()  != null ? s.getTelefono() : "—",
                s.getEmail()     != null ? s.getEmail()    : "—",
                s.getModalidad(),
                ni, nf,
                String.format("%.2f", s.getMonto()),
                s.getEstado(),
                s.getFormaPago(),
                ntx, fp,
                s.getMotivo(),
                reg
            });
            idsTabla.add(s.getId());
        }
        if (lblConteo != null)
            lblConteo.setText(lista.size() + " registro(s)");
    }

    // ─────────────────────────────────────────────────────────────
    // Abrir edición — regresa al formulario de reservas con datos cargados
    // ─────────────────────────────────────────────────────────────
    private void abrirEdicion() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila < 0 || fila >= idsTabla.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para editar.");
            return;
        }
        String id = idsTabla.get(fila);

        // Buscar el objeto completo
        ArriendoSede seleccionada = null;
        for (ArriendoSede s : listaCompleta) {
            if (id.equals(s.getId())) { seleccionada = s; break; }
        }
        // Si la tabla está en modo búsqueda, buscar también en repo
        if (seleccionada == null) {
            for (ArriendoSede s : repo.obtenerTodas()) {
                if (id.equals(s.getId())) { seleccionada = s; break; }
            }
        }
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "No se pudo obtener los datos de la reserva.");
            return;
        }

        // Precargar en el formulario padre y mostrarlo
        vistaPadre.precargarEdicion(seleccionada);
        vistaPadre.getBtnGuardar().setText("Actualizar");
        vistaPadre.getBtnGuardar().setBackground(new Color(30, 100, 180));
        // Notificar al controlador el ID en edición vía método público
        vistaPadre.setIdEnEdicionExterno(id);

        dispose();
        vistaPadre.setVisible(true);
    }

    // ─────────────────────────────────────────────────────────────
    /** Activa el modo anulación (resalta el botón Anular) */
    public void setModoAnulacion(boolean modoAnulacion) {
        if (modoAnulacion) {
            setTitle("Anular Reserva — Sede Social");
            btnAnular.setBackground(new java.awt.Color(180, 50, 0));
        }
    }

    private void anularSeleccionada() {
        int fila = tablaHistorial.getSelectedRow();
        if (fila < 0 || fila >= idsTabla.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para anular."); return;
        }
        String id = idsTabla.get(fila);
        ArriendoSede sel = null;
        for (ArriendoSede s : listaCompleta) { if (id.equals(s.getId())) { sel = s; break; } }
        if (sel == null) { for (ArriendoSede s : repo.obtenerTodas()) { if (id.equals(s.getId())) { sel = s; break; } } }

        if ("Cancelada".equals(sel != null ? sel.getEstado() : "")) {
            JOptionPane.showMessageDialog(this, "Esta reserva ya está anulada."); return;
        }
        String desc = sel != null ? sel.getNombreSolicitante() + " — " + (sel.getFechaReserva() != null ? sel.getFechaReserva().toString() : "") : id;
        int c = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de ANULAR la reserva de:\n" + desc + "?\n\nEl registro permanecerá en el historial con estado 'Cancelada'.",
            "Confirmar anulación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try {
            repo.anular(id);
            listaCompleta = repo.obtenerTodas();
            llenarTabla(listaCompleta);
            JOptionPane.showMessageDialog(this, "Reserva anulada. El registro permanece en el historial.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al anular: " + ex.getMessage());
        }
    }

    private void regresar() {
        dispose();
        vistaPadre.setVisible(true);
    }

    // ── Helper ────────────────────────────────────────────────────
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
