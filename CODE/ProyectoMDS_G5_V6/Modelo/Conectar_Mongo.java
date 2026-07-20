
package Modelo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Conectar_Mongo {
    public static String URL = "mongodb://localhost:27017";
    // El nombre de toda la base de datos, NO de la colección
    public static String data = "FortinDelValleDB"; 
    public static MongoClient cliente;
   
    public static MongoDatabase getConec(){
        if(cliente == null){
            cliente = MongoClients.create(URL);
        }
        return cliente.getDatabase(data);
    }
}