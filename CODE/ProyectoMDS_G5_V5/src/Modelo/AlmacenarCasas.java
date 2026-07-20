/*package Modelo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class AlmacenarCasas {
    private MongoCollection<Document> coleccionCasas;

    public AlmacenarCasas(Conectar_Mongo conexion) {
        // Asumiendo que tu clase Conectar_Mongo tiene un método para obtener la colección
        this.coleccionCasas = conexion.getDatabase().getCollection("casas");
    }

    public void inicializarCasas() {
        long cantidad = coleccionCasas.countDocuments();

        if (cantidad == 0) {
            System.out.println("Base de datos vacía. Generando casas iniciales...");
            
            // Generamos las 15 casas estáticas
            List<Document> casasNuevas = new ArrayList<>();
            for (int i = 1; i <= 15; i++) {
                Document casa = new Document("numCasa", i);
                // Nota: No guardamos la lista de residentes aquí en MongoDB.
                // Como acordamos, la relación se hará desde la colección de Residentes.
                casasNuevas.add(casa);
            }
            
            coleccionCasas.insertMany(casasNuevas);
            System.out.println("15 casas registradas exitosamente.");
        } else {
            System.out.println("Las casas ya se encuentran inicializadas en el sistema.");
        }
    }
}*/