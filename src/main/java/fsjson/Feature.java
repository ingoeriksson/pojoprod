package fsjson;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

/*Feature class***************/
public class Feature extends FsJSONObject{
	private FsJSONObject props=new FsJSONObject();
	private Geometry geom=null;
	
	public Feature(){
		super();
		//this.put("geometry_name", "geom");
		this.put("type", "Feature"); 
		this.put("properties", props);
	}
	
	public void setGeometry(Geometry geom){
		this.geom=geom;
		this.put("geometry", geom);
	}
	
	public void setProperties(JsonObject props){
		this.props=(FsJSONObject) props;
		this.put("properties", props);
	}
	
	public void addProp (String name, String value){
		props.put(name, value);
	}
	
	public void addProp (String name, double value){
		props.put(name, value);
	}
	
	public void addProp (String name, int value){
		props.put(name, value);
	}
	
	public void addProp (String name, JsonArray arr){
		props.put(name, arr);
	}
	
	public void setId (String strId){
		this.put("id", strId);
	}


	public FsJSONObject getProps() {
		return props;
	}

	public void addProp(String name, JsonObject jobj) {
		props.put(name, jobj);
		
	}
	
}
