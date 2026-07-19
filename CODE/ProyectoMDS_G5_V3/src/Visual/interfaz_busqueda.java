package Vista;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public interface interfaz_busqueda {
    JComboBox getComboCriterio();
    JTextField getTxtBusqueda();
    
    String getStrBusqueda();
    
    JButton getBtnBusqueda();
    JButton getBtnRegreso();
    void setVisible();
    void dispose();
}