package Controlador;

import Modelo.AlmacenarResidentes;
import Modelo.Residentes;
import Modelo.validaciones;
import Vista.Formulario_Menu;
import Vista.interfaz_menu;
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
        this.vista     = vista;
        this.menuPadre = menuPadre;
        this.repo      = new AlmacenarResidentes();

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
                    vista.mostrarMensaje("El vehículo #" + (i+1) + " tiene la placa vacía. Complétela o elimínela.");
                    return null;
                }
            }
        }

        Residentes r = new Residentes(nombres, apellidos, cedula,
                telMovil.isEmpty() ? telConv : telMovil, telConv,
                vista.getNumeroVivienda(), vista.getTipoResidente(),
                vista.getTieneMascotas(), vehiculos);
        r.setEstadoResidente(vista.getEstadoResidente());

        // ── Validaciones de formato ────────────────────────────────
        String error = validaciones.validarResidente(r);
        if (error != null) { vista.mostrarMensaje(error); return null; }

        // ── Validaciones de unicidad contra la base de datos ──────

        // 1. Cédula duplicada
        if (repo.cedulaExiste(cedula, null)) {
            vista.mostrarMensaje("La cédula " + cedula + " ya está registrada en el sistema.\nVerifique los datos e intente de nuevo.");
            return null;
        }

        // 2. Casa ya ocupada por residente activo
        String vivienda = vista.getNumeroVivienda();
        String ocupadaPor = repo.casaOcupadaPorActivo(vivienda, null);
        if (ocupadaPor != null) {
            vista.mostrarMensaje("La casa N° " + vivienda + " ya está asignada al residente:\n" + ocupadaPor
                + "\n\nPara asignarla a otro residente, primero cambie el estado del residente actual a 'Cancelado'.");
            return null;
        }

        // 3. Teléfono móvil duplicado
        if (!telMovil.isEmpty()) {
            String duploMovil = repo.telefonoMovilExiste(telMovil, null);
            if (duploMovil != null) {
                vista.mostrarMensaje("El teléfono móvil " + telMovil + " ya está registrado en:\n" + duploMovil);
                return null;
            }
        }

        // 4. Teléfono convencional duplicado
        if (!telConv.isEmpty()) {
            String duploConv = repo.telefonoConvExiste(telConv, null);
            if (duploConv != null) {
                vista.mostrarMensaje("El teléfono convencional " + telConv + " ya está registrado en:\n" + duploConv);
                return null;
            }
        }

        // 5. Validar placas de vehículos: que no estén registradas en otro residente activo
        if (vehiculos != null) {
            for (String[] v : vehiculos) {
                String placa = v[0].trim().toUpperCase();
                if (!placa.isEmpty()) {
                    String duploPlaca = repo.placaExiste(placa, null);
                    if (duploPlaca != null) {
                        vista.mostrarMensaje("La placa " + placa + " ya está registrada en:\n" + duploPlaca);
                        return null;
                    }
                }
            }
        }

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

    public void cerrarVentana() {
    // 1. Cerramos y destruimos la ventana actual de residentes
    this.vista.dispose();
    
    // 2. Volvemos a mostrar el menú principal original
    if (menuPadre != null) {
        menuPadre.setVisible(true);
    }
    }      

    public void iniciar() { 
        this.vista.iniciar(); 
    }
    }
