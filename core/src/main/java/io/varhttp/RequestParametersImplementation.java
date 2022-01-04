package io.varhttp;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RequestParametersImplementation implements RequestParameters {
	private final Map<String, List<String>> decodedParams;

	public RequestParametersImplementation(HttpServletRequest request) {
		this.decodedParams = HttpHelper.decodeRequestParameters(request.getParameterMap());
	}

	@Override
	public String get(String name) {
		return decodedParams.containsKey(name) ? decodedParams.get(name).stream().findFirst().orElse(null) : null;
	}

	@Override
	public List<String> getAll(String name) {
		return decodedParams.get(name);
	}

	@Override
	public boolean contains(String name) {
		return decodedParams.containsKey(name);
	}

	@Override
	public Map<String, List<String>> getMap() {
		return decodedParams;
	}
}
