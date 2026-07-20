package Controlador;

import Modelo.AlmacenarResidentes;
import Modelo.Residentes;
import Modelo.validaciones;
import Vista.interfaz_residentes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class controlador_residentes {

    private final interfaz_residentes vista;
    private final AlmacenarResidentes repo;
    private final JFrame              menuPadre;
    private final ArrayList<Residentes> listaLocal = new ArrayList<>();

    public controlador_residentes(interfaz_residentes vista, JFrame menuPadre) {
        this.vista       = vista;
        this.menuPadre   = menuPadre;
        this.repo        = new AlmacenarResidentes();

        listaLocal.addAll(repo.obtenerTodos());
        vista.actualizarTabla(listaLocal);

        this.vista.getBtnGuardar().addActionListener(e  -> agregarMongoD());
        this.vista.getBtnRegresar().addActionListener(e -> cerrarVentana());
    }

    public Residentes guardarResidente() {
        String nombres   = vista.getNombres().trim();
        String apellidos = vista.getApellidos().trim();
        String cedula    = vista.getCedula().trim();

        if (nombres.isEmpty())   { vista.mostrarMensaje("Debe ingresar el nombre del residente."); return null; }
        if (apellidos.isEmpty()) { vista.mostrarMensaje("Debe ingresar los apellidos del residente."); return null; }
        if (cedula.isEmpty())    { vista.mostrarMensaje("Debe ingresar la cédula del residente."); return null; }

        String telMovil = vista.getTelefonoMovil().trim();
        String telConv  = vista.getTelefonoConvencional().trim();
        if (telMovil.isEmpty() && telConv.isEmpty()) {
            vista.mostrarMensaje("Debe ingresar al menos un teléfono:\n  • Teléfono Móvil  o\n  • Teléfono Convencional");
            return null;
        }

        List<String[]> vehiculos = vista.getVehiculos();
        if (vehiculos != null) {
            for (int i = 0; i < vehiculos.size(); i++) {
                if (vehiculos.get(i)[0].trim().isEmpty()) {
                    vista.mostrarMensaje("El vehículo #" + (i + 1) + " tiene la placa vacía. Complétela o elimínela.");
                    return null;
                }
            }
        }

        Residentes r = new Residentes(nombres, apellidos, cedula,
                telMovil.isEmpty() ? telConv : telMovil, telConv,
                vista.getNumeroVivienda(), vista.getTipoResidente(),
                vista.getTieneMascotas(), vehiculos);

        String error = validaciones.validarResidente(r);
        if (error != null) { vista.mostrarMensaje(error); return null; }
        return r;
    }

    private void agregarMongoD() {
        try {
            Residentes r = guardarResidente();
            if (r == null) return;
            repo.agregarMongo(r);
            listaLocal.add(r);
            vista.actualizarTabla(listaLocal);
            vista.mostrarMensaje("Residente guardado correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error en la BDD: " + ex.getMessage());
        }
    }

    private void cerrarVentana() {
        vista.dispose();
        if (menuPadre != null) menuPadre.setVisible(true);
    }

    public void iniciar() { this.vista.iniciar(); }
}
