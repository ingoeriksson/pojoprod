package fsjson;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import dbins.DbProcs;

public class GeoJson extends FsJSONObject {
	
	public FsJSONArray featArray;
	private JsonObject crs=new JsonObject();

	public GeoJson(){
		super();
		this.put("type", "FeatureCollection");
		featArray=new FsJSONArray();
		this.put("features", this.featArray);
	}
	
	public void addCrsData(String crsType, String crsName){
		JsonObject objCrsType=new JsonObject();
		JsonObject objCrsName=new JsonObject();
    	objCrsName.put("name", crsName);
    	objCrsType.put("type", crsType);
    	objCrsType.put("properties",objCrsName);
    	this.put("crs", objCrsType);
	}
	
	public void addFeat(Feature feat){
		this.featArray.add(feat);
	}
	

	public static void main(String[] args) {
		GeoJson json=new GeoJson();
		Feature feat=new Feature();
		Geometry geom=new Geometry();
		geom.parseFromString("{\"type\":\"MultiPolygon\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:3006\"}},\"coordinates\":[[[[608178.834055119,6659250.026574803],[608192.586811025,6659250.697440944],[608193.257677166,6659231.242322833],[608175.47972441,6659233.59035433],[608170.112795276,6659247.343110235],[608178.834055119,6659250.026574803]]]]}");
		feat.setGeometry(geom);
		feat.addProp("FeatureCategory", "yttergrans");
		json.addFeat(feat);
		
		feat=new Feature();
		geom=new Geometry();
		feat.setGeometry(geom);
		feat.addProp("FeatureCategory", "basvag");
		geom.parseFromString("{\"type\":\"MultiLineString\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:3006\"}},\"coordinates\":[[[608063.614115879,6659296.929098207],[608079.672643996,6659276.644641638],[608113.480071611,6659271.573527495],[608133.76452818,6659258.89574214],[608162.500841652,6659249.598699546],[608176.023812698,6659244.527585404]]]}");
		json.addFeat(feat);
		System.out.println(json.toJson());
		
	}
	
	public String getGeometryUnion(Connection conn) throws SQLException {
		String sql="with a as (";
		for (int i=0;i<this.featArray.size()-1;i++) {
			Feature feat=(Feature) this.featArray.get(i);
			Geometry geom=(Geometry) feat.get("geometry");
			sql+= "select st_geomfromgeojson('"+geom.toJson()+"') as geom union ";
		}
		Feature feat=(Feature) this.featArray.get(this.featArray.size()-1);
		Geometry geom=(Geometry) feat.get("geometry");
		sql+= "select st_geomfromgeojson('"+geom.toJson()+"') as geom)";
		sql+= "select st_asgeojson(st_union(geom)) from a";
		//System.out.println(sql);
		return DbProcs.getStringFromDatabase(sql, conn);

	}


	

	

	






	
	

}
