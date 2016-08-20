package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
	public static enum Nivel{TODOS, NINGUNO, TRAZA, DEPURACION, INFO, AVISO, ERROR, ERROR_FATAL};
	public static enum Salida{CONSOLA, FICHERO};
	private static Nivel nivel;
	private static Salida salida;
	
	static{
		Log.nivel = Nivel.valueOf(System.getProperty("akagest.log.nivel"));
		Log.salida = Salida.valueOf(System.getProperty("akagest.log.salida"));
	}
	
	public static void log(Nivel nivel, String mensaje){
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) +
				" " + nivel + 
				": " + mensaje);
	}

}
