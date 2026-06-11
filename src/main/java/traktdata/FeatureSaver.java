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
@WebServlet("/FeatureSaver")
public class FeatureSaver extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureSaver() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		String j=request.getParameter("feat");
		String geom=null;
		String t=request.getParameter("type");
		String msgType=request.getParameter("messagetype");
		

		JsonObject respobj=new JsonObject();
		respobj =  (JsonObject) Jsoner.deserialize(j);
		if (respobj!=null){
			geom=((JsonObject) respobj.get("geometry")).toJson();
			//System.out.println("utskrift "+geom);
		}
		String sql="";
		if (t.equals("Polygon")){
			
			sql="select * from  fore.getCroppedPolygon('"+geom+"',"+usr_id+")";
			//System.out.println(sql);
			geom=DbProcs.getStringFromDatabase(sql, conn);
		}
		
		
		switch (t){
			case "Polygon":sql="insert into fore.place (user_id,polygon,verticalorder) values "+
					"("+ usr_id + ",ST_SetSRID(ST_Multi(ST_GeomFromGeoJSON('"+geom+"')),3006),0)";break;
			case "LineString":sql="insert into fore.place (user_id,line,verticalorder) values "+
					"("+ usr_id + ",ST_SetSRID(ST_multi(ST_GeomFromGeoJSON('"+geom+"')),3006),0)";break;
			case "Point":sql="insert into fore.place (user_id,point,verticalorder) values "+
					"("+ usr_id + ",ST_SetSRID(ST_GeomFromGeoJSON('"+geom+"'),3006),0)";
		}

		
		//System.out.println(sql);

		int newId=DbProcs.insertInTable(this.conn,"fore.place",sql);
		
		// f�r test
		if (t.equals("Point")) conn.createStatement().executeUpdate("insert into fore.place_concepts (place_id,concept) values ("+newId+",'http://forestand.skogforsk.se/yttyper/expandera')");
		
		//System.out.println(newId);
		resp.put("newid", newId);


		return true;

		
	}

}
