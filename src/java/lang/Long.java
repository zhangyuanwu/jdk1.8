package java.lang;

import java.lang.annotation.Native;
import java.math.BigInteger;

public final class Long extends Number implements Comparable<Long> {
	@Native
	public static final long MIN_VALUE = 0x8000000000000000L;
	@Native
	public static final long MAX_VALUE = 0x7fffffffffffffffL;
	@SuppressWarnings("unchecked")
	public static final Class<Long> TYPE = (Class<Long>) Class.getPrimitiveClass("long");

	public static String toString(long i, int radix) {
		if ((radix < Character.MIN_RADIX) || (radix > Character.MAX_RADIX)) {
			radix = 10;
		}
		if (radix == 10) {
			return toString(i);
		}
		char[] buf = new char[65];
		int charPos = 64;
		boolean negative = (i < 0);
		if (!negative) {
			i = -i;
		}
		while (i <= -radix) {
			buf[charPos--] = Integer.digits[(int) (-(i % radix))];
			i = i / radix;
		}
		buf[charPos] = Integer.digits[(int) (-i)];
		if (negative) {
			buf[--charPos] = '-';
		}
		return new String(buf, charPos, (65 - charPos));
	}

	/**
	 * 返回第一个参数的字符串表示形式,作为第二个参数指定的基数中的无符号整数值.
	 * <p>
	 * 如果基数小于{@code Character.MIN_RADIX}或大于{@code Character.MAX_RADIX},则使用基数{@code 10}代替.
	 * <p>
	 * 请注意,由于第一个参数被视为无符号值,因此不会打印前导符号字符.
	 * <p>
	 * 如果幅度为零,则由单个零字符{@code '0'}表示({@code '\u005Cu0030'}); 否则,幅度表示的第一个字符将不是零字符.
	 * <p>
	 * 基数的行为和用作数字的字符与{@link #toString(long,int) toString}相同.
	 *
	 * @param i
	 *            要转换为无符号字符串的整数.
	 * @param radix
	 *            要在字符串表示中使用的基数.
	 * @return 指定基数中参数的无符号字符串表示形式.
	 * @see #toString(long, int)
	 * @since 1.8
	 */
	public static String toUnsignedString(long i, int radix) {
		if (i >= 0) {
			return toString(i, radix);
		} else {
			switch (radix) {
			case 2:
				return toBinaryString(i);
			case 4:
				return toUnsignedString0(i, 2);
			case 8:
				return toOctalString(i);
			case 10:
				long quot = (i >>> 1) / 5;
				long rem = i - (quot * 10);
				return toString(quot) + rem;
			case 16:
				return toHexString(i);
			case 32:
				return toUnsignedString0(i, 5);
			default:
				return toUnsignedBigInteger(i).toString(radix);
			}
		}
	}

	private static BigInteger toUnsignedBigInteger(long i) {
		if (i >= 0L) {
			return BigInteger.valueOf(i);
		} else {
			int upper = (int) (i >>> 32);
			int lower = (int) i;
			return (BigInteger.valueOf(Integer.toUnsignedLong(upper))).shiftLeft(32).add(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
		}
	}

	public static String toHexString(long i) {
		return toUnsignedString0(i, 4);
	}

	public static String toOctalString(long i) {
		return toUnsignedString0(i, 3);
	}

	public static String toBinaryString(long i) {
		return toUnsignedString0(i, 1);
	}

	static String toUnsignedString0(long val, int shift) {
		int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
		int chars = Math.max(((mag + (shift - 1)) / shift), 1);
		char[] buf = new char[chars];
		formatUnsignedLong(val, shift, buf, 0, chars);
		return new String(buf, true);
	}

	static int formatUnsignedLong(long val, int shift, char[] buf, int offset, int len) {
		int charPos = len;
		int radix = 1 << shift;
		int mask = radix - 1;
		do {
			buf[offset + --charPos] = Integer.digits[((int) val) & mask];
			val >>>= shift;
		} while ((val != 0) && (charPos > 0));
		return charPos;
	}

	public static String toString(long i) {
		if (i == Long.MIN_VALUE) {
			return "-9223372036854775808";
		}
		int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
		char[] buf = new char[size];
		getChars(i, size, buf);
		return new String(buf, true);
	}

	/**
	 * 以无符号十进制值的形式返回参数的字符串表示形式.
	 * 参数转换为无符号十进制表示形式,并作为字符串返回,就像参数和基数10作为{@link #toUnsignedString(long,int)}方法的参数一样.
	 *
	 * @param i
	 *            要转换为无符号字符串的整数.
	 * @return 参数的无符号字符串表示形式.
	 * @see #toUnsignedString(long, int)
	 * @since 1.8
	 */
	public static String toUnsignedString(long i) {
		return toUnsignedString(i, 10);
	}

	static void getChars(long i, int index, char[] buf) {
		long q;
		int r;
		int charPos = index;
		char sign = 0;
		if (i < 0) {
			sign = '-';
			i = -i;
		}
		while (i > Integer.MAX_VALUE) {
			q = i / 100;
			r = (int) (i - ((q << 6) + (q << 5) + (q << 2)));
			i = q;
			buf[--charPos] = Integer.DigitOnes[r];
			buf[--charPos] = Integer.DigitTens[r];
		}
		int q2;
		int i2 = (int) i;
		while (i2 >= 65536) {
			q2 = i2 / 100;
			r = i2 - ((q2 << 6) + (q2 << 5) + (q2 << 2));
			i2 = q2;
			buf[--charPos] = Integer.DigitOnes[r];
			buf[--charPos] = Integer.DigitTens[r];
		}
		for (;;) {
			q2 = (i2 * 52429) >>> (16 + 3);
			r = i2 - ((q2 << 3) + (q2 << 1));
			buf[--charPos] = Integer.digits[r];
			i2 = q2;
			if (i2 == 0) {
				break;
			}
		}
		if (sign != 0) {
			buf[--charPos] = sign;
		}
	}

	static int stringSize(long x) {
		long p = 10;
		for (int i = 1; i < 19; i++) {
			if (x < p) {
				return i;
			}
			p = 10 * p;
		}
		return 19;
	}

	public static long parseLong(String s, int radix) throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException("null");
		}
		if (radix < Character.MIN_RADIX) {
			throw new NumberFormatException("radix " + radix + " less than Character.MIN_RADIX");
		}
		if (radix > Character.MAX_RADIX) {
			throw new NumberFormatException("radix " + radix + " greater than Character.MAX_RADIX");
		}
		long result = 0;
		boolean negative = false;
		int i = 0, len = s.length();
		long limit = -Long.MAX_VALUE;
		long multmin;
		int digit;
		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar < '0') {
				if (firstChar == '-') {
					negative = true;
					limit = Long.MIN_VALUE;
				} else if (firstChar != '+') {
					throw NumberFormatException.forInputString(s);
				}
				if (len == 1) {
					throw NumberFormatException.forInputString(s);
				}
				i++;
			}
			multmin = limit / radix;
			while (i < len) {
				digit = Character.digit(s.charAt(i++), radix);
				if (digit < 0) {
					throw NumberFormatException.forInputString(s);
				}
				if (result < multmin) {
					throw NumberFormatException.forInputString(s);
				}
				result *= radix;
				if (result < (limit + digit)) {
					throw NumberFormatException.forInputString(s);
				}
				result -= digit;
			}
		} else {
			throw NumberFormatException.forInputString(s);
		}
		return negative ? result : -result;
	}

	public static long parseLong(String s) throws NumberFormatException {
		return parseLong(s, 10);
	}

	/**
	 * 将字符串参数解析为第二个参数指定的基数中的无符号{@code long}.
	 * 无符号整数将通常与负数关联的值映射到大于{@code MAX_VALUE}的正数.
	 * 字符串中的字符必须都是指定基数的数字(由{@link java.lang.Character#digit(char,int)}返回非负值确定),除了第一个字符可能是ASCII加上
	 * 签署{@code '+'}({@code '\u005Cu002B'}). 返回结果整数值.
	 * <p>
	 * 如果发生以下任何情况,则抛出类型{@code NumberFormatException}的异常:
	 * <ul>
	 * <li>第一个参数是{@code null}或者是一个长度为零的字符串.
	 * <li>基数小于{@link java.lang.Character#MIN_RADIX}或大于{@link java.lang.Character #MAX_RADIX}.
	 * <li>字符串的任何字符都不是指定基数的数字,除了第一个字符可能是加号{@code '+'}({@code '\u005Cu002B'}),前提是字符串长度超过1.
	 * <li>字符串表示的值大于最大的无符号{@code long},2<sup>64</sup>-1.
	 * </ul>
	 *
	 * @param s
	 *            包含要解析的无符号整数表示的{@code String}
	 * @param radix
	 *            解析{@code s}时使用的基数.
	 * @return 由指定基数中的字符串参数表示的无符号{@code long}.
	 * @throws NumberFormatException
	 *             如果{@code String}不包含可解析的{@code long}.
	 * @since 1.8
	 */
	public static long parseUnsignedLong(String s, int radix) throws NumberFormatException {
		if (s == null) {
			throw new NumberFormatException("null");
		}
		int len = s.length();
		if (len > 0) {
			char firstChar = s.charAt(0);
			if (firstChar == '-') {
				throw new NumberFormatException(String.format("Illegal leading minus sign on unsigned string %s.", s));
			} else {
				if ((len <= 12) || ((radix == 10) && (len <= 18))) {
					return parseLong(s, radix);
				}
				long first = parseLong(s.substring(0, len - 1), radix);
				int second = Character.digit(s.charAt(len - 1), radix);
				if (second < 0) {
					throw new NumberFormatException("Bad digit at end of " + s);
				}
				long result = (first * radix) + second;
				if (compareUnsigned(result, first) < 0) {
					throw new NumberFormatException(String.format("String value %s exceeds range of unsigned long.", s));
				}
				return result;
			}
		} else {
			throw NumberFormatException.forInputString(s);
		}
	}

	/**
	 * 将字符串参数解析为无符号十进制{@code long}.
	 * 字符串中的字符必须都是十进制数字,除了第一个字符可能是ASCII加号{@code '+'}({@code '\u005Cu002B'}).
	 * 返回结果整数值,就像将参数和基数10作为{@link #parseUnsignedLong(java.lang.String,int)}方法的参数给出一样.
	 *
	 * @param s
	 *            包含要解析的无符号{@code long}表示的{@code String}
	 * @return 由十进制字符串参数表示的无符号{@code long}值
	 * @throws NumberFormatException
	 *             如果字符串不包含可解析的无符号整数.
	 * @since 1.8
	 */
	public static long parseUnsignedLong(String s) throws NumberFormatException {
		return parseUnsignedLong(s, 10);
	}

	public static Long valueOf(String s, int radix) throws NumberFormatException {
		return Long.valueOf(parseLong(s, radix));
	}

	public static Long valueOf(String s) throws NumberFormatException {
		return Long.valueOf(parseLong(s, 10));
	}

	private static class LongCache {
		private LongCache() {
		}

		static final Long cache[] = new Long[-(-128) + 127 + 1];
		static {
			for (int i = 0; i < cache.length; i++) {
				cache[i] = new Long(i - 128);
			}
		}
	}

	public static Long valueOf(long l) {
		final int offset = 128;
		if ((l >= -128) && (l <= 127)) {
			return LongCache.cache[(int) l + offset];
		}
		return new Long(l);
	}

	public static Long decode(String nm) throws NumberFormatException {
		int radix = 10;
		int index = 0;
		boolean negative = false;
		Long result;
		if (nm.length() == 0) {
			throw new NumberFormatException("Zero length string");
		}
		char firstChar = nm.charAt(0);
		if (firstChar == '-') {
			negative = true;
			index++;
		} else if (firstChar == '+') {
			index++;
		}
		if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
			index += 2;
			radix = 16;
		} else if (nm.startsWith("#", index)) {
			index++;
			radix = 16;
		} else if (nm.startsWith("0", index) && (nm.length() > (1 + index))) {
			index++;
			radix = 8;
		}
		if (nm.startsWith("-", index) || nm.startsWith("+", index)) {
			throw new NumberFormatException("Sign character in wrong position");
		}
		try {
			result = Long.valueOf(nm.substring(index), radix);
			result = negative ? Long.valueOf(-result.longValue()) : result;
		} catch (NumberFormatException e) {
			String constant = negative ? ("-" + nm.substring(index)) : nm.substring(index);
			result = Long.valueOf(constant, radix);
		}
		return result;
	}

	private final long value;

	public Long(long value) {
		this.value = value;
	}

	public Long(String s) throws NumberFormatException {
		value = parseLong(s, 10);
	}

	@Override
	public byte byteValue() {
		return (byte) value;
	}

	@Override
	public short shortValue() {
		return (short) value;
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public String toString() {
		return toString(value);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	/**
	 * 返回{@code long}值的哈希码; 与{@code Long.hashCode()}兼容.
	 *
	 * @param value
	 *            哈希值
	 * @return {@code long}值的哈希码值.
	 * @since 1.8
	 */
	public static int hashCode(long value) {
		return (int) (value ^ (value >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Long) {
			return value == ((Long) obj).longValue();
		}
		return false;
	}

	public static Long getLong(String nm) {
		return getLong(nm, null);
	}

	public static Long getLong(String nm, long val) {
		Long result = Long.getLong(nm, null);
		return (result == null) ? Long.valueOf(val) : result;
	}

	public static Long getLong(String nm, Long val) {
		String v = null;
		try {
			v = System.getProperty(nm);
		} catch (IllegalArgumentException | NullPointerException e) {
		}
		if (v != null) {
			try {
				return Long.decode(v);
			} catch (NumberFormatException e) {
			}
		}
		return val;
	}

	@Override
	public int compareTo(Long anotherLong) {
		return compare(value, anotherLong.value);
	}

	public static int compare(long x, long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	/**
	 * 比较两个{@code long}值,以数值方式将值视为无符号.
	 *
	 * @param x
	 *            第一个{@code long}进行比较
	 * @param y
	 *            第二个{@code long}进行比较
	 * @return 如果{@code x == y},则值为{@code 0}; 如果{@code x <y}为无符号值,则值小于{@code 0};
	 *         如果{@code x> y}为无符号值,则值大于{@code 0}
	 * @since 1.8
	 */
	public static int compareUnsigned(long x, long y) {
		return compare(x + MIN_VALUE, y + MIN_VALUE);
	}

	/**
	 * 返回将第一个参数除以第二个参数的无符号商,其中每个参数和结果都被解释为无符号值.
	 * <p>
	 * 注意,在二进制补码算法中,如果两个操作数被认为是有符号的或两者都是无符号的,则加,减和乘法的其他三个基本算术运算是按位相同的.
	 * 因此,不提供单独的{@code addUnsigned}等方法.
	 *
	 * @param dividend
	 *            要划分的价值
	 * @param divisor
	 *            做分裂的价值
	 * @return 第一个参数的无符号商除以第二个参数
	 * @see #remainderUnsigned
	 * @since 1.8
	 */
	public static long divideUnsigned(long dividend, long divisor) {
		if (divisor < 0L) {
			return (compareUnsigned(dividend, divisor)) < 0 ? 0L : 1L;
		}
		if (dividend > 0) {
			return dividend / divisor;
		} else {
			return toUnsignedBigInteger(dividend).divide(toUnsignedBigInteger(divisor)).longValue();
		}
	}

	/**
	 * 返回将第一个参数除以第二个参数的无符号余数,其中每个参数和结果都被解释为无符号值.
	 *
	 * @param dividend
	 *            要划分的价值
	 * @param divisor
	 *            做分裂的价值
	 * @return 第一个参数的无符号余数除以第二个参数
	 * @see #divideUnsigned
	 * @since 1.8
	 */
	public static long remainderUnsigned(long dividend, long divisor) {
		if ((dividend > 0) && (divisor > 0)) {
			return dividend % divisor;
		} else {
			if (compareUnsigned(dividend, divisor) < 0) {
				return dividend;
			} else {
				return toUnsignedBigInteger(dividend).remainder(toUnsignedBigInteger(divisor)).longValue();
			}
		}
	}

	@Native
	public static final int SIZE = 64;
	/**
	 * 用于表示二进制补码二进制形式的{@code long}值的字节数.
	 *
	 * @since 1.8
	 */
	public static final int BYTES = SIZE / Byte.SIZE;

	public static long highestOneBit(long i) {
		i |= (i >> 1);
		i |= (i >> 2);
		i |= (i >> 4);
		i |= (i >> 8);
		i |= (i >> 16);
		i |= (i >> 32);
		return i - (i >>> 1);
	}

	public static long lowestOneBit(long i) {
		return i & -i;
	}

	public static int numberOfLeadingZeros(long i) {
		if (i == 0) {
			return 64;
		}
		int n = 1;
		int x = (int) (i >>> 32);
		if (x == 0) {
			n += 32;
			x = (int) i;
		}
		if ((x >>> 16) == 0) {
			n += 16;
			x <<= 16;
		}
		if ((x >>> 24) == 0) {
			n += 8;
			x <<= 8;
		}
		if ((x >>> 28) == 0) {
			n += 4;
			x <<= 4;
		}
		if ((x >>> 30) == 0) {
			n += 2;
			x <<= 2;
		}
		n -= x >>> 31;
		return n;
	}

	public static int numberOfTrailingZeros(long i) {
		int x, y;
		if (i == 0) {
			return 64;
		}
		int n = 63;
		y = (int) i;
		if (y != 0) {
			n = n - 32;
			x = y;
		} else {
			x = (int) (i >>> 32);
		}
		y = x << 16;
		if (y != 0) {
			n = n - 16;
			x = y;
		}
		y = x << 8;
		if (y != 0) {
			n = n - 8;
			x = y;
		}
		y = x << 4;
		if (y != 0) {
			n = n - 4;
			x = y;
		}
		y = x << 2;
		if (y != 0) {
			n = n - 2;
			x = y;
		}
		return n - ((x << 1) >>> 31);
	}

	public static int bitCount(long i) {
		i = i - ((i >>> 1) & 0x5555555555555555L);
		i = (i & 0x3333333333333333L) + ((i >>> 2) & 0x3333333333333333L);
		i = (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
		i = i + (i >>> 8);
		i = i + (i >>> 16);
		i = i + (i >>> 32);
		return (int) i & 0x7f;
	}

	public static long rotateLeft(long i, int distance) {
		return (i << distance) | (i >>> -distance);
	}

	public static long rotateRight(long i, int distance) {
		return (i >>> distance) | (i << -distance);
	}

	public static long reverse(long i) {
		i = ((i & 0x5555555555555555L) << 1) | ((i >>> 1) & 0x5555555555555555L);
		i = ((i & 0x3333333333333333L) << 2) | ((i >>> 2) & 0x3333333333333333L);
		i = ((i & 0x0f0f0f0f0f0f0f0fL) << 4) | ((i >>> 4) & 0x0f0f0f0f0f0f0f0fL);
		i = ((i & 0x00ff00ff00ff00ffL) << 8) | ((i >>> 8) & 0x00ff00ff00ff00ffL);
		i = (i << 48) | ((i & 0xffff0000L) << 16) | ((i >>> 16) & 0xffff0000L) | (i >>> 48);
		return i;
	}

	public static int signum(long i) {
		return (int) ((i >> 63) | (-i >>> 63));
	}

	public static long reverseBytes(long i) {
		i = ((i & 0x00ff00ff00ff00ffL) << 8) | ((i >>> 8) & 0x00ff00ff00ff00ffL);
		return (i << 48) | ((i & 0xffff0000L) << 16) | ((i >>> 16) & 0xffff0000L) | (i >>> 48);
	}

	/**
	 * 根据+运算符将两个{@code long}值相加.
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}的总和
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static long sum(long a, long b) {
		return a + b;
	}

	/**
	 * 返回两个{@code long}值中较大的一个,就像调用{@link Math#max(long,long) Math.max}一样.
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}中的较大者
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static long max(long a, long b) {
		return Math.max(a, b);
	}

	/**
	 * 返回两个{@code long}值中较小的一个,就好像通过调用{@link Math#min(long,long) Math.min}一样.
	 *
	 * @param a
	 *            第一个操作数
	 * @param b
	 *            第二个操作数
	 * @return {@code a}和{@code b}中较小的一个
	 * @see java.util.function.BinaryOperator
	 * @since 1.8
	 */
	public static long min(long a, long b) {
		return Math.min(a, b);
	}

	@Native
	private static final long serialVersionUID = 4290774380558885855L;
}
