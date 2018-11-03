package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public abstract class AtomicLongFieldUpdater<T> {
	@CallerSensitive
	public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> tclass, String fieldName) {
		Class<?> caller = Reflection.getCallerClass();
		if (AtomicLong.VM_SUPPORTS_LONG_CAS) {
			return new CASUpdater<>(tclass, fieldName, caller);
		} else {
			return new LockedUpdater<>(tclass, fieldName, caller);
		}
	}

	protected AtomicLongFieldUpdater() {
	}

	public abstract boolean compareAndSet(T obj, long expect, long update);

	public abstract boolean weakCompareAndSet(T obj, long expect, long update);

	public abstract void set(T obj, long newValue);

	public abstract void lazySet(T obj, long newValue);

	public abstract long get(T obj);

	public long getAndSet(T obj, long newValue) {
		long prev;
		do {
			prev = get(obj);
		} while (!compareAndSet(obj, prev, newValue));
		return prev;
	}

	public long getAndIncrement(T obj) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev + 1;
		} while (!compareAndSet(obj, prev, next));
		return prev;
	}

	/**
	 * Atomically decrements by one the current value of the field of the given
	 * object managed by this updater.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @return the previous value
	 */
	public long getAndDecrement(T obj) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev - 1;
		} while (!compareAndSet(obj, prev, next));
		return prev;
	}

	/**
	 * Atomically adds the given value to the current value of the field of the
	 * given object managed by this updater.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param delta
	 *            the value to add
	 * @return the previous value
	 */
	public long getAndAdd(T obj, long delta) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev + delta;
		} while (!compareAndSet(obj, prev, next));
		return prev;
	}

	/**
	 * Atomically increments by one the current value of the field of the given
	 * object managed by this updater.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @return the updated value
	 */
	public long incrementAndGet(T obj) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev + 1;
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	/**
	 * Atomically decrements by one the current value of the field of the given
	 * object managed by this updater.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @return the updated value
	 */
	public long decrementAndGet(T obj) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev - 1;
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	/**
	 * Atomically adds the given value to the current value of the field of the
	 * given object managed by this updater.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param delta
	 *            the value to add
	 * @return the updated value
	 */
	public long addAndGet(T obj, long delta) {
		long prev, next;
		do {
			prev = get(obj);
			next = prev + delta;
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	/**
	 * Atomically updates the field of the given object managed by this updater
	 * with the results of applying the given function, returning the previous
	 * value. The function should be side-effect-free, since it may be
	 * re-applied when attempted updates fail due to contention among threads.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param updateFunction
	 *            a side-effect-free function
	 * @return the previous value
	 * @since 1.8
	 */
	public final long getAndUpdate(T obj, LongUnaryOperator updateFunction) {
		long prev, next;
		do {
			prev = get(obj);
			next = updateFunction.applyAsLong(prev);
		} while (!compareAndSet(obj, prev, next));
		return prev;
	}

	/**
	 * Atomically updates the field of the given object managed by this updater
	 * with the results of applying the given function, returning the updated
	 * value. The function should be side-effect-free, since it may be
	 * re-applied when attempted updates fail due to contention among threads.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param updateFunction
	 *            a side-effect-free function
	 * @return the updated value
	 * @since 1.8
	 */
	public final long updateAndGet(T obj, LongUnaryOperator updateFunction) {
		long prev, next;
		do {
			prev = get(obj);
			next = updateFunction.applyAsLong(prev);
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	/**
	 * Atomically updates the field of the given object managed by this updater
	 * with the results of applying the given function to the current and given
	 * values, returning the previous value. The function should be
	 * side-effect-free, since it may be re-applied when attempted updates fail
	 * due to contention among threads. The function is applied with the current
	 * value as its first argument, and the given update as the second argument.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param x
	 *            the update value
	 * @param accumulatorFunction
	 *            a side-effect-free function of two arguments
	 * @return the previous value
	 * @since 1.8
	 */
	public final long getAndAccumulate(T obj, long x, LongBinaryOperator accumulatorFunction) {
		long prev, next;
		do {
			prev = get(obj);
			next = accumulatorFunction.applyAsLong(prev, x);
		} while (!compareAndSet(obj, prev, next));
		return prev;
	}

	/**
	 * Atomically updates the field of the given object managed by this updater
	 * with the results of applying the given function to the current and given
	 * values, returning the updated value. The function should be
	 * side-effect-free, since it may be re-applied when attempted updates fail
	 * due to contention among threads. The function is applied with the current
	 * value as its first argument, and the given update as the second argument.
	 *
	 * @param obj
	 *            An object whose field to get and set
	 * @param x
	 *            the update value
	 * @param accumulatorFunction
	 *            a side-effect-free function of two arguments
	 * @return the updated value
	 * @since 1.8
	 */
	public final long accumulateAndGet(T obj, long x, LongBinaryOperator accumulatorFunction) {
		long prev, next;
		do {
			prev = get(obj);
			next = accumulatorFunction.applyAsLong(prev, x);
		} while (!compareAndSet(obj, prev, next));
		return next;
	}

	private static final class CASUpdater<T> extends AtomicLongFieldUpdater<T> {
		private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();
		private final long offset;
		/**
		 * if field is protected, the subclass constructing updater, else the
		 * same as tclass
		 */
		private final Class<?> cclass;
		/** class holding the field */
		private final Class<T> tclass;

		CASUpdater(final Class<T> tclass, final String fieldName, final Class<?> caller) {
			final Field field;
			final int modifiers;
			try {
				field = AccessController.doPrivileged((PrivilegedExceptionAction<Field>) () -> tclass.getDeclaredField(fieldName));
				modifiers = field.getModifiers();
				sun.reflect.misc.ReflectUtil.ensureMemberAccess(caller, tclass, null, modifiers);
				ClassLoader cl = tclass.getClassLoader();
				ClassLoader ccl = caller.getClassLoader();
				if ((ccl != null) && (ccl != cl) && ((cl == null) || !isAncestor(cl, ccl))) {
					sun.reflect.misc.ReflectUtil.checkPackageAccess(tclass);
				}
			} catch (PrivilegedActionException pae) {
				throw new RuntimeException(pae.getException());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			if (field.getType() != long.class) {
				throw new IllegalArgumentException("Must be long type");
			}
			if (!Modifier.isVolatile(modifiers)) {
				throw new IllegalArgumentException("Must be volatile type");
			}
			// Access to protected field members is restricted to receivers only
			// of the accessing class, or one of its subclasses, and the
			// accessing class must in turn be a subclass (or package sibling)
			// of the protected member's defining class.
			// If the updater refers to a protected field of a declaring class
			// outside the current package, the receiver argument will be
			// narrowed to the type of the accessing class.
			this.cclass = (Modifier.isProtected(modifiers) && tclass.isAssignableFrom(caller) && !isSamePackage(tclass, caller)) ? caller : tclass;
			this.tclass = tclass;
			this.offset = U.objectFieldOffset(field);
		}

		/**
		 * Checks that target argument is instance of cclass. On failure, throws
		 * cause.
		 */
		private final void accessCheck(T obj) {
			if (!cclass.isInstance(obj)) {
				throwAccessCheckException(obj);
			}
		}

		/**
		 * Throws access exception if accessCheck failed due to protected
		 * access, else ClassCastException.
		 */
		private final void throwAccessCheckException(T obj) {
			if (cclass == tclass) {
				throw new ClassCastException();
			} else {
				throw new RuntimeException(new IllegalAccessException("Class " + cclass.getName() + " can not access a protected member of class " + tclass.getName() + " using an instance of " + obj.getClass().getName()));
			}
		}

		@Override
		public final boolean compareAndSet(T obj, long expect, long update) {
			accessCheck(obj);
			return U.compareAndSwapLong(obj, offset, expect, update);
		}

		@Override
		public final boolean weakCompareAndSet(T obj, long expect, long update) {
			accessCheck(obj);
			return U.compareAndSwapLong(obj, offset, expect, update);
		}

		@Override
		public final void set(T obj, long newValue) {
			accessCheck(obj);
			U.putLongVolatile(obj, offset, newValue);
		}

		@Override
		public final void lazySet(T obj, long newValue) {
			accessCheck(obj);
			U.putOrderedLong(obj, offset, newValue);
		}

		@Override
		public final long get(T obj) {
			accessCheck(obj);
			return U.getLongVolatile(obj, offset);
		}

		@Override
		public final long getAndSet(T obj, long newValue) {
			accessCheck(obj);
			return U.getAndSetLong(obj, offset, newValue);
		}

		@Override
		public final long getAndAdd(T obj, long delta) {
			accessCheck(obj);
			return U.getAndAddLong(obj, offset, delta);
		}

		@Override
		public final long getAndIncrement(T obj) {
			return getAndAdd(obj, 1);
		}

		@Override
		public final long getAndDecrement(T obj) {
			return getAndAdd(obj, -1);
		}

		@Override
		public final long incrementAndGet(T obj) {
			return getAndAdd(obj, 1) + 1;
		}

		@Override
		public final long decrementAndGet(T obj) {
			return getAndAdd(obj, -1) - 1;
		}

		@Override
		public final long addAndGet(T obj, long delta) {
			return getAndAdd(obj, delta) + delta;
		}
	}

	private static final class LockedUpdater<T> extends AtomicLongFieldUpdater<T> {
		private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();
		private final long offset;
		/**
		 * if field is protected, the subclass constructing updater, else the
		 * same as tclass
		 */
		private final Class<?> cclass;
		/** class holding the field */
		private final Class<T> tclass;

		LockedUpdater(final Class<T> tclass, final String fieldName, final Class<?> caller) {
			Field field = null;
			int modifiers = 0;
			try {
				field = AccessController.doPrivileged((PrivilegedExceptionAction<Field>) () -> tclass.getDeclaredField(fieldName));
				modifiers = field.getModifiers();
				sun.reflect.misc.ReflectUtil.ensureMemberAccess(caller, tclass, null, modifiers);
				ClassLoader cl = tclass.getClassLoader();
				ClassLoader ccl = caller.getClassLoader();
				if ((ccl != null) && (ccl != cl) && ((cl == null) || !isAncestor(cl, ccl))) {
					sun.reflect.misc.ReflectUtil.checkPackageAccess(tclass);
				}
			} catch (PrivilegedActionException pae) {
				throw new RuntimeException(pae.getException());
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			if (field.getType() != long.class) {
				throw new IllegalArgumentException("Must be long type");
			}
			if (!Modifier.isVolatile(modifiers)) {
				throw new IllegalArgumentException("Must be volatile type");
			}
			// Access to protected field members is restricted to receivers only
			// of the accessing class, or one of its subclasses, and the
			// accessing class must in turn be a subclass (or package sibling)
			// of the protected member's defining class.
			// If the updater refers to a protected field of a declaring class
			// outside the current package, the receiver argument will be
			// narrowed to the type of the accessing class.
			this.cclass = (Modifier.isProtected(modifiers) && tclass.isAssignableFrom(caller) && !isSamePackage(tclass, caller)) ? caller : tclass;
			this.tclass = tclass;
			this.offset = U.objectFieldOffset(field);
		}

		/**
		 * Checks that target argument is instance of cclass. On failure, throws
		 * cause.
		 */
		private final void accessCheck(T obj) {
			if (!cclass.isInstance(obj)) {
				throw accessCheckException(obj);
			}
		}

		/**
		 * Returns access exception if accessCheck failed due to protected
		 * access, else ClassCastException.
		 */
		private final RuntimeException accessCheckException(T obj) {
			if (cclass == tclass) {
				return new ClassCastException();
			} else {
				return new RuntimeException(new IllegalAccessException("Class " + cclass.getName() + " can not access a protected member of class " + tclass.getName() + " using an instance of " + obj.getClass().getName()));
			}
		}

		@Override
		public final boolean compareAndSet(T obj, long expect, long update) {
			accessCheck(obj);
			synchronized (this) {
				long v = U.getLong(obj, offset);
				if (v != expect) {
					return false;
				}
				U.putLong(obj, offset, update);
				return true;
			}
		}

		@Override
		public final boolean weakCompareAndSet(T obj, long expect, long update) {
			return compareAndSet(obj, expect, update);
		}

		@Override
		public final void set(T obj, long newValue) {
			accessCheck(obj);
			synchronized (this) {
				U.putLong(obj, offset, newValue);
			}
		}

		@Override
		public final void lazySet(T obj, long newValue) {
			set(obj, newValue);
		}

		@Override
		public final long get(T obj) {
			accessCheck(obj);
			synchronized (this) {
				return U.getLong(obj, offset);
			}
		}
	}

	static boolean isAncestor(ClassLoader first, ClassLoader second) {
		ClassLoader acl = first;
		do {
			acl = acl.getParent();
			if (second == acl) {
				return true;
			}
		} while (acl != null);
		return false;
	}

	private static boolean isSamePackage(Class<?> class1, Class<?> class2) {
		return (class1.getClassLoader() == class2.getClassLoader()) && Objects.equals(getPackageName(class1), getPackageName(class2));
	}

	private static String getPackageName(Class<?> cls) {
		String cn = cls.getName();
		int dot = cn.lastIndexOf('.');
		return (dot != -1) ? cn.substring(0, dot) : "";
	}
}
