package java.util;

public interface Set<E> extends Collection<E> {
	int size();

	boolean isEmpty();

	boolean contains(Object o);

	Iterator<E> iterator();

	Object[] toArray();

	<T> T[] toArray(T[] a);

	boolean add(E e);

	boolean remove(Object o);

	boolean containsAll(Collection<?> c);

	boolean addAll(Collection<? extends E> c);

	boolean retainAll(Collection<?> c);

	boolean removeAll(Collection<?> c);

	void clear();

	boolean equals(Object o);

	int hashCode();

	/**
	 * Creates a {@code Spliterator} over the elements in this set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#DISTINCT}.
	 * Implementations should document the reporting of additional
	 * characteristic values.
	 *
	 * @implSpec The default implementation creates a
	 *           <em><a href="Spliterator.html#binding">late-binding</a></em>
	 *           spliterator from the set's {@code Iterator}. The spliterator
	 *           inherits the <em>fail-fast</em> properties of the set's
	 *           iterator.
	 *           <p>
	 *           The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SIZED}.
	 *
	 * @implNote The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED}.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	@Override
	default Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, Spliterator.DISTINCT);
	}
}
