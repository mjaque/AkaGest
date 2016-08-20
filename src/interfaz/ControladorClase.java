package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import datos.DAOAlumno;
import datos.DAOAsignatura;
import datos.DAOClase;
import interfaz.util.DatePickerTableCell;
import interfaz.util.LocalTimeTableCell;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import negocio.Alumno;
import negocio.Asignatura;
import negocio.Clase;

public class ControladorClase implements Initializable, ChangeListener<Clase>, EventHandler {

	private Controlador controladorPrincipal;
	private ObservableList<Clase> olClases;
	private ObservableList<Alumno> olAlumnos;
	private ObservableList<Asignatura> olAsignaturas;
	private ObservableList<Clase.Estado> olEstados;

	private enum Modo {
		ESPERA, SELECCIONADO
	};

	private Modo modo;

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

	public ControladorClase() throws Exception {
		super();
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.getLista());
		olAsignaturas = FXCollections.observableArrayList(DAOAsignatura.INSTANCE.getLista());
		olEstados = FXCollections.observableArrayList(Clase.Estado.values());
	}

	@FXML
	protected void insertarClase(ActionEvent event) throws Exception {
		Clase clase = new Clase();
		clase.guardar();
		actualizarClases();
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
		ChoiceDialog<Alumno> dialog = new ChoiceDialog<>(null, DAOAlumno.INSTANCE.listar());
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
			this.controladorPrincipal = IU.getControlador();

			tcClaseId.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("id"));

			Callback<TableColumn<Clase, LocalDate>, TableCell<Clase, LocalDate>> cellFactoryLocalDate = new Callback<TableColumn<Clase, LocalDate>, TableCell<Clase, LocalDate>>() {
				public TableCell call(TableColumn p) {
					return new DatePickerTableCell<Clase>(tcClaseFecha);
				}
			};

			tcClaseFecha.setCellValueFactory(new PropertyValueFactory<Clase, LocalDate>("fecha"));
			tcClaseFecha.setCellFactory(cellFactoryLocalDate);
			tcClaseFecha.setOnEditCommit(this);

			tcClaseHora.setCellValueFactory(new PropertyValueFactory<Clase, LocalTime>("hora"));
			tcClaseHora.setCellFactory(new Callback<TableColumn<Clase, LocalTime>, TableCell<Clase, LocalTime>>() {
				public TableCell call(TableColumn p) {
					return new LocalTimeTableCell<Clase>();
				}
			});
			tcClaseHora.setOnEditCommit(this);

			tcClaseAlumno.setCellValueFactory(new PropertyValueFactory<Clase, Alumno>("alumno"));
			tcClaseAlumno.setCellFactory(ComboBoxTableCell.forTableColumn(olAlumnos));
			tcClaseAlumno.setOnEditCommit(this);

			olAsignaturas.add(new Asignatura("Nueva Asignatura"));
			tcClaseAsignatura.setCellValueFactory(new PropertyValueFactory<Clase, Asignatura>("asignatura"));
			tcClaseAsignatura.setCellFactory(ComboBoxTableCell.forTableColumn(olAsignaturas));
			// tcClaseAsignatura.setOnEditStart(this);
			tcClaseAsignatura.setOnEditCommit(this);

			tcClaseDuracion.setCellValueFactory(new PropertyValueFactory<Clase, Float>("duracion"));
			tcClaseDuracion.setCellFactory(TextFieldTableCell.<Clase, Float>forTableColumn(new FloatStringConverter()));
			tcClaseDuracion.setOnEditCommit(this);

			tcClasePrecioHora.setCellValueFactory(new PropertyValueFactory<Clase, Float>("precioHora"));
			tcClasePrecioHora
					.setCellFactory(TextFieldTableCell.<Clase, Float>forTableColumn(new FloatStringConverter()));
			tcClasePrecioHora.setOnEditCommit(this);

			tcClaseEstado.setCellValueFactory(new PropertyValueFactory<Clase, Clase.Estado>("estado"));
			tcClaseEstado.setCellFactory(ComboBoxTableCell.forTableColumn(olEstados));
			tcClaseEstado.setOnEditCommit(this);

			tcClaseAsistencia.setCellValueFactory(new PropertyValueFactory<Clase, Boolean>("asistencia"));
			tcClaseAsistencia.setCellFactory(CheckBoxTableCell.forTableColumn(tcClaseAsistencia));
			tcClaseAsistencia
					.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
						@Override
						public ObservableValue<Boolean> call(Integer param) {
							try {
								tvClases.getItems().get(param).guardar();
							} catch (Exception e) {
								e.printStackTrace();
								Controlador.lanzarExcepcion(e.getMessage());
							}
							// Quitado porque se lanza automáticamente al cargar
							// las clases.
							// controladorPrincipal.mostrarExito("La asistencia
							// a clase se actualizó con éxito.");
							return tvClases.getItems().get(param).asistenciaProperty();
						}
					}));

			tcClaseObservaciones.setCellValueFactory(new PropertyValueFactory<Clase, String>("notas"));
			tcClaseObservaciones.setCellFactory(TextFieldTableCell.<Clase>forTableColumn());
			tcClaseObservaciones.setOnEditCommit(this);

			tvClases.getSelectionModel().selectedItemProperty().addListener(this);

			this.actualizarClases();
			
			this.setModo(Modo.ESPERA);

		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Clase> observable, Clase oldValue, Clase newValue) {
		System.out.println("changed");
		Clase clase = tvClases.getSelectionModel().getSelectedItem();
		System.out.println(clase);
		if (clase != null)
			this.setModo(Modo.SELECCIONADO);
		else
			this.setModo(Modo.ESPERA);
	}

	@Override
	public void handle(Event event) {
		try {
			if (event.getTarget().equals(tcClaseFecha)) {
				CellEditEvent<Clase, LocalDate> cellEditEventFecha = (CellEditEvent<Clase, LocalDate>) event;
				// event.getRowValue().setFecha(LocalDateTime.parse(event.getNewValue()));
				cellEditEventFecha.getRowValue().setFecha(cellEditEventFecha.getNewValue());
				cellEditEventFecha.getRowValue().guardar();
				controladorPrincipal.mostrarExito("La fecha de la clase se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcClaseHora)) {
				CellEditEvent<Clase, LocalTime> cellEditEventHora = (CellEditEvent<Clase, LocalTime>) event;
				// event.getRowValue().setFecha(LocalDateTime.parse(event.getNewValue()));
				cellEditEventHora.getRowValue().setHora(cellEditEventHora.getNewValue());
				cellEditEventHora.getRowValue().guardar();
				controladorPrincipal.mostrarExito("La hora de la clase se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcClaseAlumno)) {
				CellEditEvent<Clase, Alumno> cellEditEventAlumno = (CellEditEvent<Clase, Alumno>) event;
				cellEditEventAlumno.getRowValue().setAlumno(cellEditEventAlumno.getNewValue());
				cellEditEventAlumno.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El alumno de la clase se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcClaseAsignatura)) {
				CellEditEvent<Clase, Asignatura> cellEditEventAsignatura = (CellEditEvent<Clase, Asignatura>) event;
				Asignatura asignatura = cellEditEventAsignatura.getNewValue();
				if (asignatura.getId() == null) {
					TextInputDialog textInputDialog = new TextInputDialog("nombre...");
					textInputDialog.setTitle("Nueva Asignatura");
					// textInputDialog.setHeaderText("Indica el nombre de la
					// nueva asignatura");
					textInputDialog.setContentText("Indica el nombre de la nueva asignatura");
					Optional<String> nombre = textInputDialog.showAndWait();
					if (nombre.isPresent()) {
						asignatura.setNombre(nombre.get());
						asignatura.guardar();
						cellEditEventAsignatura.getRowValue().setAsignatura(asignatura);
						cellEditEventAsignatura.getRowValue().guardar();
						controladorPrincipal.mostrarExito("La asignatura de la clase se actualizó con éxito.");
					} else {
						cellEditEventAsignatura.getRowValue().setAsignatura(cellEditEventAsignatura.getOldValue());
						controladorPrincipal.mostrarExito("Edición de asignatura cancelada.");
					}
				} else {
					cellEditEventAsignatura.getRowValue().setAsignatura(cellEditEventAsignatura.getNewValue());
					cellEditEventAsignatura.getRowValue().guardar();
					controladorPrincipal.mostrarExito("La asignatura de la clase se actualizó con éxito.");
				}
			}

			else if (event.getTarget().equals(tcClaseDuracion)) {
				CellEditEvent<Clase, Float> cellEditEventFloat = (CellEditEvent<Clase, Float>) event;
				cellEditEventFloat.getRowValue().setDuracion(Float.valueOf(cellEditEventFloat.getNewValue()));
				cellEditEventFloat.getRowValue().guardar();
				controladorPrincipal.mostrarExito("La duración de la clase se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcClasePrecioHora)) {
				CellEditEvent<Clase, Float> cellEditEventPrecioHora = (CellEditEvent<Clase, Float>) event;
				cellEditEventPrecioHora.getRowValue()
						.setPrecioHora(Float.valueOf(cellEditEventPrecioHora.getNewValue()));
				cellEditEventPrecioHora.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El precio/hora de la clase se actualizó con éxito.");
			}

			else if (event.getTarget().equals(tcClaseEstado)) {
				CellEditEvent<Clase, Clase.Estado> cellEditEventEstado = (CellEditEvent<Clase, Clase.Estado>) event;
				cellEditEventEstado.getRowValue().setEstado(cellEditEventEstado.getNewValue());
				cellEditEventEstado.getRowValue().guardar();
				controladorPrincipal.mostrarExito("El estado de la clase se actualizó con éxito.");
			}

			// else if (event.getTarget().equals(tcClaseAsistencia))
			// event.getRowValue().setAsistencia(Boolean.valueOf(event.getNewValue()));

			else if (event.getTarget().equals(tcClaseObservaciones)) {
				CellEditEvent<Clase, String> cellEditEventObservaciones = (CellEditEvent<Clase, String>) event;
				cellEditEventObservaciones.getRowValue().setNotas(cellEditEventObservaciones.getNewValue());
				cellEditEventObservaciones.getRowValue().guardar();
				controladorPrincipal.mostrarExito("Las observaciones de la clase se actualizaron con éxito.");
			}

			actualizarClases();

		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	private void actualizarClases() throws Exception {
		if (olClases != null)
			olClases.removeAll(olClases);
		olClases = FXCollections.observableArrayList(DAOClase.INSTANCE.listar());
		tvClases.setItems(olClases);
	}

	private void setModo(Modo modo) {
		switch (modo) {
		case ESPERA:
			btnClaseCopiar.setDisable(true);
			btnClasesBorrar.setDisable(true);
			break;
		case SELECCIONADO:
			btnClaseCopiar.setDisable(false);
			btnClasesBorrar.setDisable(false);
			break;
		}
		this.modo = modo;
	}

}
