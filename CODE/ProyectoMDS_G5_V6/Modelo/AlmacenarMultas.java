package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AlmacenarMultas {

    private final MongoCollection<Document> coleccion;

    public AlmacenarMultas() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("Multas");
    }

    // ── Multa → Document ──────────────────────────────────────────
    private Document toDoc(Multa m) {
        Date fInfrac = m.getFechaInfraccion() != null
            ? Date.from(m.getFechaInfraccion().atStartOfDay(ZoneId.systemDefault()).toInstant())
            : null;
        Date fReg = m.getFechaRegistro() != null
            ? Date.from(m.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant())
            : new Date();

        return new Document("NumeroCasa",      m.getNumeroCasa())
                .append("CedulaResidente", m.getCedulaResidente())
                .append("NombreResidente", m.getNombreResidente())
                .append("Categoria",       m.getCategoria())
                .append("Motivo",          m.getMotivo())
                .append("FechaInfraccion", fInfrac)
                .append("Monto",           m.getMonto())
                .append("Estado",          m.getEstado())
                .append("Observaciones",   m.getObservaciones())
                .append("FechaRegistro",   fReg);
    }

    // ── Document → Multa ──────────────────────────────────────────
    private Multa fromDoc(Document d) {
        Multa m = new Multa();
        m.setId(d.getObjectId("_id").toHexString());
        m.setNumeroCasa(d.getString("NumeroCasa"));
        m.setCedulaResidente(d.getString("CedulaResidente"));
        m.setNombreResidente(d.getString("NombreResidente"));
        m.setCategoria(d.getString("Categoria"));
        m.setMotivo(d.getString("Motivo"));

        Date fi = d.getDate("FechaInfraccion");
        if (fi != null)
            m.setFechaInfraccion(fi.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        Double monto = d.getDouble("Monto");
        m.setMonto(monto != null ? monto : 0.0);
        m.setEstado(d.getString("Estado"));
        m.setObservaciones(d.getString("Observaciones"));

        Date fr = d.getDate("FechaRegistro");
        if (fr != null)
            m.setFechaRegistro(fr.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        return m;
    }

    // ── CRUD ──────────────────────────────────────────────────────
    public void guardar(Multa m) {
        coleccion.insertOne(toDoc(m));
    }

    public ArrayList<Multa> obtenerTodas() {
        ArrayList<Multa> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDoc(d));
        return lista;
    }

    /** Actualiza por _id (hex string). */
    public void actualizar(String id, Multa m) {
        Document filtro = new Document("_id", new ObjectId(id));
        coleccion.replaceOne(filtro, toDoc(m), new ReplaceOptions().upsert(false));
    }

    /** Elimina por _id (hex string). */
    public void eliminar(String id) {
        coleccion.deleteOne(new Document("_id", new ObjectId(id)));
    }

    // ── Consultas de apoyo ────────────────────────────────────────

    /** Multas pendientes de un residente (por casa o cédula). */
    public ArrayList<Multa> multasPendientesPorCasa(String numeroCasa) {
        ArrayList<Multa> lista = new ArrayList<>();
        Document filtro = new Document("NumeroCasa", numeroCasa)
                          .append("Estado", "Pendiente");
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        return lista;
    }

    public ArrayList<Multa> multasPendientesPorCedula(String cedula) {
        ArrayList<Multa> lista = new ArrayList<>();
        Document filtro = new Document("CedulaResidente", cedula)
                          .append("Estado", "Pendiente");
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        return lista;
    }

    /** Total de multas pendientes de una casa. */
    public double totalPendientePorCasa(String numeroCasa) {
        double total = 0;
        for (Multa m : multasPendientesPorCasa(numeroCasa)) total += m.getMonto();
        return total;
    }

    // ── Autocompletar: buscar residente por casa ──────────────────
    public String[] obtenerResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> res = bdd.getCollection("Residentes");
        Document encontrado = res.find(new Document("NumeroVivienda", numeroCasa)).first();
        if (encontrado != null) {
            String nombres   = encontrado.getString("Nombres");
            String apellidos = encontrado.getString("Apellidos");
            String cedula    = encontrado.getString("Cedula");
            return new String[]{
                ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim(),
                cedula != null ? cedula : ""
            };
        }
        return null;
    }

    // ── Autocompletar: buscar residente por cédula ────────────────
    public String[] obtenerResidentePorCedula(String cedula) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> res = bdd.getCollection("Residentes");
        Document encontrado = res.find(new Document("Cedula", cedula)).first();
        if (encontrado != null) {
            String nombres   = encontrado.getString("Nombres");
            String apellidos = encontrado.getString("Apellidos");
            String casa      = encontrado.getString("NumeroVivienda");
            return new String[]{
                ((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim(),
                casa != null ? casa : ""
            };
        }
        return null;
    }
}
