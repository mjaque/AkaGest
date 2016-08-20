package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import datos.DAOAlumno;
import datos.DAOPago;
import interfaz.util.DatePickerTableCell;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import negocio.Alumno;
import negocio.Pago;

public class ControladorPago implements Initializable, ChangeListener<Pago>, EventHandler {

	private Controlador controladorPrincipal;
	private ObservableList<Pago> olPagos;
	private ObservableList<Alumno> olAlumnos;
	private ObservableList<Pago.Operacion> olOperaciones = FXCollections.observableArrayList(Pago.Operacion.values());

	private enum Modo {
		ESPERA, SELECCIONADO
	};

	private Modo modo;
	
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
	
	public ControladorPago() throws Exception {
		super();
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.getLista());
		olOperaciones = FXCollections.observableArrayList(Pago.Operacion.values());
	}

	@FXML
	protected void insertarPago(ActionEvent event) throws Exception {
		Pago pago = new Pago();
		pago.guardar();
		actualizarPagos();
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
			this.controladorPrincipal = IU.getControlador();
			
			tcPagoId.setCellValueFactory(new PropertyValueFactory<Pago, Integer>("id"));

			Callback<TableColumn<Pago, LocalDate>, TableCell<Pago, LocalDate>> cellFactoryLocalDate = new Callback<TableColumn<Pago, LocalDate>, TableCell<Pago, LocalDate>>() {
				public TableCell call(TableColumn p) {
					return new DatePickerTableCell<Pago>(tcPagoFecha);
				}
			};

			tcPagoFecha.setCellValueFactory(new PropertyValueFactory<Pago, LocalDate>("fecha"));
			tcPagoFecha.setCellFactory(cellFactoryLocalDate);
			tcPagoFecha.setOnEditCommit(this);

			tcPagoOperacion.setCellValueFactory(new PropertyValueFactory<Pago, Pago.Operacion>("operacion"));
			tcPagoOperacion.setCellFactory(ComboBoxTableCell.forTableColumn(olOperaciones));
			tcPagoOperacion.setOnEditCommit(this);

			tcPagoImporte.setCellValueFactory(new PropertyValueFactory<Pago, Float>("importe"));
			tcPagoImporte.setCellFactory(TextFieldTableCell.<Pago, Float>forTableColumn(new FloatStringConverter()));
			tcPagoImporte.setOnEditCommit(this);

			tcPagoAlumno.setCellValueFactory(new PropertyValueFactory<Pago, Alumno>("alumno"));
			tcPagoAlumno.setCellFactory(ComboBoxTableCell.forTableColumn(olAlumnos));
			tcPagoAlumno.setOnEditCommit(this);

			tcPagoRecibo.setCellValueFactory(new PropertyValueFactory<Pago, String>("recibo"));
			tcPagoRecibo.setCellFactory(TextFieldTableCell.<Pago>forTableColumn());
			tcPagoRecibo.setOnEditCommit(this);

			tcPagoObservaciones.setCellValueFactory(new PropertyValueFactory<Pago, String>("notas"));
			tcPagoObservaciones.setCellFactory(TextFieldTableCell.<Pago>forTableColumn());
			tcPagoObservaciones.setOnEditCommit(this);

			tvPagos.getSelectionModel().selectedItemProperty().addListener(this);

			actualizarPagos();
			
			this.setModo(Modo.ESPERA);
			
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Pago> observable, Pago oldValue, Pago newValue) {
			Pago pago = tvPagos.getSelectionModel().getSelectedItem();
			if (pago != null) 
				this.setModo(Modo.SELECCIONADO);
			else
				this.setModo(Modo.ESPERA);
		}

		private void setModo(Modo modo) {
			switch (modo) {
			case ESPERA:
				btnPagosBorrar.setDisable(true);
				break;
			case SELECCIONADO:
				btnPagosBorrar.setDisable(false);
				break;
			}
			this.modo = modo;
		}

	@Override
	public void handle(Event event) {
		try {
			if (event.getTarget().equals(tcPagoFecha)) {
				CellEditEvent<Pago, LocalDate> cellEditEventFecha = (CellEditEvent<Pago, LocalDate>) event;
				cellEditEventFecha.getRowValue().setFecha(cellEditEventFecha.getNewValue());
				cellEditEventFecha.getRowValue().guardar();
				controladorPrincipal.mostrarExito("La fecha del pago se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcPagoImporte)) {
				CellEditEvent<Pago, Float> cellEditEventFloat = (CellEditEvent<Pago, Float>) event;
				cellEditEventFloat.getRowValue().setImporte(Float.valueOf(cellEditEventFloat.getNewValue()));
				cellEditEventFloat.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El importe del pago se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcPagoOperacion)) {
				CellEditEvent<Pago, Pago.Operacion> cellEditEventOperacion = (CellEditEvent<Pago, Pago.Operacion>) event;
				cellEditEventOperacion.getRowValue().setOperacion(cellEditEventOperacion.getNewValue());
				cellEditEventOperacion.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El tipo de operación del pago se actualizó con éxito.");
			}
			
			else if (event.getTarget().equals(tcPagoAlumno)) {
				CellEditEvent<Pago, Alumno> cellEditEventAlumno = (CellEditEvent<Pago, Alumno>) event;
				cellEditEventAlumno.getRowValue().setAlumno(cellEditEventAlumno.getNewValue());
				cellEditEventAlumno.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El alumno del pago se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcPagoRecibo)) {
				CellEditEvent<Pago, String> cellEditEventString = (CellEditEvent<Pago, String>) event;
				cellEditEventString.getRowValue().setRecibo(cellEditEventString.getNewValue());
				cellEditEventString.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El recibo del pago se actualizaró con éxito.");
			}

			else if (event.getTarget().equals(tcPagoObservaciones)) {
				CellEditEvent<Pago, String> cellEditEventObservaciones = (CellEditEvent<Pago, String>) event;
				cellEditEventObservaciones.getRowValue().setNotas(cellEditEventObservaciones.getNewValue());
				cellEditEventObservaciones.getRowValue().guardar();
				controladorPrincipal.mostrarExito("Las observaciones del pago se actualizaron con éxito.");
			}

			actualizarPagos();

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

}
