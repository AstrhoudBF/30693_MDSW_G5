package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.time.LocalDate;
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

    private Date toDate(java.time.LocalDateTime d) {
        return d != null ? Date.from(d.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    private Date toDate(LocalDate d) {
        return d != null ? Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
    private LocalDate toLocalDate(Date d) {
        return d != null ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }

    private Document toDocument(Alicuota a) {
        return new Document("NumeroCasa",       a.getNumeroCasa())
                .append("NombreResidente",  a.getNombreResidente())
                .append("Telefono",         a.getTelefono() != null ? a.getTelefono() : "")
                .append("Email",            a.getEmail()    != null ? a.getEmail()    : "")
                .append("Monto",            a.getMonto())
                .append("Periodo",          a.getPeriodo())
                .append("Estado",           a.getEstado())
                .append("FormaPago",        a.getFormaPago())
                .append("NumeroTransaccion", a.getNumeroTransaccion() != null ? a.getNumeroTransaccion() : "")
                .append("FechaPago",        toDate(a.getFechaPago()))
                .append("FechaRegistro",    toDate(a.getFechaRegistro()));
    }

    private Alicuota fromDocument(Document d) {
        Alicuota a = new Alicuota();
        a.setId(d.getObjectId("_id").toHexString());
        a.setNumeroCasa(d.getString("NumeroCasa"));
        a.setNombreResidente(d.getString("NombreResidente"));
        a.setTelefono(d.getString("Telefono"));
        a.setEmail(d.getString("Email"));
        Double monto = d.getDouble("Monto");
        a.setMonto(monto != null ? monto : 0.0);
        a.setPeriodo(d.getString("Periodo"));
        a.setEstado(d.getString("Estado"));
        a.setFormaPago(d.getString("FormaPago"));
        a.setNumeroTransaccion(d.getString("NumeroTransaccion"));
        a.setFechaPago(toLocalDate(d.getDate("FechaPago")));
        Date fr = d.getDate("FechaRegistro");
        if (fr != null)
            a.setFechaRegistro(fr.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return a;
    }

    public void guardar(Alicuota a)          { coleccion.insertOne(toDocument(a)); }

    public void actualizar(String id, Alicuota a) {
        coleccion.replaceOne(new Document("_id", new ObjectId(id)),
                             toDocument(a), new ReplaceOptions().upsert(false));
    }

    public void eliminar(String id) {
        coleccion.deleteOne(new Document("_id", new ObjectId(id)));
    }

    // Anula (lógicamente) la alícuota cambiando su estado a "Cancelado",
    // sin eliminar el documento de la base de datos.
    public void anular(String id) {
        coleccion.updateOne(
            new Document("_id", new ObjectId(id)),
            new Document("$set", new Document("Estado", "Cancelado"))
        );
    }

    public ArrayList<Alicuota> obtenerTodas() {
        ArrayList<Alicuota> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDocument(d));
        return lista;
    }

    // ── Autocompletar residente por casa (solo ACTIVO) ─────────────
    public String[] obtenerDatosResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> residentes = bdd.getCollection("Residentes");
        // Solo residentes ACTIVOS
        Document filtro = new Document("NumeroVivienda", numeroCasa)
                          .append("EstadoResidente", "Activo");
        Document encontrado = residentes.find(filtro).first();
        if (encontrado != null) {
            String n  = encontrado.getString("Nombres");
            String ap = encontrado.getString("Apellidos");
            String tel = encontrado.getString("TelefonoMovil");
            if (tel == null || tel.isEmpty()) tel = encontrado.getString("TelefonoConvencional");
            String email = encontrado.getString("Email");
            return new String[]{
                ((n != null ? n : "") + " " + (ap != null ? ap : "")).trim(),
                tel   != null ? tel   : "",
                email != null ? email : ""
            };
        }
        return null; // casa sin residente activo
    }

    // Retrocompatibilidad
    public String obtenerResidentePorCasa(String numeroCasa) {
        String[] datos = obtenerDatosResidentePorCasa(numeroCasa);
        return datos != null ? datos[0] : null;
    }

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
