package io.varhttp;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpHelperTest {

	@Test
	public void parseQueryString_happy() throws Throwable{
		Map<String, List<String>> actual = HttpHelper.parseQueryString("my-param=var-http%20is%20awesome&my-param=cool&my-second-param=hello");

		assertEquals(Arrays.asList("var-http is awesome", "cool"), actual.get("my-param"));
		assertEquals(Arrays.asList("hello"), actual.get("my-second-param"));
	}

	@Test
	public void parseQueryString_encodedSeparators() throws Throwable{
		Map<String, List<String>> actual = HttpHelper.parseQueryString("my-param=var-http%3Dawesome%26cool&my-param=var-http%3Dawesome&var-http=cool&var-http=awesome%26cool");

		assertEquals(Arrays.asList("cool", "awesome&cool"), actual.get("var-http"));
		assertEquals(Arrays.asList("var-http=awesome&cool", "var-http=awesome"), actual.get("my-param"));
	}

	@Test
	public void parseQueryString_ignoreBrokenParameters() throws Throwable{
		Map<String, List<String>> actual = HttpHelper.parseQueryString("my-param&var-http=yyy=zzz&&only=good");

		assertEquals(1, actual.size());
		assertEquals(Arrays.asList("good"), actual.get("only"));
	}

	@Test
	public void parseQueryString_spaceContainingParameters() throws Throwable{
		Map<String, List<String>> actual = HttpHelper.parseQueryString("+my%20param+=+contains+spaces%20&var-http=yyy=zzz&&only=good");

		assertEquals(2, actual.size());
		assertEquals(Arrays.asList("good"), actual.get("only"));
		assertEquals(Arrays.asList(" contains spaces "), actual.get(" my param "));
	}

}