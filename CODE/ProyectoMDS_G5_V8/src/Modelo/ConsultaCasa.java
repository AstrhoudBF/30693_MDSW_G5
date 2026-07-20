package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import org.bson.Document;

/**
 * Servicio que centraliza la consulta de TODOS los datos
 * asociados a un número de casa: residente, alícuotas,
 * multas y arriendos.
 */
public class ConsultaCasa {

    private final MongoDatabase bdd;

    public ConsultaCasa() {
        this.bdd = Conectar_Mongo.getConec();
    }

    // ── Residente asignado a la casa ──────────────────────────────
    public Residentes obtenerResidente(String numeroCasa) {
        AlmacenarResidentes repo = new AlmacenarResidentes();
        for (Residentes r : repo.obtenerTodos()) {
            if (numeroCasa.equals(r.getNumeroVivienda())) return r;
        }
        return null;
    }

    // ── Alícuotas de la casa ──────────────────────────────────────
    public ArrayList<Alicuota> obtenerAlicuotas(String numeroCasa) {
        ArrayList<Alicuota> lista = new ArrayList<>();
        MongoCollection<Document> col = bdd.getCollection("Alicuotas");
        for (Document d : col.find(new Document("NumeroCasa", numeroCasa))) {
            AlmacenarAlicuotas repo = new AlmacenarAlicuotas();
            // Reusamos fromDocument vía obtenerTodas + filtro
            lista.add(null); // placeholder
        }
        // Forma directa: usar el repo existente
        lista.clear();
        AlmacenarAlicuotas repoAlic = new AlmacenarAlicuotas();
        for (Alicuota a : repoAlic.obtenerTodas()) {
            if (numeroCasa.equals(a.getNumeroCasa())) lista.add(a);
        }
        return lista;
    }

    // ── Multas de la casa ─────────────────────────────────────────
    public ArrayList<Multa> obtenerMultas(String numeroCasa) {
        AlmacenarMultas repoMultas = new AlmacenarMultas();
        ArrayList<Multa> lista = new ArrayList<>();
        for (Multa m : repoMultas.obtenerTodas()) {
            if (numeroCasa.equals(m.getNumeroCasa())) lista.add(m);
        }
        return lista;
    }

    // ── Arriendos de la casa (por numeroCasaResidente) ────────────
    public ArrayList<Arriendo> obtenerArriendos(String numeroCasa) {
        AlmacenarArriendos repoArr = new AlmacenarArriendos();
        ArrayList<Arriendo> lista = new ArrayList<>();
        for (Arriendo a : repoArr.obtenerTodos()) {
            if (numeroCasa.equals(a.getNumeroCasaResidente())) lista.add(a);
        }
        return lista;
    }

    // ── Reservas de sede (por numeroCasaResidente) ────────────────
    public ArrayList<ArriendoSede> obtenerReservasSede(String numeroCasa) {
        AlmacenarArriendosSede repoSede = new AlmacenarArriendosSede();
        ArrayList<ArriendoSede> lista = new ArrayList<>();
        for (ArriendoSede s : repoSede.obtenerTodas()) {
            if (numeroCasa.equals(s.getNumeroCasaResidente())) lista.add(s);
        }
        return lista;
    }

    // ── Totales por estado ────────────────────────────────────────
    public double totalAlicuotasPorEstado(ArrayList<Alicuota> lista, String estado) {
        return lista.stream().filter(a -> estado.equals(a.getEstado())).mapToDouble(Alicuota::getMonto).sum();
    }

    public double totalMultasPorEstado(ArrayList<Multa> lista, String estado) {
        return lista.stream().filter(m -> estado.equals(m.getEstado())).mapToDouble(Multa::getMonto).sum();
    }

    public double totalArriendosPorEstado(ArrayList<Arriendo> lista, String estado) {
        return lista.stream().filter(a -> estado.equals(a.getEstado())).mapToDouble(Arriendo::getMontoMensual).sum();
    }
}
