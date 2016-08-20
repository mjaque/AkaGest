package negocio;

import datos.DAOAsignatura;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Asignatura {
	private IntegerProperty id = new SimpleIntegerProperty();
	private StringProperty nombre = new SimpleStringProperty();
	
	private DAOAsignatura dao;
	
	public Asignatura() throws Exception {
		this.dao = DAOAsignatura.INSTANCE;
	}
	public Asignatura(String nombre) throws Exception {
		this.nombre.set(nombre);
		this.dao = DAOAsignatura.INSTANCE;
	}
	public Integer getId() {
		return id.get();
	}
	public void setId(Integer id) {
		this.id.set(id);
	}
	public IntegerProperty idProperty(){
		return id;
	}
	public String getNombre() {
		return nombre.get();
	}
	public void setNombre(String nombre) {
		this.nombre.set(nombre);
	}
	public StringProperty nombreProperty(){
		return nombre;
	}

	@Override
	public String toString() {
		return nombre.get();
	}
	public void guardar() throws Exception {
		this.dao.guardar(this);
	}
	
}
