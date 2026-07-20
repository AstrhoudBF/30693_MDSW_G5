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

public class AlmacenarArriendosSede {

    private final MongoCollection<Document> coleccion;

    public AlmacenarArriendosSede() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("ArriendosSede");
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

    private Document toDoc(ArriendoSede s) {
        return new Document("NombreSolicitante",   s.getNombreSolicitante())
                .append("TipoSolicitante",         s.getTipoSolicitante())
                .append("NumeroCasaResidente",      s.getNumeroCasaResidente() != null ? s.getNumeroCasaResidente() : "")
                .append("Telefono",                 s.getTelefono() != null ? s.getTelefono() : "")
                .append("Email",                    s.getEmail() != null ? s.getEmail() : "")
                .append("FechaReserva",             toDate(s.getFechaReserva()))
                .append("Modalidad",                s.getModalidad())
                .append("HoraInicio",               s.getHoraInicio())
                .append("HoraFin",                  s.getHoraFin())
                .append("Monto",                    s.getMonto())
                .append("Estado",                   s.getEstado())
                .append("FormaPago",                s.getFormaPago())
                .append("NumeroTransaccion",        s.getNumeroTransaccion() != null ? s.getNumeroTransaccion() : "")
                .append("FechaPago",                toDate(s.getFechaPago()))
                .append("Motivo",                   s.getMotivo())
                .append("FechaRegistro",            toDate(s.getFechaRegistro()));
    }

    private ArriendoSede fromDoc(Document d) {
        ArriendoSede s = new ArriendoSede();
        s.setId(d.getObjectId("_id").toHexString());
        s.setNombreSolicitante(d.getString("NombreSolicitante"));
        s.setTipoSolicitante(d.getString("TipoSolicitante"));
        s.setNumeroCasaResidente(d.getString("NumeroCasaResidente"));
        s.setTelefono(d.getString("Telefono"));
        s.setEmail(d.getString("Email"));
        s.setFechaReserva(toLocalDate(d.getDate("FechaReserva")));
        s.setModalidad(d.getString("Modalidad"));
        s.setHoraInicio(d.getString("HoraInicio"));
        s.setHoraFin(d.getString("HoraFin"));
        Double monto = d.getDouble("Monto");
        s.setMonto(monto != null ? monto : 0.0);
        s.setEstado(d.getString("Estado"));
        s.setFormaPago(d.getString("FormaPago"));
        s.setNumeroTransaccion(d.getString("NumeroTransaccion"));
        s.setFechaPago(toLocalDate(d.getDate("FechaPago")));
        s.setMotivo(d.getString("Motivo"));
        s.setFechaRegistro(toLocalDateTime(d.getDate("FechaRegistro")));
        return s;
    }

    public void guardar(ArriendoSede s)           { coleccion.insertOne(toDoc(s)); }

    public void actualizar(String id, ArriendoSede s) {
        coleccion.replaceOne(new Document("_id", new ObjectId(id)),
                             toDoc(s), new ReplaceOptions().upsert(false));
    }

    public ArrayList<ArriendoSede> obtenerTodas() {
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDoc(d));
        return lista;
    }

    public String verificarChoque(LocalDate fecha) {
        return verificarChoqueExcluyendo(fecha, null);
    }

    /** Verifica choque excluyendo el ID de la reserva que se está editando. */
    public String verificarChoqueExcluyendo(LocalDate fecha, String idExcluir) {
        Date fechaBuscar = toDate(fecha);
        Document filtro = new Document("FechaReserva", fechaBuscar)
                          .append("Estado", new Document("$in", Arrays.asList("Confirmada", "Pendiente")));
        for (Document d : coleccion.find(filtro)) {
            String id = d.getObjectId("_id").toHexString();
            if (idExcluir != null && idExcluir.equals(id)) continue;
            return d.getString("NombreSolicitante");
        }
        return null;
    }

    public ArrayList<ArriendoSede> reservasProximas() {
        Date hoy = toDate(LocalDate.now());
        Document filtro = new Document("FechaReserva", new Document("$gte", hoy))
                          .append("Estado", new Document("$in", Arrays.asList("Confirmada", "Pendiente")));
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        return lista;
    }

    public double totalRecaudado() {
        double total = 0;
        Document filtro = new Document("Estado", new Document("$in", Arrays.asList("Confirmada", "Pagado")));
        for (Document d : coleccion.find(filtro)) {
            Double m = d.getDouble("Monto");
            if (m != null) total += m;
        }
        return total;
    }
}
