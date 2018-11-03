package java.util;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Collection<E> extends Iterable<E> {
	int size();

	boolean isEmpty();

	boolean contains(Object o);

	@Override
	Iterator<E> iterator();

	Object[] toArray();

	<T> T[] toArray(T[] a);

	boolean add(E e);

	boolean remove(Object o);

	boolean containsAll(Collection<?> c);

	boolean addAll(Collection<? extends E> c);

	boolean removeAll(Collection<?> c);

	/**
	 * 删除此集合中满足给定谓词的所有元素. 在迭代期间或通过谓词抛出的错误或运行时异常被中继到调用者.
	 *
	 * @implSpec 默认实现使用其{@link #iterator}遍历集合的所有元素.
	 *           使用{@link Iterator #remove()}删除每个匹配元素.
	 *           如果集合的迭代器不支持删除,那么将在第一个匹配元素上抛出{@code UnsupportedOperationException}.
	 *
	 * @param filter
	 *            一个谓词,它返回{@code true}表示要删除的元素
	 * @return {@code true}如果删除了任何元素
	 * @throws NullPointerException
	 *             如果指定的过滤器为null
	 * @throws UnsupportedOperationException
	 *             如果无法从此集合中删除元素. 如果无法删除匹配元素或者通常不支持删除,则实现可能会抛出此异常.
	 * @since 1.8
	 */
	default boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		boolean removed = false;
		final Iterator<E> each = iterator();
		while (each.hasNext()) {
			if (filter.test(each.next())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}

	boolean retainAll(Collection<?> c);

	void clear();

	@Override
	boolean equals(Object o);

	@Override
	int hashCode();

	/**
	 * Creates a {@link Spliterator} over the elements in this collection.
	 *
	 * Implementations should document characteristic values reported by the
	 * spliterator. Such characteristic values are not required to be reported
	 * if the spliterator reports {@link Spliterator#SIZED} and this collection
	 * contains no elements.
	 *
	 * <p>
	 * The default implementation should be overridden by subclasses that can
	 * return a more efficient spliterator. In order to preserve expected
	 * laziness behavior for the {@link #stream()} and
	 * {@link #parallelStream()}} methods, spliterators should either have the
	 * characteristic of {@code IMMUTABLE} or {@code CONCURRENT}, or be
	 * <em><a href="Spliterator.html#binding">late-binding</a></em>. If none of
	 * these is practical, the overriding class should describe the
	 * spliterator's documented policy of binding and structural interference,
	 * and should override the {@link #stream()} and {@link #parallelStream()}
	 * methods to create streams using a {@code Supplier} of the spliterator, as
	 * in:
	 *
	 * <pre>
	 * {@code
	 *     Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
	 * }
	 * </pre>
	 * <p>
	 * These requirements ensure that streams produced by the {@link #stream()}
	 * and {@link #parallelStream()} methods will reflect the contents of the
	 * collection as of initiation of the terminal stream operation.
	 *
	 * @implSpec The default implementation creates a
	 *           <em><a href="Spliterator.html#binding">late-binding</a></em>
	 *           spliterator from the collections's {@code Iterator}. The
	 *           spliterator inherits the <em>fail-fast</em> properties of the
	 *           collection's iterator.
	 *           <p>
	 *           The created {@code Spliterator} reports
	 *           {@link Spliterator#SIZED}.
	 *
	 * @implNote The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED}.
	 *
	 *           <p>
	 *           If a spliterator covers no elements then the reporting of
	 *           additional characteristic values, beyond that of {@code SIZED}
	 *           and {@code SUBSIZED}, does not aid clients to control,
	 *           specialize or simplify computation. However, this does enable
	 *           shared use of an immutable and empty spliterator instance (see
	 *           {@link Spliterators#emptySpliterator()}) for empty collections,
	 *           and enables clients to determine if such a spliterator covers
	 *           no elements.
	 *
	 * @return 对此集合中的元素进行{@code Spliterator}
	 * @since 1.8
	 */
	@Override
	default Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, 0);
	}

	/**
	 * 返回以此集合为源的顺序{@code Stream}。
	 * <p>
	 * This method should be overridden when the {@link #spliterator()} method
	 * cannot return a spliterator that is {@code IMMUTABLE},
	 * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
	 * for details.)
	 *
	 * @implSpec The default implementation creates a sequential {@code Stream}
	 *           from the collection's {@code Spliterator}.
	 * @return 对此集合中的元素进行连续的{@code Stream}
	 * @since 1.8
	 */
	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * 以此集合为源返回可能并行的{@code Stream}. 此方法允许返回顺序流.
	 * <p>
	 * 当{@link #spliterator()}方法无法返回{@code IMMUTABLE},{@code CONCURRENT}或<em>后期绑定</em>的分裂器时,应该重写此方法.
	 * (有关详细信息,请参阅{@link #spliterator()}.)
	 *
	 * @implSpec 默认实现从集合的{@code Spliterator}创建并行{@code Stream}.
	 * @return 可能与此集合中的元素并行{@code Stream}
	 * @since 1.8
	 */
	default Stream<E> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
}
