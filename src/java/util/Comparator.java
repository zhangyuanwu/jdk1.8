package java.util;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.ToDoubleFunction;
import java.util.Comparators;

@FunctionalInterface
public interface Comparator<T> {
	int compare(T o1, T o2);

	@Override
	boolean equals(Object obj);

	/**
	 * 返回一个比较器,它强制执行此比较器的反向排序。
	 *
	 * @return 比较器,强制使用该比较器的反向排序。
	 * @since 1.8
	 */
	default Comparator<T> reversed() {
		return Collections.reverseOrder(this);
	}

	/**
	 * 返回带有另一个比较器的字典顺序比较器。
	 * 如果此{@code Comparator}认为两个元素相等,即{@code compare(a,b)== 0},则使用{@code other}来确定顺序。
	 * <p>
	 * 如果指定的比较器也是可序列化的,则返回的比较器是可序列化的。
	 *
	 * @apiNote 例如,要根据长度和不区分大小写的自然顺序对{@code String}的集合进行排序,可以使用以下代码组合比较器,
	 *
	 *          <pre>
	 * {@code
	 *     Comparator<String> cmp = Comparator.comparingInt(String::length)
	 *             .thenComparing(String.CASE_INSENSITIVE_ORDER);
	 * }
	 *          </pre>
	 *
	 * @param other
	 *            当该比较器比较两个相等的物体时要使用的另一个比较器。
	 * @return 由此组成的词典顺序比较器,然后是另一个比较器
	 * @throws NullPointerException
	 *             如果参数为null。
	 * @since 1.8
	 */
	default Comparator<T> thenComparing(Comparator<? super T> other) {
		Objects.requireNonNull(other);
		return (Comparator<T> & Serializable) (c1, c2) -> {
			int res = compare(c1, c2);
			return (res != 0) ? res : other.compare(c1, c2);
		};
	}

	/**
	 * 返回一个字典顺序比较器，其中包含一个函数，用于提取要与给定{@code Comparator}进行比较的键。
	 *
	 * @implSpec 此默认实现的行为就像 {@code thenComparing(comparing(keyExtractor, cmp))}.
	 * @param <U>
	 *            排序键的类型
	 * @param keyExtractor
	 *            用于提取排序键的函数
	 * @param keyComparator
	 *            {@code Comparator}用于比较排序键
	 * @return 由该比较器组成的词典顺序比较器，然后比较keyExtractor函数提取的密钥
	 * @throws NullPointerException
	 *             如果任一参数为null。
	 * @see #comparing(Function, Comparator)
	 * @see #thenComparing(Comparator)
	 * @since 1.8
	 */
	default <U> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
		return thenComparing(comparing(keyExtractor, keyComparator));
	}

	/**
	 * Returns a lexicographic-order comparator with a function that extracts a
	 * {@code Comparable} sort key.
	 *
	 * @implSpec This default implementation behaves as if {@code
	 *           thenComparing(comparing(keyExtractor))}.
	 *
	 * @param <U>
	 *            the type of the {@link Comparable} sort key
	 * @param keyExtractor
	 *            the function used to extract the {@link Comparable} sort key
	 * @return a lexicographic-order comparator composed of this and then the
	 *         {@link Comparable} sort key.
	 * @throws NullPointerException
	 *             if the argument is null.
	 * @see #comparing(Function)
	 * @see #thenComparing(Comparator)
	 * @since 1.8
	 */
	default <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> keyExtractor) {
		return thenComparing(comparing(keyExtractor));
	}

	/**
	 * Returns a lexicographic-order comparator with a function that extracts a
	 * {@code int} sort key.
	 *
	 * @implSpec This default implementation behaves as if {@code
	 *           thenComparing(comparingInt(keyExtractor))}.
	 *
	 * @param keyExtractor
	 *            the function used to extract the integer sort key
	 * @return a lexicographic-order comparator composed of this and then the
	 *         {@code int} sort key
	 * @throws NullPointerException
	 *             if the argument is null.
	 * @see #comparingInt(ToIntFunction)
	 * @see #thenComparing(Comparator)
	 * @since 1.8
	 */
	default Comparator<T> thenComparingInt(ToIntFunction<? super T> keyExtractor) {
		return thenComparing(comparingInt(keyExtractor));
	}

	/**
	 * Returns a lexicographic-order comparator with a function that extracts a
	 * {@code long} sort key.
	 *
	 * @implSpec This default implementation behaves as if {@code
	 *           thenComparing(comparingLong(keyExtractor))}.
	 *
	 * @param keyExtractor
	 *            the function used to extract the long sort key
	 * @return a lexicographic-order comparator composed of this and then the
	 *         {@code long} sort key
	 * @throws NullPointerException
	 *             if the argument is null.
	 * @see #comparingLong(ToLongFunction)
	 * @see #thenComparing(Comparator)
	 * @since 1.8
	 */
	default Comparator<T> thenComparingLong(ToLongFunction<? super T> keyExtractor) {
		return thenComparing(comparingLong(keyExtractor));
	}

	/**
	 * Returns a lexicographic-order comparator with a function that extracts a
	 * {@code double} sort key.
	 *
	 * @implSpec This default implementation behaves as if {@code
	 *           thenComparing(comparingDouble(keyExtractor))}.
	 *
	 * @param keyExtractor
	 *            the function used to extract the double sort key
	 * @return a lexicographic-order comparator composed of this and then the
	 *         {@code double} sort key
	 * @throws NullPointerException
	 *             if the argument is null.
	 * @see #comparingDouble(ToDoubleFunction)
	 * @see #thenComparing(Comparator)
	 * @since 1.8
	 */
	default Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> keyExtractor) {
		return thenComparing(comparingDouble(keyExtractor));
	}

	/**
	 * Returns a comparator that imposes the reverse of the <em>natural
	 * ordering</em>.
	 *
	 * <p>
	 * The returned comparator is serializable and throws
	 * {@link NullPointerException} when comparing {@code null}.
	 *
	 * @param <T>
	 *            the {@link Comparable} type of element to be compared
	 * @return a comparator that imposes the reverse of the <i>natural
	 *         ordering</i> on {@code Comparable} objects.
	 * @see Comparable
	 * @since 1.8
	 */
	public static <T extends Comparable<? super T>> Comparator<T> reverseOrder() {
		return Collections.reverseOrder();
	}

	/**
	 * Returns a comparator that compares {@link Comparable} objects in natural
	 * order.
	 *
	 * <p>
	 * The returned comparator is serializable and throws
	 * {@link NullPointerException} when comparing {@code null}.
	 *
	 * @param <T>
	 *            the {@link Comparable} type of element to be compared
	 * @return a comparator that imposes the <i>natural ordering</i> on {@code
	 *         Comparable} objects.
	 * @see Comparable
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Comparable<? super T>> Comparator<T> naturalOrder() {
		return (Comparator<T>) Comparators.NaturalOrderComparator.INSTANCE;
	}

	/**
	 * Returns a null-friendly comparator that considers {@code null} to be less
	 * than non-null. When both are {@code null}, they are considered equal. If
	 * both are non-null, the specified {@code Comparator} is used to determine
	 * the order. If the specified comparator is {@code null}, then the returned
	 * comparator considers all non-null values to be equal.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified comparator is
	 * serializable.
	 *
	 * @param <T>
	 *            the type of the elements to be compared
	 * @param comparator
	 *            a {@code Comparator} for comparing non-null values
	 * @return a comparator that considers {@code null} to be less than
	 *         non-null, and compares non-null objects with the supplied
	 *         {@code Comparator}.
	 * @since 1.8
	 */
	public static <T> Comparator<T> nullsFirst(Comparator<? super T> comparator) {
		return new Comparators.NullComparator<>(true, comparator);
	}

	/**
	 * Returns a null-friendly comparator that considers {@code null} to be
	 * greater than non-null. When both are {@code null}, they are considered
	 * equal. If both are non-null, the specified {@code Comparator} is used to
	 * determine the order. If the specified comparator is {@code null}, then
	 * the returned comparator considers all non-null values to be equal.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified comparator is
	 * serializable.
	 *
	 * @param <T>
	 *            the type of the elements to be compared
	 * @param comparator
	 *            a {@code Comparator} for comparing non-null values
	 * @return a comparator that considers {@code null} to be greater than
	 *         non-null, and compares non-null objects with the supplied
	 *         {@code Comparator}.
	 * @since 1.8
	 */
	public static <T> Comparator<T> nullsLast(Comparator<? super T> comparator) {
		return new Comparators.NullComparator<>(false, comparator);
	}

	/**
	 * Accepts a function that extracts a sort key from a type {@code T}, and
	 * returns a {@code Comparator<T>} that compares by that sort key using the
	 * specified {@link Comparator}.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified function and
	 * comparator are both serializable.
	 *
	 * @apiNote For example, to obtain a {@code Comparator} that compares {@code
	 * Person} objects by their last name ignoring case differences,
	 *
	 *          <pre>
	 * {@code
	 *     Comparator<Person> cmp = Comparator.comparing(
	 *             Person::getLastName,
	 *             String.CASE_INSENSITIVE_ORDER);
	 * }
	 *          </pre>
	 *
	 * @param <T>
	 *            the type of element to be compared
	 * @param <U>
	 *            the type of the sort key
	 * @param keyExtractor
	 *            the function used to extract the sort key
	 * @param keyComparator
	 *            the {@code Comparator} used to compare the sort key
	 * @return a comparator that compares by an extracted key using the
	 *         specified {@code Comparator}
	 * @throws NullPointerException
	 *             if either argument is null
	 * @since 1.8
	 */
	public static <T, U> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
		Objects.requireNonNull(keyExtractor);
		Objects.requireNonNull(keyComparator);
		return (Comparator<T> & Serializable) (c1, c2) -> keyComparator.compare(keyExtractor.apply(c1), keyExtractor.apply(c2));
	}

	/**
	 * Accepts a function that extracts a {@link java.lang.Comparable
	 * Comparable} sort key from a type {@code T}, and returns a {@code
	 * Comparator<T>} that compares by that sort key.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified function is also
	 * serializable.
	 *
	 * @apiNote For example, to obtain a {@code Comparator} that compares {@code
	 * Person} objects by their last name,
	 *
	 *          <pre>
	 * {@code
	 *     Comparator<Person> byLastName = Comparator.comparing(Person::getLastName);
	 * }
	 *          </pre>
	 *
	 * @param <T>
	 *            the type of element to be compared
	 * @param <U>
	 *            the type of the {@code Comparable} sort key
	 * @param keyExtractor
	 *            the function used to extract the {@link Comparable} sort key
	 * @return a comparator that compares by an extracted key
	 * @throws NullPointerException
	 *             if the argument is null
	 * @since 1.8
	 */
	public static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> keyExtractor) {
		Objects.requireNonNull(keyExtractor);
		return (Comparator<T> & Serializable) (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
	}

	/**
	 * Accepts a function that extracts an {@code int} sort key from a type
	 * {@code T}, and returns a {@code Comparator<T>} that compares by that sort
	 * key.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified function is also
	 * serializable.
	 *
	 * @param <T>
	 *            the type of element to be compared
	 * @param keyExtractor
	 *            the function used to extract the integer sort key
	 * @return a comparator that compares by an extracted key
	 * @see #comparing(Function)
	 * @throws NullPointerException
	 *             if the argument is null
	 * @since 1.8
	 */
	public static <T> Comparator<T> comparingInt(ToIntFunction<? super T> keyExtractor) {
		Objects.requireNonNull(keyExtractor);
		return (Comparator<T> & Serializable) (c1, c2) -> Integer.compare(keyExtractor.applyAsInt(c1), keyExtractor.applyAsInt(c2));
	}

	/**
	 * Accepts a function that extracts a {@code long} sort key from a type
	 * {@code T}, and returns a {@code Comparator<T>} that compares by that sort
	 * key.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified function is also
	 * serializable.
	 *
	 * @param <T>
	 *            the type of element to be compared
	 * @param keyExtractor
	 *            the function used to extract the long sort key
	 * @return a comparator that compares by an extracted key
	 * @see #comparing(Function)
	 * @throws NullPointerException
	 *             if the argument is null
	 * @since 1.8
	 */
	public static <T> Comparator<T> comparingLong(ToLongFunction<? super T> keyExtractor) {
		Objects.requireNonNull(keyExtractor);
		return (Comparator<T> & Serializable) (c1, c2) -> Long.compare(keyExtractor.applyAsLong(c1), keyExtractor.applyAsLong(c2));
	}

	/**
	 * Accepts a function that extracts a {@code double} sort key from a type
	 * {@code T}, and returns a {@code Comparator<T>} that compares by that sort
	 * key.
	 *
	 * <p>
	 * The returned comparator is serializable if the specified function is also
	 * serializable.
	 *
	 * @param <T>
	 *            the type of element to be compared
	 * @param keyExtractor
	 *            the function used to extract the double sort key
	 * @return a comparator that compares by an extracted key
	 * @see #comparing(Function)
	 * @throws NullPointerException
	 *             if the argument is null
	 * @since 1.8
	 */
	public static <T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> keyExtractor) {
		Objects.requireNonNull(keyExtractor);
		return (Comparator<T> & Serializable) (c1, c2) -> Double.compare(keyExtractor.applyAsDouble(c1), keyExtractor.applyAsDouble(c2));
	}
}
