package io.varhttp;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ContentTypesTest {
	ContentTypes contentTypes = new ContentTypes();

	@Test
	public void happyPath() {
		contentTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5, */*; q=0.1");

		assertEquals(6, contentTypes.size());

		Iterator<ContentTypes.ContentType> itt = contentTypes.iterator();
		ContentTypes.ContentType type = itt.next();
		assertEquals("text/html", type.getType());
		type = itt.next();
		assertEquals("text/*", type.getType());
		type = itt.next();
		assertEquals("image/gif", type.getType());
		type = itt.next();
		assertEquals("image/jpeg", type.getType());
		type = itt.next();
		assertEquals("image/*", type.getType());
		type = itt.next();
		assertEquals("*/*", type.getType());
	}

	@Test
	public void versionParameter() {
		contentTypes.add("application/signed-exchange;v=b3;q=0.9");

		assertEquals(1, contentTypes.size());

		Iterator<ContentTypes.ContentType> itt = contentTypes.iterator();
		ContentTypes.ContentType type = itt.next();
		assertEquals("application/signed-exchange", type.getType());
	}

	@Test
	public void limitedTo_happy() {
		contentTypes.add("text/html; q=1.0, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5");

		assertEquals("text/html", contentTypes.limitTo("text/html").getHighestPriority().getType());
	}

	@Test
	public void limitedTo_partialMatch() {
		contentTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5");

		assertEquals("text/plain", contentTypes.limitTo("text/plain").getHighestPriority().getType());
	}

	@Test
	public void limitedTo_completeWildcard() {
		contentTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5, */*; q=0.1");

		assertEquals("my/custom", contentTypes.limitTo("my/custom").getHighestPriority().getType());
	}

	@Test
	public void limitedTo_superTypeToVendorSpecific() {
		contentTypes.add("application/json");

		assertEquals("application/vnd.my-company+json", contentTypes.limitTo("application/vnd.my-company+json").getHighestPriority().getType());
	}

	@Test
	public void limitedTo_exactMatch() {
		contentTypes.add("application/vnd.my-company+json");

		assertEquals("application/vnd.my-company+json", contentTypes.limitTo("application/vnd.my-company+json").getHighestPriority().getType());
	}
}
