package java.lang.reflect;

import java.security.AccessController;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import java.lang.annotation.Annotation;

public class AccessibleObject implements AnnotatedElement {
	static final private java.security.Permission ACCESS_PERMISSION = new ReflectPermission("suppressAccessChecks");

	public static void setAccessible(AccessibleObject[] array, boolean flag) throws SecurityException {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(ACCESS_PERMISSION);
		}
		for (AccessibleObject element : array) {
			setAccessible0(element, flag);
		}
	}

	public void setAccessible(boolean flag) throws SecurityException {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(ACCESS_PERMISSION);
		}
		setAccessible0(this, flag);
	}

	private static void setAccessible0(AccessibleObject obj, boolean flag) throws SecurityException {
		if ((obj instanceof Constructor) && (flag == true)) {
			Constructor<?> c = (Constructor<?>) obj;
			if (c.getDeclaringClass() == Class.class) {
				throw new SecurityException("Cannot make a java.lang.Class constructor accessible");
			}
		}
		obj.override = flag;
	}

	public boolean isAccessible() {
		return override;
	}

	protected AccessibleObject() {
	}

	boolean override;
	static final ReflectionFactory reflectionFactory = AccessController.doPrivileged(new sun.reflect.ReflectionFactory.GetReflectionFactoryAction());

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		throw new AssertionError("All subclasses should override this method");
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return AnnotatedElement.super.isAnnotationPresent(annotationClass);
	}

	/**
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @since 1.8
	 */
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		throw new AssertionError("All subclasses should override this method");
	}

	@Override
	public Annotation[] getAnnotations() {
		return getDeclaredAnnotations();
	}

	/**
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @since 1.8
	 */
	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotationClass);
	}

	/**
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @since 1.8
	 */
	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return getAnnotationsByType(annotationClass);
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		throw new AssertionError("All subclasses should override this method");
	}

	volatile Object securityCheckCache;

	void checkAccess(Class<?> caller, Class<?> clazz, Object obj, int modifiers) throws IllegalAccessException {
		if (caller == clazz) {
			return;
		}
		Object cache = securityCheckCache;
		Class<?> targetClass = clazz;
		if ((obj != null) && Modifier.isProtected(modifiers) && ((targetClass = obj.getClass()) != clazz)) {
			if (cache instanceof Class[]) {
				Class<?>[] cache2 = (Class<?>[]) cache;
				if ((cache2[1] == targetClass) && (cache2[0] == caller)) {
					return;
				}
			}
		} else if (cache == caller) {
			return;
		}
		slowCheckMemberAccess(caller, clazz, obj, modifiers, targetClass);
	}

	void slowCheckMemberAccess(Class<?> caller, Class<?> clazz, Object obj, int modifiers, Class<?> targetClass) throws IllegalAccessException {
		Reflection.ensureMemberAccess(caller, clazz, obj, modifiers);
		Object cache = ((targetClass == clazz) ? caller : new Class<?>[] { caller, targetClass });
		securityCheckCache = cache;
	}
}
