package io.varhttp;

import io.odinjector.Binder;
import io.odinjector.Context;

public class ControllerFilterContext extends Context {
	@Override
	public void configure(Binder binder) {
		binder.bind(ControllerFilter.class).to(() -> new ControllerFilter() {
			@Override
			public boolean accepts(Request request, ControllerExecution execution) {
				if (execution.getMethod().getName().equals("muh")) {
					return false;
				} else {
					return true;
				}
			}
		});
	}
}
