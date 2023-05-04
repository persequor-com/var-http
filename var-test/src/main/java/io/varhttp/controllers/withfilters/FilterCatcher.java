package io.varhttp.controllers.withfilters;

import javax.inject.Singleton;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class FilterCatcher {
	private final List<String> result = Collections.synchronizedList(new ArrayList<>());

	public void add(String result) {
		this.result.add(result);
	}

	public List<String> getResult() {
		return this.result;
	}
}
