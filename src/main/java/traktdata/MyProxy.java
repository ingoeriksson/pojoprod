package traktdata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
//import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * Servlet implementation class MyProxy
 */
@WebServlet("/MyProxy")
public class MyProxy extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyProxy() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest browreq, HttpServletResponse browresp) throws ServletException, IOException {

		//System.out.println("smyproxy called"+System.currentTimeMillis());

		browresp.setContentType("image/png");
		
		String LMurl = "http://"+
				"skfo0001"+":"+
				"a0yhAuO8Cru1Z143Z"+"@"+
				"maps.lantmateriet.se/ortofoto-ar/wms/v1.2?";
		
		//System.out.println(request.getRequestURL());
		
		// Copy parameters from browser to url
		Enumeration e=browreq.getParameterNames();
		while (e.hasMoreElements()){
			String n=(String) e.nextElement();
			LMurl+=n;
			String s=browreq.getParameter(n);
			LMurl+="="+s;
			if (e.hasMoreElements()) LMurl+="&";
		}
		
		//System.out.println(LMurl);
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet LMreq = new HttpGet(LMurl); 
		
		// Copy browser header
		Enumeration henum=browreq.getHeaderNames();
		while (henum.hasMoreElements()){
			String n=(String) henum.nextElement();

			String s=browreq.getHeader(n);

			LMreq.addHeader(n, s);
			// System.out.println(n+" "+s);
		}

		HttpResponse LMresp = client.execute(LMreq);
		// Copy LM header
		for (Header h:LMresp.getAllHeaders()){
			browresp.addHeader(h.getName(), h.getValue());
			// System.out.println(h.getName()+" "+ h.getValue());
		}
	
		//System.out.println(LMresp.getStatusLine().getStatusCode());
		
		
		// Handle stream

		InputStream is =LMresp.getEntity().getContent();
		
		OutputStream out=browresp.getOutputStream();
		byte[] buf = new byte[1024];
		int count = 0;
		while ((count = is.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		is.close();
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}
