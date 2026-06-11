package traktdata;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.JsonObject;



/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/FeatureRenderSequencer")
public class FeatureRenderSequencer extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureRenderSequencer() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		String aid=request.getParameter("aid");
		String opt=request.getParameter("opt");
		String sql="";

		sql="select fore.setShoworder("+aid+","+opt+") ";
		conn.createStatement().executeQuery(sql);
		
		
		return true;
		
	}

}
