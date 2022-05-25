package io.varhttp;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class ContentTypesTest {
	private ContentTypes acceptedTypes = new ContentTypes();

	@Test
	public void happyPath() {
		acceptedTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5, */*; q=0.1");

		assertEquals(6, acceptedTypes.size());

		Iterator<ContentTypes.ContentType> itt = acceptedTypes.iterator();
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
	public void reverseQualifier() {
		acceptedTypes.add("text/*; q=0.8, text/html; q=1.0");

		assertEquals(2, acceptedTypes.size());

		Iterator<ContentTypes.ContentType> itt = acceptedTypes.iterator();
		ContentTypes.ContentType type = itt.next();
		assertEquals("text/html", type.getType());
		type = itt.next();
		assertEquals("text/*", type.getType());
	}

	@Test
	public void versionParameter() {
		acceptedTypes.add("application/signed-exchange;v=b3;q=0.9");

		assertEquals(1, acceptedTypes.size());

		Iterator<ContentTypes.ContentType> itt = acceptedTypes.iterator();
		ContentTypes.ContentType type = itt.next();
		assertEquals("application/signed-exchange", type.getType());
	}

	@Test
	public void limitedTo_happy() {
		acceptedTypes.add("text/html; q=1.0, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5");

		assertEquals("text/html", acceptedTypes.limitTo("text/html").getHighestPriority(new ContentTypes()).getType());
	}

	@Test
	public void limitedTo_partialMatch() {
		acceptedTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5");

		assertEquals("text/plain", acceptedTypes.limitTo("text/plain").getHighestPriority(new ContentTypes()).getType());
	}

	@Test
	public void limitedTo_completeWildcard() {
		acceptedTypes.add("text/html; q=1.0, text/*; q=0.8, image/gif; q=0.6, image/jpeg; q=0.6, image/*; q=0.5, */*; q=0.1");

		assertEquals("my/custom", acceptedTypes.limitTo("my/custom").getHighestPriority(new ContentTypes()).getType());
	}

	@Test
	public void limitedTo_superTypeToVendorSpecific() {
		acceptedTypes.add("application/json");

		assertEquals("application/vnd.my-company+json", acceptedTypes.limitTo("application/vnd.my-company+json").getHighestPriority(new ContentTypes()).getType());
	}

	@Test(expected = ContentTypeException.class)
	public void limitedTo_askedForVendorSpecific_butOnlySupertypeIsSupported() {
		acceptedTypes.add("application/vnd.my-company+json");

		acceptedTypes.limitTo("application/json").getHighestPriority(new ContentTypes());
	}

	@Test
	public void limitedTo_vendorSpecific_exactMatch() {
		acceptedTypes.add("application/vnd.my-company+json");

		assertEquals("application/vnd.my-company+json", acceptedTypes.limitTo("application/vnd.my-company+json").getHighestPriority(new ContentTypes()).getType());
	}

	@Test
	public void limitedTo_vendorSpecific_mixed() {
		acceptedTypes.add("application/json, application/vnd.my-company+json");

		assertEquals("application/vnd.my-company+json", acceptedTypes.limitTo("application/vnd.my-company+json").getHighestPriority(new ContentTypes()).getType());

		acceptedTypes = new ContentTypes();
		acceptedTypes.add("application/vnd.my-company+json, application/json");

		assertEquals("application/vnd.my-company+json", acceptedTypes.limitTo("application/vnd.my-company+json").getHighestPriority(new ContentTypes()).getType());

		acceptedTypes = new ContentTypes();
		acceptedTypes.add("application/vnd.my-company+json, application/json");

		assertEquals("application/json", acceptedTypes.limitTo("application/json").getHighestPriority(new ContentTypes()).getType());
	}

	@Test
	public void limitedTo_vendorSpecific_bothRequestedAndSupported() {
		acceptedTypes.add("application/json, application/vnd.my-company+json");

		assertEquals("application/json", acceptedTypes.limitTo(Arrays.asList("application/vnd.my-company+json","application/json")).getHighestPriority(new ContentTypes()).getType());

		acceptedTypes = new ContentTypes();
		acceptedTypes.add("application/vnd.my-company+json, application/json");

		assertEquals("application/vnd.my-company+json", acceptedTypes.limitTo(Arrays.asList("application/json", "application/vnd.my-company+json")).getHighestPriority(new ContentTypes()).getType());
	}
}
