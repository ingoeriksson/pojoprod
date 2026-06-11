package dbins;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import javax.sql.DataSource;




public class DbUtil {
	public static Connection getConnectionToBlackbox(String dbName){
		Connection c = null;
		String host="localhost";
		try {

			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://192.168.1.245:5432/"+dbName+"?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);
//			String searchPath="";
//			switch (dbName){
//				case "hprdatabase":searchPath="public,hpr,yield,fstand,forest, pg_catalog";break;
//				case "traktdata":searchPath="public,fore,pg_catalog";break;
//				case "skosTest":searchPath="public,skos,fstand";
//			}
//			c.createStatement().execute("SET search_path TO "+searchPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToLocal(String dbName){
		Connection c = null;
		String host="localhost";
		try {

			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://localhost:5432/"+dbName+"?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);
//			String searchPath="";
//			switch (dbName){
//				case "hprdatabase":searchPath="public, pg_catalog";break;
//				case "skosTest":searchPath="public,skos,fstand";
//			}
//			c.createStatement().execute("SET search_path TO "+searchPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToThunderStorm(String dbName){
		Connection c = null;
		String host="localhost";
		System.out.println("thunderstorm invoked");
		try {

			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://192.168.1.115:5432/"+dbName+"?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToSF(String dbName){
		Connection c = null;
		String host="skordardatabas.skogforsk.se";
		try {

			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://"+host+":5432/"+dbName+"?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToAzure(String dbName){
		Connection c = null;
		
		try {
			//System.out.println("syspwd"+System.getenv("AZCRED"));
			String host="db-forestcore-postgreraw.postgres.database.azure.com";
			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://"+host+":5432/"+dbName+"?user=sqladmin&password=Gs!WGbx2aiR#gCLN9HC2hS4#";
        
			c = DriverManager.getConnection(url);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static Connection getConnectionToSkos(){
		Connection c = null;
		try {
			//Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			//String url = "jdbc:postgresql://localhost:5432/skosdatabase?user=postgres&password="+System.getenv("PGSTDCRED");
			String host="db-forestcore-postgreraw-debug.postgres.database.azure.com";
			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://"+host+":5432/"+"skosdatabase"+"?user=sqladmin&password=fPGFUG!SqEeeKV!r6#Su1wMf";
        
			c = DriverManager.getConnection(url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToFL(String dbName){
		Connection c = null;
		String host="localhost";
		try {

			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://217.198.151.236:5432/"+dbName+"?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);
			String searchPath="";

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Connection getConnectionToSkosTest(){
		Connection c = null;
		try {
			Class.forName("org.postgresql.Driver");//C:\JAVA\divjars\postgresql-9.4-1200.jdbc4.jar
			String url = "jdbc:postgresql://localhost:5432/skosTest?user=postgres&password="+System.getenv("PGSTDCRED");
        
			c = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	//kolla web.xml i web-inf och context.xml i meta-inf i hprdemo
	//http://tomcat.apache.org/tomcat-8.0-doc/jndi-datasource-examples-howto.html

	public static Connection getConnectionToPg(String server){
		Connection c = null;
		String resourceName;
		//System.out.println("Server in connection"+server);

		try {
			

				resourceName="java:comp/env/jdbc/postgres";

				InitialContext cxt = new InitialContext();
				
//				Map map=toMap(cxt);
//				map.forEach((k,v)->System.out.println(k+" "+v));

				DataSource ds = (DataSource) cxt.lookup(resourceName);
				
				if ( ds == null ) {
				   throw new Exception("Data source not found!");
				} 
				
				c=ds.getConnection();

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public static Map toMap(Context ctx) throws NamingException {
	    String namespace = ctx instanceof InitialContext ? ctx.getNameInNamespace() : "";
	    HashMap<String, Object> map = new HashMap<String, Object>();
	    //System.out.println("> Listing namespace: " + namespace);
	    NamingEnumeration<NameClassPair> list = ctx.list(namespace);
	    while (list.hasMoreElements()) {
	        NameClassPair next = list.next();
	        String name = next.getName();
	        String jndiPath = namespace + name;
	        Object lookup;
	        try {
	        	//System.out.println("> Looking up name: " + jndiPath);
	            Object tmp = ctx.lookup(jndiPath);
	            if (tmp instanceof Context) {
	                lookup = toMap((Context) tmp);
	            } else {
	                lookup = tmp.toString();
	            }
	        } catch (Throwable t) {
	            lookup = t.getMessage();
	        }
	        map.put(name, lookup);

	    }
	    return map;
	}
	
	public static void main(String[] args) throws SQLException {
		Connection conn= getConnectionToAzure("hprdatabase");
		conn.close();
	}
    


}
