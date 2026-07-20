package Controlador;

import Modelo.AlmacenarResidentes;
import Modelo.Residentes;
import Vista.Formulario_Editar_Residente;
import Vista.interfaz_busqueda;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_busqueda {

    private final interfaz_busqueda   vista;
    private final AlmacenarResidentes repo;
    private final JFrame              menuPadre;
    private ArrayList<Residentes>     listaActual = new ArrayList<>();

    public controlador_busqueda(interfaz_busqueda vista, JFrame menuPadre) {
        this.vista     = vista;
        this.menuPadre = menuPadre;
        this.repo      = new AlmacenarResidentes();

        vista.getBtnBusqueda().addActionListener(e  -> buscar());
        vista.getBtnRegreso().addActionListener(e   -> regresar());
        vista.getBtnModificar().addActionListener(e -> abrirEdicion());
    }

    private void buscar() {
        String criterio = vista.getComboCriterio().getSelectedItem().toString();
        String dato     = vista.getStrBusqueda().trim().toLowerCase();
        if (dato.isEmpty()) { vista.mostrarMensaje("Ingrese un dato para buscar."); return; }

        ArrayList<Residentes> todos     = repo.obtenerTodos();
        ArrayList<Residentes> resultado = new ArrayList<>();
        for (Residentes r : todos) {
            switch (criterio) {
                case "Nombre":
                    if ((r.getNombres() + " " + r.getApellidos()).toLowerCase().contains(dato))
                        resultado.add(r); break;
                case "Cedula":
                    if (r.getCedula() != null && r.getCedula().contains(dato))
                        resultado.add(r); break;
                case "N° Casa":
                    if (r.getNumeroVivienda() != null && r.getNumeroVivienda().equalsIgnoreCase(dato))
                        resultado.add(r); break;
            }
        }
        listaActual = resultado;
        vista.mostrarResultados(resultado);
        if (resultado.isEmpty()) vista.mostrarMensaje("No se encontraron residentes con ese criterio.");
    }

    private void abrirEdicion() {
        String cedula = vista.getCedulaSeleccionada();
        if (cedula == null) { vista.mostrarMensaje("Seleccione un residente de la tabla para modificar."); return; }
        Residentes seleccionado = null;
        for (Residentes r : listaActual) { if (cedula.equals(r.getCedula())) { seleccionado = r; break; } }
        if (seleccionado == null) { vista.mostrarMensaje("No se pudo obtener los datos del residente."); return; }
        new Formulario_Editar_Residente(seleccionado, cedula, this).setVisible(true);
    }

    public void refrescarBusqueda() { buscar(); }

    private void regresar() {
        vista.dispose();
        if (menuPadre != null) menuPadre.setVisible(true);
    }

    public void iniciar() { vista.setVisible(); }
}
