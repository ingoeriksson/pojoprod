package traktdata;

import java.io.IOException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.*;

import dbins.DbProcs;

/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/FeatureUpdater")
public class FeatureUpdater extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureUpdater() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
    	String aid=request.getParameter("aid");
    	//System.out.println(aid);
    	String concepts=request.getParameter("concepts");
//    	System.out.println(concepts);
//    	String vo=request.getParameter("vo");
//    	String tn=request.getParameter("tn");
//    	String oui=request.getParameter("oui");
    	
    	
    	

    	JsonArray conceptsArray =  (JsonArray) Jsoner.deserialize(concepts);
    	
    	String afreetext=request.getParameter("freetext");
    	//System.out.println(afreetext);
    	
    	conn.createStatement().executeUpdate("delete from fore.place_concepts where place_id="+aid);
    	
    	for (int i=0;i<conceptsArray.size();i++){
    		JsonObject aSchemeAndConcept=(JsonObject) conceptsArray.get(i);
    		//String sql="select count(*) from fore.place_concepts where place_id="+aid+" and concept= '"+aSchemeAndConcept.get("concept")+"'";
    		//System.out.println(sql);
    		try {
        		String sql="insert into fore.place_concepts (place_id,concept) values ("+aid+",'"+aSchemeAndConcept.get("concept")+"')";
                //System.out.println(sql);
                conn.createStatement().execute(sql);
				
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("dublett "+e.getMessage());
			}
        	
    	}


    	String sql="update fore.place set anytext='"+afreetext+"' where id= "+aid;
    	//System.out.println(sql);
    	conn.createStatement().executeUpdate(sql);
    	
    	sql="select fore.setShoworder("+aid+",1)";
		conn.createStatement().executeQuery(sql);
    	
    	

    	
    	return true;
		
	}

}
