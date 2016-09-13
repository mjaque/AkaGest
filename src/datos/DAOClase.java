package datos;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;

import interfaz.Controlador;
import negocio.Alumno;
import negocio.Clase;

public enum DAOClase {
	INSTANCE;
	
	// ID int primary key auto_increment, ID_ALUMNO int, ID_ASIGNATURA int
	// REFERENCES ASIGNATURA, FECHA date, HORA time, DURACION decimal(5,2),
	// PRECIO decimal(5,2), ID_ESTADO int REFERENCES ESTADO_CLASE, ASISTENCIA
	// boolean, NOTAS text
	private final String TABLA = "CLASE";
	private final String COLUMNAS = "ID, ID_ALUMNO, ID_ASIGNATURA, FECHA, HORA, DURACION, PRECIO, ESTADO, ASISTENCIA, NOTAS";
	private final String SQL_CARGAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID=?";
	private final String SQL_INSERTAR = "INSERT INTO " + TABLA + " (" + COLUMNAS
			+ ") VALUES (NULL,?,?,?,?,?,?,?,?,?)";
	private final String SQL_ACTUALIZAR = "UPDATE " + TABLA + " SET (" + COLUMNAS
			+ ") = (?,?,?,?,?,?,?,?,?,?) WHERE ID=?";
	private final String SQL_BORRAR = "DELETE FROM " + TABLA + " WHERE ID=?";
	private final String SQL_LISTAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY FECHA, HORA ASC";
	private final String SQL_LISTAR_POR_ALUMNO = "SELECT " + COLUMNAS + " FROM " + TABLA
			+ " WHERE ID_ALUMNO = ? ORDER BY FECHA, HORA ASC";
	private final String SQL_CONTRATADO_POR_ALUMNO = "SELECT SUM(PRECIO * DURACION) FROM " + TABLA
			+ " WHERE ID_ALUMNO = ?";
	private PreparedStatement psCargar, psInsertar, psActualizar, psBorrar, psListar, psListarPorAlumno, psBuscar, psContratadoPorAlumno;

	private DAOClase(){
		try {
			this.psListar = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR);
			this.psListarPorAlumno = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR_POR_ALUMNO);
			this.psContratadoPorAlumno = BD.INSTANCE.getConexion().prepareStatement(SQL_CONTRATADO_POR_ALUMNO);
			this.psCargar = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
			this.psInsertar = BD.INSTANCE.getConexion().prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
			this.psActualizar = BD.INSTANCE.getConexion().prepareStatement(SQL_ACTUALIZAR);
			this.psBorrar = BD.INSTANCE.getConexion().prepareStatement(SQL_BORRAR);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	public void guardar(Clase clase) throws Exception {
		if (clase.getId() == 0)
			this.insertar(clase);
		else
			this.actualizar(clase);
	}

	private void insertar(Clase clase) throws Exception {
		int i = 1;
		if (clase.getAlumno() != null)
			this.psInsertar.setInt(i++, clase.getAlumno().getId());
		else
			this.psInsertar.setNull(i++, Types.INTEGER);
		if (clase.getAsignatura() != null)
			this.psInsertar.setInt(i++, clase.getAsignatura().getId());
		else
			this.psInsertar.setNull(i++, Types.INTEGER);
		this.psInsertar.setDate(i++, Date.valueOf(clase.getFechaHora().toLocalDate()));
		this.psInsertar.setTime(i++, Time.valueOf(clase.getFechaHora().toLocalTime()));
		this.psInsertar.setFloat(i++, clase.getDuracion());
		this.psInsertar.setFloat(i++, clase.getPrecioHora());
		this.psInsertar.setString(i++, clase.getEstado().toString());
		this.psInsertar.setBoolean(i++, clase.isAsistencia());
		this.psInsertar.setString(i++, clase.getNotas());

		this.psInsertar.execute();
		ResultSet rsNuevosIds = psInsertar.getGeneratedKeys();
		if (rsNuevosIds.first()) {
			clase.setId(((Long) rsNuevosIds.getLong(1)).intValue());
		} else {
			throw new Exception("En la inserción no se ha generado id.");
		}
		rsNuevosIds.close();
	}

	private void actualizar(Clase clase) throws Exception {
		int i = 1;
		this.psActualizar.setInt(i++, clase.getId());
		if (clase.getAlumno() != null)
			this.psActualizar.setInt(i++, clase.getAlumno().getId());
		else
			this.psActualizar.setNull(i++, Types.INTEGER);
		if (clase.getAsignatura() != null)
			this.psActualizar.setInt(i++, clase.getAsignatura().getId());
		else
			this.psActualizar.setNull(i++, Types.INTEGER);
		this.psActualizar.setDate(i++, Date.valueOf(clase.getFechaHora().toLocalDate()));
		this.psActualizar.setTime(i++, Time.valueOf(clase.getFechaHora().toLocalTime()));
		this.psActualizar.setFloat(i++, clase.getDuracion());
		this.psActualizar.setFloat(i++, clase.getPrecioHora());
		this.psActualizar.setString(i++, clase.getEstado().toString());
		this.psActualizar.setBoolean(i++, clase.isAsistencia());
		this.psActualizar.setString(i++, clase.getNotas());
		this.psActualizar.setInt(i++, clase.getId());

		this.psActualizar.execute();

	}

	public ArrayList<Clase> listar() throws Exception {
		ArrayList<Clase> resultado = new ArrayList<>();

		ResultSet rs = this.psListar.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public ArrayList<Clase> listarPorAlumno(Alumno alumno) throws Exception {
		ArrayList<Clase> resultado = new ArrayList<>();

		this.psListarPorAlumno.setInt(1, alumno.getId());
		ResultSet rs = this.psListarPorAlumno.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public Float verContratadoAlumno(Alumno alumno) throws Exception{
		Float resultado = null;

		this.psContratadoPorAlumno.setInt(1, alumno.getId());
		ResultSet rs = this.psContratadoPorAlumno.executeQuery();
		if (rs.next()) 
			resultado = rs.getFloat(1);
			
		rs.close();
		return resultado;
	}

	public void borrar(Clase clase) throws Exception {
		this.psBorrar.setInt(1, clase.getId());
		this.psBorrar.execute();

	}
	
	private Clase get(ResultSet rs) throws Exception{
		Clase item = new Clase();
		item.setId(rs.getInt("ID"));
		//TODO: Mejora. Consulta SQL anidada en bucle.
		item.setAlumno(DAOAlumno.INSTANCE.buscarPorId(rs.getInt("ID_ALUMNO")));
		//TODO: Mejora. Consulta SQL anidada en bucle.			
		item.setAsignatura(DAOAsignatura.INSTANCE.buscarPorId(rs.getInt("ID_ASIGNATURA")));
		item.setFechaHora(LocalDateTime.of(rs.getDate("FECHA").toLocalDate(), rs.getTime("HORA").toLocalTime()));
		item.setDuracion(rs.getFloat("DURACION"));
		item.setPrecioHora(rs.getFloat("PRECIO"));
		item.setEstado(Clase.Estado.valueOf(rs.getString("ESTADO")));
		item.setAsistencia(rs.getBoolean("ASISTENCIA"));
		item.setNotas(rs.getString("NOTAS"));
		return item;
	}


}