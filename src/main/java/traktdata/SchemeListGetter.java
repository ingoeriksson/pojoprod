package traktdata;

import java.sql.ResultSet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;



/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/SchemeListGetter")
public class SchemeListGetter extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SchemeListGetter() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
        //String inscheme=request.getParameter("inscheme");

        //System.out.println(inscheme);
        String sql="select scheme,label from fore.schemes where show_this=true order by sort_order";
        //System.out.println(sql);
        ResultSet rsSchemes=conn.createStatement().executeQuery(sql);
        
        JsonArray schemes=new JsonArray();
        while (rsSchemes.next()){
        	JsonObject scheme=new JsonObject();
        	scheme.put("label", rsSchemes.getString("label"));
        	scheme.put("scheme", rsSchemes.getString("scheme"));
        	JsonArray concepts=new JsonArray();
            
        	String sql1="select label,concept from fore.concepts where show_this=true and concept_scheme= '"+rsSchemes.getString("scheme")+"' order by label";
        	
        	ResultSet rsConcepts=conn.createStatement().executeQuery(sql1);
        	while (rsConcepts.next()){
        		JsonObject concept=new JsonObject();
            	concept.put("label", rsConcepts.getString("label"));
            	concept.put("concept", rsConcepts.getString("concept"));
            	concepts.add(concept);
            }
            scheme.put("concepts", concepts);
        	
            schemes.add(scheme);
        }
        resp.put("schemes", schemes);
    	
    	
    	return true;
		
	}

}
