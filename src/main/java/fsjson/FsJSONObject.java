package fsjson;

import com.github.cliftonlabs.json_simple.JsonObject;

//import org.json.simple.JSONObject;



public class FsJSONObject extends JsonObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FsJSONObject getFsJSON(String key) {
		try {
			 return (FsJSONObject) super.get(key);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getString(String key) {
		try {
			 return  super.get(key)+"";
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Integer getInt(String key) {
		try {
			return Integer.parseInt(super.get(key)+"");
		} catch (Exception e) {
			return null;
		}
	}
	
	public Boolean getBoolean(String key) {
		try {
			return Boolean.parseBoolean(super.get(key)+"");
		} catch (Exception e) {
			return null;
		}
	}
	
	public FsJSONArray getArray(String key) {
		return (FsJSONArray) super.get(key);
	}
	
	public FsJSONObject(String key,Object value) {
		this.put(key, value);
	}
	
	public FsJSONObject() {
		super();	
	}
	
	public FsJSONObject(JsonObject source) {
		super();	
		for (Object key : source.keySet()) {
            String value = source.get(key)+"";
            this.put(key+"", value);
        }
	}
}
