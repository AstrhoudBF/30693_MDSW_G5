package Modelo;

import java.time.LocalDateTime;

public class Alicuota {

    private String id;
    private String numeroCasa;
    private String nombreResidente;
    private double monto;
    private String periodo;          // ej: "Junio 2026"
    private String estado;           // "Pagado", "Pendiente", "Atrasado"
    private String formaPago;        // "Efectivo", "Transferencia", "Depósito"
    private LocalDateTime fechaRegistro;

    public Alicuota() {
    }

    public Alicuota(String numeroCasa, String nombreResidente, double monto,
                    String periodo, String estado, String formaPago,
                    LocalDateTime fechaRegistro) {
        this.numeroCasa      = numeroCasa;
        this.nombreResidente = nombreResidente;
        this.monto           = monto;
        this.periodo         = periodo;
        this.estado          = estado;
        this.formaPago       = formaPago;
        this.fechaRegistro   = fechaRegistro;
    }

    // ── Getters y Setters ─────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(String numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getNombreResidente() { return nombreResidente; }
    public void setNombreResidente(String nombreResidente) { this.nombreResidente = nombreResidente; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
