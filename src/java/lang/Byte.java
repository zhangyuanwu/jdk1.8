package java.lang;

public final class Byte extends Number implements Comparable<Byte> {
	public static final byte MIN_VALUE = -128;
	public static final byte MAX_VALUE = 127;
	@SuppressWarnings("unchecked")
	public static final Class<Byte> TYPE = (Class<Byte>) Class.getPrimitiveClass("byte");

	public static String toString(byte b) {
		return Integer.toString(b, 10);
	}

	private static class ByteCache {
		private ByteCache() {
		}

		static final Byte cache[] = new Byte[-(-128) + 127 + 1];
		static {
			for (int i = 0; i < cache.length; i++) {
				cache[i] = new Byte((byte) (i - 128));
			}
		}
	}

	public static Byte valueOf(byte b) {
		final int offset = 128;
		return ByteCache.cache[b + offset];
	}

	public static byte parseByte(String s, int radix) throws NumberFormatException {
		int i = Integer.parseInt(s, radix);
		if ((i < MIN_VALUE) || (i > MAX_VALUE)) {
			throw new NumberFormatException("Value out of range. Value:\"" + s + "\" Radix:" + radix);
		}
		return (byte) i;
	}

	public static byte parseByte(String s) throws NumberFormatException {
		return parseByte(s, 10);
	}

	public static Byte valueOf(String s, int radix) throws NumberFormatException {
		return valueOf(parseByte(s, radix));
	}

	public static Byte valueOf(String s) throws NumberFormatException {
		return valueOf(s, 10);
	}

	public static Byte decode(String nm) throws NumberFormatException {
		int i = Integer.decode(nm);
		if ((i < MIN_VALUE) || (i > MAX_VALUE)) {
			throw new NumberFormatException("Value " + i + " out of range from input " + nm);
		}
		return valueOf((byte) i);
	}

	private final byte value;

	public Byte(byte value) {
		this.value = value;
	}

	public Byte(String s) throws NumberFormatException {
		value = parseByte(s, 10);
	}

	@Override
	public byte byteValue() {
		return value;
	}

	@Override
	public short shortValue() {
		return value;
	}

	@Override
	public int intValue() {
		return value;
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
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		return Byte.hashCode(value);
	}

	/**
	 * 返回{@code byte}值的哈希码; 与{@code Byte.hashCode()}兼容.
	 *
	 * @param value
	 *            哈希值
	 * @return {@code byte}值的哈希码值.
	 * @since 1.8
	 */
	public static int hashCode(byte value) {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Byte) {
			return value == ((Byte) obj).byteValue();
		}
		return false;
	}

	@Override
	public int compareTo(Byte anotherByte) {
		return compare(value, anotherByte.value);
	}

	public static int compare(byte x, byte y) {
		return x - y;
	}

	/**
	 * 通过无符号转换将参数转换为{@code int}。
	 * 在无符号转换为{@code int}时，{@code int}的高24位为零，低8位等于{@code byte}参数的位。
	 * 因此，零和正{@code byte}值被映射到数值相等的{@code int}值，而负{@code byte}值被映射到等于输入的{@code int}值加上<sup>8</sup>。
	 *
	 * @param x
	 *            the value to convert to an unsigned {@code int}
	 * @return the argument converted to {@code int} by an unsigned conversion
	 * @since 1.8
	 */
	public static int toUnsignedInt(byte x) {
		return (x) & 0xff;
	}

	/**
	 * 通过无符号转换将参数转换为{@code long}。 In an unsigned conversion to a {@code long},
	 * the high-order 56 bits of the {@code long} are zero and the low-order 8
	 * bits are equal to the bits of the {@code byte} argument.
	 *
	 * Consequently, zero and positive {@code byte} values are mapped to a
	 * numerically equal {@code long} value and negative {@code
	 * byte} values are mapped to a {@code long} value equal to the input plus
	 * 2<sup>8</sup>.
	 *
	 * @param x
	 *            the value to convert to an unsigned {@code long}
	 * @return 通过无符号转换将参数转换为{@code long}
	 * @since 1.8
	 */
	public static long toUnsignedLong(byte x) {
		return (x) & 0xffL;
	}

	public static final int SIZE = 8;
	/**
	 * 用于表示二进制补码二进制形式的{@code byte}值的字节数.
	 *
	 * @since 1.8
	 */
	public static final int BYTES = SIZE / Byte.SIZE;
	private static final long serialVersionUID = -7183698231559129828L;
}
