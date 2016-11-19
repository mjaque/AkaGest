package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOAlumno;
import datos.DAOPago;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import negocio.Alumno;
import negocio.Pago;

public class ControladorPagos implements Actualizable, Initializable, ChangeListener<Pago>, EventHandler<MouseEvent> {

	private Controlador controladorPrincipal;
	private ObservableList<Pago> olPagos;
	private ObservableList<Alumno> olAlumnos;
	private ObservableList<Pago.Operacion> olOperaciones = FXCollections.observableArrayList(Pago.Operacion.values());

	@FXML
	TableView<Pago> tvPagos;
	@FXML
	TableColumn<Pago, Integer> tcPagoId;
	@FXML
	TableColumn<Pago, LocalDate> tcPagoFecha;
	@FXML
	TableColumn<Pago, Pago.Operacion> tcPagoOperacion;
	@FXML
	TableColumn<Pago, Float> tcPagoImporte;
	@FXML
	TableColumn<Pago, Alumno> tcPagoAlumno;
	@FXML
	TableColumn<Pago, String> tcPagoRecibo, tcPagoObservaciones;
	@FXML
	Button btnPagosBorrar, btnPagosInsertar;

	public ControladorPagos() throws Exception {
		super();
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.getLista());
		olOperaciones = FXCollections.observableArrayList(Pago.Operacion.values());
	}

	@FXML
	protected void insertarPago(ActionEvent event) throws Exception {
		this.abrirVentanaPago(null);
	}

	public void abrirVentanaPago(Pago pago) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("fxml/Pago.fxml"));
		Parent root = fxmlLoader.load();
		// root.getStylesheets().add(this.getClass().getResource("fxml/pago.css").toExternalForm());
		ControladorPago controladorPago = fxmlLoader.<ControladorPago>getController();
		controladorPago.setPago(pago);
		controladorPago.setControladorLlamante(this);
		Stage stage = new Stage();
		if (pago == null)
			stage.setTitle("Nuevo Pago");
		else
			stage.setTitle("Edición de Pago " + pago.getId());
		stage.setScene(new Scene(root));
		stage.show();
	}

	@FXML
	protected void borrarPago(ActionEvent event) throws Exception {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmación de Borrado");
		alert.setHeaderText("¿Seguro que quiere BORRAR este pago?");
		alert.setContentText("Esta acción no puede deshacerse");
		Optional<ButtonType> resultado = alert.showAndWait();
		if (resultado.get() == ButtonType.OK) {
			this.tvPagos.getSelectionModel().getSelectedItem().borrar();
			this.controladorPrincipal.mostrarExito("El pago ha sido borrado.");
			actualizarPagos();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			this.controladorPrincipal = AkaGest.getControlador();

			tcPagoId.setCellValueFactory(new PropertyValueFactory<Pago, Integer>("id"));
			tcPagoFecha.setCellValueFactory(new PropertyValueFactory<Pago, LocalDate>("fecha"));
			tcPagoOperacion.setCellValueFactory(new PropertyValueFactory<Pago, Pago.Operacion>("operacion"));
			tcPagoImporte.setCellValueFactory(new PropertyValueFactory<Pago, Float>("importe"));
			tcPagoAlumno.setCellValueFactory(new PropertyValueFactory<Pago, Alumno>("alumno"));
			tcPagoRecibo.setCellValueFactory(new PropertyValueFactory<Pago, String>("recibo"));
			tcPagoObservaciones.setCellValueFactory(new PropertyValueFactory<Pago, String>("notas"));

			//tvPagos.getSelectionModel().selectedItemProperty().addListener(this);
			tvPagos.setOnMousePressed(this);

			actualizarPagos();

		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Pago> observable, Pago oldValue, Pago newValue) {
		Pago pago = tvPagos.getSelectionModel().getSelectedItem();
	}

	@Override
	public void handle(MouseEvent event) {
		try {
			if (event.getSource().equals(tvPagos)) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2)
					this.abrirVentanaPago((Pago) tvPagos.getSelectionModel().getSelectedItem());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	private void actualizarPagos() throws Exception {
		if (olPagos != null)
			olPagos.removeAll(olPagos);
		olPagos = FXCollections.observableArrayList(DAOPago.INSTANCE.listar());
		tvPagos.setItems(olPagos);
	}

	@Override
	public void actualizar(Object object) throws Exception {
		actualizarPagos();
	}

}
