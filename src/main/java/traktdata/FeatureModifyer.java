package traktdata;

import java.io.IOException;
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
@WebServlet("/FeatureModifyer")
public class FeatureModifyer extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureModifyer() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		String j=request.getParameter("feat");
		String geom=null;
		

		JsonObject respobj=new JsonObject();
		respobj =  (JsonObject) Jsoner.deserialize(j);
		if (respobj!=null){
			geom=((JsonObject) respobj.get("geometry")).toJson();
			//System.out.println("utskrift "+geom);
		}
		String sql="";
			
		sql="select * from  fore.modifyPolygons('"+geom+"',"+usr_id+")";
		//System.out.println(sql);
		geom=DbProcs.getStringFromDatabase(sql, conn);
		

		return true;

		
		
		
		
		
	}

}
