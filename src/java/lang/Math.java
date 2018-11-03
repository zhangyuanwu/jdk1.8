package java.lang;

import java.util.Random;
import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;

public final class Math {
	private Math() {
	}

	public static final double E = 2.7182818284590452354;
	public static final double PI = 3.14159265358979323846;

	public static double sin(double a) {
		return StrictMath.sin(a);
	}

	public static double cos(double a) {
		return StrictMath.cos(a);
	}

	public static double tan(double a) {
		return StrictMath.tan(a);
	}

	public static double asin(double a) {
		return StrictMath.asin(a);
	}

	public static double acos(double a) {
		return StrictMath.acos(a);
	}

	public static double atan(double a) {
		return StrictMath.atan(a);
	}

	public static double toRadians(double angdeg) {
		return (angdeg / 180.0) * PI;
	}

	public static double toDegrees(double angrad) {
		return (angrad * 180.0) / PI;
	}

	public static double exp(double a) {
		return StrictMath.exp(a);
	}

	public static double log(double a) {
		return StrictMath.log(a);
	}

	public static double log10(double a) {
		return StrictMath.log10(a);
	}

	public static double sqrt(double a) {
		return StrictMath.sqrt(a);
	}

	public static double cbrt(double a) {
		return StrictMath.cbrt(a);
	}

	public static double IEEEremainder(double f1, double f2) {
		return StrictMath.IEEEremainder(f1, f2);
	}

	public static double ceil(double a) {
		return StrictMath.ceil(a);
	}

	public static double floor(double a) {
		return StrictMath.floor(a);
	}

	public static double rint(double a) {
		return StrictMath.rint(a);
	}

	public static double atan2(double y, double x) {
		return StrictMath.atan2(y, x);
	}

	public static double pow(double a, double b) {
		return StrictMath.pow(a, b);
	}

	public static int round(float a) {
		int intBits = Float.floatToRawIntBits(a);
		int biasedExp = (intBits & FloatConsts.EXP_BIT_MASK) >> (FloatConsts.SIGNIFICAND_WIDTH - 1);
		int shift = ((FloatConsts.SIGNIFICAND_WIDTH - 2) + FloatConsts.EXP_BIAS) - biasedExp;
		if ((shift & -32) == 0) {
			int r = ((intBits & FloatConsts.SIGNIF_BIT_MASK) | (FloatConsts.SIGNIF_BIT_MASK + 1));
			if (intBits < 0) {
				r = -r;
			}
			return ((r >> shift) + 1) >> 1;
		} else {
			return (int) a;
		}
	}

	public static long round(double a) {
		long longBits = Double.doubleToRawLongBits(a);
		long biasedExp = (longBits & DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH - 1);
		long shift = ((DoubleConsts.SIGNIFICAND_WIDTH - 2) + DoubleConsts.EXP_BIAS) - biasedExp;
		if ((shift & -64) == 0) {
			long r = ((longBits & DoubleConsts.SIGNIF_BIT_MASK) | (DoubleConsts.SIGNIF_BIT_MASK + 1));
			if (longBits < 0) {
				r = -r;
			}
			return ((r >> shift) + 1) >> 1;
		} else {
			return (long) a;
		}
	}

	private static final class RandomNumberGeneratorHolder {
		static final Random randomNumberGenerator = new Random();
	}

	public static double random() {
		return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
	}

	/**
	 * 返回其参数的总和,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int addExact(int x, int y) {
		int r = x + y;
		if (((x ^ r) & (y ^ r)) < 0) {
			throw new ArithmeticException("integer overflow");
		}
		return r;
	}

	/**
	 * 返回其参数的总和,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long addExact(long x, long y) {
		long r = x + y;
		if (((x ^ r) & (y ^ r)) < 0) {
			throw new ArithmeticException("long overflow");
		}
		return r;
	}

	/**
	 * 返回参数的差异,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            要从第一个值中减去的第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int subtractExact(int x, int y) {
		int r = x - y;
		if (((x ^ y) & (x ^ r)) < 0) {
			throw new ArithmeticException("integer overflow");
		}
		return r;
	}

	/**
	 * 返回参数的差异,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            要从第一个值中减去的第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long subtractExact(long x, long y) {
		long r = x - y;
		if (((x ^ y) & (x ^ r)) < 0) {
			throw new ArithmeticException("long overflow");
		}
		return r;
	}

	/**
	 * 返回参数的乘积,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int multiplyExact(int x, int y) {
		long r = (long) x * (long) y;
		if ((int) r != r) {
			throw new ArithmeticException("integer overflow");
		}
		return (int) r;
	}

	/**
	 * 返回参数的乘积,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param x
	 *            第一个值
	 * @param y
	 *            第二个值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long multiplyExact(long x, long y) {
		long r = x * y;
		long ax = Math.abs(x);
		long ay = Math.abs(y);
		if ((((ax | ay) >>> 31) != 0)) {
			if (((y != 0) && ((r / y) != x)) || ((x == Long.MIN_VALUE) && (y == -1))) {
				throw new ArithmeticException("long overflow");
			}
		}
		return r;
	}

	/**
	 * 返回以1递增的参数,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param a
	 *            要增加的值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int incrementExact(int a) {
		if (a == Integer.MAX_VALUE) {
			throw new ArithmeticException("integer overflow");
		}
		return a + 1;
	}

	/**
	 * 返回以1递增的参数,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param a
	 *            要增加的值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long incrementExact(long a) {
		if (a == Long.MAX_VALUE) {
			throw new ArithmeticException("long overflow");
		}
		return a + 1L;
	}

	/**
	 * 返回参数递减1,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param a
	 *            递减的价值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int decrementExact(int a) {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("integer overflow");
		}
		return a - 1;
	}

	/**
	 * 返回减1的参数,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param a
	 *            递减的价值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long decrementExact(long a) {
		if (a == Long.MIN_VALUE) {
			throw new ArithmeticException("long overflow");
		}
		return a - 1L;
	}

	/**
	 * 返回参数的否定,如果结果溢出{@code int}则抛出异常.
	 *
	 * @param a
	 *            否定的价值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出一个int
	 * @since 1.8
	 */
	public static int negateExact(int a) {
		if (a == Integer.MIN_VALUE) {
			throw new ArithmeticException("integer overflow");
		}
		return -a;
	}

	/**
	 * 返回参数的否定,如果结果溢出{@code long}则抛出异常.
	 *
	 * @param a
	 *            否定的价值
	 * @return 结果
	 * @throws ArithmeticException
	 *             如果结果溢出了很长时间
	 * @since 1.8
	 */
	public static long negateExact(long a) {
		if (a == Long.MIN_VALUE) {
			throw new ArithmeticException("long overflow");
		}
		return -a;
	}

	/**
	 * 返回{@code long}参数的值; 如果值溢出{@code int}则抛出异常.
	 *
	 * @param value
	 *            很长的价值
	 * @return 参数为int
	 * @throws ArithmeticException
	 *             如果{@code参数}溢出一个int
	 * @since 1.8
	 */
	public static int toIntExact(long value) {
		if ((int) value != value) {
			throw new ArithmeticException("integer overflow");
		}
		return (int) value;
	}

	/**
	 * 返回小于或等于代数商的最大(最接近正无穷大){@code int}值.
	 * 有一种特殊情况,如果被除数为{@linkplain Integer#MIN_VALUE
	 * Integer.MIN_VALUE}且除数为{@code -1},则出现整数溢出,结果等于{@code Integer.MIN_VALUE}.
	 * <p>
	 * 正整数除法在舍入到零舍入模式(截断)下操作. 相反,该操作在朝向负无穷大(地板)舍入模式的轮次下起作用.
	 * 当精确结果为负时,地板舍入模式给出与截断不同的结果.
	 * <ul>
	 * <li>如果参数的符号相同,则{@code floorDiv}和{@code /}运算符的结果相同. <br>
	 * 例如,{@code floorDiv(4,3)== 1}和{@code (4/3)== 1}.</li>
	 * <li>如果参数的符号不同,则商为负,{@code floorDiv}返回小于或等于商的整数,{@code /}运算符返回最接近零的整数.<br>
	 * 例如,{@code floorDiv(-4,3)== -2},而{@code(-4 / 3)== -1}.</li>
	 * </ul>
	 * <p>
	 *
	 * @param x
	 *            红利
	 * @param y
	 *            除数
	 * @return 最大(最接近正无穷大){@code int}值小于或等于代数商.
	 * @throws ArithmeticException
	 *             如果除数{@code y}为零
	 * @see #floorMod(int, int)
	 * @see #floor(double)
	 * @since 1.8
	 */
	public static int floorDiv(int x, int y) {
		int r = x / y;
		if (((x ^ y) < 0) && ((r * y) != x)) {
			r--;
		}
		return r;
	}

	/**
	 * 返回小于或等于代数商的最大(最接近正无穷大){@code long}值.
	 * 有一种特殊情况,如果被除数为{@linkplain Long#MIN_VALUE
	 * Long.MIN_VALUE}且除数为{@code -1},则会发生整数溢出,结果等于{@code Long.MIN_VALUE}.
	 * <p>
	 * 正整数除法在舍入到零舍入模式(截断)下操作. 相反,该操作在朝向负无穷大(地板)舍入模式的轮次下起作用.
	 * 当精确结果为负时,地板舍入模式给出与截断不同的结果.
	 * <p>
	 * 有关示例,请参阅{@link #floorDiv(int,int)}.
	 *
	 * @param x
	 *            红利
	 * @param y
	 *            除数
	 * @return 最大(最接近正无穷大){@code long}值小于或等于代数商.
	 * @throws ArithmeticException
	 *             如果除数{@code y}为零
	 * @see #floorMod(long, long)
	 * @see #floor(double)
	 * @since 1.8
	 */
	public static long floorDiv(long x, long y) {
		long r = x / y;
		if (((x ^ y) < 0) && ((r * y) != x)) {
			r--;
		}
		return r;
	}

	/**
	 * 返回{@code int}参数的floor模数.
	 * <p>
	 * 地板模数为{@code x - (floorDiv(x,y)* y)},与除数{@code y}具有相同的符号,并且在{@code -abs(y)<r的范围内 <+ abs(y)}.
	 * <p>
	 * {@code floorDiv}和{@code floorMod}之间的关系是这样的:
	 * <ul>
	 * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
	 * </ul>
	 * <p>
	 * {@code floorMod}和{@code %}运算符之间的值差异是由于返回小于或等于商的整数的{@code floorDiv}与返回的{@code /}运算符之间的差异造成的
	 * 最接近零的整数.
	 * <p>
	 * 例子:
	 * <ul>
	 * <li>如果参数的符号相同,则{@code floorMod}和{@code %}运算符的结果相同. <br>
	 * <ul>
	 * <li>{@code floorMod(4, 3) == 1}; &nbsp; 和 {@code (4 % 3) == 1}</li>
	 * </ul>
	 * <li>如果参数的符号不同,则结果与{@code %}运算符不同.<br>
	 * <ul>
	 * <li>{@code floorMod(+4, -3) == -2}; &nbsp; 和 {@code (+4 % -3) == +1}</li>
	 * <li>{@code floorMod(-4, +3) == +2}; &nbsp; 和 {@code (-4 % +3) == -1}</li>
	 * <li>{@code floorMod(-4, -3) == -1}; &nbsp; 和 {@code (-4 % -3) == -1 }
	 * </li>
	 * </ul>
	 * </li>
	 * </ul>
	 * <p>
	 * 如果参数的符号未知并且需要正模数,则可以将其计算为{@code(floorMod(x,y)+ abs(y))％abs(y)}.
	 *
	 * @param x
	 *            红利
	 * @param y
	 *            除数
	 * @return 地板模数{@code x - (floorDiv(x,y)* y)}
	 * @throws ArithmeticException
	 *             如果除数{@code y}为零
	 * @see #floorDiv(int, int)
	 * @since 1.8
	 */
	public static int floorMod(int x, int y) {
		int r = x - (floorDiv(x, y) * y);
		return r;
	}

	/**
	 * 返回{@code long}参数的floor模数.
	 * <p>
	 * 地板模数为{@code x - (floorDiv(x,y)* y)},与除数{@code y}具有相同的符号,并且在{@code -abs(y)<r的范围内 <+ abs(y)}.
	 * <p>
	 * {@code floorDiv}和{@code floorMod}之间的关系是这样的:
	 * <ul>
	 * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
	 * </ul>
	 * <p>
	 * 有关示例,请参阅{@link #floorMod(int,int)}.
	 *
	 * @param x
	 *            红利
	 * @param y
	 *            除数
	 * @return 地板模数{@code x - (floorDiv(x,y)* y)}
	 * @throws ArithmeticException
	 *             如果除数{@code y}为零
	 * @see #floorDiv(long, long)
	 * @since 1.8
	 */
	public static long floorMod(long x, long y) {
		return x - (floorDiv(x, y) * y);
	}

	public static int abs(int a) {
		return (a < 0) ? -a : a;
	}

	public static long abs(long a) {
		return (a < 0) ? -a : a;
	}

	public static float abs(float a) {
		return (a <= 0.0F) ? 0.0F - a : a;
	}

	public static double abs(double a) {
		return (a <= 0.0D) ? 0.0D - a : a;
	}

	public static int max(int a, int b) {
		return (a >= b) ? a : b;
	}

	public static long max(long a, long b) {
		return (a >= b) ? a : b;
	}

	private static long negativeZeroFloatBits = Float.floatToRawIntBits(-0.0f);
	private static long negativeZeroDoubleBits = Double.doubleToRawLongBits(-0.0d);

	public static float max(float a, float b) {
		if (a != a) {
			return a;
		}
		if ((a == 0.0f) && (b == 0.0f) && (Float.floatToRawIntBits(a) == negativeZeroFloatBits)) {
			return b;
		}
		return (a >= b) ? a : b;
	}

	public static double max(double a, double b) {
		if (a != a) {
			return a;
		}
		if ((a == 0.0d) && (b == 0.0d) && (Double.doubleToRawLongBits(a) == negativeZeroDoubleBits)) {
			return b;
		}
		return (a >= b) ? a : b;
	}

	public static int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	public static long min(long a, long b) {
		return (a <= b) ? a : b;
	}

	public static float min(float a, float b) {
		if (a != a) {
			return a;
		}
		if ((a == 0.0f) && (b == 0.0f) && (Float.floatToRawIntBits(b) == negativeZeroFloatBits)) {
			return b;
		}
		return (a <= b) ? a : b;
	}

	public static double min(double a, double b) {
		if (a != a) {
			return a;
		}
		if ((a == 0.0d) && (b == 0.0d) && (Double.doubleToRawLongBits(b) == negativeZeroDoubleBits)) {
			return b;
		}
		return (a <= b) ? a : b;
	}

	public static double ulp(double d) {
		int exp = getExponent(d);
		switch (exp) {
		case DoubleConsts.MAX_EXPONENT + 1:
			return Math.abs(d);
		case DoubleConsts.MIN_EXPONENT - 1:
			return Double.MIN_VALUE;
		default:
			assert (exp <= DoubleConsts.MAX_EXPONENT) && (exp >= DoubleConsts.MIN_EXPONENT);
			exp = exp - (DoubleConsts.SIGNIFICAND_WIDTH - 1);
			if (exp >= DoubleConsts.MIN_EXPONENT) {
				return powerOfTwoD(exp);
			} else {
				return Double.longBitsToDouble(1L << (exp - (DoubleConsts.MIN_EXPONENT - (DoubleConsts.SIGNIFICAND_WIDTH - 1))));
			}
		}
	}

	public static float ulp(float f) {
		int exp = getExponent(f);
		switch (exp) {
		case FloatConsts.MAX_EXPONENT + 1:
			return Math.abs(f);
		case FloatConsts.MIN_EXPONENT - 1:
			return FloatConsts.MIN_VALUE;
		default:
			assert (exp <= FloatConsts.MAX_EXPONENT) && (exp >= FloatConsts.MIN_EXPONENT);
			exp = exp - (FloatConsts.SIGNIFICAND_WIDTH - 1);
			if (exp >= FloatConsts.MIN_EXPONENT) {
				return powerOfTwoF(exp);
			} else {
				return Float.intBitsToFloat(1 << (exp - (FloatConsts.MIN_EXPONENT - (FloatConsts.SIGNIFICAND_WIDTH - 1))));
			}
		}
	}

	public static double signum(double d) {
		return ((d == 0.0) || Double.isNaN(d)) ? d : copySign(1.0, d);
	}

	public static float signum(float f) {
		return ((f == 0.0f) || Float.isNaN(f)) ? f : copySign(1.0f, f);
	}

	public static double sinh(double x) {
		return StrictMath.sinh(x);
	}

	public static double cosh(double x) {
		return StrictMath.cosh(x);
	}

	public static double tanh(double x) {
		return StrictMath.tanh(x);
	}

	public static double hypot(double x, double y) {
		return StrictMath.hypot(x, y);
	}

	public static double expm1(double x) {
		return StrictMath.expm1(x);
	}

	public static double log1p(double x) {
		return StrictMath.log1p(x);
	}

	public static double copySign(double magnitude, double sign) {
		return Double.longBitsToDouble((Double.doubleToRawLongBits(sign) & (DoubleConsts.SIGN_BIT_MASK)) | (Double.doubleToRawLongBits(magnitude) & (DoubleConsts.EXP_BIT_MASK | DoubleConsts.SIGNIF_BIT_MASK)));
	}

	public static float copySign(float magnitude, float sign) {
		return Float.intBitsToFloat((Float.floatToRawIntBits(sign) & (FloatConsts.SIGN_BIT_MASK)) | (Float.floatToRawIntBits(magnitude) & (FloatConsts.EXP_BIT_MASK | FloatConsts.SIGNIF_BIT_MASK)));
	}

	public static int getExponent(float f) {
		return ((Float.floatToRawIntBits(f) & FloatConsts.EXP_BIT_MASK) >> (FloatConsts.SIGNIFICAND_WIDTH - 1)) - FloatConsts.EXP_BIAS;
	}

	public static int getExponent(double d) {
		return (int) (((Double.doubleToRawLongBits(d) & DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH - 1)) - DoubleConsts.EXP_BIAS);
	}

	public static double nextAfter(double start, double direction) {
		if (Double.isNaN(start) || Double.isNaN(direction)) {
			return start + direction;
		} else if (start == direction) {
			return direction;
		} else {
			long transducer = Double.doubleToRawLongBits(start + 0.0d);
			if (direction > start) {
				transducer = transducer + (transducer >= 0L ? 1L : -1L);
			} else {
				assert direction < start;
				if (transducer > 0L) {
					--transducer;
				} else if (transducer < 0L) {
					++transducer;
				} else {
					transducer = DoubleConsts.SIGN_BIT_MASK | 1L;
				}
			}
			return Double.longBitsToDouble(transducer);
		}
	}

	public static float nextAfter(float start, double direction) {
		if (Float.isNaN(start) || Double.isNaN(direction)) {
			return start + (float) direction;
		} else if (start == direction) {
			return (float) direction;
		} else {
			int transducer = Float.floatToRawIntBits(start + 0.0f);
			if (direction > start) {
				transducer = transducer + (transducer >= 0 ? 1 : -1);
			} else {
				assert direction < start;
				if (transducer > 0) {
					--transducer;
				} else if (transducer < 0) {
					++transducer;
				} else {
					transducer = FloatConsts.SIGN_BIT_MASK | 1;
				}
			}
			return Float.intBitsToFloat(transducer);
		}
	}

	public static double nextUp(double d) {
		if (Double.isNaN(d) || (d == Double.POSITIVE_INFINITY)) {
			return d;
		} else {
			d += 0.0d;
			return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d >= 0.0d) ? +1L : -1L));
		}
	}

	public static float nextUp(float f) {
		if (Float.isNaN(f) || (f == FloatConsts.POSITIVE_INFINITY)) {
			return f;
		} else {
			f += 0.0f;
			return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f >= 0.0f) ? +1 : -1));
		}
	}

	/**
	 * Returns the floating-point value adjacent to {@code d} in the direction
	 * of negative infinity. This method is semantically equivalent to
	 * {@code nextAfter(d,
	 * Double.NEGATIVE_INFINITY)}; however, a {@code nextDown} implementation
	 * may run faster than its equivalent {@code nextAfter} call.
	 * <p>
	 * 特别案例:
	 * <ul>
	 * <li>如果参数是NaN,则结果为NaN.
	 * <li>如果参数为负无穷大,则结果为负无穷大.
	 * <li>If the argument is zero, the result is {@code -Double.MIN_VALUE}
	 * </ul>
	 *
	 * @param d
	 *            starting floating-point value
	 * @return The adjacent floating-point value closer to negative infinity.
	 * @since 1.8
	 */
	public static double nextDown(double d) {
		if (Double.isNaN(d) || (d == Double.NEGATIVE_INFINITY)) {
			return d;
		} else {
			if (d == 0.0) {
				return -Double.MIN_VALUE;
			} else {
				return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d > 0.0d) ? -1L : +1L));
			}
		}
	}

	/**
	 * 返回负无穷大方向上{@code f}附近的浮点值.
	 * 该方法在语义上等同于{@code nextAfter(f,Float.NEGATIVE_INFINITY)};
	 * 但是,{@code nextDown}实现可能比其等效的{@code nextAfter}调用运行得更快.
	 * <p>
	 * 特别案例:
	 * <ul>
	 * <li>如果参数是NaN,则结果为NaN.
	 * <li>如果参数为负无穷大,则结果为负无穷大.
	 * <li>如果参数为零,则结果为{@code -Float.MIN_VALUE}
	 * </ul>
	 *
	 * @param f
	 *            开始浮点值
	 * @return 相邻的浮点值更接近负无穷大.
	 * @since 1.8
	 */
	public static float nextDown(float f) {
		if (Float.isNaN(f) || (f == Float.NEGATIVE_INFINITY)) {
			return f;
		} else {
			if (f == 0.0f) {
				return -Float.MIN_VALUE;
			} else {
				return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f > 0.0f) ? -1 : +1));
			}
		}
	}

	public static double scalb(double d, int scaleFactor) {
		final int MAX_SCALE = DoubleConsts.MAX_EXPONENT + -DoubleConsts.MIN_EXPONENT + DoubleConsts.SIGNIFICAND_WIDTH + 1;
		int exp_adjust = 0;
		int scale_increment = 0;
		double exp_delta = Double.NaN;
		if (scaleFactor < 0) {
			scaleFactor = Math.max(scaleFactor, -MAX_SCALE);
			scale_increment = -512;
			exp_delta = twoToTheDoubleScaleDown;
		} else {
			scaleFactor = Math.min(scaleFactor, MAX_SCALE);
			scale_increment = 512;
			exp_delta = twoToTheDoubleScaleUp;
		}
		int t = (scaleFactor >> (9 - 1)) >>> (32 - 9);
		exp_adjust = ((scaleFactor + t) & (512 - 1)) - t;
		d *= powerOfTwoD(exp_adjust);
		scaleFactor -= exp_adjust;
		while (scaleFactor != 0) {
			d *= exp_delta;
			scaleFactor -= scale_increment;
		}
		return d;
	}

	public static float scalb(float f, int scaleFactor) {
		final int MAX_SCALE = FloatConsts.MAX_EXPONENT + -FloatConsts.MIN_EXPONENT + FloatConsts.SIGNIFICAND_WIDTH + 1;
		scaleFactor = Math.max(Math.min(scaleFactor, MAX_SCALE), -MAX_SCALE);
		return (float) (f * powerOfTwoD(scaleFactor));
	}

	static double twoToTheDoubleScaleUp = powerOfTwoD(512);
	static double twoToTheDoubleScaleDown = powerOfTwoD(-512);

	static double powerOfTwoD(int n) {
		assert ((n >= DoubleConsts.MIN_EXPONENT) && (n <= DoubleConsts.MAX_EXPONENT));
		return Double.longBitsToDouble((((long) n + (long) DoubleConsts.EXP_BIAS) << (DoubleConsts.SIGNIFICAND_WIDTH - 1)) & DoubleConsts.EXP_BIT_MASK);
	}

	static float powerOfTwoF(int n) {
		assert ((n >= FloatConsts.MIN_EXPONENT) && (n <= FloatConsts.MAX_EXPONENT));
		return Float.intBitsToFloat(((n + FloatConsts.EXP_BIAS) << (FloatConsts.SIGNIFICAND_WIDTH - 1)) & FloatConsts.EXP_BIT_MASK);
	}
}
