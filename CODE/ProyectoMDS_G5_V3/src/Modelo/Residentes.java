package Modelo;

public class Residentes {

    private String nombres;
    private String apellidos;
    private String cedula;
    private String telefonoMovil;
    private String telefonoConvencional;
    private String numeroVivienda;
    private boolean tieneMascotas;
    private String numeroVehiculos;
    private String tipoResidente;


    public Residentes(String nombres, String apellidos, String cedula,
                      String telefonoMovil, String telefonoConvencional,
                      String numeroVivienda,
                      boolean tieneMascotas, String numeroVehiculos, String tipoResidente ) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.telefonoMovil = telefonoMovil;
        this.telefonoConvencional = telefonoConvencional;
        this.numeroVivienda = numeroVivienda;
        this.tieneMascotas = tieneMascotas;
        this.numeroVehiculos = numeroVehiculos;
        this.tipoResidente = tipoResidente;
    }

    public String getNombres(){ 
        return nombres;
    }
    
    public void setNombres(String nombres){ 
        this.nombres = nombres; 
    }

    public String getApellidos(){ 
        return apellidos; 
    }
    
    public void setApellidos(String apellidos){ 
        this.apellidos = apellidos; 
    }

    public String getCedula(){ 
        return cedula; 
    }
    
    public void setCedula(String cedula){ 
        this.cedula = cedula; 
    }

    public String getTelefonoMovil(){ 
        return telefonoMovil; 
    }
    
    public void setTelefonoMovil(String telefonoMovil){ 
        this.telefonoMovil = telefonoMovil; 
    }

    public String getTelefonoConvencional(){ 
        return telefonoConvencional; 
    }
    
    public void setTelefonoConvencional(String telefonoConvencional){ 
        this.telefonoConvencional = telefonoConvencional; 
    }

    public String getNumeroVivienda(){ 
        return numeroVivienda; 
    }
    
    public void setNumeroVivienda(String numeroVivienda){ 
        this.numeroVivienda = numeroVivienda; 
    }

    public boolean isTieneMascotas(){ 
        return tieneMascotas; 
    }
    
    public void setTieneMascotas(boolean tieneMascotas){ 
        this.tieneMascotas = tieneMascotas; 
    }

    public String getNumeroVehiculos(){ 
        return numeroVehiculos; 
    }
    
    public void setNumeroVehiculos(String numeroVehiculos){ 
        this.numeroVehiculos = numeroVehiculos; 
    }

    public String getTipoResidente() {
        return tipoResidente;
    }

    public void setTipoResidente(String tipoResidente) {
        this.tipoResidente = tipoResidente;
    }

    
}