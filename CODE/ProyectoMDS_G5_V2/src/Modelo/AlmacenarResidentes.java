package Modelo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class AlmacenarResidentes {

    public static MongoCollection<Document> columnas;

    public AlmacenarResidentes() {
        MongoDatabase bdd = Conectar_Mongo.getConec();
        this.columnas = bdd.getCollection("Residentes");
    }

    private Document agregarDoc(Residentes r) {
        return new Document("Nombres", r.getNombres())
                .append("Apellidos", r.getApellidos())
                .append("Cedula", r.getCedula())
                .append("TelefonoMovil", r.getTelefonoMovil())
                .append("TelefonoConvencional", r.getTelefonoConvencional())
                .append("NumeroVivienda", r.getNumeroVivienda())
                .append("Direccion", r.getDireccion())
                .append("TieneMascotas", r.isTieneMascotas())
                .append("NumeroVehiculos", r.getNumeroVehiculos())
                .append("NumeroPersonas", r.getNumeroPersonas())
                .append("NombresResidentes", r.getNombresResidentes());
    }

    private Residentes sacarDocumento(Document d) {
        return new Residentes(
                d.getString("Nombres"),
                d.getString("Apellidos"),
                d.getString("Cedula"),
                d.getString("TelefonoMovil"),
                d.getString("TelefonoConvencional"),
                d.getString("NumeroVivienda"),
                d.getString("Direccion"),
                Boolean.TRUE.equals(d.getBoolean("TieneMascotas")),
                d.getString("NumeroVehiculos"),
                d.getString("NumeroPersonas"),
                d.getString("NombresResidentes"),
                "" // apellidosResidentes ya no se usa por separado
        );
    }

    public ArrayList<Residentes> obtenerTodos() {
        ArrayList<Residentes> lista = new ArrayList<>();
        for (Document d : columnas.find()) {
            lista.add(sacarDocumento(d));
        }
        return lista;
    }

    public void agregarMongo(Residentes r) {
        columnas.insertOne(agregarDoc(r));
    }

    public final List<Residentes> lista = new ArrayList<>();

    public void guardar(Residentes r) {
        lista.add(r);
    }

    public List<Residentes> listar() {
        return lista;
    }
}