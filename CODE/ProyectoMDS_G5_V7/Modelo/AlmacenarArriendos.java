package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;

public class AlmacenarArriendos {

    private final MongoCollection<Document> coleccion;

    public AlmacenarArriendos() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("Arriendos");
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
    private java.time.LocalDateTime toLocalDateTime(Date d) {
        return d != null ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    private Document toDocument(Arriendo a) {
        return new Document("TipoEspacio",         a.getTipoEspacio())
                .append("NombreEspacio",       a.getNombreEspacio())
                .append("NombreArrendatario",  a.getNombreArrendatario())
                .append("TipoArrendatario",    a.getTipoArrendatario())
                .append("NumeroCasaResidente", a.getNumeroCasaResidente() != null ? a.getNumeroCasaResidente() : "")
                .append("Telefono",            a.getTelefono() != null ? a.getTelefono() : "")
                .append("Email",               a.getEmail() != null ? a.getEmail() : "")
                .append("MontoMensual",        a.getMontoMensual())
                .append("MesPeriodo",          a.getMesPeriodo())
                .append("Estado",              a.getEstado())
                .append("FormaPago",           a.getFormaPago())
                .append("NumeroTransaccion",   a.getNumeroTransaccion() != null ? a.getNumeroTransaccion() : "")
                .append("FechaPago",           toDate(a.getFechaPago()))
                .append("FechaRegistro",       toDate(a.getFechaRegistro()));
    }

    private Arriendo fromDocument(Document d) {
        Arriendo a = new Arriendo();
        a.setId(d.getObjectId("_id").toHexString());
        a.setTipoEspacio(d.getString("TipoEspacio"));
        // Soporte para campo viejo "NumeroEspacio" y nuevo "NombreEspacio"
        String ne = d.getString("NombreEspacio");
        if (ne == null) ne = d.getString("NumeroEspacio");
        a.setNombreEspacio(ne);
        a.setNombreArrendatario(d.getString("NombreArrendatario"));
        a.setTipoArrendatario(d.getString("TipoArrendatario"));
        a.setNumeroCasaResidente(d.getString("NumeroCasaResidente"));
        a.setTelefono(d.getString("Telefono"));
        a.setEmail(d.getString("Email"));
        Double monto = d.getDouble("MontoMensual");
        a.setMontoMensual(monto != null ? monto : 0.0);
        a.setMesPeriodo(d.getString("MesPeriodo"));
        a.setEstado(d.getString("Estado"));
        a.setFormaPago(d.getString("FormaPago"));
        a.setNumeroTransaccion(d.getString("NumeroTransaccion"));
        a.setFechaPago(toLocalDate(d.getDate("FechaPago")));
        a.setFechaRegistro(toLocalDateTime(d.getDate("FechaRegistro")));
        return a;
    }

    public void guardar(Arriendo a)          { coleccion.insertOne(toDocument(a)); }

    public void actualizar(String id, Arriendo a) {
        coleccion.replaceOne(new Document("_id", new ObjectId(id)),
                             toDocument(a), new ReplaceOptions().upsert(false));
    }

    public void eliminar(String id) {
        coleccion.deleteOne(new Document("_id", new ObjectId(id)));
    }

    public ArrayList<Arriendo> obtenerTodos() {
        ArrayList<Arriendo> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDocument(d));
        return lista;
    }

    public ArrayList<Arriendo> obtenerPorTipo(String tipo) {
        ArrayList<Arriendo> lista = new ArrayList<>();
        for (Document d : coleccion.find(new Document("TipoEspacio", tipo))) lista.add(fromDocument(d));
        return lista;
    }

    public double totalRecaudadoPorTipo(String tipo) {
        double total = 0;
        Document filtro = new Document("TipoEspacio", tipo)
                .append("Estado", new Document("$in", Arrays.asList("Pagado")));
        for (Document d : coleccion.find(filtro)) {
            Double m = d.getDouble("MontoMensual");
            if (m != null) total += m;
        }
        return total;
    }

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
