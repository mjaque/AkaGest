package interfaz.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import util.Log;
import util.Log.Nivel;

//TODO: Unificar con EditingCell
//TODO: Unificar la validación. O con ENTER o con TAB. LocalTimeTableCell valida con blur, mientras que TextFieldTableCell lo hace con ENTER.

public class LocalTimeTableCell<T> extends TableCell<T, LocalTime> {

	private TextField textField;
	private DateTimeFormatter formateador = DateTimeFormatter.ofPattern("HH:mm");

	public LocalTimeTableCell() {
	}

	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			createTextField();
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(getItem().format(formateador));

		//setText((String) getItem());
		setGraphic(null);
	}

	@Override
	public void updateItem(LocalTime item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(null);
			}
		}
	}

	private void createTextField() {
		textField = new TextField(getString());
		textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (!arg2) {
					Log.log(Nivel.DEPURACION, textField.getText());
					commitEdit(LocalTime.parse(textField.getText(), formateador));
				}
			}
		});
	}

	private String getString() {
		return getItem() == null ? "" : getItem().format(formateador);
	}
}
