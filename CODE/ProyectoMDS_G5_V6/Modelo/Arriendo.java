package Modelo;

import java.time.LocalDateTime;

/**
 * Representa un arriendo mensual de LOCAL o PARQUEADERO.
 * El arrendatario puede ser un residente o una persona externa.
 */
public class Arriendo {

    private String  id;
    private String  tipoEspacio;      // "Local" | "Parqueadero"
    private String  numeroEspacio;    // ej. "Local 3" | "Parqueadero 7"
    private String  nombreArrendatario;
    private String  tipoArrendatario; // "Residente" | "Externo"
    private String  contacto;         // teléfono o email
    private double  montoMensual;
    private String  mesPeriodo;        // ej. "Junio 2026"
    private String  estado;            // "Activo" | "Pagado" | "Pendiente" | "Cancelado"
    private String  formaPago;         // "Efectivo" | "Transferencia" | "Depósito"
    private LocalDateTime fechaRegistro;

    public Arriendo() {}

    public Arriendo(String tipoEspacio, String numeroEspacio,
                    String nombreArrendatario, String tipoArrendatario, String contacto,
                    double montoMensual, String mesPeriodo,
                    String estado, String formaPago, LocalDateTime fechaRegistro) {
        this.tipoEspacio        = tipoEspacio;
        this.numeroEspacio      = numeroEspacio;
        this.nombreArrendatario = nombreArrendatario;
        this.tipoArrendatario   = tipoArrendatario;
        this.contacto           = contacto;
        this.montoMensual       = montoMensual;
        this.mesPeriodo         = mesPeriodo;
        this.estado             = estado;
        this.formaPago          = formaPago;
        this.fechaRegistro      = fechaRegistro;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public String getId()                     { return id; }
    public void   setId(String id)            { this.id = id; }

    public String getTipoEspacio()            { return tipoEspacio; }
    public void   setTipoEspacio(String v)    { this.tipoEspacio = v; }

    public String getNumeroEspacio()          { return numeroEspacio; }
    public void   setNumeroEspacio(String v)  { this.numeroEspacio = v; }

    public String getNombreArrendatario()     { return nombreArrendatario; }
    public void   setNombreArrendatario(String v) { this.nombreArrendatario = v; }

    public String getTipoArrendatario()       { return tipoArrendatario; }
    public void   setTipoArrendatario(String v){ this.tipoArrendatario = v; }

    public String getContacto()               { return contacto; }
    public void   setContacto(String v)       { this.contacto = v; }

    public double getMontoMensual()           { return montoMensual; }
    public void   setMontoMensual(double v)   { this.montoMensual = v; }

    public String getMesPeriodo()             { return mesPeriodo; }
    public void   setMesPeriodo(String v)     { this.mesPeriodo = v; }

    public String getEstado()                 { return estado; }
    public void   setEstado(String v)         { this.estado = v; }

    public String getFormaPago()              { return formaPago; }
    public void   setFormaPago(String v)      { this.formaPago = v; }

    public LocalDateTime getFechaRegistro()   { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v) { this.fechaRegistro = v; }
}
