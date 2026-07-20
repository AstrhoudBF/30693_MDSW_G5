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

    /** Anula una reserva cambiando el estado a "Cancelada" sin eliminar el registro */
    public void anular(String id) {
        coleccion.updateOne(
            new Document("_id", new ObjectId(id)),
            new Document("$set", new Document("Estado", "Cancelada"))
        );
    }

    public ArrayList<ArriendoSede> obtenerTodas() {
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find()) lista.add(fromDoc(d));
        return lista;
    }

    public String verificarChoque(LocalDate fecha) {
        return verificarChoqueExcluyendo(fecha, "Día Completo", "", "", null);
    }

    /**
     * Verifica si la nueva reserva choca con alguna ya existente (Confirmada/Pendiente)
     * en la misma fecha, considerando modalidad y rangos horarios.
     *
     * Reglas:
     *  - Si la nueva reserva es "Día Completo", choca con cualquier otra reserva
     *    del mismo día (sea "Día Completo" u "Por Horas"), porque ocupa todo el día.
     *  - Si la nueva reserva es "Por Horas", choca si existe una "Día Completo"
     *    o si hay otra "Por Horas" con rango horario que se solape
     *    (inicioNueva < finExistente  y  finNueva > inicioExistente).
     *  - La comparación de horas usa el formato "HH:mm", que es lexicográficamente
     *    correcto para rangos de 24 h.
     */
    public String verificarChoqueExcluyendo(LocalDate fecha, String modalidadNueva,
                                            String horaIniNueva, String horaFinNueva,
                                            String idExcluir) {
        Date fechaBuscar = toDate(fecha);
        Document filtro = new Document("FechaReserva", fechaBuscar)
                          .append("Estado", new Document("$in", Arrays.asList("Confirmada", "Pendiente")));
        boolean nuevaPorHoras = "Por Horas".equals(modalidadNueva);
        for (Document d : coleccion.find(filtro)) {
            String id = d.getObjectId("_id").toHexString();
            if (idExcluir != null && idExcluir.equals(id)) continue;
            String modExist = d.getString("Modalidad");
            String nombre   = d.getString("NombreSolicitante");

            // 1) Si la EXISTENTE es "Día Completo", bloquea cualquier nueva reserva del día.
            if ("Día Completo".equals(modExist)) {
                return nombre;
            }
            // 2) Si la NUEVA es "Día Completo", choca con cualquier "Por Horas" existente
            //    del mismo día (porque ocuparía el día entero).
            if ("Día Completo".equals(modalidadNueva) && "Por Horas".equals(modExist)) {
                return nombre;
            }
            // 3) Ambas "Por Horas": choque por solape de rangos.
            if (nuevaPorHoras && "Por Horas".equals(modExist)) {
                String hI = d.getString("HoraInicio");
                String hF = d.getString("HoraFin");
                if (hI != null && hF != null
                        && horaIniNueva.compareTo(hF) < 0
                        && horaFinNueva.compareTo(hI) > 0) {
                    return nombre;
                }
            }
        }
        return null;
    }

    public ArrayList<ArriendoSede> reservasProximas() {
        Date hoy = toDate(LocalDate.now());
        Document filtro = new Document("FechaReserva", new Document("$gte", hoy))
                          .append("Estado", new Document("$in", Arrays.asList("Confirmada", "Pendiente")));
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (Document d : coleccion.find(filtro)) lista.add(fromDoc(d));
        // Ordenar: primero por fecha ascendente, luego por hora de inicio ascendente.
        // Las reservas "Día Completo" (sin hora) se muestran antes de las "Por Horas" del mismo día.
        lista.sort((a, b) -> {
            int cmpFecha = a.getFechaReserva().compareTo(b.getFechaReserva());
            if (cmpFecha != 0) return cmpFecha;
            String hA = a.getHoraInicio() == null ? "" : a.getHoraInicio();
            String hB = b.getHoraInicio() == null ? "" : b.getHoraInicio();
            // Cadenas vacías van primero (Día Completo antes que Por Horas).
            if (hA.isEmpty() && !hB.isEmpty()) return -1;
            if (!hA.isEmpty() && hB.isEmpty()) return  1;
            return hA.compareTo(hB);
        });
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

    /**
     * Devuelve {nombre, telefono, email} del residente ACTIVO de una casa,
     * o null si la casa no tiene residente activo.
     */
    public String[] obtenerDatosResidentePorCasa(String numeroCasa) {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        MongoCollection<Document> residentes = bdd.getCollection("Residentes");
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
        return null;
    }
}
