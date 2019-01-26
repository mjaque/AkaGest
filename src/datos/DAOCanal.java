package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import interfaz.Controlador;
import negocio.Alumno;
import negocio.Marketing;

public enum DAOCanal {
	INSTANCE;

	// ID int primary key auto_increment, ID_ALUMNO int, ID_ASIGNATURA int
	// REFERENCES ASIGNATURA, FECHA date, HORA time, DURACION decimal(5,2),
	// PRECIO decimal(5,2), ID_ESTADO int REFERENCES ESTADO_CLASE, ASISTENCIA
	// boolean, NOTAS text
	private final String TABLA = "MARKETING";
	private final String COLUMNAS = "ID, CANAL";
	private final String SQL_LISTAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY ID";;
	private final String SQL_CARGAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID=?";
	private final String SQL_BUSCAR_POR_ID = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID = ?";

	private PreparedStatement psListar, psCargar;

	private DAOCanal() {
		try {
			this.psListar = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR);
			this.psCargar = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}
	}

	public ArrayList<Marketing> listar() throws Exception {
		ArrayList<Marketing> resultado = new ArrayList<>();
		ResultSet rs = this.psListar.executeQuery();
		while (rs.next())
			resultado.add(this.get(rs));

		rs.close();
		return resultado;
	}

	private Marketing get(ResultSet rs) throws Exception {
		Marketing item = new Marketing();
		item.setId(rs.getInt("ID"));
		item.setCanal(rs.getString("CANAL"));
		return item;
	}
	
	public Marketing cargar(Integer id) throws Exception {
		Marketing resultado = null;
		this.psCargar.setInt(1, id);
		ResultSet rs = this.psCargar.executeQuery();
		if (rs.next()) {
			resultado = this.get(rs);
		}
		rs.close();
		return resultado;
	}
}
