package javax.lang.model.element;

public enum Modifier {
	PUBLIC, PROTECTED, PRIVATE, ABSTRACT,
	/**
	 * The modifier {@code default}
	 *
	 * @since 1.8
	 */
	DEFAULT, STATIC, FINAL, TRANSIENT, VOLATILE, SYNCHRONIZED, NATIVE, STRICTFP;
	public String toString() {
		return name().toLowerCase(java.util.Locale.US);
	}
}
