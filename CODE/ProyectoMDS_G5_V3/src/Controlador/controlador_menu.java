package Controlador;

import Vista.interfaz_menu;
import Vista.Formulario_Busqueda;
import Vista.Formulario_Residentes;
import Vista.interfaz_busqueda;
import Vista.interfaz_residentes;

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
        interfaz_residentes vistaR = new Formulario_Residentes();
        controlador_residentes ctrlR = new controlador_residentes(vistaR);
        ctrlR.iniciar();
        
        // CERRAR EL MENÚ ACTUAL
        // Convertimos (casteamos) la vista a JFrame para poder usar dispose()
        // por si no lo tienes declarado en tu interfaz_menu
        ((javax.swing.JFrame) this.vista).dispose();
    }

    public void mostrarBusqueda() {
        interfaz_busqueda vistaB = new Formulario_Busqueda();
        controlador_busqueda ctrlB = new controlador_busqueda(vistaB);
        ctrlB.iniciar();
        
        // CERRAR EL MENÚ ACTUAL
        ((javax.swing.JFrame) this.vista).dispose();
    }

    public void irRegistroAlicuotas() {
        // Asumo que aquí harás lo mismo cuando implementes este módulo
        vista.abrirRegistroAlicuotas();
    }
    
    public void abrirMenu(){
        vista.mostrarMenu();
    }
}