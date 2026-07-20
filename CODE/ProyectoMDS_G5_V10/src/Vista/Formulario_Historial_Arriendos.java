package Vista;

import Modelo.AlmacenarArriendos;
import Modelo.AlmacenarArriendosSede;
import Modelo.Arriendo;
import Modelo.ArriendoSede;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Pantalla de historial y recaudación de arriendos.
 *
 * Pestañas:
 *  🏪 Locales        — historial completo + resumen por mes/año
 *  🚗 Parqueaderos   — historial completo + resumen por mes/año
 *  🏛 Sede Social    — historial completo + resumen por mes/año
 *  📊 Resumen Global — tarjetas de totales con filtro por mes/año
 */
public class Formulario_Historial_Arriendos extends JFrame {

    private final AlmacenarArriendos     repoArr  = new AlmacenarArriendos();
    private final AlmacenarArriendosSede repoSede = new AlmacenarArriendosSede();

    public Formulario_Historial_Arriendos() {
        setTitle("Historial de Arriendos y Recaudación");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        JLabel lblTit = new JLabel("  Historial de Arriendos y Recaudación");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setOpaque(true);
        lblTit.setBackground(Color.WHITE);
        lblTit.setPreferredSize(new Dimension(1100, 40));
        add(lblTit, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("🏪 Locales",      buildTabArriendo("Local",       new Color(220, 240, 255)));
        tabs.addTab("🚗 Parqueaderos", buildTabArriendo("Parqueadero", new Color(220, 255, 220)));
        tabs.addTab("🏛 Sede Social",  buildTabSede());
        tabs.addTab("📊 Resumen",      buildTabResumen());
        add(tabs, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════════
    // TAB LOCALES / PARQUEADEROS
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTabArriendo(String tipo, Color colorTema) {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(Color.WHITE);

        // Sub-tabs: Historial | Por Mes | Por Año
        JTabbedPane sub = new JTabbedPane(JTabbedPane.TOP);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ArrayList<Arriendo> lista = repoArr.obtenerPorTipo(tipo);
        double total = repoArr.totalRecaudadoPorTipo(tipo);

        sub.addTab("📋 Historial",  buildHistorialArriendo(lista, total, colorTema));
        sub.addTab("📅 Por Mes",    buildResumenMesArriendo(lista, colorTema));
        sub.addTab("📆 Por Año",    buildResumenAnioArriendo(lista, colorTema));

        p.add(sub, BorderLayout.CENTER);
        return p;
    }

    // ── Historial completo de arriendos ───────────────────────────
    private JPanel buildHistorialArriendo(ArrayList<Arriendo> lista, double total, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblTotal = banner(String.format("  Total recaudado (Pagado): $ %.2f  —  %d registro(s)", total, lista.size()), color);
        p.add(lblTotal, BorderLayout.NORTH);

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Nombre Espacio","Arrendatario","Tipo Arr.","Teléfono","Email",
                         "Monto ($)","Período","Estado","Forma Pago","N° Tx","Fecha Pago","Registrado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Arriendo a : lista) {
            String reg = a.getFechaRegistro() != null ? a.getFechaRegistro().format(fmtR) : "—";
            String fp  = a.getFechaPago()     != null ? a.getFechaPago().format(fmtF)     : "—";
            m.addRow(new Object[]{
                ns(a.getNombreEspacio()), a.getNombreArrendatario(),
                a.getTipoArrendatario(), ns(a.getTelefono()), ns(a.getEmail()),
                String.format("%.2f", a.getMontoMensual()),
                ns(a.getMesPeriodo()), a.getEstado(), a.getFormaPago(),
                ns(a.getNumeroTransaccion()).isEmpty()?"—":a.getNumeroTransaccion(),
                fp, reg
            });
        }
        p.add(new JScrollPane(buildTable(m)), BorderLayout.CENTER);
        return p;
    }

    // ── Resumen por mes (Locales/Parqueaderos) ────────────────────
    // El período viene en formato "MES-AÑO" → usamos eso para agrupar
    private JPanel buildResumenMesArriendo(ArrayList<Arriendo> lista, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Agrupar por Período (ya es mes-año)
        Map<String, List<Arriendo>> porMes = new LinkedHashMap<>();
        for (Arriendo a : lista) {
            String k = ns(a.getMesPeriodo()).isEmpty() ? "Sin período" : a.getMesPeriodo();
            porMes.computeIfAbsent(k, x -> new ArrayList<>()).add(a);
        }
        // Ordenar por período
        List<Map.Entry<String, List<Arriendo>>> entradas = new ArrayList<>(porMes.entrySet());
        entradas.sort(Comparator.comparing(Map.Entry::getKey));

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Período","Registros","Total Pagado ($)","Total Pendiente ($)","Total Cancelado ($)"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        double grandTotal = 0;
        for (Map.Entry<String, List<Arriendo>> e : entradas) {
            double pagado    = e.getValue().stream().filter(a -> "Pagado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            double pendiente = e.getValue().stream().filter(a -> "Pendiente".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            double cancelado = e.getValue().stream().filter(a -> "Cancelado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            grandTotal += pagado;
            m.addRow(new Object[]{
                e.getKey(), e.getValue().size(),
                String.format("%.2f", pagado),
                String.format("%.2f", pendiente),
                String.format("%.2f", cancelado)
            });
        }

        JLabel lblTotal = banner(String.format("  Total recaudado (Pagado) en todos los períodos: $ %.2f", grandTotal), color);
        p.add(lblTotal, BorderLayout.NORTH);

        JTable tabla = buildTable(m);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(130);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    // ── Resumen por año (Locales/Parqueaderos) ────────────────────
    private JPanel buildResumenAnioArriendo(ArrayList<Arriendo> lista, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Extraer año del período "MES-YYYY"
        Map<String, List<Arriendo>> porAnio = new LinkedHashMap<>();
        for (Arriendo a : lista) {
            String per = ns(a.getMesPeriodo());
            String anio = "Sin año";
            if (per.contains("-")) anio = per.substring(per.lastIndexOf('-') + 1);
            porAnio.computeIfAbsent(anio, x -> new ArrayList<>()).add(a);
        }
        List<Map.Entry<String, List<Arriendo>>> entradas = new ArrayList<>(porAnio.entrySet());
        entradas.sort(Comparator.comparing(Map.Entry::getKey));

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Año","Registros","Total Pagado ($)","Total Pendiente ($)","Total Cancelado ($)"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        double grandTotal = 0;
        for (Map.Entry<String, List<Arriendo>> e : entradas) {
            double pagado    = e.getValue().stream().filter(a -> "Pagado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            double pendiente = e.getValue().stream().filter(a -> "Pendiente".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            double cancelado = e.getValue().stream().filter(a -> "Cancelado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
            grandTotal += pagado;
            m.addRow(new Object[]{
                e.getKey(), e.getValue().size(),
                String.format("%.2f", pagado),
                String.format("%.2f", pendiente),
                String.format("%.2f", cancelado)
            });
        }

        JLabel lblTotal = banner(String.format("  Total recaudado (Pagado) global: $ %.2f", grandTotal), color);
        p.add(lblTotal, BorderLayout.NORTH);

        JTable tabla = buildTable(m);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 5; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(130);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    // TAB SEDE SOCIAL
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTabSede() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);

        JTabbedPane sub = new JTabbedPane(JTabbedPane.TOP);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        ArrayList<ArriendoSede> lista = repoSede.obtenerTodas();
        double total = repoSede.totalRecaudado();
        Color colorSede = new Color(255, 240, 210);

        sub.addTab("📋 Historial", buildHistorialSede(lista, total, colorSede));
        sub.addTab("📅 Por Mes",   buildResumenMesSede(lista, colorSede));
        sub.addTab("📆 Por Año",   buildResumenAnioSede(lista, colorSede));

        p.add(sub, BorderLayout.CENTER);
        return p;
    }

    // ── Historial completo sede ───────────────────────────────────
    private JPanel buildHistorialSede(ArrayList<ArriendoSede> lista, double total, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblTotal = banner(String.format("  Total recaudado (Confirmada/Pagado): $ %.2f  —  %d reserva(s)", total, lista.size()), color);
        p.add(lblTotal, BorderLayout.NORTH);

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Fecha Reserva","Solicitante","Tipo","Casa","Teléfono","Email",
                         "Modalidad","H.Inicio","H.Fin","Monto ($)","Estado",
                         "Forma Pago","N° Tx","Fecha Pago","Motivo","Registrado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (ArriendoSede s : lista) {
            String fr  = s.getFechaReserva()  != null ? s.getFechaReserva().format(fmtF)  : "—";
            String fp  = s.getFechaPago()      != null ? s.getFechaPago().format(fmtF)     : "—";
            String reg = s.getFechaRegistro() != null ? s.getFechaRegistro().format(fmtR) : "—";
            m.addRow(new Object[]{
                fr, s.getNombreSolicitante(), s.getTipoSolicitante(),
                ns(s.getNumeroCasaResidente()).isEmpty()?"—":s.getNumeroCasaResidente(),
                ns(s.getTelefono()), ns(s.getEmail()),
                s.getModalidad(),
                ns(s.getHoraInicio()).isEmpty()?"—":s.getHoraInicio(),
                ns(s.getHoraFin()).isEmpty()?"—":s.getHoraFin(),
                String.format("%.2f", s.getMonto()),
                s.getEstado(), s.getFormaPago(),
                ns(s.getNumeroTransaccion()).isEmpty()?"—":s.getNumeroTransaccion(),
                fp, s.getMotivo(), reg
            });
        }
        p.add(new JScrollPane(buildTable(m)), BorderLayout.CENTER);
        return p;
    }

    // ── Resumen por mes (Sede Social) ─────────────────────────────
    private JPanel buildResumenMesSede(ArrayList<ArriendoSede> lista, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Agrupar por Mes-Año de fechaReserva
        Map<String, List<ArriendoSede>> porMes = new LinkedHashMap<>();
        for (ArriendoSede s : lista) {
            String k = "Sin fecha";
            if (s.getFechaReserva() != null) {
                k = s.getFechaReserva().getMonth().getDisplayName(
                    java.time.format.TextStyle.FULL, new Locale("es")).toUpperCase()
                    + "-" + s.getFechaReserva().getYear();
            }
            porMes.computeIfAbsent(k, x -> new ArrayList<>()).add(s);
        }
        List<Map.Entry<String, List<ArriendoSede>>> entradas = new ArrayList<>(porMes.entrySet());
        entradas.sort(Comparator.comparing(Map.Entry::getKey));

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Mes/Año","Reservas","Total Confirmado ($)","Total Pendiente ($)","Total Cancelado ($)"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        double grandTotal = 0;
        for (Map.Entry<String, List<ArriendoSede>> e : entradas) {
            double confirmado = e.getValue().stream().filter(s -> "Confirmada".equals(s.getEstado()) || "Pagado".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            double pendiente  = e.getValue().stream().filter(s -> "Pendiente".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            double cancelado  = e.getValue().stream().filter(s -> "Cancelada".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            grandTotal += confirmado;
            m.addRow(new Object[]{
                e.getKey(), e.getValue().size(),
                String.format("%.2f", confirmado),
                String.format("%.2f", pendiente),
                String.format("%.2f", cancelado)
            });
        }

        JLabel lblTotal = banner(String.format("  Total recaudado (Confirmado/Pagado) en todos los meses: $ %.2f", grandTotal), color);
        p.add(lblTotal, BorderLayout.NORTH);

        JTable tabla = buildTable(m);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {130, 70, 140, 140, 140};
        for (int i = 0; i < w.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    // ── Resumen por año (Sede Social) ─────────────────────────────
    private JPanel buildResumenAnioSede(ArrayList<ArriendoSede> lista, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Map<String, List<ArriendoSede>> porAnio = new LinkedHashMap<>();
        for (ArriendoSede s : lista) {
            String k = s.getFechaReserva() != null ? String.valueOf(s.getFechaReserva().getYear()) : "Sin año";
            porAnio.computeIfAbsent(k, x -> new ArrayList<>()).add(s);
        }
        List<Map.Entry<String, List<ArriendoSede>>> entradas = new ArrayList<>(porAnio.entrySet());
        entradas.sort(Comparator.comparing(Map.Entry::getKey));

        DefaultTableModel m = new DefaultTableModel(
            new String[]{"Año","Reservas","Total Confirmado ($)","Total Pendiente ($)","Total Cancelado ($)"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        double grandTotal = 0;
        for (Map.Entry<String, List<ArriendoSede>> e : entradas) {
            double confirmado = e.getValue().stream().filter(s -> "Confirmada".equals(s.getEstado()) || "Pagado".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            double pendiente  = e.getValue().stream().filter(s -> "Pendiente".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            double cancelado  = e.getValue().stream().filter(s -> "Cancelada".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
            grandTotal += confirmado;
            m.addRow(new Object[]{
                e.getKey(), e.getValue().size(),
                String.format("%.2f", confirmado),
                String.format("%.2f", pendiente),
                String.format("%.2f", cancelado)
            });
        }

        JLabel lblTotal = banner(String.format("  Total recaudado (Confirmado/Pagado) global: $ %.2f", grandTotal), color);
        p.add(lblTotal, BorderLayout.NORTH);

        JTable tabla = buildTable(m);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 5; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(130);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════════
    // TAB RESUMEN GLOBAL con selector Mes/Año
    // ══════════════════════════════════════════════════════════════
    private JPanel buildTabResumen() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // ── Selector de filtro ─────────────────────────────────────
        JPanel pnlFiltro = new JPanel(null);
        pnlFiltro.setBackground(new Color(247, 247, 247));
        pnlFiltro.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)),
            "Filtrar resumen", 0, 0, new Font("Segoe UI", Font.BOLD, 12)
        ));
        pnlFiltro.setPreferredSize(new Dimension(1060, 70));

        JLabel lblVista = new JLabel("Ver por:");
        lblVista.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVista.setBounds(10, 22, 70, 26);
        pnlFiltro.add(lblVista);

        JComboBox<String> comboVista = new JComboBox<>(new String[]{"Global (todo)", "Por Año", "Por Mes"});
        comboVista.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboVista.setBounds(85, 22, 160, 26);
        pnlFiltro.add(comboVista);

        JLabel lblFiltroVal = new JLabel("Valor:");
        lblFiltroVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFiltroVal.setBounds(260, 22, 55, 26);
        pnlFiltro.add(lblFiltroVal);

        JComboBox<String> comboValor = new JComboBox<>();
        comboValor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboValor.setBounds(320, 22, 180, 26);
        comboValor.setEnabled(false);
        pnlFiltro.add(comboValor);

        JButton btnAplicar = new JButton("Aplicar");
        btnAplicar.setBackground(Color.BLACK);
        btnAplicar.setForeground(Color.WHITE);
        btnAplicar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAplicar.setFocusPainted(false);
        btnAplicar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAplicar.setBounds(515, 22, 100, 26);
        pnlFiltro.add(btnAplicar);

        p.add(pnlFiltro, BorderLayout.NORTH);

        // ── Panel de contenido dinámico ────────────────────────────
        JPanel pnlContenido = new JPanel(new BorderLayout());
        pnlContenido.setBackground(Color.WHITE);
        p.add(pnlContenido, BorderLayout.CENTER);

        // Poblar valores cuando cambia la vista
        ArrayList<Arriendo> todosArr    = repoArr.obtenerTodos();
        ArrayList<ArriendoSede> todosSede = repoSede.obtenerTodas();

        comboVista.addActionListener(e -> {
            String vista = (String) comboVista.getSelectedItem();
            comboValor.removeAllItems();
            if ("Global (todo)".equals(vista)) {
                comboValor.setEnabled(false);
            } else {
                comboValor.setEnabled(true);
                Set<String> vals = new LinkedHashSet<>();
                if ("Por Año".equals(vista)) {
                    for (Arriendo a : todosArr) {
                        String per = ns(a.getMesPeriodo());
                        if (per.contains("-")) vals.add(per.substring(per.lastIndexOf('-') + 1));
                    }
                    for (ArriendoSede s : todosSede) {
                        if (s.getFechaReserva() != null)
                            vals.add(String.valueOf(s.getFechaReserva().getYear()));
                    }
                } else { // Por Mes
                    for (Arriendo a : todosArr) {
                        String per = ns(a.getMesPeriodo());
                        if (!per.isEmpty()) vals.add(per);
                    }
                    for (ArriendoSede s : todosSede) {
                        if (s.getFechaReserva() != null) {
                            String k = s.getFechaReserva().getMonth().getDisplayName(
                                java.time.format.TextStyle.FULL, new Locale("es")).toUpperCase()
                                + "-" + s.getFechaReserva().getYear();
                            vals.add(k);
                        }
                    }
                }
                List<String> sorted = new ArrayList<>(vals);
                Collections.sort(sorted);
                for (String v : sorted) comboValor.addItem(v);
            }
        });

        // Renderizar el resumen
        btnAplicar.addActionListener(e -> {
            String vista  = (String) comboVista.getSelectedItem();
            String filtro = comboValor.isEnabled() ? (String) comboValor.getSelectedItem() : null;
            pnlContenido.removeAll();
            pnlContenido.add(renderResumen(vista, filtro, todosArr, todosSede), BorderLayout.CENTER);
            pnlContenido.revalidate();
            pnlContenido.repaint();
        });

        // Cargar resumen global inicial
        pnlContenido.add(renderResumen("Global (todo)", null, todosArr, todosSede), BorderLayout.CENTER);

        return p;
    }

    private JPanel renderResumen(String vista, String filtro,
                                  ArrayList<Arriendo> todosArr,
                                  ArrayList<ArriendoSede> todosSede) {
        // Filtrar datos
        List<Arriendo> arrFiltrado = todosArr.stream().filter(a -> {
            if ("Global (todo)".equals(vista)) return true;
            String per = ns(a.getMesPeriodo());
            if ("Por Año".equals(vista)) {
                String anio = per.contains("-") ? per.substring(per.lastIndexOf('-')+1) : "";
                return filtro != null && filtro.equals(anio);
            } else { // Por Mes
                return filtro != null && filtro.equals(per);
            }
        }).collect(Collectors.toList());

        List<ArriendoSede> sedeFiltrado = todosSede.stream().filter(s -> {
            if ("Global (todo)".equals(vista)) return true;
            if (s.getFechaReserva() == null) return false;
            if ("Por Año".equals(vista)) return filtro != null && filtro.equals(String.valueOf(s.getFechaReserva().getYear()));
            else {
                String k = s.getFechaReserva().getMonth().getDisplayName(
                    java.time.format.TextStyle.FULL, new Locale("es")).toUpperCase()
                    + "-" + s.getFechaReserva().getYear();
                return filtro != null && filtro.equals(k);
            }
        }).collect(Collectors.toList());

        double totLocal  = arrFiltrado.stream().filter(a -> "Local".equals(a.getTipoEspacio()) && "Pagado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
        double totParq   = arrFiltrado.stream().filter(a -> "Parqueadero".equals(a.getTipoEspacio()) && "Pagado".equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
        double totSede   = sedeFiltrado.stream().filter(s -> "Confirmada".equals(s.getEstado()) || "Pagado".equals(s.getEstado())).mapToDouble(ArriendoSede::getMonto).sum();
        double totGlobal = totLocal + totParq + totSede;

        String subtitulo = "Global (todo)".equals(vista) ? "Todos los períodos"
                : ("Por Año".equals(vista) ? "Año: " + filtro : "Período: " + filtro);

        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel lblSub = new JLabel("Resumen — " + subtitulo);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSub.setBounds(20, 10, 600, 26);
        p.add(lblSub);

        // Tarjetas
        int cx = 20, cy = 50, cw = 200, ch = 110, gap = 20;
        addCard(p, "🏪 Locales",      totLocal,  new Color(220,240,255), cx, cy, cw, ch);
        addCard(p, "🚗 Parqueaderos", totParq,   new Color(220,255,220), cx+cw+gap, cy, cw, ch);
        addCard(p, "🏛 Sede Social",  totSede,   new Color(255,240,210), cx+(cw+gap)*2, cy, cw, ch);
        addCard(p, "💰 Total",        totGlobal, new Color(240,230,255), cx+(cw+gap)*3, cy, cw, ch);

        // Barra proporcional
        if (totGlobal > 0) {
            JLabel lblBar = new JLabel("Distribución:");
            lblBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblBar.setBounds(20, 180, 200, 20);
            p.add(lblBar);
            int barY = 204, barW = 840, xPos = 20;
            double[] vals   = {totLocal, totParq, totSede};
            Color[]  cols   = {new Color(100,160,240), new Color(100,200,130), new Color(240,160,80)};
            String[] noms   = {"Locales","Parqueaderos","Sede Social"};
            for (int i = 0; i < 3; i++) {
                int w = (int)(barW * vals[i] / totGlobal);
                if (w > 0) {
                    JPanel bar = new JPanel(new BorderLayout());
                    bar.setBackground(cols[i]);
                    JLabel lbl = new JLabel(String.format(" %s  %.0f%%", noms[i], vals[i]/totGlobal*100), SwingConstants.CENTER);
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setForeground(Color.WHITE);
                    bar.add(lbl); bar.setBounds(xPos, barY, w, 30); p.add(bar);
                    xPos += w;
                }
            }
        } else {
            JLabel nd = new JLabel("Sin recaudación para el período seleccionado.");
            nd.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            nd.setBounds(20, 190, 500, 25);
            p.add(nd);
        }

        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────
    private JLabel banner(String txt, Color bg) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setOpaque(true); l.setBackground(bg);
        l.setPreferredSize(new Dimension(100, 34)); return l;
    }
    private JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(21); t.setEnabled(false);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(Color.WHITE); return t;
    }
    private void addCard(JPanel parent, String titulo, double monto, Color bg, int x, int y, int w, int h) {
        JPanel card = new JPanel(null);
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));
        card.setBounds(x, y, w, h);
        JLabel lt = new JLabel(titulo, SwingConstants.CENTER);
        lt.setFont(new Font("Segoe UI", Font.BOLD, 13)); lt.setBounds(0, 10, w, 22); card.add(lt);
        JLabel lm = new JLabel(String.format("$ %.2f", monto), SwingConstants.CENTER);
        lm.setFont(new Font("Segoe UI", Font.BOLD, 20)); lm.setBounds(0, 42, w, 30); card.add(lm);
        JLabel ll = new JLabel("recaudado", SwingConstants.CENTER);
        ll.setFont(new Font("Segoe UI", Font.PLAIN, 11)); ll.setBounds(0, 76, w, 18); card.add(ll);
        parent.add(card);
    }
    private String ns(String s) { return s != null ? s : ""; }
}
