package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ArriendoSede {

    private String    id;
    private String    nombreSolicitante;
    private String    tipoSolicitante;   // "Residente" | "Externo"
    private String    numeroCasaResidente; // solo si tipoSolicitante="Residente"
    private String    telefono;
    private String    email;
    private LocalDate fechaReserva;
    private String    modalidad;         // "Por Horas" | "Día Completo"
    private String    horaInicio;
    private String    horaFin;
    private double    monto;
    private String    estado;            // "Confirmada" | "Pendiente" | "Cancelada"
    private String    formaPago;         // "Efectivo" | "Transferencia" | "Depósito"
    private String    numeroTransaccion; // solo Transferencia/Depósito
    private LocalDate fechaPago;         // solo si estado != Pendiente
    private String    motivo;
    private LocalDateTime fechaRegistro;

    public ArriendoSede() {}

    public ArriendoSede(String nombreSolicitante, String tipoSolicitante,
                        String numeroCasaResidente,
                        String telefono, String email,
                        LocalDate fechaReserva, String modalidad,
                        String horaInicio, String horaFin,
                        double monto, String estado, String formaPago,
                        String numeroTransaccion, LocalDate fechaPago,
                        String motivo, LocalDateTime fechaRegistro) {
        this.nombreSolicitante   = nombreSolicitante;
        this.tipoSolicitante     = tipoSolicitante;
        this.numeroCasaResidente = numeroCasaResidente;
        this.telefono            = telefono;
        this.email               = email;
        this.fechaReserva        = fechaReserva;
        this.modalidad           = modalidad;
        this.horaInicio          = horaInicio;
        this.horaFin             = horaFin;
        this.monto               = monto;
        this.estado              = estado;
        this.formaPago           = formaPago;
        this.numeroTransaccion   = numeroTransaccion;
        this.fechaPago           = fechaPago;
        this.motivo              = motivo;
        this.fechaRegistro       = fechaRegistro;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public String getId()                              { return id; }
    public void   setId(String v)                      { this.id = v; }

    public String getNombreSolicitante()               { return nombreSolicitante; }
    public void   setNombreSolicitante(String v)       { this.nombreSolicitante = v; }

    public String getTipoSolicitante()                 { return tipoSolicitante; }
    public void   setTipoSolicitante(String v)         { this.tipoSolicitante = v; }

    public String getNumeroCasaResidente()             { return numeroCasaResidente; }
    public void   setNumeroCasaResidente(String v)     { this.numeroCasaResidente = v; }

    public String getTelefono()                        { return telefono; }
    public void   setTelefono(String v)                { this.telefono = v; }

    public String getEmail()                           { return email; }
    public void   setEmail(String v)                   { this.email = v; }

    public LocalDate getFechaReserva()                 { return fechaReserva; }
    public void      setFechaReserva(LocalDate v)      { this.fechaReserva = v; }

    public String getModalidad()                       { return modalidad; }
    public void   setModalidad(String v)               { this.modalidad = v; }

    public String getHoraInicio()                      { return horaInicio; }
    public void   setHoraInicio(String v)              { this.horaInicio = v; }

    public String getHoraFin()                         { return horaFin; }
    public void   setHoraFin(String v)                 { this.horaFin = v; }

    public double getMonto()                           { return monto; }
    public void   setMonto(double v)                   { this.monto = v; }

    public String getEstado()                          { return estado; }
    public void   setEstado(String v)                  { this.estado = v; }

    public String getFormaPago()                       { return formaPago; }
    public void   setFormaPago(String v)               { this.formaPago = v; }

    public String getNumeroTransaccion()               { return numeroTransaccion; }
    public void   setNumeroTransaccion(String v)       { this.numeroTransaccion = v; }

    public LocalDate getFechaPago()                    { return fechaPago; }
    public void      setFechaPago(LocalDate v)         { this.fechaPago = v; }

    public String getMotivo()                          { return motivo; }
    public void   setMotivo(String v)                  { this.motivo = v; }

    public LocalDateTime getFechaRegistro()            { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)      { this.fechaRegistro = v; }

    // Retrocompatibilidad: contacto unificado para código anterior
    public String getContacto() {
        String tel = telefono != null ? telefono : "";
        String em  = email    != null ? email    : "";
        return tel + ((!tel.isEmpty() && !em.isEmpty()) ? " / " : "") + em;
    }
}
