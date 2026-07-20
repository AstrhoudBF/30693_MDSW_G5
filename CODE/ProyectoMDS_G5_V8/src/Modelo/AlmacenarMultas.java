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

    private Date toDate(LocalDate d) {
        return d != null ? Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
    }
    private Date toDate(LocalDateTime d) {
        return d != null ? Date.from(d.atZone(ZoneId.systemDefault()).toInstant()) : null;
    }
    private LocalDate toLocalDate(Date d) {
        return d != null ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
    private LocalDateTime toLocalDateTime(Date d) {
        return d != null ? d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    private Document toDoc(Multa m) {
        return new Document("NumeroCasa",       m.getNumeroCasa())
                .append("CedulaResidente",  m.getCedulaResidente())
                .append("NombreResidente",  m.getNombreResidente())
                .append("Categoria",        m.getCategoria())
                .append("Motivo",           m.getMotivo())
                .append("FechaInfraccion",  toDate(m.getFechaInfraccion()))
                .append("Monto",            m.getMonto())
                .append("Estado",           m.getEstado())
                .append("FormaPago",        m.getFormaPago() != null ? m.getFormaPago() : "")
                .append("NumeroTransaccion", m.getNumeroTransaccion() != null ? m.getNumeroTransaccion() : "")
                .append("FechaPago",        toDate(m.getFechaPago()))
                .append("Observaciones",    m.getObservaciones())
                .append("FechaRegistro",    toDate(m.getFechaRegistro()));
    }

    private Multa fromDoc(Document d) {
        Multa m = new Multa();
        m.setId(d.getObjectId("_id").toHexString());
        m.setNumeroCasa(d.getString("NumeroCasa"));
        m.setCedulaResidente(d.getString("CedulaResidente"));
        m.setNombreResidente(d.getString("NombreResidente"));
        m.setCategoria(d.getString("Categoria"));
        m.setMotivo(d.getString("Motivo"));
        m.setFechaInfraccion(toLocalDate(d.getDate("FechaInfraccion")));
        Double monto = d.getDouble("Monto");
        m.setMonto(monto != null ? monto : 0.0);
        m.setEstado(d.getString("Estado"));
        m.setFormaPago(d.getString("FormaPago"));
        m.setNumeroTransaccion(d.getString("NumeroTransaccion"));
        m.setFechaPago(toLocalDate(d.getDate("FechaPago")));
        m.setObservaciones(d.getString("Observaciones"));
        m.setFechaRegistro(toLocalDateTime(d.getDate("FechaRegistro")));
        return m;
    }

    public void guardar(Multa m)  { coleccion.insertOne(toDoc(m)); }

    public void actualizar(String id, Multa m) {
        coleccion.replaceOne(new Document("_id", new ObjectId(id)),
                             toDoc(m), new ReplaceOptions().upsert(false));
    }

    public void eliminar(String id) {
        coleccion.deleteOne(new Document("_id", new ObjectId(id)));
    }

    public ArrayList<Multa> obtenerTodas() {
        ArrayList<Multa> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDoc(d));
        return lista;
    }

    public ArrayList<Multa> multasPendientesPorCasa(String numeroCasa) {
        ArrayList<Multa> lista = new ArrayList<>();
        Document filtro = new Document("NumeroCasa", numeroCasa).append("Estado", "Pendiente");
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        return lista;
    }

    public ArrayList<Multa> multasPendientesPorCedula(String cedula) {
        ArrayList<Multa> lista = new ArrayList<>();
        Document filtro = new Document("CedulaResidente", cedula).append("Estado", "Pendiente");
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        return lista;
    }

    public double totalPendientePorCasa(String numeroCasa) {
        double total = 0;
        for (Multa m : multasPendientesPorCasa(numeroCasa)) total += m.getMonto();
        return total;
    }

    public String[] obtenerResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> res = bdd.getCollection("Residentes");
        Document encontrado = res.find(new Document("NumeroVivienda", numeroCasa)).first();
        if (encontrado != null) {
            String n = encontrado.getString("Nombres");
            String a = encontrado.getString("Apellidos");
            String c = encontrado.getString("Cedula");
            return new String[]{ ((n!=null?n:"")+" "+(a!=null?a:"")).trim(), c!=null?c:"" };
        }
        return null;
    }

    public String[] obtenerResidentePorCedula(String cedula) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> res = bdd.getCollection("Residentes");
        Document encontrado = res.find(new Document("Cedula", cedula)).first();
        if (encontrado != null) {
            String n = encontrado.getString("Nombres");
            String a = encontrado.getString("Apellidos");
            String c = encontrado.getString("NumeroVivienda");
            return new String[]{ ((n!=null?n:"")+" "+(a!=null?a:"")).trim(), c!=null?c:"" };
        }
        return null;
    }
}
