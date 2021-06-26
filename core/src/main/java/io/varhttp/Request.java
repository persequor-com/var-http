package io.varhttp;

import java.util.regex.Pattern;

class Request {
	HttpMethod method;
	String path;

	public Request(HttpMethod method, String path) {
		this.method = method;
		this.path = path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Request request = (Request) o;

		if (method != request.method) return false;
		return path != null ? path.equals(request.path) : request.path == null;
	}

	@Override
	public int hashCode() {
		int result = method != null ? method.hashCode() : 0;
		result = 31 * result + (path != null ? path.hashCode() : 0);
		return result;
	}

	public boolean matchPath(String path) {
		return Pattern.compile(this.path.replaceAll("\\{[a-zA-Z]+}", "[^\\/]+")).matcher(path).matches();
	}
}