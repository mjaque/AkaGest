package interfaz;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOPago;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Controlador implements Initializable {
	
	private Parent panelAlumnos;
	private Parent panelClases;
	private Parent panelPagos;
	private Parent panelResultados;
	private Parent panelConsulta;
	private Stage stageResultado = new Stage();
	private ControladorResultados controladorResultados;
	private ControladorConsulta controladorConsulta;
		
	@FXML //  fx:id="panelPrincipal"
    private BorderPane panelPrincipal; // Value injected by FXMLLoader
	
	@FXML
    private Label lblEstado; // Value injected by FXMLLoader
	
	public Controlador(){
		AkaGest.setControlador(this);
		try {
			this.panelAlumnos = FXMLLoader.load(this.getClass().getResource("fxml/Alumnos.fxml"));
			this.panelAlumnos.getStylesheets().add(this.getClass().getResource("fxml/alumnos.css").toExternalForm());
			this.panelClases = FXMLLoader.load(this.getClass().getResource("fxml/Clases.fxml"));
			//this.panelClases.getStylesheets().add(this.getClass().getResource("fxml/clases.css").toExternalForm());
			this.panelPagos = FXMLLoader.load(this.getClass().getResource("fxml/Pagos.fxml"));
			
			FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxml/Consulta.fxml"));
			this.controladorConsulta = fxmlLoader.<ControladorConsulta>getController();
			System.out.println("TRON: " + controladorConsulta);
			this.panelConsulta = fxmlLoader.load();
			
			FXMLLoader fxmlLoader2 = new FXMLLoader(this.getClass().getResource("fxml/Resultados.fxml"));
			this.panelResultados = fxmlLoader2.load();
			this.controladorResultados = fxmlLoader2.<ControladorResultados>getController();
			stageResultado.setScene(new Scene(this.panelResultados));
			
		} catch (IOException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}
	
	@FXML
	public void verAlumnos(ActionEvent event){
		this.panelPrincipal.setCenter(this.panelAlumnos);
	}
	
	@FXML
	public void verClases(ActionEvent event){
		this.panelPrincipal.setCenter(this.panelClases);
	}
	
	@FXML
	public void verPagos(ActionEvent event){
		this.panelPrincipal.setCenter(this.panelPagos);
	}
	
	@FXML
	public void salir(ActionEvent event){
		Platform.exit();
	}
	
	@FXML
	public void verResultados(ActionEvent event){
		this.controladorResultados.actualizar();
		this.stageResultado.show();
	}
	
	@FXML
	public void verArqueoCaja(ActionEvent event) throws Exception{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Arqueo de Caja");
		alert.setHeaderText(null);
		Float saldo = DAOPago.INSTANCE.verArqueoCaja();
		alert.setContentText("El saldo actual de caja debe ser de " + saldo + "€");
		alert.showAndWait();	
	}
	
	@FXML
	public void verAlumnosPorFacturacion(ActionEvent event) throws Exception{
		this.panelPrincipal.setCenter(this.panelConsulta);
		//this.panelConsulta.DAOConsultas.INSTANCE.verAlumnosPorFacturacion()
		System.out.println("TRON: " + this.controladorConsulta);
		this.controladorConsulta.setResultado("Aquí va el resultado de la consulta");
	}
	
	@FXML
	public void verAcercaDe(ActionEvent event){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Acerca de AkaGest");
		alert.setHeaderText("AkaGest " + AkaGest.VERSION + "\nCreado por Miguel Jaque (mjaque@migueljaque.com)");
		alert.setContentText("Este programa se distribuye bajo los términos de la licencia GPL v3, que puede consultarse en www.gnu.org\nPuedes acceder al código en https://github.com/mjaque/AkaGest");
		//Workarround para evitar que el texto salga cortado
		alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node-> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
		alert.showAndWait();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		verAlumnos(null);
	}
	
	public Pane getPanelPrincipal(){
		return this.panelPrincipal;
	}

	public void mostrarExcepcion(Exception e) {
		this.lblEstado.setText(e.getMessage());
	}

	public void mostrarExito(String mensaje) {
		this.lblEstado.setText(mensaje);
	}

	public void cancelar() {
		
	}

	public static void lanzarExcepcion(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error (Excepción)");
		alert.setHeaderText("Ha ocurrido un error.");
		alert.setContentText(message);
		//Workarround para evitar que el texto salga cortado
		alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node-> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
		alert.showAndWait();
		//System.exit(-1);
	}

}
