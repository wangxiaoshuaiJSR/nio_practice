package com.self.code.tomcat.servlet;

import com.self.code.tomcat.http.Request;
import com.self.code.tomcat.http.Response;
import com.self.code.tomcat.http.Servlet;

public class SecondServlet extends Servlet {

	public void doGet(Request request, Response response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(Request request, Response response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
