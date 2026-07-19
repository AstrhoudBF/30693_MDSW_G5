package Controlador;

import Vista.interfaz_busqueda;
import Modelo.Residentes;
import Modelo.AlmacenarResidentes;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class controlador_busqueda {

    private final interfaz_busqueda vista;
    private final AlmacenarResidentes repo;
    private ArrayList<Residentes> listaLocal = new ArrayList<>();

    public controlador_busqueda(interfaz_busqueda vista) {
        this.vista = vista;
        this.repo  = new AlmacenarResidentes();
        listaLocal.addAll(repo.obtenerTodos());

        this.vista.getBtnBusqueda().addActionListener(l -> busquedaParametros());
        this.vista.getBtnRegreso().addActionListener(l  -> cerrarVentana());
    }

    public void busquedaParametros() {
        String criterio = (String) this.vista.getComboCriterio().getSelectedItem();
        String busqueda = this.vista.getStrBusqueda();

        if (busqueda.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Por favor ingrese un valor para buscar.",
                "Campo vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recargar lista actualizada desde MongoDB
        listaLocal.clear();
        listaLocal.addAll(repo.obtenerTodos());

        ArrayList<Residentes> encontrados = new ArrayList<>();

        for (Residentes r : listaLocal) {
            boolean coincide = false;

            switch (criterio) {
                case "Nombre":
                    coincide = r.getNombres().trim().toLowerCase()
                                 .contains(busqueda.toLowerCase());
                    break;
                case "Cedula":
                    coincide = r.getCedula().trim().equals(busqueda);
                    break;
                case "N° Casa":
                    coincide = r.getNumeroVivienda().trim().equals(busqueda.trim());
                    break;
            }

            if (coincide) {
                encontrados.add(r);
            }
        }

        if (!encontrados.isEmpty()) {
            for (Residentes r : encontrados) {
                mostrarDatosResidente(r);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                "No se encontró ningún residente con ese criterio.",
                "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarDatosResidente(Residentes r) {
        String datos =
            "╔══════════════════════════════════════╗\n"  +
            "         DATOS DEL RESIDENTE           \n"   +
            "╚══════════════════════════════════════╝\n"  +
            "Nombres:               " + r.getNombres()               + "\n" +
            "Apellidos:             " + r.getApellidos()             + "\n" +
            "Cédula:                " + r.getCedula()                + "\n" +
            "Teléfono Móvil:        " + r.getTelefonoMovil()         + "\n" +
            "Teléfono Convencional: " + r.getTelefonoConvencional()  + "\n" +
            "N° Vivienda:           " + r.getNumeroVivienda()        + "\n" +
            "Tiene Mascotas:        " + (r.isTieneMascotas() ? "Sí" : "No") + "\n" +
            "N° Vehículos:          " + r.getNumeroVehiculos()       + "\n"
    ;

        JOptionPane.showMessageDialog(null, datos,
            "Residente Encontrado", JOptionPane.INFORMATION_MESSAGE);
    }

    public void cerrarVentana() {
        // 1. Instanciamos y abrimos el menú anterior
        Vista.interfaz_menu vistaM = new Vista.Formulario_Menu();
        Controlador.controlador_menu ctrM = new Controlador.controlador_menu(vistaM);
        ctrM.abrirMenu();
        
        // 2. Cerramos la ventana actual de búsqueda
        // Asegúrate de que 'vista' herede de JFrame para poder usar dispose()
        // Si tienes problemas de casteo, puedes intentar: 
        // ((javax.swing.JFrame) this.vista).dispose();
        this.vista.dispose();
    }

    public void iniciar() {
        this.vista.setVisible();
    }
}