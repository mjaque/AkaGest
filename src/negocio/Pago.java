package negocio;

import java.time.LocalDate;

import datos.DAOPago;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Pago {
	public enum Operacion {Efectivo, Banco, Salida_Caja, Saldo_Inicial};
	private IntegerProperty id = new SimpleIntegerProperty();
	private Alumno alumno;
	private LocalDate fecha;
	private Operacion operacion;
	private StringProperty recibo = new SimpleStringProperty();
	private FloatProperty importe = new SimpleFloatProperty();
	private StringProperty notas = new SimpleStringProperty();
	
	private DAOPago dao;
	
	public Pago() throws Exception{
		this.dao = DAOPago.INSTANCE;
		this.fecha = LocalDate.now();
		this.operacion = Operacion.Efectivo;
	}
	
	public int getId(){
		return this.id.get();
	}
	public void setId(int id){
		this.id.set(id);
	}
	public IntegerProperty idProperty(){
		return this.id;
	}
	public Alumno getAlumno(){
		return this.alumno;
	}
	public void setAlumno(Alumno alumno){
		this.alumno = alumno;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public Operacion getOperacion() {
		return operacion;
	}
	public void setOperacion(Operacion operacion) {
		this.operacion = operacion;
	}
	public String getRecibo(){
		return this.recibo.get();
	}
	public void setRecibo(String recibo){
		this.recibo.set(recibo);
	}
	public StringProperty reciboProperty(){
		return this.recibo;
	}
	public float getImporte(){
		return this.importe.get();
	}
	public void setImporte(float importe){
		this.importe.set(importe);
	}
	public FloatProperty importeProperty(){
		return this.importe;
	}
	public String getNotas(){
		return this.notas.get();
	}
	public void setNotas(String notas){
		this.notas.set(notas);
	}
	public StringProperty notasProperty(){
		return this.notas;
	}
	
	public void guardar() throws Exception{
		this.dao.guardar(this);
	}
	
	public void borrar() throws Exception{
		this.dao.borrar(this);	
	}

}
