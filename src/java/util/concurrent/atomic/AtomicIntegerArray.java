package java.util.concurrent.atomic;

import java.util.function.IntUnaryOperator;
import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import sun.misc.Unsafe;

public class AtomicIntegerArray implements Serializable {
	private static final long serialVersionUID = 2862133569453604235L;
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final int base = unsafe.arrayBaseOffset(int[].class);
	private static final int shift;
	private final int[] array;
	static {
		int scale = unsafe.arrayIndexScale(int[].class);
		if ((scale & (scale - 1)) != 0) {
			throw new Error("data type scale not a power of two");
		}
		shift = 31 - Integer.numberOfLeadingZeros(scale);
	}

	private long checkedByteOffset(int i) {
		if ((i < 0) || (i >= array.length)) {
			throw new IndexOutOfBoundsException("index " + i);
		}
		return byteOffset(i);
	}

	private static long byteOffset(int i) {
		return ((long) i << shift) + base;
	}

	public AtomicIntegerArray(int length) {
		array = new int[length];
	}

	public AtomicIntegerArray(int[] array) {
		this.array = array.clone();
	}

	public final int length() {
		return array.length;
	}

	public final int get(int i) {
		return getRaw(checkedByteOffset(i));
	}

	private int getRaw(long offset) {
		return unsafe.getIntVolatile(array, offset);
	}

	public final void set(int i, int newValue) {
		unsafe.putIntVolatile(array, checkedByteOffset(i), newValue);
	}

	public final void lazySet(int i, int newValue) {
		unsafe.putOrderedInt(array, checkedByteOffset(i), newValue);
	}

	public final int getAndSet(int i, int newValue) {
		return unsafe.getAndSetInt(array, checkedByteOffset(i), newValue);
	}

	public final boolean compareAndSet(int i, int expect, int update) {
		return compareAndSetRaw(checkedByteOffset(i), expect, update);
	}

	private boolean compareAndSetRaw(long offset, int expect, int update) {
		return unsafe.compareAndSwapInt(array, offset, expect, update);
	}

	public final boolean weakCompareAndSet(int i, int expect, int update) {
		return compareAndSet(i, expect, update);
	}

	public final int getAndIncrement(int i) {
		return getAndAdd(i, 1);
	}

	public final int getAndDecrement(int i) {
		return getAndAdd(i, -1);
	}

	public final int getAndAdd(int i, int delta) {
		return unsafe.getAndAddInt(array, checkedByteOffset(i), delta);
	}

	public final int incrementAndGet(int i) {
		return getAndAdd(i, 1) + 1;
	}

	public final int decrementAndGet(int i) {
		return getAndAdd(i, -1) - 1;
	}

	public final int addAndGet(int i, int delta) {
		return getAndAdd(i, delta) + delta;
	}

	/**
	 * 使用应用给定函数的结果以索引{@code i}原子更新元素，返回先前的值。
	 * 该函数应该是无副作用的，因为当尝试的更新由于线程之间的争用而失败时，它可能会被重新应用。
	 *
	 * @param i
	 *            指数
	 * @param updateFunction
	 *            无副作用的功能
	 * @return 以前的值
	 * @since 1.8
	 */
	public final int getAndUpdate(int i, IntUnaryOperator updateFunction) {
		long offset = checkedByteOffset(i);
		int prev, next;
		do {
			prev = getRaw(offset);
			next = updateFunction.applyAsInt(prev);
		} while (!compareAndSetRaw(offset, prev, next));
		return prev;
	}

	/**
	 * 使用应用给定函数的结果以索引{@code i}原子更新元素，返回更新的值。
	 * 该函数应该是无副作用的，因为当尝试的更新由于线程之间的争用而失败时，它可能会被重新应用。
	 *
	 * @param i
	 *            指数
	 * @param updateFunction
	 *            无副作用的功能
	 * @return 更新的值
	 * @since 1.8
	 */
	public final int updateAndGet(int i, IntUnaryOperator updateFunction) {
		long offset = checkedByteOffset(i);
		int prev, next;
		do {
			prev = getRaw(offset);
			next = updateFunction.applyAsInt(prev);
		} while (!compareAndSetRaw(offset, prev, next));
		return next;
	}

	/**
	 * 以原子方式更新索引{@code i}处的元素，并将给定函数应用于当前值和给定值，并返回先前的值。
	 * 该函数应该是无副作用的，因为当尝试的更新由于线程之间的争用而失败时，它可能会被重新应用。
	 * 该函数应用索引{@code i}的当前值作为其第一个参数，并将给定的更新作为第二个参数。
	 *
	 * @param i
	 *            指数
	 * @param x
	 *            更新值
	 * @param accumulatorFunction
	 *            两个参数的无副作用函数
	 * @return 以前的值
	 * @since 1.8
	 */
	public final int getAndAccumulate(int i, int x, IntBinaryOperator accumulatorFunction) {
		long offset = checkedByteOffset(i);
		int prev, next;
		do {
			prev = getRaw(offset);
			next = accumulatorFunction.applyAsInt(prev, x);
		} while (!compareAndSetRaw(offset, prev, next));
		return prev;
	}

	/**
	 * 以原子方式更新索引{@code i}处的元素，并将给定函数应用于当前值和给定值，并返回更新后的值。
	 * 该函数应该是无副作用的，因为当尝试的更新由于线程之间的争用而失败时，它可能会被重新应用。
	 * 该函数应用索引{@code i}的当前值作为其第一个参数，并将给定的更新作为第二个参数。
	 *
	 * @param i
	 *            指数
	 * @param x
	 *            更新值
	 * @param accumulatorFunction
	 *            两个参数的无副作用函数
	 * @return 更新的值
	 * @since 1.8
	 */
	public final int accumulateAndGet(int i, int x, IntBinaryOperator accumulatorFunction) {
		long offset = checkedByteOffset(i);
		int prev, next;
		do {
			prev = getRaw(offset);
			next = accumulatorFunction.applyAsInt(prev, x);
		} while (!compareAndSetRaw(offset, prev, next));
		return next;
	}

	@Override
	public String toString() {
		int iMax = array.length - 1;
		if (iMax == -1) {
			return "[]";
		}
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0;; i++) {
			b.append(getRaw(byteOffset(i)));
			if (i == iMax) {
				return b.append(']').toString();
			}
			b.append(',').append(' ');
		}
	}
}
