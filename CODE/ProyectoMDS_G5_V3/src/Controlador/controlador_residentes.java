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
        this.repo  = new AlmacenarResidentes();

        listaLocal.addAll(repo.obtenerTodos());
        vista.actualizarTabla(listaLocal);

        this.vista.getBtnGuardar().addActionListener(e  -> agregarMongoD());
        this.vista.getBtnRegresar().addActionListener(e -> cerrarVentana());
    }

    public Residentes guardarResidente() {
        
        String numeroTel = this.vista.getTelefonoMovil();
        String numeroCon = this.vista.getTelefonoConvencional();
        
        if(numeroTel.trim().isEmpty() && numeroCon.trim().isEmpty()){
            javax.swing.JOptionPane.showMessageDialog(null,"EL numero de telefono y convencional estan vacios.");
            return null;
        }
        if(numeroTel.trim().isEmpty()){
            numeroTel = numeroCon;
        }
        
        Residentes r = new Residentes(
                vista.getNombres(),
                vista.getApellidos(),
                vista.getCedula(),
                numeroTel,
                numeroCon,
                vista.getNumeroVivienda(),
                vista.getTieneMascotas(),
                vista.getNumeroVehiculos(),
                vista.getTipoResidente());
        String error = validaciones.validarResidente(r);
        if (error != null) {
            vista.mostrarMensaje(error);
            return null;
        }
        return r;
    }
    
    private void agregarMongoD() {
        try {
            Residentes r = guardarResidente();
            if (r == null){ 
                return;
            }
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
        // 1. Instanciar y abrir de nuevo el menú principal
        Vista.interfaz_menu vistaM = new Vista.Formulario_Menu();
        Controlador.controlador_menu ctrM = new Controlador.controlador_menu(vistaM);
        ctrM.abrirMenu();
        
        // 2. Cerrar la ventana actual de Registro de Residentes
        // Al igual que antes, si Java se queja, usa el casteo: ((javax.swing.JFrame) this.vista).dispose();
        this.vista.dispose();
    }

    public void iniciar(){
        this.vista.iniciar();
    }    
}