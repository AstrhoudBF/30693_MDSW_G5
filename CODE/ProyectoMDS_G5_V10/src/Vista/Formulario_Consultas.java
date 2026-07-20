package Vista;

import Modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
        setSize(1150, 820);
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

        JLabel lblHint = new JLabel("Al hacer clic en \"Generar PDF\" se abrirá un diálogo para guardar el archivo PDF directamente.");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(new Color(120, 120, 120));
        lblHint.setBounds(750, 26, 360, 20);
        pnlBusq.add(lblHint);

        pnl.add(pnlBusq, BorderLayout.NORTH);

        // ── Panel central (resumen + tabs) ─────────────────────────
        JPanel pnlCentro = new JPanel(new BorderLayout(0, 8));
        pnlCentro.setBackground(Color.WHITE);
        pnl.add(pnlCentro, BorderLayout.CENTER);

        // Tarjetas de resumen (GridBagLayout para mantener una sola fila,
        // y con el nombre largo se trunca con elipsis en lugar de desbordar)
        pnlResumen = new JPanel(new GridBagLayout());
        pnlResumen.setBackground(Color.WHITE);
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

        casaActual      = casa;
        residenteActual = servicio.obtenerResidente(casa);

        // Ignorar casas con residente cancelado
        if (residenteActual != null && "Cancelado".equals(residenteActual.getEstadoResidente())) {
            JOptionPane.showMessageDialog(this,
                "La Casa N° " + casa + " tiene el residente en estado 'Cancelado'.\n" +
                "Las consultas solo están disponibles para residentes activos.",
                "Residente cancelado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (residenteActual == null) {
            JOptionPane.showMessageDialog(this,
                "La Casa N° " + casa + " no tiene un residente activo asignado.",
                "Sin residente", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

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

        // Etiqueta nombre (ancho fijo + elipsis, así no rompe el layout)
        JLabel lbl = new JLabel("Casa " + casaActual + " — " + nombre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(30, 30, 30));
        lbl.setPreferredSize(new Dimension(260, 20));
        lbl.setMinimumSize(new Dimension(260, 20));
        lbl.setMaximumSize(new Dimension(260, 20));
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        // Separador vertical
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 50));
        sep.setMaximumSize(new Dimension(1, 50));

        // Tarjetas pequeñas
        JPanel c1 = cardSmall("Alíc. Pendientes",  alicPend,  new Color(255, 243, 224));
        JPanel c2 = cardSmall("Alíc. Atrasadas",   alicAtras, new Color(255, 235, 238));
        JPanel c3 = cardSmall("Multas Pendientes", multPend,  new Color(255, 235, 238));
        JPanel c4 = cardSmall("Arriendos Pend.",   arrPend,   new Color(255, 243, 224));

        // Deuda total
        JPanel cardTotal = new JPanel(null);
        cardTotal.setBackground(new Color(198, 40, 40));
        cardTotal.setPreferredSize(new Dimension(160, 55));
        cardTotal.setMinimumSize(new Dimension(160, 55));
        cardTotal.setMaximumSize(new Dimension(160, 55));
        cardTotal.setBorder(BorderFactory.createLineBorder(new Color(150, 20, 20), 1));
        JLabel lt = new JLabel("⚠ TOTAL DEUDA", SwingConstants.CENTER);
        lt.setFont(new Font("Segoe UI", Font.BOLD, 11)); lt.setForeground(Color.WHITE);
        lt.setBounds(0, 6, 160, 16); cardTotal.add(lt);
        JLabel lv = new JLabel(String.format("$ %.2f", total), SwingConstants.CENTER);
        lv.setFont(new Font("Segoe UI", Font.BOLD, 18)); lv.setForeground(Color.WHITE);
        lv.setBounds(0, 26, 160, 24); cardTotal.add(lv);

        // Distribución: nombre, separador, 4 cards, total — todo en una fila,
        // alineado a la izquierda, con gridx creciente.
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.insets = new Insets(4, 4, 4, 4);
        g.fill = GridBagConstraints.VERTICAL;
        g.anchor = GridBagConstraints.CENTER;

        g.gridx = 0; g.weightx = 0;
        pnlResumen.add(lbl, g);

        g.gridx = 1; g.weightx = 0;
        pnlResumen.add(sep, g);

        g.gridx = 2; g.weightx = 0;
        pnlResumen.add(c1, g);

        g.gridx = 3; g.weightx = 0;
        pnlResumen.add(c2, g);

        g.gridx = 4; g.weightx = 0;
        pnlResumen.add(c3, g);

        g.gridx = 5; g.weightx = 0;
        pnlResumen.add(c4, g);

        g.gridx = 6; g.weightx = 0;
        pnlResumen.add(cardTotal, g);

        // Un componente "elástico" al final que empuja todo a la izquierda
        g.gridx = 7; g.weightx = 1;
        pnlResumen.add(Box.createHorizontalGlue(), g);
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
        tabsDetalle.addTab("📊 Pagos Alícuotas",             buildTabPagosAlicuotas());
        tabsDetalle.addTab("🚫 Multas (" + nMult + ")",       scroll(tablaMultas));
        tabsDetalle.addTab("🏪 Arriendos (" + nArr + ")",     scroll(tablaArriendos));
        tabsDetalle.addTab("🏛 Sede Social (" + nSede + ")",  scroll(tablaReservasSede));

        if (alicuotasActuales.stream().anyMatch(a -> "Pendiente".equals(a.getEstado()) || "Atrasado".equals(a.getEstado())))
            tabsDetalle.setBackgroundAt(0, new Color(255, 220, 220));
        if (multasActuales.stream().anyMatch(m -> "Pendiente".equals(m.getEstado())))
            tabsDetalle.setBackgroundAt(2, new Color(255, 220, 220));
        if (arriendosActuales.stream().anyMatch(a -> "Pendiente".equals(a.getEstado())))
            tabsDetalle.setBackgroundAt(3, new Color(255, 220, 220));
    }

    private JPanel buildTabPagosAlicuotas() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(Color.WHITE);

        JTabbedPane sub = new JTabbedPane(JTabbedPane.TOP);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        double totalPagado    = sumaEstado(alicuotasActuales, "Pagado",    1);
        double totalPendiente = sumaEstado(alicuotasActuales, "Pendiente", 1);
        double totalAtrasado  = sumaEstado(alicuotasActuales, "Atrasado",  1);

        // ── Resumen global ──────────────────────────────────────
        JPanel pnlGlobal = new JPanel(new BorderLayout(0, 8));
        pnlGlobal.setBackground(Color.WHITE);
        pnlGlobal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        pnlCards.setBackground(Color.WHITE);
        pnlCards.add(cardSmall("Total Pagado",    totalPagado,    new Color(220, 255, 220)));
        pnlCards.add(cardSmall("Total Pendiente", totalPendiente, new Color(255, 243, 224)));
        pnlCards.add(cardSmall("Total Atrasado",  totalAtrasado,  new Color(255, 235, 238)));
        pnlCards.add(cardSmall("Total General",   totalPagado + totalPendiente + totalAtrasado, new Color(235, 235, 255)));
        pnlGlobal.add(pnlCards, BorderLayout.NORTH);

        DefaultTableModel mGlobal = modelo("Período","Monto ($)","Estado","Forma Pago","Fecha Pago","Registrado");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Modelo.Alicuota a : alicuotasActuales) {
            mGlobal.addRow(new Object[]{
                ns(a.getPeriodo()), String.format("%.2f", a.getMonto()), a.getEstado(),
                ns(a.getFormaPago()),
                a.getFechaPago()!=null ? a.getFechaPago().format(fmtF) : "—",
                a.getFechaRegistro()!=null ? a.getFechaRegistro().format(fmtR) : "—"
            });
        }
        pnlGlobal.add(scroll(tabla(mGlobal)), BorderLayout.CENTER);

        // ── Por mes ─────────────────────────────────────────────
        JPanel pnlMes = new JPanel(new BorderLayout(0, 4));
        pnlMes.setBackground(Color.WHITE);
        pnlMes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        java.util.Map<String, java.util.List<Modelo.Alicuota>> porMes = new java.util.LinkedHashMap<>();
        for (Modelo.Alicuota a : alicuotasActuales) {
            String k = ns(a.getPeriodo()).isEmpty() ? "Sin período" : a.getPeriodo();
            porMes.computeIfAbsent(k, x -> new java.util.ArrayList<>()).add(a);
        }
        java.util.List<java.util.Map.Entry<String, java.util.List<Modelo.Alicuota>>> entradasMes =
            new java.util.ArrayList<>(porMes.entrySet());
        entradasMes.sort(java.util.Comparator.comparing(java.util.Map.Entry::getKey));

        DefaultTableModel mMes = modelo("Período","Cant.","Pagado ($)","Pendiente ($)","Atrasado ($)","Total ($)");
        double gtMes = 0;
        for (java.util.Map.Entry<String, java.util.List<Modelo.Alicuota>> e : entradasMes) {
            double pag = e.getValue().stream().filter(a->"Pagado".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double pen = e.getValue().stream().filter(a->"Pendiente".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double atr = e.getValue().stream().filter(a->"Atrasado".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double tot = pag + pen + atr;
            gtMes += pag;
            mMes.addRow(new Object[]{e.getKey(), e.getValue().size(),
                String.format("%.2f",pag), String.format("%.2f",pen),
                String.format("%.2f",atr), String.format("%.2f",tot)});
        }
        JLabel lblMes = cardBanner("Total pagado (todos los períodos): $" + String.format("%.2f", gtMes), new Color(220,255,220));
        pnlMes.add(lblMes, BorderLayout.NORTH);
        pnlMes.add(scroll(tabla(mMes)), BorderLayout.CENTER);

        // ── Por año ──────────────────────────────────────────────
        JPanel pnlAnio = new JPanel(new BorderLayout(0, 4));
        pnlAnio.setBackground(Color.WHITE);
        pnlAnio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        java.util.Map<String, java.util.List<Modelo.Alicuota>> porAnio = new java.util.LinkedHashMap<>();
        for (Modelo.Alicuota a : alicuotasActuales) {
            String per = ns(a.getPeriodo());
            String anio = per.contains("-") ? per.substring(per.lastIndexOf('-') + 1) : "Sin año";
            porAnio.computeIfAbsent(anio, x -> new java.util.ArrayList<>()).add(a);
        }
        java.util.List<java.util.Map.Entry<String, java.util.List<Modelo.Alicuota>>> entradasAnio =
            new java.util.ArrayList<>(porAnio.entrySet());
        entradasAnio.sort(java.util.Comparator.comparing(java.util.Map.Entry::getKey));

        DefaultTableModel mAnio = modelo("Año","Cant.","Pagado ($)","Pendiente ($)","Atrasado ($)","Total ($)");
        double gtAnio = 0;
        for (java.util.Map.Entry<String, java.util.List<Modelo.Alicuota>> e : entradasAnio) {
            double pag = e.getValue().stream().filter(a->"Pagado".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double pen = e.getValue().stream().filter(a->"Pendiente".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double atr = e.getValue().stream().filter(a->"Atrasado".equals(a.getEstado())).mapToDouble(Modelo.Alicuota::getMonto).sum();
            double tot = pag + pen + atr;
            gtAnio += pag;
            mAnio.addRow(new Object[]{e.getKey(), e.getValue().size(),
                String.format("%.2f",pag), String.format("%.2f",pen),
                String.format("%.2f",atr), String.format("%.2f",tot)});
        }
        JLabel lblAnio = cardBanner("Total pagado (todos los años): $" + String.format("%.2f", gtAnio), new Color(220,255,220));
        pnlAnio.add(lblAnio, BorderLayout.NORTH);
        pnlAnio.add(scroll(tabla(mAnio)), BorderLayout.CENTER);

        sub.addTab("📋 Todas",     pnlGlobal);
        sub.addTab("📅 Por Mes",   pnlMes);
        sub.addTab("📆 Por Año",   pnlAnio);
        p.add(sub, BorderLayout.CENTER);
        return p;
    }

    private JLabel cardBanner(String txt, Color bg) {
        JLabel l = new JLabel("  " + txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setOpaque(true); l.setBackground(bg);
        l.setPreferredSize(new Dimension(100, 34)); return l;
    }

    // ── GENERAR PDF ────────────────────────────────────────────────
    private void generarPdf() {
        if (casaActual == null) return;
        try {
            Modelo.GeneradorPDF gen = new Modelo.GeneradorPDF(
                casaActual, residenteActual,
                alicuotasActuales, multasActuales,
                arriendosActuales, sedeActuales
            );
            gen.guardarPDF(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al generar el PDF: " + ex.getMessage(),
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
