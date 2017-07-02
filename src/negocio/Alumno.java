package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;

import datos.DAOAlumno;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Alumno {
	private IntegerProperty id = new SimpleIntegerProperty();
	private StringProperty nif = new SimpleStringProperty();
	private StringProperty nombreCompleto = new SimpleStringProperty();
	private StringProperty email = new SimpleStringProperty();
	private StringProperty telefonos = new SimpleStringProperty();
	private StringProperty centroEstudios = new SimpleStringProperty();
	private StringProperty datosProgenitor = new SimpleStringProperty();
	private StringProperty notas = new SimpleStringProperty();
	private LocalDate fechaAlta;
	private LocalDate fechaBaja;
	
	private DAOAlumno dao;
	
	public Alumno() throws Exception{
		this.dao = DAOAlumno.INSTANCE;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Alumno other = (Alumno) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public int getId(){
		return id.get();
	}
	public void setId(Integer id){
		this.id.set(id);
	}
	public IntegerProperty idProperty(){
		return id;
	}
	public String getNif(){
		return nif.get();
	}
	public void setNif(String nif){
		this.nif.set(nif);
	}
	public StringProperty nifProperty(){
		return nif;
	}
	public String getNombreCompleto(){
		return nombreCompleto.get();
	}
	public void setNombreCompleto(String nombreCompleto){
		this.nombreCompleto.set(nombreCompleto);
	}
	public StringProperty nombreCompletoProperty(){
		return nombreCompleto;
	}
	public String getEmail(){
		return email.get();
	}
	public void setEmail(String email){
		this.email.set(email);
	}
	public StringProperty emailProperty(){
		return email;
	}
	public String getTelefonos(){
		return telefonos.get();
	}
	public void setTelefonos(String telefonos){
		this.telefonos.set(telefonos);
	}
	public StringProperty telefonosProperty(){
		return telefonos;
	}
	public String getCentroEstudios(){
		return centroEstudios.get();
	}
	public void setCentroEstudios(String centroEstudios){
		this.centroEstudios.set(centroEstudios);
	}
	public StringProperty centroEstudiosProperty(){
		return centroEstudios;
	}
	public String getDatosProgenitor(){
		return datosProgenitor.get();
	}
	public void setDatosProgenitor(String datosProgenitor){
		this.datosProgenitor.set(datosProgenitor);
	}
	public StringProperty datosProgenitorProperty(){
		return datosProgenitor;
	}
	public String getNotas(){
		return notas.get();
	}
	public void setNotas(String notas){
		this.notas.set(notas);
	}
	public StringProperty notasProperty(){
		return notas;
	}

	public void guardar()  throws Exception {
		dao.guardar(this);
	}

	public void borrar()  throws Exception {
		this.dao.borrar(this);	
	}

	
	@Override
	public String toString() {
		return nombreCompleto.get() + " (" + id.get() + ")";
	}

	public void setFechaAlta(LocalDate fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	
	public LocalDate getFechaAlta() {
		return this.fechaAlta;
	}
	
	public void setFechaBaja(LocalDate fechaBaja) {
		this.fechaBaja = fechaBaja;
	}
	
	public LocalDate getFechaBaja() {
		return this.fechaBaja;
	}
	
	
}
