package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOAlumno;
import datos.DAOAsignatura;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import negocio.Alumno;
import negocio.Asignatura;
import negocio.Clase;

public class ControladorClase implements Initializable, EventHandler {

	private Controlador controladorPrincipal;
	private Actualizable controladorLlamante;
	private Alumno alumno;
	private Clase clase;

	@FXML
	Label lblId, lblTotal;
	@FXML
	DatePicker dpFecha;
	@FXML
	ComboBox<Asignatura> cbAsignatura;
	@FXML
	ComboBox<Alumno> cbAlumno;
	@FXML
	ComboBox<Clase.Estado> cbEstado;
	@FXML
	TextField tfHora, tfDuracion, tfPrecioHora;
	@FXML
	TextArea taObservaciones;
	@FXML
	CheckBox chbAsistencia;
	@FXML
	Button btnAceptar, btnCancelar;

	public ControladorClase() throws Exception {
		super();
		this.clase = null;
	}

	@FXML
	protected void aceptar(ActionEvent event) throws Exception {
		try {
			if (this.clase == null)
				this.clase = new Clase();
			this.clase.setFecha(this.dpFecha.getValue());
			this.clase.setHora(LocalTime.parse(this.tfHora.getText(), Clase.getFormateadorHora()));
			Asignatura asignatura = this.cbAsignatura.getValue();
			if (asignatura.getId() == 0) {
				TextInputDialog textInputDialog = new TextInputDialog("nombre...");
				textInputDialog.setTitle("Nueva Asignatura");
				textInputDialog.setContentText("Indica el nombre de la nueva asignatura");
				Optional<String> nombre = textInputDialog.showAndWait();
				if (nombre.isPresent()) {
					asignatura.setNombre(nombre.get());
					asignatura.guardar();
				} else {
					controladorPrincipal.mostrarExito("Edición de asignatura cancelada.");
					return;
				}
			}
			this.clase.setAsignatura(asignatura);
			this.clase.setAlumno(this.cbAlumno.getValue());
			this.clase.setDuracion(Float.parseFloat(this.tfDuracion.getText()));
			this.clase.setPrecioHora(Float.parseFloat(this.tfPrecioHora.getText()));
			this.clase.setEstado(this.cbEstado.getValue());
			this.clase.setAsistencia(this.chbAsistencia.isSelected());
			this.clase.setNotas(this.taObservaciones.getText());
			this.clase.guardar();
			if (this.controladorLlamante != null)
				this.controladorLlamante.actualizar(this.clase);
			this.cancelar(null);
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@FXML
	protected void cancelar(ActionEvent event) throws Exception {
		((Stage) this.btnCancelar.getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			this.controladorPrincipal = AkaGest.getControlador();
			this.dpFecha.setValue(LocalDate.now());
			this.cbAsignatura.getItems().add(new Asignatura("Nueva Asignatura"));
			this.cbAsignatura.getItems().addAll(DAOAsignatura.INSTANCE.getLista());
			this.cbAsignatura.getSelectionModel().select(0);
			this.cbAlumno.getItems().addAll(DAOAlumno.INSTANCE.getLista());
			this.cbAlumno.getSelectionModel().select(0);
			this.cbEstado.getItems().addAll(Clase.Estado.values());
			this.cbEstado.getSelectionModel().select(0);
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void handle(Event event) {
	}

	public void setClase(Clase clase) {
		if (clase != null) {
			this.clase = clase;
			this.lblId.setText(String.valueOf(clase.getId()));
			this.dpFecha.setValue(clase.getFecha());
			this.tfHora.setText(clase.getHora().toString());
			this.cbAsignatura.setValue(clase.getAsignatura());
			this.cbAlumno.setValue(clase.getAlumno());
			this.tfDuracion.setText(String.valueOf(clase.getDuracion()));
			this.tfPrecioHora.setText(String.valueOf(clase.getPrecioHora()));
			this.lblTotal.setText("= " + clase.getPrecioTotal());
			this.cbEstado.setValue(clase.getEstado());
			this.chbAsistencia.setSelected(clase.isAsistencia());
			this.taObservaciones.setText(clase.getNotas());
		}
	}

	public void setControladorLlamante(Actualizable controladorLlamante) {
		this.controladorLlamante = controladorLlamante;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
		this.cbAlumno.setValue(alumno);
	}

}
