package Controlador;

import Vista.Dialogo_Selector_Arriendo;
import Vista.Formulario_Alicuotas;
import Vista.Formulario_Busqueda;
import Vista.Formulario_Multas;
import Vista.Formulario_Residentes;
import Vista.interfaz_alicuotas;
import Vista.interfaz_busqueda;
import Vista.interfaz_menu;
import Vista.interfaz_multas;
import Vista.interfaz_residentes;
import javax.swing.JFrame;

public class controlador_menu {

    private final interfaz_menu vista;

    public controlador_menu(interfaz_menu vista) {
        this.vista = vista;
        this.vista.getButtonResidentes().addActionListener(e -> irRegistroResidentes());
        this.vista.getBtnBusqueda().addActionListener(e      -> mostrarBusqueda());
        this.vista.getBtnAlicuotas().addActionListener(e     -> irRegistroAlicuotas());
        this.vista.getBtnArriendos().addActionListener(e     -> irArriendos());
        this.vista.getBtnMultas().addActionListener(e        -> irMultas());
    }

    private JFrame menuFrame() {
        return (JFrame) this.vista;
    }

    public void irRegistroResidentes() {
        menuFrame().setVisible(false);
        interfaz_residentes vistaR = new Formulario_Residentes();
        new controlador_residentes(vistaR, menuFrame()).iniciar();
    }

    public void mostrarBusqueda() {
        menuFrame().setVisible(false);
        interfaz_busqueda vistaB = new Formulario_Busqueda();
        new controlador_busqueda(vistaB, menuFrame()).iniciar();
    }

    public void irRegistroAlicuotas() {
        menuFrame().setVisible(false);
        interfaz_alicuotas vistaA = new Formulario_Alicuotas();
        new controlador_alicuotas(vistaA, menuFrame()).iniciar();
    }

    public void irArriendos() {
        // Diálogo modal: el menú no se oculta hasta que el diálogo decida
        Dialogo_Selector_Arriendo dlg = new Dialogo_Selector_Arriendo(menuFrame());
        dlg.setVisible(true);
    }

    public void irMultas() {
        menuFrame().setVisible(false);
        interfaz_multas vistaM = new Formulario_Multas();
        new controlador_multas(vistaM, menuFrame()).iniciar();
    }

    public void abrirMenu() {
        vista.mostrarMenu();
    }
}
