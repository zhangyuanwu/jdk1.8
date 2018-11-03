package java.util.concurrent.atomic;

import java.util.function.LongUnaryOperator;
import java.io.Serializable;
import java.util.function.LongBinaryOperator;
import sun.misc.Unsafe;

public class AtomicLong extends Number implements Serializable {
	private static final long serialVersionUID = 1927816293512124184L;
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
	static final boolean VM_SUPPORTS_LONG_CAS = VMSupportsCS8();

	private static native boolean VMSupportsCS8();

	static {
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicLong.class.getDeclaredField("value"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
	private volatile long value;

	public AtomicLong(long initialValue) {
		value = initialValue;
	}

	public AtomicLong() {
	}

	public final long get() {
		return value;
	}

	public final void set(long newValue) {
		value = newValue;
	}

	public final void lazySet(long newValue) {
		unsafe.putOrderedLong(this, valueOffset, newValue);
	}

	public final long getAndSet(long newValue) {
		return unsafe.getAndSetLong(this, valueOffset, newValue);
	}

	public final boolean compareAndSet(long expect, long update) {
		return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
	}

	public final boolean weakCompareAndSet(long expect, long update) {
		return unsafe.compareAndSwapLong(this, valueOffset, expect, update);
	}

	public final long getAndIncrement() {
		return unsafe.getAndAddLong(this, valueOffset, 1L);
	}

	public final long getAndDecrement() {
		return unsafe.getAndAddLong(this, valueOffset, -1L);
	}

	public final long getAndAdd(long delta) {
		return unsafe.getAndAddLong(this, valueOffset, delta);
	}

	public final long incrementAndGet() {
		return unsafe.getAndAddLong(this, valueOffset, 1L) + 1L;
	}

	public final long decrementAndGet() {
		return unsafe.getAndAddLong(this, valueOffset, -1L) - 1L;
	}

	public final long addAndGet(long delta) {
		return unsafe.getAndAddLong(this, valueOffset, delta) + delta;
	}

	/**
	 * Atomically updates the current value with the results of applying the
	 * given function, returning the previous value. The function should be
	 * side-effect-free, since it may be re-applied when attempted updates fail
	 * due to contention among threads.
	 *
	 * @param updateFunction
	 *            a side-effect-free function
	 * @return the previous value
	 * @since 1.8
	 */
	public final long getAndUpdate(LongUnaryOperator updateFunction) {
		long prev, next;
		do {
			prev = get();
			next = updateFunction.applyAsLong(prev);
		} while (!compareAndSet(prev, next));
		return prev;
	}

	/**
	 * Atomically updates the current value with the results of applying the
	 * given function, returning the updated value. The function should be
	 * side-effect-free, since it may be re-applied when attempted updates fail
	 * due to contention among threads.
	 *
	 * @param updateFunction
	 *            a side-effect-free function
	 * @return the updated value
	 * @since 1.8
	 */
	public final long updateAndGet(LongUnaryOperator updateFunction) {
		long prev, next;
		do {
			prev = get();
			next = updateFunction.applyAsLong(prev);
		} while (!compareAndSet(prev, next));
		return next;
	}

	/**
	 * Atomically updates the current value with the results of applying the
	 * given function to the current and given values, returning the previous
	 * value. The function should be side-effect-free, since it may be
	 * re-applied when attempted updates fail due to contention among threads.
	 * The function is applied with the current value as its first argument, and
	 * the given update as the second argument.
	 *
	 * @param x
	 *            the update value
	 * @param accumulatorFunction
	 *            a side-effect-free function of two arguments
	 * @return the previous value
	 * @since 1.8
	 */
	public final long getAndAccumulate(long x, LongBinaryOperator accumulatorFunction) {
		long prev, next;
		do {
			prev = get();
			next = accumulatorFunction.applyAsLong(prev, x);
		} while (!compareAndSet(prev, next));
		return prev;
	}

	/**
	 * Atomically updates the current value with the results of applying the
	 * given function to the current and given values, returning the updated
	 * value. The function should be side-effect-free, since it may be
	 * re-applied when attempted updates fail due to contention among threads.
	 * The function is applied with the current value as its first argument, and
	 * the given update as the second argument.
	 *
	 * @param x
	 *            the update value
	 * @param accumulatorFunction
	 *            a side-effect-free function of two arguments
	 * @return the updated value
	 * @since 1.8
	 */
	public final long accumulateAndGet(long x, LongBinaryOperator accumulatorFunction) {
		long prev, next;
		do {
			prev = get();
			next = accumulatorFunction.applyAsLong(prev, x);
		} while (!compareAndSet(prev, next));
		return next;
	}

	@Override
	public String toString() {
		return Long.toString(get());
	}

	@Override
	public int intValue() {
		return (int) get();
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
