package java.awt;

import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LightweightPeer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import sun.util.logging.PlatformLogger;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.CausedFocusEvent;
import sun.awt.KeyboardFocusManagerPeerProvider;
import sun.awt.AWTAccessor;

public abstract class KeyboardFocusManager implements KeyEventDispatcher, KeyEventPostProcessor {
	private static final PlatformLogger focusLog = PlatformLogger.getLogger("java.awt.focus.KeyboardFocusManager");
	static {
		Toolkit.loadLibraries();
		if (!GraphicsEnvironment.isHeadless()) {
			initIDs();
		}
		AWTAccessor.setKeyboardFocusManagerAccessor(new AWTAccessor.KeyboardFocusManagerAccessor() {
			public int shouldNativelyFocusHeavyweight(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
				return KeyboardFocusManager.shouldNativelyFocusHeavyweight(heavyweight, descendant, temporary, focusedWindowChangeAllowed, time, cause);
			}

			public boolean processSynchronousLightweightTransfer(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time) {
				return KeyboardFocusManager.processSynchronousLightweightTransfer(heavyweight, descendant, temporary, focusedWindowChangeAllowed, time);
			}

			public void removeLastFocusRequest(Component heavyweight) {
				KeyboardFocusManager.removeLastFocusRequest(heavyweight);
			}

			public void setMostRecentFocusOwner(Window window, Component component) {
				KeyboardFocusManager.setMostRecentFocusOwner(window, component);
			}

			public KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext ctx) {
				return KeyboardFocusManager.getCurrentKeyboardFocusManager(ctx);
			}

			public Container getCurrentFocusCycleRoot() {
				return KeyboardFocusManager.currentFocusCycleRoot;
			}
		});
	}
	transient KeyboardFocusManagerPeer peer;

	private static native void initIDs();

	private static final PlatformLogger log = PlatformLogger.getLogger("java.awt.KeyboardFocusManager");
	public static final int FORWARD_TRAVERSAL_KEYS = 0;
	public static final int BACKWARD_TRAVERSAL_KEYS = 1;
	public static final int UP_CYCLE_TRAVERSAL_KEYS = 2;
	public static final int DOWN_CYCLE_TRAVERSAL_KEYS = 3;
	static final int TRAVERSAL_KEY_LENGTH = DOWN_CYCLE_TRAVERSAL_KEYS + 1;

	/**
	 * Returns the current KeyboardFocusManager instance for the calling
	 * thread's context.
	 *
	 * @return this thread's context's KeyboardFocusManager
	 * @see #setCurrentKeyboardFocusManager
	 */
	public static KeyboardFocusManager getCurrentKeyboardFocusManager() {
		return getCurrentKeyboardFocusManager(AppContext.getAppContext());
	}

	synchronized static KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext appcontext) {
		KeyboardFocusManager manager = (KeyboardFocusManager) appcontext.get(KeyboardFocusManager.class);
		if (manager == null) {
			manager = new DefaultKeyboardFocusManager();
			appcontext.put(KeyboardFocusManager.class, manager);
		}
		return manager;
	}

	/**
	 * Sets the current KeyboardFocusManager instance for the calling thread's
	 * context. If null is specified, then the current KeyboardFocusManager is
	 * replaced with a new instance of DefaultKeyboardFocusManager.
	 * <p>
	 * If a SecurityManager is installed, the calling thread must be granted the
	 * AWTPermission "replaceKeyboardFocusManager" in order to replace the the
	 * current KeyboardFocusManager. If this permission is not granted, this
	 * method will throw a SecurityException, and the current
	 * KeyboardFocusManager will be unchanged.
	 *
	 * @param newManager
	 *            the new KeyboardFocusManager for this thread's context
	 * @see #getCurrentKeyboardFocusManager
	 * @see DefaultKeyboardFocusManager
	 * @throws SecurityException
	 *             if the calling thread does not have permission to replace the
	 *             current KeyboardFocusManager
	 */
	public static void setCurrentKeyboardFocusManager(KeyboardFocusManager newManager) throws SecurityException {
		checkReplaceKFMPermission();
		KeyboardFocusManager oldManager = null;
		synchronized (KeyboardFocusManager.class) {
			AppContext appcontext = AppContext.getAppContext();
			if (newManager != null) {
				oldManager = getCurrentKeyboardFocusManager(appcontext);
				appcontext.put(KeyboardFocusManager.class, newManager);
			} else {
				oldManager = getCurrentKeyboardFocusManager(appcontext);
				appcontext.remove(KeyboardFocusManager.class);
			}
		}
		if (oldManager != null) {
			oldManager.firePropertyChange("managingFocus", Boolean.TRUE, Boolean.FALSE);
		}
		if (newManager != null) {
			newManager.firePropertyChange("managingFocus", Boolean.FALSE, Boolean.TRUE);
		}
	}

	/**
	 * The Component in an application that will typically receive all KeyEvents
	 * generated by the user.
	 */
	private static Component focusOwner;
	/**
	 * The Component in an application that will regain focus when an
	 * outstanding temporary focus transfer has completed, or the focus owner,
	 * if no outstanding temporary transfer exists.
	 */
	private static Component permanentFocusOwner;
	/**
	 * The Window which is, or contains, the focus owner.
	 */
	private static Window focusedWindow;
	/**
	 * Only a Frame or a Dialog can be the active Window. The native windowing
	 * system may denote the active Window with a special decoration, such as a
	 * highlighted title bar. The active Window is always either the focused
	 * Window, or the first Frame or Dialog which is an owner of the focused
	 * Window.
	 */
	private static Window activeWindow;
	/**
	 * The default FocusTraversalPolicy for all Windows that have no policy of
	 * their own set. If those Windows have focus-cycle-root children that have
	 * no keyboard-traversal policy of their own, then those children will also
	 * inherit this policy (as will, recursively, their focus-cycle-root
	 * children).
	 */
	private FocusTraversalPolicy defaultPolicy = new DefaultFocusTraversalPolicy();
	/**
	 * The bound property names of each focus traversal key.
	 */
	private static final String[] defaultFocusTraversalKeyPropertyNames = { "forwardDefaultFocusTraversalKeys", "backwardDefaultFocusTraversalKeys", "upCycleDefaultFocusTraversalKeys", "downCycleDefaultFocusTraversalKeys" };
	/**
	 * The default strokes for initializing the default focus traversal keys.
	 */
	private static final AWTKeyStroke[][] defaultFocusTraversalKeyStrokes = { { AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0, false), AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK, false), }, { AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK, false), AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK, false), }, {}, {}, };
	/**
	 * The default focus traversal keys. Each array of traversal keys will be in
	 * effect on all Windows that have no such array of their own explicitly
	 * set. Each array will also be inherited, recursively, by any child
	 * Component of those Windows that has no such array of its own explicitly
	 * set.
	 */
	private Set<AWTKeyStroke>[] defaultFocusTraversalKeys = new Set[4];
	/**
	 * The current focus cycle root. If the focus owner is itself a focus cycle
	 * root, then it may be ambiguous as to which Components represent the next
	 * and previous Components to focus during normal focus traversal. In that
	 * case, the current focus cycle root is used to differentiate among the
	 * possibilities.
	 */
	private static Container currentFocusCycleRoot;
	/**
	 * A description of any VetoableChangeListeners which have been registered.
	 */
	private VetoableChangeSupport vetoableSupport;
	/**
	 * A description of any PropertyChangeListeners which have been registered.
	 */
	private PropertyChangeSupport changeSupport;
	/**
	 * This KeyboardFocusManager's KeyEventDispatcher chain. The List does not
	 * include this KeyboardFocusManager unless it was explicitly re-registered
	 * via a call to <code>addKeyEventDispatcher</code>. If no other
	 * KeyEventDispatchers are registered, this field may be null or refer to a
	 * List of length 0.
	 */
	private java.util.LinkedList<KeyEventDispatcher> keyEventDispatchers;
	/**
	 * This KeyboardFocusManager's KeyEventPostProcessor chain. The List does
	 * not include this KeyboardFocusManager unless it was explicitly
	 * re-registered via a call to <code>addKeyEventPostProcessor</code>. If no
	 * other KeyEventPostProcessors are registered, this field may be null or
	 * refer to a List of length 0.
	 */
	private java.util.LinkedList<KeyEventPostProcessor> keyEventPostProcessors;
	/**
	 * Maps Windows to those Windows' most recent focus owners.
	 */
	private static java.util.Map<Window, WeakReference<Component>> mostRecentFocusOwners = new WeakHashMap<>();
	/**
	 * We cache the permission used to verify that the calling thread is
	 * permitted to access the global focus state.
	 */
	private static AWTPermission replaceKeyboardFocusManagerPermission;
	/*
	 * SequencedEvent which is currently dispatched in AppContext.
	 */
	transient SequencedEvent currentSequencedEvent = null;

	final void setCurrentSequencedEvent(SequencedEvent current) {
		synchronized (SequencedEvent.class) {
			assert ((current == null) || (currentSequencedEvent == null));
			currentSequencedEvent = current;
		}
	}

	final SequencedEvent getCurrentSequencedEvent() {
		synchronized (SequencedEvent.class) {
			return currentSequencedEvent;
		}
	}

	static Set<AWTKeyStroke> initFocusTraversalKeysSet(String value, Set<AWTKeyStroke> targetSet) {
		StringTokenizer tokens = new StringTokenizer(value, ",");
		while (tokens.hasMoreTokens()) {
			targetSet.add(AWTKeyStroke.getAWTKeyStroke(tokens.nextToken()));
		}
		return (targetSet.isEmpty()) ? Collections.EMPTY_SET : Collections.unmodifiableSet(targetSet);
	}

	/**
	 * Initializes a KeyboardFocusManager.
	 */
	public KeyboardFocusManager() {
		for (int i = 0; i < TRAVERSAL_KEY_LENGTH; i++) {
			Set<AWTKeyStroke> work_set = new HashSet<>();
			for (int j = 0; j < defaultFocusTraversalKeyStrokes[i].length; j++) {
				work_set.add(defaultFocusTraversalKeyStrokes[i][j]);
			}
			defaultFocusTraversalKeys[i] = (work_set.isEmpty()) ? Collections.EMPTY_SET : Collections.unmodifiableSet(work_set);
		}
		initPeer();
	}

	private void initPeer() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		KeyboardFocusManagerPeerProvider peerProvider = (KeyboardFocusManagerPeerProvider) tk;
		peer = peerProvider.getKeyboardFocusManagerPeer();
	}

	/**
	 * Returns the focus owner, if the focus owner is in the same context as the
	 * calling thread. The focus owner is defined as the Component in an
	 * application that will typically receive all KeyEvents generated by the
	 * user. KeyEvents which map to the focus owner's focus traversal keys will
	 * not be delivered if focus traversal keys are enabled for the focus owner.
	 * In addition, KeyEventDispatchers may retarget or consume KeyEvents before
	 * they reach the focus owner.
	 *
	 * @return the focus owner, or null if the focus owner is not a member of
	 *         the calling thread's context
	 * @see #getGlobalFocusOwner
	 * @see #setGlobalFocusOwner
	 */
	public Component getFocusOwner() {
		synchronized (KeyboardFocusManager.class) {
			if (focusOwner == null) {
				return null;
			}
			return (focusOwner.appContext == AppContext.getAppContext()) ? focusOwner : null;
		}
	}

	/**
	 * Returns the focus owner, even if the calling thread is in a different
	 * context than the focus owner. The focus owner is defined as the Component
	 * in an application that will typically receive all KeyEvents generated by
	 * the user. KeyEvents which map to the focus owner's focus traversal keys
	 * will not be delivered if focus traversal keys are enabled for the focus
	 * owner. In addition, KeyEventDispatchers may retarget or consume KeyEvents
	 * before they reach the focus owner.
	 * <p>
	 * This method will throw a SecurityException if this KeyboardFocusManager
	 * is not the current KeyboardFocusManager for the calling thread's context.
	 *
	 * @return the focus owner
	 * @see #getFocusOwner
	 * @see #setGlobalFocusOwner
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	protected Component getGlobalFocusOwner() throws SecurityException {
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			return focusOwner;
		}
	}

	/**
	 * Sets the focus owner. The operation will be cancelled if the Component is
	 * not focusable. The focus owner is defined as the Component in an
	 * application that will typically receive all KeyEvents generated by the
	 * user. KeyEvents which map to the focus owner's focus traversal keys will
	 * not be delivered if focus traversal keys are enabled for the focus owner.
	 * In addition, KeyEventDispatchers may retarget or consume KeyEvents before
	 * they reach the focus owner.
	 * <p>
	 * This method does not actually set the focus to the specified Component.
	 * It merely stores the value to be subsequently returned by
	 * <code>getFocusOwner()</code>. Use <code>Component.requestFocus()</code>
	 * or <code>Component.requestFocusInWindow()</code> to change the focus
	 * owner, subject to platform limitations.
	 *
	 * @param focusOwner
	 *            the focus owner
	 * @see #getFocusOwner
	 * @see #getGlobalFocusOwner
	 * @see Component#requestFocus()
	 * @see Component#requestFocusInWindow()
	 * @see Component#isFocusable
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 * @beaninfo bound: true
	 */
	protected void setGlobalFocusOwner(Component focusOwner) throws SecurityException {
		Component oldFocusOwner = null;
		boolean shouldFire = false;
		if ((focusOwner == null) || focusOwner.isFocusable()) {
			synchronized (KeyboardFocusManager.class) {
				checkKFMSecurity();
				oldFocusOwner = getFocusOwner();
				try {
					fireVetoableChange("focusOwner", oldFocusOwner, focusOwner);
				} catch (PropertyVetoException e) {
					// rejected
					return;
				}
				KeyboardFocusManager.focusOwner = focusOwner;
				if ((focusOwner != null) && ((getCurrentFocusCycleRoot() == null) || !focusOwner.isFocusCycleRoot(getCurrentFocusCycleRoot()))) {
					Container rootAncestor = focusOwner.getFocusCycleRootAncestor();
					if ((rootAncestor == null) && (focusOwner instanceof Window)) {
						rootAncestor = (Container) focusOwner;
					}
					if (rootAncestor != null) {
						setGlobalCurrentFocusCycleRootPriv(rootAncestor);
					}
				}
				shouldFire = true;
			}
		}
		if (shouldFire) {
			firePropertyChange("focusOwner", oldFocusOwner, focusOwner);
		}
	}

	/**
	 * Clears the focus owner at both the Java and native levels if the focus
	 * owner exists and resides in the same context as the calling thread,
	 * otherwise the method returns silently.
	 * <p>
	 * The focus owner component will receive a permanent FOCUS_LOST event.
	 * After this operation completes, the native windowing system will discard
	 * all user-generated KeyEvents until the user selects a new Component to
	 * receive focus, or a Component is given focus explicitly via a call to
	 * {@code requestFocus()}. This operation does not change the focused or
	 * active Windows.
	 *
	 * @see Component#requestFocus()
	 * @see java.awt.event.FocusEvent#FOCUS_LOST
	 * @since 1.8
	 */
	public void clearFocusOwner() {
		if (getFocusOwner() != null) {
			clearGlobalFocusOwner();
		}
	}

	/**
	 * Clears the global focus owner at both the Java and native levels. If
	 * there exists a focus owner, that Component will receive a permanent
	 * FOCUS_LOST event. After this operation completes, the native windowing
	 * system will discard all user-generated KeyEvents until the user selects a
	 * new Component to receive focus, or a Component is given focus explicitly
	 * via a call to <code>requestFocus()</code>. This operation does not change
	 * the focused or active Windows.
	 * <p>
	 * If a SecurityManager is installed, the calling thread must be granted the
	 * "replaceKeyboardFocusManager" AWTPermission. If this permission is not
	 * granted, this method will throw a SecurityException, and the current
	 * focus owner will not be cleared.
	 * <p>
	 * This method is intended to be used only by KeyboardFocusManager set as
	 * current KeyboardFocusManager for the calling thread's context. It is not
	 * for general client use.
	 *
	 * @see KeyboardFocusManager#clearFocusOwner
	 * @see Component#requestFocus()
	 * @see java.awt.event.FocusEvent#FOCUS_LOST
	 * @throws SecurityException
	 *             if the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	public void clearGlobalFocusOwner() throws SecurityException {
		checkReplaceKFMPermission();
		if (!GraphicsEnvironment.isHeadless()) {
			// Toolkit must be fully initialized, otherwise
			// _clearGlobalFocusOwner will crash or throw an exception
			Toolkit.getDefaultToolkit();
			_clearGlobalFocusOwner();
		}
	}

	private void _clearGlobalFocusOwner() {
		Window activeWindow = markClearGlobalFocusOwner();
		peer.clearGlobalFocusOwner(activeWindow);
	}

	void clearGlobalFocusOwnerPriv() {
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			clearGlobalFocusOwner();
			return null;
		});
	}

	Component getNativeFocusOwner() {
		return peer.getCurrentFocusOwner();
	}

	void setNativeFocusOwner(Component comp) {
		if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
			focusLog.finest("Calling peer {0} setCurrentFocusOwner for {1}", String.valueOf(peer), String.valueOf(comp));
		}
		peer.setCurrentFocusOwner(comp);
	}

	Window getNativeFocusedWindow() {
		return peer.getCurrentFocusedWindow();
	}

	/**
	 * Returns the permanent focus owner, if the permanent focus owner is in the
	 * same context as the calling thread. The permanent focus owner is defined
	 * as the last Component in an application to receive a permanent
	 * FOCUS_GAINED event. The focus owner and permanent focus owner are
	 * equivalent unless a temporary focus change is currently in effect. In
	 * such a situation, the permanent focus owner will again be the focus owner
	 * when the temporary focus change ends.
	 *
	 * @return the permanent focus owner, or null if the permanent focus owner
	 *         is not a member of the calling thread's context
	 * @see #getGlobalPermanentFocusOwner
	 * @see #setGlobalPermanentFocusOwner
	 */
	public Component getPermanentFocusOwner() {
		synchronized (KeyboardFocusManager.class) {
			if (permanentFocusOwner == null) {
				return null;
			}
			return (permanentFocusOwner.appContext == AppContext.getAppContext()) ? permanentFocusOwner : null;
		}
	}

	/**
	 * Returns the permanent focus owner, even if the calling thread is in a
	 * different context than the permanent focus owner. The permanent focus
	 * owner is defined as the last Component in an application to receive a
	 * permanent FOCUS_GAINED event. The focus owner and permanent focus owner
	 * are equivalent unless a temporary focus change is currently in effect. In
	 * such a situation, the permanent focus owner will again be the focus owner
	 * when the temporary focus change ends.
	 *
	 * @return the permanent focus owner
	 * @see #getPermanentFocusOwner
	 * @see #setGlobalPermanentFocusOwner
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	protected Component getGlobalPermanentFocusOwner() throws SecurityException {
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			return permanentFocusOwner;
		}
	}

	/**
	 * Sets the permanent focus owner. The operation will be cancelled if the
	 * Component is not focusable. The permanent focus owner is defined as the
	 * last Component in an application to receive a permanent FOCUS_GAINED
	 * event. The focus owner and permanent focus owner are equivalent unless a
	 * temporary focus change is currently in effect. In such a situation, the
	 * permanent focus owner will again be the focus owner when the temporary
	 * focus change ends.
	 * <p>
	 * This method does not actually set the focus to the specified Component.
	 * It merely stores the value to be subsequently returned by
	 * <code>getPermanentFocusOwner()</code>. Use
	 * <code>Component.requestFocus()</code> or
	 * <code>Component.requestFocusInWindow()</code> to change the focus owner,
	 * subject to platform limitations.
	 *
	 * @param permanentFocusOwner
	 *            the permanent focus owner
	 * @see #getPermanentFocusOwner
	 * @see #getGlobalPermanentFocusOwner
	 * @see Component#requestFocus()
	 * @see Component#requestFocusInWindow()
	 * @see Component#isFocusable
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 * @beaninfo bound: true
	 */
	protected void setGlobalPermanentFocusOwner(Component permanentFocusOwner) throws SecurityException {
		Component oldPermanentFocusOwner = null;
		boolean shouldFire = false;
		if ((permanentFocusOwner == null) || permanentFocusOwner.isFocusable()) {
			synchronized (KeyboardFocusManager.class) {
				checkKFMSecurity();
				oldPermanentFocusOwner = getPermanentFocusOwner();
				try {
					fireVetoableChange("permanentFocusOwner", oldPermanentFocusOwner, permanentFocusOwner);
				} catch (PropertyVetoException e) {
					// rejected
					return;
				}
				KeyboardFocusManager.permanentFocusOwner = permanentFocusOwner;
				KeyboardFocusManager.setMostRecentFocusOwner(permanentFocusOwner);
				shouldFire = true;
			}
		}
		if (shouldFire) {
			firePropertyChange("permanentFocusOwner", oldPermanentFocusOwner, permanentFocusOwner);
		}
	}

	/**
	 * Returns the focused Window, if the focused Window is in the same context
	 * as the calling thread. The focused Window is the Window that is or
	 * contains the focus owner.
	 *
	 * @return the focused Window, or null if the focused Window is not a member
	 *         of the calling thread's context
	 * @see #getGlobalFocusedWindow
	 * @see #setGlobalFocusedWindow
	 */
	public Window getFocusedWindow() {
		synchronized (KeyboardFocusManager.class) {
			if (focusedWindow == null) {
				return null;
			}
			return (focusedWindow.appContext == AppContext.getAppContext()) ? focusedWindow : null;
		}
	}

	/**
	 * Returns the focused Window, even if the calling thread is in a different
	 * context than the focused Window. The focused Window is the Window that is
	 * or contains the focus owner.
	 *
	 * @return the focused Window
	 * @see #getFocusedWindow
	 * @see #setGlobalFocusedWindow
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	protected Window getGlobalFocusedWindow() throws SecurityException {
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			return focusedWindow;
		}
	}

	/**
	 * Sets the focused Window. The focused Window is the Window that is or
	 * contains the focus owner. The operation will be cancelled if the
	 * specified Window to focus is not a focusable Window.
	 * <p>
	 * This method does not actually change the focused Window as far as the
	 * native windowing system is concerned. It merely stores the value to be
	 * subsequently returned by <code>getFocusedWindow()</code>. Use
	 * <code>Component.requestFocus()</code> or
	 * <code>Component.requestFocusInWindow()</code> to change the focused
	 * Window, subject to platform limitations.
	 *
	 * @param focusedWindow
	 *            the focused Window
	 * @see #getFocusedWindow
	 * @see #getGlobalFocusedWindow
	 * @see Component#requestFocus()
	 * @see Component#requestFocusInWindow()
	 * @see Window#isFocusableWindow
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 * @beaninfo bound: true
	 */
	protected void setGlobalFocusedWindow(Window focusedWindow) throws SecurityException {
		Window oldFocusedWindow = null;
		boolean shouldFire = false;
		if ((focusedWindow == null) || focusedWindow.isFocusableWindow()) {
			synchronized (KeyboardFocusManager.class) {
				checkKFMSecurity();
				oldFocusedWindow = getFocusedWindow();
				try {
					fireVetoableChange("focusedWindow", oldFocusedWindow, focusedWindow);
				} catch (PropertyVetoException e) {
					// rejected
					return;
				}
				KeyboardFocusManager.focusedWindow = focusedWindow;
				shouldFire = true;
			}
		}
		if (shouldFire) {
			firePropertyChange("focusedWindow", oldFocusedWindow, focusedWindow);
		}
	}

	/**
	 * Returns the active Window, if the active Window is in the same context as
	 * the calling thread. Only a Frame or a Dialog can be the active Window.
	 * The native windowing system may denote the active Window or its children
	 * with special decorations, such as a highlighted title bar. The active
	 * Window is always either the focused Window, or the first Frame or Dialog
	 * that is an owner of the focused Window.
	 *
	 * @return the active Window, or null if the active Window is not a member
	 *         of the calling thread's context
	 * @see #getGlobalActiveWindow
	 * @see #setGlobalActiveWindow
	 */
	public Window getActiveWindow() {
		synchronized (KeyboardFocusManager.class) {
			if (activeWindow == null) {
				return null;
			}
			return (activeWindow.appContext == AppContext.getAppContext()) ? activeWindow : null;
		}
	}

	/**
	 * Returns the active Window, even if the calling thread is in a different
	 * context than the active Window. Only a Frame or a Dialog can be the
	 * active Window. The native windowing system may denote the active Window
	 * or its children with special decorations, such as a highlighted title
	 * bar. The active Window is always either the focused Window, or the first
	 * Frame or Dialog that is an owner of the focused Window.
	 *
	 * @return the active Window
	 * @see #getActiveWindow
	 * @see #setGlobalActiveWindow
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	protected Window getGlobalActiveWindow() throws SecurityException {
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			return activeWindow;
		}
	}

	/**
	 * Sets the active Window. Only a Frame or a Dialog can be the active
	 * Window. The native windowing system may denote the active Window or its
	 * children with special decorations, such as a highlighted title bar. The
	 * active Window is always either the focused Window, or the first Frame or
	 * Dialog that is an owner of the focused Window.
	 * <p>
	 * This method does not actually change the active Window as far as the
	 * native windowing system is concerned. It merely stores the value to be
	 * subsequently returned by <code>getActiveWindow()</code>. Use
	 * <code>Component.requestFocus()</code> or
	 * <code>Component.requestFocusInWindow()</code>to change the active Window,
	 * subject to platform limitations.
	 *
	 * @param activeWindow
	 *            the active Window
	 * @see #getActiveWindow
	 * @see #getGlobalActiveWindow
	 * @see Component#requestFocus()
	 * @see Component#requestFocusInWindow()
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 * @beaninfo bound: true
	 */
	protected void setGlobalActiveWindow(Window activeWindow) throws SecurityException {
		Window oldActiveWindow;
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			oldActiveWindow = getActiveWindow();
			if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
				focusLog.finer("Setting global active window to " + activeWindow + ", old active " + oldActiveWindow);
			}
			try {
				fireVetoableChange("activeWindow", oldActiveWindow, activeWindow);
			} catch (PropertyVetoException e) {
				// rejected
				return;
			}
			KeyboardFocusManager.activeWindow = activeWindow;
		}
		firePropertyChange("activeWindow", oldActiveWindow, activeWindow);
	}

	/**
	 * Returns the default FocusTraversalPolicy. Top-level components use this
	 * value on their creation to initialize their own focus traversal policy by
	 * explicit call to Container.setFocusTraversalPolicy.
	 *
	 * @return the default FocusTraversalPolicy. null will never be returned.
	 * @see #setDefaultFocusTraversalPolicy
	 * @see Container#setFocusTraversalPolicy
	 * @see Container#getFocusTraversalPolicy
	 */
	public synchronized FocusTraversalPolicy getDefaultFocusTraversalPolicy() {
		return defaultPolicy;
	}

	/**
	 * Sets the default FocusTraversalPolicy. Top-level components use this
	 * value on their creation to initialize their own focus traversal policy by
	 * explicit call to Container.setFocusTraversalPolicy. Note: this call
	 * doesn't affect already created components as they have their policy
	 * initialized. Only new components will use this policy as their default
	 * policy.
	 *
	 * @param defaultPolicy
	 *            the new, default FocusTraversalPolicy
	 * @see #getDefaultFocusTraversalPolicy
	 * @see Container#setFocusTraversalPolicy
	 * @see Container#getFocusTraversalPolicy
	 * @throws IllegalArgumentException
	 *             if defaultPolicy is null
	 * @beaninfo bound: true
	 */
	public void setDefaultFocusTraversalPolicy(FocusTraversalPolicy defaultPolicy) {
		if (defaultPolicy == null) {
			throw new IllegalArgumentException("default focus traversal policy cannot be null");
		}
		FocusTraversalPolicy oldPolicy;
		synchronized (this) {
			oldPolicy = this.defaultPolicy;
			this.defaultPolicy = defaultPolicy;
		}
		firePropertyChange("defaultFocusTraversalPolicy", oldPolicy, defaultPolicy);
	}

	/**
	 * Sets the default focus traversal keys for a given traversal operation.
	 * This traversal key {@code Set} will be in effect on all {@code Window}s
	 * that have no such {@code Set} of their own explicitly defined. This
	 * {@code Set} will also be inherited, recursively, by any child
	 * {@code Component} of those {@code Windows} that has no such {@code Set}
	 * of its own explicitly defined.
	 * <p>
	 * The default values for the default focus traversal keys are
	 * implementation-dependent. Sun recommends that all implementations for a
	 * particular native platform use the same default values. The
	 * recommendations for Windows and Unix are listed below. These
	 * recommendations are used in the Sun AWT implementations.
	 *
	 * <table border=1 summary="Recommended default values for focus traversal
	 * keys">
	 * <tr>
	 * <th>Identifier</th>
	 * <th>Meaning</th>
	 * <th>Default</th>
	 * </tr>
	 * <tr>
	 * <td>{@code KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS}</td>
	 * <td>Normal forward keyboard traversal</td>
	 * <td>{@code TAB} on {@code KEY_PRESSED}, {@code CTRL-TAB} on
	 * {@code KEY_PRESSED}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS}</td>
	 * <td>Normal reverse keyboard traversal</td>
	 * <td>{@code SHIFT-TAB} on {@code KEY_PRESSED}, {@code CTRL-SHIFT-TAB} on
	 * {@code KEY_PRESSED}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS}</td>
	 * <td>Go up one focus traversal cycle</td>
	 * <td>none</td>
	 * </tr>
	 * <tr>
	 * <td>{@code KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS}</td>
	 * <td>Go down one focus traversal cycle</td>
	 * <td>none</td>
	 * </tr>
	 * </table>
	 *
	 * To disable a traversal key, use an empty {@code Set};
	 * {@code Collections.EMPTY_SET} is recommended.
	 * <p>
	 * Using the {@code AWTKeyStroke} API, client code can specify on which of
	 * two specific {@code KeyEvent}s, {@code KEY_PRESSED} or
	 * {@code KEY_RELEASED}, the focus traversal operation will occur.
	 * Regardless of which {@code KeyEvent} is specified, however, all
	 * {@code KeyEvent}s related to the focus traversal key, including the
	 * associated {@code KEY_TYPED} event, will be consumed, and will not be
	 * dispatched to any {@code Component}. It is a runtime error to specify a
	 * {@code KEY_TYPED} event as mapping to a focus traversal operation, or to
	 * map the same event to multiple default focus traversal operations.
	 * <p>
	 * This method may throw a {@code ClassCastException} if any {@code Object}
	 * in {@code keystrokes} is not an {@code AWTKeyStroke}.
	 *
	 * @param id
	 *            one of {@code KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS},
	 *            {@code KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS},
	 *            {@code KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS}, or
	 *            {@code KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS}
	 * @param keystrokes
	 *            the Set of {@code AWTKeyStroke}s for the specified operation
	 * @see #getDefaultFocusTraversalKeys
	 * @see Component#setFocusTraversalKeys
	 * @see Component#getFocusTraversalKeys
	 * @throws IllegalArgumentException
	 *             if id is not one of
	 *             {@code KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS},
	 *             {@code KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS},
	 *             {@code KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS}, or
	 *             {@code KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS}, or if
	 *             keystrokes is {@code null}, or if keystrokes contains
	 *             {@code null}, or if any keystroke represents a
	 *             {@code KEY_TYPED} event, or if any keystroke already maps to
	 *             another default focus traversal operation
	 * @beaninfo bound: true
	 */
	public void setDefaultFocusTraversalKeys(int id, Set<? extends AWTKeyStroke> keystrokes) {
		if ((id < 0) || (id >= TRAVERSAL_KEY_LENGTH)) {
			throw new IllegalArgumentException("invalid focus traversal key identifier");
		}
		if (keystrokes == null) {
			throw new IllegalArgumentException("cannot set null Set of default focus traversal keys");
		}
		Set<AWTKeyStroke> oldKeys;
		synchronized (this) {
			for (AWTKeyStroke keystroke : keystrokes) {
				if (keystroke == null) {
					throw new IllegalArgumentException("cannot set null focus traversal key");
				}
				if (keystroke.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
					throw new IllegalArgumentException("focus traversal keys cannot map to KEY_TYPED events");
				}
				// Check to see if key already maps to another traversal
				// operation
				for (int i = 0; i < TRAVERSAL_KEY_LENGTH; i++) {
					if (i == id) {
						continue;
					}
					if (defaultFocusTraversalKeys[i].contains(keystroke)) {
						throw new IllegalArgumentException("focus traversal keys must be unique for a Component");
					}
				}
			}
			oldKeys = defaultFocusTraversalKeys[id];
			defaultFocusTraversalKeys[id] = Collections.unmodifiableSet(new HashSet<>(keystrokes));
		}
		firePropertyChange(defaultFocusTraversalKeyPropertyNames[id], oldKeys, keystrokes);
	}

	/**
	 * Returns a Set of default focus traversal keys for a given traversal
	 * operation. This traversal key Set will be in effect on all Windows that
	 * have no such Set of their own explicitly defined. This Set will also be
	 * inherited, recursively, by any child Component of those Windows that has
	 * no such Set of its own explicitly defined. (See
	 * <code>setDefaultFocusTraversalKeys</code> for a full description of each
	 * operation.)
	 *
	 * @param id
	 *            one of KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
	 *            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
	 *            KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
	 *            KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
	 * @return the <code>Set</code> of <code>AWTKeyStroke</code>s for the
	 *         specified operation; the <code>Set</code> will be unmodifiable,
	 *         and may be empty; <code>null</code> will never be returned
	 * @see #setDefaultFocusTraversalKeys
	 * @see Component#setFocusTraversalKeys
	 * @see Component#getFocusTraversalKeys
	 * @throws IllegalArgumentException
	 *             if id is not one of
	 *             KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
	 *             KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
	 *             KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS, or
	 *             KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS
	 */
	public Set<AWTKeyStroke> getDefaultFocusTraversalKeys(int id) {
		if ((id < 0) || (id >= TRAVERSAL_KEY_LENGTH)) {
			throw new IllegalArgumentException("invalid focus traversal key identifier");
		}
		// Okay to return Set directly because it is an unmodifiable view
		return defaultFocusTraversalKeys[id];
	}

	/**
	 * Returns the current focus cycle root, if the current focus cycle root is
	 * in the same context as the calling thread. If the focus owner is itself a
	 * focus cycle root, then it may be ambiguous as to which Components
	 * represent the next and previous Components to focus during normal focus
	 * traversal. In that case, the current focus cycle root is used to
	 * differentiate among the possibilities.
	 * <p>
	 * This method is intended to be used only by KeyboardFocusManagers and
	 * focus implementations. It is not for general client use.
	 *
	 * @return the current focus cycle root, or null if the current focus cycle
	 *         root is not a member of the calling thread's context
	 * @see #getGlobalCurrentFocusCycleRoot
	 * @see #setGlobalCurrentFocusCycleRoot
	 */
	public Container getCurrentFocusCycleRoot() {
		synchronized (KeyboardFocusManager.class) {
			if (currentFocusCycleRoot == null) {
				return null;
			}
			return (currentFocusCycleRoot.appContext == AppContext.getAppContext()) ? currentFocusCycleRoot : null;
		}
	}

	/**
	 * Returns the current focus cycle root, even if the calling thread is in a
	 * different context than the current focus cycle root. If the focus owner
	 * is itself a focus cycle root, then it may be ambiguous as to which
	 * Components represent the next and previous Components to focus during
	 * normal focus traversal. In that case, the current focus cycle root is
	 * used to differentiate among the possibilities.
	 *
	 * @return the current focus cycle root, or null if the current focus cycle
	 *         root is not a member of the calling thread's context
	 * @see #getCurrentFocusCycleRoot
	 * @see #setGlobalCurrentFocusCycleRoot
	 * @throws SecurityException
	 *             if this KeyboardFocusManager is not the current
	 *             KeyboardFocusManager for the calling thread's context and if
	 *             the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 */
	protected Container getGlobalCurrentFocusCycleRoot() throws SecurityException {
		synchronized (KeyboardFocusManager.class) {
			checkKFMSecurity();
			return currentFocusCycleRoot;
		}
	}

	/**
	 * Sets the current focus cycle root. If the focus owner is itself a focus
	 * cycle root, then it may be ambiguous as to which Components represent the
	 * next and previous Components to focus during normal focus traversal. In
	 * that case, the current focus cycle root is used to differentiate among
	 * the possibilities.
	 * <p>
	 * If a SecurityManager is installed, the calling thread must be granted the
	 * "replaceKeyboardFocusManager" AWTPermission. If this permission is not
	 * granted, this method will throw a SecurityException, and the current
	 * focus cycle root will not be changed.
	 * <p>
	 * This method is intended to be used only by KeyboardFocusManagers and
	 * focus implementations. It is not for general client use.
	 *
	 * @param newFocusCycleRoot
	 *            the new focus cycle root
	 * @see #getCurrentFocusCycleRoot
	 * @see #getGlobalCurrentFocusCycleRoot
	 * @throws SecurityException
	 *             if the calling thread does not have
	 *             "replaceKeyboardFocusManager" permission
	 * @beaninfo bound: true
	 */
	public void setGlobalCurrentFocusCycleRoot(Container newFocusCycleRoot) throws SecurityException {
		checkReplaceKFMPermission();
		Container oldFocusCycleRoot;
		synchronized (KeyboardFocusManager.class) {
			oldFocusCycleRoot = getCurrentFocusCycleRoot();
			currentFocusCycleRoot = newFocusCycleRoot;
		}
		firePropertyChange("currentFocusCycleRoot", oldFocusCycleRoot, newFocusCycleRoot);
	}

	void setGlobalCurrentFocusCycleRootPriv(final Container newFocusCycleRoot) {
		AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
			setGlobalCurrentFocusCycleRoot(newFocusCycleRoot);
			return null;
		});
	}

	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all bound properties of this class, including the
	 * following:
	 * <ul>
	 * <li>whether the KeyboardFocusManager is currently managing focus for this
	 * application or applet's browser context ("managingFocus")</li>
	 * <li>the focus owner ("focusOwner")</li>
	 * <li>the permanent focus owner ("permanentFocusOwner")</li>
	 * <li>the focused Window ("focusedWindow")</li>
	 * <li>the active Window ("activeWindow")</li>
	 * <li>the default focus traversal policy
	 * ("defaultFocusTraversalPolicy")</li>
	 * <li>the Set of default FORWARD_TRAVERSAL_KEYS
	 * ("forwardDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default BACKWARD_TRAVERSAL_KEYS
	 * ("backwardDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default UP_CYCLE_TRAVERSAL_KEYS
	 * ("upCycleDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default DOWN_CYCLE_TRAVERSAL_KEYS
	 * ("downCycleDefaultFocusTraversalKeys")</li>
	 * <li>the current focus cycle root ("currentFocusCycleRoot")</li>
	 * </ul>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener
	 *            the PropertyChangeListener to be added
	 * @see #removePropertyChangeListener
	 * @see #getPropertyChangeListeners
	 * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (changeSupport == null) {
					changeSupport = new PropertyChangeSupport(this);
				}
				changeSupport.addPropertyChangeListener(listener);
			}
		}
	}

	/**
	 * Removes a PropertyChangeListener from the listener list. This method
	 * should be used to remove the PropertyChangeListeners that were registered
	 * for all bound properties of this class.
	 * <p>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 * @see #addPropertyChangeListener
	 * @see #getPropertyChangeListeners
	 * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (changeSupport != null) {
					changeSupport.removePropertyChangeListener(listener);
				}
			}
		}
	}

	/**
	 * Returns an array of all the property change listeners registered on this
	 * keyboard focus manager.
	 *
	 * @return all of this keyboard focus manager's
	 *         <code>PropertyChangeListener</code>s or an empty array if no
	 *         property change listeners are currently registered
	 *
	 * @see #addPropertyChangeListener
	 * @see #removePropertyChangeListener
	 * @see #getPropertyChangeListeners(java.lang.String)
	 * @since 1.4
	 */
	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		return changeSupport.getPropertyChangeListeners();
	}

	/**
	 * Adds a PropertyChangeListener to the listener list for a specific
	 * property. The specified property may be user-defined, or one of the
	 * following:
	 * <ul>
	 * <li>whether the KeyboardFocusManager is currently managing focus for this
	 * application or applet's browser context ("managingFocus")</li>
	 * <li>the focus owner ("focusOwner")</li>
	 * <li>the permanent focus owner ("permanentFocusOwner")</li>
	 * <li>the focused Window ("focusedWindow")</li>
	 * <li>the active Window ("activeWindow")</li>
	 * <li>the default focus traversal policy
	 * ("defaultFocusTraversalPolicy")</li>
	 * <li>the Set of default FORWARD_TRAVERSAL_KEYS
	 * ("forwardDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default BACKWARD_TRAVERSAL_KEYS
	 * ("backwardDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default UP_CYCLE_TRAVERSAL_KEYS
	 * ("upCycleDefaultFocusTraversalKeys")</li>
	 * <li>the Set of default DOWN_CYCLE_TRAVERSAL_KEYS
	 * ("downCycleDefaultFocusTraversalKeys")</li>
	 * <li>the current focus cycle root ("currentFocusCycleRoot")</li>
	 * </ul>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param propertyName
	 *            one of the property names listed above
	 * @param listener
	 *            the PropertyChangeListener to be added
	 * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
	 * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 * @see #getPropertyChangeListeners(java.lang.String)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (changeSupport == null) {
					changeSupport = new PropertyChangeSupport(this);
				}
				changeSupport.addPropertyChangeListener(propertyName, listener);
			}
		}
	}

	/**
	 * Removes a PropertyChangeListener from the listener list for a specific
	 * property. This method should be used to remove PropertyChangeListeners
	 * that were registered for a specific bound property.
	 * <p>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param propertyName
	 *            a valid property name
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 * @see #getPropertyChangeListeners(java.lang.String)
	 * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (changeSupport != null) {
					changeSupport.removePropertyChangeListener(propertyName, listener);
				}
			}
		}
	}

	/**
	 * Returns an array of all the <code>PropertyChangeListener</code>s
	 * associated with the named property.
	 *
	 * @return all of the <code>PropertyChangeListener</code>s associated with
	 *         the named property or an empty array if no such listeners have
	 *         been added.
	 *
	 * @see #addPropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 * @see #removePropertyChangeListener(java.lang.String,java.beans.PropertyChangeListener)
	 * @since 1.4
	 */
	public synchronized PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		return changeSupport.getPropertyChangeListeners(propertyName);
	}

	/**
	 * Fires a PropertyChangeEvent in response to a change in a bound property.
	 * The event will be delivered to all registered PropertyChangeListeners. No
	 * event will be delivered if oldValue and newValue are the same.
	 *
	 * @param propertyName
	 *            the name of the property that has changed
	 * @param oldValue
	 *            the property's previous value
	 * @param newValue
	 *            the property's new value
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (oldValue == newValue) {
			return;
		}
		PropertyChangeSupport changeSupport = this.changeSupport;
		if (changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Adds a VetoableChangeListener to the listener list. The listener is
	 * registered for all vetoable properties of this class, including the
	 * following:
	 * <ul>
	 * <li>the focus owner ("focusOwner")</li>
	 * <li>the permanent focus owner ("permanentFocusOwner")</li>
	 * <li>the focused Window ("focusedWindow")</li>
	 * <li>the active Window ("activeWindow")</li>
	 * </ul>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener
	 *            the VetoableChangeListener to be added
	 * @see #removeVetoableChangeListener
	 * @see #getVetoableChangeListeners
	 * @see #addVetoableChangeListener(java.lang.String,java.beans.VetoableChangeListener)
	 */
	public void addVetoableChangeListener(VetoableChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (vetoableSupport == null) {
					vetoableSupport = new VetoableChangeSupport(this);
				}
				vetoableSupport.addVetoableChangeListener(listener);
			}
		}
	}

	/**
	 * Removes a VetoableChangeListener from the listener list. This method
	 * should be used to remove the VetoableChangeListeners that were registered
	 * for all vetoable properties of this class.
	 * <p>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param listener
	 *            the VetoableChangeListener to be removed
	 * @see #addVetoableChangeListener
	 * @see #getVetoableChangeListeners
	 * @see #removeVetoableChangeListener(java.lang.String,java.beans.VetoableChangeListener)
	 */
	public void removeVetoableChangeListener(VetoableChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (vetoableSupport != null) {
					vetoableSupport.removeVetoableChangeListener(listener);
				}
			}
		}
	}

	/**
	 * Returns an array of all the vetoable change listeners registered on this
	 * keyboard focus manager.
	 *
	 * @return all of this keyboard focus manager's
	 *         <code>VetoableChangeListener</code>s or an empty array if no
	 *         vetoable change listeners are currently registered
	 *
	 * @see #addVetoableChangeListener
	 * @see #removeVetoableChangeListener
	 * @see #getVetoableChangeListeners(java.lang.String)
	 * @since 1.4
	 */
	public synchronized VetoableChangeListener[] getVetoableChangeListeners() {
		if (vetoableSupport == null) {
			vetoableSupport = new VetoableChangeSupport(this);
		}
		return vetoableSupport.getVetoableChangeListeners();
	}

	/**
	 * Adds a VetoableChangeListener to the listener list for a specific
	 * property. The specified property may be user-defined, or one of the
	 * following:
	 * <ul>
	 * <li>the focus owner ("focusOwner")</li>
	 * <li>the permanent focus owner ("permanentFocusOwner")</li>
	 * <li>the focused Window ("focusedWindow")</li>
	 * <li>the active Window ("activeWindow")</li>
	 * </ul>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param propertyName
	 *            one of the property names listed above
	 * @param listener
	 *            the VetoableChangeListener to be added
	 * @see #addVetoableChangeListener(java.beans.VetoableChangeListener)
	 * @see #removeVetoableChangeListener
	 * @see #getVetoableChangeListeners
	 */
	public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (vetoableSupport == null) {
					vetoableSupport = new VetoableChangeSupport(this);
				}
				vetoableSupport.addVetoableChangeListener(propertyName, listener);
			}
		}
	}

	/**
	 * Removes a VetoableChangeListener from the listener list for a specific
	 * property. This method should be used to remove VetoableChangeListeners
	 * that were registered for a specific bound property.
	 * <p>
	 * If listener is null, no exception is thrown and no action is performed.
	 *
	 * @param propertyName
	 *            a valid property name
	 * @param listener
	 *            the VetoableChangeListener to be removed
	 * @see #addVetoableChangeListener
	 * @see #getVetoableChangeListeners
	 * @see #removeVetoableChangeListener(java.beans.VetoableChangeListener)
	 */
	public void removeVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
		if (listener != null) {
			synchronized (this) {
				if (vetoableSupport != null) {
					vetoableSupport.removeVetoableChangeListener(propertyName, listener);
				}
			}
		}
	}

	/**
	 * Returns an array of all the <code>VetoableChangeListener</code>s
	 * associated with the named property.
	 *
	 * @return all of the <code>VetoableChangeListener</code>s associated with
	 *         the named property or an empty array if no such listeners have
	 *         been added.
	 *
	 * @see #addVetoableChangeListener(java.lang.String,java.beans.VetoableChangeListener)
	 * @see #removeVetoableChangeListener(java.lang.String,java.beans.VetoableChangeListener)
	 * @see #getVetoableChangeListeners
	 * @since 1.4
	 */
	public synchronized VetoableChangeListener[] getVetoableChangeListeners(String propertyName) {
		if (vetoableSupport == null) {
			vetoableSupport = new VetoableChangeSupport(this);
		}
		return vetoableSupport.getVetoableChangeListeners(propertyName);
	}

	/**
	 * Fires a PropertyChangeEvent in response to a change in a vetoable
	 * property. The event will be delivered to all registered
	 * VetoableChangeListeners. If a VetoableChangeListener throws a
	 * PropertyVetoException, a new event is fired reverting all
	 * VetoableChangeListeners to the old value and the exception is then
	 * rethrown. No event will be delivered if oldValue and newValue are the
	 * same.
	 *
	 * @param propertyName
	 *            the name of the property that has changed
	 * @param oldValue
	 *            the property's previous value
	 * @param newValue
	 *            the property's new value
	 * @throws java.beans.PropertyVetoException
	 *             if a <code>VetoableChangeListener</code> threw
	 *             <code>PropertyVetoException</code>
	 */
	protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException {
		if (oldValue == newValue) {
			return;
		}
		VetoableChangeSupport vetoableSupport = this.vetoableSupport;
		if (vetoableSupport != null) {
			vetoableSupport.fireVetoableChange(propertyName, oldValue, newValue);
		}
	}

	/**
	 * Adds a KeyEventDispatcher to this KeyboardFocusManager's dispatcher
	 * chain. This KeyboardFocusManager will request that each
	 * KeyEventDispatcher dispatch KeyEvents generated by the user before
	 * finally dispatching the KeyEvent itself. KeyEventDispatchers will be
	 * notified in the order in which they were added. Notifications will halt
	 * as soon as one KeyEventDispatcher returns <code>true</code> from its
	 * <code>dispatchKeyEvent</code> method. There is no limit to the total
	 * number of KeyEventDispatchers which can be added, nor to the number of
	 * times which a particular KeyEventDispatcher instance can be added.
	 * <p>
	 * If a null dispatcher is specified, no action is taken and no exception is
	 * thrown.
	 * <p>
	 * In a multithreaded application, {@link KeyEventDispatcher} behaves the
	 * same as other AWT listeners. See
	 * <a href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading
	 * Issues</a> for more details.
	 *
	 * @param dispatcher
	 *            the KeyEventDispatcher to add to the dispatcher chain
	 * @see #removeKeyEventDispatcher
	 */
	public void addKeyEventDispatcher(KeyEventDispatcher dispatcher) {
		if (dispatcher != null) {
			synchronized (this) {
				if (keyEventDispatchers == null) {
					keyEventDispatchers = new java.util.LinkedList<>();
				}
				keyEventDispatchers.add(dispatcher);
			}
		}
	}

	/**
	 * Removes a KeyEventDispatcher which was previously added to this
	 * KeyboardFocusManager's dispatcher chain. This KeyboardFocusManager cannot
	 * itself be removed, unless it was explicitly re-registered via a call to
	 * <code>addKeyEventDispatcher</code>.
	 * <p>
	 * If a null dispatcher is specified, if the specified dispatcher is not in
	 * the dispatcher chain, or if this KeyboardFocusManager is specified
	 * without having been explicitly re-registered, no action is taken and no
	 * exception is thrown.
	 * <p>
	 * In a multithreaded application, {@link KeyEventDispatcher} behaves the
	 * same as other AWT listeners. See
	 * <a href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading
	 * Issues</a> for more details.
	 *
	 * @param dispatcher
	 *            the KeyEventDispatcher to remove from the dispatcher chain
	 * @see #addKeyEventDispatcher
	 */
	public void removeKeyEventDispatcher(KeyEventDispatcher dispatcher) {
		if (dispatcher != null) {
			synchronized (this) {
				if (keyEventDispatchers != null) {
					keyEventDispatchers.remove(dispatcher);
				}
			}
		}
	}

	/**
	 * Returns this KeyboardFocusManager's KeyEventDispatcher chain as a List.
	 * The List will not include this KeyboardFocusManager unless it was
	 * explicitly re-registered via a call to
	 * <code>addKeyEventDispatcher</code>. If no other KeyEventDispatchers are
	 * registered, implementations are free to return null or a List of length
	 * 0. Client code should not assume one behavior over another, nor should it
	 * assume that the behavior, once established, will not change.
	 *
	 * @return a possibly null or empty List of KeyEventDispatchers
	 * @see #addKeyEventDispatcher
	 * @see #removeKeyEventDispatcher
	 */
	protected synchronized java.util.List<KeyEventDispatcher> getKeyEventDispatchers() {
		return (keyEventDispatchers != null) ? (java.util.List) keyEventDispatchers.clone() : null;
	}

	/**
	 * Adds a KeyEventPostProcessor to this KeyboardFocusManager's post-
	 * processor chain. After a KeyEvent has been dispatched to and handled by
	 * its target, KeyboardFocusManager will request that each
	 * KeyEventPostProcessor perform any necessary post-processing as part of
	 * the KeyEvent's final resolution. KeyEventPostProcessors will be notified
	 * in the order in which they were added; the current KeyboardFocusManager
	 * will be notified last. Notifications will halt as soon as one
	 * KeyEventPostProcessor returns <code>true</code> from its
	 * <code>postProcessKeyEvent</code> method. There is no limit to the the
	 * total number of KeyEventPostProcessors that can be added, nor to the
	 * number of times that a particular KeyEventPostProcessor instance can be
	 * added.
	 * <p>
	 * If a null post-processor is specified, no action is taken and no
	 * exception is thrown.
	 * <p>
	 * In a multithreaded application, {@link KeyEventPostProcessor} behaves the
	 * same as other AWT listeners. See
	 * <a href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading
	 * Issues</a> for more details.
	 *
	 * @param processor
	 *            the KeyEventPostProcessor to add to the post-processor chain
	 * @see #removeKeyEventPostProcessor
	 */
	public void addKeyEventPostProcessor(KeyEventPostProcessor processor) {
		if (processor != null) {
			synchronized (this) {
				if (keyEventPostProcessors == null) {
					keyEventPostProcessors = new java.util.LinkedList<>();
				}
				keyEventPostProcessors.add(processor);
			}
		}
	}

	/**
	 * Removes a previously added KeyEventPostProcessor from this
	 * KeyboardFocusManager's post-processor chain. This KeyboardFocusManager
	 * cannot itself be entirely removed from the chain. Only additional
	 * references added via <code>addKeyEventPostProcessor</code> can be
	 * removed.
	 * <p>
	 * If a null post-processor is specified, if the specified post-processor is
	 * not in the post-processor chain, or if this KeyboardFocusManager is
	 * specified without having been explicitly added, no action is taken and no
	 * exception is thrown.
	 * <p>
	 * In a multithreaded application, {@link KeyEventPostProcessor} behaves the
	 * same as other AWT listeners. See
	 * <a href="doc-files/AWTThreadIssues.html#ListenersThreads" >AWT Threading
	 * Issues</a> for more details.
	 *
	 * @param processor
	 *            the KeyEventPostProcessor to remove from the post- processor
	 *            chain
	 * @see #addKeyEventPostProcessor
	 */
	public void removeKeyEventPostProcessor(KeyEventPostProcessor processor) {
		if (processor != null) {
			synchronized (this) {
				if (keyEventPostProcessors != null) {
					keyEventPostProcessors.remove(processor);
				}
			}
		}
	}

	/**
	 * Returns this KeyboardFocusManager's KeyEventPostProcessor chain as a
	 * List. The List will not include this KeyboardFocusManager unless it was
	 * explicitly added via a call to <code>addKeyEventPostProcessor</code>. If
	 * no KeyEventPostProcessors are registered, implementations are free to
	 * return null or a List of length 0. Client code should not assume one
	 * behavior over another, nor should it assume that the behavior, once
	 * established, will not change.
	 *
	 * @return a possibly null or empty List of KeyEventPostProcessors
	 * @see #addKeyEventPostProcessor
	 * @see #removeKeyEventPostProcessor
	 */
	protected java.util.List<KeyEventPostProcessor> getKeyEventPostProcessors() {
		return (keyEventPostProcessors != null) ? (java.util.List) keyEventPostProcessors.clone() : null;
	}

	static void setMostRecentFocusOwner(Component component) {
		Component window = component;
		while ((window != null) && !(window instanceof Window)) {
			window = window.parent;
		}
		if (window != null) {
			setMostRecentFocusOwner((Window) window, component);
		}
	}

	static synchronized void setMostRecentFocusOwner(Window window, Component component) {
		// ATTN: component has a strong reference to window via chain
		// of Component.parent fields. Since WeakHasMap refers to its
		// values strongly, we need to break the strong link from the
		// value (component) back to its key (window).
		WeakReference<Component> weakValue = null;
		if (component != null) {
			weakValue = new WeakReference<>(component);
		}
		mostRecentFocusOwners.put(window, weakValue);
	}

	static void clearMostRecentFocusOwner(Component comp) {
		Container window;
		if (comp == null) {
			return;
		}
		synchronized (comp.getTreeLock()) {
			window = comp.getParent();
			while ((window != null) && !(window instanceof Window)) {
				window = window.getParent();
			}
		}
		synchronized (KeyboardFocusManager.class) {
			if ((window != null) && (getMostRecentFocusOwner((Window) window) == comp)) {
				setMostRecentFocusOwner((Window) window, null);
			}
			// Also clear temporary lost component stored in Window
			if (window != null) {
				Window realWindow = (Window) window;
				if (realWindow.getTemporaryLostComponent() == comp) {
					realWindow.setTemporaryLostComponent(null);
				}
			}
		}
	}

	/*
	 * Please be careful changing this method! It is called from
	 * javax.swing.JComponent.runInputVerifier() using reflection.
	 */
	static synchronized Component getMostRecentFocusOwner(Window window) {
		WeakReference<Component> weakValue = mostRecentFocusOwners.get(window);
		return weakValue == null ? null : (Component) weakValue.get();
	}

	/**
	 * This method is called by the AWT event dispatcher requesting that the
	 * current KeyboardFocusManager dispatch the specified event on its behalf.
	 * It is expected that all KeyboardFocusManagers will dispatch all
	 * FocusEvents, all WindowEvents related to focus, and all KeyEvents. These
	 * events should be dispatched based on the KeyboardFocusManager's notion of
	 * the focus owner and the focused and active Windows, sometimes overriding
	 * the source of the specified AWTEvent. Dispatching must be done using
	 * <code>redispatchEvent</code> to prevent the AWT event dispatcher from
	 * recursively requesting that the KeyboardFocusManager dispatch the event
	 * again. If this method returns <code>false</code>, then the AWT event
	 * dispatcher will attempt to dispatch the event itself.
	 *
	 * @param e
	 *            the AWTEvent to be dispatched
	 * @return <code>true</code> if this method dispatched the event;
	 *         <code>false</code> otherwise
	 * @see #redispatchEvent
	 * @see #dispatchKeyEvent
	 */
	public abstract boolean dispatchEvent(AWTEvent e);

	/**
	 * Redispatches an AWTEvent in such a way that the AWT event dispatcher will
	 * not recursively request that the KeyboardFocusManager, or any installed
	 * KeyEventDispatchers, dispatch the event again. Client implementations of
	 * <code>dispatchEvent</code> and client-defined KeyEventDispatchers must
	 * call <code>redispatchEvent(target, e)</code> instead of
	 * <code>target.dispatchEvent(e)</code> to dispatch an event.
	 * <p>
	 * This method is intended to be used only by KeyboardFocusManagers and
	 * KeyEventDispatchers. It is not for general client use.
	 *
	 * @param target
	 *            the Component to which the event should be dispatched
	 * @param e
	 *            the event to dispatch
	 * @see #dispatchEvent
	 * @see KeyEventDispatcher
	 */
	public final void redispatchEvent(Component target, AWTEvent e) {
		e.focusManagerIsDispatching = true;
		target.dispatchEvent(e);
		e.focusManagerIsDispatching = false;
	}

	/**
	 * Typically this method will be called by <code>dispatchEvent</code> if no
	 * other KeyEventDispatcher in the dispatcher chain dispatched the KeyEvent,
	 * or if no other KeyEventDispatchers are registered. If an implementation
	 * of this method returns <code>false</code>, <code>dispatchEvent</code> may
	 * try to dispatch the KeyEvent itself, or may simply return
	 * <code>false</code>. If <code>true</code> is returned,
	 * <code>dispatchEvent</code> should return <code>true</code> as well.
	 *
	 * @param e
	 *            the KeyEvent which the current KeyboardFocusManager has
	 *            requested that this KeyEventDispatcher dispatch
	 * @return <code>true</code> if the KeyEvent was dispatched;
	 *         <code>false</code> otherwise
	 * @see #dispatchEvent
	 */
	public abstract boolean dispatchKeyEvent(KeyEvent e);

	/**
	 * This method will be called by <code>dispatchKeyEvent</code>. By default,
	 * this method will handle any unconsumed KeyEvents that map to an AWT
	 * <code>MenuShortcut</code> by consuming the event and activating the
	 * shortcut.
	 *
	 * @param e
	 *            the KeyEvent to post-process
	 * @return <code>true</code> to indicate that no other KeyEventPostProcessor
	 *         will be notified of the KeyEvent.
	 * @see #dispatchKeyEvent
	 * @see MenuShortcut
	 */
	public abstract boolean postProcessKeyEvent(KeyEvent e);

	/**
	 * This method initiates a focus traversal operation if and only if the
	 * KeyEvent represents a focus traversal key for the specified
	 * focusedComponent. It is expected that focusedComponent is the current
	 * focus owner, although this need not be the case. If it is not, focus
	 * traversal will nevertheless proceed as if focusedComponent were the
	 * current focus owner.
	 *
	 * @param focusedComponent
	 *            the Component that will be the basis for a focus traversal
	 *            operation if the specified event represents a focus traversal
	 *            key for the Component
	 * @param e
	 *            the event that may represent a focus traversal key
	 */
	public abstract void processKeyEvent(Component focusedComponent, KeyEvent e);

	/**
	 * Called by the AWT to notify the KeyboardFocusManager that it should delay
	 * dispatching of KeyEvents until the specified Component becomes the focus
	 * owner. If client code requests a focus change, and the AWT determines
	 * that this request might be granted by the native windowing system, then
	 * the AWT will call this method. It is the responsibility of the
	 * KeyboardFocusManager to delay dispatching of KeyEvents with timestamps
	 * later than the specified time stamp until the specified Component
	 * receives a FOCUS_GAINED event, or the AWT cancels the delay request by
	 * invoking <code>dequeueKeyEvents</code> or <code>discardKeyEvents</code>.
	 *
	 * @param after
	 *            timestamp of current event, or the current, system time if the
	 *            current event has no timestamp, or the AWT cannot determine
	 *            which event is currently being handled
	 * @param untilFocused
	 *            Component which should receive a FOCUS_GAINED event before any
	 *            pending KeyEvents
	 * @see #dequeueKeyEvents
	 * @see #discardKeyEvents
	 */
	protected abstract void enqueueKeyEvents(long after, Component untilFocused);

	/**
	 * Called by the AWT to notify the KeyboardFocusManager that it should
	 * cancel delayed dispatching of KeyEvents. All KeyEvents which were
	 * enqueued because of a call to <code>enqueueKeyEvents</code> with the same
	 * timestamp and Component should be released for normal dispatching to the
	 * current focus owner. If the given timestamp is less than zero, the
	 * outstanding enqueue request for the given Component with the <b>
	 * oldest</b> timestamp (if any) should be cancelled.
	 *
	 * @param after
	 *            the timestamp specified in the call to
	 *            <code>enqueueKeyEvents</code>, or any value &lt; 0
	 * @param untilFocused
	 *            the Component specified in the call to
	 *            <code>enqueueKeyEvents</code>
	 * @see #enqueueKeyEvents
	 * @see #discardKeyEvents
	 */
	protected abstract void dequeueKeyEvents(long after, Component untilFocused);

	/**
	 * Called by the AWT to notify the KeyboardFocusManager that it should
	 * cancel delayed dispatching of KeyEvents. All KeyEvents which were
	 * enqueued because of one or more calls to <code>enqueueKeyEvents</code>
	 * with the same Component should be discarded.
	 *
	 * @param comp
	 *            the Component specified in one or more calls to
	 *            <code>enqueueKeyEvents</code>
	 * @see #enqueueKeyEvents
	 * @see #dequeueKeyEvents
	 */
	protected abstract void discardKeyEvents(Component comp);

	/**
	 * Focuses the Component after aComponent, typically based on a
	 * FocusTraversalPolicy.
	 *
	 * @param aComponent
	 *            the Component that is the basis for the focus traversal
	 *            operation
	 * @see FocusTraversalPolicy
	 */
	public abstract void focusNextComponent(Component aComponent);

	/**
	 * Focuses the Component before aComponent, typically based on a
	 * FocusTraversalPolicy.
	 *
	 * @param aComponent
	 *            the Component that is the basis for the focus traversal
	 *            operation
	 * @see FocusTraversalPolicy
	 */
	public abstract void focusPreviousComponent(Component aComponent);

	/**
	 * Moves the focus up one focus traversal cycle. Typically, the focus owner
	 * is set to aComponent's focus cycle root, and the current focus cycle root
	 * is set to the new focus owner's focus cycle root. If, however,
	 * aComponent's focus cycle root is a Window, then typically the focus owner
	 * is set to the Window's default Component to focus, and the current focus
	 * cycle root is unchanged.
	 *
	 * @param aComponent
	 *            the Component that is the basis for the focus traversal
	 *            operation
	 */
	public abstract void upFocusCycle(Component aComponent);

	/**
	 * Moves the focus down one focus traversal cycle. Typically, if aContainer
	 * is a focus cycle root, then the focus owner is set to aContainer's
	 * default Component to focus, and the current focus cycle root is set to
	 * aContainer. If aContainer is not a focus cycle root, then no focus
	 * traversal operation occurs.
	 *
	 * @param aContainer
	 *            the Container that is the basis for the focus traversal
	 *            operation
	 */
	public abstract void downFocusCycle(Container aContainer);

	/**
	 * Focuses the Component after the current focus owner.
	 */
	public final void focusNextComponent() {
		Component focusOwner = getFocusOwner();
		if (focusOwner != null) {
			focusNextComponent(focusOwner);
		}
	}

	/**
	 * Focuses the Component before the current focus owner.
	 */
	public final void focusPreviousComponent() {
		Component focusOwner = getFocusOwner();
		if (focusOwner != null) {
			focusPreviousComponent(focusOwner);
		}
	}

	/**
	 * Moves the focus up one focus traversal cycle from the current focus
	 * owner. Typically, the new focus owner is set to the current focus owner's
	 * focus cycle root, and the current focus cycle root is set to the new
	 * focus owner's focus cycle root. If, however, the current focus owner's
	 * focus cycle root is a Window, then typically the focus owner is set to
	 * the focus cycle root's default Component to focus, and the current focus
	 * cycle root is unchanged.
	 */
	public final void upFocusCycle() {
		Component focusOwner = getFocusOwner();
		if (focusOwner != null) {
			upFocusCycle(focusOwner);
		}
	}

	/**
	 * Moves the focus down one focus traversal cycle from the current focus
	 * owner, if and only if the current focus owner is a Container that is a
	 * focus cycle root. Typically, the focus owner is set to the current focus
	 * owner's default Component to focus, and the current focus cycle root is
	 * set to the current focus owner. If the current focus owner is not a
	 * Container that is a focus cycle root, then no focus traversal operation
	 * occurs.
	 */
	public final void downFocusCycle() {
		Component focusOwner = getFocusOwner();
		if (focusOwner instanceof Container) {
			downFocusCycle((Container) focusOwner);
		}
	}

	/**
	 * Dumps the list of focus requests to stderr
	 */
	void dumpRequests() {
		System.err.println(">>> Requests dump, time: " + System.currentTimeMillis());
		synchronized (heavyweightRequests) {
			for (HeavyweightFocusRequest req : heavyweightRequests) {
				System.err.println(">>> Req: " + req);
			}
		}
		System.err.println("");
	}

	private static final class LightweightFocusRequest {
		final Component component;
		final boolean temporary;
		final CausedFocusEvent.Cause cause;

		LightweightFocusRequest(Component component, boolean temporary, CausedFocusEvent.Cause cause) {
			this.component = component;
			this.temporary = temporary;
			this.cause = cause;
		}

		public String toString() {
			return "LightweightFocusRequest[component=" + component + ",temporary=" + temporary + ", cause=" + cause + "]";
		}
	}

	private static final class HeavyweightFocusRequest {
		final Component heavyweight;
		final LinkedList<LightweightFocusRequest> lightweightRequests;
		static final HeavyweightFocusRequest CLEAR_GLOBAL_FOCUS_OWNER = new HeavyweightFocusRequest();

		private HeavyweightFocusRequest() {
			heavyweight = null;
			lightweightRequests = null;
		}

		HeavyweightFocusRequest(Component heavyweight, Component descendant, boolean temporary, CausedFocusEvent.Cause cause) {
			if (log.isLoggable(PlatformLogger.Level.FINE)) {
				if (heavyweight == null) {
					log.fine("Assertion (heavyweight != null) failed");
				}
			}
			this.heavyweight = heavyweight;
			lightweightRequests = new LinkedList<>();
			addLightweightRequest(descendant, temporary, cause);
		}

		boolean addLightweightRequest(Component descendant, boolean temporary, CausedFocusEvent.Cause cause) {
			if (log.isLoggable(PlatformLogger.Level.FINE)) {
				if (this == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
					log.fine("Assertion (this != HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) failed");
				}
				if (descendant == null) {
					log.fine("Assertion (descendant != null) failed");
				}
			}
			Component lastDescendant = ((lightweightRequests.size() > 0) ? lightweightRequests.getLast().component : null);
			if (descendant != lastDescendant) {
				// Not a duplicate request
				lightweightRequests.add(new LightweightFocusRequest(descendant, temporary, cause));
				return true;
			} else {
				return false;
			}
		}

		LightweightFocusRequest getFirstLightweightRequest() {
			if (this == CLEAR_GLOBAL_FOCUS_OWNER) {
				return null;
			}
			return lightweightRequests.getFirst();
		}

		public String toString() {
			boolean first = true;
			String str = "HeavyweightFocusRequest[heavweight=" + heavyweight + ",lightweightRequests=";
			if (lightweightRequests == null) {
				str += null;
			} else {
				str += "[";
				for (LightweightFocusRequest lwRequest : lightweightRequests) {
					if (first) {
						first = false;
					} else {
						str += ",";
					}
					str += lwRequest;
				}
				str += "]";
			}
			str += "]";
			return str;
		}
	}

	/*
	 * heavyweightRequests is used as a monitor for synchronized changes of
	 * currentLightweightRequests, clearingCurrentLightweightRequests and
	 * newFocusOwner.
	 */
	private static LinkedList<HeavyweightFocusRequest> heavyweightRequests = new LinkedList<>();
	private static LinkedList<LightweightFocusRequest> currentLightweightRequests;
	private static boolean clearingCurrentLightweightRequests;
	private static boolean allowSyncFocusRequests = true;
	private static Component newFocusOwner = null;
	private static volatile boolean disableRestoreFocus;
	static final int SNFH_FAILURE = 0;
	static final int SNFH_SUCCESS_HANDLED = 1;
	static final int SNFH_SUCCESS_PROCEED = 2;

	static boolean processSynchronousLightweightTransfer(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time) {
		Window parentWindow = SunToolkit.getContainingWindow(heavyweight);
		if ((parentWindow == null) || !parentWindow.syncLWRequests) {
			return false;
		}
		if (descendant == null) {
			// Focus transfers from a lightweight child back to the
			// heavyweight Container should be treated like lightweight
			// focus transfers.
			descendant = heavyweight;
		}
		KeyboardFocusManager manager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(descendant));
		FocusEvent currentFocusOwnerEvent = null;
		FocusEvent newFocusOwnerEvent = null;
		Component currentFocusOwner = manager.getGlobalFocusOwner();
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getLastHWRequest();
			if ((hwFocusRequest == null) && (heavyweight == manager.getNativeFocusOwner()) && allowSyncFocusRequests) {
				if (descendant == currentFocusOwner) {
					// Redundant request.
					return true;
				}
				// 'heavyweight' owns the native focus and there are no pending
				// requests. 'heavyweight' must be a Container and
				// 'descendant' must not be the focus owner. Otherwise,
				// we would never have gotten this far.
				manager.enqueueKeyEvents(time, descendant);
				hwFocusRequest = new HeavyweightFocusRequest(heavyweight, descendant, temporary, CausedFocusEvent.Cause.UNKNOWN);
				heavyweightRequests.add(hwFocusRequest);
				if (currentFocusOwner != null) {
					currentFocusOwnerEvent = new FocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, temporary, descendant);
				}
				newFocusOwnerEvent = new FocusEvent(descendant, FocusEvent.FOCUS_GAINED, temporary, currentFocusOwner);
			}
		}
		boolean result = false;
		final boolean clearing = clearingCurrentLightweightRequests;
		Throwable caughtEx = null;
		try {
			clearingCurrentLightweightRequests = false;
			synchronized (Component.LOCK) {
				if ((currentFocusOwnerEvent != null) && (currentFocusOwner != null)) {
					((AWTEvent) currentFocusOwnerEvent).isPosted = true;
					caughtEx = dispatchAndCatchException(caughtEx, currentFocusOwner, currentFocusOwnerEvent);
					result = true;
				}
				if ((newFocusOwnerEvent != null) && (descendant != null)) {
					((AWTEvent) newFocusOwnerEvent).isPosted = true;
					caughtEx = dispatchAndCatchException(caughtEx, descendant, newFocusOwnerEvent);
					result = true;
				}
			}
		} finally {
			clearingCurrentLightweightRequests = clearing;
		}
		if (caughtEx instanceof RuntimeException) {
			throw (RuntimeException) caughtEx;
		} else if (caughtEx instanceof Error) {
			throw (Error) caughtEx;
		}
		return result;
	}

	/**
	 * Indicates whether the native implementation should proceed with a
	 * pending, native focus request. Before changing the focus at the native
	 * level, the AWT implementation should always call this function for
	 * permission. This function will reject the request if a duplicate request
	 * preceded it, or if the specified heavyweight Component already owns the
	 * focus and no native focus changes are pending. Otherwise, the request
	 * will be approved and the focus request list will be updated so that, if
	 * necessary, the proper descendant will be focused when the corresponding
	 * FOCUS_GAINED event on the heavyweight is received.
	 *
	 * An implementation must ensure that calls to this method and native focus
	 * changes are atomic. If this is not guaranteed, then the ordering of the
	 * focus request list may be incorrect, leading to errors in the type-ahead
	 * mechanism. Typically this is accomplished by only calling this function
	 * from the native event pumping thread, or by holding a global, native lock
	 * during invocation.
	 */
	static int shouldNativelyFocusHeavyweight(Component heavyweight, Component descendant, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
		if (log.isLoggable(PlatformLogger.Level.FINE)) {
			if (heavyweight == null) {
				log.fine("Assertion (heavyweight != null) failed");
			}
			if (time == 0) {
				log.fine("Assertion (time != 0) failed");
			}
		}
		if (descendant == null) {
			// Focus transfers from a lightweight child back to the
			// heavyweight Container should be treated like lightweight
			// focus transfers.
			descendant = heavyweight;
		}
		KeyboardFocusManager manager = getCurrentKeyboardFocusManager(SunToolkit.targetToAppContext(descendant));
		KeyboardFocusManager thisManager = getCurrentKeyboardFocusManager();
		Component currentFocusOwner = thisManager.getGlobalFocusOwner();
		Component nativeFocusOwner = thisManager.getNativeFocusOwner();
		Window nativeFocusedWindow = thisManager.getNativeFocusedWindow();
		if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
			focusLog.finer("SNFH for {0} in {1}", String.valueOf(descendant), String.valueOf(heavyweight));
		}
		if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
			focusLog.finest("0. Current focus owner {0}", String.valueOf(currentFocusOwner));
			focusLog.finest("0. Native focus owner {0}", String.valueOf(nativeFocusOwner));
			focusLog.finest("0. Native focused window {0}", String.valueOf(nativeFocusedWindow));
		}
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getLastHWRequest();
			if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
				focusLog.finest("Request {0}", String.valueOf(hwFocusRequest));
			}
			if ((hwFocusRequest == null) && (heavyweight == nativeFocusOwner) && (heavyweight.getContainingWindow() == nativeFocusedWindow)) {
				if (descendant == currentFocusOwner) {
					// Redundant request.
					if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
						focusLog.finest("1. SNFH_FAILURE for {0}", String.valueOf(descendant));
					}
					return SNFH_FAILURE;
				}
				// 'heavyweight' owns the native focus and there are no pending
				// requests. 'heavyweight' must be a Container and
				// 'descendant' must not be the focus owner. Otherwise,
				// we would never have gotten this far.
				manager.enqueueKeyEvents(time, descendant);
				hwFocusRequest = new HeavyweightFocusRequest(heavyweight, descendant, temporary, cause);
				heavyweightRequests.add(hwFocusRequest);
				if (currentFocusOwner != null) {
					FocusEvent currentFocusOwnerEvent = new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, temporary, descendant, cause);
					// Fix 5028014. Rolled out.
					// SunToolkit.postPriorityEvent(currentFocusOwnerEvent);
					SunToolkit.postEvent(currentFocusOwner.appContext, currentFocusOwnerEvent);
				}
				FocusEvent newFocusOwnerEvent = new CausedFocusEvent(descendant, FocusEvent.FOCUS_GAINED, temporary, currentFocusOwner, cause);
				// Fix 5028014. Rolled out.
				// SunToolkit.postPriorityEvent(newFocusOwnerEvent);
				SunToolkit.postEvent(descendant.appContext, newFocusOwnerEvent);
				if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
					focusLog.finest("2. SNFH_HANDLED for {0}", String.valueOf(descendant));
				}
				return SNFH_SUCCESS_HANDLED;
			} else if ((hwFocusRequest != null) && (hwFocusRequest.heavyweight == heavyweight)) {
				// 'heavyweight' doesn't have the native focus right now, but
				// if all pending requests were completed, it would. Add
				// descendant to the heavyweight's list of pending
				// lightweight focus transfers.
				if (hwFocusRequest.addLightweightRequest(descendant, temporary, cause)) {
					manager.enqueueKeyEvents(time, descendant);
				}
				if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
					focusLog.finest("3. SNFH_HANDLED for lightweight" + descendant + " in " + heavyweight);
				}
				return SNFH_SUCCESS_HANDLED;
			} else {
				if (!focusedWindowChangeAllowed) {
					// For purposes of computing oldFocusedWindow, we should
					// look at
					// the second to last HeavyweightFocusRequest on the queue
					// iff the
					// last HeavyweightFocusRequest is CLEAR_GLOBAL_FOCUS_OWNER.
					// If
					// there is no second to last HeavyweightFocusRequest, null
					// is an
					// acceptable value.
					if (hwFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
						int size = heavyweightRequests.size();
						hwFocusRequest = (size >= 2) ? heavyweightRequests.get(size - 2) : null;
					}
					if (focusedWindowChanged(heavyweight, (hwFocusRequest != null) ? hwFocusRequest.heavyweight : nativeFocusedWindow)) {
						if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
							focusLog.finest("4. SNFH_FAILURE for " + descendant);
						}
						return SNFH_FAILURE;
					}
				}
				manager.enqueueKeyEvents(time, descendant);
				heavyweightRequests.add(new HeavyweightFocusRequest(heavyweight, descendant, temporary, cause));
				if (focusLog.isLoggable(PlatformLogger.Level.FINEST)) {
					focusLog.finest("5. SNFH_PROCEED for " + descendant);
				}
				return SNFH_SUCCESS_PROCEED;
			}
		}
	}

	/**
	 * Returns the Window which will be active after processing this request, or
	 * null if this is a duplicate request. The active Window is useful because
	 * some native platforms do not support setting the native focus owner to
	 * null. On these platforms, the obvious choice is to set the focus owner to
	 * the focus proxy of the active Window.
	 */
	static Window markClearGlobalFocusOwner() {
		// need to call this out of synchronized block to avoid possible
		// deadlock
		// see 6454631.
		final Component nativeFocusedWindow = getCurrentKeyboardFocusManager().getNativeFocusedWindow();
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getLastHWRequest();
			if (hwFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
				// duplicate request
				return null;
			}
			heavyweightRequests.add(HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER);
			Component activeWindow = ((hwFocusRequest != null) ? SunToolkit.getContainingWindow(hwFocusRequest.heavyweight) : nativeFocusedWindow);
			while ((activeWindow != null) && !((activeWindow instanceof Frame) || (activeWindow instanceof Dialog))) {
				activeWindow = activeWindow.getParent_NoClientCode();
			}
			return (Window) activeWindow;
		}
	}

	Component getCurrentWaitingRequest(Component parent) {
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getFirstHWRequest();
			if (hwFocusRequest != null) {
				if (hwFocusRequest.heavyweight == parent) {
					LightweightFocusRequest lwFocusRequest = hwFocusRequest.lightweightRequests.getFirst();
					if (lwFocusRequest != null) {
						return lwFocusRequest.component;
					}
				}
			}
		}
		return null;
	}

	static boolean isAutoFocusTransferEnabled() {
		synchronized (heavyweightRequests) {
			return (heavyweightRequests.size() == 0) && !disableRestoreFocus && (null == currentLightweightRequests);
		}
	}

	static boolean isAutoFocusTransferEnabledFor(Component comp) {
		return isAutoFocusTransferEnabled() && comp.isAutoFocusTransferOnDisposal();
	}

	/*
	 * Used to process exceptions in dispatching focus event (in
	 * focusLost/focusGained callbacks).
	 *
	 * @param ex previously caught exception that may be processed right here,
	 * or null
	 *
	 * @param comp the component to dispatch the event to
	 *
	 * @param event the event to dispatch to the component
	 */
	static private Throwable dispatchAndCatchException(Throwable ex, Component comp, FocusEvent event) {
		Throwable retEx = null;
		try {
			comp.dispatchEvent(event);
		} catch (RuntimeException re) {
			retEx = re;
		} catch (Error er) {
			retEx = er;
		}
		if (retEx != null) {
			if (ex != null) {
				handleException(ex);
			}
			return retEx;
		}
		return ex;
	}

	static private void handleException(Throwable ex) {
		ex.printStackTrace();
	}

	static void processCurrentLightweightRequests() {
		KeyboardFocusManager manager = getCurrentKeyboardFocusManager();
		LinkedList<LightweightFocusRequest> localLightweightRequests = null;
		Component globalFocusOwner = manager.getGlobalFocusOwner();
		if ((globalFocusOwner != null) && (globalFocusOwner.appContext != AppContext.getAppContext())) {
			// The current app context differs from the app context of a focus
			// owner (and all pending lightweight requests), so we do nothing
			// now and wait for a next event.
			return;
		}
		synchronized (heavyweightRequests) {
			if (currentLightweightRequests != null) {
				clearingCurrentLightweightRequests = true;
				disableRestoreFocus = true;
				localLightweightRequests = currentLightweightRequests;
				allowSyncFocusRequests = (localLightweightRequests.size() < 2);
				currentLightweightRequests = null;
			} else {
				// do nothing
				return;
			}
		}
		Throwable caughtEx = null;
		try {
			if (localLightweightRequests != null) {
				Component lastFocusOwner = null;
				Component currentFocusOwner = null;
				for (Iterator<KeyboardFocusManager.LightweightFocusRequest> iter = localLightweightRequests.iterator(); iter.hasNext();) {
					currentFocusOwner = manager.getGlobalFocusOwner();
					LightweightFocusRequest lwFocusRequest = iter.next();
					/*
					 * WARNING: This is based on DKFM's logic solely!
					 *
					 * We allow to trigger restoreFocus() in the dispatching
					 * process only if we have the last request to dispatch. If
					 * the last request fails, focus will be restored to either
					 * the component of the last previously succedded request,
					 * or to to the focus owner that was before this clearing
					 * process.
					 */
					if (!iter.hasNext()) {
						disableRestoreFocus = false;
					}
					FocusEvent currentFocusOwnerEvent = null;
					/*
					 * We're not dispatching FOCUS_LOST while the current focus
					 * owner is null. But regardless of whether it's null or
					 * not, we're clearing ALL the local lw requests.
					 */
					if (currentFocusOwner != null) {
						currentFocusOwnerEvent = new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, lwFocusRequest.temporary, lwFocusRequest.component, lwFocusRequest.cause);
					}
					FocusEvent newFocusOwnerEvent = new CausedFocusEvent(lwFocusRequest.component, FocusEvent.FOCUS_GAINED, lwFocusRequest.temporary, currentFocusOwner == null ? lastFocusOwner : currentFocusOwner, lwFocusRequest.cause);
					if (currentFocusOwner != null) {
						((AWTEvent) currentFocusOwnerEvent).isPosted = true;
						caughtEx = dispatchAndCatchException(caughtEx, currentFocusOwner, currentFocusOwnerEvent);
					}
					((AWTEvent) newFocusOwnerEvent).isPosted = true;
					caughtEx = dispatchAndCatchException(caughtEx, lwFocusRequest.component, newFocusOwnerEvent);
					if (manager.getGlobalFocusOwner() == lwFocusRequest.component) {
						lastFocusOwner = lwFocusRequest.component;
					}
				}
			}
		} finally {
			clearingCurrentLightweightRequests = false;
			disableRestoreFocus = false;
			localLightweightRequests = null;
			allowSyncFocusRequests = true;
		}
		if (caughtEx instanceof RuntimeException) {
			throw (RuntimeException) caughtEx;
		} else if (caughtEx instanceof Error) {
			throw (Error) caughtEx;
		}
	}

	static FocusEvent retargetUnexpectedFocusEvent(FocusEvent fe) {
		synchronized (heavyweightRequests) {
			// Any other case represents a failure condition which we did
			// not expect. We need to clearFocusRequestList() and patch up
			// the event as best as possible.
			if (removeFirstRequest()) {
				return (FocusEvent) retargetFocusEvent(fe);
			}
			Component source = fe.getComponent();
			Component opposite = fe.getOppositeComponent();
			boolean temporary = false;
			if ((fe.getID() == FocusEvent.FOCUS_LOST) && ((opposite == null) || isTemporary(opposite, source))) {
				temporary = true;
			}
			return new CausedFocusEvent(source, fe.getID(), temporary, opposite, CausedFocusEvent.Cause.NATIVE_SYSTEM);
		}
	}

	static FocusEvent retargetFocusGained(FocusEvent fe) {
		assert (fe.getID() == FocusEvent.FOCUS_GAINED);
		Component currentFocusOwner = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
		Component source = fe.getComponent();
		Component opposite = fe.getOppositeComponent();
		Component nativeSource = getHeavyweight(source);
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getFirstHWRequest();
			if (hwFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
				return retargetUnexpectedFocusEvent(fe);
			}
			if ((source != null) && (nativeSource == null) && (hwFocusRequest != null)) {
				// if source w/o peer and
				// if source is equal to first lightweight
				// then we should correct source and nativeSource
				if (source == hwFocusRequest.getFirstLightweightRequest().component) {
					source = hwFocusRequest.heavyweight;
					nativeSource = source; // source is heavuweight itself
				}
			}
			if ((hwFocusRequest != null) && (nativeSource == hwFocusRequest.heavyweight)) {
				// Focus change as a result of a known call to requestFocus(),
				// or known click on a peer focusable heavyweight Component.
				heavyweightRequests.removeFirst();
				LightweightFocusRequest lwFocusRequest = hwFocusRequest.lightweightRequests.removeFirst();
				Component newSource = lwFocusRequest.component;
				if (currentFocusOwner != null) {
					/*
					 * Since we receive FOCUS_GAINED when current focus owner is
					 * not null, correcponding FOCUS_LOST is supposed to be
					 * lost. And so, we keep new focus owner to determine
					 * synthetic FOCUS_LOST event which will be generated by
					 * KeyboardFocusManager for this FOCUS_GAINED.
					 *
					 * This code based on knowledge of
					 * DefaultKeyboardFocusManager's implementation and might be
					 * not applicable for another KeyboardFocusManager.
					 */
					newFocusOwner = newSource;
				}
				boolean temporary = ((opposite == null) || isTemporary(newSource, opposite)) ? false : lwFocusRequest.temporary;
				if (hwFocusRequest.lightweightRequests.size() > 0) {
					currentLightweightRequests = hwFocusRequest.lightweightRequests;
					EventQueue.invokeLater(() -> processCurrentLightweightRequests());
				}
				// 'opposite' will be fixed by
				// DefaultKeyboardFocusManager.realOppositeComponent
				return new CausedFocusEvent(newSource, FocusEvent.FOCUS_GAINED, temporary, opposite, lwFocusRequest.cause);
			}
			if ((currentFocusOwner != null) && (currentFocusOwner.getContainingWindow() == source) && ((hwFocusRequest == null) || (source != hwFocusRequest.heavyweight))) {
				// Special case for FOCUS_GAINED in top-levels
				// If it arrives as the result of activation we should skip it
				// This event will not have appropriate request record and
				// on arrival there will be already some focus owner set.
				return new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_GAINED, false, null, CausedFocusEvent.Cause.ACTIVATION);
			}
			return retargetUnexpectedFocusEvent(fe);
		} // end synchronized(heavyweightRequests)
	}

	static FocusEvent retargetFocusLost(FocusEvent fe) {
		assert (fe.getID() == FocusEvent.FOCUS_LOST);
		Component currentFocusOwner = getCurrentKeyboardFocusManager().getGlobalFocusOwner();
		Component opposite = fe.getOppositeComponent();
		Component nativeOpposite = getHeavyweight(opposite);
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getFirstHWRequest();
			if (hwFocusRequest == HeavyweightFocusRequest.CLEAR_GLOBAL_FOCUS_OWNER) {
				if (currentFocusOwner != null) {
					// Call to KeyboardFocusManager.clearGlobalFocusOwner()
					heavyweightRequests.removeFirst();
					return new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, false, null, CausedFocusEvent.Cause.CLEAR_GLOBAL_FOCUS_OWNER);
				}
				// Otherwise, fall through to failure case below
			} else if (opposite == null) {
				// Focus leaving application
				if (currentFocusOwner != null) {
					return new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, true, null, CausedFocusEvent.Cause.ACTIVATION);
				} else {
					return fe;
				}
			} else if ((hwFocusRequest != null) && ((nativeOpposite == hwFocusRequest.heavyweight) || ((nativeOpposite == null) && (opposite == hwFocusRequest.getFirstLightweightRequest().component)))) {
				if (currentFocusOwner == null) {
					return fe;
				}
				// Focus change as a result of a known call to requestFocus(),
				// or click on a peer focusable heavyweight Component.
				// If a focus transfer is made across top-levels, then the
				// FOCUS_LOST event is always temporary, and the FOCUS_GAINED
				// event is always permanent. Otherwise, the stored temporary
				// value is honored.
				LightweightFocusRequest lwFocusRequest = hwFocusRequest.lightweightRequests.getFirst();
				boolean temporary = isTemporary(opposite, currentFocusOwner) ? true : lwFocusRequest.temporary;
				return new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, temporary, lwFocusRequest.component, lwFocusRequest.cause);
			} else if (focusedWindowChanged(opposite, currentFocusOwner)) {
				// If top-level changed there might be no focus request in a
				// list
				// But we know the opposite, we now it is temporary - dispatch
				// the event.
				if (!fe.isTemporary() && (currentFocusOwner != null)) {
					// Create copy of the event with only difference in
					// temporary parameter.
					fe = new CausedFocusEvent(currentFocusOwner, FocusEvent.FOCUS_LOST, true, opposite, CausedFocusEvent.Cause.ACTIVATION);
				}
				return fe;
			}
			return retargetUnexpectedFocusEvent(fe);
		} // end synchronized(heavyweightRequests)
	}

	static AWTEvent retargetFocusEvent(AWTEvent event) {
		if (clearingCurrentLightweightRequests) {
			return event;
		}
		KeyboardFocusManager manager = getCurrentKeyboardFocusManager();
		if (focusLog.isLoggable(PlatformLogger.Level.FINER)) {
			if ((event instanceof FocusEvent) || (event instanceof WindowEvent)) {
				focusLog.finer(">>> {0}", String.valueOf(event));
			}
			if (focusLog.isLoggable(PlatformLogger.Level.FINER) && (event instanceof KeyEvent)) {
				focusLog.finer("    focus owner is {0}", String.valueOf(manager.getGlobalFocusOwner()));
				focusLog.finer(">>> {0}", String.valueOf(event));
			}
		}
		synchronized (heavyweightRequests) {
			/*
			 * This code handles FOCUS_LOST event which is generated by
			 * DefaultKeyboardFocusManager for FOCUS_GAINED.
			 *
			 * This code based on knowledge of DefaultKeyboardFocusManager's
			 * implementation and might be not applicable for another
			 * KeyboardFocusManager.
			 *
			 * Fix for 4472032
			 */
			if ((newFocusOwner != null) && (event.getID() == FocusEvent.FOCUS_LOST)) {
				FocusEvent fe = (FocusEvent) event;
				if ((manager.getGlobalFocusOwner() == fe.getComponent()) && (fe.getOppositeComponent() == newFocusOwner)) {
					newFocusOwner = null;
					return event;
				}
			}
		}
		processCurrentLightweightRequests();
		switch (event.getID()) {
		case FocusEvent.FOCUS_GAINED: {
			event = retargetFocusGained((FocusEvent) event);
			break;
		}
		case FocusEvent.FOCUS_LOST: {
			event = retargetFocusLost((FocusEvent) event);
			break;
		}
		default:
			/* do nothing */
		}
		return event;
	}

	/**
	 * Clears markers queue This method is not intended to be overridden by
	 * KFM's. Only DefaultKeyboardFocusManager can implement it.
	 *
	 * @since 1.5
	 */
	void clearMarkers() {
	}

	static boolean removeFirstRequest() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getFirstHWRequest();
			if (hwFocusRequest != null) {
				heavyweightRequests.removeFirst();
				if (hwFocusRequest.lightweightRequests != null) {
					for (LightweightFocusRequest lightweightFocusRequest : hwFocusRequest.lightweightRequests) {
						manager.dequeueKeyEvents(-1, lightweightFocusRequest.component);
					}
				}
			}
			// Fix for 4799136 - clear type-ahead markers if requests queue is
			// empty
			// We do it here because this method is called only when problems
			// happen
			if (heavyweightRequests.size() == 0) {
				manager.clearMarkers();
			}
			return (heavyweightRequests.size() > 0);
		}
	}

	static void removeLastFocusRequest(Component heavyweight) {
		if (log.isLoggable(PlatformLogger.Level.FINE)) {
			if (heavyweight == null) {
				log.fine("Assertion (heavyweight != null) failed");
			}
		}
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		synchronized (heavyweightRequests) {
			HeavyweightFocusRequest hwFocusRequest = getLastHWRequest();
			if ((hwFocusRequest != null) && (hwFocusRequest.heavyweight == heavyweight)) {
				heavyweightRequests.removeLast();
			}
			// Fix for 4799136 - clear type-ahead markers if requests queue is
			// empty
			// We do it here because this method is called only when problems
			// happen
			if (heavyweightRequests.size() == 0) {
				manager.clearMarkers();
			}
		}
	}

	private static boolean focusedWindowChanged(Component to, Component from) {
		Window wto = SunToolkit.getContainingWindow(to);
		Window wfrom = SunToolkit.getContainingWindow(from);
		if ((wto == null) && (wfrom == null)) {
			return true;
		}
		if (wto == null) {
			return true;
		}
		if (wfrom == null) {
			return true;
		}
		return (wto != wfrom);
	}

	private static boolean isTemporary(Component to, Component from) {
		Window wto = SunToolkit.getContainingWindow(to);
		Window wfrom = SunToolkit.getContainingWindow(from);
		if ((wto == null) && (wfrom == null)) {
			return false;
		}
		if (wto == null) {
			return true;
		}
		if (wfrom == null) {
			return false;
		}
		return (wto != wfrom);
	}

	static Component getHeavyweight(Component comp) {
		if ((comp == null) || (comp.getPeer() == null)) {
			return null;
		} else if (comp.getPeer() instanceof LightweightPeer) {
			return comp.getNativeContainer();
		} else {
			return comp;
		}
	}

	static Field proxyActive;

	// Accessor to private field isProxyActive of KeyEvent
	private static boolean isProxyActiveImpl(KeyEvent e) {
		if (proxyActive == null) {
			proxyActive = AccessController.doPrivileged((PrivilegedAction<Field>) () -> {
				Field field = null;
				try {
					field = KeyEvent.class.getDeclaredField("isProxyActive");
					if (field != null) {
						field.setAccessible(true);
					}
				} catch (NoSuchFieldException nsf) {
					assert (false);
				}
				return field;
			});
		}
		try {
			return proxyActive.getBoolean(e);
		} catch (IllegalAccessException iae) {
			assert (false);
		}
		return false;
	}

	static boolean isProxyActive(KeyEvent e) {
		if (!GraphicsEnvironment.isHeadless()) {
			return isProxyActiveImpl(e);
		} else {
			return false;
		}
	}

	private static HeavyweightFocusRequest getLastHWRequest() {
		synchronized (heavyweightRequests) {
			return (heavyweightRequests.size() > 0) ? heavyweightRequests.getLast() : null;
		}
	}

	private static HeavyweightFocusRequest getFirstHWRequest() {
		synchronized (heavyweightRequests) {
			return (heavyweightRequests.size() > 0) ? heavyweightRequests.getFirst() : null;
		}
	}

	private static void checkReplaceKFMPermission() throws SecurityException {
		SecurityManager security = System.getSecurityManager();
		if (security != null) {
			if (replaceKeyboardFocusManagerPermission == null) {
				replaceKeyboardFocusManagerPermission = new AWTPermission("replaceKeyboardFocusManager");
			}
			security.checkPermission(replaceKeyboardFocusManagerPermission);
		}
	}

	private void checkKFMSecurity() throws SecurityException {
		if (this != getCurrentKeyboardFocusManager()) {
			checkReplaceKFMPermission();
		}
	}
}
