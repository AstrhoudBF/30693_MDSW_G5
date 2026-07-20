package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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
        // Serializar vehículos como lista de documentos {Placa, TipoVehiculo}
        List<Document> vDocs = new ArrayList<>();
        if (r.getVehiculos() != null) {
            for (String[] v : r.getVehiculos()) {
                vDocs.add(new Document("Placa", v[0]).append("TipoVehiculo", v[1]));
            }
        }
        Date fechaReg = r.getFechaRegistro() != null
            ? Date.from(r.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant())
            : new Date();

        return new Document("Nombres",              r.getNombres())
                .append("Apellidos",             r.getApellidos())
                .append("Cedula",                r.getCedula())
                .append("TelefonoMovil",         r.getTelefonoMovil())
                .append("TelefonoConvencional",  r.getTelefonoConvencional() != null ? r.getTelefonoConvencional() : "")
                .append("NumeroVivienda",        r.getNumeroVivienda())
                .append("TipoResidente",         r.getTipoResidente())
                .append("TieneMascotas",         r.isTieneMascotas())
                .append("Vehiculos",             vDocs)
                .append("FechaRegistro",         fechaReg);
    }

    // ── Document → Residentes ─────────────────────────────────────
    @SuppressWarnings("unchecked")
    private Residentes sacarDocumento(Document d) {
        // Deserializar vehículos
        List<String[]> vehiculos = new ArrayList<>();
        List<Document> vDocs = (List<Document>) d.get("Vehiculos");
        if (vDocs != null) {
            for (Document v : vDocs) {
                vehiculos.add(new String[]{
                    v.getString("Placa") != null    ? v.getString("Placa")        : "",
                    v.getString("TipoVehiculo") != null ? v.getString("TipoVehiculo") : ""
                });
            }
        }
        Residentes r = new Residentes(
                d.getString("Nombres"),
                d.getString("Apellidos"),
                d.getString("Cedula"),
                d.getString("TelefonoMovil"),
                d.getString("TelefonoConvencional"),
                d.getString("NumeroVivienda"),
                d.getString("TipoResidente"),
                Boolean.TRUE.equals(d.getBoolean("TieneMascotas")),
                vehiculos
        );
        // Restaurar fecha de registro guardada en Mongo (no generar una nueva)
        Date fechaReg = d.getDate("FechaRegistro");
        if (fechaReg != null) {
            r.setFechaRegistro(fechaReg.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        return r;
    }

    public ArrayList<Residentes> obtenerTodos() {
        ArrayList<Residentes> lista = new ArrayList<>();
        for (Document d : columnas.find()) {
            lista.add(sacarDocumento(d));
        }
        return lista;
    }

    public void agregarMongo(Residentes r) {
        columnas.insertOne(agregarDoc(r));
    }

    /**
     * Actualiza el residente cuya cédula coincide con r.getCedula().
     * Si la cédula cambió, se usa cedulaOriginal para encontrar el documento.
     */
    public void actualizar(String cedulaOriginal, Residentes r) {
        Document filtro = new Document("Cedula", cedulaOriginal);
        columnas.replaceOne(filtro, agregarDoc(r), new ReplaceOptions().upsert(false));
    }

    /**
     * Elimina el residente cuya cédula coincide.
     */
    public void eliminar(String cedula) {
        columnas.deleteOne(new Document("Cedula", cedula));
    }

    // Compatibilidad con código legacy
    public final List<Residentes> lista = new ArrayList<>();
    public void guardar(Residentes r)   { lista.add(r); }
    public List<Residentes> listar()    { return lista; }
}
