package java.util;

import java.util.function.UnaryOperator;

public interface List<E> extends Collection<E> {
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

	boolean addAll(int index, Collection<? extends E> c);

	boolean removeAll(Collection<?> c);

	boolean retainAll(Collection<?> c);

	/**
	 * Replaces each element of this list with the result of applying the
	 * operator to that element. Errors or runtime exceptions thrown by the
	 * operator are relayed to the caller.
	 *
	 * @implSpec The default implementation is equivalent to, for this
	 *           {@code list}:
	 *
	 *           <pre>
	 * {@code
	 *     final ListIterator<E> li = list.listIterator();
	 *           	while (li.hasNext()) {
	 *           		li.set(operator.apply(li.next()));
	 *           	}
	 *           }
	 *           </pre>
	 *
	 *           If the list's list-iterator does not support the {@code set}
	 *           operation then an {@code UnsupportedOperationException} will be
	 *           thrown when replacing the first element.
	 *
	 * @param operator
	 *            the operator to apply to each element
	 * @throws UnsupportedOperationException
	 *             if this list is unmodifiable. Implementations may throw this
	 *             exception if an element cannot be replaced or if, in general,
	 *             modification is not supported
	 * @throws NullPointerException
	 *             if the specified operator is null or if the operator result
	 *             is a null value and this list does not permit null elements
	 *             (<a href=
	 *             "Collection.html#optional-restrictions">optional</a>)
	 * @since 1.8
	 */
	default void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		final ListIterator<E> li = this.listIterator();
		while (li.hasNext()) {
			li.set(operator.apply(li.next()));
		}
	}

	/**
	 * Sorts this list according to the order induced by the specified
	 * {@link Comparator}.
	 *
	 * <p>
	 * All elements in this list must be <i>mutually comparable</i> using the
	 * specified comparator (that is, {@code c.compare(e1, e2)} must not throw a
	 * {@code ClassCastException} for any elements {@code e1} and {@code e2} in
	 * the list).
	 *
	 * <p>
	 * If the specified comparator is {@code null} then all elements in this
	 * list must implement the {@link Comparable} interface and the elements'
	 * {@linkplain Comparable natural ordering} should be used.
	 *
	 * <p>
	 * This list must be modifiable, but need not be resizable.
	 *
	 * @implSpec The default implementation obtains an array containing all
	 *           elements in this list, sorts the array, and iterates over this
	 *           list resetting each element from the corresponding position in
	 *           the array. (This avoids the n<sup>2</sup> log(n) performance
	 *           that would result from attempting to sort a linked list in
	 *           place.)
	 *
	 * @implNote This implementation is a stable, adaptive, iterative mergesort
	 *           that requires far fewer than n lg(n) comparisons when the input
	 *           array is partially sorted, while offering the performance of a
	 *           traditional mergesort when the input array is randomly ordered.
	 *           If the input array is nearly sorted, the implementation
	 *           requires approximately n comparisons. Temporary storage
	 *           requirements vary from a small constant for nearly sorted input
	 *           arrays to n/2 object references for randomly ordered input
	 *           arrays.
	 *
	 *           <p>
	 *           The implementation takes equal advantage of ascending and
	 *           descending order in its input array, and can take advantage of
	 *           ascending and descending order in different parts of the same
	 *           input array. It is well-suited to merging two or more sorted
	 *           arrays: simply concatenate the arrays and sort the resulting
	 *           array.
	 *
	 *           <p>
	 *           The implementation was adapted from Tim Peters's list sort for
	 *           Python (<a href=
	 *           "http://svn.python.org/projects/python/trunk/Objects/listsort.txt">
	 *           TimSort</a>). It uses techniques from Peter McIlroy's
	 *           "Optimistic Sorting and Information Theoretic Complexity", in
	 *           Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete
	 *           Algorithms, pp 467-474, January 1993.
	 *
	 * @param c
	 *            the {@code Comparator} used to compare list elements. A
	 *            {@code null} value indicates that the elements'
	 *            {@linkplain Comparable natural ordering} should be used
	 * @throws ClassCastException
	 *             if the list contains elements that are not <i>mutually
	 *             comparable</i> using the specified comparator
	 * @throws UnsupportedOperationException
	 *             if the list's list-iterator does not support the {@code set}
	 *             operation
	 * @throws IllegalArgumentException
	 *             (<a href=
	 *             "Collection.html#optional-restrictions">optional</a>) if the
	 *             comparator is found to violate the {@link Comparator}
	 *             contract
	 * @since 1.8
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default void sort(Comparator<? super E> c) {
		Object[] a = this.toArray();
		Arrays.sort(a, (Comparator) c);
		ListIterator<E> i = this.listIterator();
		for (Object e : a) {
			i.next();
			i.set((E) e);
		}
	}

	/**
	 * Removes all of the elements from this list (optional operation). The list
	 * will be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException
	 *             if the <tt>clear</tt> operation is not supported by this list
	 */
	void clear();

	// Comparison and hashing
	/**
	 * Compares the specified object with this list for equality. Returns
	 * <tt>true</tt> if and only if the specified object is also a list, both
	 * lists have the same size, and all corresponding pairs of elements in the
	 * two lists are <i>equal</i>. (Two elements <tt>e1</tt> and <tt>e2</tt> are
	 * <i>equal</i> if <tt>(e1==null ? e2==null :
	 * e1.equals(e2))</tt>.) In other words, two lists are defined to be equal
	 * if they contain the same elements in the same order. This definition
	 * ensures that the equals method works properly across different
	 * implementations of the <tt>List</tt> interface.
	 *
	 * @param o
	 *            the object to be compared for equality with this list
	 * @return <tt>true</tt> if the specified object is equal to this list
	 */
	boolean equals(Object o);

	/**
	 * Returns the hash code value for this list. The hash code of a list is
	 * defined to be the result of the following calculation:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	int hashCode = 1;
	 * 	for (E e : list)
	 * 		hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
	 * }
	 * </pre>
	 *
	 * This ensures that <tt>list1.equals(list2)</tt> implies that
	 * <tt>list1.hashCode()==list2.hashCode()</tt> for any two lists,
	 * <tt>list1</tt> and <tt>list2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 *
	 * @return the hash code value for this list
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	int hashCode();

	E get(int index);

	E set(int index, E element);

	void add(int index, E element);

	E remove(int index);

	int indexOf(Object o);

	int lastIndexOf(Object o);

	ListIterator<E> listIterator();

	ListIterator<E> listIterator(int index);

	List<E> subList(int fromIndex, int toIndex);

	/**
	 * Creates a {@link Spliterator} over the elements in this list.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED} and
	 * {@link Spliterator#ORDERED}. Implementations should document the
	 * reporting of additional characteristic values.
	 *
	 * @implSpec The default implementation creates a
	 *           <em><a href="Spliterator.html#binding">late-binding</a></em>
	 *           spliterator from the list's {@code Iterator}. The spliterator
	 *           inherits the <em>fail-fast</em> properties of the list's
	 *           iterator.
	 *
	 * @implNote The created {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED}.
	 *
	 * @return a {@code Spliterator} over the elements in this list
	 * @since 1.8
	 */
	@Override
	default Spliterator<E> spliterator() {
		return Spliterators.spliterator(this, Spliterator.ORDERED);
	}
}
