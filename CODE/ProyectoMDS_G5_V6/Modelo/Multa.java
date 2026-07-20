package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Multa {

    private String        id;
    private String        numeroCasa;
    private String        cedulaResidente;
    private String        nombreResidente;
    private String        categoria;        // "Mascotas","Minga","Asamblea","Parqueaderos"
    private String        motivo;           // texto libre
    private LocalDate     fechaInfraccion;
    private double        monto;
    private String        estado;           // "Pendiente","Pagada","Anulada"
    private String        observaciones;
    private LocalDateTime fechaRegistro;

    public Multa() {}

    public Multa(String numeroCasa, String cedulaResidente, String nombreResidente,
                 String categoria, String motivo, LocalDate fechaInfraccion,
                 double monto, String estado, String observaciones,
                 LocalDateTime fechaRegistro) {
        this.numeroCasa       = numeroCasa;
        this.cedulaResidente  = cedulaResidente;
        this.nombreResidente  = nombreResidente;
        this.categoria        = categoria;
        this.motivo           = motivo;
        this.fechaInfraccion  = fechaInfraccion;
        this.monto            = monto;
        this.estado           = estado;
        this.observaciones    = observaciones;
        this.fechaRegistro    = fechaRegistro;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public String getId()                          { return id; }
    public void   setId(String v)                  { this.id = v; }

    public String getNumeroCasa()                  { return numeroCasa; }
    public void   setNumeroCasa(String v)          { this.numeroCasa = v; }

    public String getCedulaResidente()             { return cedulaResidente; }
    public void   setCedulaResidente(String v)     { this.cedulaResidente = v; }

    public String getNombreResidente()             { return nombreResidente; }
    public void   setNombreResidente(String v)     { this.nombreResidente = v; }

    public String getCategoria()                   { return categoria; }
    public void   setCategoria(String v)           { this.categoria = v; }

    public String getMotivo()                      { return motivo; }
    public void   setMotivo(String v)              { this.motivo = v; }

    public LocalDate getFechaInfraccion()          { return fechaInfraccion; }
    public void      setFechaInfraccion(LocalDate v){ this.fechaInfraccion = v; }

    public double getMonto()                       { return monto; }
    public void   setMonto(double v)               { this.monto = v; }

    public String getEstado()                      { return estado; }
    public void   setEstado(String v)              { this.estado = v; }

    public String getObservaciones()               { return observaciones; }
    public void   setObservaciones(String v)       { this.observaciones = v; }

    public LocalDateTime getFechaRegistro()        { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)  { this.fechaRegistro = v; }
}
