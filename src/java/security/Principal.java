package java.security;

import javax.security.auth.Subject;

public interface Principal {
	public boolean equals(Object another);

	public String toString();

	public int hashCode();

	public String getName();

	/**
	 * Returns true if the specified subject is implied by this principal.
	 *
	 * <p>
	 * The default implementation of this method returns true if {@code subject}
	 * is non-null and contains at least one principal that is equal to this
	 * principal.
	 *
	 * <p>
	 * Subclasses may override this with a different implementation, if
	 * necessary.
	 *
	 * @param subject
	 *            the {@code Subject}
	 * @return true if {@code subject} is non-null and is implied by this
	 *         principal, or false otherwise.
	 * @since 1.8
	 */
	public default boolean implies(Subject subject) {
		if (subject == null) {
			return false;
		}
		return subject.getPrincipals().contains(this);
	}
}
