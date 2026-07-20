package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AlmacenarAlicuotas {

    private final MongoCollection<Document> coleccion;

    public AlmacenarAlicuotas() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("Alicuotas");
    }

    // ── Alicuota → Document ───────────────────────────────────────
    private Document toDocument(Alicuota a) {
        Date fecha = Date.from(
            a.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant()
        );
        return new Document("NumeroCasa",      a.getNumeroCasa())
                .append("NombreResidente", a.getNombreResidente())
                .append("Monto",           a.getMonto())
                .append("Periodo",         a.getPeriodo())
                .append("Estado",          a.getEstado())
                .append("FormaPago",       a.getFormaPago())
                .append("FechaRegistro",   fecha);
    }

    // ── Document → Alicuota ───────────────────────────────────────
    private Alicuota fromDocument(Document d) {
        Alicuota a = new Alicuota();
        a.setId(d.getObjectId("_id").toHexString());
        a.setNumeroCasa(d.getString("NumeroCasa"));
        a.setNombreResidente(d.getString("NombreResidente"));
        a.setMonto(d.getDouble("Monto") != null ? d.getDouble("Monto") : 0.0);
        a.setPeriodo(d.getString("Periodo"));
        a.setEstado(d.getString("Estado"));
        a.setFormaPago(d.getString("FormaPago"));
        Date fecha = d.getDate("FechaRegistro");
        if (fecha != null)
            a.setFechaRegistro(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return a;
    }

    // ── CRUD ──────────────────────────────────────────────────────
    public void guardar(Alicuota a) {
        coleccion.insertOne(toDocument(a));
    }

    /** Actualiza una alícuota por su _id. Conserva la FechaRegistro original. */
    public void actualizar(String id, Alicuota a) {
        Document filtro = new Document("_id", new ObjectId(id));
        coleccion.replaceOne(filtro, toDocument(a), new ReplaceOptions().upsert(false));
    }

    /** Elimina una alícuota por su _id. */
    public void eliminar(String id) {
        coleccion.deleteOne(new Document("_id", new ObjectId(id)));
    }

    public ArrayList<Alicuota> obtenerTodas() {
        ArrayList<Alicuota> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDocument(d));
        return lista;
    }

    // ── Autocompletar residente por casa ──────────────────────────
    public String obtenerResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> residentes = bdd.getCollection("Residentes");
        Document encontrado = residentes.find(new Document("NumeroVivienda", numeroCasa)).first();
        if (encontrado != null) {
            String n  = encontrado.getString("Nombres");
            String ap = encontrado.getString("Apellidos");
            return (n != null ? n : "") + " " + (ap != null ? ap : "");
        }
        return null;
    }

    // ── Cruce con multas pendientes ────────────────────────────────
    public String generarAvisoMultas(String numeroCasa) {
        AlmacenarMultas repoMultas = new AlmacenarMultas();
        ArrayList<Multa> pendientes = repoMultas.multasPendientesPorCasa(numeroCasa);
        if (pendientes.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        sb.append("⚠ ATENCIÓN — Este residente tiene ")
          .append(pendientes.size()).append(" multa(s) pendiente(s):\n\n");
        double total = 0;
        for (Multa m : pendientes) {
            sb.append("  • [").append(m.getCategoria()).append("] ")
              .append(m.getMotivo())
              .append(" — $").append(String.format("%.2f", m.getMonto())).append("\n");
            total += m.getMonto();
        }
        sb.append("\nTotal multas pendientes: $").append(String.format("%.2f", total))
          .append("\n\nSe recomienda gestionar el cobro de las multas junto con la alícuota.");
        return sb.toString();
    }
}
