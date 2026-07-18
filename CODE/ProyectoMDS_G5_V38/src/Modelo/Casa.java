package Modelo;

import java.time.LocalDate;

public class Casa {
    
    private String id;
    private int numCasa;
    private double saldoPendiente;
    private LocalDate fechaUltimoCobro;

    public Casa() {
    }
    
    public Casa(int numCasa) {
        this.numCasa = numCasa;
        this.saldoPendiente = 0.0;
        this.fechaUltimoCobro = LocalDate.now(); 
    }

    public Casa(String id, int numCasa, double saldoPendiente, LocalDate fechaUltimoCobro) {
        this.id = id;
        this.numCasa = numCasa;
        this.saldoPendiente = saldoPendiente;
        this.fechaUltimoCobro = fechaUltimoCobro;
    }

    public String getId(){ 
        return id; 
    }
    
    public void setId(String id){ 
        this.id = id; 
    }
    
    public int getNumCasa(){ 
        return numCasa; 
    }
    
    public void setNumCasa(int numCasa){ 
        this.numCasa = numCasa; 
    }

    public double getSaldoPendiente(){ 
        return saldoPendiente; 
    }
    public void setSaldoPendiente(double saldoPendiente){ 
        this.saldoPendiente = saldoPendiente; 
    }

    public LocalDate getFechaUltimoCobro(){ 
        return fechaUltimoCobro; 
    }
    
    public void setFechaUltimoCobro(LocalDate fechaUltimoCobro){ 
        this.fechaUltimoCobro = fechaUltimoCobro; 
    }
    
}