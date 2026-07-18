package Controlador;

import Vista.interfaz_menu;
import Vista.Formulario_Busqueda;
import Vista.interfaz_busqueda;

public class controlador_menu {

    private interfaz_menu vista;

    public controlador_menu(interfaz_menu vista) {
        this.vista = vista;

        // Lambda botón registro de residentes
        this.vista.getButtonResidentes().addActionListener(e -> irRegistroResidentes());

        // Lambda botón búsqueda
        this.vista.getBtnBusqueda().addActionListener(e -> mostrarBusqueda());
    }

    public void irRegistroResidentes() {
        vista.abrirRegistroResidentes();
    }

    public void mostrarBusqueda() {
        interfaz_busqueda vistaB = new Formulario_Busqueda();
        controlador_busqueda ctrlB = new controlador_busqueda(vistaB);
        ctrlB.iniciar();
    }

    public void irRegistroAlicuotas() {
        vista.abrirRegistroAlicuotas();
    }
}