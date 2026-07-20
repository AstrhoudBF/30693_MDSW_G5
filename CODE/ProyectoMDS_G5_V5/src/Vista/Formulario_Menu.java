package Vista;

import Controlador.controlador_menu;
import javax.swing.JButton;
import java.awt.*;

public class Formulario_Menu extends javax.swing.JFrame implements interfaz_menu {

    public Formulario_Menu() {
        initComponents();
        new controlador_menu(this);
    }

    @Override public void abrirRegistroResidentes() { /* gestionado exclusivamente por controlador_menu */ }
    @Override public void abrirRegistroAlicuotas()  { /* gestionado exclusivamente por controlador_menu */ }

    @Override public JButton getBtnBusqueda()      { return btn_busqueda;    }
    @Override public JButton getButtonResidentes() { return buttonResidentes; }
    @Override public JButton getBtnAlicuotas()     { return btn_alicuotas;   }
    @Override public JButton getBtnArriendos()     { return btn_arriendos;   }
    @Override public JButton getBtnMultas()        { return btn_multas;      }
    @Override public JButton getBtnConsultas()     { return btn_consultas;   }

    @Override public void mostrarMenu() { this.setVisible(true); }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPanel1          = new javax.swing.JPanel();
        jLabel3          = new javax.swing.JLabel();
        jLabel1          = new javax.swing.JLabel();
        buttonResidentes = new javax.swing.JButton();
        jLabel2          = new javax.swing.JLabel();
        btn_busqueda     = new javax.swing.JButton();
        jLabel4          = new javax.swing.JLabel();
        btn_alicuotas    = new javax.swing.JButton();
        jLabel5          = new javax.swing.JLabel();
        btn_arriendos    = new javax.swing.JButton();
        jLabel6          = new javax.swing.JLabel();
        btn_multas       = new javax.swing.JButton();
        jLabel7          = new javax.swing.JLabel();
        btn_consultas    = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menú Principal");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Bienvenida
        jLabel3.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel3.setText("Bienvenido SR. Pablo Chisaguano");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        // Fila 1 — Residentes
        addFila(jLabel1, "Registro de Residentes:", buttonResidentes,
                new java.awt.Color(0,0,0), 52);

        // Fila 2 — Búsqueda
        addFila(jLabel2, "Búsqueda de Residentes:", btn_busqueda,
                new java.awt.Color(0,0,0), 94);

        // Fila 3 — Alícuotas
        addFila(jLabel4, "Registro de Alícuotas:", btn_alicuotas,
                new java.awt.Color(0,0,0), 136);

        // Fila 4 — Arriendos
        addFila(jLabel5, "Registro de Arriendos:", btn_arriendos,
                new java.awt.Color(30,100,180), 178);

        // Fila 5 — Multas
        addFila(jLabel6, "Gestión de Multas:", btn_multas,
                new java.awt.Color(160,30,30), 220);

        // Fila 6 — Consultas (NUEVO)
        addFila(jLabel7, "Consultas por Casa:", btn_consultas,
                new java.awt.Color(30,130,80), 262);

        // Layout
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 304,
                              javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pack();
    }

    private void addFila(javax.swing.JLabel lbl, String texto,
                         javax.swing.JButton btn, java.awt.Color bgBtn, int y) {
        lbl.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        lbl.setText(texto);
        jPanel1.add(lbl, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, y, 225, 22));
        btn.setBackground(bgBtn);
        btn.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new java.awt.Color(255, 255, 255));
        btn.setText("IR");
        jPanel1.add(btn, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, y - 2, 60, -1));
    }

    // Variables declaration
    private javax.swing.JButton btn_alicuotas;
    private javax.swing.JButton btn_arriendos;
    private javax.swing.JButton btn_busqueda;
    private javax.swing.JButton btn_multas;
    private javax.swing.JButton btn_consultas;
    private javax.swing.JButton buttonResidentes;
    private javax.swing.JLabel  jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7;
    private javax.swing.JPanel  jPanel1;
}
