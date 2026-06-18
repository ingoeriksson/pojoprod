package traktdata;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbins.DbProcs;
import dbins.DbUtil;
import global.EncodeHandler;

/**
 * Servlet implementation class ProjSuper
 */
@WebServlet("/ProjSuper")
public class ProjSuper extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected int usr_id;
	protected int proj_id;
	protected String token64;
	Connection conn;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProjSuper() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Called"+this.getClass().getSimpleName());
		JsonObject resp=new JsonObject();
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        conn=DbUtil.getConnectionToPg("forestlink");

		String token64=request.getHeader("token");
		if (token64==null||token64=="") {
			token64=request.getParameter("token");
		}
		this.token64=token64;
    	String token=EncodeHandler.decode64(token64);
    	//System.out.println("token "+token);
    	this.usr_id=checkUser(token,conn);
    	
    	if (!(usr_id>0)) {
    		try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		throw new ServletException("Access denied");
    	}

    	usr_id=proj_id;
    	
        
        try{
        	
        	if (!doTheThing(resp,request)){
    	        System.out.println("uppgiften kunde inte utforas");
    	    	return;       		
        	};
			conn.close();
			
			resp.put("status","ok");
			response.getWriter().write(resp.toJson());
		
	    }catch (Exception e){

	    	System.out.println(e.getMessage());
	    	e.printStackTrace();
	    	
	    }
	}
	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		return false;
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected int checkUser(String token,Connection conn){
		try {
			//Connection conn=DbUtil.getConnectionToLocal("hprdatabase");
			String sql="select user_id,user_name,project_id from api.tokens as a join api.users as b on a.user_id=b.id where token='"+token +"' and expires>localtimestamp";
			//System.out.println(sql);
			ResultSet rs=conn.createStatement().executeQuery(sql);
			int userId=0;
			String userName="";
			if (rs.next()){
				userId=rs.getInt(1);
				userName=rs.getString(2);
				proj_id=rs.getInt(3);
			}			
			rs.close();
			return userId;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
