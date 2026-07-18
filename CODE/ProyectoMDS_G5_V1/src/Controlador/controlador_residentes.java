package Controlador;

import Modelo.AlmacenarResidentes;
import Modelo.Residentes;
import Modelo.validaciones;
import Vista.interfaz_residentes;
import java.util.ArrayList;

public class controlador_residentes {

    private final interfaz_residentes vista;
    private final AlmacenarResidentes repo;
    private final ArrayList<Residentes> listaLocal = new ArrayList<>();

    public controlador_residentes(interfaz_residentes vista) {
        this.vista = vista;
        this.repo = new AlmacenarResidentes();
        this.vista.getBtnGuardar().addActionListener(e -> agregarMongoD());
    }

    private void agregarMongoD() {
        try {
            Residentes r = guardarResidente();
            if (r == null) return;
            repo.agregarMongo(r);
            listaLocal.add(r);
            vista.actualizarTabla(listaLocal);
            vista.mostrarMensaje("Residente guardado en BDD");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error en la BDD: " + ex.getMessage());
        }
    }

    public Residentes guardarResidente() {
        Residentes r = new Residentes(
                vista.getNombres(),
                vista.getApellidos(),
                vista.getCedula(),
                vista.getTelefonoMovil(),
                vista.getTelefonoConvencional(),
                vista.getNumeroVivienda(),
                vista.getDireccion(),
                vista.getTieneMascotas(),
                vista.getNumeroVehiculos(),
                vista.getNumeroPersonas(),
                vista.getNombresResidentes(),
                vista.getApellidosResidentes()
        );

        String error = validaciones.validarResidente(r);
        if (error != null) {
            vista.mostrarMensaje(error);
            return null;
        }
        return r;
    }
}