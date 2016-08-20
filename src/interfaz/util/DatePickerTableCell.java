package interfaz.util;

import java.time.LocalDate;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

//Referencia: http://stackoverflow.com/questions/23075139/datepicker-in-javafx-tablecell
//TODO: Reemplazar lambdas con clases anónimas.

public class DatePickerTableCell<T> extends TableCell<T, LocalDate> {

	private final DatePicker datePicker;

	public DatePickerTableCell(TableColumn<T, LocalDate> column) {
		this.datePicker = new DatePicker();
		this.datePicker.editableProperty().bind(column.editableProperty());
		this.datePicker.disableProperty().bind(column.editableProperty().not());
		this.datePicker.setOnShowing(event -> {
			final TableView<T> tableView = getTableView();
			tableView.getSelectionModel().select(getTableRow().getIndex());
			tableView.edit(tableView.getSelectionModel().getSelectedIndex(), column);
		});
		this.datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (isEditing()) {
				commitEdit(newValue);
			}
		});
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	@Override
	protected void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);

		setText(null);
		if (empty) {
			setGraphic(null);
		} else {
			this.datePicker.setValue(item);
			this.setGraphic(this.datePicker);
		}
	}

}
