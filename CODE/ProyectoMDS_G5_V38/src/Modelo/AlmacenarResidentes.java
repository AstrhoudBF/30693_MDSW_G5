package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;

public class AlmacenarResidentes {

    public static MongoCollection<Document> columnas;

    public AlmacenarResidentes() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.columnas = bdd.getCollection("Residentes");
    }

    // ── Residentes → Document ─────────────────────────────────────
    private Document agregarDoc(Residentes r) {
        List<Document> vDocs = new ArrayList<>();
        if (r.getVehiculos() != null) {
            for (String[] v : r.getVehiculos())
                vDocs.add(new Document("Placa", v[0]).append("TipoVehiculo", v[1]));
        }
        Date fechaReg = r.getFechaRegistro() != null
            ? Date.from(r.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant())
            : new Date();

        return new Document("Nombres",             r.getNombres())
                .append("Apellidos",            r.getApellidos())
                .append("Cedula",               r.getCedula())
                .append("TelefonoMovil",        r.getTelefonoMovil())
                .append("TelefonoConvencional", r.getTelefonoConvencional() != null ? r.getTelefonoConvencional() : "")
                .append("NumeroVivienda",       r.getNumeroVivienda())
                .append("TipoResidente",        r.getTipoResidente())
                .append("TieneMascotas",        r.isTieneMascotas())
                .append("Vehiculos",            vDocs)
                .append("EstadoResidente",      r.getEstadoResidente())
                .append("FechaRegistro",        fechaReg);
    }

    // ── Document → Residentes ─────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Residentes sacarDocumento(Document d) {
        List<String[]> vehiculos = new ArrayList<>();
        List<Document> vDocs = (List<Document>) d.get("Vehiculos");
        if (vDocs != null) {
            for (Document v : vDocs)
                vehiculos.add(new String[]{
                    v.getString("Placa")        != null ? v.getString("Placa")        : "",
                    v.getString("TipoVehiculo") != null ? v.getString("TipoVehiculo") : ""
                });
        }
        Residentes r = new Residentes(
                d.getString("Nombres"), d.getString("Apellidos"), d.getString("Cedula"),
                d.getString("TelefonoMovil"), d.getString("TelefonoConvencional"),
                d.getString("NumeroVivienda"), d.getString("TipoResidente"),
                Boolean.TRUE.equals(d.getBoolean("TieneMascotas")), vehiculos);
        Date fechaReg = d.getDate("FechaRegistro");
        if (fechaReg != null)
            r.setFechaRegistro(fechaReg.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        String est = d.getString("EstadoResidente");
        r.setEstadoResidente(est != null ? est : "Activo");
        return r;
    }

    public ArrayList<Residentes> obtenerTodos() {
        ArrayList<Residentes> lista = new ArrayList<>();
        for (Document d : columnas.find()) lista.add(sacarDocumento(d));
        return lista;
    }

    public void agregarMongo(Residentes r) { columnas.insertOne(agregarDoc(r)); }

    public void actualizar(String cedulaOriginal, Residentes r) {
        columnas.replaceOne(new Document("Cedula", cedulaOriginal),
                            agregarDoc(r), new ReplaceOptions().upsert(false));
    }

    public void eliminar(String cedula) {
        columnas.deleteOne(new Document("Cedula", cedula));
    }

    // ══════════════════════════════════════════════════════════════
    // Consultas de unicidad
    // ══════════════════════════════════════════════════════════════

    /**
     * Verifica si el número de casa está ocupado por un residente ACTIVO.
     * @param numeroCasa   la casa a verificar
     * @param excluirCedula cédula del residente a ignorar (para edición), o null
     * @return nombre completo del residente que la ocupa, o null si está libre
     */
    public String casaOcupadaPorActivo(String numeroCasa, String excluirCedula) {
        if (numeroCasa == null || numeroCasa.trim().isEmpty()) return null;
        Document filtro = new Document("NumeroVivienda", numeroCasa)
                          .append("EstadoResidente", "Activo");
        Document found = columnas.find(filtro).first();
        if (found == null) return null;
        // Si es el propio residente (edición), ignorar
        String ced = found.getString("Cedula");
        if (excluirCedula != null && excluirCedula.equals(ced)) return null;
        return found.getString("Nombres") + " " + found.getString("Apellidos");
    }

    /**
     * Verifica si la cédula ya existe en la colección.
     * @param cedula       cédula a buscar
     * @param excluirCedula cédula del residente a ignorar (para edición), o null
     */
    public boolean cedulaExiste(String cedula, String excluirCedula) {
        Document found = columnas.find(new Document("Cedula", cedula)).first();
        if (found == null) return false;
        if (excluirCedula != null && excluirCedula.equals(found.getString("Cedula"))) return false;
        return true;
    }

    /**
     * Verifica si el teléfono móvil ya está registrado en otro residente.
     */
    public String telefonoMovilExiste(String tel, String excluirCedula) {
        if (tel == null || tel.trim().isEmpty()) return null;
        Document found = columnas.find(new Document("TelefonoMovil", tel)).first();
        if (found == null) return null;
        if (excluirCedula != null && excluirCedula.equals(found.getString("Cedula"))) return null;
        return found.getString("Nombres") + " " + found.getString("Apellidos")
               + " (Cédula: " + found.getString("Cedula") + ")";
    }

    /**
     * Verifica si el teléfono convencional ya está registrado en otro residente.
     */
    public String telefonoConvExiste(String tel, String excluirCedula) {
        if (tel == null || tel.trim().isEmpty()) return null;
        Document found = columnas.find(new Document("TelefonoConvencional", tel)).first();
        if (found == null) return null;
        if (excluirCedula != null && excluirCedula.equals(found.getString("Cedula"))) return null;
        return found.getString("Nombres") + " " + found.getString("Apellidos")
               + " (Cédula: " + found.getString("Cedula") + ")";
    }

    /**
     * Verifica si una placa ya está registrada en otro residente ACTIVO.
     * @param placa          la placa a verificar (en mayúsculas)
     * @param excluirCedula  cédula del residente a ignorar (edición), o null
     * @return nombre completo del residente que la tiene, o null si está libre
     */
    public String placaExiste(String placa, String excluirCedula) {
        if (placa == null || placa.trim().isEmpty()) return null;
        // Buscar en el array de vehículos de todos los residentes activos
        for (Residentes r : obtenerTodos()) {
            if (!"Activo".equals(r.getEstadoResidente())) continue;
            if (excluirCedula != null && excluirCedula.equals(r.getCedula())) continue;
            if (r.getVehiculos() != null) {
                for (String[] v : r.getVehiculos()) {
                    if (placa.equalsIgnoreCase(v[0])) {
                        return r.getNombres() + " " + r.getApellidos()
                               + " (Cédula: " + r.getCedula() + ")";
                    }
                }
            }
        }
        return null;
    }

    /**
     * Verifica si el residente de una casa tiene estado Activo.
     * Retorna null si la casa tiene residente activo, o un mensaje de error si está cancelado.
     */
    public String casaCancelada(String numeroCasa) {
        Document found = columnas.find(new Document("NumeroVivienda", numeroCasa)).first();
        if (found == null) return null; // sin residente, se valida en otro lado
        String est = found.getString("EstadoResidente");
        if ("Cancelado".equals(est)) {
            return "La Casa N° " + numeroCasa + " tiene el residente en estado 'Cancelado'. "
                 + "No se puede registrar para casas con residentes cancelados.";
        }
        return null;
    }

    // Compatibilidad con código legacy
    public final List<Residentes> lista = new ArrayList<>();
    public void guardar(Residentes r)   { lista.add(r); }
    public List<Residentes> listar()    { return lista; }
}
