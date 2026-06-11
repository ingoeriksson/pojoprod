package traktdata;

import java.io.IOException;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.JsonObject;

import fsjson.Feature;
import fsjson.GeoJson;
import fsjson.Geometry;

/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/TracklogGetter")
public class TracklogGetter extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TracklogGetter() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		String restrictAmount=request.getParameter("restrictamount");
		String restrictType=request.getParameter("restricttype");
		String opt=request.getParameter("opt");
		//System.out.println("opt "+opt);
		if (opt.equals("1")){
			String sql="select ST_asGeoJSON(point) as pt from fore.tracklog where doshow=true and user_id="+usr_id;
			GeoJson json=new GeoJson();
	        ResultSet rs=conn.createStatement().executeQuery(sql);
	        int i=0;
	        while (rs.next()){
	        	//System.out.println("adding");
	        	i++;
				Feature feat=new Feature();

				Geometry geom=new Geometry();
				String geomstr="";
				geomstr=rs.getString(1);
				 
				geom.parseFromString(geomstr);
				feat.setGeometry(geom);
				feat.setId(String.valueOf(i));

				json.addFeat(feat);
	       	
	        }

			json.addCrsData("name", "EPSG:3006");
			//System.out.println(json.toJSONString());
			resp.put("feats", json);
			
		}else{
			String sql ="update fore.tracklog set doshow=false where user_id="+usr_id;
			conn.createStatement().executeUpdate(sql);
		}
		
		
		
		return true;
		
	}

}
