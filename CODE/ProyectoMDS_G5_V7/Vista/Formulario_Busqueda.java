package Vista;

import Modelo.Residentes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Formulario_Busqueda extends javax.swing.JFrame implements interfaz_busqueda {

    private DefaultTableModel modeloTabla;

    // ── Controles de búsqueda ──────────────────────────────────────
    private JComboBox<String>  cmbx_criterio;
    private JTextField         txt_datoBusqueda;
    private JButton            btn_buscar;
    private JButton            btn_regreso;

    // ── Botones de acción sobre la selección ──────────────────────
    private JButton            btn_modificar;

    // ── Tabla de resultados ────────────────────────────────────────
    private JTable             table_resultados;
    private JScrollPane        scrollResultados;

    public Formulario_Busqueda() {
        initComponents();
        inicializarTabla();
        aplicarFiltroSegunCriterio(); // estado inicial
        // Cambiar el filtro cada vez que cambia el criterio
        cmbx_criterio.addActionListener(e -> {
            txt_datoBusqueda.setText("");
            aplicarFiltroSegunCriterio();
        });
    }

    /** Aplica el DocumentFilter adecuado al campo de búsqueda según el criterio seleccionado. */
    private void aplicarFiltroSegunCriterio() {
        String criterio = cmbx_criterio.getSelectedItem() != null
                ? cmbx_criterio.getSelectedItem().toString() : "Nombre";

        javax.swing.text.AbstractDocument doc =
                (javax.swing.text.AbstractDocument) txt_datoBusqueda.getDocument();

        if ("Nombre".equals(criterio)) {
            // Solo letras, espacios, tildes y ñ
            doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int off, String str,
                                         javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*"))
                        super.insertString(fb, off, str, a);
                }
                @Override
                public void replace(FilterBypass fb, int off, int len, String str,
                                    javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*"))
                        super.replace(fb, off, len, str, a);
                }
            });
            txt_datoBusqueda.setToolTipText("Solo letras");
        } else {
            // Cédula o N° Casa: solo dígitos
            doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int off, String str,
                                         javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d*"))
                        super.insertString(fb, off, str, a);
                }
                @Override
                public void replace(FilterBypass fb, int off, int len, String str,
                                    javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str != null && str.matches("\\d*"))
                        super.replace(fb, off, len, str, a);
                }
            });
            txt_datoBusqueda.setToolTipText("Solo números");
        }
    }

    private void initComponents() {
        setTitle("Búsqueda de Residentes");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1260, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel pnlPrincipal = new JPanel(null);
        pnlPrincipal.setBackground(Color.WHITE);
        getContentPane().add(pnlPrincipal);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTitulo = new JLabel("Búsqueda de Residentes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setBounds(20, 12, 350, 30);
        pnlPrincipal.add(lblTitulo);

        // ── Panel de búsqueda ──────────────────────────────────────
        JPanel pnlBusq = new JPanel(null);
        pnlBusq.setBackground(new Color(245, 245, 245));
        pnlBusq.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Parámetros de búsqueda", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlBusq.setBounds(15, 48, 1220, 90);
        pnlPrincipal.add(pnlBusq);

        JLabel lblCriterio = lbl("Criterio:");
        lblCriterio.setBounds(12, 28, 80, 24);
        pnlBusq.add(lblCriterio);

        cmbx_criterio = new JComboBox<>(new String[]{"Nombre", "Cedula", "N° Casa"});
        cmbx_criterio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbx_criterio.setBounds(95, 28, 140, 28);
        pnlBusq.add(cmbx_criterio);

        JLabel lblDato = lbl("Dato a buscar:");
        lblDato.setBounds(255, 28, 130, 24);
        pnlBusq.add(lblDato);

        txt_datoBusqueda = new JTextField();
        txt_datoBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_datoBusqueda.setBounds(390, 28, 280, 28);
        pnlBusq.add(txt_datoBusqueda);
        // Buscar también al presionar Enter
        txt_datoBusqueda.addActionListener(e -> btn_buscar.doClick());

        btn_buscar = mkBtn("BUSCAR", new Color(0, 0, 0), Color.WHITE);
        btn_buscar.setBounds(690, 28, 130, 28);
        pnlBusq.add(btn_buscar);

        btn_regreso = mkBtn("← Regresar", new Color(80, 80, 80), Color.WHITE);
        btn_regreso.setBounds(835, 28, 130, 28);
        pnlBusq.add(btn_regreso);

        // ── Barra de acciones (Modificar / Eliminar) ──────────────
        JPanel pnlAcciones = new JPanel(null);
        pnlAcciones.setBackground(new Color(250, 250, 250));
        pnlAcciones.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Acciones sobre el residente seleccionado", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlAcciones.setBounds(15, 146, 1220, 65);
        pnlPrincipal.add(pnlAcciones);

        JLabel lblInfo = new JLabel("Seleccione una fila de la tabla y luego use los botones:");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(100, 100, 100));
        lblInfo.setBounds(12, 22, 430, 22);
        pnlAcciones.add(lblInfo);

        btn_modificar = mkBtn("✏ Modificar residente", new Color(30, 100, 180), Color.WHITE);
        btn_modificar.setBounds(450, 18, 210, 30);
        btn_modificar.setEnabled(false);
        pnlAcciones.add(btn_modificar);

        // ── Encabezado de la tabla ─────────────────────────────────
        JLabel lblTabla = new JLabel("Resultados de la búsqueda:");
        lblTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTabla.setBounds(15, 218, 300, 22);
        pnlPrincipal.add(lblTabla);

        // ── Tabla de resultados ────────────────────────────────────
        table_resultados = new JTable();
        table_resultados.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table_resultados.setRowHeight(22);
        table_resultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table_resultados.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table_resultados.getTableHeader().setBackground(Color.WHITE);
        table_resultados.getTableHeader().setForeground(Color.BLACK);

        // Habilitar botón al seleccionar fila
        table_resultados.getSelectionModel().addListSelectionListener(e -> {
            boolean haySeleccion = table_resultados.getSelectedRow() >= 0;
            btn_modificar.setEnabled(haySeleccion);
        });

        scrollResultados = new JScrollPane(table_resultados);
        scrollResultados.setBounds(15, 244, 1220, 420);
        pnlPrincipal.add(scrollResultados);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(
            new String[]{
                "Nombres", "Apellidos", "Cédula",
                "Tel. Móvil", "Tel. Convencional",
                "N° Vivienda", "Tipo Residente", "Estado",
                "Mascotas", "N° Vehículos", "Vehículos (Placa / Tipo)",
                "Fecha de Registro"
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table_resultados.setModel(modeloTabla);
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Implementación interfaz_busqueda ───────────────────────────
    @Override public JComboBox  getComboCriterio()   { return cmbx_criterio;      }
    @Override public JTextField getTxtBusqueda()     { return txt_datoBusqueda;   }
    @Override public String     getStrBusqueda()     { return txt_datoBusqueda.getText().trim(); }
    @Override public JButton    getBtnBusqueda()     { return btn_buscar;         }
    @Override public JButton    getBtnRegreso()      { return btn_regreso;        }
    @Override public JButton    getBtnModificar()    { return btn_modificar;      }

    @Override
    public String getCedulaSeleccionada() {
        int fila = table_resultados.getSelectedRow();
        if (fila < 0) return null;
        // Cédula está en la columna 2
        Object val = modeloTabla.getValueAt(fila, 2);
        return val != null ? val.toString() : null;
    }

    @Override
    public void mostrarResultados(ArrayList<Residentes> lista) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        modeloTabla.setRowCount(0);
        btn_modificar.setEnabled(false);
        for (Residentes r : lista) {
            String fecha = r.getFechaRegistro() != null ? r.getFechaRegistro().format(fmt) : "—";
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

    @Override public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }
    @Override public void limpiarTabla()             { modeloTabla.setRowCount(0); btn_modificar.setEnabled(false); }
    @Override public void setVisible()               { this.setVisible(true); }
    @Override public void dispose()                  { super.dispose(); }
}
