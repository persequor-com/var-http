package io.varhttp;

import java.util.*;

public class ExecutionMap {
	private static final String WILDCARD = "/{}";
	private String part = "/";
	private final ExecutionMap parent;
	Map<String, ExecutionMap> map = new LinkedHashMap<>();
	boolean isWildCard = false;
	private Map<HttpMethod,ControllerExecution> executions = new HashMap<>();

	public ExecutionMap() {
		parent = null;
	}

	public ExecutionMap(String part) {
		this.part = part;
		this.parent = null;
	}

	public ExecutionMap(String part, ExecutionMap parent) {
		this.part = part;
		this.parent = parent;
	}

	public ControllerExecution get(String[] path, HttpMethod httpMethod) {
		ArrayDeque<String> ar = new ArrayDeque<>(Arrays.asList(path));
		if (!ar.isEmpty() && ar.peekFirst().equals("")) {
			ar.pollFirst();
		}

		return get(ar, httpMethod);
	}

	public Set<HttpMethod> getAllowedMethods(String[] path) {
		ArrayDeque<String> ar = new ArrayDeque<>(Arrays.asList(path));
		if (!ar.isEmpty() && ar.peekFirst().equals("")) {
			ar.pollFirst();
		}

		return getAllowedMethods(ar);
	}

	public Set<HttpMethod> getAllowedMethods(ArrayDeque<String> path) {
		final Map<HttpMethod, ControllerExecution> mapController = getMapController(path);
		if (mapController == null) {
			return new HashSet<>();
		}
		return mapController.keySet();
	}

	private ControllerExecution get(ArrayDeque<String> path, HttpMethod httpMethod) {
		final Map<HttpMethod, ControllerExecution> mapController = getMapController(path);

		if(mapController == null) {
			return null;
		}

		return mapController.get(httpMethod);
	}

	private Map<HttpMethod, ControllerExecution> getMapController(ArrayDeque<String> path) {
		if (path.isEmpty()) {
			if (executions.isEmpty()) {
				throw new RuntimeException("Empty path, but no execution at: " + getPath());
			}
			return executions;
		}
		String part = path.pollFirst();
		if (!map.containsKey(part) && isWildCard) {
			part = WILDCARD;
		}
		ExecutionMap executionMap = map.get(part);
		if (executionMap != null) {
			return executionMap.getMapController(path);
		} else if (isWildCard) {
			return executions;
		} else {
			return null;
		}
	}

	public void put(Request request, ControllerExecution controllerExecution) {
		ArrayDeque<String> pathParts = new ArrayDeque<>(Arrays.asList(request.path.split("/")));
		if (!pathParts.isEmpty() && pathParts.peekFirst().isEmpty()) {
			pathParts.pollFirst();
		}
//		pathParts.add("/"+request.method.name());
		put(request, pathParts, controllerExecution);
	}

	private void put(Request request, ArrayDeque<String> pathParts, ControllerExecution controllerExecution) {
		if (pathParts.size() == 0 || pathParts.peekFirst().equals("*")) {
			if (executions.containsKey(request.method)) {
				throw new ControllerAlreadyExistsException(request);
			}

			if (pathParts.size() > 0 && pathParts.peekFirst().equals("*")) {
				isWildCard = true;
			}
			executions.put(request.method, controllerExecution);
			return;
		}
		String part = pathParts.pollFirst();
		if (part.startsWith("{") && part.endsWith("}")) {
			isWildCard = true;
			part = WILDCARD;
//			if (!map.isEmpty() && !map.containsKey(WILDCARD)) {
//				throw new ControllerAlreadyExistsException(request);
//			}
		}
		if (part.equals("*")) {
			isWildCard = true;
		}
//		else if (isWildCard) {
//			throw new ControllerAlreadyExistsException(request);
//		}


		String finalPart = part;
		map.computeIfAbsent(part, s -> new ExecutionMap(finalPart, this))
			.put(request, pathParts, controllerExecution);
	}

	private String getPath() {
		return parent != null ? parent.getPath()+"/"+part : "";
	}


}
