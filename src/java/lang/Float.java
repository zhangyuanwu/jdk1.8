package java.lang;

import sun.misc.FloatingDecimal;
import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;

public final class Float extends Number implements Comparable<Float> {
	public static final float POSITIVE_INFINITY = 1.0f / 0.0f;
	public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;
	public static final float NaN = 0.0f / 0.0f;
	public static final float MAX_VALUE = 0x1.fffffeP+127f;
	public static final float MIN_NORMAL = 0x1.0p-126f;
	public static final float MIN_VALUE = 0x0.000002P-126f;
	public static final int MAX_EXPONENT = 127;
	public static final int MIN_EXPONENT = -126;
	public static final int SIZE = 32;
	/**
	 * The number of bytes used to represent a {@code float} value.
	 *
	 * @since 1.8
	 */
	public static final int BYTES = SIZE / Byte.SIZE;
	@SuppressWarnings("unchecked")
	public static final Class<Float> TYPE = (Class<Float>) Class.getPrimitiveClass("float");

	public static String toString(float f) {
		return FloatingDecimal.toJavaFormatString(f);
	}

	public static String toHexString(float f) {
		if ((Math.abs(f) < FloatConsts.MIN_NORMAL) && (f != 0.0f)) {
			String s = Double.toHexString(Math.scalb((double) f, DoubleConsts.MIN_EXPONENT - FloatConsts.MIN_EXPONENT));
			return s.replaceFirst("p-1022$", "p-126");
		} else {
			return Double.toHexString(f);
		}
	}

	public static Float valueOf(String s) throws NumberFormatException {
		return new Float(parseFloat(s));
	}

	public static Float valueOf(float f) {
		return new Float(f);
	}

	public static float parseFloat(String s) throws NumberFormatException {
		return FloatingDecimal.parseFloat(s);
	}

	/**
	 * Returns {@code true} if the specified number is a Not-a-Number (NaN)
	 * value, {@code false} otherwise.
	 *
	 * @param v
	 *            the value to be tested.
	 * @return {@code true} if the argument is NaN; {@code false} otherwise.
	 */
	public static boolean isNaN(float v) {
		return (v != v);
	}

	/**
	 * Returns {@code true} if the specified number is infinitely large in
	 * magnitude, {@code false} otherwise.
	 *
	 * @param v
	 *            the value to be tested.
	 * @return {@code true} if the argument is positive infinity or negative
	 *         infinity; {@code false} otherwise.
	 */
	public static boolean isInfinite(float v) {
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	/**
	 * Returns {@code true} if the argument is a finite floating-point value;
	 * returns {@code false} otherwise (for NaN and infinity arguments).
	 *
	 * @param f
	 *            the {@code float} value to be tested
	 * @return {@code true} if the argument is a finite floating-point value,
	 *         {@code false} otherwise.
	 * @since 1.8
	 */
	public static boolean isFinite(float f) {
		return Math.abs(f) <= FloatConsts.MAX_VALUE;
	}

	/**
	 * The value of the Float.
	 *
	 * @serial
	 */
	private final float value;

	/**
	 * Constructs a newly allocated {@code Float} object that represents the
	 * primitive {@code float} argument.
	 *
	 * @param value
	 *            the value to be represented by the {@code Float}.
	 */
	public Float(float value) {
		this.value = value;
	}

	/**
	 * Constructs a newly allocated {@code Float} object that represents the
	 * argument converted to type {@code float}.
	 *
	 * @param value
	 *            the value to be represented by the {@code Float}.
	 */
	public Float(double value) {
		this.value = (float) value;
	}

	/**
	 * Constructs a newly allocated {@code Float} object that represents the
	 * floating-point value of type {@code float} represented by the string. The
	 * string is converted to a {@code float} value as if by the {@code valueOf}
	 * method.
	 *
	 * @param s
	 *            a string to be converted to a {@code Float}.
	 * @throws NumberFormatException
	 *             if the string does not contain a parsable number.
	 * @see java.lang.Float#valueOf(java.lang.String)
	 */
	public Float(String s) throws NumberFormatException {
		value = parseFloat(s);
	}

	/**
	 * Returns {@code true} if this {@code Float} value is a Not-a-Number (NaN),
	 * {@code false} otherwise.
	 *
	 * @return {@code true} if the value represented by this object is NaN;
	 *         {@code false} otherwise.
	 */
	public boolean isNaN() {
		return isNaN(value);
	}

	/**
	 * Returns {@code true} if this {@code Float} value is infinitely large in
	 * magnitude, {@code false} otherwise.
	 *
	 * @return {@code true} if the value represented by this object is positive
	 *         infinity or negative infinity; {@code false} otherwise.
	 */
	public boolean isInfinite() {
		return isInfinite(value);
	}

	/**
	 * Returns a string representation of this {@code Float} object. The
	 * primitive {@code float} value represented by this object is converted to
	 * a {@code String} exactly as if by the method {@code toString} of one
	 * argument.
	 *
	 * @return a {@code String} representation of this object.
	 * @see java.lang.Float#toString(float)
	 */
	@Override
	public String toString() {
		return Float.toString(value);
	}

	/**
	 * Returns the value of this {@code Float} as a {@code byte} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code float} value represented by this object converted to
	 *         type {@code byte}
	 * @jls 5.1.3 Narrowing Primitive Conversions
	 */
	@Override
	public byte byteValue() {
		return (byte) value;
	}

	/**
	 * Returns the value of this {@code Float} as a {@code short} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code float} value represented by this object converted to
	 *         type {@code short}
	 * @jls 5.1.3 Narrowing Primitive Conversions
	 * @since JDK1.1
	 */
	@Override
	public short shortValue() {
		return (short) value;
	}

	/**
	 * Returns the value of this {@code Float} as an {@code int} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code float} value represented by this object converted to
	 *         type {@code int}
	 * @jls 5.1.3 Narrowing Primitive Conversions
	 */
	@Override
	public int intValue() {
		return (int) value;
	}

	/**
	 * Returns value of this {@code Float} as a {@code long} after a narrowing
	 * primitive conversion.
	 *
	 * @return the {@code float} value represented by this object converted to
	 *         type {@code long}
	 * @jls 5.1.3 Narrowing Primitive Conversions
	 */
	@Override
	public long longValue() {
		return (long) value;
	}

	/**
	 * Returns the {@code float} value of this {@code Float} object.
	 *
	 * @return the {@code float} value represented by this object
	 */
	@Override
	public float floatValue() {
		return value;
	}

	/**
	 * Returns the value of this {@code Float} as a {@code double} after a
	 * widening primitive conversion.
	 *
	 * @return the {@code float} value represented by this object converted to
	 *         type {@code double}
	 * @jls 5.1.2 Widening Primitive Conversions
	 */
	@Override
	public double doubleValue() {
		return value;
	}

	/**
	 * Returns a hash code for this {@code Float} object. The result is the
	 * integer bit representation, exactly as produced by the method
	 * {@link #floatToIntBits(float)}, of the primitive {@code float} value
	 * represented by this {@code Float} object.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return Float.hashCode(value);
	}

	/**
	 * Returns a hash code for a {@code float} value; compatible with
	 * {@code Float.hashCode()}.
	 *
	 * @param value
	 *            the value to hash
	 * @return a hash code value for a {@code float} value.
	 * @since 1.8
	 */
	public static int hashCode(float value) {
		return floatToIntBits(value);
	}

	/**
	 *
	 * Compares this object against the specified object. The result is
	 * {@code true} if and only if the argument is not {@code null} and is a
	 * {@code Float} object that represents a {@code float} with the same value
	 * as the {@code float} represented by this object. For this purpose, two
	 * {@code float} values are considered to be the same if and only if the
	 * method {@link #floatToIntBits(float)} returns the identical {@code int}
	 * value when applied to each.
	 *
	 * <p>
	 * Note that in most cases, for two instances of class {@code Float},
	 * {@code f1} and {@code f2}, the value of {@code f1.equals(f2)} is
	 * {@code true} if and only if
	 *
	 * <blockquote>
	 *
	 * <pre>
	 * f1.floatValue() == f2.floatValue()
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * <p>
	 * also has the value {@code true}. However, there are two exceptions:
	 * <ul>
	 * <li>If {@code f1} and {@code f2} both represent {@code Float.NaN}, then
	 * the {@code equals} method returns {@code true}, even though
	 * {@code Float.NaN==Float.NaN} has the value {@code false}.
	 * <li>If {@code f1} represents {@code +0.0f} while {@code f2} represents
	 * {@code -0.0f}, or vice versa, the {@code equal} test has the value
	 * {@code false}, even though {@code 0.0f==-0.0f} has the value
	 * {@code true}.
	 * </ul>
	 *
	 * This definition allows hash tables to operate properly.
	 *
	 * @param obj
	 *            the object to be compared
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 * @see java.lang.Float#floatToIntBits(float)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Float) && (floatToIntBits(((Float) obj).value) == floatToIntBits(value));
	}

	/**
	 * Returns a representation of the specified floating-point value according
	 * to the IEEE 754 floating-point "single format" bit layout.
	 *
	 * <p>
	 * Bit 31 (the bit that is selected by the mask {@code 0x80000000})
	 * represents the sign of the floating-point number. Bits 30-23 (the bits
	 * that are selected by the mask {@code 0x7f800000}) represent the exponent.
	 * Bits 22-0 (the bits that are selected by the mask {@code 0x007fffff})
	 * represent the significand (sometimes called the mantissa) of the
	 * floating-point number.
	 *
	 * <p>
	 * If the argument is positive infinity, the result is {@code 0x7f800000}.
	 *
	 * <p>
	 * If the argument is negative infinity, the result is {@code 0xff800000}.
	 *
	 * <p>
	 * If the argument is NaN, the result is {@code 0x7fc00000}.
	 *
	 * <p>
	 * In all cases, the result is an integer that, when given to the
	 * {@link #intBitsToFloat(int)} method, will produce a floating-point value
	 * the same as the argument to {@code floatToIntBits} (except all NaN values
	 * are collapsed to a single "canonical" NaN value).
	 *
	 * @param value
	 *            a floating-point number.
	 * @return the bits that represent the floating-point number.
	 */
	public static int floatToIntBits(float value) {
		int result = floatToRawIntBits(value);
		// Check for NaN based on values of bit fields, maximum
		// exponent and nonzero significand.
		if (((result & FloatConsts.EXP_BIT_MASK) == FloatConsts.EXP_BIT_MASK) && ((result & FloatConsts.SIGNIF_BIT_MASK) != 0)) {
			result = 0x7fc00000;
		}
		return result;
	}

	/**
	 * Returns a representation of the specified floating-point value according
	 * to the IEEE 754 floating-point "single format" bit layout, preserving
	 * Not-a-Number (NaN) values.
	 *
	 * <p>
	 * Bit 31 (the bit that is selected by the mask {@code 0x80000000})
	 * represents the sign of the floating-point number. Bits 30-23 (the bits
	 * that are selected by the mask {@code 0x7f800000}) represent the exponent.
	 * Bits 22-0 (the bits that are selected by the mask {@code 0x007fffff})
	 * represent the significand (sometimes called the mantissa) of the
	 * floating-point number.
	 *
	 * <p>
	 * If the argument is positive infinity, the result is {@code 0x7f800000}.
	 *
	 * <p>
	 * If the argument is negative infinity, the result is {@code 0xff800000}.
	 *
	 * <p>
	 * If the argument is NaN, the result is the integer representing the actual
	 * NaN value. Unlike the {@code floatToIntBits} method,
	 * {@code floatToRawIntBits} does not collapse all the bit patterns encoding
	 * a NaN to a single "canonical" NaN value.
	 *
	 * <p>
	 * In all cases, the result is an integer that, when given to the
	 * {@link #intBitsToFloat(int)} method, will produce a floating-point value
	 * the same as the argument to {@code floatToRawIntBits}.
	 *
	 * @param value
	 *            a floating-point number.
	 * @return the bits that represent the floating-point number.
	 * @since 1.3
	 */
	public static native int floatToRawIntBits(float value);

	/**
	 * Returns the {@code float} value corresponding to a given bit
	 * representation. The argument is considered to be a representation of a
	 * floating-point value according to the IEEE 754 floating-point "single
	 * format" bit layout.
	 *
	 * <p>
	 * If the argument is {@code 0x7f800000}, the result is positive infinity.
	 *
	 * <p>
	 * If the argument is {@code 0xff800000}, the result is negative infinity.
	 *
	 * <p>
	 * If the argument is any value in the range {@code 0x7f800001} through
	 * {@code 0x7fffffff} or in the range {@code 0xff800001} through
	 * {@code 0xffffffff}, the result is a NaN. No IEEE 754 floating-point
	 * operation provided by Java can distinguish between two NaN values of the
	 * same type with different bit patterns. Distinct values of NaN are only
	 * distinguishable by use of the {@code Float.floatToRawIntBits} method.
	 *
	 * <p>
	 * In all other cases, let <i>s</i>, <i>e</i>, and <i>m</i> be three values
	 * that can be computed from the argument:
	 *
	 * <blockquote>
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	int s = ((bits >> 31) == 0) ? 1 : -1;
	 * 	int e = ((bits >> 23) & 0xff);
	 * 	int m = (e == 0) ? (bits & 0x7fffff) << 1 : (bits & 0x7fffff) | 0x800000;
	 * }
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * Then the floating-point result equals the value of the mathematical
	 * expression <i>s</i>&middot;<i>m</i>&middot;2<sup><i>e</i>-150</sup>.
	 *
	 * <p>
	 * Note that this method may not be able to return a {@code float} NaN with
	 * exactly same bit pattern as the {@code int} argument. IEEE 754
	 * distinguishes between two kinds of NaNs, quiet NaNs and <i>signaling
	 * NaNs</i>. The differences between the two kinds of NaN are generally not
	 * visible in Java. Arithmetic operations on signaling NaNs turn them into
	 * quiet NaNs with a different, but often similar, bit pattern. However, on
	 * some processors merely copying a signaling NaN also performs that
	 * conversion. In particular, copying a signaling NaN to return it to the
	 * calling method may perform this conversion. So {@code intBitsToFloat} may
	 * not be able to return a {@code float} with a signaling NaN bit pattern.
	 * Consequently, for some {@code int} values,
	 * {@code floatToRawIntBits(intBitsToFloat(start))} may <i>not</i> equal
	 * {@code start}. Moreover, which particular bit patterns represent
	 * signaling NaNs is platform dependent; although all NaN bit patterns,
	 * quiet or signaling, must be in the NaN range identified above.
	 *
	 * @param bits
	 *            an integer.
	 * @return the {@code float} floating-point value with the same bit pattern.
	 */
	public static native float intBitsToFloat(int bits);

	/**
	 * Compares two {@code Float} objects numerically. There are two ways in
	 * which comparisons performed by this method differ from those performed by
	 * the Java language numerical comparison operators
	 * ({@code <, <=, ==, >=, >}) when applied to primitive {@code float}
	 * values:
	 *
	 * <ul>
	 * <li>{@code Float.NaN} is considered by this method to be equal to itself
	 * and greater than all other {@code float} values (including
	 * {@code Float.POSITIVE_INFINITY}).
	 * <li>{@code 0.0f} is considered by this method to be greater than
	 * {@code -0.0f}.
	 * </ul>
	 *
	 * This ensures that the <i>natural ordering</i> of {@code Float} objects
	 * imposed by this method is <i>consistent with equals</i>.
	 *
	 * @param anotherFloat
	 *            the {@code Float} to be compared.
	 * @return the value {@code 0} if {@code anotherFloat} is numerically equal
	 *         to this {@code Float}; a value less than {@code 0} if this
	 *         {@code Float} is numerically less than {@code anotherFloat}; and
	 *         a value greater than {@code 0} if this {@code Float} is
	 *         numerically greater than {@code anotherFloat}.
	 *
	 * @since 1.2
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Float anotherFloat) {
		return Float.compare(value, anotherFloat.value);
	}

	/**
	 * Compares the two specified {@code float} values. The sign of the integer
	 * value returned is the same as that of the integer that would be returned
	 * by the call:
	 *
	 * <pre>
	 * new Float(f1).compareTo(new Float(f2))
	 * </pre>
	 *
	 * @param f1
	 *            the first {@code float} to compare.
	 * @param f2
	 *            the second {@code float} to compare.
	 * @return the value {@code 0} if {@code f1} is numerically equal to
	 *         {@code f2}; a value less than {@code 0} if {@code f1} is
	 *         numerically less than {@code f2}; and a value greater than
	 *         {@code 0} if {@code f1} is numerically greater than {@code f2}.
	 * @since 1.4
	 */
	public static int compare(float f1, float f2) {
		if (f1 < f2) {
			return -1; // Neither val is NaN, thisVal is smaller
		}
		if (f1 > f2) {
			return 1; // Neither val is NaN, thisVal is larger
		}
		// Cannot use floatToRawIntBits because of possibility of NaNs.
		int thisBits = Float.floatToIntBits(f1);
		int anotherBits = Float.floatToIntBits(f2);
		return (thisBits == anotherBits ? 0 : // Values are equal
				(thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
						1)); // (0.0, -0.0) or (NaN, !NaN)
	}

	/**
	 * Adds two {@code float} values together as per the + operator.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the sum of {@code a} and {@code b}
	 * @jls 4.2.4 Floating-Point Operations
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static float sum(float a, float b) {
		return a + b;
	}

	/**
	 * Returns the greater of two {@code float} values as if by calling
	 * {@link Math#max(float, float) Math.max}.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the greater of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static float max(float a, float b) {
		return Math.max(a, b);
	}

	/**
	 * Returns the smaller of two {@code float} values as if by calling
	 * {@link Math#min(float, float) Math.min}.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the smaller of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static float min(float a, float b) {
		return Math.min(a, b);
	}

	private static final long serialVersionUID = -2671257302660747028L;
}
