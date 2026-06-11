package fsjson;

import java.util.Comparator;

import com.github.cliftonlabs.json_simple.JsonArray;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import com.github.cliftonlabs.json_simple.JsonObject;



/*geometry *************************/
public class Geometry extends FsJSONObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Geometry (String type){
		this.put("type", type);
	}
	public Geometry (){
		//
	}
	
	public void parseFromString(String geomstr){
		JsonObject j=new JsonObject();
		//JSONParser parser = new JSONParser();
		try {
			j=(JsonObject) com.github.cliftonlabs.json_simple.Jsoner.deserialize(geomstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.put("type", j.get("type"));
		this.put("coordinates", j.get("coordinates"));
		//System.out.println("inside "+this.get("coordinates"));
	}
	
	public void setCoordinates(JsonArray coordinates){
		this.put("coordinates", coordinates);
	}
	
	public void sortRings(){
		JsonArray ja=(JsonArray) this.get("coordinates");
		if (this.get("type").equals("MultiPolygon")){
			for (Object ja2:ja){
				sortPolygonRings((JsonArray) ja2);
			}
		}else{
			sortPolygonRings(ja);
		}
	}
	

	
	private void sortPolygonRings(JsonArray polygon){
		JsonArray p=(JsonArray) polygon.get(0);
		JsonArray m=null;
		for (Object o:polygon){
			
			m=(JsonArray) o;
			if (m==p) continue;
			if (calcBoundingBox(m)[0]<calcBoundingBox(p)[0]){
				p=m;
			}
			
		}
		if (!p.equals(polygon.get(0))){
			polygon.remove(p);
			polygon.add(0, p);
		}
		

		//System.out.println("ring1:"+((JSONArray) polygon.get(0)).size());
		//System.out.println("ring2:"+((JSONArray) polygon.get(1)).size());
	}
	
	private double[] calcBoundingBox(JsonArray coords){
		double xmin=Double.MAX_VALUE;
		double xmax=Double.MIN_VALUE;
		double ymin=Double.MAX_VALUE;
		double ymax=Double.MIN_VALUE;
		
		for (Object ja:coords){
			JsonArray pair=(JsonArray) ja;
			xmin=Math.min(xmin, (double) pair.get(0));
			ymin=Math.min(ymin, (double) pair.get(1));
			xmax=Math.max(xmax, (double) pair.get(0));
			ymax=Math.max(ymax, (double) pair.get(1));

		}
		
		
		double[] d=new double[4];
		d[0]=xmin;
		d[1]=xmax;
		d[2]=ymin;
		d[3]=ymax;
		//System.out.println(d[0]+"");
		return d;
		
	}
	
	public static void main(String[] args) {
		Geometry g=new Geometry("Point");
		JsonPoint a=new JsonPoint(222,333);
		g.setCoordinates(a);
		System.out.println(""+g.toJson());
		
	}

	
}
