package interfaz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class IU extends Application{
	private static Controlador controlador;
	
	public static void iniciar(String[] args){
		Application.launch(args);
	}

	@Override
	public void start(Stage escenario) throws Exception {
		try {
			escenario.setTitle("Gestión de Academia - 19e37");
			
			//Esta llamada establece el controlador principal.
			Pane panelPrincipal = FXMLLoader.load(this.getClass().getResource("fxml/Interfaz.fxml"));
			Scene escena = new Scene(panelPrincipal);
			escena.getStylesheets().add(getClass().getResource("fxml/interfaz.css").toExternalForm());
			escenario.setScene(escena);
			escenario.show();
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
		
	}
	
	public static void setControlador(Controlador controlador){
		IU.controlador = controlador;
	}
	
	public static Controlador getControlador(){
		return IU.controlador; 
	}

}
