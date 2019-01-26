package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interfaz.Controlador;
import negocio.Clase;

public enum DAOConsulta {
	INSTANCE;
	
	private final String SQL_ALUMNOS_FACTURACION = "SELECT NOMBRE_COMPLETO, SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE JOIN ALUMNO ON ALUMNO.ID = CLASE.ID_ALUMNO GROUP BY ID_ALUMNO ORDER BY 2 DESC;";
	private final String SQL_ASIGNATURAS_FACTURACION = "SELECT NOMBRE, SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE JOIN ASIGNATURA ON ASIGNATURA.ID = CLASE.ID_ASIGNATURA GROUP BY ID_ASIGNATURA ORDER BY 2 DESC;";
	private final String SQL_HORARIOS_FACTURACION = "SELECT 'Mañana', SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE WHERE CAST(SUBSTRING(HORA, 0,2) AS INT) < 14 UNION SELECT 'Tarde', SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE WHERE CAST(SUBSTRING(HORA, 0,2) AS INT) > 14 UNION 	SELECT SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE;";
	private final String SQL_HORAS_FACTURACION = "SELECT CAST(SUBSTRING(HORA, 0,2) AS INT) AS Hora, SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE GROUP BY Hora ORDER 1 DESC;";
	private final String SQL_DIASSEMANAS_FACTURACION = "SELECT DAY_OF_WEEK(Fecha) AS Dia, SUM(DURACION * PRECIO) AS FACTURACION FROM CLASE GROUP BY Dia ORDER 1 DESC;";

	private PreparedStatement psAlumnosFacturacion, psAsignaturasFacturacion, psHorariosFacturacion, psHorasFacturacion, psDiasSemanaFacturacion;

	private DAOConsulta() {
		try {
			this.psAlumnosFacturacion = BD.INSTANCE.getConexion().prepareStatement(SQL_ALUMNOS_FACTURACION);
			this.psAsignaturasFacturacion = BD.INSTANCE.getConexion().prepareStatement(SQL_ASIGNATURAS_FACTURACION);
			this.psHorariosFacturacion = BD.INSTANCE.getConexion().prepareStatement(SQL_HORARIOS_FACTURACION);
			this.psHorasFacturacion = BD.INSTANCE.getConexion().prepareStatement(SQL_HORAS_FACTURACION);
			this.psDiasSemanaFacturacion = BD.INSTANCE.getConexion().prepareStatement(SQL_DIASSEMANAS_FACTURACION);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	private List get(ResultSet rs) {
		List resultado = new ArrayList<>();
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			for(int i = 0; i < numCols; i++)
				resultado.add(rs.getObject(i+1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	public List<List> verAlumnosPorFacturacion() throws Exception {
		List<List> resultado = new ArrayList<List>();

		String[] encabezados = {"Alumno", "Facturación"};
		resultado.add(Arrays.asList(encabezados));
		ResultSet rs = this.psAlumnosFacturacion.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public List<List> verAsignaturasPorFacturacion() throws Exception {
		List<List> resultado = new ArrayList<List>();
		
		String[] encabezados = {"Asignatura", "Facturación"};
		resultado.add(Arrays.asList(encabezados));
		ResultSet rs = this.psAsignaturasFacturacion.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}	
	
	public List<List> verHorariosPorFacturacion() throws Exception {
		List<List> resultado = new ArrayList<List>();
		
		String[] encabezados = {"Horario", "Facturación"};
		resultado.add(Arrays.asList(encabezados));
		ResultSet rs = this.psHorariosFacturacion.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}	
	
	public List<List> verHorasPorFacturacion() throws Exception {
		List<List> resultado = new ArrayList<List>();
		
		String[] encabezados = {"Hora", "Facturación"};
		resultado.add(Arrays.asList(encabezados));
		ResultSet rs = this.psHorasFacturacion.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public List<List> verDiasSemanaPorFacturacion() throws Exception {
		List<List> resultado = new ArrayList<List>();
		
		String[] encabezados = {"Día", "Facturación"};
		resultado.add(Arrays.asList(encabezados));
		ResultSet rs = this.psDiasSemanaFacturacion.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}	
}
