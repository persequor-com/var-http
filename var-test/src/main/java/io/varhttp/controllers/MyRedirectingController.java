package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.PathVariable;
import io.varhttp.RequestBody;
import io.varhttp.RequestHeader;
import io.varhttp.RequestParameter;
import io.varhttp.ResponseHeader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@ControllerClass(pathPrefix = "/redirects")
public class MyRedirectingController {
	@Controller(path = "/redirectRelative")
	public void redirectRelative(ResponseHeader responseHeader) {
		responseHeader.redirectRelative("target");
	}

	@Controller(path = "/redirect")
	public void redirect(ResponseHeader responseHeader) {
		responseHeader.redirectRelative("target");
	}

	@Controller(path = "/url")
	public void url(ResponseHeader responseHeader) throws MalformedURLException {
		responseHeader.redirect(new URL("http://github.com"));
	}

	@Controller(path = "/target")
	public String myTest() {
		return "target";
	}
}
