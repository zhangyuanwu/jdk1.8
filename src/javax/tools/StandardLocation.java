package javax.tools;

import javax.tools.JavaFileManager.Location;
import java.util.concurrent.*;

public enum StandardLocation implements Location {
	CLASS_OUTPUT, SOURCE_OUTPUT, CLASS_PATH, SOURCE_PATH, ANNOTATION_PROCESSOR_PATH, PLATFORM_CLASS_PATH,
	/**
	 * Location of new native header files.
	 *
	 * @since 1.8
	 */
	NATIVE_HEADER_OUTPUT;
	public static Location locationFor(final String name) {
		if (locations.isEmpty()) {
			for (Location location : values()) {
				locations.putIfAbsent(location.getName(), location);
			}
		}
		locations.putIfAbsent(name.toString(), new Location() {
			public String getName() {
				return name;
			}

			public boolean isOutputLocation() {
				return name.endsWith("_OUTPUT");
			}
		});
		return locations.get(name);
	}

	private static final ConcurrentMap<String, Location> locations = new ConcurrentHashMap<>();

	public String getName() {
		return name();
	}

	public boolean isOutputLocation() {
		switch (this) {
		case CLASS_OUTPUT:
		case SOURCE_OUTPUT:
		case NATIVE_HEADER_OUTPUT:
			return true;
		default:
			return false;
		}
	}
}
