
package Vista;
import javax.swing.JOptionPane;
import Controlador.controlador_menu;
import javax.swing.JButton;

public class Formulario_Menu extends javax.swing.JFrame implements interfaz_menu {
    
    private Controlador.controlador_menu controlador;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Formulario_Menu.class.getName());
    
    public Formulario_Menu() {
        initComponents();
        controlador = new Controlador.controlador_menu(this);
    }
    
   @Override
    public void abrirRegistroResidentes() {
        new Formulario_Residentes().setVisible(true);
    }

    @Override
    public void abrirRegistroAlicuotas() {
        JOptionPane.showMessageDialog(this, "Módulo de Alícuotas en construcción.");
    }

    @Override
    public JButton getBtnBusqueda()      { return btn_busqueda;      }

    @Override
    public JButton getButtonResidentes() { return buttonResidentes;  }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        buttonResidentes = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btn_busqueda = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Registro de Residentes: ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 170, 20));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Busqueda de Residentes:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, -1, -1));

        buttonResidentes.setBackground(new java.awt.Color(0, 0, 0));
        buttonResidentes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonResidentes.setForeground(new java.awt.Color(255, 255, 255));
        buttonResidentes.setText("IR");
        buttonResidentes.addActionListener(this::buttonResidentesActionPerformed);
        jPanel1.add(buttonResidentes, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Bienvenido SR. Pablo Chisaguano");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        btn_busqueda.setBackground(new java.awt.Color(0, 0, 0));
        btn_busqueda.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_busqueda.setForeground(new java.awt.Color(255, 255, 255));
        btn_busqueda.setText("IR");
        btn_busqueda.addActionListener(this::btn_busquedaActionPerformed);
        jPanel1.add(btn_busqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonResidentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonResidentesActionPerformed
    }//GEN-LAST:event_buttonResidentesActionPerformed

    private void btn_busquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_busquedaActionPerformed
        
    }//GEN-LAST:event_btn_busquedaActionPerformed

   
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
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
        java.awt.EventQueue.invokeLater(() -> new Formulario_Menu().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_busqueda;
    private javax.swing.JButton buttonResidentes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables


}
