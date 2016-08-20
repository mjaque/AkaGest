package aplicacion;

import java.io.File;
import java.net.URISyntaxException;

import interfaz.IU;
import util.Log;

//Versión 1:
//TODO: Evitar warning de css (-fx-background-color
//TODO: Bug. Limpiar el formulario de Alumno cuando no hay ninguno seleccionado. No reproducido.
//TODO: Meter en GitHub.

//Versión 2:
//TODO: View de ComboBox con buscador. Utilizarlo en Clases.Alumno.
//TODO: Diseñar e implementar Clases de Grupo y Clases Programadas. Con tablas auxiliares.
//TODO: Diseñar e implementar Lista de Espera.
//TODO: Reinicio de la base de datos.

public class AkaGest{
	
	public static void main(String[] args){
		try {
			System.setProperty("akagest.log.nivel", "TODOS");
			System.setProperty("akagest.log.salida", "CONSOLA");
			Log.log(Log.Nivel.INFO, "Iniciando Akagest");
			File dirTrabajo = new File(AkaGest.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
			Log.log(Log.Nivel.INFO, "Directorio de Trabajo: " + dirTrabajo);
			System.setProperty("com.19e37.akagest.BD_URL", "jdbc:h2:"+dirTrabajo+File.separatorChar+"akagest;MV_STORE=FALSE;MVCC=FALSE");
			Log.log(Log.Nivel.INFO, "BD_URL: " + System.getProperty("com.19e37.akagest.BD_URL"));
			IU.iniciar(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("\nImposible arracar AkaGest.\n");
		}
	}

}
