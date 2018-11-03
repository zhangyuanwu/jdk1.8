package java.lang;

import sun.misc.FloatingDecimal;
import sun.misc.DoubleConsts;

public final class Double extends Number implements Comparable<Double> {
	public static final double POSITIVE_INFINITY = 1.0 / 0.0;
	public static final double NEGATIVE_INFINITY = -1.0 / 0.0;
	public static final double NaN = 0.0d / 0.0;
	public static final double MAX_VALUE = 0x1.fffffffffffffP+1023;
	public static final double MIN_NORMAL = 0x1.0p-1022;
	public static final double MIN_VALUE = 0x0.0000000000001P-1022;
	public static final int MAX_EXPONENT = 1023;
	public static final int MIN_EXPONENT = -1022;
	public static final int SIZE = 64;
	/**
	 * The number of bytes used to represent a {@code double} value.
	 *
	 * @since 1.8
	 */
	public static final int BYTES = SIZE / Byte.SIZE;
	@SuppressWarnings("unchecked")
	public static final Class<Double> TYPE = (Class<Double>) Class.getPrimitiveClass("double");

	public static String toString(double d) {
		return FloatingDecimal.toJavaFormatString(d);
	}

	public static String toHexString(double d) {
		if (!isFinite(d)) {
			return Double.toString(d);
		} else {
			StringBuilder answer = new StringBuilder(24);
			if (Math.copySign(1.0, d) == -1.0) {
				answer.append("-");
			}
			answer.append("0x");
			d = Math.abs(d);
			if (d == 0.0) {
				answer.append("0.0p0");
			} else {
				boolean subnormal = (d < DoubleConsts.MIN_NORMAL);
				long signifBits = (Double.doubleToLongBits(d) & DoubleConsts.SIGNIF_BIT_MASK) | 0x1000000000000000L;
				answer.append(subnormal ? "0." : "1.");
				String signif = Long.toHexString(signifBits).substring(3, 16);
				answer.append(signif.equals("0000000000000") ? "0" : signif.replaceFirst("0{1,12}$", ""));
				answer.append('p');
				answer.append(subnormal ? DoubleConsts.MIN_EXPONENT : Math.getExponent(d));
			}
			return answer.toString();
		}
	}

	/**
	 * Returns a {@code Double} object holding the {@code double} value
	 * represented by the argument string {@code s}.
	 *
	 * <p>
	 * If {@code s} is {@code null}, then a {@code NullPointerException} is
	 * thrown.
	 *
	 * <p>
	 * Leading and trailing whitespace characters in {@code s} are ignored.
	 * Whitespace is removed as if by the {@link String#trim} method; that is,
	 * both ASCII space and control characters are removed. The rest of
	 * {@code s} should constitute a <i>FloatValue</i> as described by the
	 * lexical syntax rules:
	 *
	 * <blockquote>
	 * <dl>
	 * <dt><i>FloatValue:</i>
	 * <dd><i>Sign<sub>opt</sub></i> {@code NaN}
	 * <dd><i>Sign<sub>opt</sub></i> {@code Infinity}
	 * <dd><i>Sign<sub>opt</sub> FloatingPointLiteral</i>
	 * <dd><i>Sign<sub>opt</sub> HexFloatingPointLiteral</i>
	 * <dd><i>SignedInteger</i>
	 * </dl>
	 *
	 * <dl>
	 * <dt><i>HexFloatingPointLiteral</i>:
	 * <dd><i>HexSignificand BinaryExponent FloatTypeSuffix<sub>opt</sub></i>
	 * </dl>
	 *
	 * <dl>
	 * <dt><i>HexSignificand:</i>
	 * <dd><i>HexNumeral</i>
	 * <dd><i>HexNumeral</i> {@code .}
	 * <dd>{@code 0x} <i>HexDigits<sub>opt</sub> </i>{@code .}<i> HexDigits</i>
	 * <dd>{@code 0X}<i> HexDigits<sub>opt</sub> </i>{@code .} <i>HexDigits</i>
	 * </dl>
	 *
	 * <dl>
	 * <dt><i>BinaryExponent:</i>
	 * <dd><i>BinaryExponentIndicator SignedInteger</i>
	 * </dl>
	 *
	 * <dl>
	 * <dt><i>BinaryExponentIndicator:</i>
	 * <dd>{@code p}
	 * <dd>{@code P}
	 * </dl>
	 *
	 * </blockquote>
	 *
	 * where <i>Sign</i>, <i>FloatingPointLiteral</i>, <i>HexNumeral</i>,
	 * <i>HexDigits</i>, <i>SignedInteger</i> and <i>FloatTypeSuffix</i> are as
	 * defined in the lexical structure sections of <cite>The Java&trade;
	 * Language Specification</cite>, except that underscores are not accepted
	 * between digits. If {@code s} does not have the form of a
	 * <i>FloatValue</i>, then a {@code NumberFormatException} is thrown.
	 * Otherwise, {@code s} is regarded as representing an exact decimal value
	 * in the usual "computerized scientific notation" or as an exact
	 * hexadecimal value; this exact numerical value is then conceptually
	 * converted to an "infinitely precise" binary value that is then rounded to
	 * type {@code double} by the usual round-to-nearest rule of IEEE 754
	 * floating-point arithmetic, which includes preserving the sign of a zero
	 * value.
	 *
	 * Note that the round-to-nearest rule also implies overflow and underflow
	 * behaviour; if the exact value of {@code s} is large enough in magnitude
	 * (greater than or equal to ({@link #MAX_VALUE} + {@link Math#ulp(double)
	 * ulp(MAX_VALUE)}/2), rounding to {@code double} will result in an infinity
	 * and if the exact value of {@code s} is small enough in magnitude (less
	 * than or equal to {@link #MIN_VALUE}/2), rounding to float will result in
	 * a zero.
	 *
	 * Finally, after rounding a {@code Double} object representing this
	 * {@code double} value is returned.
	 *
	 * <p>
	 * To interpret localized string representations of a floating-point value,
	 * use subclasses of {@link java.text.NumberFormat}.
	 *
	 * <p>
	 * Note that trailing format specifiers, specifiers that determine the type
	 * of a floating-point literal ({@code 1.0f} is a {@code float} value;
	 * {@code 1.0d} is a {@code double} value), do <em>not</em> influence the
	 * results of this method. In other words, the numerical value of the input
	 * string is converted directly to the target floating-point type. The
	 * two-step sequence of conversions, string to {@code float} followed by
	 * {@code float} to {@code double}, is <em>not</em> equivalent to converting
	 * a string directly to {@code double}. For example, the {@code float}
	 * literal {@code 0.1f} is equal to the {@code double} value
	 * {@code 0.10000000149011612}; the {@code float} literal {@code 0.1f}
	 * represents a different numerical value than the {@code double} literal
	 * {@code 0.1}. (The numerical value 0.1 cannot be exactly represented in a
	 * binary floating-point number.)
	 *
	 * <p>
	 * To avoid calling this method on an invalid string and having a
	 * {@code NumberFormatException} be thrown, the regular expression below can
	 * be used to screen the input string:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	final String Digits = "(\\p{Digit}+)";
	 * 	final String HexDigits = "(\\p{XDigit}+)";
	 * 	// an exponent is 'e' or 'E' followed by an optionally
	 * 	// signed decimal integer.
	 * 	final String Exp = "[eE][+-]?" + Digits;
	 * 	final String fpRegex = ("[\\x00-\\x20]*" + // Optional leading
	 * 												// "whitespace"
	 * 			"[+-]?(" + // Optional sign character
	 * 			"NaN|" + // "NaN" string
	 * 			"Infinity|" + // "Infinity" string
	 *
	 * 			// A decimal floating-point string representing a finite
	 * 			// positive
	 * 			// number without a leading sign has at most five basic
	 * 			// pieces:
	 * 			// Digits . Digits ExponentPart FloatTypeSuffix
	 * 			//
	 * 			// Since this method allows integer-only strings as input
	 * 			// in addition to strings of floating-point literals, the
	 * 			// two sub-patterns below are simplifications of the grammar
	 * 			// productions from section 3.10.2 of
	 * 			// The Java Language Specification.
	 *
	 * 			// Digits ._opt Digits_opt ExponentPart_opt
	 * 			// FloatTypeSuffix_opt
	 * 			"(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +
	 *
	 * 			// . Digits ExponentPart_opt FloatTypeSuffix_opt
	 * 			"(\\.(" + Digits + ")(" + Exp + ")?)|" +
	 *
	 * 			// Hexadecimal strings
	 * 			"((" +
	 * 			// 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
	 * 			"(0[xX]" + HexDigits + "(\\.)?)|" +
	 *
	 * 			// 0[xX] HexDigits_opt . HexDigits BinaryExponent
	 * 			// FloatTypeSuffix_opt
	 * 			"(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
	 *
	 * 			")[pP][+-]?" + Digits + "))" + "[fFdD]?))" + "[\\x00-\\x20]*");// Optional
	 * 																			// trailing
	 * 																			// "whitespace"
	 *
	 * 	if (Pattern.matches(fpRegex, myString))
	 * 		Double.valueOf(myString); // Will not throw NumberFormatException
	 * 	else {
	 * 		// Perform suitable alternative action
	 * 	}
	 * }
	 * </pre>
	 *
	 * @param s
	 *            the string to be parsed.
	 * @return a {@code Double} object holding the value represented by the
	 *         {@code String} argument.
	 * @throws NumberFormatException
	 *             if the string does not contain a parsable number.
	 */
	public static Double valueOf(String s) throws NumberFormatException {
		return new Double(parseDouble(s));
	}

	/**
	 * Returns a {@code Double} instance representing the specified
	 * {@code double} value. If a new {@code Double} instance is not required,
	 * this method should generally be used in preference to the constructor
	 * {@link #Double(double)}, as this method is likely to yield significantly
	 * better space and time performance by caching frequently requested values.
	 *
	 * @param d
	 *            a double value.
	 * @return a {@code Double} instance representing {@code d}.
	 * @since 1.5
	 */
	public static Double valueOf(double d) {
		return new Double(d);
	}

	/**
	 * Returns a new {@code double} initialized to the value represented by the
	 * specified {@code String}, as performed by the {@code valueOf} method of
	 * class {@code Double}.
	 *
	 * @param s
	 *            the string to be parsed.
	 * @return the {@code double} value represented by the string argument.
	 * @throws NullPointerException
	 *             if the string is null
	 * @throws NumberFormatException
	 *             if the string does not contain a parsable {@code double}.
	 * @see java.lang.Double#valueOf(String)
	 * @since 1.2
	 */
	public static double parseDouble(String s) throws NumberFormatException {
		return FloatingDecimal.parseDouble(s);
	}

	/**
	 * Returns {@code true} if the specified number is a Not-a-Number (NaN)
	 * value, {@code false} otherwise.
	 *
	 * @param v
	 *            the value to be tested.
	 * @return {@code true} if the value of the argument is NaN; {@code false}
	 *         otherwise.
	 */
	public static boolean isNaN(double v) {
		return (v != v);
	}

	/**
	 * Returns {@code true} if the specified number is infinitely large in
	 * magnitude, {@code false} otherwise.
	 *
	 * @param v
	 *            the value to be tested.
	 * @return {@code true} if the value of the argument is positive infinity or
	 *         negative infinity; {@code false} otherwise.
	 */
	public static boolean isInfinite(double v) {
		return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
	}

	/**
	 * Returns {@code true} if the argument is a finite floating-point value;
	 * returns {@code false} otherwise (for NaN and infinity arguments).
	 *
	 * @param d
	 *            the {@code double} value to be tested
	 * @return {@code true} if the argument is a finite floating-point value,
	 *         {@code false} otherwise.
	 * @since 1.8
	 */
	public static boolean isFinite(double d) {
		return Math.abs(d) <= DoubleConsts.MAX_VALUE;
	}

	/**
	 * The value of the Double.
	 *
	 * @serial
	 */
	private final double value;

	/**
	 * Constructs a newly allocated {@code Double} object that represents the
	 * primitive {@code double} argument.
	 *
	 * @param value
	 *            the value to be represented by the {@code Double}.
	 */
	public Double(double value) {
		this.value = value;
	}

	/**
	 * Constructs a newly allocated {@code Double} object that represents the
	 * floating-point value of type {@code double} represented by the string.
	 * The string is converted to a {@code double} value as if by the
	 * {@code valueOf} method.
	 *
	 * @param s
	 *            a string to be converted to a {@code Double}.
	 * @throws NumberFormatException
	 *             if the string does not contain a parsable number.
	 * @see java.lang.Double#valueOf(java.lang.String)
	 */
	public Double(String s) throws NumberFormatException {
		value = parseDouble(s);
	}

	/**
	 * Returns {@code true} if this {@code Double} value is a Not-a-Number
	 * (NaN), {@code false} otherwise.
	 *
	 * @return {@code true} if the value represented by this object is NaN;
	 *         {@code false} otherwise.
	 */
	public boolean isNaN() {
		return isNaN(value);
	}

	/**
	 * Returns {@code true} if this {@code Double} value is infinitely large in
	 * magnitude, {@code false} otherwise.
	 *
	 * @return {@code true} if the value represented by this object is positive
	 *         infinity or negative infinity; {@code false} otherwise.
	 */
	public boolean isInfinite() {
		return isInfinite(value);
	}

	/**
	 * Returns a string representation of this {@code Double} object. The
	 * primitive {@code double} value represented by this object is converted to
	 * a string exactly as if by the method {@code toString} of one argument.
	 *
	 * @return a {@code String} representation of this object.
	 * @see java.lang.Double#toString(double)
	 */
	@Override
	public String toString() {
		return toString(value);
	}

	/**
	 * Returns the value of this {@code Double} as a {@code byte} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code double} value represented by this object converted to
	 *         type {@code byte} 5.1.3 Narrowing Primitive Conversions
	 * @since JDK1.1
	 */
	@Override
	public byte byteValue() {
		return (byte) value;
	}

	/**
	 * Returns the value of this {@code Double} as a {@code short} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code double} value represented by this object converted to
	 *         type {@code short} 5.1.3 Narrowing Primitive Conversions
	 * @since JDK1.1
	 */
	@Override
	public short shortValue() {
		return (short) value;
	}

	/**
	 * Returns the value of this {@code Double} as an {@code int} after a
	 * narrowing primitive conversion.
	 *
	 * 5.1.3 Narrowing Primitive Conversions
	 *
	 * @return the {@code double} value represented by this object converted to
	 *         type {@code int}
	 */
	@Override
	public int intValue() {
		return (int) value;
	}

	/**
	 * Returns the value of this {@code Double} as a {@code long} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code double} value represented by this object converted to
	 *         type {@code long} 5.1.3 Narrowing Primitive Conversions
	 */
	@Override
	public long longValue() {
		return (long) value;
	}

	/**
	 * Returns the value of this {@code Double} as a {@code float} after a
	 * narrowing primitive conversion.
	 *
	 * @return the {@code double} value represented by this object converted to
	 *         type {@code float} 5.1.3 Narrowing Primitive Conversions
	 * @since JDK1.0
	 */
	@Override
	public float floatValue() {
		return (float) value;
	}

	/**
	 * Returns the {@code double} value of this {@code Double} object.
	 *
	 * @return the {@code double} value represented by this object
	 */
	@Override
	public double doubleValue() {
		return value;
	}

	/**
	 * Returns a hash code for this {@code Double} object. The result is the
	 * exclusive OR of the two halves of the {@code long} integer bit
	 * representation, exactly as produced by the method
	 * {@link #doubleToLongBits(double)}, of the primitive {@code double} value
	 * represented by this {@code Double} object. That is, the hash code is the
	 * value of the expression:
	 *
	 * <blockquote> {@code (int)(v^(v>>>32))} </blockquote>
	 *
	 * where {@code v} is defined by:
	 *
	 * <blockquote>
	 * {@code long v = Double.doubleToLongBits(this.doubleValue());}
	 * </blockquote>
	 *
	 * @return a {@code hash code} value for this object.
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	/**
	 * Returns a hash code for a {@code double} value; compatible with
	 * {@code Double.hashCode()}.
	 *
	 * @param value
	 *            the value to hash
	 * @return a hash code value for a {@code double} value.
	 * @since 1.8
	 */
	public static int hashCode(double value) {
		long bits = doubleToLongBits(value);
		return (int) (bits ^ (bits >>> 32));
	}

	/**
	 * Compares this object against the specified object. The result is
	 * {@code true} if and only if the argument is not {@code null} and is a
	 * {@code Double} object that represents a {@code double} that has the same
	 * value as the {@code double} represented by this object. For this purpose,
	 * two {@code double} values are considered to be the same if and only if
	 * the method {@link #doubleToLongBits(double)} returns the identical
	 * {@code long} value when applied to each.
	 *
	 * <p>
	 * Note that in most cases, for two instances of class {@code Double},
	 * {@code d1} and {@code d2}, the value of {@code d1.equals(d2)} is
	 * {@code true} if and only if
	 *
	 * <blockquote> {@code d1.doubleValue() == d2.doubleValue()} </blockquote>
	 *
	 * <p>
	 * also has the value {@code true}. However, there are two exceptions:
	 * <ul>
	 * <li>If {@code d1} and {@code d2} both represent {@code Double.NaN}, then
	 * the {@code equals} method returns {@code true}, even though
	 * {@code Double.NaN==Double.NaN} has the value {@code false}.
	 * <li>If {@code d1} represents {@code +0.0} while {@code d2} represents
	 * {@code -0.0}, or vice versa, the {@code equal} test has the value
	 * {@code false}, even though {@code +0.0==-0.0} has the value {@code true}.
	 * </ul>
	 * This definition allows hash tables to operate properly.
	 *
	 * @param obj
	 *            the object to compare with.
	 * @return {@code true} if the objects are the same; {@code false}
	 *         otherwise.
	 * @see java.lang.Double#doubleToLongBits(double)
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Double) && (doubleToLongBits(((Double) obj).value) == doubleToLongBits(value));
	}

	/**
	 * Returns a representation of the specified floating-point value according
	 * to the IEEE 754 floating-point "double format" bit layout.
	 *
	 * <p>
	 * Bit 63 (the bit that is selected by the mask {@code 0x8000000000000000L})
	 * represents the sign of the floating-point number. Bits 62-52 (the bits
	 * that are selected by the mask {@code 0x7ff0000000000000L}) represent the
	 * exponent. Bits 51-0 (the bits that are selected by the mask
	 * {@code 0x000fffffffffffffL}) represent the significand (sometimes called
	 * the mantissa) of the floating-point number.
	 *
	 * <p>
	 * If the argument is positive infinity, the result is
	 * {@code 0x7ff0000000000000L}.
	 *
	 * <p>
	 * If the argument is negative infinity, the result is
	 * {@code 0xfff0000000000000L}.
	 *
	 * <p>
	 * If the argument is NaN, the result is {@code 0x7ff8000000000000L}.
	 *
	 * <p>
	 * In all cases, the result is a {@code long} integer that, when given to
	 * the {@link #longBitsToDouble(long)} method, will produce a floating-point
	 * value the same as the argument to {@code doubleToLongBits} (except all
	 * NaN values are collapsed to a single "canonical" NaN value).
	 *
	 * @param value
	 *            a {@code double} precision floating-point number.
	 * @return the bits that represent the floating-point number.
	 */
	public static long doubleToLongBits(double value) {
		long result = doubleToRawLongBits(value);
		// Check for NaN based on values of bit fields, maximum
		// exponent and nonzero significand.
		if (((result & DoubleConsts.EXP_BIT_MASK) == DoubleConsts.EXP_BIT_MASK) && ((result & DoubleConsts.SIGNIF_BIT_MASK) != 0L)) {
			result = 0x7ff8000000000000L;
		}
		return result;
	}

	/**
	 * Returns a representation of the specified floating-point value according
	 * to the IEEE 754 floating-point "double format" bit layout, preserving
	 * Not-a-Number (NaN) values.
	 *
	 * <p>
	 * Bit 63 (the bit that is selected by the mask {@code 0x8000000000000000L})
	 * represents the sign of the floating-point number. Bits 62-52 (the bits
	 * that are selected by the mask {@code 0x7ff0000000000000L}) represent the
	 * exponent. Bits 51-0 (the bits that are selected by the mask
	 * {@code 0x000fffffffffffffL}) represent the significand (sometimes called
	 * the mantissa) of the floating-point number.
	 *
	 * <p>
	 * If the argument is positive infinity, the result is
	 * {@code 0x7ff0000000000000L}.
	 *
	 * <p>
	 * If the argument is negative infinity, the result is
	 * {@code 0xfff0000000000000L}.
	 *
	 * <p>
	 * If the argument is NaN, the result is the {@code long} integer
	 * representing the actual NaN value. Unlike the {@code doubleToLongBits}
	 * method, {@code doubleToRawLongBits} does not collapse all the bit
	 * patterns encoding a NaN to a single "canonical" NaN value.
	 *
	 * <p>
	 * In all cases, the result is a {@code long} integer that, when given to
	 * the {@link #longBitsToDouble(long)} method, will produce a floating-point
	 * value the same as the argument to {@code doubleToRawLongBits}.
	 *
	 * @param value
	 *            a {@code double} precision floating-point number.
	 * @return the bits that represent the floating-point number.
	 * @since 1.3
	 */
	public static native long doubleToRawLongBits(double value);

	/**
	 * Returns the {@code double} value corresponding to a given bit
	 * representation. The argument is considered to be a representation of a
	 * floating-point value according to the IEEE 754 floating-point "double
	 * format" bit layout.
	 *
	 * <p>
	 * If the argument is {@code 0x7ff0000000000000L}, the result is positive
	 * infinity.
	 *
	 * <p>
	 * If the argument is {@code 0xfff0000000000000L}, the result is negative
	 * infinity.
	 *
	 * <p>
	 * If the argument is any value in the range {@code 0x7ff0000000000001L}
	 * through {@code 0x7fffffffffffffffL} or in the range
	 * {@code 0xfff0000000000001L} through {@code 0xffffffffffffffffL}, the
	 * result is a NaN. No IEEE 754 floating-point operation provided by Java
	 * can distinguish between two NaN values of the same type with different
	 * bit patterns. Distinct values of NaN are only distinguishable by use of
	 * the {@code Double.doubleToRawLongBits} method.
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
	 * 	int s = ((bits >> 63) == 0) ? 1 : -1;
	 * 	int e = (int) ((bits >> 52) & 0x7ffL);
	 * 	long m = (e == 0) ? (bits & 0xfffffffffffffL) << 1 : (bits & 0xfffffffffffffL) | 0x10000000000000L;
	 * }
	 * </pre>
	 *
	 * </blockquote>
	 *
	 * Then the floating-point result equals the value of the mathematical
	 * expression <i>s</i>&middot;<i>m</i>&middot;2<sup><i>e</i>-1075</sup>.
	 *
	 * <p>
	 * Note that this method may not be able to return a {@code double} NaN with
	 * exactly same bit pattern as the {@code long} argument. IEEE 754
	 * distinguishes between two kinds of NaNs, quiet NaNs and <i>signaling
	 * NaNs</i>. The differences between the two kinds of NaN are generally not
	 * visible in Java. Arithmetic operations on signaling NaNs turn them into
	 * quiet NaNs with a different, but often similar, bit pattern. However, on
	 * some processors merely copying a signaling NaN also performs that
	 * conversion. In particular, copying a signaling NaN to return it to the
	 * calling method may perform this conversion. So {@code longBitsToDouble}
	 * may not be able to return a {@code double} with a signaling NaN bit
	 * pattern. Consequently, for some {@code long} values,
	 * {@code doubleToRawLongBits(longBitsToDouble(start))} may <i>not</i> equal
	 * {@code start}. Moreover, which particular bit patterns represent
	 * signaling NaNs is platform dependent; although all NaN bit patterns,
	 * quiet or signaling, must be in the NaN range identified above.
	 *
	 * @param bits
	 *            any {@code long} integer.
	 * @return the {@code double} floating-point value with the same bit
	 *         pattern.
	 */
	public static native double longBitsToDouble(long bits);

	/**
	 * Compares two {@code Double} objects numerically. There are two ways in
	 * which comparisons performed by this method differ from those performed by
	 * the Java language numerical comparison operators
	 * ({@code <, <=, ==, >=, >}) when applied to primitive {@code double}
	 * values:
	 * <ul>
	 * <li>{@code Double.NaN} is considered by this method to be equal to itself
	 * and greater than all other {@code double} values (including
	 * {@code Double.POSITIVE_INFINITY}).
	 * <li>{@code 0.0d} is considered by this method to be greater than
	 * {@code -0.0d}.
	 * </ul>
	 * This ensures that the <i>natural ordering</i> of {@code Double} objects
	 * imposed by this method is <i>consistent with equals</i>.
	 *
	 * @param anotherDouble
	 *            the {@code Double} to be compared.
	 * @return the value {@code 0} if {@code anotherDouble} is numerically equal
	 *         to this {@code Double}; a value less than {@code 0} if this
	 *         {@code Double} is numerically less than {@code anotherDouble};
	 *         and a value greater than {@code 0} if this {@code Double} is
	 *         numerically greater than {@code anotherDouble}.
	 *
	 * @since 1.2
	 */
	@Override
	public int compareTo(Double anotherDouble) {
		return Double.compare(value, anotherDouble.value);
	}

	/**
	 * Compares the two specified {@code double} values. The sign of the integer
	 * value returned is the same as that of the integer that would be returned
	 * by the call:
	 *
	 * <pre>
	 * new Double(d1).compareTo(new Double(d2))
	 * </pre>
	 *
	 * @param d1
	 *            the first {@code double} to compare
	 * @param d2
	 *            the second {@code double} to compare
	 * @return the value {@code 0} if {@code d1} is numerically equal to
	 *         {@code d2}; a value less than {@code 0} if {@code d1} is
	 *         numerically less than {@code d2}; and a value greater than
	 *         {@code 0} if {@code d1} is numerically greater than {@code d2}.
	 * @since 1.4
	 */
	public static int compare(double d1, double d2) {
		if (d1 < d2) {
			return -1; // Neither val is NaN, thisVal is smaller
		}
		if (d1 > d2) {
			return 1; // Neither val is NaN, thisVal is larger
		}
		// Cannot use doubleToRawLongBits because of possibility of NaNs.
		long thisBits = Double.doubleToLongBits(d1);
		long anotherBits = Double.doubleToLongBits(d2);
		return (thisBits == anotherBits ? 0 : // Values are equal
				(thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
						1)); // (0.0, -0.0) or (NaN, !NaN)
	}

	/**
	 * Adds two {@code double} values together as per the + operator.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the sum of {@code a} and {@code b} 4.2.4 Floating-Point
	 *         Operations
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static double sum(double a, double b) {
		return a + b;
	}

	/**
	 * Returns the greater of two {@code double} values as if by calling
	 * {@link Math#max(double, double) Math.max}.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the greater of {@code a} and {@code b}
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static double max(double a, double b) {
		return Math.max(a, b);
	}

	/**
	 * Returns the smaller of two {@code double} values as if by calling
	 * {@link Math#min(double, double) Math.min}.
	 *
	 * @param a
	 *            the first operand
	 * @param b
	 *            the second operand
	 * @return the smaller of {@code a} and {@code b}.
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static double min(double a, double b) {
		return Math.min(a, b);
	}

	private static final long serialVersionUID = -9172774392245257468L;
}
