package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import datos.DAOClase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Clase {
	public enum Estado{Prevista, Impartida, Cancelada, Anulada};
	
	private IntegerProperty id = new SimpleIntegerProperty();
	private Alumno alumno;
	private Asignatura asignatura;
	private LocalDateTime fechaHora;
	private FloatProperty duracion = new SimpleFloatProperty();
	private FloatProperty precioHora = new SimpleFloatProperty();
	private Estado estado;
	private StringProperty pagada = new SimpleStringProperty();
	private BooleanProperty asistencia = new SimpleBooleanProperty();
	private StringProperty notas = new SimpleStringProperty();
	private StringProperty pago = new SimpleStringProperty();
	
	private DAOClase dao;
	
	public Clase() throws Exception{
		this.dao = DAOClase.INSTANCE;
		this.fechaHora = LocalDateTime.now();
		this.duracion.set(1f);
		this.precioHora.set(15f);
		this.estado = Estado.Prevista;
		this.pagada.set("PENDIENTE");
		this.asistencia.set(false);
		this.notas.set("");
		this.pago.set("Sin calcular");
	}
	
	public Clase(Clase clase) throws Exception{
		this.dao = DAOClase.INSTANCE;
		this.fechaHora = clase.getFechaHora();
		this.duracion.set(clase.getDuracion());
		this.asignatura = clase.asignatura;
		this.precioHora.set(clase.getPrecioHora());
		this.estado = clase.getEstado();
		this.pagada.set(clase.getPagada());
		this.asistencia.set(clase.isAsistencia());
		this.notas.set(clase.getNotas());
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
	public Alumno getAlumno() {
		return alumno;
	}
	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}
	public Asignatura getAsignatura() {
		return asignatura;
	}
	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}
	public Float getDuracion() {
		return duracion.get();
	}
	public void setDuracion(Float duracion) {
		this.duracion.set(duracion);
	}
	public FloatProperty duracionProperty(){
		return duracion;
	}
	public Float getPrecioHora() {
		return precioHora.get();
	}
	public void setPrecioHora(Float precioHora) {
		this.precioHora.set(precioHora);
	}
	public FloatProperty precioHoraProperty(){
		return precioHora;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public String getPagada() {
		return pagada.get();
	}
	public void setPagada(String pagada) {
		this.pagada.set(pagada);
	}
	public Boolean isAsistencia() {
		return asistencia.get();
	}
	public StringProperty pagadaProperty(){
		return pagada;
	}
	public void setAsistencia(Boolean asistencia) {
		this.asistencia.set(asistencia);
	}
	public BooleanProperty asistenciaProperty(){
		return asistencia;
	}
	public String getNotas() {
		return notas.get();
	}
	public void setNotas(String notas) {
		this.notas.set(notas);
	}
	public StringProperty notasProperty(){
		return notas;
	}
	
	public void guardar() throws Exception {
		this.dao.guardar(this);
	}
	public LocalDate getFecha() {
		return this.fechaHora.toLocalDate();
	}
	public void setFecha(LocalDate fecha) {
		this.fechaHora = LocalDateTime.of(fecha, this.fechaHora.toLocalTime());
	}
	public LocalTime getHora() {
		return this.fechaHora.toLocalTime();
	}
	public void setHora(LocalTime hora) {
		this.fechaHora = LocalDateTime.of(this.fechaHora.toLocalDate(), hora);
	}

	public void borrar() throws Exception {
		this.dao.borrar(this);	
	}

	
	public Clase copiar() throws Exception{
		return new Clase(this);
	}

	public static DateTimeFormatter getFormateadorHora() {
		return DateTimeFormatter.ofPattern("H:mm");
	}
	
	public Float getPrecioTotal(){
		return this.getDuracion() * this.getPrecioHora();
	}
	
}
