package negocio;

import datos.DAOCanal;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Marketing {

	private IntegerProperty id = new SimpleIntegerProperty();
	private String canal;
	
	private DAOCanal dao;
	
	public Marketing() throws Exception{
		this.dao = DAOCanal.INSTANCE;
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
	public String getCanal() {
		return this.canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}

	@Override
	public String toString() {
		return this.canal;
	}
	
}
