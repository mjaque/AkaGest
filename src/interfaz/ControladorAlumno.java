package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import datos.DAOAlumno;
import datos.DAOAsignatura;
import datos.DAOClase;
import datos.DAOPago;
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
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import negocio.Alumno;
import negocio.Asignatura;
import negocio.Clase;
import negocio.Pago;
import util.Log;
import util.Log.Nivel;

public class ControladorAlumno
		implements Initializable, ChangeListener<Alumno>, EventHandler<CellEditEvent<Alumno, String>> {

	private Controlador controladorPrincipal;
	private ControladorAlumnoClase controladorAlumnoClase;
	private ControladorAlumnoPago controladorAlumnoPago;
	private ObservableList<Alumno> olAlumnos;
	private ObservableList<Clase> olClases;
	private ObservableList<Pago> olPagos;

	private enum Modo {
		ESPERA, SELECCIONADO, INSERTAR, EDITAR
	};

	private Modo modo;

	// Componentes del Subpanel superior - Tabla de Alumnos
	@FXML
	Button btnHoy;
	@FXML
	TextField tfBuscar;
	@FXML
	TableView<Alumno> tvAlumnos;
	@FXML
	TableView<Clase> tvClases;
	@FXML
	TableView<Pago> tvPagos;
	@FXML
	TableColumn<Alumno, Integer> tcAlumnoId;
	@FXML
	TableColumn<Alumno, String> tcAlumnoNombreCompleto, tcAlumnoEmail, tcAlumnoTelefonos, tcAlumnoCentroEstudios;
	@FXML
	TableColumn<Clase, Integer> tcClaseId;
	@FXML
	TableColumn<Clase, LocalDate> tcClaseFecha;
	@FXML
	TableColumn<Clase, LocalTime> tcClaseHora;
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
	TableColumn<Pago, Integer> tcPagoId;
	@FXML
	TableColumn<Pago, LocalDate> tcPagoFecha;
	@FXML
	TableColumn<Pago, Pago.Operacion> tcPagoOperacion;
	@FXML
	TableColumn<Pago, Float> tcPagoImporte;
	@FXML
	TableColumn<Pago, String> tcPagoRecibo, tcPagoObservaciones;
	@FXML
	Label lblContratado, lblPagado, lblSaldo;
	@FXML
	Tab tabPagos;

	// Componentes del Subpanel inferior - TabPanel
	@FXML
	private Label lblId;
	@FXML
	private TextArea taDatosProgenitor, taNotas;
	@FXML
	private TextField tfNombreCompleto, tfNif, tfEmail, tfTelefonos, tfCentroEstudios;
	@FXML
	private StackPane spBotones;
	@FXML
	private Pane pAceptarCancelar, pEditarBorrarNuevo;
	@FXML
	private Button btnAlumnoBorrar, btnAlumnoEditar, btnAlumnoNuevo, btnAlumnoAceptar, btnAlumnoCancelar,
			btnClasesBorrar, btnClasesInsertar, btnClaseCopiar, btnPagosBorrar, btnPagosInsertar;

	private void buscarPorNombre() throws Exception {
		if (tfBuscar.getText().isEmpty())
			this.actualizarTablaAlumnos();
		else {
			if (olAlumnos != null)
				olAlumnos.removeAll(olAlumnos);
			System.out.println("TRON:" + tfBuscar);
			// olAlumnos =
			// FXCollections.observableArrayList(DAOAlumno.buscarPorNombre(tfBuscar.getText()));
			olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.buscarPorNombre("jaque"));
			tvAlumnos.setItems(olAlumnos);
			this.borrarTablaClases();
			this.borrarTablaPagos();
		}
	}

	@FXML
	protected void insertarAlumno(ActionEvent event) {
		this.setModo(Modo.INSERTAR);
	}

	@FXML
	protected void insertarClase(ActionEvent event) throws Exception {
		Clase clase = new Clase();
		clase.setAlumno(this.tvAlumnos.getSelectionModel().getSelectedItem());
		clase.guardar();
		cargarClases(tvAlumnos.getSelectionModel().getSelectedItem());
	}

	@FXML
	protected void insertarPago(ActionEvent event) throws Exception {
		Pago pago = new Pago();
		pago.setAlumno(this.tvAlumnos.getSelectionModel().getSelectedItem());
		pago.guardar();
		cargarPagos(tvAlumnos.getSelectionModel().getSelectedItem());
	}

	@FXML
	protected void borrarAlumno(ActionEvent event) throws Exception {
		String nombre = this.tvAlumnos.getSelectionModel().getSelectedItem().getNombreCompleto();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmación de Borrado");
		alert.setHeaderText("¿Seguro que quiere BORRAR a " + nombre + "?");
		alert.setContentText("Esta acción no puede deshacerse");
		Optional<ButtonType> resultado = alert.showAndWait();
		if (resultado.get() == ButtonType.OK) {
			this.tvAlumnos.getSelectionModel().getSelectedItem().borrar();
			this.controladorPrincipal.mostrarExito("El alumno ha sido borrado.");
			this.actualizarTablaAlumnos();
			this.setModo(Modo.ESPERA);
		}
	}

	@FXML
	protected void borrarClase(ActionEvent event) throws Exception {
		// String nombre =
		// this.tvAlumnos.getSelectionModel().getSelectedItem().getNombreCompleto();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmación de Borrado");
		alert.setHeaderText("¿Seguro que quiere BORRAR esta clase?");
		alert.setContentText("Esta acción no puede deshacerse");
		Optional<ButtonType> resultado = alert.showAndWait();
		if (resultado.get() == ButtonType.OK) {
			this.tvClases.getSelectionModel().getSelectedItem().borrar();
			this.controladorPrincipal.mostrarExito("La clase ha sido borrada.");
			cargarClases(tvAlumnos.getSelectionModel().getSelectedItem());
		}
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
			cargarPagos(tvAlumnos.getSelectionModel().getSelectedItem());
		}
	}

	@FXML
	protected void editarAlumno(ActionEvent event) {
		this.setModo(Modo.EDITAR);
	}

	@FXML
	protected void aceptarAlumno(ActionEvent event) throws Exception {
		switch (this.modo) {
		case INSERTAR:
			Alumno alumno = new Alumno();
			actualizar(alumno);
			this.controladorPrincipal.mostrarExito("El alumno se creó con éxito.");
			break;
		case EDITAR:
			actualizar(this.tvAlumnos.getSelectionModel().getSelectedItem());
			this.controladorPrincipal.mostrarExito("El alumno se actualizó con éxito.");
			break;
		default:
		}
		this.actualizarTablaAlumnos();
		this.setModo(Modo.ESPERA);
	}

	@FXML
	protected void cancelarAlumno(ActionEvent event) throws Exception {
		switch (this.modo) {
		case INSERTAR:
			this.setModo(Modo.ESPERA);
			break;
		case EDITAR:
			this.cargarDatos(this.tvAlumnos.getSelectionModel().getSelectedItem());
			this.setModo(Modo.SELECCIONADO);
			break;
		default:
		}
	}

	@FXML
	protected void buscarHoy(ActionEvent event) throws Exception {
		if (olAlumnos != null)
			olAlumnos.removeAll(olAlumnos);
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.buscarHoy());
		tvAlumnos.setItems(olAlumnos);
		this.borrarTablaClases();
		this.borrarTablaPagos();
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
				cargarClases(tvAlumnos.getSelectionModel().getSelectedItem());
				cargarPagos(tvAlumnos.getSelectionModel().getSelectedItem());
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
			this.controladorAlumnoClase = new ControladorAlumnoClase();
			this.controladorAlumnoPago = new ControladorAlumnoPago();

			// Definimos un cellFactory que no haga commit solo con ENTER
			// Callback<TableColumn<Alumno, String>, TableCell<Alumno, String>>
			// cellFactory =
			// new Callback<TableColumn<Alumno, String>, TableCell<Alumno,
			// String>>() {
			// public TableCell call(TableColumn p) {
			// return new EditingCell<Alumno, String>();
			// }
			// };

			tcAlumnoId.setCellValueFactory(new PropertyValueFactory<Alumno, Integer>("id"));
			tcAlumnoNombreCompleto.setCellValueFactory(new PropertyValueFactory<Alumno, String>("nombreCompleto"));
			tcAlumnoNombreCompleto.setCellFactory(TextFieldTableCell.<Alumno>forTableColumn());
			// tcAlumnoNombreCompleto.setCellFactory(cellFactory);
			tcAlumnoNombreCompleto.setOnEditCommit(this);
			tcAlumnoEmail.setCellValueFactory(new PropertyValueFactory<Alumno, String>("email"));
			tcAlumnoEmail.setCellFactory(TextFieldTableCell.<Alumno>forTableColumn());
			tcAlumnoEmail.setOnEditCommit(this);
			tcAlumnoTelefonos.setCellValueFactory(new PropertyValueFactory<Alumno, String>("telefonos"));
			tcAlumnoTelefonos.setCellFactory(TextFieldTableCell.<Alumno>forTableColumn());
			tcAlumnoTelefonos.setOnEditCommit(this);
			tcAlumnoCentroEstudios.setCellValueFactory(new PropertyValueFactory<Alumno, String>("centroEstudios"));
			tcAlumnoCentroEstudios.setCellFactory(TextFieldTableCell.<Alumno>forTableColumn());
			tcAlumnoCentroEstudios.setOnEditCommit(this);
			tvAlumnos.getSelectionModel().selectedItemProperty().addListener(this);
			actualizarTablaAlumnos();

			// //Campo de Búsqueda
			tfBuscar.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue) { // Ha perdido el foco
					try {
						buscarPorNombre();
					} catch (Exception e) {
						e.printStackTrace();
						Controlador.lanzarExcepcion(e.getMessage());
					}
				}
			});

			this.controladorAlumnoClase.initialize(location, resources);
			this.controladorAlumnoPago.initialize(location, resources);

			this.setModo(Modo.ESPERA);
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void changed(ObservableValue<? extends Alumno> observable, Alumno oldValue, Alumno newValue) {
		Alumno alumno = tvAlumnos.getSelectionModel().getSelectedItem();
		if (alumno != null) {
			this.setModo(Modo.SELECCIONADO);
			try {
				this.cargarDatos(alumno);
			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}
		}
	}

	@Override
	public void handle(CellEditEvent<Alumno, String> event) {
		try {
			if (event.getTarget().equals(tcAlumnoNombreCompleto))
				event.getRowValue().setNombreCompleto(event.getNewValue());
			if (event.getTarget().equals(tcAlumnoEmail))
				event.getRowValue().setEmail(event.getNewValue());
			if (event.getTarget().equals(tcAlumnoTelefonos)) {
				System.out.println("Teléfonos: " + event.getNewValue());
				event.getRowValue().setTelefonos(event.getNewValue());
			}
			if (event.getTarget().equals(tcAlumnoCentroEstudios))
				event.getRowValue().setCentroEstudios(event.getNewValue());
			event.getRowValue().guardar();
			try {
				this.cargarDatos(event.getRowValue());
			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}
			this.controladorPrincipal.mostrarExito("El alumno se actualizó con éxito.");
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	protected void iniciarFormulario() {
		this.tvAlumnos.getSelectionModel().clearSelection();
		this.lblId.setText("---");
		this.tfNombreCompleto.clear();
		this.tfNif.clear();
		this.tfEmail.clear();
		this.tfTelefonos.clear();
		this.tfCentroEstudios.clear();
		this.taDatosProgenitor.clear();
		this.taNotas.clear();
	}

	private void actualizarTablaAlumnos() throws Exception {
		if (olAlumnos != null)
			olAlumnos.removeAll(olAlumnos);
		olAlumnos = FXCollections.observableArrayList(DAOAlumno.INSTANCE.listar());
		tvAlumnos.setItems(olAlumnos);
		this.borrarTablaClases();
		this.borrarTablaPagos();
	}

	private void borrarTablaClases() {
		if (olClases != null)
			olClases.removeAll(olClases);
	}

	private void borrarTablaPagos() {
		if (olPagos != null)
			olPagos.removeAll(olPagos);
	}

	private void editarFormulario(boolean b) {
		this.tfNombreCompleto.setEditable(b);
		this.tfNif.setEditable(b);
		this.tfEmail.setEditable(b);
		this.tfTelefonos.setEditable(b);
		this.tfCentroEstudios.setEditable(b);
		this.taDatosProgenitor.setEditable(b);
		this.taNotas.setEditable(b);
	}

	private void actualizar(Alumno alumno) throws Exception {
		if (alumno != null) {
			alumno.setNombreCompleto(tfNombreCompleto.getText());
			alumno.setNif(tfNif.getText());
			alumno.setEmail(tfEmail.getText());
			System.out.println("Controlador Principal fijado");
			alumno.setTelefonos(tfTelefonos.getText());
			alumno.setCentroEstudios(tfCentroEstudios.getText());
			alumno.setDatosProgenitor(taDatosProgenitor.getText());
			alumno.setNotas(taNotas.getText());
			alumno.guardar();
		}
	}

	private void cargarDatos(Alumno alumno) throws Exception {
		this.lblId.setText(String.valueOf(alumno.getId()));
		this.tfNombreCompleto.setText(alumno.getNombreCompleto());
		this.tfNif.setText(alumno.getNif());
		this.tfEmail.setText(alumno.getEmail());
		this.tfTelefonos.setText(alumno.getTelefonos());
		this.tfCentroEstudios.setText(alumno.getCentroEstudios());
		this.taDatosProgenitor.setText(alumno.getDatosProgenitor());
		this.taNotas.setText(alumno.getNotas());
		this.cargarClases(alumno);
		this.cargarPagos(alumno);
	}

	private void cargarClases(Alumno alumno) throws Exception {
		borrarTablaClases();
		olClases = FXCollections.observableArrayList(DAOClase.INSTANCE.listarPorAlumno(alumno));
		tvClases.setItems(olClases);
	}

	private void cargarPagos(Alumno alumno) throws Exception {
		borrarTablaPagos();
		olPagos = FXCollections.observableArrayList(DAOPago.INSTANCE.listarPorAlumno(alumno));
		tvPagos.setItems(olPagos);

		// Cargamos los totales
		Float contratado = DAOClase.INSTANCE.verContratadoAlumno(alumno);
		Float pagado = DAOPago.INSTANCE.verPagosAlumno(alumno);
		lblContratado.setText("Contratado: " + contratado + "€");
		lblPagado.setText("Pagado: " + pagado + "€");
		lblSaldo.setText("Saldo: " + (pagado - contratado) + "€");
		if (pagado - contratado < 0)
			tabPagos.setStyle("-fx-background-color: red");
		else
			// TODO: Evitar el warning de esta llamada setStyle. Habrá que
			// utilizar clases de CSS.
			tabPagos.setStyle("-fx-background-color: ");
	}

	private void setModo(Modo modo) {
		switch (modo) {
		case ESPERA:
			this.editarFormulario(false);
			this.pAceptarCancelar.setVisible(false);
			this.pEditarBorrarNuevo.setVisible(true);
			this.btnAlumnoEditar.setDisable(true);
			this.btnAlumnoBorrar.setDisable(true);
			this.btnAlumnoNuevo.setDisable(false);
			break;
		case SELECCIONADO:
			this.editarFormulario(false);
			this.pAceptarCancelar.setVisible(false);
			this.pEditarBorrarNuevo.setVisible(true);
			this.btnAlumnoEditar.setDisable(false);
			this.btnAlumnoBorrar.setDisable(false);
			this.btnAlumnoNuevo.setDisable(false);
			break;
		case EDITAR:
			this.editarFormulario(true);
			this.tfNombreCompleto.requestFocus();
			this.pAceptarCancelar.setVisible(true);
			this.pEditarBorrarNuevo.setVisible(false);
			break;
		case INSERTAR:
			this.editarFormulario(true);
			this.iniciarFormulario();
			this.tfNombreCompleto.requestFocus();
			this.pAceptarCancelar.setVisible(true);
			this.pEditarBorrarNuevo.setVisible(false);
			break;
		}
		this.modo = modo;
	}

	class ControladorAlumnoClase implements Initializable, ChangeListener<Clase>, EventHandler {

		private ObservableList<Asignatura> olAsignaturas;
		private ObservableList<Clase.Estado> olEstados;
		private Modo modo;

		public ControladorAlumnoClase() throws Exception {
			super();
			olAsignaturas = FXCollections.observableArrayList(DAOAsignatura.INSTANCE.getLista());
			olEstados = FXCollections.observableArrayList(Clase.Estado.values());
		}

		@Override
		public void initialize(URL location, ResourceBundle resources) {
			try {
				tcClaseId.setCellValueFactory(new PropertyValueFactory<Clase, Integer>("id"));

				// //Definimos un cellFactory que no haga commit solo con ENTER
				// Callback<TableColumn<Clase, String>, TableCell<Clase,
				// String>> cellFactoryString =
				// new Callback<TableColumn<Clase, String>, TableCell<Clase,
				// String>>() {
				// public TableCell call(TableColumn p) {
				// return new EditingCell<Alumno, String>();
				// }
				// };
				// Callback<TableColumn<Clase, Float>, TableCell<Clase, Float>>
				// cellFactoryFloat =
				// new Callback<TableColumn<Clase, Float>, TableCell<Clase,
				// Float>>() {
				// public TableCell call(TableColumn p) {
				// return new EditingCell<Alumno, Float>();
				// }
				// };

				Callback<TableColumn<Clase, LocalDate>, TableCell<Clase, LocalDate>> cellFactoryLocalDate = new Callback<TableColumn<Clase, LocalDate>, TableCell<Clase, LocalDate>>() {
					public TableCell call(TableColumn p) {
						return new DatePickerTableCell<Clase>(tcClaseFecha);
					}
				};

				tcClaseFecha.setCellValueFactory(new PropertyValueFactory<Clase, LocalDate>("fecha"));
				tcClaseFecha.setCellFactory(cellFactoryLocalDate);
				tcClaseFecha.setOnEditCommit(this);

				tcClaseHora.setCellValueFactory(new PropertyValueFactory<Clase, LocalTime>("hora"));
				// tcClaseHora.setCellFactory(TextFieldTableCell.<Clase>
				// forTableColumn());
				tcClaseHora.setCellFactory(new Callback<TableColumn<Clase, LocalTime>, TableCell<Clase, LocalTime>>() {
					public TableCell call(TableColumn p) {
						return new LocalTimeTableCell<Clase>();
					}
				});
				tcClaseHora.setOnEditCommit(this);

				olAsignaturas.add(new Asignatura("Nueva Asignatura"));
				tcClaseAsignatura.setCellValueFactory(new PropertyValueFactory<Clase, Asignatura>("asignatura"));
				tcClaseAsignatura.setCellFactory(ComboBoxTableCell.forTableColumn(olAsignaturas));
				// tcClaseAsignatura.setOnEditStart(this);
				tcClaseAsignatura.setOnEditCommit(this);

				tcClaseDuracion.setCellValueFactory(new PropertyValueFactory<Clase, Float>("duracion"));
				tcClaseDuracion
						.setCellFactory(TextFieldTableCell.<Clase, Float>forTableColumn(new FloatStringConverter()));
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
				tcClaseAsistencia.setCellFactory(
						CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
							@Override
							public ObservableValue<Boolean> call(Integer param) {
								try {
									tvClases.getItems().get(param).guardar();
								} catch (Exception e) {
									e.printStackTrace();
									Controlador.lanzarExcepcion(e.getMessage());
								}
								// Quitado porque se lanza automáticamente al
								// cargar las clases.
								// controladorPrincipal.mostrarExito("La
								// asistencia a clase se actualizó con éxito.");
								return tvClases.getItems().get(param).asistenciaProperty();
							}
						}));

				tcClaseObservaciones.setCellValueFactory(new PropertyValueFactory<Clase, String>("notas"));
				tcClaseObservaciones.setCellFactory(TextFieldTableCell.<Clase>forTableColumn());
				tcClaseObservaciones.setOnEditCommit(this);

				tvClases.getSelectionModel().selectedItemProperty().addListener(this);

				this.setModo(Modo.ESPERA);

			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}

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

				else if (event.getTarget().equals(tcClaseAsignatura)) {
					CellEditEvent<Clase, Asignatura> cellEditEventAsignatura = (CellEditEvent<Clase, Asignatura>) event;
					Asignatura asignatura = cellEditEventAsignatura.getNewValue();
					if (asignatura.getId() == 0) {
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

				cargarClases(tvAlumnos.getSelectionModel().getSelectedItem());
			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}
		}

		@Override
		public void changed(ObservableValue<? extends Clase> observable, Clase oldValue, Clase newValue) {
			Clase clase = tvClases.getSelectionModel().getSelectedItem();
			if (clase != null)
				this.setModo(Modo.SELECCIONADO);
			else
				this.setModo(Modo.ESPERA);
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

	class ControladorAlumnoPago implements Initializable, ChangeListener<Pago>, EventHandler {

		private ObservableList<Pago.Operacion> olOperaciones = FXCollections
				.observableArrayList(Pago.Operacion.values());
		private Modo modo;

		@Override
		public void initialize(URL location, ResourceBundle resources) {
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

			tcPagoRecibo.setCellValueFactory(new PropertyValueFactory<Pago, String>("recibo"));
			tcPagoRecibo.setCellFactory(TextFieldTableCell.<Pago>forTableColumn());
			tcPagoRecibo.setOnEditCommit(this);

			tcPagoObservaciones.setCellValueFactory(new PropertyValueFactory<Pago, String>("notas"));
			tcPagoObservaciones.setCellFactory(TextFieldTableCell.<Pago>forTableColumn());
			tcPagoObservaciones.setOnEditCommit(this);

			tvPagos.getSelectionModel().selectedItemProperty().addListener(this);

			setModo(Modo.ESPERA);
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

				cargarPagos(tvAlumnos.getSelectionModel().getSelectedItem());

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

	}
}
