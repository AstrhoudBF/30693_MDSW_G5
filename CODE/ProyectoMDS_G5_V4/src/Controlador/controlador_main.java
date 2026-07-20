package Controlador;

import Modelo.login;
import Vista.Login;
import Vista.interfaz_login;

public class controlador_main {

    public static void main(String[] args) {

        login modeloL = new login("pablochisaguano1", "pablo1234");

        // 2. Instanciamos la VISTA
        interfaz_login vistaL = new Login();

        // 3. Unimos todo en el CONTROLADOR
        controlador_login ctrlL = new controlador_login(modeloL, vistaL);

        // 4. Arrancamos el programa
        ctrlL.iniciar();
    }

}
