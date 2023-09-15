package io.varhttp.controllers;

import io.varhttp.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@ControllerClass(pathPrefix = "/redirects")
public class MyRedirectingController {
	@Controller(path = "/redirectRelative", httpMethods = {HttpMethod.POST})
	public void redirectRelative(ResponseHeader responseHeader) {
		responseHeader.redirectRelative("target");
	}

	@Controller(path = "/redirect", httpMethods = {HttpMethod.POST})
	public void redirect(ResponseHeader responseHeader) {
		responseHeader.redirectRelative("target");
	}

	@Controller(path = "/url", httpMethods = {HttpMethod.POST})
	public void url(ResponseHeader responseHeader) throws MalformedURLException {
		responseHeader.redirect(new URL("http://github.com"));
	}

	@Controller(path = "/target", httpMethods = {HttpMethod.GET})
	public String myTest() {
		return "target";
	}
}
