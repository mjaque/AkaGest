package test;

import static org.junit.Assert.*;

import org.junit.Test;

import datos.DAOAlumno;
import negocio.Alumno;

public class DAOAlumnoTest {

	@Test
	public void testConstructor() {
		DAOAlumno dao;
		try {
			dao = DAOAlumno.INSTANCE;
			assertNotNull("El objeto no debe ser nulo.", dao);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInsertar_Cargar_Borrar() {
		try {
			DAOAlumno dao = DAOAlumno.INSTANCE;
			Alumno alumno = new Alumno();
			alumno.setNif("nif");
			alumno.setCentroEstudios("IES Virgen de Guadalupe");
			dao.guardar(alumno);
			assertNotNull("El alumno debe tener id.", alumno.getId());
			int id = alumno.getId();
			alumno.setNombreCompleto("Perico de los Palotes");
			dao.guardar(alumno);
			alumno = null;
			alumno = dao.cargar(id);
			assertNotNull("El alumno no puede ser nulo.", alumno);
			assertEquals("El id debe coincidir", id, alumno.getId());
			assertEquals("El nif debe coincidir", "nif", alumno.getNif());
			assertEquals("El Centro de Estudios debe coincidir", "IES Virgen de Guadalupe", alumno.getCentroEstudios());
			dao.borrar(alumno);
			alumno = dao.cargar(id);
			assertNull("El alumno debe ser nulo.", alumno);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
