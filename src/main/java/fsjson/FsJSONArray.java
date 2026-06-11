package fsjson;

import com.github.cliftonlabs.json_simple.JsonArray;

//import org.json.simple.JSONArray;




public class FsJSONArray extends JsonArray {
	
	public FsJSONObject get(int idx) {
		return (FsJSONObject) super.get(idx);
	}
	
	public FsJSONObject[] getAsFsJSONObjects() {
		FsJSONObject[] res=new FsJSONObject[this.size()];
		for (int i = 0; i < this.size(); i++) {
			res[i]=this.get(i);
		}
		return res;
	}
	
	public FsJSONArray() {
		super();
	}
	
	
}
