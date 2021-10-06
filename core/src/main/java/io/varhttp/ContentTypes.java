package io.varhttp;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ContentTypes extends TreeSet<ContentTypes.ContentType> {
	public ContentTypes(ContentTypes contentTypes) {
		addAll(contentTypes);
	}

	public ContentTypes() {

	}

	public void add(String types) {
		if (types.isEmpty()) {
			return;
		}
		Stream.of(types.split(",")).map(ContentType::new).forEach(this::add);
	}

	public void add(Collection<String> types) {
		types.stream().map(ContentType::new).forEach(this::add);
	}

	public ContentType getHighestPriority() {
		if (isEmpty()) {
			throw new ContentTypeException("Requested Content-Type is not supported");
		}
		return this.first();
	}

	public ContentTypes limitTo(List<String> types) throws ContentTypeException {
		ContentTypes newTypes = new ContentTypes();
		newTypes.add(types);
		newTypes.removeIf(ct -> this.stream().noneMatch(existingType -> existingType.matches(ct.type)));
		return newTypes;
	}

	public ContentTypes limitTo(String contentType) {
		if (contentType == null) {
			return new ContentTypes(this);
		}
		return limitTo(Arrays.asList(contentType));
	}


	public static class ContentType implements Comparable<ContentType> {
		private String type;
		private double qualifier;

		private ContentType(String contentType) {
			String[] parts = contentType.split(";");
			if (parts.length == 1) {
				type = parts[0].trim();
				qualifier = 1.1;
			} else {
				type = parts[0].trim();
				for(int i=1;i<parts.length;i++) {
					if (parts[i].startsWith("q=")) {
						qualifier = Double.parseDouble(parts[i].replaceAll("q=","").trim());
					}
				}
			}
		}

		//Accept:

		@Override
		public int compareTo(ContentType o) {
			int res = (int)((o.qualifier * 1000) - (qualifier * 1000));
			if (res == 0) {
				return 1;
			} else {
				return res;
			}
		}

		public String getType() {
			return type;
		}


		public boolean matches(String supportedType) {
			if(type.equals("*") || type.equals(supportedType)) {
				return true;
			}
			final String typeToMatch = toSuperType(supportedType);
			return typeToMatch.matches("^" + regexType(type) + "$");
		}

		private String toSuperType(String supportedType) {
			return supportedType.replaceAll("/vnd\\..*\\+", "/");
		}

		private String regexType(String type) {
			return type.replaceAll("\\.", "[.]").replaceAll("\\+", "[+]").replaceAll("\\*",".+");
		}
	}
}