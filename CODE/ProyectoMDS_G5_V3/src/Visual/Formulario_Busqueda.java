
package Vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class Formulario_Busqueda extends javax.swing.JFrame implements interfaz_busqueda{
    
    
    public Formulario_Busqueda() {
        initComponents();
    }

    @Override public JComboBox getComboCriterio(){ 
        return cmbx_criterio;   
    }
    
    @Override public JTextField getTxtBusqueda(){ 
        return txt_datoBusqueda; 
    }
    
    @Override public JButton getBtnBusqueda(){ return btn_buscar;       
    }
    
    @Override public JButton getBtnRegreso(){ 
        return btn_regreso;      
    }

    @Override
    public void setVisible(){ 
        this.setVisible(true); 
    }

    @Override
    public void dispose(){ 
        super.dispose();
    }
   
    @Override
    public String getStrBusqueda() {
        return this.txt_datoBusqueda.getText().trim();
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cmbx_criterio = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txt_datoBusqueda = new javax.swing.JTextField();
        btn_buscar = new javax.swing.JButton();
        btn_regreso = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        jLabel3.setText("jLabel3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbx_criterio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        cmbx_criterio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nombre", "Cedula", "N° Casa" }));
        jPanel1.add(cmbx_criterio, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 90, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Seleccione el criterio de busqueda:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(56, 89, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Ingrese los datos de la busqueda:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, -1));
        jPanel1.add(txt_datoBusqueda, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 240, -1));

        btn_buscar.setBackground(new java.awt.Color(0, 0, 0));
        btn_buscar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_buscar.setForeground(new java.awt.Color(255, 255, 255));
        btn_buscar.setText("BUSCAR RESIDENTE");
        btn_buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_buscarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_buscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, -1, -1));

        btn_regreso.setBackground(new java.awt.Color(0, 0, 0));
        btn_regreso.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btn_regreso.setForeground(new java.awt.Color(255, 255, 255));
        btn_regreso.setText("REGRESAR");
        jPanel1.add(btn_regreso, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 200, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Busqueda de Residentes");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_buscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btn_buscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_buscar;
    private javax.swing.JButton btn_regreso;
    private javax.swing.JComboBox<String> cmbx_criterio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txt_datoBusqueda;
    // End of variables declaration//GEN-END:variables





}
