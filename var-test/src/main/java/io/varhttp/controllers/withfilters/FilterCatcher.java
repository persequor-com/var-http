package io.varhttp.controllers.withfilters;

import javax.inject.Singleton;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class FilterCatcher {
	List<String> result = new ArrayList<>();

	public void add(String result) {
		this.result.add(result);
	}

	public List<String> getResult() {
		return result;
	}
}
