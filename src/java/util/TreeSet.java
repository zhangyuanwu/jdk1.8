package java.util;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, java.io.Serializable {
	private transient NavigableMap<E, Object> m;
	private static final Object PRESENT = new Object();

	TreeSet(NavigableMap<E, Object> m) {
		this.m = m;
	}

	public TreeSet() {
		this(new TreeMap<E, Object>());
	}

	public TreeSet(Comparator<? super E> comparator) {
		this(new TreeMap<>(comparator));
	}

	public TreeSet(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	public TreeSet(SortedSet<E> s) {
		this(s.comparator());
		addAll(s);
	}

	public Iterator<E> iterator() {
		return m.navigableKeySet().iterator();
	}

	public Iterator<E> descendingIterator() {
		return m.descendingKeySet().iterator();
	}

	public NavigableSet<E> descendingSet() {
		return new TreeSet<>(m.descendingMap());
	}

	public int size() {
		return m.size();
	}

	public boolean isEmpty() {
		return m.isEmpty();
	}

	/**
	 * Returns {@code true} if this set contains the specified element. More
	 * formally, returns {@code true} if and only if this set contains an
	 * element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o
	 *            object to be checked for containment in this set
	 * @return {@code true} if this set contains the specified element
	 * @throws ClassCastException
	 *             if the specified object cannot be compared with the elements
	 *             currently in the set
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 */
	public boolean contains(Object o) {
		return m.containsKey(o);
	}

	/**
	 * Adds the specified element to this set if it is not already present. More
	 * formally, adds the specified element {@code e} to this set if the set
	 * contains no element {@code e2} such that
	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this
	 * set already contains the element, the call leaves the set unchanged and
	 * returns {@code false}.
	 *
	 * @param e
	 *            element to be added to this set
	 * @return {@code true} if this set did not already contain the specified
	 *         element
	 * @throws ClassCastException
	 *             if the specified object cannot be compared with the elements
	 *             currently in this set
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 */
	public boolean add(E e) {
		return m.put(e, PRESENT) == null;
	}

	/**
	 * Removes the specified element from this set if it is present. More
	 * formally, removes an element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if this
	 * set contains such an element. Returns {@code true} if this set contained
	 * the element (or equivalently, if this set changed as a result of the
	 * call). (This set will not contain the element once the call returns.)
	 *
	 * @param o
	 *            object to be removed from this set, if present
	 * @return {@code true} if this set contained the specified element
	 * @throws ClassCastException
	 *             if the specified object cannot be compared with the elements
	 *             currently in this set
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 */
	public boolean remove(Object o) {
		return m.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after
	 * this call returns.
	 */
	public void clear() {
		m.clear();
	}

	/**
	 * Adds all of the elements in the specified collection to this set.
	 *
	 * @param c
	 *            collection containing elements to be added to this set
	 * @return {@code true} if this set changed as a result of the call
	 * @throws ClassCastException
	 *             if the elements provided cannot be compared with the elements
	 *             currently in the set
	 * @throws NullPointerException
	 *             if the specified collection is null or if any element is null
	 *             and this set uses natural ordering, or its comparator does
	 *             not permit null elements
	 */
	public boolean addAll(Collection<? extends E> c) {
		// Use linear-time version if applicable
		if ((m.size() == 0) && (c.size() > 0) && (c instanceof SortedSet) && (m instanceof TreeMap)) {
			SortedSet<? extends E> set = (SortedSet<? extends E>) c;
			TreeMap<E, Object> map = (TreeMap<E, Object>) m;
			Comparator<?> cc = set.comparator();
			Comparator<? super E> mc = map.comparator();
			if ((cc == mc) || ((cc != null) && cc.equals(mc))) {
				map.addAllForTreeSet(set, PRESENT);
				return true;
			}
		}
		return super.addAll(c);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code fromElement} or {@code toElement} is null and this
	 *             set uses natural ordering, or its comparator does not permit
	 *             null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return new TreeSet<>(m.subMap(fromElement, fromInclusive, toElement, toInclusive));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code toElement} is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return new TreeSet<>(m.headMap(toElement, inclusive));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code fromElement} is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 * @since 1.6
	 */
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return new TreeSet<>(m.tailMap(fromElement, inclusive));
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code fromElement} or {@code toElement} is null and this
	 *             set uses natural ordering, or its comparator does not permit
	 *             null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code toElement} is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public SortedSet<E> headSet(E toElement) {
		return headSet(toElement, false);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if {@code fromElement} is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public SortedSet<E> tailSet(E fromElement) {
		return tailSet(fromElement, true);
	}

	public Comparator<? super E> comparator() {
		return m.comparator();
	}

	/**
	 * @throws NoSuchElementException
	 *             {@inheritDoc}
	 */
	public E first() {
		return m.firstKey();
	}

	/**
	 * @throws NoSuchElementException
	 *             {@inheritDoc}
	 */
	public E last() {
		return m.lastKey();
	}

	// NavigableSet API methods
	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @since 1.6
	 */
	public E lower(E e) {
		return m.lowerKey(e);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @since 1.6
	 */
	public E floor(E e) {
		return m.floorKey(e);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @since 1.6
	 */
	public E ceiling(E e) {
		return m.ceilingKey(e);
	}

	/**
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified element is null and this set uses natural
	 *             ordering, or its comparator does not permit null elements
	 * @since 1.6
	 */
	public E higher(E e) {
		return m.higherKey(e);
	}

	/**
	 * @since 1.6
	 */
	public E pollFirst() {
		Map.Entry<E, ?> e = m.pollFirstEntry();
		return (e == null) ? null : e.getKey();
	}

	/**
	 * @since 1.6
	 */
	public E pollLast() {
		Map.Entry<E, ?> e = m.pollLastEntry();
		return (e == null) ? null : e.getKey();
	}

	/**
	 * Returns a shallow copy of this {@code TreeSet} instance. (The elements
	 * themselves are not cloned.)
	 *
	 * @return a shallow copy of this set
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		TreeSet<E> clone;
		try {
			clone = (TreeSet<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
		clone.m = new TreeMap<>(m);
		return clone;
	}

	/**
	 * Save the state of the {@code TreeSet} instance to a stream (that is,
	 * serialize it).
	 *
	 * @serialData Emits the comparator used to order this set, or {@code null}
	 *             if it obeys its elements' natural ordering (Object), followed
	 *             by the size of the set (the number of elements it contains)
	 *             (int), followed by all of its elements (each an Object) in
	 *             order (as determined by the set's Comparator, or by the
	 *             elements' natural ordering if the set has no Comparator).
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeObject(m.comparator());
		s.writeInt(m.size());
		for (E e : m.keySet()) {
			s.writeObject(e);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		@SuppressWarnings("unchecked")
		Comparator<? super E> c = (Comparator<? super E>) s.readObject();
		TreeMap<E, Object> tm = new TreeMap<>(c);
		m = tm;
		int size = s.readInt();
		tm.readTreeSet(size, s, PRESENT);
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED},
	 * {@link Spliterator#DISTINCT}, {@link Spliterator#SORTED}, and
	 * {@link Spliterator#ORDERED}. Overriding implementations should document
	 * the reporting of additional characteristic values.
	 *
	 * <p>
	 * The spliterator's comparator (see
	 * {@link java.util.Spliterator#getComparator()}) is {@code null} if the
	 * tree set's comparator (see {@link #comparator()}) is {@code null}.
	 * Otherwise, the spliterator's comparator is the same as or imposes the
	 * same total ordering as the tree set's comparator.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	public Spliterator<E> spliterator() {
		return TreeMap.keySpliteratorFor(m);
	}

	private static final long serialVersionUID = -2479143000061671589L;
}
