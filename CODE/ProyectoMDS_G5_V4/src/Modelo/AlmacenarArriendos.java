package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;

public class AlmacenarArriendos {

    private final MongoCollection<Document> coleccion;

    public AlmacenarArriendos() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("Arriendos");
    }

    // ── Document → Arriendo ───────────────────────────────────────
    private Document toDocument(Arriendo a) {
        Date fecha = Date.from(
            a.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant()
        );
        return new Document("TipoEspacio",        a.getTipoEspacio())
                .append("NumeroEspacio",      a.getNumeroEspacio())
                .append("NombreArrendatario", a.getNombreArrendatario())
                .append("TipoArrendatario",   a.getTipoArrendatario())
                .append("Contacto",           a.getContacto())
                .append("MontoMensual",       a.getMontoMensual())
                .append("MesPeriodo",         a.getMesPeriodo())
                .append("Estado",             a.getEstado())
                .append("FormaPago",          a.getFormaPago())
                .append("FechaRegistro",      fecha);
    }

    // ── Arriendo → Document ───────────────────────────────────────
    private Arriendo fromDocument(Document d) {
        Arriendo a = new Arriendo();
        a.setId(d.getObjectId("_id").toHexString());
        a.setTipoEspacio(d.getString("TipoEspacio"));
        a.setNumeroEspacio(d.getString("NumeroEspacio"));
        a.setNombreArrendatario(d.getString("NombreArrendatario"));
        a.setTipoArrendatario(d.getString("TipoArrendatario"));
        a.setContacto(d.getString("Contacto"));
        Double monto = d.getDouble("MontoMensual");
        a.setMontoMensual(monto != null ? monto : 0.0);
        a.setMesPeriodo(d.getString("MesPeriodo"));
        a.setEstado(d.getString("Estado"));
        a.setFormaPago(d.getString("FormaPago"));
        Date fecha = d.getDate("FechaRegistro");
        if (fecha != null)
            a.setFechaRegistro(fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        return a;
    }

    // ── Insertar ──────────────────────────────────────────────────
    public void guardar(Arriendo a) {
        coleccion.insertOne(toDocument(a));
    }

    // ── Obtener todos ─────────────────────────────────────────────
    public ArrayList<Arriendo> obtenerTodos() {
        ArrayList<Arriendo> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDocument(d));
        return lista;
    }

    // ── Obtener por tipo (Local / Parqueadero) ────────────────────
    public ArrayList<Arriendo> obtenerPorTipo(String tipo) {
        ArrayList<Arriendo> lista = new ArrayList<>();
        Document filtro = new Document("TipoEspacio", tipo);
        for (Document d : coleccion.find(filtro)) lista.add(fromDocument(d));
        return lista;
    }

    // ── Total recaudado por tipo ───────────────────────────────────
    public double totalRecaudadoPorTipo(String tipo) {
        double total = 0;
        Document filtro = new Document("TipoEspacio", tipo)
                          .append("Estado", new Document("$in",
                              java.util.Arrays.asList("Pagado", "Activo")));
        for (Document d : coleccion.find(filtro)) {
            Double m = d.getDouble("MontoMensual");
            if (m != null) total += m;
        }
        return total;
    }

    // ── Buscar residente por casa (para autocompletar) ────────────
    public String obtenerResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> residentes = bdd.getCollection("Residentes");
        Document encontrado = residentes.find(new Document("NumeroVivienda", numeroCasa)).first();
        if (encontrado != null) {
            String n = encontrado.getString("Nombres");
            String ap = encontrado.getString("Apellidos");
            return (n != null ? n : "") + " " + (ap != null ? ap : "");
        }
        return null;
    }
}
