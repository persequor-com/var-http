package io.varhttp;

import io.odinjector.Binder;
import io.odinjector.Context;

public class OdinContext extends Context {
	private VarConfig config;

	public OdinContext(VarConfig config) {
		this.config = config;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(VarConfig.class).to(() -> config);
		binder.bind(Serializer.class).to(GsonSerializer.class);
		binder.bind(ControllerFactory.class).to(TestControllerFactory.class);
		binder.bind(FilterFactory.class).to(TestFilterFactory.class);
		binder.bind(ParameterHandlerMatcherFactory.class).to(TestParameterHandlerMatcherFactory.class);
	}
}
