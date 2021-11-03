package io.varhttp;

import com.google.common.base.Strings;

import java.util.*;

public class ExecutionMap {
	private static final String WILDCARD = "/{}";
	private String part = "/";
	private final ExecutionMap parent;
	Map<String, ExecutionMap> children = new LinkedHashMap<>();
	boolean isWildCard = false;
	private final Map<HttpMethod,ControllerExecution> executions = new HashMap<>();
	private VarConfigurationContext context;

	public ExecutionMap(VarConfigurationContext context) {
		parent = null;
		this.context = context;
	}

	public ExecutionMap(String part) {
		this.part = part;
		this.parent = null;
	}

	public ExecutionMap(String part, ExecutionMap parent, VarConfigurationContext context) {
		this.part = part;
		this.parent = parent;
		this.context = context;
	}

	public ControllerExecution get(String[] path, HttpMethod httpMethod) {
		ArrayDeque<String> ar = treatPath(path);

		return get(ar, httpMethod);
	}

	private ControllerExecution get(ArrayDeque<String> path, HttpMethod httpMethod) {
		if (path.isEmpty()) {
			return get(httpMethod);
		}

		String part = path.pollFirst();

		if (!children.containsKey(part) && isWildCard) {
			part = WILDCARD;
		}

		ExecutionMap executionMap = children.get(part);

		if (executionMap != null) {
			return executionMap.get(path, httpMethod);
		} else if (!isWildCard) {
			return getNotFoundController();
		}

		return get(httpMethod);
	}

	private ControllerExecution get(HttpMethod httpMethod) {
		if (executions.containsKey(httpMethod)) {
			return executions.get(httpMethod);
		}

		return getNotFoundController();
	}

	public void createPathContext(VarConfigurationContext context, String path) {
		if (Strings.isNullOrEmpty(path)) {
			this.context = context;
			return;
		}

		ArrayDeque<String> pathParts = treatPath(path.split("/"));

		ExecutionMap executionMap = this;

		do {
			String part = pathParts.pollFirst();
			executionMap = executionMap.children.computeIfAbsent(part, s -> new ExecutionMap(part, this, context));
		} while (!pathParts.isEmpty());
	}

	public void put(VarConfigurationContext context, Request request, ControllerExecution controllerExecution) {
		put(context, request, treatPath(request.path.split("/")), controllerExecution);
	}

	private void put(VarConfigurationContext context, Request request, ArrayDeque<String> pathParts, ControllerExecution controllerExecution) {
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
		}

		if (part.equals("*")) {
			isWildCard = true;
		}

		String finalPart = part;
		children.computeIfAbsent(part, s -> new ExecutionMap(finalPart, this, context))
				.put(context, request, pathParts, controllerExecution);
	}

	private String getPath() {
		return parent != null ? parent.getPath() + "/" + part : "";
	}

	public ControllerExecution getNotFoundController() {
		ControllerExecution notFoundController = context.getNotFoundController();

		if (notFoundController != null || parent == null) {
			return notFoundController;
		}

		return parent.getNotFoundController();
	}

	private ArrayDeque<String> treatPath(String[] path) {
		ArrayDeque<String> pathParts = new ArrayDeque<>(Arrays.asList(path));
		if (!pathParts.isEmpty() && pathParts.peekFirst().isEmpty()) {
			pathParts.pollFirst();
		}
		return pathParts;
	}
}
