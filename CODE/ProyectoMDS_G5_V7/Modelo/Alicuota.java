package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Alicuota {

    private String    id;
    private String    numeroCasa;
    private String    nombreResidente;
    private String    telefono;
    private String    email;
    private double    monto;
    private String    periodo;           // formato: "JUNIO-2026"
    private String    estado;            // "Pagado" | "Pendiente" | "Atrasado"
    private String    formaPago;         // "Efectivo" | "Transferencia" | "Depósito"
    private String    numeroTransaccion; // solo Transferencia/Depósito
    private LocalDate fechaPago;         // null si estado != Pagado
    private LocalDateTime fechaRegistro;

    public Alicuota() {}

    public Alicuota(String numeroCasa, String nombreResidente,
                    String telefono, String email,
                    double monto, String periodo, String estado,
                    String formaPago, String numeroTransaccion,
                    LocalDate fechaPago, LocalDateTime fechaRegistro) {
        this.numeroCasa       = numeroCasa;
        this.nombreResidente  = nombreResidente;
        this.telefono         = telefono;
        this.email            = email;
        this.monto            = monto;
        this.periodo          = periodo;
        this.estado           = estado;
        this.formaPago        = formaPago;
        this.numeroTransaccion = numeroTransaccion;
        this.fechaPago        = fechaPago;
        this.fechaRegistro    = fechaRegistro;
    }

    public String getId()                              { return id; }
    public void   setId(String v)                      { this.id = v; }
    public String getNumeroCasa()                      { return numeroCasa; }
    public void   setNumeroCasa(String v)              { this.numeroCasa = v; }
    public String getNombreResidente()                 { return nombreResidente; }
    public void   setNombreResidente(String v)         { this.nombreResidente = v; }
    public String getTelefono()                        { return telefono; }
    public void   setTelefono(String v)                { this.telefono = v; }
    public String getEmail()                           { return email; }
    public void   setEmail(String v)                   { this.email = v; }
    public double getMonto()                           { return monto; }
    public void   setMonto(double v)                   { this.monto = v; }
    public String getPeriodo()                         { return periodo; }
    public void   setPeriodo(String v)                 { this.periodo = v; }
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
}
