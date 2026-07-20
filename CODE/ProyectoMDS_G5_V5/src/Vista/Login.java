package Vista;

import Controlador.controlador_login;
import Modelo.login;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Login extends javax.swing.JFrame implements interfaz_login {

    private controlador_login controlador;
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(Login.class.getName());

    public Login() {
        initComponents();   // primero NetBeans
        
        /*login modelo = new login("pablochisaguano1", "pablo1234");
        controlador = new controlador_login(modelo, this);

        // Eliminar listeners previos del botón antes de agregar el nuevo
        for (java.awt.event.ActionListener al : jButton1.getActionListeners()) {
            jButton1.removeActionListener(al);
        }

        jButton1.addActionListener(e -> {
            if (controlador.validarLogin()) {
                new Vista.Formulario_Menu().setVisible(true);
                this.setVisible(false);
            }
        });

        setVisible(true);*/
    }

    @Override
    public String getUsuario() {
        return txt_usuario.getText();
    }

    @Override
    public String getContrasena() {
        return new String(txt_password.getPassword());
    }

    @Override
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
    
    @Override
    public void iniciar() {
        this.setVisible(true);
    }

    @Override
    public void bloquearBoton(int segundos) {
        JOptionPane.showMessageDialog(this,
            "Demasiados intentos fallidos. Espere " + segundos + " segundos.",
            "Cuenta bloqueada", JOptionPane.ERROR_MESSAGE);

        jButton1.setEnabled(false);

        Timer timer = new Timer(1000, null);
        final int[] restante = {segundos};

        timer.addActionListener(e -> {
            restante[0]--;
            jButton1.setText("Espere " + restante[0] + "s");
            if (restante[0] <= 0) {
                timer.stop();
                jButton1.setEnabled(true);
                jButton1.setText("INGRESAR");
            }
        });

        timer.start();
    }

    @Override
    public JButton getBtnAcc() {
        //return btn_temporal;
        return jButton1;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_usuario = new javax.swing.JTextField();
        txt_password = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        btn_temporal = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("SISTEMA ADMINISTRATIVO");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 200, 30));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Vista/i2.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 50, 420, 130));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("CONJUNTO RESIDENCIAL \"FORTÍN DEL VALLE\"");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("BIENVENIDO");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 170, -1, -1));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("USUARIO: ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("CONTRASEÑA: ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 270, -1, -1));

        txt_usuario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(txt_usuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 260, 30));

        txt_password.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txt_password.addActionListener(this::txt_passwordActionPerformed);
        jPanel1.add(txt_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, 260, 30));

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("INGRESAR");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        // NOTA: el listener lo agrega controlador_login, NO aquí
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 350, 130, -1));

        btn_temporal.setText("TEMPORAL");
        jPanel1.add(btn_temporal, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 350, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txt_passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_passwordActionPerformed
      
    }//GEN-LAST:event_txt_passwordActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      
    }//GEN-LAST:event_jButton1ActionPerformed

   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_temporal;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JPasswordField txt_password;
    public javax.swing.JTextField txt_usuario;
    // End of variables declaration//GEN-END:variables


}
