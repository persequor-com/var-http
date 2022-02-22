package io.varhttp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

	@Override
	public boolean add(ContentType contentType) {
		if (stream().noneMatch(ct -> ct.equals(contentType))) {
			return super.add(contentType);
		}
		return false;
	}

	public void add(Collection<String> types) {
		types.forEach(this::add);
	}

	public ContentType getHighestPriority() {
		if (isEmpty()) {
			throw new ContentTypeException("Requested Content-Type is not supported");
		}
		return this.first();
	}

	public ContentTypes limitTo(List<String> types) throws ContentTypeException {
		ContentTypes newTypes = new ContentTypes();
		for (ContentType acceptedType : this) {
			List<String> sorted = acceptedType.bestMatch(types);
			newTypes.add(sorted);
		}
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
					if (parts[i].trim().startsWith("q=")) {
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

		public List<String> bestMatch(List<String> supportedTypes) {
			List<String> result = new ArrayList<>();
			List<String> nonMatched = new ArrayList<>();
			for (String supportedType : supportedTypes) {
				if (type.equals(supportedType)) {
					result.add(supportedType);
				} else {
					nonMatched.add(supportedType);
				}
			}
			List<String> stillNonMatched = new ArrayList<>();
			for (String supportedType : nonMatched) {
				final String typeToMatch = toSuperType(supportedType);
				if (typeToMatch.matches("^" + regexType(type) + "$")) {
					result.add(supportedType);
				} else {
					stillNonMatched.add(supportedType);
				}
			}
			for (String supportedType : stillNonMatched) {
				if (type.equals("*")) {
					result.add(supportedType);
				}
			}

			return result;
		}

		private String toSuperType(String supportedType) {
			return supportedType.replaceAll("/vnd\\..*\\+", "/");
		}

		private String regexType(String type) {
			return type.replaceAll("\\.", "[.]").replaceAll("\\+", "[+]").replaceAll("\\*",".+");
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ContentType that = (ContentType) o;

			if (Double.compare(that.qualifier, qualifier) != 0) return false;
			return Objects.equals(type, that.type);
		}

		@Override
		public int hashCode() {
			int result;
			long temp;
			result = type != null ? type.hashCode() : 0;
			temp = Double.doubleToLongBits(qualifier);
			result = 31 * result + (int) (temp ^ (temp >>> 32));
			return result;
		}
	}
}