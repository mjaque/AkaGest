package datos;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import interfaz.Controlador;
import util.Log;

/**
 * Singleton realizado con enum
 * 
 * @author mjaque
 *
 *         Nota: Consola de conexión a H2 con ~/Software/h2/bin/h2.sh
 */
public enum BD {
	INSTANCE;

	private static final String DRIVER = "org.h2.Driver";
	// Sin ;MV_STORE=FALSE;MVCC=FALSE - Crea BD db.mv.db que no se ve en el
	// console
	private static final String URL = System.getProperty("com.19e37.akagest.BD_URL");
	private static final String USUARIO = "";
	private static final String CLAVE = "";

	private static final String CREATE_ALUMNO = "CREATE TABLE ALUMNO (ID int primary key auto_increment, NIF varchar(15), NOMBRE_COMPLETO varchar(255)," + "EMAIL varchar(255), TELEFONOS varchar(255), CENTRO_ESTUDIOS TEXT, DATOS_PROGENITOR TEXT, NOTAS TEXT )";
	private static final String CREATE_ASIGNATURA = "CREATE TABLE ASIGNATURA (ID int primary key auto_increment, NOMBRE varchar(255))";
	private static final String CREATE_CLASE = "CREATE TABLE CLASE (ID int primary key auto_increment, ID_ALUMNO int, ID_ASIGNATURA int REFERENCES ASIGNATURA, FECHA date, HORA time, DURACION decimal(5,2), PRECIO decimal(5,2), ESTADO varchar(25), ASISTENCIA boolean, NOTAS text ) ";
	private static final String CREATE_TIPO_OPERACION = "CREATE TABLE TIPO_OPERACION (ID int primary key auto_increment, NOMBRE varchar(100))";
	private static final String CREATE_PAGO = "CREATE TABLE PAGO (ID int primary key auto_increment, ID_ALUMNO int REFERENCES ALUMNO, FECHA date, OPERACION varchar(100), RECIBO varchar(255), IMPORTE decimal(5,2), OBSERVACIONES text)";


	private Connection conexion = null;

	public Connection getConexion() {
		if (this.conexion == null) {
			try {
				Class.forName(DRIVER);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				this.conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
				DatabaseMetaData dbmd = this.conexion.getMetaData();
				ResultSet rs = dbmd.getTables(null, null, "ALUMNO", null);
				if (!rs.next())
					this.crearBD();
			} catch (Exception e) {
				e.printStackTrace();
				Controlador.lanzarExcepcion(e.getMessage());
			}
		}
		return this.conexion;
	}

	private void crearBD() {
		Log.log(Log.Nivel.INFO, "Creando base de datos.");
		try {
			Statement sentencia = this.conexion.createStatement();
			sentencia.execute(CREATE_ALUMNO);
			sentencia.execute(CREATE_ASIGNATURA);
			sentencia.execute(CREATE_CLASE);
			sentencia.execute(CREATE_TIPO_OPERACION);
			sentencia.execute(CREATE_PAGO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
