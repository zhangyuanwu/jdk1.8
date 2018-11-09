package java.security;

import sun.security.util.Debug;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

public final class AccessController {
	private AccessController() {
	}

	@CallerSensitive
	public static native <T> T doPrivileged(PrivilegedAction<T> action);

	@CallerSensitive
	public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action) {
		AccessControlContext acc = getStackAccessControlContext();
		if (acc == null) {
			return AccessController.doPrivileged(action);
		}
		DomainCombiner dc = acc.getAssignedCombiner();
		return AccessController.doPrivileged(action, preserveCombiner(dc, Reflection.getCallerClass()));
	}

	@CallerSensitive
	public static native <T> T doPrivileged(PrivilegedAction<T> action, AccessControlContext context);

	/**
	 * Performs the specified {@code PrivilegedAction} with privileges enabled
	 * and restricted by the specified {@code AccessControlContext} and with a
	 * privilege scope limited by specified {@code Permission} arguments.
	 *
	 * The action is performed with the intersection of the permissions
	 * possessed by the caller's protection domain, and those possessed by the
	 * domains represented by the specified {@code AccessControlContext}.
	 * <p>
	 * If the action's {@code run} method throws an (unchecked) exception, it
	 * will propagate through this method.
	 * <p>
	 * If a security manager is installed and the specified
	 * {@code AccessControlContext} was not created by system code and the
	 * caller's {@code ProtectionDomain} has not been granted the
	 * {@literal "createAccessControlContext"}
	 * {@link java.security.SecurityPermission}, then the action is performed
	 * with no permissions.
	 *
	 * @param <T>
	 *            the type of the value returned by the PrivilegedAction's
	 *            {@code run} method.
	 * @param action
	 *            the action to be performed.
	 * @param context
	 *            an <i>access control context</i> representing the restriction
	 *            to be applied to the caller's domain's privileges before
	 *            performing the specified action. If the context is
	 *            {@code null}, then no additional restriction is applied.
	 * @param perms
	 *            the {@code Permission} arguments which limit the scope of the
	 *            caller's privileges. The number of arguments is variable.
	 *
	 * @return the value returned by the action's {@code run} method.
	 *
	 * @throws NullPointerException
	 *             if action or perms or any element of perms is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
	 *
	 * @since 1.8
	 */
	@CallerSensitive
	public static <T> T doPrivileged(PrivilegedAction<T> action, AccessControlContext context, Permission... perms) {
		AccessControlContext parent = getContext();
		if (perms == null) {
			throw new NullPointerException("null permissions parameter");
		}
		Class<?> caller = Reflection.getCallerClass();
		return AccessController.doPrivileged(action, createWrapper(null, caller, parent, context, perms));
	}

	/**
	 * Performs the specified {@code PrivilegedAction} with privileges enabled
	 * and restricted by the specified {@code AccessControlContext} and with a
	 * privilege scope limited by specified {@code Permission} arguments.
	 *
	 * The action is performed with the intersection of the permissions
	 * possessed by the caller's protection domain, and those possessed by the
	 * domains represented by the specified {@code AccessControlContext}.
	 * <p>
	 * If the action's {@code run} method throws an (unchecked) exception, it
	 * will propagate through this method.
	 *
	 * <p>
	 * This method preserves the current AccessControlContext's DomainCombiner
	 * (which may be null) while the action is performed.
	 * <p>
	 * If a security manager is installed and the specified
	 * {@code AccessControlContext} was not created by system code and the
	 * caller's {@code ProtectionDomain} has not been granted the
	 * {@literal "createAccessControlContext"}
	 * {@link java.security.SecurityPermission}, then the action is performed
	 * with no permissions.
	 *
	 * @param <T>
	 *            the type of the value returned by the PrivilegedAction's
	 *            {@code run} method.
	 * @param action
	 *            the action to be performed.
	 * @param context
	 *            an <i>access control context</i> representing the restriction
	 *            to be applied to the caller's domain's privileges before
	 *            performing the specified action. If the context is
	 *            {@code null}, then no additional restriction is applied.
	 * @param perms
	 *            the {@code Permission} arguments which limit the scope of the
	 *            caller's privileges. The number of arguments is variable.
	 *
	 * @return the value returned by the action's {@code run} method.
	 *
	 * @throws NullPointerException
	 *             if action or perms or any element of perms is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
	 * @see java.security.DomainCombiner
	 *
	 * @since 1.8
	 */
	@CallerSensitive
	public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> action, AccessControlContext context, Permission... perms) {
		AccessControlContext parent = getContext();
		DomainCombiner dc = parent.getCombiner();
		if ((dc == null) && (context != null)) {
			dc = context.getCombiner();
		}
		if (perms == null) {
			throw new NullPointerException("null permissions parameter");
		}
		Class<?> caller = Reflection.getCallerClass();
		return AccessController.doPrivileged(action, createWrapper(dc, caller, parent, context, perms));
	}

	/**
	 * Performs the specified {@code PrivilegedExceptionAction} with privileges
	 * enabled. The action is performed with <i>all</i> of the permissions
	 * possessed by the caller's protection domain.
	 *
	 * <p>
	 * If the action's {@code run} method throws an <i>unchecked</i> exception,
	 * it will propagate through this method.
	 *
	 * <p>
	 * Note that any DomainCombiner associated with the current
	 * AccessControlContext will be ignored while the action is performed.
	 *
	 * @param <T>
	 *            the type of the value returned by the
	 *            PrivilegedExceptionAction's {@code run} method.
	 *
	 * @param action
	 *            the action to be performed
	 *
	 * @return the value returned by the action's {@code run} method
	 *
	 * @exception PrivilegedActionException
	 *                if the specified action's {@code run} method threw a
	 *                <i>checked</i> exception
	 * @exception NullPointerException
	 *                if the action is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
	 * @see #doPrivilegedWithCombiner(PrivilegedExceptionAction)
	 * @see java.security.DomainCombiner
	 */
	@CallerSensitive
	public static native <T> T doPrivileged(PrivilegedExceptionAction<T> action) throws PrivilegedActionException;

	/**
	 * Performs the specified {@code PrivilegedExceptionAction} with privileges
	 * enabled. The action is performed with <i>all</i> of the permissions
	 * possessed by the caller's protection domain.
	 *
	 * <p>
	 * If the action's {@code run} method throws an <i>unchecked</i> exception,
	 * it will propagate through this method.
	 *
	 * <p>
	 * This method preserves the current AccessControlContext's DomainCombiner
	 * (which may be null) while the action is performed.
	 *
	 * @param <T>
	 *            the type of the value returned by the
	 *            PrivilegedExceptionAction's {@code run} method.
	 *
	 * @param action
	 *            the action to be performed.
	 *
	 * @return the value returned by the action's {@code run} method
	 *
	 * @exception PrivilegedActionException
	 *                if the specified action's {@code run} method threw a
	 *                <i>checked</i> exception
	 * @exception NullPointerException
	 *                if the action is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedExceptionAction,AccessControlContext)
	 * @see java.security.DomainCombiner
	 *
	 * @since 1.6
	 */
	@CallerSensitive
	public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action) throws PrivilegedActionException {
		AccessControlContext acc = getStackAccessControlContext();
		if (acc == null) {
			return AccessController.doPrivileged(action);
		}
		DomainCombiner dc = acc.getAssignedCombiner();
		return AccessController.doPrivileged(action, preserveCombiner(dc, Reflection.getCallerClass()));
	}

	/**
	 * preserve the combiner across the doPrivileged call
	 */
	private static AccessControlContext preserveCombiner(DomainCombiner combiner, Class<?> caller) {
		return createWrapper(combiner, caller, null, null, null);
	}

	/**
	 * Create a wrapper to contain the limited privilege scope data.
	 */
	private static AccessControlContext createWrapper(DomainCombiner combiner, Class<?> caller, AccessControlContext parent, AccessControlContext context, Permission[] perms) {
		ProtectionDomain callerPD = getCallerPD(caller);
		// check if caller is authorized to create context
		if ((context != null) && !context.isAuthorized() && (System.getSecurityManager() != null) && !callerPD.impliesCreateAccessControlContext()) {
			ProtectionDomain nullPD = new ProtectionDomain(null, null);
			return new AccessControlContext(new ProtectionDomain[] { nullPD });
		} else {
			return new AccessControlContext(callerPD, combiner, parent, context, perms);
		}
	}

	private static ProtectionDomain getCallerPD(final Class<?> caller) {
		ProtectionDomain callerPd = doPrivileged((PrivilegedAction<ProtectionDomain>) () -> caller.getProtectionDomain());
		return callerPd;
	}

	/**
	 * Performs the specified {@code PrivilegedExceptionAction} with privileges
	 * enabled and restricted by the specified {@code AccessControlContext}. The
	 * action is performed with the intersection of the permissions possessed by
	 * the caller's protection domain, and those possessed by the domains
	 * represented by the specified {@code AccessControlContext}.
	 * <p>
	 * If the action's {@code run} method throws an <i>unchecked</i> exception,
	 * it will propagate through this method.
	 * <p>
	 * If a security manager is installed and the specified
	 * {@code AccessControlContext} was not created by system code and the
	 * caller's {@code ProtectionDomain} has not been granted the
	 * {@literal "createAccessControlContext"}
	 * {@link java.security.SecurityPermission}, then the action is performed
	 * with no permissions.
	 *
	 * @param <T>
	 *            the type of the value returned by the
	 *            PrivilegedExceptionAction's {@code run} method.
	 * @param action
	 *            the action to be performed
	 * @param context
	 *            an <i>access control context</i> representing the restriction
	 *            to be applied to the caller's domain's privileges before
	 *            performing the specified action. If the context is
	 *            {@code null}, then no additional restriction is applied.
	 *
	 * @return the value returned by the action's {@code run} method
	 *
	 * @exception PrivilegedActionException
	 *                if the specified action's {@code run} method threw a
	 *                <i>checked</i> exception
	 * @exception NullPointerException
	 *                if the action is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedAction,AccessControlContext)
	 */
	@CallerSensitive
	public static native <T> T doPrivileged(PrivilegedExceptionAction<T> action, AccessControlContext context) throws PrivilegedActionException;

	/**
	 * Performs the specified {@code PrivilegedExceptionAction} with privileges
	 * enabled and restricted by the specified {@code AccessControlContext} and
	 * with a privilege scope limited by specified {@code Permission} arguments.
	 *
	 * The action is performed with the intersection of the permissions
	 * possessed by the caller's protection domain, and those possessed by the
	 * domains represented by the specified {@code AccessControlContext}.
	 * <p>
	 * If the action's {@code run} method throws an (unchecked) exception, it
	 * will propagate through this method.
	 * <p>
	 * If a security manager is installed and the specified
	 * {@code AccessControlContext} was not created by system code and the
	 * caller's {@code ProtectionDomain} has not been granted the
	 * {@literal "createAccessControlContext"}
	 * {@link java.security.SecurityPermission}, then the action is performed
	 * with no permissions.
	 *
	 * @param <T>
	 *            the type of the value returned by the
	 *            PrivilegedExceptionAction's {@code run} method.
	 * @param action
	 *            the action to be performed.
	 * @param context
	 *            an <i>access control context</i> representing the restriction
	 *            to be applied to the caller's domain's privileges before
	 *            performing the specified action. If the context is
	 *            {@code null}, then no additional restriction is applied.
	 * @param perms
	 *            the {@code Permission} arguments which limit the scope of the
	 *            caller's privileges. The number of arguments is variable.
	 *
	 * @return the value returned by the action's {@code run} method.
	 *
	 * @throws PrivilegedActionException
	 *             if the specified action's {@code run} method threw a
	 *             <i>checked</i> exception
	 * @throws NullPointerException
	 *             if action or perms or any element of perms is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedAction,AccessControlContext)
	 *
	 * @since 1.8
	 */
	@CallerSensitive
	public static <T> T doPrivileged(PrivilegedExceptionAction<T> action, AccessControlContext context, Permission... perms) throws PrivilegedActionException {
		AccessControlContext parent = getContext();
		if (perms == null) {
			throw new NullPointerException("null permissions parameter");
		}
		Class<?> caller = Reflection.getCallerClass();
		return AccessController.doPrivileged(action, createWrapper(null, caller, parent, context, perms));
	}

	/**
	 * Performs the specified {@code PrivilegedExceptionAction} with privileges
	 * enabled and restricted by the specified {@code AccessControlContext} and
	 * with a privilege scope limited by specified {@code Permission} arguments.
	 *
	 * The action is performed with the intersection of the permissions
	 * possessed by the caller's protection domain, and those possessed by the
	 * domains represented by the specified {@code AccessControlContext}.
	 * <p>
	 * If the action's {@code run} method throws an (unchecked) exception, it
	 * will propagate through this method.
	 *
	 * <p>
	 * This method preserves the current AccessControlContext's DomainCombiner
	 * (which may be null) while the action is performed.
	 * <p>
	 * If a security manager is installed and the specified
	 * {@code AccessControlContext} was not created by system code and the
	 * caller's {@code ProtectionDomain} has not been granted the
	 * {@literal "createAccessControlContext"}
	 * {@link java.security.SecurityPermission}, then the action is performed
	 * with no permissions.
	 *
	 * @param <T>
	 *            the type of the value returned by the
	 *            PrivilegedExceptionAction's {@code run} method.
	 * @param action
	 *            the action to be performed.
	 * @param context
	 *            an <i>access control context</i> representing the restriction
	 *            to be applied to the caller's domain's privileges before
	 *            performing the specified action. If the context is
	 *            {@code null}, then no additional restriction is applied.
	 * @param perms
	 *            the {@code Permission} arguments which limit the scope of the
	 *            caller's privileges. The number of arguments is variable.
	 *
	 * @return the value returned by the action's {@code run} method.
	 *
	 * @throws PrivilegedActionException
	 *             if the specified action's {@code run} method threw a
	 *             <i>checked</i> exception
	 * @throws NullPointerException
	 *             if action or perms or any element of perms is {@code null}
	 *
	 * @see #doPrivileged(PrivilegedAction)
	 * @see #doPrivileged(PrivilegedAction,AccessControlContext)
	 * @see java.security.DomainCombiner
	 *
	 * @since 1.8
	 */
	@CallerSensitive
	public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> action, AccessControlContext context, Permission... perms) throws PrivilegedActionException {
		AccessControlContext parent = getContext();
		DomainCombiner dc = parent.getCombiner();
		if ((dc == null) && (context != null)) {
			dc = context.getCombiner();
		}
		if (perms == null) {
			throw new NullPointerException("null permissions parameter");
		}
		Class<?> caller = Reflection.getCallerClass();
		return AccessController.doPrivileged(action, createWrapper(dc, caller, parent, context, perms));
	}

	private static native AccessControlContext getStackAccessControlContext();

	static native AccessControlContext getInheritedAccessControlContext();

	public static AccessControlContext getContext() {
		AccessControlContext acc = getStackAccessControlContext();
		if (acc == null) {
			return new AccessControlContext(null, true);
		} else {
			return acc.optimize();
		}
	}

	public static void checkPermission(Permission perm) throws AccessControlException {
		if (perm == null) {
			throw new NullPointerException("permission can't be null");
		}
		AccessControlContext stack = getStackAccessControlContext();
		if (stack == null) {
			Debug debug = AccessControlContext.getDebug();
			boolean dumpDebug = false;
			if (debug != null) {
				dumpDebug = !Debug.isOn("codebase=");
				dumpDebug &= !Debug.isOn("permission=") || Debug.isOn("permission=" + perm.getClass().getCanonicalName());
			}
			if (dumpDebug && Debug.isOn("stack")) {
				Thread.dumpStack();
			}
			if (dumpDebug && Debug.isOn("domain")) {
				debug.println("domain (context is null)");
			}
			if (dumpDebug) {
				debug.println("access allowed " + perm);
			}
			return;
		}
		AccessControlContext acc = stack.optimize();
		acc.checkPermission(perm);
	}
}
