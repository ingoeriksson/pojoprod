package traktdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.JsonObject;

import dbins.DbProcs;
import dbins.DbUtil;
import fsjson.Feature;
import fsjson.GeoJson;
import fsjson.Geometry;

/**
 * Servlet implementation class HprImputor
 */
@WebServlet("/stemfetcher")
public class StemFetcher extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StemFetcher() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
    	
		String aid=request.getParameter("aid");
		String opt=request.getParameter("opt");
		//System.out.println("stemfetcher has been called "+aid+ " "+opt);
		//String vonr=request.getParameter("vonr");
		String sql=null;
		
		sql="select coalesce(vo,0) from fore.place where id="+aid;
		int vonr=DbProcs.getIntFromDatabase(sql, conn);

		sql="with a as (select ST_asGeoJson(geom),St_X(geom) as X,ST_Y(geom) as Y,(geom is not null) as hascoord,stem_code from sfd.stem where "+
					"(machinekey,objectkey) in (select machinekey,objectkey from sfd.object_definition where contract_number ="
					+vonr+")),"
					+" b as (select a.*,lower(sci.label) as label from a left outer join codes.stem_codes as sci on a.stem_code=sci.id) ";
		switch (opt){
		case "alla":sql+=" select * from b";break;
		case "hog":sql+=" select * from b where label like '%gstubbe%'";break;
		case "kultur":sql+=" select * from b  where label like '%kultur%'";break;
		case "evighet":sql+=" select * from b  where label like '%evighet%'";break;
		case "grupp":sql+=" select * from b  where label like '%grupp%'";break;
		}

		System.out.println(sql);
		
		ResultSet rs1 =conn.createStatement().executeQuery(sql);
		GeoJson json=new GeoJson();
		//System.out.println("loopar stammar");
		double x=0;double y=0;int n=0;int all=0;
		while (rs1.next()){
			all++;

			if (rs1.getBoolean(4)){
					x=x+rs1.getFloat(2);
					y=y+rs1.getFloat(3);
					n++;
				
	
				Feature feat=new Feature();
				//String geom=rsGeo.getString(1);
				Geometry geom=new Geometry();
				String geomstr=rs1.getString(1);
				geom.parseFromString(geomstr);
				feat.setGeometry(geom);
				//feat.setId(rs1.getString("id"));
		
	//			feat.addProp("place_id", rsGeo.getString("id"));
	//			feat.addProp("place_name", rsGeo.getString("place_name"));
	//			feat.addProp("inv_met", rsGeo.getString("inventory_method"));
	//			
					json.addFeat(feat);

			}
		}
		if (n>0){
			x=x/n;
			y=y/n;
			resp.put("x", x);
			resp.put("y", y);
		}
		resp.put("stemswithcoord", n);
		resp.put("countstems", all);
		rs1.close();
		json.addCrsData("name", "EPSG:3006");
		
		resp.put("geoms", json);
		return true;

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
