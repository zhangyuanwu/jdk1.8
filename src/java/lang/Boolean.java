package java.lang;

import java.io.Serializable;

public final class Boolean implements Serializable, Comparable<Boolean> {
	public static final Boolean TRUE = new Boolean(true);
	public static final Boolean FALSE = new Boolean(false);
	@SuppressWarnings("unchecked")
	public static final Class<Boolean> TYPE = (Class<Boolean>) Class.getPrimitiveClass("boolean");
	private final boolean value;
	private static final long serialVersionUID = -3665804199014368530L;

	public Boolean(boolean value) {
		this.value = value;
	}

	public Boolean(String s) {
		this(parseBoolean(s));
	}

	public static boolean parseBoolean(String s) {
		return ((s != null) && s.equalsIgnoreCase("true"));
	}

	public boolean booleanValue() {
		return value;
	}

	public static Boolean valueOf(boolean b) {
		return (b ? TRUE : FALSE);
	}

	public static Boolean valueOf(String s) {
		return parseBoolean(s) ? TRUE : FALSE;
	}

	public static String toString(boolean b) {
		return b ? "true" : "false";
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

	@Override
	public int hashCode() {
		return Boolean.hashCode(value);
	}

	/**
	 * 返回{@code boolean}值的哈希码; 与{@code Boolean.hashCode()}兼容。
	 *
	 * @param value
	 *            哈希值
	 * @return {@code boolean}值的哈希码值。
	 * @since 1.8
	 */
	public static int hashCode(boolean value) {
		return value ? 1231 : 1237;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Boolean) {
			return value == ((Boolean) obj).booleanValue();
		}
		return false;
	}

	public static boolean getBoolean(String name) {
		boolean result = false;
		try {
			result = parseBoolean(System.getProperty(name));
		} catch (IllegalArgumentException | NullPointerException e) {
		}
		return result;
	}

	@Override
	public int compareTo(Boolean b) {
		return compare(value, b.value);
	}

	public static int compare(boolean x, boolean y) {
		return (x == y) ? 0 : (x ? 1 : -1);
	}

	/**
	 * 返回将逻辑AND运算符应用于指定的{@code boolean}操作数的结果。
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}的逻辑AND
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static boolean logicalAnd(boolean a, boolean b) {
		return a && b;
	}

	/**
	 * 返回将逻辑OR运算符应用于指定的{@code boolean}操作数的结果。
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}的逻辑OR
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static boolean logicalOr(boolean a, boolean b) {
		return a || b;
	}

	/**
	 * 返回将逻辑XOR运算符应用于指定的{@code boolean}操作数的结果。
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}的逻辑异或
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static boolean logicalXor(boolean a, boolean b) {
		return a ^ b;
	}
}
