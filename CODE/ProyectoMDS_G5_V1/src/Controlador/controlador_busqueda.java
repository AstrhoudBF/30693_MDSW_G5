
package Controlador;


import Vista.interfaz_busqueda;
import Modelo.Residentes;
import Modelo.AlmacenarResidentes;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/**
 *
 * @author Paul
 */
public class controlador_busqueda {
    private final interfaz_busqueda vista;
    private final AlmacenarResidentes repo;
    private ArrayList<Residentes> listaLocal = new ArrayList<>();
    
    public controlador_busqueda(interfaz_busqueda vista){
        this.vista = vista;
        this.repo = new AlmacenarResidentes();
        listaLocal.addAll(repo.obtenerTodos());
        this.vista.getBtnBusqueda().addActionListener(l -> busquedaParametros());
        this.vista.getBtnRegreso().addActionListener(l -> cerrarVentana());
    }
   
    
 public void busquedaParametros() {
    String criterio = (String) this.vista.getComboCriterio().getSelectedItem();

    if (criterio.equals("Cedula")) {
        String busqueda = (String) this.vista.getTxtBusqueda().getText();
        for (Residentes r : listaLocal) {
            if (r.getCedula().equals(busqueda)) {
                String mensaje = "¿Se ha encontrado al residente con cédula " + r.getCedula() + ". ¿Desea registrar la acción para este residente?";
                String tituloVentana = "Confirmación de Residente";
                int respuesta = JOptionPane.showConfirmDialog(null, mensaje, tituloVentana, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (respuesta == JOptionPane.YES_OPTION) {
                    System.out.println("Acción confirmada para la cédula.");
                    break; 
                } else if (respuesta == JOptionPane.NO_OPTION) {
                    
                    System.out.println("Acción cancelada.");
                }
            }
        }
    } 
    else if (criterio.equals("Nombre")) {
        String busqueda = (String) this.vista.getTxtBusqueda().getText();
        
        for (Residentes r : listaLocal) {
            if (r.getNombres().equalsIgnoreCase(busqueda)) {
                
                String mensaje = "Se encontró un residente llamado " + r.getNombres() + ". ¿Es correcto?";
                String tituloVentana = "Validación de Nombre";
                int respuesta = JOptionPane.showConfirmDialog(null, mensaje, tituloVentana, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (respuesta == JOptionPane.YES_OPTION) {
                    System.out.println("Acción confirmada para el nombre.");
                    break; // Rompe el bucle si es la persona correcta
                } else if (respuesta == JOptionPane.NO_OPTION) {
                    System.out.println("Acción cancelada. Buscando siguientes coincidencias...");
                }
            }
        }
    }
}
    
    public void cerrarVentana(){
        this.vista.dispose();
    }
    
    public void iniciar(){
        this.vista.setVisible();
    }
}
