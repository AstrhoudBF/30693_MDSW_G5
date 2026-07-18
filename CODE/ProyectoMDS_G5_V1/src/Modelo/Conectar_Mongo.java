
package Modelo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Conectar_Mongo {
   public static String URL="mongodb://localhost:27017";
   public static String data="Residentes";
   public static MongoClient cliente;
   public static MongoDatabase getConec(){
       if(cliente ==null){
           cliente = MongoClients.create(URL);
       }
       return cliente.getDatabase(data);
   }
}
