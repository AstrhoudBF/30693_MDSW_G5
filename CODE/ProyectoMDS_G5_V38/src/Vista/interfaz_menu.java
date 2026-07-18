package Vista;

import javax.swing.JButton;

public interface interfaz_menu {
    void abrirRegistroResidentes();
    void abrirRegistroAlicuotas();
    void mostrarMenu();
    void cerrarVentana();
    JButton getBtnBusqueda();
    JButton getButtonResidentes();
    JButton getBtnAlicuotas();
    JButton getBtnArriendos();
    JButton getBtnMultas();
    JButton getBtnConsultas();
}
