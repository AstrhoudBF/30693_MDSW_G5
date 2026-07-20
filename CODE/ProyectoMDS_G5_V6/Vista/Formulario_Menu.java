package Vista;

import Controlador.controlador_menu;
import javax.swing.JButton;
import java.awt.*;

public class Formulario_Menu extends javax.swing.JFrame implements interfaz_menu {

    public Formulario_Menu() {
        initComponents();
        new controlador_menu(this);
    }

    @Override public void abrirRegistroResidentes() { new Formulario_Residentes().setVisible(true); }
    @Override public void abrirRegistroAlicuotas()  { /* delegado al controlador */ }

    @Override public JButton getBtnBusqueda()      { return btn_busqueda;    }
    @Override public JButton getButtonResidentes() { return buttonResidentes; }
    @Override public JButton getBtnAlicuotas()     { return btn_alicuotas;   }
    @Override public JButton getBtnArriendos()     { return btn_arriendos;   }
    @Override public JButton getBtnMultas()        { return btn_multas;      }

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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menú Principal");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Bienvenida
        jLabel3.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel3.setText("Bienvenido SR. Pablo Chisaguano");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        // Fila 1 — Residentes
        jLabel1.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel1.setText("Registro de Residentes:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 52, 215, 22));
        buttonResidentes.setBackground(new java.awt.Color(0, 0, 0));
        buttonResidentes.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        buttonResidentes.setForeground(new java.awt.Color(255, 255, 255));
        buttonResidentes.setText("IR");
        jPanel1.add(buttonResidentes, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 50, 60, -1));

        // Fila 2 — Búsqueda
        jLabel2.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel2.setText("Búsqueda de Residentes:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 94, 215, 22));
        btn_busqueda.setBackground(new java.awt.Color(0, 0, 0));
        btn_busqueda.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        btn_busqueda.setForeground(new java.awt.Color(255, 255, 255));
        btn_busqueda.setText("IR");
        jPanel1.add(btn_busqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 92, 60, -1));

        // Fila 3 — Alícuotas
        jLabel4.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel4.setText("Registro de Alícuotas:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 136, 215, 22));
        btn_alicuotas.setBackground(new java.awt.Color(0, 0, 0));
        btn_alicuotas.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        btn_alicuotas.setForeground(new java.awt.Color(255, 255, 255));
        btn_alicuotas.setText("IR");
        jPanel1.add(btn_alicuotas, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 134, 60, -1));

        // Fila 4 — Arriendos
        jLabel5.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel5.setText("Registro de Arriendos:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 178, 215, 22));
        btn_arriendos.setBackground(new java.awt.Color(30, 100, 180));
        btn_arriendos.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        btn_arriendos.setForeground(new java.awt.Color(255, 255, 255));
        btn_arriendos.setText("IR");
        jPanel1.add(btn_arriendos, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 176, 60, -1));

        // Fila 5 — Multas (NUEVO)
        jLabel6.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        jLabel6.setText("Gestión de Multas:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 215, 22));
        btn_multas.setBackground(new java.awt.Color(160, 30, 30));
        btn_multas.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        btn_multas.setForeground(new java.awt.Color(255, 255, 255));
        btn_multas.setText("IR");
        jPanel1.add(btn_multas, new org.netbeans.lib.awtextra.AbsoluteConstraints(255, 218, 60, -1));

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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pack();
    }

    // Variables declaration
    private javax.swing.JButton btn_alicuotas;
    private javax.swing.JButton btn_arriendos;
    private javax.swing.JButton btn_busqueda;
    private javax.swing.JButton btn_multas;
    private javax.swing.JButton buttonResidentes;
    private javax.swing.JLabel  jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6;
    private javax.swing.JPanel  jPanel1;
}
