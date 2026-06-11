package traktdata;

import java.io.IOException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.JsonObject;



/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/FeatureDeleter")
public class FeatureDeleter extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureDeleter() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
    	String aid=request.getParameter("aid");
    	//System.out.println("deleting "+aid);
    	Statement stmt=conn.createStatement();
    	int r=stmt.executeUpdate("delete from fore.place_concepts where place_id="+aid);
    	//r=stmt.executeUpdate("delete from fore.traktinfo where place_id="+aid);
    	r=stmt.executeUpdate("delete from fore.place where id="+aid);
    	
    	if (r>0) {
    		resp.put("status", "deleted");
    	}else{
    		resp.put("status", "not found");
    	}
    	return true;
		
	}

}
