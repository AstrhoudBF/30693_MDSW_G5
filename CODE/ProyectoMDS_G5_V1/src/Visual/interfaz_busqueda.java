
package Vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;



public interface interfaz_busqueda {
    
    void setVisible();
    void dispose();
    JComboBox getComboCriterio();
    JTextField getTxtBusqueda();
    JButton getBtnBusqueda(); 
    JButton getBtnRegreso();
}
