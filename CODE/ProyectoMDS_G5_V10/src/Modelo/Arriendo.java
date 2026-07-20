package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Arriendo {

    private String        id;
    private String        tipoEspacio;          // "Local" | "Parqueadero"
    private String        nombreEspacio;         // nombre del local/parqueadero
    private String        nombreArrendatario;
    private String        tipoArrendatario;      // "Residente" | "Externo"
    private String        numeroCasaResidente;   // solo si tipoArrendatario = Residente
    private String        telefono;
    private String        email;
    private double        montoMensual;
    private String        mesPeriodo;            // formato: "JUNIO-2026"
    private String        estado;               // "Pagado" | "Pendiente" | "Cancelado"
    private String        formaPago;            // "Efectivo" | "Transferencia" | "Depósito"
    private String        numeroTransaccion;    // solo Transferencia/Depósito
    private LocalDate     fechaPago;            // null si estado = Pendiente/Cancelado
    private LocalDateTime fechaRegistro;

    public Arriendo() {}

    public Arriendo(String tipoEspacio, String nombreEspacio,
                    String nombreArrendatario, String tipoArrendatario,
                    String numeroCasaResidente,
                    String telefono, String email,
                    double montoMensual, String mesPeriodo,
                    String estado, String formaPago,
                    String numeroTransaccion, LocalDate fechaPago,
                    LocalDateTime fechaRegistro) {
        this.tipoEspacio          = tipoEspacio;
        this.nombreEspacio        = nombreEspacio;
        this.nombreArrendatario   = nombreArrendatario;
        this.tipoArrendatario     = tipoArrendatario;
        this.numeroCasaResidente  = numeroCasaResidente;
        this.telefono             = telefono;
        this.email                = email;
        this.montoMensual         = montoMensual;
        this.mesPeriodo           = mesPeriodo;
        this.estado               = estado;
        this.formaPago            = formaPago;
        this.numeroTransaccion    = numeroTransaccion;
        this.fechaPago            = fechaPago;
        this.fechaRegistro        = fechaRegistro;
    }

    public String getId()                              { return id; }
    public void   setId(String v)                      { this.id = v; }
    public String getTipoEspacio()                     { return tipoEspacio; }
    public void   setTipoEspacio(String v)             { this.tipoEspacio = v; }
    public String getNombreEspacio()                   { return nombreEspacio; }
    public void   setNombreEspacio(String v)           { this.nombreEspacio = v; }
    public String getNombreArrendatario()              { return nombreArrendatario; }
    public void   setNombreArrendatario(String v)      { this.nombreArrendatario = v; }
    public String getTipoArrendatario()                { return tipoArrendatario; }
    public void   setTipoArrendatario(String v)        { this.tipoArrendatario = v; }
    public String getNumeroCasaResidente()             { return numeroCasaResidente; }
    public void   setNumeroCasaResidente(String v)     { this.numeroCasaResidente = v; }
    public String getTelefono()                        { return telefono; }
    public void   setTelefono(String v)                { this.telefono = v; }
    public String getEmail()                           { return email; }
    public void   setEmail(String v)                   { this.email = v; }
    public double getMontoMensual()                    { return montoMensual; }
    public void   setMontoMensual(double v)            { this.montoMensual = v; }
    public String getMesPeriodo()                      { return mesPeriodo; }
    public void   setMesPeriodo(String v)              { this.mesPeriodo = v; }
    public String getEstado()                          { return estado; }
    public void   setEstado(String v)                  { this.estado = v; }
    public String getFormaPago()                       { return formaPago; }
    public void   setFormaPago(String v)               { this.formaPago = v; }
    public String getNumeroTransaccion()               { return numeroTransaccion; }
    public void   setNumeroTransaccion(String v)       { this.numeroTransaccion = v; }
    public LocalDate getFechaPago()                    { return fechaPago; }
    public void      setFechaPago(LocalDate v)         { this.fechaPago = v; }
    public LocalDateTime getFechaRegistro()            { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)      { this.fechaRegistro = v; }

    // Retrocompatibilidad
    public String getContacto()       { return (telefono!=null?telefono:"") + " / " + (email!=null?email:""); }
    public String getNumeroEspacio()  { return nombreEspacio; }
}
