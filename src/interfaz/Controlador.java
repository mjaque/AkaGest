package interfaz;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOAlumno;
import datos.DAOConsulta;
import datos.DAOPago;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import negocio.Alumno;

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
			//this.panelConsulta = FXMLLoader.load(this.getClass().getResource("fxml/Consulta.fxml"));
			
			FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxml/Consulta.fxml"));
			this.panelConsulta = fxmlLoader.load();
			this.controladorConsulta = fxmlLoader.<ControladorConsulta>getController();
			
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
		this.controladorConsulta.setResultado(DAOConsulta.INSTANCE.verAlumnosPorFacturacion());
	}
	
	@FXML
	public void verAsignaturasPorFacturacion(ActionEvent event) throws Exception{
		this.panelPrincipal.setCenter(this.panelConsulta);
		this.controladorConsulta.setResultado(DAOConsulta.INSTANCE.verAsignaturasPorFacturacion());
	}
	
	@FXML
	public void verHorariosPorFacturacion(ActionEvent event) throws Exception{
		this.panelPrincipal.setCenter(this.panelConsulta);
		this.controladorConsulta.setResultado(DAOConsulta.INSTANCE.verHorariosPorFacturacion());
	}
	
	@FXML
	public void verHorasPorFacturacion(ActionEvent event) throws Exception{
		this.panelPrincipal.setCenter(this.panelConsulta);
		this.controladorConsulta.setResultado(DAOConsulta.INSTANCE.verHorasPorFacturacion());
	}
	
	@FXML
	public void verDiasSemanaPorFacturacion(ActionEvent event) throws Exception{
		System.out.println("Días Semana");
		this.panelPrincipal.setCenter(this.panelConsulta);
		this.controladorConsulta.setResultado(DAOConsulta.INSTANCE.verDiasSemanaPorFacturacion());
	}
	
	@FXML
	public void verListadoAlumnosActivos(ActionEvent event) throws Exception{
		System.out.println("verListadoAlumnosActivos");
		ArrayList<Alumno> alumnosActivos = DAOAlumno.INSTANCE.listar(true);
		StringBuilder sbCSV = new StringBuilder();
		for(Alumno alumno : alumnosActivos) {
			sbCSV.append('\"' + alumno.getNombreCompleto() + '\"');
			sbCSV.append("\n");
		}
		Files.write(Paths.get("/tmp/listaAlumnosActivos.csv"), sbCSV.toString().getBytes(StandardCharsets.UTF_8));
		//TODO: Lanzar LibreOffice calc
		Runtime.getRuntime().exec("libreoffice --calc /tmp/listaAlumnosActivos.csv");
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
