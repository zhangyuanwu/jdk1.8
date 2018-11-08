package java.util.concurrent;

import java.util.Collection;
import java.util.Set;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.Consumer;

public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements Serializable {
	private static final long serialVersionUID = 5457747651344034263L;
	private final CopyOnWriteArrayList<E> al;

	public CopyOnWriteArraySet() {
		al = new CopyOnWriteArrayList<>();
	}

	public CopyOnWriteArraySet(Collection<? extends E> c) {
		if (c.getClass() == CopyOnWriteArraySet.class) {
			@SuppressWarnings("unchecked")
			CopyOnWriteArraySet<E> cc = (CopyOnWriteArraySet<E>) c;
			al = new CopyOnWriteArrayList<>(cc.al);
		} else {
			al = new CopyOnWriteArrayList<>();
			al.addAllAbsent(c);
		}
	}

	public int size() {
		return al.size();
	}

	public boolean isEmpty() {
		return al.isEmpty();
	}

	public boolean contains(Object o) {
		return al.contains(o);
	}

	public Object[] toArray() {
		return al.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return al.toArray(a);
	}

	public void clear() {
		al.clear();
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
	 */
	public boolean remove(Object o) {
		return al.remove(o);
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
	 */
	public boolean add(E e) {
		return al.addIfAbsent(e);
	}

	/**
	 * Returns {@code true} if this set contains all of the elements of the
	 * specified collection. If the specified collection is also a set, this
	 * method returns {@code true} if it is a <i>subset</i> of this set.
	 *
	 * @param c
	 *            collection to be checked for containment in this set
	 * @return {@code true} if this set contains all of the elements of the
	 *         specified collection
	 * @throws NullPointerException
	 *             if the specified collection is null
	 * @see #contains(Object)
	 */
	public boolean containsAll(Collection<?> c) {
		return al.containsAll(c);
	}

	/**
	 * Adds all of the elements in the specified collection to this set if
	 * they're not already present. If the specified collection is also a set,
	 * the {@code addAll} operation effectively modifies this set so that its
	 * value is the <i>union</i> of the two sets. The behavior of this operation
	 * is undefined if the specified collection is modified while the operation
	 * is in progress.
	 *
	 * @param c
	 *            collection containing elements to be added to this set
	 * @return {@code true} if this set changed as a result of the call
	 * @throws NullPointerException
	 *             if the specified collection is null
	 * @see #add(Object)
	 */
	public boolean addAll(Collection<? extends E> c) {
		return al.addAllAbsent(c) > 0;
	}

	/**
	 * Removes from this set all of its elements that are contained in the
	 * specified collection. If the specified collection is also a set, this
	 * operation effectively modifies this set so that its value is the
	 * <i>asymmetric set difference</i> of the two sets.
	 *
	 * @param c
	 *            collection containing elements to be removed from this set
	 * @return {@code true} if this set changed as a result of the call
	 * @throws ClassCastException
	 *             if the class of an element of this set is incompatible with
	 *             the specified collection (optional)
	 * @throws NullPointerException
	 *             if this set contains a null element and the specified
	 *             collection does not permit null elements (optional), or if
	 *             the specified collection is null
	 * @see #remove(Object)
	 */
	public boolean removeAll(Collection<?> c) {
		return al.removeAll(c);
	}

	/**
	 * Retains only the elements in this set that are contained in the specified
	 * collection. In other words, removes from this set all of its elements
	 * that are not contained in the specified collection. If the specified
	 * collection is also a set, this operation effectively modifies this set so
	 * that its value is the <i>intersection</i> of the two sets.
	 *
	 * @param c
	 *            collection containing elements to be retained in this set
	 * @return {@code true} if this set changed as a result of the call
	 * @throws ClassCastException
	 *             if the class of an element of this set is incompatible with
	 *             the specified collection (optional)
	 * @throws NullPointerException
	 *             if this set contains a null element and the specified
	 *             collection does not permit null elements (optional), or if
	 *             the specified collection is null
	 * @see #remove(Object)
	 */
	public boolean retainAll(Collection<?> c) {
		return al.retainAll(c);
	}

	/**
	 * Returns an iterator over the elements contained in this set in the order
	 * in which these elements were added.
	 *
	 * <p>
	 * The returned iterator provides a snapshot of the state of the set when
	 * the iterator was constructed. No synchronization is needed while
	 * traversing the iterator. The iterator does <em>NOT</em> support the
	 * {@code remove} method.
	 *
	 * @return an iterator over the elements in this set
	 */
	public Iterator<E> iterator() {
		return al.iterator();
	}

	/**
	 * Compares the specified object with this set for equality. Returns
	 * {@code true} if the specified object is the same object as this object,
	 * or if it is also a {@link Set} and the elements returned by an
	 * {@linkplain Set#iterator() iterator} over the specified set are the same
	 * as the elements returned by an iterator over this set. More formally, the
	 * two iterators are considered to return the same elements if they return
	 * the same number of elements and for every element {@code e1} returned by
	 * the iterator over the specified set, there is an element {@code e2}
	 * returned by the iterator over this set such that
	 * {@code (e1==null ? e2==null : e1.equals(e2))}.
	 *
	 * @param o
	 *            object to be compared for equality with this set
	 * @return {@code true} if the specified object is equal to this set
	 */
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Set)) {
			return false;
		}
		Set<?> set = (Set<?>) (o);
		Iterator<?> it = set.iterator();
		// Uses O(n^2) algorithm that is only appropriate
		// for small sets, which CopyOnWriteArraySets should be.
		// Use a single snapshot of underlying array
		Object[] elements = al.getArray();
		int len = elements.length;
		// Mark matched elements to avoid re-checking
		boolean[] matched = new boolean[len];
		int k = 0;
		outer: while (it.hasNext()) {
			if (++k > len) {
				return false;
			}
			Object x = it.next();
			for (int i = 0; i < len; ++i) {
				if (!matched[i] && eq(x, elements[i])) {
					matched[i] = true;
					continue outer;
				}
			}
			return false;
		}
		return k == len;
	}

	public boolean removeIf(Predicate<? super E> filter) {
		return al.removeIf(filter);
	}

	public void forEach(Consumer<? super E> action) {
		al.forEach(action);
	}

	/**
	 * Returns a {@link Spliterator} over the elements in this set in the order
	 * in which these elements were added.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#IMMUTABLE},
	 * {@link Spliterator#DISTINCT}, {@link Spliterator#SIZED}, and
	 * {@link Spliterator#SUBSIZED}.
	 *
	 * <p>
	 * The spliterator provides a snapshot of the state of the set when the
	 * spliterator was constructed. No synchronization is needed while operating
	 * on the spliterator.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	public Spliterator<E> spliterator() {
		return Spliterators.spliterator(al.getArray(), Spliterator.IMMUTABLE | Spliterator.DISTINCT);
	}

	/**
	 * Tests for equality, coping with nulls.
	 */
	private static boolean eq(Object o1, Object o2) {
		return (o1 == null) ? o2 == null : o1.equals(o2);
	}
}
