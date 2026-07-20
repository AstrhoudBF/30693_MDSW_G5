package Controlador;

import Modelo.Arriendo;
import Modelo.AlmacenarArriendos;
import Vista.interfaz_arriendos;
import Vista.Formulario_Historial_Arriendos;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JFrame;

public class controlador_arriendos {

    private final interfaz_arriendos  vista;
    private final AlmacenarArriendos  repo;
    private final JFrame              menuPadre;
    private final ArrayList<Arriendo> listaLocal = new ArrayList<>();

    // Sin botón Regresar en arriendos — se cierra con la X
    public controlador_arriendos(interfaz_arriendos vista, JFrame menuPadre) {
        this.vista      = vista;
        this.menuPadre  = menuPadre;
        this.repo       = new AlmacenarArriendos();

        listaLocal.addAll(repo.obtenerTodos());
        vista.actualizarTabla(listaLocal);

        vista.getBtnGuardar().addActionListener(e   -> guardar());
        vista.getBtnHistorial().addActionListener(e -> abrirHistorial());

        // Al cerrar la ventana de arriendos, mostrar el menú de vuelta
        if (vista instanceof JFrame) {
            ((JFrame) vista).addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    if (menuPadre != null) menuPadre.setVisible(true);
                }
            });
        }
    }

    private void guardar() {
        try {
            String tipo    = vista.getTipoEspacio();
            String espacio = vista.getNumeroEspacio().trim();
            if (espacio.isEmpty()) { vista.mostrarMensaje("Debe ingresar el número o nombre del espacio."); return; }
            String nombre = vista.getNombreArrendatario().trim();
            if (nombre.isEmpty()) { vista.mostrarMensaje("Debe ingresar el nombre del arrendatario."); return; }
            String contacto = vista.getContacto().trim();
            double monto;
            try {
                monto = vista.getMontoMensual();
                if (monto <= 0) { vista.mostrarMensaje("El monto debe ser mayor a cero."); return; }
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto ingresado no es válido."); return;
            }
            String periodo = vista.getMesPeriodo().trim();
            if (periodo.isEmpty()) { vista.mostrarMensaje("Debe ingresar el mes/período (ej: Junio 2026)."); return; }

            Arriendo a = new Arriendo(tipo, espacio, nombre, vista.getTipoArrendatario(),
                    contacto, monto, periodo, vista.getEstado(), vista.getFormaPago(), LocalDateTime.now());
            repo.guardar(a);
            listaLocal.add(a);
            vista.actualizarTabla(listaLocal);
            vista.mostrarMensaje("Arriendo registrado correctamente.");
            vista.limpiarCampos();
        } catch (Exception ex) {
            vista.mostrarMensaje("Error al guardar: " + ex.getMessage());
        }
    }

    private void abrirHistorial() {
        new Formulario_Historial_Arriendos().setVisible(true);
    }

    public void iniciar() { vista.iniciar(); }
}
