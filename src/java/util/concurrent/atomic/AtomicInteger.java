package java.util.concurrent.atomic;

import java.util.function.IntUnaryOperator;
import java.io.Serializable;
import java.util.function.IntBinaryOperator;
import sun.misc.Unsafe;

public class AtomicInteger extends Number implements Serializable {
	private static final long serialVersionUID = 6214790243416807050L;
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
	static {
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	private volatile int value;

	public AtomicInteger(int initialValue) {
		value = initialValue;
	}

	public AtomicInteger() {
	}

	public final int get() {
		return value;
	}

	public final void set(int newValue) {
		value = newValue;
	}

	public final void lazySet(int newValue) {
		unsafe.putOrderedInt(this, valueOffset, newValue);
	}

	public final int getAndSet(int newValue) {
		return unsafe.getAndSetInt(this, valueOffset, newValue);
	}

	public final boolean compareAndSet(int expect, int update) {
		return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
	}

	public final boolean weakCompareAndSet(int expect, int update) {
		return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
	}

	public final int getAndIncrement() {
		return unsafe.getAndAddInt(this, valueOffset, 1);
	}

	public final int getAndDecrement() {
		return unsafe.getAndAddInt(this, valueOffset, -1);
	}

	public final int getAndAdd(int delta) {
		return unsafe.getAndAddInt(this, valueOffset, delta);
	}

	public final int incrementAndGet() {
		return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
	}

	public final int decrementAndGet() {
		return unsafe.getAndAddInt(this, valueOffset, -1) - 1;
	}

	public final int addAndGet(int delta) {
		return unsafe.getAndAddInt(this, valueOffset, delta) + delta;
	}

	/**
	 * 原子地使用应用给定函数的结果更新当前值,返回先前的值. 该函数应该是无副作用的,因为当尝试的更新由于线程之间的争用而失败时,它可能会被重新应用.
	 *
	 * @param updateFunction
	 *            无副作用的功能
	 * @return 以前的值
	 * @since 1.8
	 */
	public final int getAndUpdate(IntUnaryOperator updateFunction) {
		int prev, next;
		do {
			prev = get();
			next = updateFunction.applyAsInt(prev);
		} while (!compareAndSet(prev, next));
		return prev;
	}

	/**
	 * 原子地使用应用给定函数的结果更新当前值,返回更新的值. 该函数应该是无副作用的,因为当尝试的更新由于线程之间的争用而失败时,它可能会被重新应用.
	 *
	 * @param updateFunction
	 *            无副作用的功能
	 * @return 更新的值
	 * @since 1.8
	 */
	public final int updateAndGet(IntUnaryOperator updateFunction) {
		int prev, next;
		do {
			prev = get();
			next = updateFunction.applyAsInt(prev);
		} while (!compareAndSet(prev, next));
		return next;
	}

	/**
	 * 原子地使用将给定函数应用于当前值和给定值的结果更新当前值,返回先前的值.
	 * 该函数应该是无副作用的,因为当尝试的更新由于线程之间的争用而失败时,它可能会被重新应用.
	 * 该函数应用当前值作为其第一个参数,并将给定更新作为第二个参数.
	 *
	 * @param x
	 *            更新值
	 * @param accumulatorFunction
	 *            两个参数的无副作用函数
	 * @return 以前的值
	 * @since 1.8
	 */
	public final int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
		int prev, next;
		do {
			prev = get();
			next = accumulatorFunction.applyAsInt(prev, x);
		} while (!compareAndSet(prev, next));
		return prev;
	}

	/**
	 * 以原始方式更新当前值,并将给定函数应用于当前值和给定值,并返回更新后的值.
	 * 该函数应该是无副作用的,因为当尝试的更新由于线程之间的争用而失败时,它可能会被重新应用.
	 * 该函数应用当前值作为其第一个参数,并将给定更新作为第二个参数.
	 *
	 * @param x
	 *            更新值
	 * @param accumulatorFunction
	 *            两个参数的无副作用函数
	 * @return 更新的值
	 * @since 1.8
	 */
	public final int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
		int prev, next;
		do {
			prev = get();
			next = accumulatorFunction.applyAsInt(prev, x);
		} while (!compareAndSet(prev, next));
		return next;
	}

	@Override
	public String toString() {
		return Integer.toString(get());
	}

	@Override
	public int intValue() {
		return get();
	}

	@Override
	public long longValue() {
		return get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return get();
	}
}
