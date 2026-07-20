package Modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Residentes {

    private String nombres;
    private String apellidos;
    private String cedula;
    private String telefonoMovil;
    private String telefonoConvencional;   // opcional
    private String numeroVivienda;
    private String tipoResidente;          // "Propietario" | "Residente"
    private boolean tieneMascotas;
    private List<String[]> vehiculos;      // cada String[]{placa, tipo}
    private LocalDateTime fechaRegistro;   // se asigna automáticamente al guardar

    public Residentes() {
        this.vehiculos = new ArrayList<>();
    }

    public Residentes(String nombres, String apellidos, String cedula,
                      String telefonoMovil, String telefonoConvencional,
                      String numeroVivienda, String tipoResidente,
                      boolean tieneMascotas, List<String[]> vehiculos) {
        this.nombres               = nombres;
        this.apellidos             = apellidos;
        this.cedula                = cedula;
        this.telefonoMovil         = telefonoMovil;
        this.telefonoConvencional  = telefonoConvencional;
        this.numeroVivienda        = numeroVivienda;
        this.tipoResidente         = tipoResidente;
        this.tieneMascotas         = tieneMascotas;
        this.vehiculos             = vehiculos != null ? vehiculos : new ArrayList<>();
        this.fechaRegistro         = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public String getNombres()                        { return nombres; }
    public void   setNombres(String v)                { this.nombres = v; }

    public String getApellidos()                      { return apellidos; }
    public void   setApellidos(String v)              { this.apellidos = v; }

    public String getCedula()                         { return cedula; }
    public void   setCedula(String v)                 { this.cedula = v; }

    public String getTelefonoMovil()                  { return telefonoMovil; }
    public void   setTelefonoMovil(String v)          { this.telefonoMovil = v; }

    public String getTelefonoConvencional()           { return telefonoConvencional; }
    public void   setTelefonoConvencional(String v)   { this.telefonoConvencional = v; }

    public String getNumeroVivienda()                 { return numeroVivienda; }
    public void   setNumeroVivienda(String v)         { this.numeroVivienda = v; }

    public String getTipoResidente()                  { return tipoResidente; }
    public void   setTipoResidente(String v)          { this.tipoResidente = v; }

    public boolean isTieneMascotas()                  { return tieneMascotas; }
    public void    setTieneMascotas(boolean v)        { this.tieneMascotas = v; }

    public List<String[]> getVehiculos()              { return vehiculos; }
    public void setVehiculos(List<String[]> v)        { this.vehiculos = v; }

    /** Devuelve cuántos vehículos tiene registrados */
    public int getCantidadVehiculos()                 { return vehiculos != null ? vehiculos.size() : 0; }

    /**
     * Devuelve resumen legible de vehículos para la tabla.
     * Ej: "ABC-123 (Liviano), XYZ-999 (Pesado)"
     */
    public String getVehiculosResumen() {
        if (vehiculos == null || vehiculos.isEmpty()) return "0";
        StringBuilder sb = new StringBuilder();
        for (String[] v : vehiculos) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(v[0]).append(" (").append(v[1]).append(")");
        }
        return sb.toString();
    }

    public LocalDateTime getFechaRegistro()            { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)      { this.fechaRegistro = v; }

    // Compatibilidad con código que usaba getNumeroVehiculos() como String
    public String getNumeroVehiculos() {
        return String.valueOf(getCantidadVehiculos());
    }
}
