package Vista;

import Controlador.controlador_residentes;
import Modelo.Residentes;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.JButton;

public class Formulario_Residentes extends javax.swing.JFrame implements interfaz_residentes {
    
   private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Formulario_Residentes.class.getName());
    private controlador_residentes controlador;
    private DefaultTableModel modeloTabla;

    public Formulario_Residentes() {
        initComponents();
        inicializarTabla();
        controlador = new controlador_residentes(this);
    }
    
   private void inicializarTabla() {
    modeloTabla = new DefaultTableModel(
        new String[]{
            "Nombres", "Apellidos", "Cédula", "Tel. Móvil", "Tel. Convencional",
            "N° Vivienda", "Dirección", "Mascotas", "Vehículos",
            "N° Personas", "Nombres y Apellidos Residentes"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    table_residentes.setModel(modeloTabla);
    table_residentes.getTableHeader().setBackground(java.awt.Color.WHITE);
    table_residentes.getTableHeader().setForeground(java.awt.Color.BLACK);
}

    // ── Getters ──────────────────────────────────────────────────
    @Override public String getNombres()             { return txt_nombresp.getText(); }
    @Override public String getApellidos()           { return txt_apellidosp.getText(); }
    @Override public String getCedula()              { return txt_cedula.getText(); }
    @Override public String getTelefonoMovil()       { return txt_telefonom.getText(); }
    @Override public String getTelefonoConvencional(){ return txt_telefonoc.getText(); }
    @Override public String getNumeroVivienda()      { return (String) box_vivienda.getSelectedItem(); }
    @Override public String getDireccion()           { return txt_direccion.getText(); }
    @Override public boolean getTieneMascotas()      { return jRadioButton1.isSelected(); }
    @Override public String getNumeroVehiculos()     { return txt_vehiculos.getText(); }
    @Override public String getNumeroPersonas()      { return txt_numeror.getText(); }

    @Override
    public String getNombresYApellidosResidentes() {
        return txt_nombresr.getText(); // ← nombre real del JTextField en NetBeans
    }

    @Override
    public JButton getBtnGuardar()   { return BtnGuardar1;   }

    @Override
    public JButton getBtnRegresar()  { return btn_regresar;  }

    // ── Acciones ─────────────────────────────────────────────────
    @Override
public void actualizarTabla(ArrayList<Residentes> lista) {
    modeloTabla.setRowCount(0);
    for (Residentes r : lista) {
        modeloTabla.addRow(new Object[]{
            r.getNombres(),
            r.getApellidos(),
            r.getCedula(),
            r.getTelefonoMovil(),
            r.getTelefonoConvencional(),
            r.getNumeroVivienda(),
            r.getDireccion(),
            r.isTieneMascotas() ? "Sí" : "No",
            r.getNumeroVehiculos(),
            r.getNumeroPersonas(),
            r.getNombresResidentes() + " " + r.getApellidosResidentes()
        });
    }
}

    @Override
    public void limpiarCampos() {
        txt_nombresp.setText("");
        txt_apellidosp.setText("");
        txt_cedula.setText("");
        txt_telefonom.setText("");
        txt_telefonoc.setText("");
        txt_direccion.setText("");
        txt_vehiculos.setText("");
        txt_numeror.setText("");
        txt_nombresr.setText("");
        box_vivienda.setSelectedIndex(0);
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    @Override
    public void dispose() {
        this.setVisible(false);
    }

    
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_nombresr = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_telefonoc = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        box_vivienda = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        txt_vehiculos = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_nombresp = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txt_apellidosp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txt_cedula = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txt_telefonom = new javax.swing.JTextField();
        btn_regresar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txt_direccion = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txt_numeror = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_residentes = new javax.swing.JTable();
        BtnGuardar1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Registro de Residentes");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 10, 210, 20));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Nombres y Apellidos: ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, -1, -1));
        jPanel1.add(txt_nombresr, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 310, 850, 50));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Datos propietario: ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 140, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Teléfono convencional: ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 110, 170, 20));
        jPanel1.add(txt_telefonoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 110, 140, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Número de vivienda: ");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, -1, -1));

        box_vivienda.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        jPanel1.add(box_vivienda, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 150, -1, -1));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setText("Dirección: ");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 150, 80, -1));
        jPanel1.add(txt_vehiculos, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 190, 80, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("¿Tiene mascotas? ");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 140, 20));

        jRadioButton1.setText("Si");
        jPanel1.add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 190, -1, -1));

        jRadioButton2.setText("No");
        jPanel1.add(jRadioButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 190, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel10.setText("Datos residentes: ");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel11.setText("Nombres: ");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));
        jPanel1.add(txt_nombresp, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 270, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Apellidos: ");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 70, -1, -1));
        jPanel1.add(txt_apellidosp, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 70, 280, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Cédula: ");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 60, 20));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 60, 20));
        jPanel1.add(txt_cedula, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 270, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 310, 70, 20));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Teléfono movil: ");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 110, 110, 20));
        jPanel1.add(txt_telefonom, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 110, 140, -1));

        btn_regresar.setBackground(new java.awt.Color(0, 0, 0));
        btn_regresar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_regresar.setForeground(new java.awt.Color(255, 255, 255));
        btn_regresar.setText("REGRESAR");
        btn_regresar.addActionListener(this::btn_regresarActionPerformed);
        jPanel1.add(btn_regresar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 260, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Número de Vehiculos: ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 190, 160, -1));
        jPanel1.add(txt_direccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 150, 280, -1));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel17.setText("Numero de personas que viven con el propietario: ");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, -1, -1));
        jPanel1.add(txt_numeror, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 80, -1));

        table_residentes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(table_residentes);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 410, 1210, 330));

        BtnGuardar1.setBackground(new java.awt.Color(0, 0, 0));
        BtnGuardar1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        BtnGuardar1.setForeground(new java.awt.Color(255, 255, 255));
        BtnGuardar1.setText("GUARDAR");
        BtnGuardar1.addActionListener(this::BtnGuardar1ActionPerformed);
        jPanel1.add(BtnGuardar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 190, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_regresarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_regresarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_regresarActionPerformed

    private void BtnGuardar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnGuardar1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnGuardar1ActionPerformed

    /**
     * @param args the command line arguments
     */
    //public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
       /* try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
     //   java.awt.EventQueue.invokeLater(() -> new Formulario_Residentes().setVisible(true));
   // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton BtnGuardar1;
    public javax.swing.JComboBox<String> box_vivienda;
    public javax.swing.JButton btn_regresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JRadioButton jRadioButton1;
    public javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable table_residentes;
    public javax.swing.JTextField txt_apellidosp;
    public javax.swing.JTextField txt_cedula;
    public javax.swing.JTextField txt_direccion;
    public javax.swing.JTextField txt_nombresp;
    public javax.swing.JTextField txt_nombresr;
    private javax.swing.JTextField txt_numeror;
    public javax.swing.JTextField txt_telefonoc;
    public javax.swing.JTextField txt_telefonom;
    public javax.swing.JTextField txt_vehiculos;
    // End of variables declaration//GEN-END:variables
}
