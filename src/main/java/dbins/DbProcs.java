package dbins;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;






public class DbProcs {
	
	public static int insertInTable(Connection conn,String tab, String sql) throws SQLException{
		Statement stmt=conn.createStatement();
		
		stmt.executeUpdate(sql);
		ResultSet rs=stmt.executeQuery("SELECT currval(pg_get_serial_sequence('"+tab+"', 'id'))");
		rs.next();
		int object_id=rs.getInt(1);
		rs.close();
		stmt.close();
		return object_id;
	}
	
	public static int getIdForInsert(Connection conn,String tab) throws SQLException {
		ResultSet rs=conn.createStatement().executeQuery("SELECT currval(pg_get_serial_sequence('"+tab+"', 'id'))");
		rs.next();
		int object_id=rs.getInt(1);
		rs.close();
		return object_id;
	}
	

	


	public static BigDecimal roundToDouble(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_DOWN);
        return bd;
    }
	
	
	public static int getIntFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return Integer.MIN_VALUE;}
		;
		int r= rs.getInt(1);	
		rs.close();
		return r;
	}
	
	public static boolean getBoolFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return false;}
		;
		boolean r= rs.getBoolean(1);	
		rs.close();
		return r;
	}
	
	public static double getFloatFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return -2000000000;}
		double r= rs.getDouble(1);	
		rs.close();
		return r;
	}
	
	public static String getStringFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return null;}
		String r= rs.getString(1);	
		rs.close();
		return r;
	}
	
	public static LocalDateTime getLocalDateTimeFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return null;}
		LocalDateTime r= rs.getTimestamp(1).toLocalDateTime();	
		rs.close();
		return r;
	}
	
	public static String getSqlFromFile(String fname) throws IOException{
		String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fname))); 
	    return data; 
	}
	
	public static java.sql.Array getArrayFromDatabase(String sql,Connection conn) throws SQLException{
		ResultSet rs=conn.createStatement().executeQuery(sql);
		if (!rs.next()){return null;}
		java.sql.Array r= rs.getArray(1);	
		rs.close();
		return r;
	}

}
