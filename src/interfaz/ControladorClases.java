package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOAlumno;
import datos.DAOAsignatura;
import datos.DAOClase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import negocio.Alumno;
import negocio.Asignatura;
import negocio.Clase;

public class ControladorClases implements Actualizable, Initializable, ChangeListener<Clase>, EventHandler<MouseEvent> {

	private Controlador controladorPrincipal;
	private ObservableList<Clase> olClases;
	private ObservableList<Alumno> olAlumnos;
	private ObservableList<Asignatura> olAsignaturas;
	private ObservableList<Clase.Estado> olEstados;

	// Componentes del Subpanel superior - Tabla de Alumnos
	@FXML
	TableView<Clase> tvClases;
	@FXML
	TableColumn<Clase, Integer> tcClaseId;
	@FXML
	TableColumn<Clase, LocalDate> tcClaseFecha;
	@FXML
	TableColumn<Clase, LocalTime> tcClaseHora;
	@FXML
	TableColumn<Clase, Alumno> tcClaseAlumno;
	@FXML
	TableColumn<Clase, Asignatura> tcClaseAsignatura;
	@FXML
	TableColumn<Clase, String> tcClaseObservaciones;
	@FXML
	TableColumn<Clase, Float> tcClaseDuracion, tcClasePrecioHora;
	@FXML
	TableColumn<Clase, Boolean> tcClaseAsistencia;
	@FXML
	TableColumn<Clase, Clase.Estado> tcClaseEstado;
	@FXML
	private Button btnClasesBorrar, btnClasesInsertar, btnClaseCopiar;

	public ControladorClases() throws Exception {
		super();
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.getLista());
		olAsignaturas = FXCollections.observableArrayList(DAOAsignatura.INSTANCE.getLista());
		olEstados = FXCollections.observableArrayList(Clase.Estado.values());
	}

	@FXML
	protected void insertarClase(ActionEvent event) throws Exception {
		this.abrirVentanaClase(null);
	}

	@FXML
	protected void borrarClase(ActionEvent event) throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmación de Borrado");
		alert.setHeaderText("¿Seguro que quiere BORRAR esta clase?");
		alert.setContentText("Esta acción no puede deshacerse");
		Optional<ButtonType> resultado = alert.showAndWait();
		if (resultado.get() == ButtonType.OK) {
			this.tvClases.getSelectionModel().getSelectedItem().borrar();
			this.controladorPrincipal.mostrarExito("La clase ha sido borrada.");
			actualizarClases();
		}
	}

	@FXML
	protected void copiarClase(ActionEvent event) throws Exception {
		ChoiceDialog<Alumno> dialog = new ChoiceDialog<>(null, DAOAlumno.INSTANCE.listar(true));
		dialog.setSelectedItem(this.tvClases.getSelectionModel().getSelectedItem().getAlumno());
		dialog.setTitle("Elección de Alumno");
		dialog.setHeaderText(null);
		dialog.setContentText("Elija el Alumno al que copiar la clase seleccionada:");
		Optional<Alumno> resultado = dialog.showAndWait();
		resultado.ifPresent(alumno -> {
			try {
				Clase clase = tvClases.getSelectionModel().getSelectedItem();
				Clase nuevaClase = clase.copiar();
				nuevaClase.setAlumno(alumno);
				nuevaClase.guardar();
				actualizarClases();
				controladorPrincipal.mostrarExito("La clase de copió con éxito");
			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			this.controladorPrincipal = AkaGest.getControlador();

			tcClaseId.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("id"));
			tcClaseFecha.setCellValueFactory(new PropertyValueFactory<Clase, LocalDate>("fecha"));
			tcClaseHora.setCellValueFactory(new PropertyValueFactory<Clase, LocalTime>("hora"));
			tcClaseAlumno.setCellValueFactory(new PropertyValueFactory<Clase, Alumno>("alumno"));
			olAsignaturas.add(new Asignatura("Nueva Asignatura"));
			tcClaseAsignatura.setCellValueFactory(new PropertyValueFactory<Clase, Asignatura>("asignatura"));
			tcClaseDuracion.setCellValueFactory(new PropertyValueFactory<Clase, Float>("duracion"));
			tcClasePrecioHora.setCellValueFactory(new PropertyValueFactory<Clase, Float>("precioHora"));
			tcClaseEstado.setCellValueFactory(new PropertyValueFactory<Clase, Clase.Estado>("estado"));
			tcClaseAsistencia.setCellValueFactory(new PropertyValueFactory<Clase, Boolean>("asistencia"));
			tcClaseObservaciones.setCellValueFactory(new PropertyValueFactory<Clase, String>("notas"));

			//tvClases.getSelectionModel().selectedItemProperty().addListener(this);
			tvClases.setOnMousePressed(this);

			this.actualizarClases();

		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Clase> observable, Clase oldValue, Clase newValue) {
		Clase clase = tvClases.getSelectionModel().getSelectedItem();
	}

	@Override
	public void handle(MouseEvent event) {
		try {
			if (event.getSource().equals(tvClases)) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2)
					this.abrirVentanaClase((Clase) tvClases.getSelectionModel().getSelectedItem());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void actualizar(Object object) throws Exception {
		actualizarClases();
	}
	
	public void actualizarClases() throws Exception{
		if (olClases != null)
			olClases.removeAll(olClases);
		olClases = FXCollections.observableArrayList(DAOClase.INSTANCE.listar());
		tvClases.setItems(olClases);
	}
	
	public void abrirVentanaClase(Clase clase) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxml/Clase.fxml"));
		Parent root = fxmlLoader.load();
		// root.getStylesheets().add(this.getClass().getResource("fxml/pago.css").toExternalForm());
		ControladorClase controladorClase = fxmlLoader.<ControladorClase>getController();
		controladorClase.setClase(clase);
		controladorClase.setControladorLlamante(this);
		Stage stage = new Stage();
		if (clase == null)
			stage.setTitle("Nueva Clase");
		else
			stage.setTitle("Edición de la Clase " + clase.getId());
		stage.setScene(new Scene(root));
		stage.show();
	}
}
