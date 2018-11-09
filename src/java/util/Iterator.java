package java.util;

import java.util.function.Consumer;

public interface Iterator<E> {
	boolean hasNext();

	E next();

	default void remove() {
		throw new UnsupportedOperationException("remove");
	}

	/**
	 * Performs the given action for each remaining element until all elements
	 * have been processed or the action throws an exception. Actions are
	 * performed in the order of iteration, if that order is specified.
	 * Exceptions thrown by the action are relayed to the caller.
	 *
	 * @implSpec
	 *           <p>
	 *           The default implementation behaves as if:
	 *
	 *           <pre>
	 * {@code
	 *     while (hasNext())
	 *         action.accept(next());
	 * }
	 *           </pre>
	 *
	 * @param action
	 *            The action to be performed for each element
	 * @throws NullPointerException
	 *             if the specified action is null
	 * @since 1.8
	 */
	default void forEachRemaining(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		while (hasNext()) {
			action.accept(next());
		}
	}
}
