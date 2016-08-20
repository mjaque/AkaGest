package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import interfaz.Controlador;
import negocio.Alumno;

public enum DAOAlumno {
	INSTANCE;
	
	private final String TABLA = "ALUMNO";
	//private final String COLUMNAS = TABLA + ".ID, " + TABLA + ".NIF, " + TABLA + ".NOMBRE_COMPLETO, " + TABLA + ".EMAIL, " + TABLA + ".TELEFONOS, " + TABLA + ".CENTRO_ESTUDIOS, " + TABLA + ".DATOS_PROGENITOR, " + TABLA + ".NOTAS";
	private final String COLUMNAS = "ID, NIF, NOMBRE_COMPLETO, EMAIL, TELEFONOS, CENTRO_ESTUDIOS, DATOS_PROGENITOR, NOTAS";
	private final String SQL_CARGAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID=?";
	private final String SQL_INSERTAR = "INSERT INTO " + TABLA + " (" + COLUMNAS
			+ ") VALUES (NULL,?,?,?,?,?,?,?)";
	private final String SQL_ACTUALIZAR = "UPDATE " + TABLA + " SET (" + COLUMNAS
			+ ") = (?,?,?,?,?,?,?,?) WHERE ID=?";
	private final String SQL_BORRAR = "DELETE FROM " + TABLA + " WHERE ID=?";
	private final String SQL_LISTAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY UPPER(NOMBRE_COMPLETO) ASC";
	private final String SQL_BUSCAR_POR_ID = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID = ?";
	private final String SQL_BUSCAR_POR_NOMBRE = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE UPPER(NOMBRE_COMPLETO) LIKE ?";
	//private this.final String SQL_BUSCAR_HOY = "SELECT " + COLUMNAS + " FROM " + TABLA + " JOIN CLASE ON ALUMNO.ID = CLASE.ID_ALUMNO WHERE CLASE.FECHA = CURRENT_DATE() "
	//		+ "ORDER BY CLASE.HORA ASC";
	private final String SQL_BUSCAR_HOY = "SELECT ALUMNO.ID, ALUMNO.NIF, ALUMNO.NOMBRE_COMPLETO, ALUMNO.EMAIL, ALUMNO.TELEFONOS, ALUMNO.CENTRO_ESTUDIOS, ALUMNO.DATOS_PROGENITOR, ALUMNO.NOTAS FROM ALUMNO JOIN CLASE ON ALUMNO.ID = CLASE.ID_ALUMNO WHERE CLASE.FECHA = CURRENT_DATE() ORDER BY CLASE.HORA ASC";
	private PreparedStatement psInsertar, psActualizar, psBorrar, psCargar, psListar, psBuscarPorId, psBuscarPorNombre, psBuscarHoy;

	private DAOAlumno(){
		try {
			this.psListar = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR);
			this.psBuscarPorId = BD.INSTANCE.getConexion().prepareStatement(SQL_BUSCAR_POR_ID);
			this.psBuscarPorNombre = BD.INSTANCE.getConexion().prepareStatement(SQL_BUSCAR_POR_NOMBRE);
			this.psBuscarHoy = BD.INSTANCE.getConexion().prepareStatement(SQL_BUSCAR_HOY);
			this.psCargar = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
			this.psInsertar = BD.INSTANCE.getConexion().prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
			this.psActualizar = BD.INSTANCE.getConexion().prepareStatement(SQL_ACTUALIZAR);
			this.psBorrar = BD.INSTANCE.getConexion().prepareStatement(SQL_BORRAR);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	private void actualizar(Alumno alumno) throws Exception {
		this.psActualizar.setInt(1, alumno.getId());
		this.psActualizar.setString(2, alumno.getNif());
		this.psActualizar.setString(3, alumno.getNombreCompleto());
		this.psActualizar.setString(4, alumno.getEmail());
		this.psActualizar.setString(5, alumno.getTelefonos());
		System.out.println("alumno.getTelefonos() = " + alumno.getTelefonos());
		this.psActualizar.setString(6, alumno.getCentroEstudios());
		this.psActualizar.setString(7, alumno.getDatosProgenitor());
		this.psActualizar.setString(8, alumno.getNotas());
		this.psActualizar.setInt(9, alumno.getId());
		this.psActualizar.execute();
	}

	public void borrar(Alumno alumno) throws Exception {
		this.psBorrar.setInt(1, alumno.getId());
		this.psBorrar.execute();
	}

	public ArrayList<Alumno> buscar() {
		// TODO: Hacer el método cuando haga falta
		return null;
	}

	public Alumno cargar(Integer id) throws Exception {
		Alumno resultado = null;
		this.psCargar.setInt(1, id);
		ResultSet rs = this.psCargar.executeQuery();
		if (rs.next()) {
			resultado = this.get(rs);
		}
		rs.close();
		return resultado;
	}

	public void guardar(Alumno alumno) throws Exception {
		if (alumno.getId() == 0)
			this.insertar(alumno);
		else
			this.actualizar(alumno);
	}

	private void insertar(Alumno alumno) throws Exception {
		this.psInsertar.setString(1, alumno.getNif());
		this.psInsertar.setString(2, alumno.getNombreCompleto());
		this.psInsertar.setString(3, alumno.getEmail());
		this.psInsertar.setString(4, alumno.getTelefonos());
		this.psInsertar.setString(5, alumno.getCentroEstudios());
		this.psInsertar.setString(6, alumno.getDatosProgenitor());
		this.psInsertar.setString(7, alumno.getNotas());
		this.psInsertar.execute();
		ResultSet rsNuevosIds = psInsertar.getGeneratedKeys();
		if (rsNuevosIds.first()) {
			alumno.setId(((Long) rsNuevosIds.getLong(1)).intValue());
		} else {
			throw new Exception("En la inserción no se ha generado id.");
		}
		rsNuevosIds.close();
	}

	public ArrayList<Alumno> listar() throws Exception {
		ArrayList<Alumno> resultado = new ArrayList<>();

		ResultSet rs = this.psListar.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}

	public Alumno buscarPorId(int int1) throws Exception{
		Alumno resultado = null;

		ResultSet rs = null;
		this.psBuscarPorId.setInt(1, int1);
		rs = this.psBuscarPorId.executeQuery();
		if (rs.next()) {
			resultado = this.get(rs);
		}
		rs.close();
		return resultado;
	}
	
	public ArrayList<Alumno> buscarPorNombre(String nombre) throws Exception {
		ArrayList<Alumno> resultado = new ArrayList<>();

		this.psBuscarPorNombre.setString(1, "%" + nombre.toUpperCase() + "%");
		ResultSet rs = this.psBuscarPorNombre.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	//TODO: Unificar estos métodos. Son todos iguales. Con un método Alumno get(consulta) y  otro List<Alumno> get(consulta). Enumerar las consultas. Pero algunos son static, tiene parámetros ¿¿??.
	public ArrayList<Alumno> buscarHoy() throws Exception {
		ArrayList<Alumno> resultado = new ArrayList<>();

		ResultSet rs = this.psBuscarHoy.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public ArrayList<Alumno> getLista() throws Exception {
		ArrayList<Alumno> resultado = new ArrayList<>();
		ResultSet rs = this.psListar.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	private Alumno get(ResultSet rs) throws Exception{
		Alumno item = new Alumno();
		item.setId(rs.getInt("ID"));
		item.setNif(rs.getString("NIF"));
		item.setNombreCompleto(rs.getString("NOMBRE_COMPLETO"));
		item.setEmail(rs.getString("EMAIL"));
		item.setTelefonos(rs.getString("TELEFONOS"));
		item.setCentroEstudios(rs.getString("CENTRO_ESTUDIOS"));
		item.setDatosProgenitor(rs.getString("DATOS_PROGENITOR"));
		item.setNotas(rs.getString("NOTAS"));
		return item;
	}


}
