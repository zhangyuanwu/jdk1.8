package java.util;

public class LinkedHashSet<E> extends HashSet<E> implements Set<E>, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = -2851667679971038690L;

	public LinkedHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, true);
	}

	public LinkedHashSet(int initialCapacity) {
		super(initialCapacity, .75f, true);
	}

	public LinkedHashSet() {
		super(16, .75f, true);
	}

	public LinkedHashSet(Collection<? extends E> c) {
		super(Math.max(2 * c.size(), 11), .75f, true);
		addAll(c);
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@code Spliterator} over the elements in this set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED},
	 * {@link Spliterator#DISTINCT}, and {@code ORDERED}. Implementations should
	 * document the reporting of additional characteristic values.
	 *
	 * @implNote The implementation creates a
	 *           <em><a href="Spliterator.html#binding">late-binding</a></em>
	 *           spliterator from the set's {@code Iterator}. The spliterator
	 *           inherits the <em>fail-fast</em> properties of the set's
	 *           iterator. The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED}.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	@Override
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, Spliterator.DISTINCT | Spliterator.ORDERED);
	}
}
