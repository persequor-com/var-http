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
		ArrayDeque<String> ar = new ArrayDeque<>(Arrays.asList(path));
		if (!ar.isEmpty() && ar.peekFirst().equals("")) {
			ar.pollFirst();
		}

		return get(ar, httpMethod);
	}

	private ResultExecutionMap getExecutionMap(ArrayDeque<String> path, ResultExecutionMap result) {
		if (path.isEmpty()) {
			result.setExecutionMap(this);
			return result;
		}

		String part = path.pollFirst();

		if (!children.containsKey(part) && isWildCard) {
			part = WILDCARD;
		}

		ExecutionMap executionMap = children.get(part);
		if (executionMap != null) {
			result.setExecutionMap(this);
			return executionMap.getExecutionMap(path, result);
		} else if (isWildCard) {
			result.setExecutionMap(this);
		} else {
			result.notFound();
		}
		return result;
	}

	private ControllerExecution get(ArrayDeque<String> path, HttpMethod httpMethod) {
		final ResultExecutionMap result = getExecutionMap(path, new ResultExecutionMap(this));
		Map<HttpMethod, ControllerExecution> mapController = result.getExecutionMap().executions;

		if (result.isFound() && !mapController.isEmpty()) {
			ControllerExecution controllerExecution = mapController.get(httpMethod);

			if(controllerExecution != null){
				return controllerExecution;
			}
		}

		return result.getExecutionMap().getNotFoundController();
	}

	public void createPathContext(VarConfigurationContext context, String path) {
		if(Strings.isNullOrEmpty(path)) {
			this.context = context;
			return;
		}

		ArrayDeque<String> pathParts = new ArrayDeque<>(Arrays.asList(path.split("/")));
		if (!pathParts.isEmpty() && pathParts.peekFirst().isEmpty()) {
			pathParts.pollFirst();
		}

		ExecutionMap executionMap = this;
		do {
			String part = pathParts.pollFirst();
			executionMap = executionMap.children.computeIfAbsent(part, s -> new ExecutionMap(part, this, context));
		} while (!pathParts.isEmpty());
	}

	public void put(VarConfigurationContext context, Request request, ControllerExecution controllerExecution) {
		ArrayDeque<String> pathParts = new ArrayDeque<>(Arrays.asList(request.path.split("/")));
		if (!pathParts.isEmpty() && pathParts.peekFirst().isEmpty()) {
			pathParts.pollFirst();
		}
//		pathParts.add("/"+request.method.name());
		put(context, request, pathParts, controllerExecution);
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
		children.computeIfAbsent(part, s -> new ExecutionMap(finalPart, this, context))
			.put(context, request, pathParts, controllerExecution);
	}

	private String getPath() {
		return parent != null ? parent.getPath()+"/"+part : "";
	}

	public ControllerExecution getNotFoundController() {
		return context.getNotFoundController();
	}

	private static class ResultExecutionMap {
		private ExecutionMap executionMap;
		private boolean isFound = true;

		public boolean isFound() {
			return isFound;
		}

		ResultExecutionMap(ExecutionMap executionMap) {
			this.executionMap = executionMap;
		}

		public void notFound() {
			this.isFound = false;
		}

		public ExecutionMap getExecutionMap() {
			return executionMap;
		}

		public void setExecutionMap(ExecutionMap executionMap) {
			this.executionMap = executionMap;
		}
	}
}
