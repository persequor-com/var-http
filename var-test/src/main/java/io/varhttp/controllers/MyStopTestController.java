package io.varhttp.controllers;

import io.varhttp.Controller;
import io.varhttp.ControllerClass;
import io.varhttp.HttpMethod;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ControllerClass
public class MyStopTestController {

	private final static CountDownLatch controlPoint = new CountDownLatch(1);
	private final static CountDownLatch started = new CountDownLatch(1);

	@Controller(path = "/controllable-endpoint", httpMethods = {HttpMethod.GET})
	public String myTest() throws InterruptedException {
		started.countDown();
		if(!controlPoint.await(5, TimeUnit.SECONDS)){
			return "timed-out";
		}
		return "success!";
	}

	public static void awaitStart() throws InterruptedException {
		if(!started.await(5, TimeUnit.SECONDS)){
			throw new IllegalStateException("Stop controller has never been called/started");
		}
	}
	public static void finishWork(){
		controlPoint.countDown();
	}
}
