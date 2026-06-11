package global;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncodeHandler {

	public EncodeHandler() {
		// TODO Auto-generated constructor stub
	}
	
	public static String encode64(String strToEncode){
		 String base64encoded = Base64.getEncoder().encodeToString(strToEncode.getBytes(StandardCharsets.ISO_8859_1));
		 return base64encoded;
	}
	
	public static String decode64(String base64encoded){
		byte[] base64decoded = Base64.getDecoder().decode(base64encoded);
		String strDecoded=new String(base64decoded,StandardCharsets.ISO_8859_1);
		return strDecoded;
	}

}
