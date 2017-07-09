package interfaz;

import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import datos.DAOClase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

public class ControladorResultados implements Initializable {

	@FXML
	ScrollPane panelTablaResultados;

	private String[][] datos = {
			{ "Curso", "Septiembre", "Octubre", "Noviembre", "Diciembre", "Enero", "Febrero", "Marzo", "Abril", "Mayo",
					"Junio", "Julio", "Agosto", "Anual" }};
//			{ "2012-13", "0 €", "	0 €", "	0 €", "	160 €", "	342 €", "	666 €", "	934 €", "	1.134 €",
//					"	1.239 €", "	1.465 €", "	1.736 €", "	665 €", "	8.340 €" },
//			{ "2013-14", "577 €", "	1.946 €", "	2.133 €", "	1.701 €", "	2.850 €", "	1.933 €", "	2.419 €", "	2.581 €",
//					"	3.044 €", "	2.351 €", "	2.269 €", "	0 €", "	23.804 €" },
//			{ "2014-15", "2.163 €", "	2.263 €", "	2.991 €", "	1.967 €", "	2.461 €", "	2.408 €", "	3.265 €",
//					"	2.864 €", "	3.757 €", "	2.568 €", "	2.519 €", "	0 €", "	29.225 €" },
//			{ "2015-16", "1.533 €", "	2.612 €", "	3.261 €", "	1.978 €", "	2.778 €", "	2.412 €", "	2.916 €",
//					"	3.113 €", "	3.957 €", "	2.749 €", "	3.050 €", "	900 €", "	31.259 €" }
//			};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.actualizar();
	}

	public void actualizar() {
		ObservableList<String[]> data = FXCollections.observableArrayList();
		data.addAll(Arrays.asList(datos));
		data.remove(0);// remove titles from data
		TableView<String[]> table = new TableView<>();
		for (int i = 0; i < datos[0].length; i++) {
			TableColumn tc = new TableColumn(datos[0][i]);
			final int colNo = i;
			tc.setCellValueFactory(new Callback<CellDataFeatures<String[], String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<String[], String> p) {
					return new SimpleStringProperty((p.getValue()[colNo]));
				}
			});
			tc.setStyle( "-fx-alignment: CENTER-RIGHT;");
			tc.setPrefWidth(110);
			table.getColumns().add(tc);
		}
		//Cargamos los datos de cada año
		int curso = 2012;
		Double totalAnual = 0d;
		String[] datosCurso = new String[14];
		datosCurso[0] = String.valueOf(curso) + "-" + String.valueOf((curso + 1) % 100);
		try {
			Map<LocalDate, Float> datos = DAOClase.INSTANCE.verPagosPorMes();
			for(Entry<LocalDate, Float> dato: datos.entrySet()){
				int modificador = -8;
				if (dato.getKey().getMonthValue() < 9){
					modificador = 4;
				}
				if(dato.getKey().getMonthValue() == 9){
					//Cambio de curso
					datosCurso[13] = totalAnual + "€";
					totalAnual = 0d;
					data.add(datosCurso);
					curso++;
					datosCurso = new String[14];
					datosCurso[0] = String.valueOf(curso) + "-" + String.valueOf((curso + 1) % 100);
				}
				int mes = dato.getKey().getMonthValue()+ modificador;
				int year = dato.getKey().getYear();
				datosCurso[mes] = String.valueOf(dato.getValue()) + "€";
				totalAnual += dato.getValue();
			}
			//Grabamos el último
			datosCurso[13] = totalAnual + "€";
			totalAnual = 0d;
			data.add(datosCurso);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		table.setItems(data);
		this.panelTablaResultados.setContent(table);
	}

}
