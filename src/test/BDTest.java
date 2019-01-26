package test;

import static org.junit.Assert.*;
import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import datos.BD;

public class BDTest {

	private static String bdUrl = null;
	
	@Before
	public void setUp() throws Exception {
		System.out.println(1);
		if (System.getProperty("com.19e37.akagest.BD_URL") != null)
			BDTest.bdUrl = System.getProperty("com.19e37.akagest.BD_URL");
		System.setProperty("com.19e37.akagest.BD_URL", "jdbc:h2:/tmp/akagest_test_db;MV_STORE=FALSE;MVCC=FALSE");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("3");
		if (BDTest.bdUrl != null)
			System.setProperty("com.19e37.akagest.BD_URL", BDTest.bdUrl);
	}

	@Test
	public void test_getConexion() {
		System.out.println("2");
		assertNotNull("La conexión no debe ser nula", BD.INSTANCE.getConexion());
		assertTrue("La conexión debe ser de tipo Connection", BD.INSTANCE.getConexion() instanceof Connection);
	}

}
