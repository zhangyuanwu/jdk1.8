package java.util;

import java.util.function.Supplier;

public final class Objects {
	private Objects() {
		throw new AssertionError("No java.util.Objects instances for you!");
	}

	public static boolean equals(Object a, Object b) {
		return (a == b) || ((a != null) && a.equals(b));
	}

	public static boolean deepEquals(Object a, Object b) {
		if (a == b) {
			return true;
		} else if ((a == null) || (b == null)) {
			return false;
		} else {
			return Arrays.deepEquals0(a, b);
		}
	}

	public static int hashCode(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	public static int hash(Object... values) {
		return Arrays.hashCode(values);
	}

	public static String toString(Object o) {
		return String.valueOf(o);
	}

	public static String toString(Object o, String nullDefault) {
		return (o != null) ? o.toString() : nullDefault;
	}

	public static <T> int compare(T a, T b, Comparator<? super T> c) {
		return (a == b) ? 0 : c.compare(a, b);
	}

	public static <T> T requireNonNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	/**
	 * Checks that the specified object reference is not {@code null} and throws
	 * a customized {@link NullPointerException} if it is. This method is
	 * designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote>
	 *
	 * <pre>
	 * public Foo(Bar bar, Baz baz) {
	 * 	this.bar = Objects.requireNonNull(bar, "bar must not be null");
	 * 	this.baz = Objects.requireNonNull(baz, "baz must not be null");
	 * }
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * @param obj
	 *            the object reference to check for nullity
	 * @param message
	 *            detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param <T>
	 *            the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException
	 *             if {@code obj} is {@code null}
	 */
	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null) {
			throw new NullPointerException(message);
		}
		return obj;
	}

	/**
	 * Returns {@code true} if the provided reference is {@code null} otherwise
	 * returns {@code false}.
	 *
	 * @apiNote This method exists to be used as a
	 *          {@link java.util.function.Predicate},
	 *          {@code filter(Objects::isNull)}
	 *
	 * @param obj
	 *            a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is {@code null} otherwise
	 *         {@code false}
	 *
	 * @see java.util.function.Predicate
	 * @since 1.8
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}

	/**
	 * Returns {@code true} if the provided reference is non-{@code null}
	 * otherwise returns {@code false}.
	 *
	 * @apiNote This method exists to be used as a
	 *          {@link java.util.function.Predicate},
	 *          {@code filter(Objects::nonNull)}
	 *
	 * @param obj
	 *            a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is non-{@code null}
	 *         otherwise {@code false}
	 *
	 * @see java.util.function.Predicate
	 * @since 1.8
	 */
	public static boolean nonNull(Object obj) {
		return obj != null;
	}

	/**
	 * Checks that the specified object reference is not {@code null} and throws
	 * a customized {@link NullPointerException} if it is.
	 *
	 * <p>
	 * Unlike the method {@link #requireNonNull(Object, String)}, this method
	 * allows creation of the message to be deferred until after the null check
	 * is made. While this may confer a performance advantage in the non-null
	 * case, when deciding to call this method care should be taken that the
	 * costs of creating the message supplier are less than the cost of just
	 * creating the string message directly.
	 *
	 * @param obj
	 *            the object reference to check for nullity
	 * @param messageSupplier
	 *            supplier of the detail message to be used in the event that a
	 *            {@code NullPointerException} is thrown
	 * @param <T>
	 *            the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException
	 *             if {@code obj} is {@code null}
	 * @since 1.8
	 */
	public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
		if (obj == null) {
			throw new NullPointerException(messageSupplier.get());
		}
		return obj;
	}
}
