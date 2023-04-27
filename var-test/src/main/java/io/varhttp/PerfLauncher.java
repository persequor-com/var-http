package io.varhttp;

import io.varhttp.performance.Class1;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PerfLauncher implements Runnable {
	private final VarUndertow undertow;

	@Inject
	public PerfLauncher(VarUndertow undertow) {
		this.undertow = undertow;
	}

	@Override
	public void run() {
		long s = System.currentTimeMillis();
		undertow.setExecutor(Executors.newCachedThreadPool());
		undertow.configure(configuration -> {
			configuration.addControllerPackage(Class1.class.getPackage());
		});
		undertow.run();
		System.out.println("Startup time: "+(System.currentTimeMillis()-s));
	}

	public void stop() {
		undertow.stop(Duration.ofSeconds(20));
	}

	public Future<Boolean> isStarted() {
		return undertow.getStarted();
	}

}
