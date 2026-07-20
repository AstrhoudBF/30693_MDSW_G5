package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa una reserva de la SEDE SOCIAL.
 * Puede ser por horas dentro de un día o el día completo.
 */
public class ArriendoSede {

    private String    id;
    private String    nombreSolicitante;
    private String    tipoSolicitante;  // "Residente" | "Externo"
    private String    contacto;
    private LocalDate fechaReserva;     // día de la reserva
    private String    modalidad;        // "Por Horas" | "Día Completo"
    private String    horaInicio;       // ej. "09:00"  (solo para Por Horas)
    private String    horaFin;          // ej. "13:00"  (solo para Por Horas)
    private double    monto;
    private String    estado;           // "Confirmada" | "Pendiente" | "Cancelada"
    private String    formaPago;        // "Efectivo" | "Transferencia" | "Depósito"
    private String    motivo;           // descripción del evento
    private LocalDateTime fechaRegistro;

    public ArriendoSede() {}

    public ArriendoSede(String nombreSolicitante, String tipoSolicitante, String contacto,
                        LocalDate fechaReserva, String modalidad,
                        String horaInicio, String horaFin,
                        double monto, String estado, String formaPago,
                        String motivo, LocalDateTime fechaRegistro) {
        this.nombreSolicitante = nombreSolicitante;
        this.tipoSolicitante   = tipoSolicitante;
        this.contacto          = contacto;
        this.fechaReserva      = fechaReserva;
        this.modalidad         = modalidad;
        this.horaInicio        = horaInicio;
        this.horaFin           = horaFin;
        this.monto             = monto;
        this.estado            = estado;
        this.formaPago         = formaPago;
        this.motivo            = motivo;
        this.fechaRegistro     = fechaRegistro;
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public String getId()                          { return id; }
    public void   setId(String id)                 { this.id = id; }

    public String getNombreSolicitante()           { return nombreSolicitante; }
    public void   setNombreSolicitante(String v)   { this.nombreSolicitante = v; }

    public String getTipoSolicitante()             { return tipoSolicitante; }
    public void   setTipoSolicitante(String v)     { this.tipoSolicitante = v; }

    public String getContacto()                    { return contacto; }
    public void   setContacto(String v)            { this.contacto = v; }

    public LocalDate getFechaReserva()             { return fechaReserva; }
    public void      setFechaReserva(LocalDate v)  { this.fechaReserva = v; }

    public String getModalidad()                   { return modalidad; }
    public void   setModalidad(String v)           { this.modalidad = v; }

    public String getHoraInicio()                  { return horaInicio; }
    public void   setHoraInicio(String v)          { this.horaInicio = v; }

    public String getHoraFin()                     { return horaFin; }
    public void   setHoraFin(String v)             { this.horaFin = v; }

    public double getMonto()                       { return monto; }
    public void   setMonto(double v)               { this.monto = v; }

    public String getEstado()                      { return estado; }
    public void   setEstado(String v)              { this.estado = v; }

    public String getFormaPago()                   { return formaPago; }
    public void   setFormaPago(String v)           { this.formaPago = v; }

    public String getMotivo()                      { return motivo; }
    public void   setMotivo(String v)              { this.motivo = v; }

    public LocalDateTime getFechaRegistro()        { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime v)  { this.fechaRegistro = v; }
}
