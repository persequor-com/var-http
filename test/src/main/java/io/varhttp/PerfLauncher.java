package io.varhttp;

import javax.inject.Inject;

public class PerfLauncher implements Runnable {
	private TestStandalone standalone;

	@Inject
	public PerfLauncher(TestStandalone standalone) {
		this.standalone = standalone;
	}

	@Override
	public void run() {
		long s = System.currentTimeMillis();
		for(int i=1;i<40;i++) {
			for(int y=1;y<40;y++) {
				try {
					standalone.addController("/controller"+i+"/method"+y+"/{muh}", Class.forName("io.varhttp.performance.Class"+(((int)i%7)+1)));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		standalone.run();
		System.out.println("Startup time: "+(System.currentTimeMillis()-s));
	}

	public void stop() {
		standalone.stop();
	}
}
