package aplicacion;

import java.io.File;

import interfaz.Controlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import util.Log;

//Versión:
//TODO: En el interfaz principal, en clases, añadir columna calculada con el id del pago correspondiente
//TODO: Evitar warning de css (-fx-background-color
//TODO: Bug. Al cerrar la app, cerrar los posibles diálogos abiertos.


public class AkaGest extends Application{
	
	public static final String VERSION = "4.3";
	
	private static Controlador controlador;
	
	public static void main(String[] args){
		try {
			System.setProperty("akagest.log.nivel", "TODOS");
			System.setProperty("akagest.log.salida", "CONSOLA");
			Log.log(Log.Nivel.INFO, "Iniciando Akagest");
			File dirTrabajo = new File(AkaGest.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
			Log.log(Log.Nivel.INFO, "Directorio de Trabajo: " + dirTrabajo);
			System.setProperty("com.19e37.akagest.BD_URL", "jdbc:h2:"+dirTrabajo+File.separatorChar+"akagest;MV_STORE=FALSE;MVCC=FALSE");
			Log.log(Log.Nivel.INFO, "BD_URL: " + System.getProperty("com.19e37.akagest.BD_URL"));
			Application.launch(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("\nImposible arracar AkaGest.\n");
		}
	}
	
	@Override
	public void start(Stage escenario) throws Exception {
		try {
			escenario.setTitle("Gestión de Academia - 19e37");
			
			//Esta llamada establece el controlador principal.
			Pane panelPrincipal = FXMLLoader.load(this.getClass().getResource("/interfaz/fxml/Interfaz.fxml"));
			Scene escena = new Scene(panelPrincipal);
			escena.getStylesheets().add(getClass().getResource("/interfaz/fxml/interfaz.css").toExternalForm());
			escenario.setScene(escena);
			escenario.show();
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}
	
	public static void setControlador(Controlador controlador){
		AkaGest.controlador = controlador;
	}
	
	public static Controlador getControlador(){
		return AkaGest.controlador; 
	}

}
