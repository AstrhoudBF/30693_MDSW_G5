package Modelo;

public class validaciones {

    public static boolean campoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean soloNumeros(String texto) {
        return texto.matches("\\d+");
    }

    public static boolean soloLetras(String texto) {
        return texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
    }

    public static boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) return false;

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if ((provincia < 1 || provincia > 24) && provincia != 30) return false;

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) return false;

        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) digito -= 9;
            }
            suma += digito;
        }

        int decenaSuperior = ((suma + 9) / 10) * 10;
        int digitoVerificador = decenaSuperior - suma;
        if (digitoVerificador == 10) digitoVerificador = 0;

        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }

    public static String validarResidente(Residentes r) {

        // Campos vacíos
        if (campoVacio(r.getNombres()))
            return "Ingrese los nombres.";
        if (campoVacio(r.getApellidos()))
            return "Ingrese los apellidos.";
        if (campoVacio(r.getCedula()))
            return "Ingrese la cédula.";
        if (campoVacio(r.getTelefonoMovil()))
            return "Ingrese el teléfono móvil.";
        if (campoVacio(r.getTelefonoConvencional()))
            return "Ingrese el teléfono convencional.";
        if (campoVacio(r.getDireccion()))
            return "Ingrese la dirección.";
        if (campoVacio(r.getNumeroVehiculos()))
            return "Ingrese el número de vehículos.";
        if (campoVacio(r.getNumeroPersonas()))
            return "Ingrese el número de personas que viven con el propietario.";
        if (campoVacio(r.getNombresResidentes()))
            return "Ingrese los nombres y apellidos de los residentes.";

        // Formato de texto
        if (!soloLetras(r.getNombres()))
            return "Los nombres no deben contener números.";
        if (!soloLetras(r.getApellidos()))
            return "Los apellidos no deben contener números.";
        if (!soloLetras(r.getNombresResidentes()))
            return "Los nombres y apellidos de residentes no deben contener números.";

        // Cédula ecuatoriana
        if (!validarCedulaEcuatoriana(r.getCedula()))
            return "La cédula ecuatoriana ingresada no es válida.";

        // Teléfonos
        if (!r.getTelefonoMovil().matches("\\d{10}"))
            return "El teléfono móvil debe contener exactamente 10 números.";
        if (!r.getTelefonoConvencional().matches("\\d{7}"))
            return "El teléfono convencional debe contener exactamente 7 números.";

        // Números
        if (!soloNumeros(r.getNumeroVehiculos()))
            return "El número de vehículos solo debe contener números.";
        if (!soloNumeros(r.getNumeroPersonas()))
            return "El número de personas solo debe contener números.";

        return null; // Todo correcto
    }
}