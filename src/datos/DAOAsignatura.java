package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import interfaz.Controlador;
import negocio.Asignatura;

public enum DAOAsignatura {
	INSTANCE;
	// CREATE TABLE ASIGNATURA (ID int primary key auto_increment, NOMBRE
	// varchar(255))
	private final String TABLA = "ASIGNATURA";
	private final String COLUMNAS = "ID, NOMBRE";
	private final String SQL_CARGAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID=?";
	private final String SQL_BUSCAR_POR_NOMBRE = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE NOMBRE=?";
	private final String SQL_INSERTAR = "INSERT INTO " + TABLA + " (" + COLUMNAS + ") VALUES (NULL,?)";
	private final String SQL_ACTUALIZAR = "UPDATE " + TABLA + " SET (" + COLUMNAS + ") = (?,?) WHERE ID=?";
	private final String SQL_BORRAR = "DELETE FROM " + TABLA + " WHERE ID=?";
	private final String SQL_LISTAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY NOMBRE";
	private PreparedStatement psCargar, psBorrar, psInsertar, psActualizar, psListar, psBuscarPorId, psBuscarPorNombre;

	private DAOAsignatura(){
		try {
			this.psListar = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR);
			this.psBuscarPorId = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
			this.psBuscarPorNombre = BD.INSTANCE.getConexion().prepareStatement(SQL_BUSCAR_POR_NOMBRE);
			this.psCargar = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
			this.psInsertar = BD.INSTANCE.getConexion().prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
			this.psActualizar = BD.INSTANCE.getConexion().prepareStatement(SQL_ACTUALIZAR);
			this.psBorrar = BD.INSTANCE.getConexion().prepareStatement(SQL_BORRAR);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	public Asignatura buscarPorId(int id) throws Exception {
		Asignatura resultado = null;
		this.psBuscarPorId.setInt(1, id);
		ResultSet rs = this.psBuscarPorId.executeQuery();
		if (rs.next()) 
			resultado = this.get(rs);
		
		rs.close();
		return resultado;
	}

	public Asignatura buscarPorNombre(String nombre) throws Exception {
		Asignatura resultado = null;
		this.psBuscarPorId.setString(1, nombre);
		ResultSet rs = this.psBuscarPorNombre.executeQuery();
		if (rs.next()) 
			resultado = this.get(rs); 
			
		rs.close();
		return resultado;
	}

	public ArrayList<Asignatura> getLista() throws Exception {
		ArrayList<Asignatura> resultado = new ArrayList<>();
		ResultSet rs = this.psListar.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}

	public void guardar(Asignatura asignatura) throws Exception {
		if (asignatura.getId() == 0)
			this.insertar(asignatura);
		else
			this.actualizar(asignatura);
	}

	private void actualizar(Asignatura asignatura) throws Exception {
		this.psActualizar.setInt(1, asignatura.getId());
		this.psActualizar.setString(2, asignatura.getNombre());

		this.psActualizar.execute();
	}

	private void insertar(Asignatura asignatura) throws Exception {
		this.psInsertar.setString(1, asignatura.getNombre());

		this.psInsertar.execute();
		ResultSet rsNuevosIds = psInsertar.getGeneratedKeys();
		if (rsNuevosIds.first()) {
			asignatura.setId(((Long) rsNuevosIds.getLong(1)).intValue());
		} else {
			throw new Exception("En la inserción no se ha generado id.");
		}
		rsNuevosIds.close();
	}
	
	private Asignatura get(ResultSet rs) throws Exception{
		Asignatura item = new Asignatura();
		item.setId(rs.getInt("ID"));
		item.setNombre(rs.getString("NOMBRE"));
		return item;
	}

}
