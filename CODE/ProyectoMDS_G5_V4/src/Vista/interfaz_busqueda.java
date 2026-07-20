package Vista;

import Modelo.Residentes;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.ArrayList;

public interface interfaz_busqueda {
    JComboBox  getComboCriterio();
    JTextField getTxtBusqueda();
    String     getStrBusqueda();

    JButton getBtnBusqueda();
    JButton getBtnRegreso();
    JButton getBtnModificar();
    JButton getBtnEliminar();

    /** Retorna la cédula del residente actualmente seleccionado en la tabla, o null si ninguno. */
    String getCedulaSeleccionada();

    void mostrarResultados(ArrayList<Residentes> lista);
    void mostrarMensaje(String msg);
    void limpiarTabla();

    void setVisible();
    void dispose();
}
