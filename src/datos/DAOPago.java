package datos;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import interfaz.Controlador;
import negocio.Alumno;
import negocio.Pago;

public enum DAOPago {
	INSTANCE;
	
	// ID int primary key auto_increment, ID_ALUMNO int REFERENCES ALUMNO, FECHA date, OPERACION varchar(100), RECIBO varchar(255), IMPORTE decimal(5,2), OBSERVACIONES text
	private final String TABLA = "PAGO";
	private final String COLUMNAS = "ID, ID_ALUMNO, FECHA, OPERACION, RECIBO, IMPORTE, OBSERVACIONES";
	private final String SQL_CARGAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE ID=?";
	private final String SQL_INSERTAR = "INSERT INTO " + TABLA + " (" + COLUMNAS + ") VALUES (NULL,?,?,?,?,?,?)";
	private final String SQL_ACTUALIZAR = "UPDATE " + TABLA + " SET (" + COLUMNAS + ") = (?,?,?,?,?,?,?) WHERE ID=?";
	private final String SQL_BORRAR = "DELETE FROM " + TABLA + " WHERE ID=?";
	private final String SQL_LISTAR = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY FECHA ASC";
	private final String SQL_LISTAR_POR_ALUMNO = "SELECT " + COLUMNAS + " FROM " + TABLA
			+ " WHERE ID_ALUMNO = ? ORDER BY FECHA ASC";
	private final String SQL_SUM_PAGOS_DE_ALUMNO = "SELECT SUM(IMPORTE) AS PAGADO FROM " + TABLA
			+ " WHERE ID_ALUMNO = ?";
	private final String SQL_ARQUEO_CAJA = "SELECT SUM(IMPORTE) FROM " + TABLA + " WHERE OPERACION IN ('Efectivo', 'Salida_Caja')";
	
	private PreparedStatement psCargar, psInsertar, psActualizar, psBorrar, psListar, psListarPorAlumno, psBuscar, psSumaPagosAlumno, psArqueoCaja;

	private DAOPago() {
		try {
			this.psListar = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR);
			this.psListarPorAlumno = BD.INSTANCE.getConexion().prepareStatement(SQL_LISTAR_POR_ALUMNO);
			this.psSumaPagosAlumno = BD.INSTANCE.getConexion().prepareStatement(SQL_SUM_PAGOS_DE_ALUMNO);
			this.psArqueoCaja = BD.INSTANCE.getConexion().prepareStatement(SQL_ARQUEO_CAJA);
			this.psCargar = BD.INSTANCE.getConexion().prepareStatement(SQL_CARGAR);
			this.psInsertar = BD.INSTANCE.getConexion().prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
			this.psActualizar = BD.INSTANCE.getConexion().prepareStatement(SQL_ACTUALIZAR);
			this.psBorrar = BD.INSTANCE.getConexion().prepareStatement(SQL_BORRAR);
		} catch (SQLException e) {
			e.printStackTrace();
			Controlador.lanzarExcepcion(e.getMessage());
		}

	}

	public void guardar(Pago item) throws Exception {
		if (item.getId() == 0)
			this.insertar(item);
		else
			this.actualizar(item);
	}

	private void insertar(Pago item) throws Exception {
		int i = 1;
		if (item.getAlumno() != null)
			this.psInsertar.setInt(i++, item.getAlumno().getId());
		else
			this.psInsertar.setNull(i++, Types.INTEGER);
		this.psInsertar.setDate(i++, Date.valueOf(item.getFecha()));
		this.psInsertar.setString(i++, item.getOperacion().toString());
		this.psInsertar.setString(i++, item.getRecibo());
		this.psInsertar.setFloat(i++, item.getImporte());
		this.psInsertar.setString(i++, item.getNotas());

		this.psInsertar.execute();
		ResultSet rsNuevosIds = psInsertar.getGeneratedKeys();
		if (rsNuevosIds.first()) {
			item.setId(((Long) rsNuevosIds.getLong(1)).intValue());
		} else {
			throw new Exception("En la inserción no se ha generado id.");
		}
		rsNuevosIds.close();
	}

	private void actualizar(Pago item) throws Exception {
		int i = 1;
		this.psActualizar.setInt(i++, item.getId());
		if (item.getAlumno() != null)
			this.psActualizar.setInt(i++, item.getAlumno().getId());
		else
			this.psActualizar.setNull(i++, Types.INTEGER);
		this.psActualizar.setDate(i++, Date.valueOf(item.getFecha()));
		this.psActualizar.setString(i++, item.getOperacion().toString());
		this.psActualizar.setString(i++, item.getRecibo());
		this.psActualizar.setFloat(i++, item.getImporte());
		this.psActualizar.setString(i++, item.getNotas());
		this.psActualizar.setInt(i++, item.getId());

		this.psActualizar.execute();
	}

	public ArrayList<Pago> listar() throws Exception {
		ArrayList<Pago> resultado = new ArrayList<>();

		ResultSet rs = this.psListar.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}
	
	public ArrayList<Pago> listarPorAlumno(Alumno alumno) throws Exception {
		ArrayList<Pago> resultado = new ArrayList<>();

		this.psListarPorAlumno.setInt(1, alumno.getId());
		ResultSet rs = this.psListarPorAlumno.executeQuery();
		while (rs.next()) 
			resultado.add(this.get(rs));
		
		rs.close();
		return resultado;
	}

	public void borrar(Pago item) throws Exception {
		this.psBorrar.setInt(1, item.getId());
		this.psBorrar.execute();
	}

	public Float verPagosAlumno(Alumno alumno) throws Exception {
		Float resultado = null;

		this.psSumaPagosAlumno.setInt(1, alumno.getId());
		ResultSet rs = this.psSumaPagosAlumno.executeQuery();
		if (rs.next()) {
			resultado = rs.getFloat(1);
		}
		rs.close();
		return resultado;
	}
	
	private Pago get(ResultSet rs) throws Exception{
		Pago item = new Pago();
		item.setId(rs.getInt("ID"));
		//TODO: Mejorar. Hay consulta SQL anidada en bucle.
		item.setAlumno(DAOAlumno.INSTANCE.buscarPorId(rs.getInt("ID_ALUMNO")));
		item.setFecha(rs.getDate("FECHA").toLocalDate());
		item.setOperacion(Pago.Operacion.valueOf(rs.getString("OPERACION")));
		item.setRecibo(rs.getString("RECIBO"));
		item.setImporte(rs.getFloat("IMPORTE"));
		item.setNotas(rs.getString("OBSERVACIONES"));
		return item;
	}

	public Float verArqueoCaja() throws Exception{
		Float resultado = null;

		ResultSet rs = this.psArqueoCaja.executeQuery();
		if (rs.next()) 
			resultado = rs.getFloat(1);
		
		rs.close();
		return resultado;
	}

}
