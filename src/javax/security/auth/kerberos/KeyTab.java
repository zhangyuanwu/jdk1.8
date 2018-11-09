package javax.security.auth.kerberos;

import java.io.File;
import java.security.AccessControlException;
import java.util.Objects;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KerberosSecrets;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.RealmException;

public final class KeyTab {
	private final File file;
	private final KerberosPrincipal princ;
	private final boolean bound;
	static {
		KerberosSecrets.setJavaxSecurityAuthKerberosAccess(new JavaxSecurityAuthKerberosAccessImpl());
	}

	private KeyTab(KerberosPrincipal princ, File file, boolean bound) {
		this.princ = princ;
		this.file = file;
		this.bound = bound;
	}

	public static KeyTab getInstance(File file) {
		if (file == null) {
			throw new NullPointerException("file must be non null");
		}
		return new KeyTab(null, file, true);
	}

	/**
	 * Returns an unbound {@code KeyTab} instance from a {@code File} object.
	 * <p>
	 * The result of this method is never null. This method only associates the
	 * returned {@code KeyTab} object with the file and does not read it.
	 *
	 * @param file
	 *            the keytab {@code File} object, must not be null
	 * @return the keytab instance
	 * @throws NullPointerException
	 *             if the file argument is null
	 * @since 1.8
	 */
	public static KeyTab getUnboundInstance(File file) {
		if (file == null) {
			throw new NullPointerException("file must be non null");
		}
		return new KeyTab(null, file, false);
	}

	/**
	 * Returns a {@code KeyTab} instance from a {@code File} object that is
	 * bound to the specified service principal.
	 * <p>
	 * The result of this method is never null. This method only associates the
	 * returned {@code KeyTab} object with the file and does not read it.
	 *
	 * @param princ
	 *            the bound service principal, must not be null
	 * @param file
	 *            the keytab {@code File} object, must not be null
	 * @return the keytab instance
	 * @throws NullPointerException
	 *             if either of the arguments is null
	 * @since 1.8
	 */
	public static KeyTab getInstance(KerberosPrincipal princ, File file) {
		if (princ == null) {
			throw new NullPointerException("princ must be non null");
		}
		if (file == null) {
			throw new NullPointerException("file must be non null");
		}
		return new KeyTab(princ, file, true);
	}

	/**
	 * Returns the default {@code KeyTab} instance that is bound to an unknown
	 * service principal.
	 * <p>
	 * The result of this method is never null. This method only associates the
	 * returned {@code KeyTab} object with the default keytab file and does not
	 * read it.
	 * <p>
	 * Developers should call {@link #getInstance(KerberosPrincipal)} when the
	 * bound service principal is known.
	 *
	 * @return the default keytab instance.
	 */
	public static KeyTab getInstance() {
		return new KeyTab(null, null, true);
	}

	/**
	 * Returns the default unbound {@code KeyTab} instance.
	 * <p>
	 * The result of this method is never null. This method only associates the
	 * returned {@code KeyTab} object with the default keytab file and does not
	 * read it.
	 *
	 * @return the default keytab instance
	 * @since 1.8
	 */
	public static KeyTab getUnboundInstance() {
		return new KeyTab(null, null, false);
	}

	/**
	 * Returns the default {@code KeyTab} instance that is bound to the
	 * specified service principal.
	 * <p>
	 * The result of this method is never null. This method only associates the
	 * returned {@code KeyTab} object with the default keytab file and does not
	 * read it.
	 *
	 * @param princ
	 *            the bound service principal, must not be null
	 * @return the default keytab instance
	 * @throws NullPointerException
	 *             if {@code princ} is null
	 * @since 1.8
	 */
	public static KeyTab getInstance(KerberosPrincipal princ) {
		if (princ == null) {
			throw new NullPointerException("princ must be non null");
		}
		return new KeyTab(princ, null, true);
	}

	// Takes a snapshot of the keytab content. This method is called by
	// JavaxSecurityAuthKerberosAccessImpl so no more private
	sun.security.krb5.internal.ktab.KeyTab takeSnapshot() {
		try {
			return sun.security.krb5.internal.ktab.KeyTab.getInstance(file);
		} catch (AccessControlException ace) {
			if (file != null) {
				// It's OK to show the name if caller specified it
				throw ace;
			} else {
				AccessControlException ace2 = new AccessControlException("Access to default keytab denied (modified exception)");
				ace2.setStackTrace(ace.getStackTrace());
				throw ace2;
			}
		}
	}

	/**
	 * Returns fresh keys for the given Kerberos principal.
	 * <p>
	 * Implementation of this method should make sure the returned keys match
	 * the latest content of the keytab file. The result is a newly created copy
	 * that can be modified by the caller without modifying the keytab object.
	 * The caller should {@link KerberosKey#destroy() destroy} the result keys
	 * after they are used.
	 * <p>
	 * Please note that the keytab file can be created after the {@code KeyTab}
	 * object is instantiated and its content may change over time. Therefore,
	 * an application should call this method only when it needs to use the
	 * keys. Any previous result from an earlier invocation could potentially be
	 * expired.
	 * <p>
	 * If there is any error (say, I/O error or format error) during the reading
	 * process of the KeyTab file, a saved result should be returned. If there
	 * is no saved result (say, this is the first time this method is called,
	 * or, all previous read attempts failed), an empty array should be
	 * returned. This can make sure the result is not drastically changed during
	 * the (probably slow) update of the keytab file.
	 * <p>
	 * Each time this method is called and the reading of the file succeeds with
	 * no exception (say, I/O error or file format error), the result should be
	 * saved for {@code principal}. The implementation can also save keys for
	 * other principals having keys in the same keytab object if convenient.
	 * <p>
	 * Any unsupported key read from the keytab is ignored and not included in
	 * the result.
	 * <p>
	 * If this keytab is bound to a specific principal, calling this method on
	 * another principal will return an empty array.
	 *
	 * @param principal
	 *            the Kerberos principal, must not be null.
	 * @return the keys (never null, may be empty)
	 * @throws NullPointerException
	 *             if the {@code principal} argument is null
	 * @throws SecurityException
	 *             if a security manager exists and the read access to the
	 *             keytab file is not permitted
	 */
	public KerberosKey[] getKeys(KerberosPrincipal principal) {
		try {
			if ((princ != null) && !principal.equals(princ)) {
				return new KerberosKey[0];
			}
			PrincipalName pn = new PrincipalName(principal.getName());
			EncryptionKey[] keys = takeSnapshot().readServiceKeys(pn);
			KerberosKey[] kks = new KerberosKey[keys.length];
			for (int i = 0; i < kks.length; i++) {
				Integer tmp = keys[i].getKeyVersionNumber();
				kks[i] = new KerberosKey(principal, keys[i].getBytes(), keys[i].getEType(), tmp == null ? 0 : tmp.intValue());
				keys[i].destroy();
			}
			return kks;
		} catch (RealmException re) {
			return new KerberosKey[0];
		}
	}

	EncryptionKey[] getEncryptionKeys(PrincipalName principal) {
		return takeSnapshot().readServiceKeys(principal);
	}

	/**
	 * Checks if the keytab file exists. Implementation of this method should
	 * make sure that the result matches the latest status of the keytab file.
	 * <p>
	 * The caller can use the result to determine if it should fallback to
	 * another mechanism to read the keys.
	 *
	 * @return true if the keytab file exists; false otherwise.
	 * @throws SecurityException
	 *             if a security manager exists and the read access to the
	 *             keytab file is not permitted
	 */
	public boolean exists() {
		return !takeSnapshot().isMissing();
	}

	public String toString() {
		String s = (file == null) ? "Default keytab" : file.toString();
		if (!bound) {
			return s;
		} else if (princ == null) {
			return s + " for someone";
		} else {
			return s + " for " + princ;
		}
	}

	public int hashCode() {
		return Objects.hash(file, princ, bound);
	}

	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof KeyTab)) {
			return false;
		}
		KeyTab otherKtab = (KeyTab) other;
		return Objects.equals(otherKtab.princ, princ) && Objects.equals(otherKtab.file, file) && (bound == otherKtab.bound);
	}

	/**
	 * Returns the service principal this {@code KeyTab} object is bound to.
	 * Returns {@code null} if it's not bound.
	 * <p>
	 * Please note the deprecated constructors create a KeyTab object bound for
	 * some unknown principal. In this case, this method also returns null. User
	 * can call {@link #isBound()} to verify this case.
	 *
	 * @return the service principal
	 * @since 1.8
	 */
	public KerberosPrincipal getPrincipal() {
		return princ;
	}

	/**
	 * Returns if the keytab is bound to a principal
	 *
	 * @return if the keytab is bound to a principal
	 * @since 1.8
	 */
	public boolean isBound() {
		return bound;
	}
}
