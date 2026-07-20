package Vista;

import Modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Formulario_Consultas extends JFrame {

    private final JFrame menuPadre;
    private final ConsultaCasa servicio = new ConsultaCasa();

    // ── Controles de búsqueda ─────────────────────────────────────
    private JComboBox<String> comboCasa;
    private JButton           btnBuscar;
    private JButton           btnExportar;
    private JButton           btnRegresar;

    // ── Panel de resultados ───────────────────────────────────────
    private JPanel            pnlResumen;
    private JLabel            lblNombreResidente;
    private JLabel            lblTotalDeuda;
    private JTabbedPane       tabsDetalle;

    // Tablas por módulo
    private JTable tablaAlicuotas;
    private JTable tablaMultas;
    private JTable tablaArriendos;
    private JTable tablaReservasSede;

    // Modelos
    private DefaultTableModel modeloAlicuotas;
    private DefaultTableModel modeloMultas;
    private DefaultTableModel modeloArriendos;
    private DefaultTableModel modeloReservasSede;

    // Datos actuales
    private String            casaActual = null;
    private Residentes        residenteActual = null;
    private ArrayList<Alicuota>     alicuotasActuales = new ArrayList<>();
    private ArrayList<Multa>        multasActuales    = new ArrayList<>();
    private ArrayList<Arriendo>     arriendosActuales = new ArrayList<>();
    private ArrayList<ArriendoSede> sedeActuales      = new ArrayList<>();

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Formulario_Consultas(JFrame menuPadre) {
        this.menuPadre = menuPadre;
        initComponents();
        inicializarTablas();
    }

    private void initComponents() {
        setTitle("Consultas por Casa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1150, 780);
        setLocationRelativeTo(null);
        setResizable(true);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                if (menuPadre != null) menuPadre.setVisible(true);
            }
        });

        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        getContentPane().add(pnl);

        // ── Título ─────────────────────────────────────────────────
        JLabel lblTit = new JLabel("Consultas por Casa");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnl.add(lblTit, BorderLayout.NORTH);

        // ── Panel búsqueda ─────────────────────────────────────────
        JPanel pnlBusq = new JPanel(null);
        pnlBusq.setBackground(new Color(247, 247, 247));
        pnlBusq.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            "Seleccionar casa", 0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlBusq.setPreferredSize(new Dimension(1120, 70));

        JLabel lblCasa = new JLabel("N° de Casa:");
        lblCasa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCasa.setBounds(12, 22, 100, 26);
        pnlBusq.add(lblCasa);

        comboCasa = new JComboBox<>();
        comboCasa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboCasa.addItem("-- Seleccione --");
        for (int i = 1; i <= 20; i++) comboCasa.addItem(String.valueOf(i));
        comboCasa.setBounds(115, 22, 130, 28);
        pnlBusq.add(comboCasa);

        btnBuscar = mkBtn("🔍 Consultar", Color.BLACK, Color.WHITE);
        btnBuscar.setBounds(260, 22, 150, 28);
        btnBuscar.addActionListener(e -> buscar());
        pnlBusq.add(btnBuscar);

        btnExportar = mkBtn("📄 Generar PDF", new Color(30, 100, 180), Color.WHITE);
        btnExportar.setBounds(425, 22, 160, 28);
        btnExportar.setEnabled(false);
        btnExportar.addActionListener(e -> generarPdf());
        pnlBusq.add(btnExportar);

        btnRegresar = mkBtn("← Regresar", new Color(80, 80, 80), Color.WHITE);
        btnRegresar.setBounds(600, 22, 130, 28);
        btnRegresar.addActionListener(e -> {
            dispose();
            if (menuPadre != null) menuPadre.setVisible(true);
        });
        pnlBusq.add(btnRegresar);

        JLabel lblHint = new JLabel("Al hacer clic en \"Generar PDF\" se abrirá el reporte en el navegador. Use Ctrl+P → Guardar como PDF.");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(120, 120, 120));
        lblHint.setBounds(750, 26, 360, 20);
        pnlBusq.add(lblHint);

        pnl.add(pnlBusq, BorderLayout.NORTH);

        // ── Panel central (resumen + tabs) ─────────────────────────
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 8));
        pnlCentro.setBackground(Color.WHITE);
        pnl.add(pnlCentro, BorderLayout.CENTER);

        // Tarjetas de resumen
        pnlResumen = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        pnlResumen.setBackground(Color.WHITE);
        pnlResumen.setPreferredSize(new Dimension(1120, 80));
        pnlResumen.setVisible(false);
        pnlCentro.add(pnlResumen, BorderLayout.NORTH);

        // Tabs de detalle
        tabsDetalle = new JTabbedPane();
        tabsDetalle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabsDetalle.setVisible(false);
        pnlCentro.add(tabsDetalle, BorderLayout.CENTER);
    }

    private void inicializarTablas() {
        // Alícuotas
        modeloAlicuotas = modelo("Período","Monto ($)","Estado","Forma Pago","N° Tx","Fecha Pago","Registrado");
        tablaAlicuotas = tabla(modeloAlicuotas);

        // Multas
        modeloMultas = modelo("Categoría","Motivo","Fecha Infracción","Monto ($)","Estado","Observaciones");
        tablaMultas = tabla(modeloMultas);

        // Arriendos
        modeloArriendos = modelo("Tipo","Espacio","Período","Monto ($)","Estado","Forma Pago","N° Tx","Fecha Pago");
        tablaArriendos = tabla(modeloArriendos);

        // Sede
        modeloReservasSede = modelo("Fecha Reserva","Modalidad","H.Inicio","H.Fin","Monto ($)","Estado","Forma Pago","Motivo");
        tablaReservasSede = tabla(modeloReservasSede);
    }

    // ── BUSCAR ─────────────────────────────────────────────────────
    private void buscar() {
        String casa = (String) comboCasa.getSelectedItem();
        if (casa == null || casa.equals("-- Seleccione --")) {
            JOptionPane.showMessageDialog(this, "Seleccione un número de casa para consultar.");
            return;
        }

        casaActual        = casa;
        residenteActual   = servicio.obtenerResidente(casa);
        alicuotasActuales = servicio.obtenerAlicuotas(casa);
        multasActuales    = servicio.obtenerMultas(casa);
        arriendosActuales = servicio.obtenerArriendos(casa);
        sedeActuales      = servicio.obtenerReservasSede(casa);

        mostrarResumen();
        llenarTablas();
        construirTabs();

        pnlResumen.setVisible(true);
        tabsDetalle.setVisible(true);
        btnExportar.setEnabled(true);

        revalidate(); repaint();
    }

    private void mostrarResumen() {
        pnlResumen.removeAll();

        double alicPend  = sumaEstado(alicuotasActuales, "Pendiente",  1);
        double alicAtras = sumaEstado(alicuotasActuales, "Atrasado",   1);
        double multPend  = sumaEstado(multasActuales,    "Pendiente",  2);
        double arrPend   = sumaEstado(arriendosActuales, "Pendiente",  3);
        double total     = alicPend + alicAtras + multPend + arrPend;

        String nombre = residenteActual != null
            ? residenteActual.getNombres() + " " + residenteActual.getApellidos()
            : "Sin residente asignado";

        // Etiqueta nombre
        JLabel lbl = new JLabel("Casa " + casaActual + " — " + nombre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(30, 30, 30));
        pnlResumen.add(lbl);

        // Separador
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 40));
        pnlResumen.add(sep);

        // Tarjetas
        pnlResumen.add(cardSmall("Alíc. Pendientes",  alicPend,  new Color(255, 243, 224)));
        pnlResumen.add(cardSmall("Alíc. Atrasadas",   alicAtras, new Color(255, 235, 238)));
        pnlResumen.add(cardSmall("Multas Pendientes", multPend,  new Color(255, 235, 238)));
        pnlResumen.add(cardSmall("Arriendos Pend.",   arrPend,   new Color(255, 243, 224)));

        // Deuda total
        JPanel cardTotal = new JPanel(null);
        cardTotal.setBackground(new Color(198, 40, 40));
        cardTotal.setPreferredSize(new Dimension(160, 55));
        cardTotal.setBorder(BorderFactory.createLineBorder(new Color(150, 20, 20), 1));
        JLabel lt = new JLabel("⚠ TOTAL DEUDA", SwingConstants.CENTER);
        lt.setFont(new Font("Segoe UI", Font.BOLD, 11)); lt.setForeground(Color.WHITE);
        lt.setBounds(0, 6, 160, 16); cardTotal.add(lt);
        JLabel lv = new JLabel(String.format("$ %.2f", total), SwingConstants.CENTER);
        lv.setFont(new Font("Segoe UI", Font.BOLD, 18)); lv.setForeground(Color.WHITE);
        lv.setBounds(0, 26, 160, 24); cardTotal.add(lv);
        pnlResumen.add(cardTotal);
    }

    private void llenarTablas() {
        modeloAlicuotas.setRowCount(0);
        for (Alicuota a : alicuotasActuales) {
            modeloAlicuotas.addRow(new Object[]{
                ns(a.getPeriodo()), String.format("%.2f", a.getMonto()), a.getEstado(),
                ns(a.getFormaPago()), ns(a.getNumeroTransaccion()).isEmpty()?"—":a.getNumeroTransaccion(),
                a.getFechaPago()!=null ? a.getFechaPago().format(FMT_D) : "—",
                a.getFechaRegistro()!=null ? a.getFechaRegistro().format(FMT_DT) : "—"
            });
        }

        modeloMultas.setRowCount(0);
        for (Multa m : multasActuales) {
            modeloMultas.addRow(new Object[]{
                ns(m.getCategoria()), ns(m.getMotivo()),
                m.getFechaInfraccion()!=null ? m.getFechaInfraccion().format(FMT_D) : "—",
                String.format("%.2f", m.getMonto()), m.getEstado(), ns(m.getObservaciones())
            });
        }

        modeloArriendos.setRowCount(0);
        for (Arriendo a : arriendosActuales) {
            modeloArriendos.addRow(new Object[]{
                ns(a.getTipoEspacio()), ns(a.getNombreEspacio()), ns(a.getMesPeriodo()),
                String.format("%.2f", a.getMontoMensual()), a.getEstado(),
                ns(a.getFormaPago()),
                ns(a.getNumeroTransaccion()).isEmpty()?"—":a.getNumeroTransaccion(),
                a.getFechaPago()!=null ? a.getFechaPago().format(FMT_D) : "—"
            });
        }

        modeloReservasSede.setRowCount(0);
        for (ArriendoSede s : sedeActuales) {
            modeloReservasSede.addRow(new Object[]{
                s.getFechaReserva()!=null ? s.getFechaReserva().format(FMT_D) : "—",
                ns(s.getModalidad()),
                ns(s.getHoraInicio()).isEmpty()?"—":s.getHoraInicio(),
                ns(s.getHoraFin()).isEmpty()?"—":s.getHoraFin(),
                String.format("%.2f", s.getMonto()), s.getEstado(),
                ns(s.getFormaPago()), ns(s.getMotivo())
            });
        }
    }

    private void construirTabs() {
        tabsDetalle.removeAll();
        int nAlíc  = alicuotasActuales.size();
        int nMult  = multasActuales.size();
        int nArr   = arriendosActuales.size();
        int nSede  = sedeActuales.size();

        tabsDetalle.addTab("💵 Alícuotas (" + nAlíc + ")",   scroll(tablaAlicuotas));
        tabsDetalle.addTab("🚫 Multas (" + nMult + ")",       scroll(tablaMultas));
        tabsDetalle.addTab("🏪 Arriendos (" + nArr + ")",     scroll(tablaArriendos));
        tabsDetalle.addTab("🏛 Sede Social (" + nSede + ")",  scroll(tablaReservasSede));

        // Resaltar en rojo las pestañas con pendientes
        if (alicuotasActuales.stream().anyMatch(a -> "Pendiente".equals(a.getEstado()) || "Atrasado".equals(a.getEstado())))
            tabsDetalle.setBackgroundAt(0, new Color(255, 220, 220));
        if (multasActuales.stream().anyMatch(m -> "Pendiente".equals(m.getEstado())))
            tabsDetalle.setBackgroundAt(1, new Color(255, 220, 220));
        if (arriendosActuales.stream().anyMatch(a -> "Pendiente".equals(a.getEstado())))
            tabsDetalle.setBackgroundAt(2, new Color(255, 220, 220));
    }

    // ── GENERAR HTML/PDF ───────────────────────────────────────────
    private void generarPdf() {
        if (casaActual == null) return;
        try {
            String html = GeneradorReporte.generarHtml(
                casaActual, residenteActual,
                alicuotasActuales, multasActuales,
                arriendosActuales, sedeActuales
            );

            // Guardar en un archivo temporal
            File tmpDir = new File(System.getProperty("user.home"), "ReportesMDS");
            tmpDir.mkdirs();
            File archivo = new File(tmpDir, "Reporte_Casa_" + casaActual + ".html");

            try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8.name())) {
                pw.print(html);
            }

            // Abrir en el navegador predeterminado
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(archivo.toURI());
                JOptionPane.showMessageDialog(this,
                    "El reporte se abrió en tu navegador.\n\n" +
                    "Para guardarlo como PDF:\n" +
                    "  1. Presiona Ctrl+P (o Cmd+P en Mac)\n" +
                    "  2. En 'Destino' selecciona 'Guardar como PDF'\n" +
                    "  3. Haz clic en 'Guardar'\n\n" +
                    "El archivo HTML también está en:\n" + archivo.getAbsolutePath(),
                    "Reporte generado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo abrir el navegador automáticamente.\n" +
                    "El reporte HTML se guardó en:\n" + archivo.getAbsolutePath(),
                    "Reporte guardado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al generar el reporte: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers UI ─────────────────────────────────────────────────
    private JPanel cardSmall(String titulo, double monto, Color bg) {
        JPanel c = new JPanel(null);
        c.setBackground(bg);
        c.setPreferredSize(new Dimension(150, 55));
        c.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
        JLabel lt = new JLabel(titulo, SwingConstants.CENTER);
        lt.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lt.setBounds(0, 5, 150, 16); c.add(lt);
        JLabel lv = new JLabel(String.format("$ %.2f", monto), SwingConstants.CENTER);
        lv.setFont(new Font("Segoe UI", Font.BOLD, 15)); lv.setBounds(0, 25, 150, 22); c.add(lv);
        return c;
    }
    private DefaultTableModel modelo(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }
    private JTable tabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(21);
        t.setEnabled(false);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(Color.WHITE);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        return t;
    }
    private JScrollPane scroll(JTable t) {
        return new JScrollPane(t,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    private JButton mkBtn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
    private String ns(String s) { return s != null ? s : ""; }

    @SuppressWarnings("unchecked")
    private double sumaEstado(ArrayList<?> lista, String estado, int tipo) {
        double total = 0;
        for (Object o : lista) {
            if (tipo == 1) { Alicuota a = (Alicuota) o; if (estado.equals(a.getEstado())) total += a.getMonto(); }
            else if (tipo == 2) { Multa m = (Multa) o; if (estado.equals(m.getEstado())) total += m.getMonto(); }
            else { Arriendo a = (Arriendo) o; if (estado.equals(a.getEstado())) total += a.getMontoMensual(); }
        }
        return total;
    }
}
