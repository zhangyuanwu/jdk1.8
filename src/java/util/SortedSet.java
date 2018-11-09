package java.util;

public interface SortedSet<E> extends Set<E> {
	Comparator<? super E> comparator();

	SortedSet<E> subSet(E fromElement, E toElement);

	SortedSet<E> headSet(E toElement);

	SortedSet<E> tailSet(E fromElement);

	E first();

	E last();

	/**
	 * Creates a {@code Spliterator} over the elements in this sorted set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#DISTINCT},
	 * {@link Spliterator#SORTED} and {@link Spliterator#ORDERED}.
	 * Implementations should document the reporting of additional
	 * characteristic values.
	 *
	 * <p>
	 * The spliterator's comparator (see
	 * {@link java.util.Spliterator#getComparator()}) must be {@code null} if
	 * the sorted set's comparator (see {@link #comparator()}) is {@code null}.
	 * Otherwise, the spliterator's comparator must be the same as or impose the
	 * same total ordering as the sorted set's comparator.
	 *
	 * @implSpec The default implementation creates a
	 *           <em><a href="Spliterator.html#binding">late-binding</a></em>
	 *           spliterator from the sorted set's {@code Iterator}. The
	 *           spliterator inherits the <em>fail-fast</em> properties of the
	 *           set's iterator. The spliterator's comparator is the same as the
	 *           sorted set's comparator.
	 *           <p>
	 *           The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SIZED}.
	 *
	 * @implNote The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED}.
	 *
	 * @return a {@code Spliterator} over the elements in this sorted set
	 * @since 1.8
	 */
	@Override
	default Spliterator<E> spliterator() {
		return new Spliterators.IteratorSpliterator<E>(this, Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED) {
			@Override
			public Comparator<? super E> getComparator() {
				return SortedSet.this.comparator();
			}
		};
	}
}
