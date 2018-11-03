package java.util;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Collections {
	private Collections() {
	}

	private static final int BINARYSEARCH_THRESHOLD = 5000;
	private static final int REVERSE_THRESHOLD = 18;
	private static final int SHUFFLE_THRESHOLD = 5;
	private static final int FILL_THRESHOLD = 25;
	private static final int ROTATE_THRESHOLD = 100;
	private static final int COPY_THRESHOLD = 10;
	private static final int REPLACEALL_THRESHOLD = 11;
	private static final int INDEXOFSUBLIST_THRESHOLD = 35;

	public static <T extends Comparable<? super T>> void sort(List<T> list) {
		list.sort(null);
	}

	public static <T> void sort(List<T> list, Comparator<? super T> c) {
		list.sort(c);
	}

	public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) {
		if ((list instanceof RandomAccess) || (list.size() < BINARYSEARCH_THRESHOLD)) {
			return Collections.indexedBinarySearch(list, key);
		} else {
			return Collections.iteratorBinarySearch(list, key);
		}
	}

	private static <T> int indexedBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size() - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = list.get(mid);
			int cmp = midVal.compareTo(key);
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	private static <T> int iteratorBinarySearch(List<? extends Comparable<? super T>> list, T key) {
		int low = 0;
		int high = list.size() - 1;
		ListIterator<? extends Comparable<? super T>> i = list.listIterator();
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<? super T> midVal = get(i, mid);
			int cmp = midVal.compareTo(key);
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	private static <T> T get(ListIterator<? extends T> i, int index) {
		T obj = null;
		int pos = i.nextIndex();
		if (pos <= index) {
			do {
				obj = i.next();
			} while (pos++ < index);
		} else {
			do {
				obj = i.previous();
			} while (--pos > index);
		}
		return obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> int binarySearch(List<? extends T> list, T key, Comparator<? super T> c) {
		if (c == null) {
			return binarySearch((List<? extends Comparable<? super T>>) list, key);
		}
		if ((list instanceof RandomAccess) || (list.size() < BINARYSEARCH_THRESHOLD)) {
			return Collections.indexedBinarySearch(list, key, c);
		} else {
			return Collections.iteratorBinarySearch(list, key, c);
		}
	}

	private static <T> int indexedBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
		int low = 0;
		int high = l.size() - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = l.get(mid);
			int cmp = c.compare(midVal, key);
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	private static <T> int iteratorBinarySearch(List<? extends T> l, T key, Comparator<? super T> c) {
		int low = 0;
		int high = l.size() - 1;
		ListIterator<? extends T> i = l.listIterator();
		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = get(i, mid);
			int cmp = c.compare(midVal, key);
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void reverse(List<?> list) {
		int size = list.size();
		if ((size < REVERSE_THRESHOLD) || (list instanceof RandomAccess)) {
			for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--) {
				swap(list, i, j);
			}
		} else {
			ListIterator fwd = list.listIterator();
			ListIterator rev = list.listIterator(size);
			for (int i = 0, mid = list.size() >> 1; i < mid; i++) {
				Object tmp = fwd.next();
				fwd.set(rev.previous());
				rev.set(tmp);
			}
		}
	}

	public static void shuffle(List<?> list) {
		Random rnd = r;
		if (rnd == null) {
			r = rnd = new Random();
		}
		shuffle(list, rnd);
	}

	private static Random r;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void shuffle(List<?> list, Random rnd) {
		int size = list.size();
		if ((size < SHUFFLE_THRESHOLD) || (list instanceof RandomAccess)) {
			for (int i = size; i > 1; i--) {
				swap(list, i - 1, rnd.nextInt(i));
			}
		} else {
			Object arr[] = list.toArray();
			for (int i = size; i > 1; i--) {
				swap(arr, i - 1, rnd.nextInt(i));
			}
			ListIterator it = list.listIterator();
			for (Object element : arr) {
				it.next();
				it.set(element);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void swap(List<?> list, int i, int j) {
		final List l = list;
		l.set(i, l.set(j, l.get(i)));
	}

	private static void swap(Object[] arr, int i, int j) {
		Object tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	public static <T> void fill(List<? super T> list, T obj) {
		int size = list.size();
		if ((size < FILL_THRESHOLD) || (list instanceof RandomAccess)) {
			for (int i = 0; i < size; i++) {
				list.set(i, obj);
			}
		} else {
			ListIterator<? super T> itr = list.listIterator();
			for (int i = 0; i < size; i++) {
				itr.next();
				itr.set(obj);
			}
		}
	}

	public static <T> void copy(List<? super T> dest, List<? extends T> src) {
		int srcSize = src.size();
		if (srcSize > dest.size()) {
			throw new IndexOutOfBoundsException("Source does not fit in dest");
		}
		if ((srcSize < COPY_THRESHOLD) || ((src instanceof RandomAccess) && (dest instanceof RandomAccess))) {
			for (int i = 0; i < srcSize; i++) {
				dest.set(i, src.get(i));
			}
		} else {
			ListIterator<? super T> di = dest.listIterator();
			ListIterator<? extends T> si = src.listIterator();
			for (int i = 0; i < srcSize; i++) {
				di.next();
				di.set(si.next());
			}
		}
	}

	public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		while (i.hasNext()) {
			T next = i.next();
			if (next.compareTo(candidate) < 0) {
				candidate = next;
			}
		}
		return candidate;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
		if (comp == null) {
			return (T) min((Collection) coll);
		}
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		while (i.hasNext()) {
			T next = i.next();
			if (comp.compare(next, candidate) < 0) {
				candidate = next;
			}
		}
		return candidate;
	}

	public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		while (i.hasNext()) {
			T next = i.next();
			if (next.compareTo(candidate) > 0) {
				candidate = next;
			}
		}
		return candidate;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T max(Collection<? extends T> coll, Comparator<? super T> comp) {
		if (comp == null) {
			return (T) max((Collection) coll);
		}
		Iterator<? extends T> i = coll.iterator();
		T candidate = i.next();
		while (i.hasNext()) {
			T next = i.next();
			if (comp.compare(next, candidate) > 0) {
				candidate = next;
			}
		}
		return candidate;
	}

	public static void rotate(List<?> list, int distance) {
		if ((list instanceof RandomAccess) || (list.size() < ROTATE_THRESHOLD)) {
			rotate1(list, distance);
		} else {
			rotate2(list, distance);
		}
	}

	private static <T> void rotate1(List<T> list, int distance) {
		int size = list.size();
		if (size == 0) {
			return;
		}
		distance = distance % size;
		if (distance < 0) {
			distance += size;
		}
		if (distance == 0) {
			return;
		}
		for (int cycleStart = 0, nMoved = 0; nMoved != size; cycleStart++) {
			T displaced = list.get(cycleStart);
			int i = cycleStart;
			do {
				i += distance;
				if (i >= size) {
					i -= size;
				}
				displaced = list.set(i, displaced);
				nMoved++;
			} while (i != cycleStart);
		}
	}

	private static void rotate2(List<?> list, int distance) {
		int size = list.size();
		if (size == 0) {
			return;
		}
		int mid = -distance % size;
		if (mid < 0) {
			mid += size;
		}
		if (mid == 0) {
			return;
		}
		reverse(list.subList(0, mid));
		reverse(list.subList(mid, size));
		reverse(list);
	}

	public static <T> boolean replaceAll(List<T> list, T oldVal, T newVal) {
		boolean result = false;
		int size = list.size();
		if ((size < REPLACEALL_THRESHOLD) || (list instanceof RandomAccess)) {
			if (oldVal == null) {
				for (int i = 0; i < size; i++) {
					if (list.get(i) == null) {
						list.set(i, newVal);
						result = true;
					}
				}
			} else {
				for (int i = 0; i < size; i++) {
					if (oldVal.equals(list.get(i))) {
						list.set(i, newVal);
						result = true;
					}
				}
			}
		} else {
			ListIterator<T> itr = list.listIterator();
			if (oldVal == null) {
				for (int i = 0; i < size; i++) {
					if (itr.next() == null) {
						itr.set(newVal);
						result = true;
					}
				}
			} else {
				for (int i = 0; i < size; i++) {
					if (oldVal.equals(itr.next())) {
						itr.set(newVal);
						result = true;
					}
				}
			}
		}
		return result;
	}

	public static int indexOfSubList(List<?> source, List<?> target) {
		int sourceSize = source.size();
		int targetSize = target.size();
		int maxCandidate = sourceSize - targetSize;
		if ((sourceSize < INDEXOFSUBLIST_THRESHOLD) || ((source instanceof RandomAccess) && (target instanceof RandomAccess))) {
			nextCand: for (int candidate = 0; candidate <= maxCandidate; candidate++) {
				for (int i = 0, j = candidate; i < targetSize; i++, j++) {
					if (!eq(target.get(i), source.get(j))) {
						continue nextCand;
					}
				}
				return candidate;
			}
		} else {
			ListIterator<?> si = source.listIterator();
			nextCand: for (int candidate = 0; candidate <= maxCandidate; candidate++) {
				ListIterator<?> ti = target.listIterator();
				for (int i = 0; i < targetSize; i++) {
					if (!eq(ti.next(), si.next())) {
						for (int j = 0; j < i; j++) {
							si.previous();
						}
						continue nextCand;
					}
				}
				return candidate;
			}
		}
		return -1;
	}

	public static int lastIndexOfSubList(List<?> source, List<?> target) {
		int sourceSize = source.size();
		int targetSize = target.size();
		int maxCandidate = sourceSize - targetSize;
		if ((sourceSize < INDEXOFSUBLIST_THRESHOLD) || (source instanceof RandomAccess)) {
			nextCand: for (int candidate = maxCandidate; candidate >= 0; candidate--) {
				for (int i = 0, j = candidate; i < targetSize; i++, j++) {
					if (!eq(target.get(i), source.get(j))) {
						continue nextCand;
					}
				}
				return candidate;
			}
		} else {
			if (maxCandidate < 0) {
				return -1;
			}
			ListIterator<?> si = source.listIterator(maxCandidate);
			nextCand: for (int candidate = maxCandidate; candidate >= 0; candidate--) {
				ListIterator<?> ti = target.listIterator();
				for (int i = 0; i < targetSize; i++) {
					if (!eq(ti.next(), si.next())) {
						if (candidate != 0) {
							for (int j = 0; j <= (i + 1); j++) {
								si.previous();
							}
						}
						continue nextCand;
					}
				}
				return candidate;
			}
		}
		return -1;
	}

	public static <T> Collection<T> unmodifiableCollection(Collection<? extends T> c) {
		return new UnmodifiableCollection<>(c);
	}

	static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
		private static final long serialVersionUID = 1820017752578914078L;
		final Collection<? extends E> c;

		UnmodifiableCollection(Collection<? extends E> c) {
			if (c == null) {
				throw new NullPointerException();
			}
			this.c = c;
		}

		@Override
		public int size() {
			return c.size();
		}

		@Override
		public boolean isEmpty() {
			return c.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return c.contains(o);
		}

		@Override
		public Object[] toArray() {
			return c.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return c.toArray(a);
		}

		@Override
		public String toString() {
			return c.toString();
		}

		@Override
		public Iterator<E> iterator() {
			return new Iterator<E>() {
				private final Iterator<? extends E> i = c.iterator();

				@Override
				public boolean hasNext() {
					return i.hasNext();
				}

				@Override
				public E next() {
					return i.next();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

				@Override
				public void forEachRemaining(Consumer<? super E> action) {
					i.forEachRemaining(action);
				}
			};
		}

		@Override
		public boolean add(E e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> coll) {
			return c.containsAll(coll);
		}

		@Override
		public boolean addAll(Collection<? extends E> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> coll) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			c.forEach(action);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Spliterator<E> spliterator() {
			return (Spliterator<E>) c.spliterator();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Stream<E> stream() {
			return (Stream<E>) c.stream();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Stream<E> parallelStream() {
			return (Stream<E>) c.parallelStream();
		}
	}

	public static <T> Set<T> unmodifiableSet(Set<? extends T> s) {
		return new UnmodifiableSet<>(s);
	}

	static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E>, Serializable {
		private static final long serialVersionUID = -9215047833775013803L;

		UnmodifiableSet(Set<? extends E> s) {
			super(s);
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || c.equals(o);
		}

		@Override
		public int hashCode() {
			return c.hashCode();
		}
	}

	public static <T> SortedSet<T> unmodifiableSortedSet(SortedSet<T> s) {
		return new UnmodifiableSortedSet<>(s);
	}

	static class UnmodifiableSortedSet<E> extends UnmodifiableSet<E> implements SortedSet<E>, Serializable {
		private static final long serialVersionUID = -4929149591599911165L;
		private final SortedSet<E> ss;

		UnmodifiableSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
		}

		@Override
		public Comparator<? super E> comparator() {
			return ss.comparator();
		}

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return new UnmodifiableSortedSet<>(ss.subSet(fromElement, toElement));
		}

		@Override
		public SortedSet<E> headSet(E toElement) {
			return new UnmodifiableSortedSet<>(ss.headSet(toElement));
		}

		@Override
		public SortedSet<E> tailSet(E fromElement) {
			return new UnmodifiableSortedSet<>(ss.tailSet(fromElement));
		}

		@Override
		public E first() {
			return ss.first();
		}

		@Override
		public E last() {
			return ss.last();
		}
	}

	/**
	 * Returns an unmodifiable view of the specified navigable set. This method
	 * allows modules to provide users with "read-only" access to internal
	 * navigable sets. Query operations on the returned navigable set "read
	 * through" to the specified navigable set. Attempts to modify the returned
	 * navigable set, whether direct, via its iterator, or via its
	 * {@code subSet}, {@code headSet}, or {@code tailSet} views, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 *
	 * The returned navigable set will be serializable if the specified
	 * navigable set is serializable.
	 *
	 * @param <T>
	 *            the class of the objects in the set
	 * @param s
	 *            the navigable set for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified navigable set
	 * @since 1.8
	 */
	public static <T> NavigableSet<T> unmodifiableNavigableSet(NavigableSet<T> s) {
		return new UnmodifiableNavigableSet<>(s);
	}

	static class UnmodifiableNavigableSet<E> extends UnmodifiableSortedSet<E> implements NavigableSet<E>, Serializable {
		private static final long serialVersionUID = -6027448201786391929L;

		private static class EmptyNavigableSet<E> extends UnmodifiableNavigableSet<E> implements Serializable {
			private static final long serialVersionUID = -6291252904449939134L;

			public EmptyNavigableSet() {
				super(new TreeSet<E>());
			}

			private Object readResolve() {
				return EMPTY_NAVIGABLE_SET;
			}
		}

		private static final NavigableSet<?> EMPTY_NAVIGABLE_SET = new EmptyNavigableSet<>();
		private final NavigableSet<E> ns;

		UnmodifiableNavigableSet(NavigableSet<E> s) {
			super(s);
			ns = s;
		}

		@Override
		public E lower(E e) {
			return ns.lower(e);
		}

		@Override
		public E floor(E e) {
			return ns.floor(e);
		}

		@Override
		public E ceiling(E e) {
			return ns.ceiling(e);
		}

		@Override
		public E higher(E e) {
			return ns.higher(e);
		}

		@Override
		public E pollFirst() {
			throw new UnsupportedOperationException();
		}

		@Override
		public E pollLast() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NavigableSet<E> descendingSet() {
			return new UnmodifiableNavigableSet<>(ns.descendingSet());
		}

		@Override
		public Iterator<E> descendingIterator() {
			return descendingSet().iterator();
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
			return new UnmodifiableNavigableSet<>(ns.subSet(fromElement, fromInclusive, toElement, toInclusive));
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return new UnmodifiableNavigableSet<>(ns.headSet(toElement, inclusive));
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return new UnmodifiableNavigableSet<>(ns.tailSet(fromElement, inclusive));
		}
	}

	public static <T> List<T> unmodifiableList(List<? extends T> list) {
		return (list instanceof RandomAccess ? new UnmodifiableRandomAccessList<>(list) : new UnmodifiableList<>(list));
	}

	static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
		private static final long serialVersionUID = -283967356065247728L;
		final List<? extends E> list;

		UnmodifiableList(List<? extends E> list) {
			super(list);
			this.list = list;
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || list.equals(o);
		}

		@Override
		public int hashCode() {
			return list.hashCode();
		}

		@Override
		public E get(int index) {
			return list.get(index);
		}

		@Override
		public E set(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(int index, E element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public E remove(int index) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object o) {
			return list.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sort(Comparator<? super E> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<E> listIterator(final int index) {
			return new ListIterator<E>() {
				private final ListIterator<? extends E> i = list.listIterator(index);

				@Override
				public boolean hasNext() {
					return i.hasNext();
				}

				@Override
				public E next() {
					return i.next();
				}

				@Override
				public boolean hasPrevious() {
					return i.hasPrevious();
				}

				@Override
				public E previous() {
					return i.previous();
				}

				@Override
				public int nextIndex() {
					return i.nextIndex();
				}

				@Override
				public int previousIndex() {
					return i.previousIndex();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

				@Override
				public void set(E e) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void add(E e) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void forEachRemaining(Consumer<? super E> action) {
					i.forEachRemaining(action);
				}
			};
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
		}

		private Object readResolve() {
			return (list instanceof RandomAccess ? new UnmodifiableRandomAccessList<>(list) : this);
		}
	}

	static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
		UnmodifiableRandomAccessList(List<? extends E> list) {
			super(list);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return new UnmodifiableRandomAccessList<>(list.subList(fromIndex, toIndex));
		}

		private static final long serialVersionUID = -2542308836966382001L;

		private Object writeReplace() {
			return new UnmodifiableList<>(list);
		}
	}

	public static <K, V> Map<K, V> unmodifiableMap(Map<? extends K, ? extends V> m) {
		return new UnmodifiableMap<>(m);
	}

	private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
		private static final long serialVersionUID = -1034234728574286014L;
		private final Map<? extends K, ? extends V> m;

		UnmodifiableMap(Map<? extends K, ? extends V> m) {
			if (m == null) {
				throw new NullPointerException();
			}
			this.m = m;
		}

		@Override
		public int size() {
			return m.size();
		}

		@Override
		public boolean isEmpty() {
			return m.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return m.containsKey(key);
		}

		@Override
		public boolean containsValue(Object val) {
			return m.containsValue(val);
		}

		@Override
		public V get(Object key) {
			return m.get(key);
		}

		@Override
		public V put(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		private transient Set<K> keySet;
		private transient Set<Map.Entry<K, V>> entrySet;
		private transient Collection<V> values;

		@Override
		public Set<K> keySet() {
			if (keySet == null) {
				keySet = unmodifiableSet(m.keySet());
			}
			return keySet;
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null) {
				entrySet = new UnmodifiableEntrySet<>(m.entrySet());
			}
			return entrySet;
		}

		@Override
		public Collection<V> values() {
			if (values == null) {
				values = unmodifiableCollection(m.values());
			}
			return values;
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || m.equals(o);
		}

		@Override
		public int hashCode() {
			return m.hashCode();
		}

		@Override
		public String toString() {
			return m.toString();
		}

		@Override
		@SuppressWarnings("unchecked")
		public V getOrDefault(Object k, V defaultValue) {
			return ((Map<K, V>) m).getOrDefault(k, defaultValue);
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			m.forEach(action);
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V putIfAbsent(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V replace(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		static class UnmodifiableEntrySet<K, V> extends UnmodifiableSet<Map.Entry<K, V>> {
			private static final long serialVersionUID = 7854390611657943733L;

			@SuppressWarnings({ "unchecked", "rawtypes" })
			UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
				super((Set) s);
			}

			static <K, V> Consumer<Map.Entry<K, V>> entryConsumer(Consumer<? super Entry<K, V>> action) {
				return e -> action.accept(new UnmodifiableEntry<>(e));
			}

			@Override
			public void forEach(Consumer<? super Entry<K, V>> action) {
				Objects.requireNonNull(action);
				c.forEach(entryConsumer(action));
			}

			static final class UnmodifiableEntrySetSpliterator<K, V> implements Spliterator<Entry<K, V>> {
				final Spliterator<Map.Entry<K, V>> s;

				UnmodifiableEntrySetSpliterator(Spliterator<Entry<K, V>> s) {
					this.s = s;
				}

				@Override
				public boolean tryAdvance(Consumer<? super Entry<K, V>> action) {
					Objects.requireNonNull(action);
					return s.tryAdvance(entryConsumer(action));
				}

				@Override
				public void forEachRemaining(Consumer<? super Entry<K, V>> action) {
					Objects.requireNonNull(action);
					s.forEachRemaining(entryConsumer(action));
				}

				@Override
				public Spliterator<Entry<K, V>> trySplit() {
					Spliterator<Entry<K, V>> split = s.trySplit();
					return split == null ? null : new UnmodifiableEntrySetSpliterator<>(split);
				}

				@Override
				public long estimateSize() {
					return s.estimateSize();
				}

				@Override
				public long getExactSizeIfKnown() {
					return s.getExactSizeIfKnown();
				}

				@Override
				public int characteristics() {
					return s.characteristics();
				}

				@Override
				public boolean hasCharacteristics(int characteristics) {
					return s.hasCharacteristics(characteristics);
				}

				@Override
				public Comparator<? super Entry<K, V>> getComparator() {
					return s.getComparator();
				}
			}

			@Override
			@SuppressWarnings("unchecked")
			public Spliterator<Entry<K, V>> spliterator() {
				return new UnmodifiableEntrySetSpliterator<>((Spliterator<Map.Entry<K, V>>) c.spliterator());
			}

			@Override
			public Stream<Entry<K, V>> stream() {
				return StreamSupport.stream(spliterator(), false);
			}

			@Override
			public Stream<Entry<K, V>> parallelStream() {
				return StreamSupport.stream(spliterator(), true);
			}

			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				return new Iterator<Map.Entry<K, V>>() {
					private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();

					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public Map.Entry<K, V> next() {
						return new UnmodifiableEntry<>(i.next());
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] toArray() {
				Object[] a = c.toArray();
				for (int i = 0; i < a.length; i++) {
					a[i] = new UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) a[i]);
				}
				return a;
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T[] toArray(T[] a) {
				Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));
				for (int i = 0; i < arr.length; i++) {
					arr[i] = new UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) arr[i]);
				}
				if (arr.length > a.length) {
					return (T[]) arr;
				}
				System.arraycopy(arr, 0, a, 0, arr.length);
				if (a.length > arr.length) {
					a[arr.length] = null;
				}
				return a;
			}

			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry)) {
					return false;
				}
				return c.contains(new UnmodifiableEntry<>((Map.Entry<?, ?>) o));
			}

			@Override
			public boolean containsAll(Collection<?> coll) {
				for (Object e : coll) {
					if (!contains(e)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean equals(Object o) {
				if (o == this) {
					return true;
				}
				if (!(o instanceof Set)) {
					return false;
				}
				Set<?> s = (Set<?>) o;
				if (s.size() != c.size()) {
					return false;
				}
				return containsAll(s);
			}

			private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {
				private Map.Entry<? extends K, ? extends V> e;

				UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e) {
					this.e = Objects.requireNonNull(e);
				}

				@Override
				public K getKey() {
					return e.getKey();
				}

				@Override
				public V getValue() {
					return e.getValue();
				}

				@Override
				public V setValue(V value) {
					throw new UnsupportedOperationException();
				}

				@Override
				public int hashCode() {
					return e.hashCode();
				}

				@Override
				public boolean equals(Object o) {
					if (this == o) {
						return true;
					}
					if (!(o instanceof Map.Entry)) {
						return false;
					}
					Map.Entry<?, ?> t = (Map.Entry<?, ?>) o;
					return eq(e.getKey(), t.getKey()) && eq(e.getValue(), t.getValue());
				}

				@Override
				public String toString() {
					return e.toString();
				}
			}
		}
	}

	public static <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, ? extends V> m) {
		return new UnmodifiableSortedMap<>(m);
	}

	static class UnmodifiableSortedMap<K, V> extends UnmodifiableMap<K, V> implements SortedMap<K, V>, Serializable {
		private static final long serialVersionUID = -8806743815996713206L;
		private final SortedMap<K, ? extends V> sm;

		UnmodifiableSortedMap(SortedMap<K, ? extends V> m) {
			super(m);
			sm = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			return sm.comparator();
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return new UnmodifiableSortedMap<>(sm.subMap(fromKey, toKey));
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			return new UnmodifiableSortedMap<>(sm.headMap(toKey));
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			return new UnmodifiableSortedMap<>(sm.tailMap(fromKey));
		}

		@Override
		public K firstKey() {
			return sm.firstKey();
		}

		@Override
		public K lastKey() {
			return sm.lastKey();
		}
	}

	/**
	 * Returns an unmodifiable view of the specified navigable map. This method
	 * allows modules to provide users with "read-only" access to internal
	 * navigable maps. Query operations on the returned navigable map "read
	 * through" to the specified navigable map. Attempts to modify the returned
	 * navigable map, whether direct, via its collection views, or via its
	 * {@code subMap}, {@code headMap}, or {@code tailMap} views, result in an
	 * {@code UnsupportedOperationException}.
	 * <p>
	 *
	 * The returned navigable map will be serializable if the specified
	 * navigable map is serializable.
	 *
	 * @param <K>
	 *            the class of the map keys
	 * @param <V>
	 *            the class of the map values
	 * @param m
	 *            the navigable map for which an unmodifiable view is to be
	 *            returned
	 * @return an unmodifiable view of the specified navigable map
	 * @since 1.8
	 */
	public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> m) {
		return new UnmodifiableNavigableMap<>(m);
	}

	static class UnmodifiableNavigableMap<K, V> extends UnmodifiableSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
		private static final long serialVersionUID = -4858195264774772197L;

		private static class EmptyNavigableMap<K, V> extends UnmodifiableNavigableMap<K, V> implements Serializable {
			private static final long serialVersionUID = -2239321462712562324L;

			EmptyNavigableMap() {
				super(new TreeMap<K, V>());
			}

			@Override
			public NavigableSet<K> navigableKeySet() {
				return emptyNavigableSet();
			}

			private Object readResolve() {
				return EMPTY_NAVIGABLE_MAP;
			}
		}

		private static final EmptyNavigableMap<?, ?> EMPTY_NAVIGABLE_MAP = new EmptyNavigableMap<>();
		private final NavigableMap<K, ? extends V> nm;

		UnmodifiableNavigableMap(NavigableMap<K, ? extends V> m) {
			super(m);
			nm = m;
		}

		@Override
		public K lowerKey(K key) {
			return nm.lowerKey(key);
		}

		@Override
		public K floorKey(K key) {
			return nm.floorKey(key);
		}

		@Override
		public K ceilingKey(K key) {
			return nm.ceilingKey(key);
		}

		@Override
		public K higherKey(K key) {
			return nm.higherKey(key);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> lowerEntry(K key) {
			Entry<K, V> lower = (Entry<K, V>) nm.lowerEntry(key);
			return (null != lower) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(lower) : null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> floorEntry(K key) {
			Entry<K, V> floor = (Entry<K, V>) nm.floorEntry(key);
			return (null != floor) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(floor) : null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> ceilingEntry(K key) {
			Entry<K, V> ceiling = (Entry<K, V>) nm.ceilingEntry(key);
			return (null != ceiling) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(ceiling) : null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> higherEntry(K key) {
			Entry<K, V> higher = (Entry<K, V>) nm.higherEntry(key);
			return (null != higher) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(higher) : null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> firstEntry() {
			Entry<K, V> first = (Entry<K, V>) nm.firstEntry();
			return (null != first) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(first) : null;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Entry<K, V> lastEntry() {
			Entry<K, V> last = (Entry<K, V>) nm.lastEntry();
			return (null != last) ? new UnmodifiableEntrySet.UnmodifiableEntry<>(last) : null;
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			throw new UnsupportedOperationException();
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			return unmodifiableNavigableMap(nm.descendingMap());
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			return unmodifiableNavigableSet(nm.navigableKeySet());
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			return unmodifiableNavigableSet(nm.descendingKeySet());
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
			return unmodifiableNavigableMap(nm.subMap(fromKey, fromInclusive, toKey, toInclusive));
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			return unmodifiableNavigableMap(nm.headMap(toKey, inclusive));
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			return unmodifiableNavigableMap(nm.tailMap(fromKey, inclusive));
		}
	}

	public static <T> Collection<T> synchronizedCollection(Collection<T> c) {
		return new SynchronizedCollection<>(c);
	}

	static <T> Collection<T> synchronizedCollection(Collection<T> c, Object mutex) {
		return new SynchronizedCollection<>(c, mutex);
	}

	static class SynchronizedCollection<E> implements Collection<E>, Serializable {
		private static final long serialVersionUID = 3053995032091335093L;
		final Collection<E> c;
		final Object mutex;

		SynchronizedCollection(Collection<E> c) {
			this.c = Objects.requireNonNull(c);
			mutex = this;
		}

		SynchronizedCollection(Collection<E> c, Object mutex) {
			this.c = Objects.requireNonNull(c);
			this.mutex = Objects.requireNonNull(mutex);
		}

		@Override
		public int size() {
			synchronized (mutex) {
				return c.size();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (mutex) {
				return c.isEmpty();
			}
		}

		@Override
		public boolean contains(Object o) {
			synchronized (mutex) {
				return c.contains(o);
			}
		}

		@Override
		public Object[] toArray() {
			synchronized (mutex) {
				return c.toArray();
			}
		}

		@Override
		public <T> T[] toArray(T[] a) {
			synchronized (mutex) {
				return c.toArray(a);
			}
		}

		@Override
		public Iterator<E> iterator() {
			return c.iterator();
		}

		@Override
		public boolean add(E e) {
			synchronized (mutex) {
				return c.add(e);
			}
		}

		@Override
		public boolean remove(Object o) {
			synchronized (mutex) {
				return c.remove(o);
			}
		}

		@Override
		public boolean containsAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.containsAll(coll);
			}
		}

		@Override
		public boolean addAll(Collection<? extends E> coll) {
			synchronized (mutex) {
				return c.addAll(coll);
			}
		}

		@Override
		public boolean removeAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.removeAll(coll);
			}
		}

		@Override
		public boolean retainAll(Collection<?> coll) {
			synchronized (mutex) {
				return c.retainAll(coll);
			}
		}

		@Override
		public void clear() {
			synchronized (mutex) {
				c.clear();
			}
		}

		@Override
		public String toString() {
			synchronized (mutex) {
				return c.toString();
			}
		}

		@Override
		public void forEach(Consumer<? super E> consumer) {
			synchronized (mutex) {
				c.forEach(consumer);
			}
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			synchronized (mutex) {
				return c.removeIf(filter);
			}
		}

		@Override
		public Spliterator<E> spliterator() {
			return c.spliterator();
		}

		@Override
		public Stream<E> stream() {
			return c.stream();
		}

		@Override
		public Stream<E> parallelStream() {
			return c.parallelStream();
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	public static <T> Set<T> synchronizedSet(Set<T> s) {
		return new SynchronizedSet<>(s);
	}

	static <T> Set<T> synchronizedSet(Set<T> s, Object mutex) {
		return new SynchronizedSet<>(s, mutex);
	}

	static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
		private static final long serialVersionUID = 487447009682186044L;

		SynchronizedSet(Set<E> s) {
			super(s);
		}

		SynchronizedSet(Set<E> s, Object mutex) {
			super(s, mutex);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			synchronized (mutex) {
				return c.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (mutex) {
				return c.hashCode();
			}
		}
	}

	public static <T> SortedSet<T> synchronizedSortedSet(SortedSet<T> s) {
		return new SynchronizedSortedSet<>(s);
	}

	static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E> {
		private static final long serialVersionUID = 8695801310862127406L;
		private final SortedSet<E> ss;

		SynchronizedSortedSet(SortedSet<E> s) {
			super(s);
			ss = s;
		}

		SynchronizedSortedSet(SortedSet<E> s, Object mutex) {
			super(s, mutex);
			ss = s;
		}

		@Override
		public Comparator<? super E> comparator() {
			synchronized (mutex) {
				return ss.comparator();
			}
		}

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<>(ss.subSet(fromElement, toElement), mutex);
			}
		}

		@Override
		public SortedSet<E> headSet(E toElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<>(ss.headSet(toElement), mutex);
			}
		}

		@Override
		public SortedSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedSortedSet<>(ss.tailSet(fromElement), mutex);
			}
		}

		@Override
		public E first() {
			synchronized (mutex) {
				return ss.first();
			}
		}

		@Override
		public E last() {
			synchronized (mutex) {
				return ss.last();
			}
		}
	}

	/**
	 * Returns a synchronized (thread-safe) navigable set backed by the
	 * specified navigable set. In order to guarantee serial access, it is
	 * critical that <strong>all</strong> access to the backing navigable set is
	 * accomplished through the returned navigable set (or its views).
	 * <p>
	 *
	 * It is imperative that the user manually synchronize on the returned
	 * navigable set when iterating over it or any of its {@code subSet},
	 * {@code headSet}, or {@code tailSet} views.
	 *
	 * <pre>
	 *  NavigableSet s = Collections.synchronizedNavigableSet(new TreeSet());
	 *      ...
	 *  synchronized (s) {
	 *      Iterator i = s.iterator(); // Must be in the synchronized block
	 *      while (i.hasNext())
	 *          foo(i.next());
	 *  }
	 * </pre>
	 *
	 * or:
	 *
	 * <pre>
	 *  NavigableSet s = Collections.synchronizedNavigableSet(new TreeSet());
	 *  NavigableSet s2 = s.headSet(foo, true);
	 *      ...
	 *  synchronized (s) {  // Note: s, not s2!!!
	 *      Iterator i = s2.iterator(); // Must be in the synchronized block
	 *      while (i.hasNext())
	 *          foo(i.next());
	 *  }
	 * </pre>
	 *
	 * Failure to follow this advice may result in non-deterministic behavior.
	 *
	 * <p>
	 * The returned navigable set will be serializable if the specified
	 * navigable set is serializable.
	 *
	 * @param <T>
	 *            the class of the objects in the set
	 * @param s
	 *            the navigable set to be "wrapped" in a synchronized navigable
	 *            set
	 * @return a synchronized view of the specified navigable set
	 * @since 1.8
	 */
	public static <T> NavigableSet<T> synchronizedNavigableSet(NavigableSet<T> s) {
		return new SynchronizedNavigableSet<>(s);
	}

	static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E> {
		private static final long serialVersionUID = -5505529816273629798L;
		private final NavigableSet<E> ns;

		SynchronizedNavigableSet(NavigableSet<E> s) {
			super(s);
			ns = s;
		}

		SynchronizedNavigableSet(NavigableSet<E> s, Object mutex) {
			super(s, mutex);
			ns = s;
		}

		@Override
		public E lower(E e) {
			synchronized (mutex) {
				return ns.lower(e);
			}
		}

		@Override
		public E floor(E e) {
			synchronized (mutex) {
				return ns.floor(e);
			}
		}

		@Override
		public E ceiling(E e) {
			synchronized (mutex) {
				return ns.ceiling(e);
			}
		}

		@Override
		public E higher(E e) {
			synchronized (mutex) {
				return ns.higher(e);
			}
		}

		@Override
		public E pollFirst() {
			synchronized (mutex) {
				return ns.pollFirst();
			}
		}

		@Override
		public E pollLast() {
			synchronized (mutex) {
				return ns.pollLast();
			}
		}

		@Override
		public NavigableSet<E> descendingSet() {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.descendingSet(), mutex);
			}
		}

		@Override
		public Iterator<E> descendingIterator() {
			synchronized (mutex) {
				return descendingSet().iterator();
			}
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, E toElement) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.subSet(fromElement, true, toElement, false), mutex);
			}
		}

		@Override
		public NavigableSet<E> headSet(E toElement) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.headSet(toElement, false), mutex);
			}
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.tailSet(fromElement, true), mutex);
			}
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.subSet(fromElement, fromInclusive, toElement, toInclusive), mutex);
			}
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.headSet(toElement, inclusive), mutex);
			}
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(ns.tailSet(fromElement, inclusive), mutex);
			}
		}
	}

	public static <T> List<T> synchronizedList(List<T> list) {
		return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<>(list) : new SynchronizedList<>(list));
	}

	static <T> List<T> synchronizedList(List<T> list, Object mutex) {
		return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<>(list, mutex) : new SynchronizedList<>(list, mutex));
	}

	static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E> {
		private static final long serialVersionUID = -7754090372962971524L;
		final List<E> list;

		SynchronizedList(List<E> list) {
			super(list);
			this.list = list;
		}

		SynchronizedList(List<E> list, Object mutex) {
			super(list, mutex);
			this.list = list;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			synchronized (mutex) {
				return list.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (mutex) {
				return list.hashCode();
			}
		}

		@Override
		public E get(int index) {
			synchronized (mutex) {
				return list.get(index);
			}
		}

		@Override
		public E set(int index, E element) {
			synchronized (mutex) {
				return list.set(index, element);
			}
		}

		@Override
		public void add(int index, E element) {
			synchronized (mutex) {
				list.add(index, element);
			}
		}

		@Override
		public E remove(int index) {
			synchronized (mutex) {
				return list.remove(index);
			}
		}

		@Override
		public int indexOf(Object o) {
			synchronized (mutex) {
				return list.indexOf(o);
			}
		}

		@Override
		public int lastIndexOf(Object o) {
			synchronized (mutex) {
				return list.lastIndexOf(o);
			}
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			synchronized (mutex) {
				return list.addAll(index, c);
			}
		}

		@Override
		public ListIterator<E> listIterator() {
			return list.listIterator();
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			return list.listIterator(index);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedList<>(list.subList(fromIndex, toIndex), mutex);
			}
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			synchronized (mutex) {
				list.replaceAll(operator);
			}
		}

		@Override
		public void sort(Comparator<? super E> c) {
			synchronized (mutex) {
				list.sort(c);
			}
		}

		private Object readResolve() {
			return (list instanceof RandomAccess ? new SynchronizedRandomAccessList<>(list) : this);
		}
	}

	static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess {
		SynchronizedRandomAccessList(List<E> list) {
			super(list);
		}

		SynchronizedRandomAccessList(List<E> list, Object mutex) {
			super(list, mutex);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			synchronized (mutex) {
				return new SynchronizedRandomAccessList<>(list.subList(fromIndex, toIndex), mutex);
			}
		}

		private static final long serialVersionUID = 1530674583602358482L;

		private Object writeReplace() {
			return new SynchronizedList<>(list);
		}
	}

	public static <K, V> Map<K, V> synchronizedMap(Map<K, V> m) {
		return new SynchronizedMap<>(m);
	}

	private static class SynchronizedMap<K, V> implements Map<K, V>, Serializable {
		private static final long serialVersionUID = 1978198479659022715L;
		private final Map<K, V> m;
		final Object mutex;

		SynchronizedMap(Map<K, V> m) {
			this.m = Objects.requireNonNull(m);
			mutex = this;
		}

		SynchronizedMap(Map<K, V> m, Object mutex) {
			this.m = m;
			this.mutex = mutex;
		}

		@Override
		public int size() {
			synchronized (mutex) {
				return m.size();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (mutex) {
				return m.isEmpty();
			}
		}

		@Override
		public boolean containsKey(Object key) {
			synchronized (mutex) {
				return m.containsKey(key);
			}
		}

		@Override
		public boolean containsValue(Object value) {
			synchronized (mutex) {
				return m.containsValue(value);
			}
		}

		@Override
		public V get(Object key) {
			synchronized (mutex) {
				return m.get(key);
			}
		}

		@Override
		public V put(K key, V value) {
			synchronized (mutex) {
				return m.put(key, value);
			}
		}

		@Override
		public V remove(Object key) {
			synchronized (mutex) {
				return m.remove(key);
			}
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> map) {
			synchronized (mutex) {
				m.putAll(map);
			}
		}

		@Override
		public void clear() {
			synchronized (mutex) {
				m.clear();
			}
		}

		private transient Set<K> keySet;
		private transient Set<Map.Entry<K, V>> entrySet;
		private transient Collection<V> values;

		@Override
		public Set<K> keySet() {
			synchronized (mutex) {
				if (keySet == null) {
					keySet = new SynchronizedSet<>(m.keySet(), mutex);
				}
				return keySet;
			}
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			synchronized (mutex) {
				if (entrySet == null) {
					entrySet = new SynchronizedSet<>(m.entrySet(), mutex);
				}
				return entrySet;
			}
		}

		@Override
		public Collection<V> values() {
			synchronized (mutex) {
				if (values == null) {
					values = new SynchronizedCollection<>(m.values(), mutex);
				}
				return values;
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			synchronized (mutex) {
				return m.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (mutex) {
				return m.hashCode();
			}
		}

		@Override
		public String toString() {
			synchronized (mutex) {
				return m.toString();
			}
		}

		@Override
		public V getOrDefault(Object k, V defaultValue) {
			synchronized (mutex) {
				return m.getOrDefault(k, defaultValue);
			}
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			synchronized (mutex) {
				m.forEach(action);
			}
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			synchronized (mutex) {
				m.replaceAll(function);
			}
		}

		@Override
		public V putIfAbsent(K key, V value) {
			synchronized (mutex) {
				return m.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(Object key, Object value) {
			synchronized (mutex) {
				return m.remove(key, value);
			}
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			synchronized (mutex) {
				return m.replace(key, oldValue, newValue);
			}
		}

		@Override
		public V replace(K key, V value) {
			synchronized (mutex) {
				return m.replace(key, value);
			}
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			synchronized (mutex) {
				return m.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			synchronized (mutex) {
				return m.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			synchronized (mutex) {
				return m.compute(key, remappingFunction);
			}
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			synchronized (mutex) {
				return m.merge(key, value, remappingFunction);
			}
		}

		private void writeObject(ObjectOutputStream s) throws IOException {
			synchronized (mutex) {
				s.defaultWriteObject();
			}
		}
	}

	public static <K, V> SortedMap<K, V> synchronizedSortedMap(SortedMap<K, V> m) {
		return new SynchronizedSortedMap<>(m);
	}

	static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V> {
		private static final long serialVersionUID = -8798146769416483793L;
		private final SortedMap<K, V> sm;

		SynchronizedSortedMap(SortedMap<K, V> m) {
			super(m);
			sm = m;
		}

		SynchronizedSortedMap(SortedMap<K, V> m, Object mutex) {
			super(m, mutex);
			sm = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			synchronized (mutex) {
				return sm.comparator();
			}
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<>(sm.subMap(fromKey, toKey), mutex);
			}
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<>(sm.headMap(toKey), mutex);
			}
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			synchronized (mutex) {
				return new SynchronizedSortedMap<>(sm.tailMap(fromKey), mutex);
			}
		}

		@Override
		public K firstKey() {
			synchronized (mutex) {
				return sm.firstKey();
			}
		}

		@Override
		public K lastKey() {
			synchronized (mutex) {
				return sm.lastKey();
			}
		}
	}

	/**
	 * Returns a synchronized (thread-safe) navigable map backed by the
	 * specified navigable map. In order to guarantee serial access, it is
	 * critical that <strong>all</strong> access to the backing navigable map is
	 * accomplished through the returned navigable map (or its views).
	 * <p>
	 *
	 * It is imperative that the user manually synchronize on the returned
	 * navigable map when iterating over any of its collection views, or the
	 * collections views of any of its {@code subMap}, {@code headMap} or
	 * {@code tailMap} views.
	 *
	 * <pre>
	 *  NavigableMap m = Collections.synchronizedNavigableMap(new TreeMap());
	 *      ...
	 *  Set s = m.keySet();  // Needn't be in synchronized block
	 *      ...
	 *  synchronized (m) {  // Synchronizing on m, not s!
	 *      Iterator i = s.iterator(); // Must be in synchronized block
	 *      while (i.hasNext())
	 *          foo(i.next());
	 *  }
	 * </pre>
	 *
	 * or:
	 *
	 * <pre>
	 *  NavigableMap m = Collections.synchronizedNavigableMap(new TreeMap());
	 *  NavigableMap m2 = m.subMap(foo, true, bar, false);
	 *      ...
	 *  Set s2 = m2.keySet();  // Needn't be in synchronized block
	 *      ...
	 *  synchronized (m) {  // Synchronizing on m, not m2 or s2!
	 *      Iterator i = s.iterator(); // Must be in synchronized block
	 *      while (i.hasNext())
	 *          foo(i.next());
	 *  }
	 * </pre>
	 *
	 * Failure to follow this advice may result in non-deterministic behavior.
	 *
	 * <p>
	 * The returned navigable map will be serializable if the specified
	 * navigable map is serializable.
	 *
	 * @param <K>
	 *            the class of the map keys
	 * @param <V>
	 *            the class of the map values
	 * @param m
	 *            the navigable map to be "wrapped" in a synchronized navigable
	 *            map
	 * @return a synchronized view of the specified navigable map.
	 * @since 1.8
	 */
	public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> m) {
		return new SynchronizedNavigableMap<>(m);
	}

	static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V> {
		private static final long serialVersionUID = 699392247599746807L;
		private final NavigableMap<K, V> nm;

		SynchronizedNavigableMap(NavigableMap<K, V> m) {
			super(m);
			nm = m;
		}

		SynchronizedNavigableMap(NavigableMap<K, V> m, Object mutex) {
			super(m, mutex);
			nm = m;
		}

		@Override
		public Entry<K, V> lowerEntry(K key) {
			synchronized (mutex) {
				return nm.lowerEntry(key);
			}
		}

		@Override
		public K lowerKey(K key) {
			synchronized (mutex) {
				return nm.lowerKey(key);
			}
		}

		@Override
		public Entry<K, V> floorEntry(K key) {
			synchronized (mutex) {
				return nm.floorEntry(key);
			}
		}

		@Override
		public K floorKey(K key) {
			synchronized (mutex) {
				return nm.floorKey(key);
			}
		}

		@Override
		public Entry<K, V> ceilingEntry(K key) {
			synchronized (mutex) {
				return nm.ceilingEntry(key);
			}
		}

		@Override
		public K ceilingKey(K key) {
			synchronized (mutex) {
				return nm.ceilingKey(key);
			}
		}

		@Override
		public Entry<K, V> higherEntry(K key) {
			synchronized (mutex) {
				return nm.higherEntry(key);
			}
		}

		@Override
		public K higherKey(K key) {
			synchronized (mutex) {
				return nm.higherKey(key);
			}
		}

		@Override
		public Entry<K, V> firstEntry() {
			synchronized (mutex) {
				return nm.firstEntry();
			}
		}

		@Override
		public Entry<K, V> lastEntry() {
			synchronized (mutex) {
				return nm.lastEntry();
			}
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			synchronized (mutex) {
				return nm.pollFirstEntry();
			}
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			synchronized (mutex) {
				return nm.pollLastEntry();
			}
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.descendingMap(), mutex);
			}
		}

		@Override
		public NavigableSet<K> keySet() {
			return navigableKeySet();
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(nm.navigableKeySet(), mutex);
			}
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			synchronized (mutex) {
				return new SynchronizedNavigableSet<>(nm.descendingKeySet(), mutex);
			}
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.subMap(fromKey, true, toKey, false), mutex);
			}
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.headMap(toKey, false), mutex);
			}
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.tailMap(fromKey, true), mutex);
			}
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.subMap(fromKey, fromInclusive, toKey, toInclusive), mutex);
			}
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.headMap(toKey, inclusive), mutex);
			}
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			synchronized (mutex) {
				return new SynchronizedNavigableMap<>(nm.tailMap(fromKey, inclusive), mutex);
			}
		}
	}

	public static <E> Collection<E> checkedCollection(Collection<E> c, Class<E> type) {
		return new CheckedCollection<>(c, type);
	}

	@SuppressWarnings("unchecked")
	static <T> T[] zeroLengthArray(Class<T> type) {
		return (T[]) Array.newInstance(type, 0);
	}

	static class CheckedCollection<E> implements Collection<E>, Serializable {
		private static final long serialVersionUID = 1578914078182001775L;
		final Collection<E> c;
		final Class<E> type;

		@SuppressWarnings("unchecked")
		E typeCheck(Object o) {
			if ((o != null) && !type.isInstance(o)) {
				throw new ClassCastException(badElementMsg(o));
			}
			return (E) o;
		}

		private String badElementMsg(Object o) {
			return "Attempt to insert " + o.getClass() + " element into collection with element type " + type;
		}

		CheckedCollection(Collection<E> c, Class<E> type) {
			this.c = Objects.requireNonNull(c, "c");
			this.type = Objects.requireNonNull(type, "type");
		}

		@Override
		public int size() {
			return c.size();
		}

		@Override
		public boolean isEmpty() {
			return c.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return c.contains(o);
		}

		@Override
		public Object[] toArray() {
			return c.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return c.toArray(a);
		}

		@Override
		public String toString() {
			return c.toString();
		}

		@Override
		public boolean remove(Object o) {
			return c.remove(o);
		}

		@Override
		public void clear() {
			c.clear();
		}

		@Override
		public boolean containsAll(Collection<?> coll) {
			return c.containsAll(coll);
		}

		@Override
		public boolean removeAll(Collection<?> coll) {
			return c.removeAll(coll);
		}

		@Override
		public boolean retainAll(Collection<?> coll) {
			return c.retainAll(coll);
		}

		@Override
		public Iterator<E> iterator() {
			final Iterator<E> it = c.iterator();
			return new Iterator<E>() {
				@Override
				public boolean hasNext() {
					return it.hasNext();
				}

				@Override
				public E next() {
					return it.next();
				}

				@Override
				public void remove() {
					it.remove();
				}
			};
		}

		@Override
		public boolean add(E e) {
			return c.add(typeCheck(e));
		}

		private E[] zeroLengthElementArray;

		private E[] zeroLengthElementArray() {
			return zeroLengthElementArray != null ? zeroLengthElementArray : (zeroLengthElementArray = zeroLengthArray(type));
		}

		@SuppressWarnings("unchecked")
		Collection<E> checkedCopyOf(Collection<? extends E> coll) {
			Object[] a;
			try {
				E[] z = zeroLengthElementArray();
				a = coll.toArray(z);
				if (a.getClass() != z.getClass()) {
					a = Arrays.copyOf(a, a.length, z.getClass());
				}
			} catch (ArrayStoreException ignore) {
				a = coll.toArray().clone();
				for (Object o : a) {
					typeCheck(o);
				}
			}
			return (Collection<E>) Arrays.asList(a);
		}

		@Override
		public boolean addAll(Collection<? extends E> coll) {
			return c.addAll(checkedCopyOf(coll));
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			c.forEach(action);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			return c.removeIf(filter);
		}

		@Override
		public Spliterator<E> spliterator() {
			return c.spliterator();
		}

		@Override
		public Stream<E> stream() {
			return c.stream();
		}

		@Override
		public Stream<E> parallelStream() {
			return c.parallelStream();
		}
	}

	/**
	 * Returns a dynamically typesafe view of the specified queue. Any attempt
	 * to insert an element of the wrong type will result in an immediate
	 * {@link ClassCastException}. Assuming a queue contains no incorrectly
	 * typed elements prior to the time a dynamically typesafe view is
	 * generated, and that all subsequent access to the queue takes place
	 * through the view, it is <i>guaranteed</i> that the queue cannot contain
	 * an incorrectly typed element.
	 *
	 * <p>
	 * A discussion of the use of dynamically typesafe views may be found in the
	 * documentation for the {@link #checkedCollection checkedCollection}
	 * method.
	 *
	 * <p>
	 * The returned queue will be serializable if the specified queue is
	 * serializable.
	 *
	 * <p>
	 * Since {@code null} is considered to be a value of any reference type, the
	 * returned queue permits insertion of {@code null} elements whenever the
	 * backing queue does.
	 *
	 * @param <E>
	 *            the class of the objects in the queue
	 * @param queue
	 *            the queue for which a dynamically typesafe view is to be
	 *            returned
	 * @param type
	 *            the type of element that {@code queue} is permitted to hold
	 * @return a dynamically typesafe view of the specified queue
	 * @since 1.8
	 */
	public static <E> Queue<E> checkedQueue(Queue<E> queue, Class<E> type) {
		return new CheckedQueue<>(queue, type);
	}

	static class CheckedQueue<E> extends CheckedCollection<E> implements Queue<E>, Serializable {
		private static final long serialVersionUID = 1433151992604707767L;
		final Queue<E> queue;

		CheckedQueue(Queue<E> queue, Class<E> elementType) {
			super(queue, elementType);
			this.queue = queue;
		}

		@Override
		public E element() {
			return queue.element();
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || c.equals(o);
		}

		@Override
		public int hashCode() {
			return c.hashCode();
		}

		@Override
		public E peek() {
			return queue.peek();
		}

		@Override
		public E poll() {
			return queue.poll();
		}

		@Override
		public E remove() {
			return queue.remove();
		}

		@Override
		public boolean offer(E e) {
			return queue.offer(typeCheck(e));
		}
	}

	public static <E> Set<E> checkedSet(Set<E> s, Class<E> type) {
		return new CheckedSet<>(s, type);
	}

	static class CheckedSet<E> extends CheckedCollection<E> implements Set<E>, Serializable {
		private static final long serialVersionUID = 4694047833775013803L;

		CheckedSet(Set<E> s, Class<E> elementType) {
			super(s, elementType);
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || c.equals(o);
		}

		@Override
		public int hashCode() {
			return c.hashCode();
		}
	}

	public static <E> SortedSet<E> checkedSortedSet(SortedSet<E> s, Class<E> type) {
		return new CheckedSortedSet<>(s, type);
	}

	static class CheckedSortedSet<E> extends CheckedSet<E> implements SortedSet<E>, Serializable {
		private static final long serialVersionUID = 1599911165492914959L;
		private final SortedSet<E> ss;

		CheckedSortedSet(SortedSet<E> s, Class<E> type) {
			super(s, type);
			ss = s;
		}

		@Override
		public Comparator<? super E> comparator() {
			return ss.comparator();
		}

		@Override
		public E first() {
			return ss.first();
		}

		@Override
		public E last() {
			return ss.last();
		}

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return checkedSortedSet(ss.subSet(fromElement, toElement), type);
		}

		@Override
		public SortedSet<E> headSet(E toElement) {
			return checkedSortedSet(ss.headSet(toElement), type);
		}

		@Override
		public SortedSet<E> tailSet(E fromElement) {
			return checkedSortedSet(ss.tailSet(fromElement), type);
		}
	}

	/**
	 * Returns a dynamically typesafe view of the specified navigable set. Any
	 * attempt to insert an element of the wrong type will result in an
	 * immediate {@link ClassCastException}. Assuming a navigable set contains
	 * no incorrectly typed elements prior to the time a dynamically typesafe
	 * view is generated, and that all subsequent access to the navigable set
	 * takes place through the view, it is <em>guaranteed</em> that the
	 * navigable set cannot contain an incorrectly typed element.
	 *
	 * <p>
	 * A discussion of the use of dynamically typesafe views may be found in the
	 * documentation for the {@link #checkedCollection checkedCollection}
	 * method.
	 *
	 * <p>
	 * The returned navigable set will be serializable if the specified
	 * navigable set is serializable.
	 *
	 * <p>
	 * Since {@code null} is considered to be a value of any reference type, the
	 * returned navigable set permits insertion of null elements whenever the
	 * backing sorted set does.
	 *
	 * @param <E>
	 *            the class of the objects in the set
	 * @param s
	 *            the navigable set for which a dynamically typesafe view is to
	 *            be returned
	 * @param type
	 *            the type of element that {@code s} is permitted to hold
	 * @return a dynamically typesafe view of the specified navigable set
	 * @since 1.8
	 */
	public static <E> NavigableSet<E> checkedNavigableSet(NavigableSet<E> s, Class<E> type) {
		return new CheckedNavigableSet<>(s, type);
	}

	static class CheckedNavigableSet<E> extends CheckedSortedSet<E> implements NavigableSet<E>, Serializable {
		private static final long serialVersionUID = -5429120189805438922L;
		private final NavigableSet<E> ns;

		CheckedNavigableSet(NavigableSet<E> s, Class<E> type) {
			super(s, type);
			ns = s;
		}

		@Override
		public E lower(E e) {
			return ns.lower(e);
		}

		@Override
		public E floor(E e) {
			return ns.floor(e);
		}

		@Override
		public E ceiling(E e) {
			return ns.ceiling(e);
		}

		@Override
		public E higher(E e) {
			return ns.higher(e);
		}

		@Override
		public E pollFirst() {
			return ns.pollFirst();
		}

		@Override
		public E pollLast() {
			return ns.pollLast();
		}

		@Override
		public NavigableSet<E> descendingSet() {
			return checkedNavigableSet(ns.descendingSet(), type);
		}

		@Override
		public Iterator<E> descendingIterator() {
			return checkedNavigableSet(ns.descendingSet(), type).iterator();
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, E toElement) {
			return checkedNavigableSet(ns.subSet(fromElement, true, toElement, false), type);
		}

		@Override
		public NavigableSet<E> headSet(E toElement) {
			return checkedNavigableSet(ns.headSet(toElement, false), type);
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement) {
			return checkedNavigableSet(ns.tailSet(fromElement, true), type);
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
			return checkedNavigableSet(ns.subSet(fromElement, fromInclusive, toElement, toInclusive), type);
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return checkedNavigableSet(ns.headSet(toElement, inclusive), type);
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return checkedNavigableSet(ns.tailSet(fromElement, inclusive), type);
		}
	}

	public static <E> List<E> checkedList(List<E> list, Class<E> type) {
		return (list instanceof RandomAccess ? new CheckedRandomAccessList<>(list, type) : new CheckedList<>(list, type));
	}

	static class CheckedList<E> extends CheckedCollection<E> implements List<E> {
		private static final long serialVersionUID = 65247728283967356L;
		final List<E> list;

		CheckedList(List<E> list, Class<E> type) {
			super(list, type);
			this.list = list;
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || list.equals(o);
		}

		@Override
		public int hashCode() {
			return list.hashCode();
		}

		@Override
		public E get(int index) {
			return list.get(index);
		}

		@Override
		public E remove(int index) {
			return list.remove(index);
		}

		@Override
		public int indexOf(Object o) {
			return list.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}

		@Override
		public E set(int index, E element) {
			return list.set(index, typeCheck(element));
		}

		@Override
		public void add(int index, E element) {
			list.add(index, typeCheck(element));
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			return list.addAll(index, checkedCopyOf(c));
		}

		@Override
		public ListIterator<E> listIterator() {
			return listIterator(0);
		}

		@Override
		public ListIterator<E> listIterator(final int index) {
			final ListIterator<E> i = list.listIterator(index);
			return new ListIterator<E>() {
				@Override
				public boolean hasNext() {
					return i.hasNext();
				}

				@Override
				public E next() {
					return i.next();
				}

				@Override
				public boolean hasPrevious() {
					return i.hasPrevious();
				}

				@Override
				public E previous() {
					return i.previous();
				}

				@Override
				public int nextIndex() {
					return i.nextIndex();
				}

				@Override
				public int previousIndex() {
					return i.previousIndex();
				}

				@Override
				public void remove() {
					i.remove();
				}

				@Override
				public void set(E e) {
					i.set(typeCheck(e));
				}

				@Override
				public void add(E e) {
					i.add(typeCheck(e));
				}

				@Override
				public void forEachRemaining(Consumer<? super E> action) {
					i.forEachRemaining(action);
				}
			};
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return new CheckedList<>(list.subList(fromIndex, toIndex), type);
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			Objects.requireNonNull(operator);
			list.replaceAll(e -> typeCheck(operator.apply(e)));
		}

		@Override
		public void sort(Comparator<? super E> c) {
			list.sort(c);
		}
	}

	static class CheckedRandomAccessList<E> extends CheckedList<E> implements RandomAccess {
		private static final long serialVersionUID = 1638200125423088369L;

		CheckedRandomAccessList(List<E> list, Class<E> type) {
			super(list, type);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return new CheckedRandomAccessList<>(list.subList(fromIndex, toIndex), type);
		}
	}

	public static <K, V> Map<K, V> checkedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
		return new CheckedMap<>(m, keyType, valueType);
	}

	private static class CheckedMap<K, V> implements Map<K, V>, Serializable {
		private static final long serialVersionUID = 5742860141034234728L;
		private final Map<K, V> m;
		final Class<K> keyType;
		final Class<V> valueType;

		private void typeCheck(Object key, Object value) {
			if ((key != null) && !keyType.isInstance(key)) {
				throw new ClassCastException(badKeyMsg(key));
			}
			if ((value != null) && !valueType.isInstance(value)) {
				throw new ClassCastException(badValueMsg(value));
			}
		}

		private BiFunction<? super K, ? super V, ? extends V> typeCheck(BiFunction<? super K, ? super V, ? extends V> func) {
			Objects.requireNonNull(func);
			return (k, v) -> {
				V newValue = func.apply(k, v);
				typeCheck(k, newValue);
				return newValue;
			};
		}

		private String badKeyMsg(Object key) {
			return "Attempt to insert " + key.getClass() + " key into map with key type " + keyType;
		}

		private String badValueMsg(Object value) {
			return "Attempt to insert " + value.getClass() + " value into map with value type " + valueType;
		}

		CheckedMap(Map<K, V> m, Class<K> keyType, Class<V> valueType) {
			this.m = Objects.requireNonNull(m);
			this.keyType = Objects.requireNonNull(keyType);
			this.valueType = Objects.requireNonNull(valueType);
		}

		@Override
		public int size() {
			return m.size();
		}

		@Override
		public boolean isEmpty() {
			return m.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return m.containsKey(key);
		}

		@Override
		public boolean containsValue(Object v) {
			return m.containsValue(v);
		}

		@Override
		public V get(Object key) {
			return m.get(key);
		}

		@Override
		public V remove(Object key) {
			return m.remove(key);
		}

		@Override
		public void clear() {
			m.clear();
		}

		@Override
		public Set<K> keySet() {
			return m.keySet();
		}

		@Override
		public Collection<V> values() {
			return m.values();
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || m.equals(o);
		}

		@Override
		public int hashCode() {
			return m.hashCode();
		}

		@Override
		public String toString() {
			return m.toString();
		}

		@Override
		public V put(K key, V value) {
			typeCheck(key, value);
			return m.put(key, value);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void putAll(Map<? extends K, ? extends V> t) {
			Object[] entries = t.entrySet().toArray();
			List<Map.Entry<K, V>> checked = new ArrayList<>(entries.length);
			for (Object o : entries) {
				Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
				Object k = e.getKey();
				Object v = e.getValue();
				typeCheck(k, v);
				checked.add(new AbstractMap.SimpleImmutableEntry<>((K) k, (V) v));
			}
			for (Map.Entry<K, V> e : checked) {
				m.put(e.getKey(), e.getValue());
			}
		}

		private transient Set<Map.Entry<K, V>> entrySet;

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null) {
				entrySet = new CheckedEntrySet<>(m.entrySet(), valueType);
			}
			return entrySet;
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			m.forEach(action);
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			m.replaceAll(typeCheck(function));
		}

		@Override
		public V putIfAbsent(K key, V value) {
			typeCheck(key, value);
			return m.putIfAbsent(key, value);
		}

		@Override
		public boolean remove(Object key, Object value) {
			return m.remove(key, value);
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			typeCheck(key, newValue);
			return m.replace(key, oldValue, newValue);
		}

		@Override
		public V replace(K key, V value) {
			typeCheck(key, value);
			return m.replace(key, value);
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			Objects.requireNonNull(mappingFunction);
			return m.computeIfAbsent(key, k -> {
				V value = mappingFunction.apply(k);
				typeCheck(k, value);
				return value;
			});
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			return m.computeIfPresent(key, typeCheck(remappingFunction));
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			return m.compute(key, typeCheck(remappingFunction));
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			Objects.requireNonNull(remappingFunction);
			return m.merge(key, value, (v1, v2) -> {
				V newValue = remappingFunction.apply(v1, v2);
				typeCheck(null, newValue);
				return newValue;
			});
		}

		static class CheckedEntrySet<K, V> implements Set<Map.Entry<K, V>> {
			private final Set<Map.Entry<K, V>> s;
			private final Class<V> valueType;

			CheckedEntrySet(Set<Map.Entry<K, V>> s, Class<V> valueType) {
				this.s = s;
				this.valueType = valueType;
			}

			@Override
			public int size() {
				return s.size();
			}

			@Override
			public boolean isEmpty() {
				return s.isEmpty();
			}

			@Override
			public String toString() {
				return s.toString();
			}

			@Override
			public int hashCode() {
				return s.hashCode();
			}

			@Override
			public void clear() {
				s.clear();
			}

			@Override
			public boolean add(Map.Entry<K, V> e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean addAll(Collection<? extends Map.Entry<K, V>> coll) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Iterator<Map.Entry<K, V>> iterator() {
				final Iterator<Map.Entry<K, V>> i = s.iterator();
				final Class<V> valueType = this.valueType;
				return new Iterator<Map.Entry<K, V>>() {
					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public void remove() {
						i.remove();
					}

					@Override
					public Map.Entry<K, V> next() {
						return checkedEntry(i.next(), valueType);
					}
				};
			}

			@Override
			@SuppressWarnings("unchecked")
			public Object[] toArray() {
				Object[] source = s.toArray();
				Object[] dest = (CheckedEntry.class.isInstance(source.getClass().getComponentType()) ? source : new Object[source.length]);
				for (int i = 0; i < source.length; i++) {
					dest[i] = checkedEntry((Map.Entry<K, V>) source[i], valueType);
				}
				return dest;
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T[] toArray(T[] a) {
				T[] arr = s.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));
				for (int i = 0; i < arr.length; i++) {
					arr[i] = (T) checkedEntry((Map.Entry<K, V>) arr[i], valueType);
				}
				if (arr.length > a.length) {
					return arr;
				}
				System.arraycopy(arr, 0, a, 0, arr.length);
				if (a.length > arr.length) {
					a[arr.length] = null;
				}
				return a;
			}

			@Override
			public boolean contains(Object o) {
				if (!(o instanceof Map.Entry)) {
					return false;
				}
				Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
				return s.contains((e instanceof CheckedEntry) ? e : checkedEntry(e, valueType));
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!contains(o)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public boolean remove(Object o) {
				if (!(o instanceof Map.Entry)) {
					return false;
				}
				return s.remove(new AbstractMap.SimpleImmutableEntry<>((Map.Entry<?, ?>) o));
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				return batchRemove(c, false);
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				return batchRemove(c, true);
			}

			private boolean batchRemove(Collection<?> c, boolean complement) {
				Objects.requireNonNull(c);
				boolean modified = false;
				Iterator<Map.Entry<K, V>> it = iterator();
				while (it.hasNext()) {
					if (c.contains(it.next()) != complement) {
						it.remove();
						modified = true;
					}
				}
				return modified;
			}

			@Override
			public boolean equals(Object o) {
				if (o == this) {
					return true;
				}
				if (!(o instanceof Set)) {
					return false;
				}
				Set<?> that = (Set<?>) o;
				return (that.size() == s.size()) && containsAll(that);
			}

			static <K, V, T> CheckedEntry<K, V, T> checkedEntry(Map.Entry<K, V> e, Class<T> valueType) {
				return new CheckedEntry<>(e, valueType);
			}

			private static class CheckedEntry<K, V, T> implements Map.Entry<K, V> {
				private final Map.Entry<K, V> e;
				private final Class<T> valueType;

				CheckedEntry(Map.Entry<K, V> e, Class<T> valueType) {
					this.e = Objects.requireNonNull(e);
					this.valueType = Objects.requireNonNull(valueType);
				}

				@Override
				public K getKey() {
					return e.getKey();
				}

				@Override
				public V getValue() {
					return e.getValue();
				}

				@Override
				public int hashCode() {
					return e.hashCode();
				}

				@Override
				public String toString() {
					return e.toString();
				}

				@Override
				public V setValue(V value) {
					if ((value != null) && !valueType.isInstance(value)) {
						throw new ClassCastException(badValueMsg(value));
					}
					return e.setValue(value);
				}

				private String badValueMsg(Object value) {
					return "Attempt to insert " + value.getClass() + " value into map with value type " + valueType;
				}

				@Override
				public boolean equals(Object o) {
					if (o == this) {
						return true;
					}
					if (!(o instanceof Map.Entry)) {
						return false;
					}
					return e.equals(new AbstractMap.SimpleImmutableEntry<>((Map.Entry<?, ?>) o));
				}
			}
		}
	}

	public static <K, V> SortedMap<K, V> checkedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
		return new CheckedSortedMap<>(m, keyType, valueType);
	}

	static class CheckedSortedMap<K, V> extends CheckedMap<K, V> implements SortedMap<K, V>, Serializable {
		private static final long serialVersionUID = 1599671320688067438L;
		private final SortedMap<K, V> sm;

		CheckedSortedMap(SortedMap<K, V> m, Class<K> keyType, Class<V> valueType) {
			super(m, keyType, valueType);
			sm = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			return sm.comparator();
		}

		@Override
		public K firstKey() {
			return sm.firstKey();
		}

		@Override
		public K lastKey() {
			return sm.lastKey();
		}

		@Override
		public SortedMap<K, V> subMap(K fromKey, K toKey) {
			return checkedSortedMap(sm.subMap(fromKey, toKey), keyType, valueType);
		}

		@Override
		public SortedMap<K, V> headMap(K toKey) {
			return checkedSortedMap(sm.headMap(toKey), keyType, valueType);
		}

		@Override
		public SortedMap<K, V> tailMap(K fromKey) {
			return checkedSortedMap(sm.tailMap(fromKey), keyType, valueType);
		}
	}

	/**
	 * Returns a dynamically typesafe view of the specified navigable map. Any
	 * attempt to insert a mapping whose key or value have the wrong type will
	 * result in an immediate {@link ClassCastException}. Similarly, any attempt
	 * to modify the value currently associated with a key will result in an
	 * immediate {@link ClassCastException}, whether the modification is
	 * attempted directly through the map itself, or through a {@link Map.Entry}
	 * instance obtained from the map's {@link Map#entrySet() entry set} view.
	 *
	 * <p>
	 * Assuming a map contains no incorrectly typed keys or values prior to the
	 * time a dynamically typesafe view is generated, and that all subsequent
	 * access to the map takes place through the view (or one of its collection
	 * views), it is <em>guaranteed</em> that the map cannot contain an
	 * incorrectly typed key or value.
	 *
	 * <p>
	 * A discussion of the use of dynamically typesafe views may be found in the
	 * documentation for the {@link #checkedCollection checkedCollection}
	 * method.
	 *
	 * <p>
	 * The returned map will be serializable if the specified map is
	 * serializable.
	 *
	 * <p>
	 * Since {@code null} is considered to be a value of any reference type, the
	 * returned map permits insertion of null keys or values whenever the
	 * backing map does.
	 *
	 * @param <K>
	 *            type of map keys
	 * @param <V>
	 *            type of map values
	 * @param m
	 *            the map for which a dynamically typesafe view is to be
	 *            returned
	 * @param keyType
	 *            the type of key that {@code m} is permitted to hold
	 * @param valueType
	 *            the type of value that {@code m} is permitted to hold
	 * @return a dynamically typesafe view of the specified map
	 * @since 1.8
	 */
	public static <K, V> NavigableMap<K, V> checkedNavigableMap(NavigableMap<K, V> m, Class<K> keyType, Class<V> valueType) {
		return new CheckedNavigableMap<>(m, keyType, valueType);
	}

	static class CheckedNavigableMap<K, V> extends CheckedSortedMap<K, V> implements NavigableMap<K, V>, Serializable {
		private static final long serialVersionUID = -4852462692372534096L;
		private final NavigableMap<K, V> nm;

		CheckedNavigableMap(NavigableMap<K, V> m, Class<K> keyType, Class<V> valueType) {
			super(m, keyType, valueType);
			nm = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			return nm.comparator();
		}

		@Override
		public K firstKey() {
			return nm.firstKey();
		}

		@Override
		public K lastKey() {
			return nm.lastKey();
		}

		@Override
		public Entry<K, V> lowerEntry(K key) {
			Entry<K, V> lower = nm.lowerEntry(key);
			return (null != lower) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(lower, valueType) : null;
		}

		@Override
		public K lowerKey(K key) {
			return nm.lowerKey(key);
		}

		@Override
		public Entry<K, V> floorEntry(K key) {
			Entry<K, V> floor = nm.floorEntry(key);
			return (null != floor) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(floor, valueType) : null;
		}

		@Override
		public K floorKey(K key) {
			return nm.floorKey(key);
		}

		@Override
		public Entry<K, V> ceilingEntry(K key) {
			Entry<K, V> ceiling = nm.ceilingEntry(key);
			return (null != ceiling) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(ceiling, valueType) : null;
		}

		@Override
		public K ceilingKey(K key) {
			return nm.ceilingKey(key);
		}

		@Override
		public Entry<K, V> higherEntry(K key) {
			Entry<K, V> higher = nm.higherEntry(key);
			return (null != higher) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(higher, valueType) : null;
		}

		@Override
		public K higherKey(K key) {
			return nm.higherKey(key);
		}

		@Override
		public Entry<K, V> firstEntry() {
			Entry<K, V> first = nm.firstEntry();
			return (null != first) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(first, valueType) : null;
		}

		@Override
		public Entry<K, V> lastEntry() {
			Entry<K, V> last = nm.lastEntry();
			return (null != last) ? new CheckedMap.CheckedEntrySet.CheckedEntry<>(last, valueType) : null;
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			Entry<K, V> entry = nm.pollFirstEntry();
			return (null == entry) ? null : new CheckedMap.CheckedEntrySet.CheckedEntry<>(entry, valueType);
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			Entry<K, V> entry = nm.pollLastEntry();
			return (null == entry) ? null : new CheckedMap.CheckedEntrySet.CheckedEntry<>(entry, valueType);
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			return checkedNavigableMap(nm.descendingMap(), keyType, valueType);
		}

		@Override
		public NavigableSet<K> keySet() {
			return navigableKeySet();
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			return checkedNavigableSet(nm.navigableKeySet(), keyType);
		}

		@Override
		public NavigableSet<K> descendingKeySet() {
			return checkedNavigableSet(nm.descendingKeySet(), keyType);
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, K toKey) {
			return checkedNavigableMap(nm.subMap(fromKey, true, toKey, false), keyType, valueType);
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey) {
			return checkedNavigableMap(nm.headMap(toKey, false), keyType, valueType);
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey) {
			return checkedNavigableMap(nm.tailMap(fromKey, true), keyType, valueType);
		}

		@Override
		public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
			return checkedNavigableMap(nm.subMap(fromKey, fromInclusive, toKey, toInclusive), keyType, valueType);
		}

		@Override
		public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
			return checkedNavigableMap(nm.headMap(toKey, inclusive), keyType, valueType);
		}

		@Override
		public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
			return checkedNavigableMap(nm.tailMap(fromKey, inclusive), keyType, valueType);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> emptyIterator() {
		return (Iterator<T>) EmptyIterator.EMPTY_ITERATOR;
	}

	private static class EmptyIterator<E> implements Iterator<E> {
		static final EmptyIterator<Object> EMPTY_ITERATOR = new EmptyIterator<>();

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public E next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new IllegalStateException();
		}

		@Override
		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ListIterator<T> emptyListIterator() {
		return (ListIterator<T>) EmptyListIterator.EMPTY_ITERATOR;
	}

	private static class EmptyListIterator<E> extends EmptyIterator<E> implements ListIterator<E> {
		static final EmptyListIterator<Object> EMPTY_ITERATOR = new EmptyListIterator<>();

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public E previous() {
			throw new NoSuchElementException();
		}

		@Override
		public int nextIndex() {
			return 0;
		}

		@Override
		public int previousIndex() {
			return -1;
		}

		@Override
		public void set(E e) {
			throw new IllegalStateException();
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Enumeration<T> emptyEnumeration() {
		return (Enumeration<T>) EmptyEnumeration.EMPTY_ENUMERATION;
	}

	private static class EmptyEnumeration<E> implements Enumeration<E> {
		static final EmptyEnumeration<Object> EMPTY_ENUMERATION = new EmptyEnumeration<>();

		@Override
		public boolean hasMoreElements() {
			return false;
		}

		@Override
		public E nextElement() {
			throw new NoSuchElementException();
		}
	}

	@SuppressWarnings("rawtypes")
	public static final Set EMPTY_SET = new EmptySet<>();

	@SuppressWarnings("unchecked")
	public static final <T> Set<T> emptySet() {
		return EMPTY_SET;
	}

	private static class EmptySet<E> extends AbstractSet<E> implements Serializable {
		private static final long serialVersionUID = 1582296315990362920L;

		@Override
		public Iterator<E> iterator() {
			return emptyIterator();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean contains(Object obj) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length > 0) {
				a[0] = null;
			}
			return a;
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			Objects.requireNonNull(action);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			Objects.requireNonNull(filter);
			return false;
		}

		@Override
		public Spliterator<E> spliterator() {
			return Spliterators.emptySpliterator();
		}

		private Object readResolve() {
			return EMPTY_SET;
		}
	}

	/**
	 * Returns an empty sorted set (immutable). This set is serializable.
	 *
	 * <p>
	 * This example illustrates the type-safe way to obtain an empty sorted set:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	SortedSet<String> s = Collections.emptySortedSet();
	 * }
	 * </pre>
	 *
	 * @implNote Implementations of this method need not create a separate
	 *           {@code SortedSet} object for each call.
	 *
	 * @param <E>
	 *            type of elements, if there were any, in the set
	 * @return the empty sorted set
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public static <E> SortedSet<E> emptySortedSet() {
		return (SortedSet<E>) UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
	}

	/**
	 * Returns an empty navigable set (immutable). This set is serializable.
	 *
	 * <p>
	 * This example illustrates the type-safe way to obtain an empty navigable
	 * set:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	NavigableSet<String> s = Collections.emptyNavigableSet();
	 * }
	 * </pre>
	 *
	 * @implNote Implementations of this method need not create a separate
	 *           {@code NavigableSet} object for each call.
	 *
	 * @param <E>
	 *            type of elements, if there were any, in the set
	 * @return the empty navigable set
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public static <E> NavigableSet<E> emptyNavigableSet() {
		return (NavigableSet<E>) UnmodifiableNavigableSet.EMPTY_NAVIGABLE_SET;
	}

	@SuppressWarnings("rawtypes")
	public static final List EMPTY_LIST = new EmptyList<>();

	@SuppressWarnings("unchecked")
	public static final <T> List<T> emptyList() {
		return EMPTY_LIST;
	}

	private static class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable {
		private static final long serialVersionUID = 8842843931221139166L;

		@Override
		public Iterator<E> iterator() {
			return emptyIterator();
		}

		@Override
		public ListIterator<E> listIterator() {
			return emptyListIterator();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean contains(Object obj) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length > 0) {
				a[0] = null;
			}
			return a;
		}

		@Override
		public E get(int index) {
			throw new IndexOutOfBoundsException("Index: " + index);
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof List) && ((List<?>) o).isEmpty();
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			Objects.requireNonNull(filter);
			return false;
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			Objects.requireNonNull(operator);
		}

		@Override
		public void sort(Comparator<? super E> c) {
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			Objects.requireNonNull(action);
		}

		@Override
		public Spliterator<E> spliterator() {
			return Spliterators.emptySpliterator();
		}

		private Object readResolve() {
			return EMPTY_LIST;
		}
	}

	@SuppressWarnings("rawtypes")
	public static final Map EMPTY_MAP = new EmptyMap<>();

	@SuppressWarnings("unchecked")
	public static final <K, V> Map<K, V> emptyMap() {
		return EMPTY_MAP;
	}

	/**
	 * Returns an empty sorted map (immutable). This map is serializable.
	 *
	 * <p>
	 * This example illustrates the type-safe way to obtain an empty map:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	SortedMap<String, Date> s = Collections.emptySortedMap();
	 * }
	 * </pre>
	 *
	 * @implNote Implementations of this method need not create a separate
	 *           {@code SortedMap} object for each call.
	 *
	 * @param <K>
	 *            the class of the map keys
	 * @param <V>
	 *            the class of the map values
	 * @return an empty sorted map
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public static final <K, V> SortedMap<K, V> emptySortedMap() {
		return (SortedMap<K, V>) UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
	}

	/**
	 * Returns an empty navigable map (immutable). This map is serializable.
	 *
	 * <p>
	 * This example illustrates the type-safe way to obtain an empty map:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	NavigableMap<String, Date> s = Collections.emptyNavigableMap();
	 * }
	 * </pre>
	 *
	 * @implNote Implementations of this method need not create a separate
	 *           {@code NavigableMap} object for each call.
	 *
	 * @param <K>
	 *            the class of the map keys
	 * @param <V>
	 *            the class of the map values
	 * @return an empty navigable map
	 * @since 1.8
	 */
	@SuppressWarnings("unchecked")
	public static final <K, V> NavigableMap<K, V> emptyNavigableMap() {
		return (NavigableMap<K, V>) UnmodifiableNavigableMap.EMPTY_NAVIGABLE_MAP;
	}

	private static class EmptyMap<K, V> extends AbstractMap<K, V> implements Serializable {
		private static final long serialVersionUID = 6428348081105594320L;

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean containsKey(Object key) {
			return false;
		}

		@Override
		public boolean containsValue(Object value) {
			return false;
		}

		@Override
		public V get(Object key) {
			return null;
		}

		@Override
		public Set<K> keySet() {
			return emptySet();
		}

		@Override
		public Collection<V> values() {
			return emptySet();
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			return emptySet();
		}

		@Override
		public boolean equals(Object o) {
			return (o instanceof Map) && ((Map<?, ?>) o).isEmpty();
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public V getOrDefault(Object k, V defaultValue) {
			return defaultValue;
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			Objects.requireNonNull(action);
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			Objects.requireNonNull(function);
		}

		@Override
		public V putIfAbsent(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V replace(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		private Object readResolve() {
			return EMPTY_MAP;
		}
	}

	public static <T> Set<T> singleton(T o) {
		return new SingletonSet<>(o);
	}

	static <E> Iterator<E> singletonIterator(final E e) {
		return new Iterator<E>() {
			private boolean hasNext = true;

			@Override
			public boolean hasNext() {
				return hasNext;
			}

			@Override
			public E next() {
				if (hasNext) {
					hasNext = false;
					return e;
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void forEachRemaining(Consumer<? super E> action) {
				Objects.requireNonNull(action);
				if (hasNext) {
					action.accept(e);
					hasNext = false;
				}
			}
		};
	}

	static <T> Spliterator<T> singletonSpliterator(final T element) {
		return new Spliterator<T>() {
			long est = 1;

			@Override
			public Spliterator<T> trySplit() {
				return null;
			}

			@Override
			public boolean tryAdvance(Consumer<? super T> consumer) {
				Objects.requireNonNull(consumer);
				if (est > 0) {
					est--;
					consumer.accept(element);
					return true;
				}
				return false;
			}

			@Override
			public void forEachRemaining(Consumer<? super T> consumer) {
				tryAdvance(consumer);
			}

			@Override
			public long estimateSize() {
				return est;
			}

			@Override
			public int characteristics() {
				int value = (element != null) ? Spliterator.NONNULL : 0;
				return value | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.ORDERED;
			}
		};
	}

	private static class SingletonSet<E> extends AbstractSet<E> implements Serializable {
		private static final long serialVersionUID = 3193687207550431679L;
		private final E element;

		SingletonSet(E e) {
			element = e;
		}

		@Override
		public Iterator<E> iterator() {
			return singletonIterator(element);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean contains(Object o) {
			return eq(o, element);
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			action.accept(element);
		}

		@Override
		public Spliterator<E> spliterator() {
			return singletonSpliterator(element);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			throw new UnsupportedOperationException();
		}
	}

	public static <T> List<T> singletonList(T o) {
		return new SingletonList<>(o);
	}

	private static class SingletonList<E> extends AbstractList<E> implements RandomAccess, Serializable {
		private static final long serialVersionUID = 3093736618740652951L;
		private final E element;

		SingletonList(E obj) {
			element = obj;
		}

		@Override
		public Iterator<E> iterator() {
			return singletonIterator(element);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean contains(Object obj) {
			return eq(obj, element);
		}

		@Override
		public E get(int index) {
			if (index != 0) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: 1");
			}
			return element;
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			action.accept(element);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(UnaryOperator<E> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void sort(Comparator<? super E> c) {
		}

		@Override
		public Spliterator<E> spliterator() {
			return singletonSpliterator(element);
		}
	}

	public static <K, V> Map<K, V> singletonMap(K key, V value) {
		return new SingletonMap<>(key, value);
	}

	private static class SingletonMap<K, V> extends AbstractMap<K, V> implements Serializable {
		private static final long serialVersionUID = -6979724477215052911L;
		private final K k;
		private final V v;

		SingletonMap(K key, V value) {
			k = key;
			v = value;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean containsKey(Object key) {
			return eq(key, k);
		}

		@Override
		public boolean containsValue(Object value) {
			return eq(value, v);
		}

		@Override
		public V get(Object key) {
			return (eq(key, k) ? v : null);
		}

		private transient Set<K> keySet;
		private transient Set<Map.Entry<K, V>> entrySet;
		private transient Collection<V> values;

		@Override
		public Set<K> keySet() {
			if (keySet == null) {
				keySet = singleton(k);
			}
			return keySet;
		}

		@Override
		public Set<Map.Entry<K, V>> entrySet() {
			if (entrySet == null) {
				entrySet = Collections.<Map.Entry<K, V>>singleton(new SimpleImmutableEntry<>(k, v));
			}
			return entrySet;
		}

		@Override
		public Collection<V> values() {
			if (values == null) {
				values = singleton(v);
			}
			return values;
		}

		@Override
		public V getOrDefault(Object key, V defaultValue) {
			return eq(key, k) ? v : defaultValue;
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			action.accept(k, v);
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V putIfAbsent(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V replace(K key, V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
	}

	public static <T> List<T> nCopies(int n, T o) {
		if (n < 0) {
			throw new IllegalArgumentException("List length = " + n);
		}
		return new CopiesList<>(n, o);
	}

	private static class CopiesList<E> extends AbstractList<E> implements RandomAccess, Serializable {
		private static final long serialVersionUID = 2739099268398711800L;
		final int n;
		final E element;

		CopiesList(int n, E e) {
			assert n >= 0;
			this.n = n;
			element = e;
		}

		@Override
		public int size() {
			return n;
		}

		@Override
		public boolean contains(Object obj) {
			return (n != 0) && eq(obj, element);
		}

		@Override
		public int indexOf(Object o) {
			return contains(o) ? 0 : -1;
		}

		@Override
		public int lastIndexOf(Object o) {
			return contains(o) ? n - 1 : -1;
		}

		@Override
		public E get(int index) {
			if ((index < 0) || (index >= n)) {
				throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + n);
			}
			return element;
		}

		@Override
		public Object[] toArray() {
			final Object[] a = new Object[n];
			if (element != null) {
				Arrays.fill(a, 0, n, element);
			}
			return a;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			final int n = this.n;
			if (a.length < n) {
				a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), n);
				if (element != null) {
					Arrays.fill(a, 0, n, element);
				}
			} else {
				Arrays.fill(a, 0, n, element);
				if (a.length > n) {
					a[n] = null;
				}
			}
			return a;
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			if (fromIndex < 0) {
				throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
			}
			if (toIndex > n) {
				throw new IndexOutOfBoundsException("toIndex = " + toIndex);
			}
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
			}
			return new CopiesList<>(toIndex - fromIndex, element);
		}

		@Override
		public Stream<E> stream() {
			return IntStream.range(0, n).mapToObj(i -> element);
		}

		@Override
		public Stream<E> parallelStream() {
			return IntStream.range(0, n).parallel().mapToObj(i -> element);
		}

		@Override
		public Spliterator<E> spliterator() {
			return stream().spliterator();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> reverseOrder() {
		return (Comparator<T>) ReverseComparator.REVERSE_ORDER;
	}

	private static class ReverseComparator implements Comparator<Comparable<Object>>, Serializable {
		private static final long serialVersionUID = 7207038068494060240L;
		static final ReverseComparator REVERSE_ORDER = new ReverseComparator();

		@Override
		public int compare(Comparable<Object> c1, Comparable<Object> c2) {
			return c2.compareTo(c1);
		}

		private Object readResolve() {
			return Collections.reverseOrder();
		}

		@Override
		public Comparator<Comparable<Object>> reversed() {
			return Comparator.naturalOrder();
		}
	}

	public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) {
		if (cmp == null) {
			return reverseOrder();
		}
		if (cmp instanceof ReverseComparator2) {
			return ((ReverseComparator2<T>) cmp).cmp;
		}
		return new ReverseComparator2<>(cmp);
	}

	private static class ReverseComparator2<T> implements Comparator<T>, Serializable {
		private static final long serialVersionUID = 4374092139857L;
		final Comparator<T> cmp;

		ReverseComparator2(Comparator<T> cmp) {
			assert cmp != null;
			this.cmp = cmp;
		}

		@Override
		public int compare(T t1, T t2) {
			return cmp.compare(t2, t1);
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || ((o instanceof ReverseComparator2) && cmp.equals(((ReverseComparator2) o).cmp));
		}

		@Override
		public int hashCode() {
			return cmp.hashCode() ^ Integer.MIN_VALUE;
		}

		@Override
		public Comparator<T> reversed() {
			return cmp;
		}
	}

	public static <T> Enumeration<T> enumeration(final Collection<T> c) {
		return new Enumeration<T>() {
			private final Iterator<T> i = c.iterator();

			@Override
			public boolean hasMoreElements() {
				return i.hasNext();
			}

			@Override
			public T nextElement() {
				return i.next();
			}
		};
	}

	public static <T> ArrayList<T> list(Enumeration<T> e) {
		ArrayList<T> l = new ArrayList<>();
		while (e.hasMoreElements()) {
			l.add(e.nextElement());
		}
		return l;
	}

	static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public static int frequency(Collection<?> c, Object o) {
		int result = 0;
		if (o == null) {
			for (Object e : c) {
				if (e == null) {
					result++;
				}
			}
		} else {
			for (Object e : c) {
				if (o.equals(e)) {
					result++;
				}
			}
		}
		return result;
	}

	public static boolean disjoint(Collection<?> c1, Collection<?> c2) {
		Collection<?> contains = c2;
		Collection<?> iterate = c1;
		if (c1 instanceof Set) {
			iterate = c2;
			contains = c1;
		} else if (!(c2 instanceof Set)) {
			int c1size = c1.size();
			int c2size = c2.size();
			if ((c1size == 0) || (c2size == 0)) {
				return true;
			}
			if (c1size > c2size) {
				iterate = c2;
				contains = c1;
			}
		}
		for (Object e : iterate) {
			if (contains.contains(e)) {
				return false;
			}
		}
		return true;
	}

	@SafeVarargs
	public static <T> boolean addAll(Collection<? super T> c, T... elements) {
		boolean result = false;
		for (T element : elements) {
			result |= c.add(element);
		}
		return result;
	}

	public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
		return new SetFromMap<>(map);
	}

	private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable {
		private final Map<E, Boolean> m;
		private transient Set<E> s;

		SetFromMap(Map<E, Boolean> map) {
			if (!map.isEmpty()) {
				throw new IllegalArgumentException("Map is non-empty");
			}
			m = map;
			s = map.keySet();
		}

		@Override
		public void clear() {
			m.clear();
		}

		@Override
		public int size() {
			return m.size();
		}

		@Override
		public boolean isEmpty() {
			return m.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return m.containsKey(o);
		}

		@Override
		public boolean remove(Object o) {
			return m.remove(o) != null;
		}

		@Override
		public boolean add(E e) {
			return m.put(e, Boolean.TRUE) == null;
		}

		@Override
		public Iterator<E> iterator() {
			return s.iterator();
		}

		@Override
		public Object[] toArray() {
			return s.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return s.toArray(a);
		}

		@Override
		public String toString() {
			return s.toString();
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return (o == this) || s.equals(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return s.containsAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return s.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return s.retainAll(c);
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			s.forEach(action);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			return s.removeIf(filter);
		}

		@Override
		public Spliterator<E> spliterator() {
			return s.spliterator();
		}

		@Override
		public Stream<E> stream() {
			return s.stream();
		}

		@Override
		public Stream<E> parallelStream() {
			return s.parallelStream();
		}

		private static final long serialVersionUID = 2454657854757543876L;

		private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
			stream.defaultReadObject();
			s = m.keySet();
		}
	}

	public static <T> Queue<T> asLifoQueue(Deque<T> deque) {
		return new AsLIFOQueue<>(deque);
	}

	static class AsLIFOQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
		private static final long serialVersionUID = 1802017725587941708L;
		private final Deque<E> q;

		AsLIFOQueue(Deque<E> q) {
			this.q = q;
		}

		@Override
		public boolean add(E e) {
			q.addFirst(e);
			return true;
		}

		@Override
		public boolean offer(E e) {
			return q.offerFirst(e);
		}

		@Override
		public E poll() {
			return q.pollFirst();
		}

		@Override
		public E remove() {
			return q.removeFirst();
		}

		@Override
		public E peek() {
			return q.peekFirst();
		}

		@Override
		public E element() {
			return q.getFirst();
		}

		@Override
		public void clear() {
			q.clear();
		}

		@Override
		public int size() {
			return q.size();
		}

		@Override
		public boolean isEmpty() {
			return q.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return q.contains(o);
		}

		@Override
		public boolean remove(Object o) {
			return q.remove(o);
		}

		@Override
		public Iterator<E> iterator() {
			return q.iterator();
		}

		@Override
		public Object[] toArray() {
			return q.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return q.toArray(a);
		}

		@Override
		public String toString() {
			return q.toString();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return q.containsAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return q.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return q.retainAll(c);
		}

		@Override
		public void forEach(Consumer<? super E> action) {
			q.forEach(action);
		}

		@Override
		public boolean removeIf(Predicate<? super E> filter) {
			return q.removeIf(filter);
		}

		@Override
		public Spliterator<E> spliterator() {
			return q.spliterator();
		}

		@Override
		public Stream<E> stream() {
			return q.stream();
		}

		@Override
		public Stream<E> parallelStream() {
			return q.parallelStream();
		}
	}
}
