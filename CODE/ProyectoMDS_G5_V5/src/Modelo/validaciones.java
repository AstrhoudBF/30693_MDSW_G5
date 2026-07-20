package Modelo;

public class validaciones {

    public static boolean campoVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean soloNumeros(String texto) {
        return texto != null && texto.matches("\\d+");
    }

    public static boolean soloLetras(String texto) {
        return texto != null && texto.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+");
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
        int decenaSuperior     = ((suma + 9) / 10) * 10;
        int digitoVerificador  = decenaSuperior - suma;
        if (digitoVerificador == 10) digitoVerificador = 0;
        return digitoVerificador == Character.getNumericValue(cedula.charAt(9));
    }

    public static String validarResidente(Residentes r) {

        // ── Campos obligatorios básicos ───────────────────────────
        if (campoVacio(r.getNombres()))    return "Ingrese los nombres.";
        if (campoVacio(r.getApellidos()))  return "Ingrese los apellidos.";
        if (campoVacio(r.getCedula()))     return "Ingrese la cédula.";

        // ── Al menos un teléfono ──────────────────────────────────
        boolean tieneMovil = !campoVacio(r.getTelefonoMovil());
        boolean tieneConv  = !campoVacio(r.getTelefonoConvencional());
        if (!tieneMovil && !tieneConv)
            return "Debe ingresar al menos el teléfono móvil o el convencional.";

        // ── Formato de texto ──────────────────────────────────────
        if (!soloLetras(r.getNombres()))   return "Los nombres no deben contener números.";
        if (!soloLetras(r.getApellidos())) return "Los apellidos no deben contener números.";

        // ── Cédula ecuatoriana ────────────────────────────────────
        if (!validarCedulaEcuatoriana(r.getCedula()))
            return "La cédula ecuatoriana ingresada no es válida.";

        // ── Validar teléfonos solo si están presentes ─────────────
        if (tieneMovil && !r.getTelefonoMovil().matches("\\d{10}"))
            return "El teléfono móvil debe contener exactamente 10 dígitos.";

        if (tieneConv && !r.getTelefonoConvencional().matches("\\d{7}"))
            return "El teléfono convencional debe contener exactamente 7 dígitos.";

        // ── Vehículos: validar placas ─────────────────────────────
        if (r.getVehiculos() != null) {
            for (String[] v : r.getVehiculos()) {
                if (v[0] == null || v[0].trim().isEmpty())
                    return "Hay un vehículo con la placa vacía. Complétela o elimínela.";
            }
        }

        return null; // Todo correcto
    }
}
