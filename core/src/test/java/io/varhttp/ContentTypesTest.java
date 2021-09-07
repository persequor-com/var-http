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
}
