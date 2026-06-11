package traktdata;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import fsjson.Feature;
import fsjson.GeoJson;
import fsjson.Geometry;

/**
 * Servlet implementation class ProjTDMall
 */
@WebServlet("/FeatureFetcher")
public class FeatureFetcher extends ProjSuper {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeatureFetcher() {
        super();
        // TODO Auto-generated constructor stub
    }

	public  boolean doTheThing(JsonObject resp,HttpServletRequest request) throws Exception{
		GeoJson json=new GeoJson();

        

        String sql="select ST_asGeoJson(polygon) pg,  ST_asGeoJson(line) ls, "+
    			" ST_asGeoJson(point) pt,id,anytext from fore.place where user_id="+
    			usr_id+ "  order by verticalorder";
       // System.out.println(sql);
        ResultSet rs=conn.createStatement().executeQuery(sql);
        while (rs.next()){
			Feature feat=new Feature();

			Geometry geom=new Geometry();
			String geomstr="";
			if (rs.getString("pg")!=null){
				geomstr=rs.getString("pg");
			} else if (rs.getString("ls")!=null){
				geomstr=rs.getString("ls");
			} else if (rs.getString("pt")!=null){
				geomstr=rs.getString("pt");
			}
			 
			geom.parseFromString(geomstr);
			feat.setGeometry(geom);
			feat.setId(rs.getString("id"));
			//System.out.println(rs.getString("id"));
	
			feat.addProp("place_id", rs.getString("id"));
			feat.addProp("freetext", rs.getString("anytext"));
			
			
			JsonArray concepts=new JsonArray();
			sql="select * from fore.view_concepts where place_id="+rs.getString("id");
			//System.out.println(sql);
			ResultSet rsConcepts=conn.createStatement().executeQuery(sql);
	        while (rsConcepts.next()){
	        	JsonObject jobj=new JsonObject();
	        	jobj.put("scheme", rsConcepts.getString("concept_scheme"));
	        	jobj.put("concept", rsConcepts.getString("concept"));
	        	concepts.add(jobj);
	        }
			
			feat.addProp("concepts", concepts);
			
//			sql="select * from fore.traktinfo where place_id="+rs.getString("id");
//			//System.out.println(sql);
//			ResultSet rsTraktinfo=conn.createStatement().executeQuery(sql);
//	        if (rsConcepts.next()){
//	        	feat.addProp("tn", rsTraktinfo.getString("object_name"));
//	        	feat.addProp("vo", rsTraktinfo.getString("contract_number"));
//	        	feat.addProp("oui", rsTraktinfo.getString("object_user_id"));
//	        	
//	        }
			

			json.addFeat(feat);
       	
        }
		json.addCrsData("name", "EPSG:3006");
		rs.close();
		resp.put("feats", json);
		
		
		
		json=new GeoJson();
		sql="with a as (select st_asgeojson(polygon) as geomstr,id,vo,main_concept from fore.followup_place where st_area(polygon)>=50 and user_id="+usr_id+"),"
				+ "b as (select st_asgeojson(geom) as geomstr,round (random()*10000) as id,vo,'avverkad'::text as main_concept from fore.followup_trakt where user_id="+usr_id+")"
				+ "select * from a union select * from b";
		//System.out.println(sql);
        rs=conn.createStatement().executeQuery(sql);
        while (rs.next()){
        	if (rs.getString("geomstr")==null) continue;
			Feature feat=new Feature();

			Geometry geom=new Geometry();
			String geomstr=rs.getString("geomstr");
			String vo=rs.getString("vo");
			 
			geom.parseFromString(geomstr);
			feat.setGeometry(geom);
			feat.setId(rs.getString("id"));
			//System.out.println(rs.getString("id"));
	
			feat.addProp("concept", rs.getString("main_concept"));
			feat.addProp("vo", rs.getString("vo"));
			
			json.addFeat(feat);
       	
        }
        rs.close();
		json.addCrsData("name", "EPSG:3006");
		
		//System.out.println(json.toJSONString());
		resp.put("followupFeats", json);
    	
    	return true;

		
	}

}
