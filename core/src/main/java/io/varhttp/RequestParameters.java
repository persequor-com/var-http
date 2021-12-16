package io.varhttp;

import java.util.List;
import java.util.Map;

public interface RequestParameters {

	/**
	 * Returns first value of the parameter
	 *
	 * @param name parameter name
	 * @return value or null if parameter does not exist
	 */
	String get(String name);

	/**
	 * Returns all the values of the parameter
	 *
	 * @param name parameter name
	 * @return value or empty list if parameter doesn't exist
	 */
	List<String> getAll(String name);

	/**
	 * Checks if request has a parameter with name
	 * @param name parameter name
	 * @return true if request has a parameter with name, false otherwise
	 */
	boolean contains(String name);

	/**
	 * Map view of request parameters
	 * <p>
	 * Modifying the map returned by this method will not affect the request parameters object itself
	 *
	 * @return map view of request parameters
	 */
	Map<String, List<String>> getMap();
}
