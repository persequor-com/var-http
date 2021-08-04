package io.varhttp;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionMap {
	private static final String WILDCARD = "/{}";
	private String part = "/";
	private final ExecutionMap parent;
	Map<String, ExecutionMap> map = new HashMap<>();
	boolean isWildCard = false;
	private ControllerExecution thisExecution = null;

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
		ar.add("/"+httpMethod.name());
		return get(ar);
	}

	private ControllerExecution get(ArrayDeque<String> path) {
		if (path.isEmpty()) {
			if (thisExecution == null) {
				throw new RuntimeException("Empty path, but no execution at: " + getPath());
			}
			return thisExecution;
		}
		String part = path.pollFirst();
		if (isWildCard) {
			part = WILDCARD;
		}
		ExecutionMap executionMap = map.get(part);
		return executionMap != null ? executionMap.get(path) : null;
	}


	public void put(Request request, ControllerExecution controllerExecution) {
		String path = request.path;
		ArrayDeque<String> pathParts;
		if (!path.equals("/")) {
			pathParts = new ArrayDeque<>(Arrays.asList(path.split("/")));
			if (pathParts.peekFirst().isEmpty()) {
				pathParts.pollFirst();
			}
			pathParts.add("/"+request.method.name());
		} else {
			pathParts = new ArrayDeque<>(Collections.singleton("/"));
		}

		put(path, pathParts, controllerExecution);
	}

	private void put(String path, ArrayDeque<String> pathParts, ControllerExecution controllerExecution) {
		if (pathParts.size() == 0) {
			if (thisExecution != null) {
				throw new RuntimeException("Controller already exists for path: "+path);
			}
			thisExecution = controllerExecution;
			return;
		}
		String part = pathParts.pollFirst();
		if (part.startsWith("{") && part.endsWith("}")) {
			isWildCard = true;
			part = WILDCARD;
			if (!map.isEmpty() && !map.containsKey(WILDCARD)) {
				throw new RuntimeException("Controller already exists for path: "+path);
			}
		} else if (isWildCard) {
			throw new RuntimeException("Controller already exists for path: "+path);
		}


		String finalPart = part;
		map.computeIfAbsent(part, s -> new ExecutionMap(finalPart, this))
			.put(path, pathParts, controllerExecution);
	}

	private String getPath() {
		return parent != null ? parent.getPath()+"/"+part : "";
	}
}
