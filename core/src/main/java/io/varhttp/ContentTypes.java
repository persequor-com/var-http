package io.varhttp;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

public class ContentTypes extends TreeSet<ContentTypes.ContentType> {
	public void add(String types) {
		Stream.of(types.split(",")).map(ContentType::new).forEach(this::add);
	}

	public Optional<String> getType(List<String> supportedTypes) {
		return stream().flatMap(ct -> supportedTypes.stream().filter(ct::matches)).findFirst();
	}

	public void set(String contentType) {
		clear();
		add(contentType);
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
				qualifier = Double.parseDouble(parts[1].replaceAll("q=","").trim());
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
			if(type.equals("*") || type.equals("*/*")) {
				return true;
			}
			return type.equals(supportedType);
		}
	}
}