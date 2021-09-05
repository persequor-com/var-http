package io.varhttp;

import javax.servlet.http.HttpServletResponse;
import java.net.URL;

public class VarResponseHeader implements ResponseHeader {
	private HttpServletResponse response;
	private String classPath;

	public VarResponseHeader(HttpServletResponse response, String classPath) {
		this.response = response;
		this.classPath = classPath;
	}

	@Override
	public void setStatus(int httpResonseCode) {
		response.setStatus(httpResonseCode);
	}

	@Override
	public void addHeader(String name, String value) {
		response.addHeader(name, value);
	}

	@Override
	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

	@Override
	public void redirect(String path) {
		response.setHeader("Location", path);
	}

	@Override
	public void redirectRelative(String path) {
		response.setHeader("Location", classPath+"/"+path);
	}

	@Override
	public void redirect(URL url) {
		response.setHeader("Location", url.toString());
	}
}