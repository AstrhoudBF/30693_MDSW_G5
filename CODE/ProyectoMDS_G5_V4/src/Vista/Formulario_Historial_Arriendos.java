package Vista;

import Modelo.AlmacenarArriendos;
import Modelo.AlmacenarArriendosSede;
import Modelo.Arriendo;
import Modelo.ArriendoSede;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Pantalla de resumen global:
 *  - Tab "Locales"       → historial + total recaudado
 *  - Tab "Parqueaderos"  → historial + total recaudado
 *  - Tab "Sede Social"   → historial + total recaudado
 *  - Tab "Resumen"       → tarjetas con totales de los tres tipos
 */
public class Formulario_Historial_Arriendos extends JFrame {

    private final AlmacenarArriendos     repoArr  = new AlmacenarArriendos();
    private final AlmacenarArriendosSede repoSede = new AlmacenarArriendosSede();

    public Formulario_Historial_Arriendos() {
        setTitle("Historial de Arriendos y Totales Recaudados");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // ── Header ────────────────────────────────────────────────
        JLabel lblTit = new JLabel("  Historial de Arriendos y Recaudación");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblTit.setOpaque(true);
        lblTit.setBackground(Color.WHITE);
        lblTit.setPreferredSize(new Dimension(900, 40));
        add(lblTit, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabs.addTab("🏪 Locales",       buildTabArriendo("Local"));
        tabs.addTab("🚗 Parqueaderos",  buildTabArriendo("Parqueadero"));
        tabs.addTab("🏛 Sede Social",   buildTabSede());
        tabs.addTab("📊 Resumen",       buildTabResumen());

        add(tabs, BorderLayout.CENTER);
    }

    // ── Tab para Local o Parqueadero ──────────────────────────────
    private JPanel buildTabArriendo(String tipo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<Arriendo> lista = repoArr.obtenerPorTipo(tipo);
        double total = repoArr.totalRecaudadoPorTipo(tipo);

        // Banner total
        JLabel lblTotal = new JLabel(
            String.format("  Total recaudado (%s): $ %.2f", tipo, total)
        );
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setOpaque(true);
        lblTotal.setBackground(tipo.equals("Local")
            ? new Color(220, 240, 255)
            : new Color(220, 255, 220));
        lblTotal.setPreferredSize(new Dimension(100, 36));
        p.add(lblTotal, BorderLayout.NORTH);

        // Tabla
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Espacio", "Arrendatario", "Tipo Arr.", "Contacto",
                         "Monto ($)", "Período", "Estado", "Forma Pago", "Fecha Registro"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Arriendo a : lista) {
            String fecha = a.getFechaRegistro() != null ? a.getFechaRegistro().format(fmt) : "—";
            model.addRow(new Object[]{
                a.getNumeroEspacio(), a.getNombreArrendatario(),
                a.getTipoArrendatario(), a.getContacto(),
                String.format("%.2f", a.getMontoMensual()),
                a.getMesPeriodo(), a.getEstado(), a.getFormaPago(), fecha
            });
        }

        JTable tabla = buildTable(model);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Footer: count
        JLabel lblCount = new JLabel(String.format("  %d registro(s)", lista.size()));
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lblCount, BorderLayout.SOUTH);
        return p;
    }

    // ── Tab sede social ───────────────────────────────────────────
    private JPanel buildTabSede() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ArrayList<ArriendoSede> lista = repoSede.obtenerTodas();
        double total = repoSede.totalRecaudado();

        JLabel lblTotal = new JLabel(
            String.format("  Total recaudado (Sede Social): $ %.2f", total)
        );
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setOpaque(true);
        lblTotal.setBackground(new Color(255, 240, 210));
        lblTotal.setPreferredSize(new Dimension(100, 36));
        p.add(lblTotal, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Fecha Reserva", "Solicitante", "Tipo", "Contacto",
                         "Modalidad", "H.Inicio", "H.Fin", "Monto ($)",
                         "Estado", "Forma Pago", "Motivo", "Registrado"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        DateTimeFormatter fmtF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (ArriendoSede s : lista) {
            String fr  = s.getFechaReserva()  != null ? s.getFechaReserva().format(fmtF)  : "—";
            String reg = s.getFechaRegistro() != null ? s.getFechaRegistro().format(fmtR) : "—";
            model.addRow(new Object[]{
                fr, s.getNombreSolicitante(), s.getTipoSolicitante(), s.getContacto(),
                s.getModalidad(),
                s.getHoraInicio() != null && !s.getHoraInicio().isEmpty() ? s.getHoraInicio() : "—",
                s.getHoraFin()    != null && !s.getHoraFin().isEmpty()    ? s.getHoraFin()    : "—",
                String.format("%.2f", s.getMonto()),
                s.getEstado(), s.getFormaPago(), s.getMotivo(), reg
            });
        }

        JTable tabla = buildTable(model);
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JLabel lblCount = new JLabel(String.format("  %d reserva(s)", lista.size()));
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lblCount, BorderLayout.SOUTH);
        return p;
    }

    // ── Tab resumen con tarjetas ──────────────────────────────────
    private JPanel buildTabResumen() {
        double totLocal  = repoArr.totalRecaudadoPorTipo("Local");
        double totParq   = repoArr.totalRecaudadoPorTipo("Parqueadero");
        double totSede   = repoSede.totalRecaudado();
        double totGlobal = totLocal + totParq + totSede;

        JPanel p = new JPanel(null);
        p.setBackground(Color.WHITE);

        JLabel lblRes = new JLabel("Resumen de Recaudación");
        lblRes.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRes.setBounds(30, 20, 350, 28);
        p.add(lblRes);

        int cardX = 30, cardY = 70, cardW = 190, cardH = 110, gap = 30;
        addCard(p, "🏪 Locales",      totLocal,  new Color(220,240,255), cardX, cardY, cardW, cardH);
        addCard(p, "🚗 Parqueaderos", totParq,   new Color(220,255,220), cardX + cardW + gap, cardY, cardW, cardH);
        addCard(p, "🏛 Sede Social",  totSede,   new Color(255,240,210), cardX + (cardW+gap)*2, cardY, cardW, cardH);

        // Total global
        JPanel cardGlobal = buildCard("💰 Total Global", totGlobal, new Color(240,230,255));
        cardGlobal.setBounds(cardX + (cardW+gap)*3, cardY, cardW, cardH);
        p.add(cardGlobal);

        // Barra proporcional
        JLabel lblBarra = new JLabel("Distribución porcentual:");
        lblBarra.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBarra.setBounds(30, 220, 300, 22);
        p.add(lblBarra);

        if (totGlobal > 0) {
            int barTotal = 800;
            int barY = 248;
            int xPos = 30;

            int[] anchos = {
                (int)(barTotal * totLocal  / totGlobal),
                (int)(barTotal * totParq   / totGlobal),
                (int)(barTotal * totSede   / totGlobal)
            };
            Color[] colores = {
                new Color(100, 160, 240),
                new Color(100, 200, 130),
                new Color(240, 160, 80)
            };
            String[] nombres = {"Locales", "Parqueaderos", "Sede Social"};

            for (int i = 0; i < 3; i++) {
                if (anchos[i] > 0) {
                    JPanel barra = new JPanel();
                    barra.setBackground(colores[i]);
                    barra.setLayout(new BorderLayout());
                    JLabel lbl = new JLabel(
                        String.format(" %s %.0f%%", nombres[i], (totGlobal > 0 ? (i==0?totLocal:i==1?totParq:totSede)/totGlobal*100 : 0)),
                        SwingConstants.CENTER
                    );
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    lbl.setForeground(Color.WHITE);
                    barra.add(lbl, BorderLayout.CENTER);
                    barra.setBounds(xPos, barY, anchos[i], 32);
                    p.add(barra);
                    xPos += anchos[i];
                }
            }
        } else {
            JLabel noData = new JLabel("Sin recaudación registrada.");
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            noData.setBounds(30, 248, 400, 25);
            p.add(noData);
        }

        return p;
    }

    private void addCard(JPanel parent, String titulo, double monto,
                         Color bg, int x, int y, int w, int h) {
        JPanel card = buildCard(titulo, monto, bg);
        card.setBounds(x, y, w, h);
        parent.add(card);
    }

    private JPanel buildCard(String titulo, double monto, Color bg) {
        JPanel card = new JPanel(null);
        card.setBackground(bg);
        card.setBorder(BorderFactory.createLineBorder(bg.darker(), 1));

        JLabel lTit = new JLabel(titulo, SwingConstants.CENTER);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTit.setBounds(0, 12, 190, 22);
        card.add(lTit);

        JLabel lMonto = new JLabel(String.format("$ %.2f", monto), SwingConstants.CENTER);
        lMonto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lMonto.setBounds(0, 45, 190, 30);
        card.add(lMonto);

        JLabel lLbl = new JLabel("recaudado", SwingConstants.CENTER);
        lLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lLbl.setBounds(0, 78, 190, 18);
        card.add(lLbl);
        return card;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setRowHeight(21);
        t.setEnabled(false);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        t.getTableHeader().setBackground(Color.WHITE);
        return t;
    }
}
