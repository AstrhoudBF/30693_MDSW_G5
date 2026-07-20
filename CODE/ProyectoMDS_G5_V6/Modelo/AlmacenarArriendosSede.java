package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.bson.Document;

public class AlmacenarArriendosSede {

    private final MongoCollection<Document> coleccion;

    public AlmacenarArriendosSede() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.coleccion = bdd.getCollection("ArriendosSede");
    }

    // ── ArriendoSede → Document ───────────────────────────────────
    private Document toDocument(ArriendoSede s) {
        Date fechaRes = Date.from(
            s.getFechaReserva().atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Date fechaReg = Date.from(
            s.getFechaRegistro().atZone(ZoneId.systemDefault()).toInstant()
        );
        return new Document("NombreSolicitante", s.getNombreSolicitante())
                .append("TipoSolicitante",  s.getTipoSolicitante())
                .append("Contacto",         s.getContacto())
                .append("FechaReserva",     fechaRes)
                .append("Modalidad",        s.getModalidad())
                .append("HoraInicio",       s.getHoraInicio())
                .append("HoraFin",          s.getHoraFin())
                .append("Monto",            s.getMonto())
                .append("Estado",           s.getEstado())
                .append("FormaPago",        s.getFormaPago())
                .append("Motivo",           s.getMotivo())
                .append("FechaRegistro",    fechaReg);
    }

    // ── Document → ArriendoSede ───────────────────────────────────
    private ArriendoSede fromDocument(Document d) {
        ArriendoSede s = new ArriendoSede();
        s.setId(d.getObjectId("_id").toHexString());
        s.setNombreSolicitante(d.getString("NombreSolicitante"));
        s.setTipoSolicitante(d.getString("TipoSolicitante"));
        s.setContacto(d.getString("Contacto"));

        Date fechaRes = d.getDate("FechaReserva");
        if (fechaRes != null)
            s.setFechaReserva(fechaRes.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        s.setModalidad(d.getString("Modalidad"));
        s.setHoraInicio(d.getString("HoraInicio"));
        s.setHoraFin(d.getString("HoraFin"));
        Double monto = d.getDouble("Monto");
        s.setMonto(monto != null ? monto : 0.0);
        s.setEstado(d.getString("Estado"));
        s.setFormaPago(d.getString("FormaPago"));
        s.setMotivo(d.getString("Motivo"));

        Date fechaReg = d.getDate("FechaRegistro");
        if (fechaReg != null)
            s.setFechaRegistro(fechaReg.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        return s;
    }

    // ── Insertar ──────────────────────────────────────────────────
    public void guardar(ArriendoSede s) {
        coleccion.insertOne(toDocument(s));
    }

    // ── Obtener todas ─────────────────────────────────────────────
    public ArrayList<ArriendoSede> obtenerTodas() {
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDocument(d));
        return lista;
    }

    // ── Verificar choque de fechas ────────────────────────────────
    // Retorna el nombre del solicitante si ya existe una reserva en esa fecha
    // con estado Confirmada o Pendiente. Null si está libre.
    public String verificarChoque(LocalDate fecha) {
        Date fechaBuscar = Date.from(
            fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Document filtro = new Document("FechaReserva", fechaBuscar)
                          .append("Estado", new Document("$in",
                              java.util.Arrays.asList("Confirmada", "Pendiente")));
        Document encontrado = coleccion.find(filtro).first();
        if (encontrado != null)
            return encontrado.getString("NombreSolicitante");
        return null;
    }

    // ── Reservas próximas (a partir de hoy, activas) ──────────────
    public ArrayList<ArriendoSede> reservasProximas() {
        Date hoy = Date.from(
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
        Document filtro = new Document("FechaReserva",
                              new Document("$gte", hoy))
                          .append("Estado", new Document("$in",
                              java.util.Arrays.asList("Confirmada", "Pendiente")));
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find(filtro)) lista.add(fromDocument(d));
        return lista;
    }

    // ── Total recaudado sede ──────────────────────────────────────
    public double totalRecaudado() {
        double total = 0;
        Document filtro = new Document("Estado", new Document("$in",
                              java.util.Arrays.asList("Confirmada", "Pagado")));
        for (Document d : coleccion.find(filtro)) {
            Double m = d.getDouble("Monto");
            if (m != null) total += m;
        }
        return total;
    }
}
