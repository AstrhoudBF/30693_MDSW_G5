package Vista;

import Controlador.controlador_arriendos;
import Controlador.controlador_arriendos_sede;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal que permite elegir el tipo de arriendo.
 * Recibe el JFrame del menú padre para ocultarlo/mostrarlo correctamente.
 */
public class Dialogo_Selector_Arriendo extends JDialog {

    private final JFrame menuPadre;

    public Dialogo_Selector_Arriendo(JFrame padre) {
        super(padre, "Módulo de Arriendos", true);
        this.menuPadre = padre;
        initComponents();
    }

    private void initComponents() {
        setSize(420, 300);
        setLocationRelativeTo(menuPadre);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel pnl = new JPanel(null);
        pnl.setBackground(Color.WHITE);
        getContentPane().add(pnl);

        JLabel lblTit = new JLabel("¿Qué desea gestionar?");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTit.setBounds(20, 18, 380, 28);
        pnl.add(lblTit);

        JLabel lblSub = new JLabel("Seleccione el tipo de arriendo:");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setBounds(20, 48, 380, 20);
        pnl.add(lblSub);

        JButton btnLocales = makeBigButton(
            "🏪  Locales y Parqueaderos",
            "Contratos mensuales para locales comerciales\ny espacios de parqueadero.",
            new Color(220, 240, 255), new Color(30, 80, 160)
        );
        btnLocales.setBounds(20, 82, 375, 60);
        btnLocales.addActionListener(e -> abrirLocalesParqueaderos());
        pnl.add(btnLocales);

        JButton btnSede = makeBigButton(
            "🏛  Sede Social",
            "Reservas por horas o día completo.\nValidación automática de choques de fechas.",
            new Color(255, 240, 210), new Color(140, 80, 0)
        );
        btnSede.setBounds(20, 154, 375, 60);
        btnSede.addActionListener(e -> abrirSedeSocial());
        pnl.add(btnSede);

        JButton btnHist = new JButton("📊  Ver Historial y Totales Recaudados");
        btnHist.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHist.setBackground(new Color(240, 240, 240));
        btnHist.setForeground(new Color(50, 50, 50));
        btnHist.setFocusPainted(false);
        btnHist.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHist.setBounds(20, 226, 375, 35);
        btnHist.addActionListener(e -> new Formulario_Historial_Arriendos().setVisible(true));
        pnl.add(btnHist);
    }

    private void abrirLocalesParqueaderos() {
        dispose();                        // cerrar el diálogo selector
        menuPadre.setVisible(false);      // ocultar el menú
        interfaz_arriendos vista = new Formulario_Arriendos();
        new controlador_arriendos(vista, menuPadre).iniciar();
    }

    private void abrirSedeSocial() {
        dispose();
        menuPadre.setVisible(false);
        interfaz_arriendos_sede vista = new Formulario_Arriendos_Sede();
        new controlador_arriendos_sede(vista, menuPadre).iniciar();
    }

    private JButton makeBigButton(String titulo, String descripcion, Color bg, Color fgTit) {
        JButton b = new JButton();
        b.setLayout(new BorderLayout(8, 2));
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTit.setForeground(fgTit);
        JLabel lDesc = new JLabel("<html>" + descripcion.replace("\n", "<br>") + "</html>");
        lDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lDesc.setForeground(new Color(80, 80, 80));
        JPanel inner = new JPanel(new BorderLayout(0, 2));
        inner.setOpaque(false);
        inner.add(lTit, BorderLayout.NORTH);
        inner.add(lDesc, BorderLayout.CENTER);
        b.add(inner, BorderLayout.CENTER);
        return b;
    }
}
