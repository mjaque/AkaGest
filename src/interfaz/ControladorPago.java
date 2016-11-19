package interfaz;

import java.net.URL;
import java.util.ResourceBundle;

import aplicacion.AkaGest;
import datos.DAOAlumno;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import negocio.Alumno;
import negocio.Pago;
import negocio.Pago.Operacion;

public class ControladorPago implements Initializable, EventHandler {

	private Controlador controladorPrincipal;
	private Actualizable controladorLlamante;
	private Alumno alumno;
	private Pago pago;

	@FXML
	Label lblPagoId;
	@FXML
	DatePicker dpPagoFecha;
	@FXML
	ComboBox<Pago.Operacion> cbPagoOperacion;
	@FXML
	ComboBox<Alumno> cbPagoAlumno;
	@FXML
	TextField tfPagoImporte, tfPagoRecibo;
	@FXML
	TextArea taPagoObservaciones;
	@FXML
	Button btnPagoAceptar, btnPagoCancelar;

	public ControladorPago() throws Exception {
		super();
		this.pago = null;
	}

	@FXML
	protected void aceptar(ActionEvent event) throws Exception {
		try {
			if (this.pago == null)
				this.pago = new Pago();

			this.pago.setFecha(this.dpPagoFecha.getValue());
			this.pago.setOperacion((Operacion) this.cbPagoOperacion.getValue());
			this.pago.setImporte(Float.parseFloat(this.tfPagoImporte.getText()));
			this.pago.setAlumno((Alumno) this.cbPagoAlumno.getValue());
			this.pago.setRecibo(this.tfPagoRecibo.getText());
			this.pago.setNotas(this.taPagoObservaciones.getText());
			this.pago.guardar();
			if (this.controladorLlamante != null)
				this.controladorLlamante.actualizar(this.pago);
			this.cancelar(null);
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@FXML
	protected void cancelar(ActionEvent event) throws Exception {
		((Stage) this.btnPagoCancelar.getScene().getWindow()).close();
	}

	@FXML
	protected void buscarAlumno(ActionEvent event) {
		System.out.println("buscarAlumno");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			this.controladorPrincipal = AkaGest.getControlador();
			this.cbPagoOperacion.getItems().addAll(Pago.Operacion.values());
			this.cbPagoOperacion.getSelectionModel().select(0);
			this.cbPagoAlumno.getItems().addAll(DAOAlumno.INSTANCE.getLista());
			this.cbPagoAlumno.getSelectionModel().select(0);
		} catch (Exception e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	@Override
	public void handle(Event event) {
		System.out.println(event);
	}

	public void setPago(Pago pago) {
		if (pago != null) {
			this.pago = pago;
			this.lblPagoId.setText(String.valueOf(pago.getId()));
			this.dpPagoFecha.setValue(pago.getFecha());
			this.cbPagoOperacion.setValue(pago.getOperacion());
			this.tfPagoImporte.setText(String.valueOf(pago.getImporte()));
			this.cbPagoAlumno.setValue(pago.getAlumno());
			this.tfPagoRecibo.setText(pago.getRecibo());
			this.taPagoObservaciones.setText(pago.getNotas());
		}
	}

	public void setControladorLlamante(Actualizable controladorLlamante) {
		this.controladorLlamante = controladorLlamante;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
		this.cbPagoAlumno.setValue(alumno);
	}

}
