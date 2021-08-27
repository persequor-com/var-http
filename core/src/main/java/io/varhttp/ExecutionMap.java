package io.varhttp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	private ControllerExecution get(ArrayDeque<String> path, HttpMethod httpMethod) {
		if (path.isEmpty()) {
			if (executions.isEmpty()) {
				throw new RuntimeException("Empty path, but no execution at: " + getPath());
			}
			return executions.get(httpMethod);
		}
		String part = path.pollFirst();
		if (!map.containsKey(part) && isWildCard) {
			part = WILDCARD;
		}
		ExecutionMap executionMap = map.get(part);
		return executionMap != null ? executionMap.get(path, httpMethod) : null;
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
		if (pathParts.size() == 0) {
			if (executions.containsKey(request.method)) {
				throw new ControllerAlreadyExistsException(request);
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
