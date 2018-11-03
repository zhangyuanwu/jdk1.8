package java.lang;

import sun.misc.FloatingDecimal;
import java.util.Arrays;

abstract class AbstractStringBuilder implements Appendable, CharSequence {
	char[] value;
	int count;

	AbstractStringBuilder() {
	}

	AbstractStringBuilder(int capacity) {
		value = new char[capacity];
	}

	@Override
	public int length() {
		return count;
	}

	public int capacity() {
		return value.length;
	}

	public void ensureCapacity(int minimumCapacity) {
		if (minimumCapacity > 0) {
			ensureCapacityInternal(minimumCapacity);
		}
	}

	private void ensureCapacityInternal(int minimumCapacity) {
		if ((minimumCapacity - value.length) > 0) {
			value = Arrays.copyOf(value, newCapacity(minimumCapacity));
		}
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private int newCapacity(int minCapacity) {
		int newCapacity = (value.length << 1) + 2;
		if ((newCapacity - minCapacity) < 0) {
			newCapacity = minCapacity;
		}
		return ((newCapacity <= 0) || ((MAX_ARRAY_SIZE - newCapacity) < 0)) ? hugeCapacity(minCapacity) : newCapacity;
	}

	private int hugeCapacity(int minCapacity) {
		if ((Integer.MAX_VALUE - minCapacity) < 0) {
			throw new OutOfMemoryError();
		}
		return (minCapacity > MAX_ARRAY_SIZE) ? minCapacity : MAX_ARRAY_SIZE;
	}

	public void trimToSize() {
		if (count < value.length) {
			value = Arrays.copyOf(value, count);
		}
	}

	public void setLength(int newLength) {
		if (newLength < 0) {
			throw new StringIndexOutOfBoundsException(newLength);
		}
		ensureCapacityInternal(newLength);
		if (count < newLength) {
			Arrays.fill(value, count, newLength, '\0');
		}
		count = newLength;
	}

	@Override
	public char charAt(int index) {
		if ((index < 0) || (index >= count)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return value[index];
	}

	public int codePointAt(int index) {
		if ((index < 0) || (index >= count)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return Character.codePointAtImpl(value, index, count);
	}

	public int codePointBefore(int index) {
		int i = index - 1;
		if ((i < 0) || (i >= count)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		return Character.codePointBeforeImpl(value, index, 0);
	}

	public int codePointCount(int beginIndex, int endIndex) {
		if ((beginIndex < 0) || (endIndex > count) || (beginIndex > endIndex)) {
			throw new IndexOutOfBoundsException();
		}
		return Character.codePointCountImpl(value, beginIndex, endIndex - beginIndex);
	}

	public int offsetByCodePoints(int index, int codePointOffset) {
		if ((index < 0) || (index > count)) {
			throw new IndexOutOfBoundsException();
		}
		return Character.offsetByCodePointsImpl(value, 0, count, index, codePointOffset);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if (srcBegin < 0) {
			throw new StringIndexOutOfBoundsException(srcBegin);
		}
		if ((srcEnd < 0) || (srcEnd > count)) {
			throw new StringIndexOutOfBoundsException(srcEnd);
		}
		if (srcBegin > srcEnd) {
			throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
		}
		System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}

	public void setCharAt(int index, char ch) {
		if ((index < 0) || (index >= count)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		value[index] = ch;
	}

	public AbstractStringBuilder append(Object obj) {
		return append(String.valueOf(obj));
	}

	public AbstractStringBuilder append(String str) {
		if (str == null) {
			return appendNull();
		}
		int len = str.length();
		ensureCapacityInternal(count + len);
		str.getChars(0, len, value, count);
		count += len;
		return this;
	}

	public AbstractStringBuilder append(StringBuffer sb) {
		if (sb == null) {
			return appendNull();
		}
		int len = sb.length();
		ensureCapacityInternal(count + len);
		sb.getChars(0, len, value, count);
		count += len;
		return this;
	}

	/**
	 * @since 1.8
	 */
	AbstractStringBuilder append(AbstractStringBuilder asb) {
		if (asb == null) {
			return appendNull();
		}
		int len = asb.length();
		ensureCapacityInternal(count + len);
		asb.getChars(0, len, value, count);
		count += len;
		return this;
	}

	@Override
	public AbstractStringBuilder append(CharSequence s) {
		if (s == null) {
			return appendNull();
		}
		if (s instanceof String) {
			return this.append((String) s);
		}
		if (s instanceof AbstractStringBuilder) {
			return this.append((AbstractStringBuilder) s);
		}
		return this.append(s, 0, s.length());
	}

	private AbstractStringBuilder appendNull() {
		int c = count;
		ensureCapacityInternal(c + 4);
		final char[] value = this.value;
		value[c++] = 'n';
		value[c++] = 'u';
		value[c++] = 'l';
		value[c++] = 'l';
		count = c;
		return this;
	}

	@Override
	public AbstractStringBuilder append(CharSequence s, int start, int end) {
		if (s == null) {
			s = "null";
		}
		if ((start < 0) || (start > end) || (end > s.length())) {
			throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", s.length() " + s.length());
		}
		int len = end - start;
		ensureCapacityInternal(count + len);
		for (int i = start, j = count; i < end; i++, j++) {
			value[j] = s.charAt(i);
		}
		count += len;
		return this;
	}

	public AbstractStringBuilder append(char[] str) {
		int len = str.length;
		ensureCapacityInternal(count + len);
		System.arraycopy(str, 0, value, count, len);
		count += len;
		return this;
	}

	public AbstractStringBuilder append(char str[], int offset, int len) {
		if (len > 0) {
			ensureCapacityInternal(count + len);
		}
		System.arraycopy(str, offset, value, count, len);
		count += len;
		return this;
	}

	public AbstractStringBuilder append(boolean b) {
		if (b) {
			ensureCapacityInternal(count + 4);
			value[count++] = 't';
			value[count++] = 'r';
			value[count++] = 'u';
			value[count++] = 'e';
		} else {
			ensureCapacityInternal(count + 5);
			value[count++] = 'f';
			value[count++] = 'a';
			value[count++] = 'l';
			value[count++] = 's';
			value[count++] = 'e';
		}
		return this;
	}

	@Override
	public AbstractStringBuilder append(char c) {
		ensureCapacityInternal(count + 1);
		value[count++] = c;
		return this;
	}

	public AbstractStringBuilder append(int i) {
		if (i == Integer.MIN_VALUE) {
			append("-2147483648");
			return this;
		}
		int appendedLength = (i < 0) ? Integer.stringSize(-i) + 1 : Integer.stringSize(i);
		int spaceNeeded = count + appendedLength;
		ensureCapacityInternal(spaceNeeded);
		Integer.getChars(i, spaceNeeded, value);
		count = spaceNeeded;
		return this;
	}

	public AbstractStringBuilder append(long l) {
		if (l == Long.MIN_VALUE) {
			append("-9223372036854775808");
			return this;
		}
		int appendedLength = (l < 0) ? Long.stringSize(-l) + 1 : Long.stringSize(l);
		int spaceNeeded = count + appendedLength;
		ensureCapacityInternal(spaceNeeded);
		Long.getChars(l, spaceNeeded, value);
		count = spaceNeeded;
		return this;
	}

	public AbstractStringBuilder append(float f) {
		FloatingDecimal.appendTo(f, this);
		return this;
	}

	/**
	 * Appends the string representation of the {@code double} argument to this
	 * sequence.
	 * <p>
	 * The overall effect is exactly as if the argument were converted to a
	 * string by the method {@link String#valueOf(double)}, and the characters
	 * of that string were then {@link #append(String) appended} to this
	 * character sequence.
	 *
	 * @param d
	 *            a {@code double}.
	 * @return a reference to this object.
	 */
	public AbstractStringBuilder append(double d) {
		FloatingDecimal.appendTo(d, this);
		return this;
	}

	/**
	 * Removes the characters in a substring of this sequence. The substring
	 * begins at the specified {@code start} and extends to the character at
	 * index {@code end - 1} or to the end of the sequence if no such character
	 * exists. If {@code start} is equal to {@code end}, no changes are made.
	 *
	 * @param start
	 *            The beginning index, inclusive.
	 * @param end
	 *            The ending index, exclusive.
	 * @return This object.
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code start} is negative, greater than {@code length()},
	 *             or greater than {@code end}.
	 */
	public AbstractStringBuilder delete(int start, int end) {
		if (start < 0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if (end > count) {
			end = count;
		}
		if (start > end) {
			throw new StringIndexOutOfBoundsException();
		}
		int len = end - start;
		if (len > 0) {
			System.arraycopy(value, start + len, value, start, count - end);
			count -= len;
		}
		return this;
	}

	/**
	 * Appends the string representation of the {@code codePoint} argument to
	 * this sequence.
	 *
	 * <p>
	 * The argument is appended to the contents of this sequence. The length of
	 * this sequence increases by {@link Character#charCount(int)
	 * Character.charCount(codePoint)}.
	 *
	 * <p>
	 * The overall effect is exactly as if the argument were converted to a
	 * {@code char} array by the method {@link Character#toChars(int)} and the
	 * character in that array were then {@link #append(char[]) appended} to
	 * this character sequence.
	 *
	 * @param codePoint
	 *            a Unicode code point
	 * @return a reference to this object.
	 * @exception IllegalArgumentException
	 *                if the specified {@code codePoint} isn't a valid Unicode
	 *                code point
	 */
	public AbstractStringBuilder appendCodePoint(int codePoint) {
		final int count = this.count;
		if (Character.isBmpCodePoint(codePoint)) {
			ensureCapacityInternal(count + 1);
			value[count] = (char) codePoint;
			this.count = count + 1;
		} else if (Character.isValidCodePoint(codePoint)) {
			ensureCapacityInternal(count + 2);
			Character.toSurrogates(codePoint, value, count);
			this.count = count + 2;
		} else {
			throw new IllegalArgumentException();
		}
		return this;
	}

	/**
	 * Removes the {@code char} at the specified position in this sequence. This
	 * sequence is shortened by one {@code char}.
	 *
	 * <p>
	 * Note: If the character at the given index is a supplementary character,
	 * this method does not remove the entire character. If correct handling of
	 * supplementary characters is required, determine the number of
	 * {@code char}s to remove by calling
	 * {@code Character.charCount(thisSequence.codePointAt(index))}, where
	 * {@code thisSequence} is this sequence.
	 *
	 * @param index
	 *            Index of {@code char} to remove
	 * @return This object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the {@code index} is negative or greater than or equal to
	 *             {@code length()}.
	 */
	public AbstractStringBuilder deleteCharAt(int index) {
		if ((index < 0) || (index >= count)) {
			throw new StringIndexOutOfBoundsException(index);
		}
		System.arraycopy(value, index + 1, value, index, count - index - 1);
		count--;
		return this;
	}

	/**
	 * Replaces the characters in a substring of this sequence with characters
	 * in the specified {@code String}. The substring begins at the specified
	 * {@code start} and extends to the character at index {@code end - 1} or to
	 * the end of the sequence if no such character exists. First the characters
	 * in the substring are removed and then the specified {@code String} is
	 * inserted at {@code start}. (This sequence will be lengthened to
	 * accommodate the specified String if necessary.)
	 *
	 * @param start
	 *            The beginning index, inclusive.
	 * @param end
	 *            The ending index, exclusive.
	 * @param str
	 *            String that will replace previous contents.
	 * @return This object.
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code start} is negative, greater than {@code length()},
	 *             or greater than {@code end}.
	 */
	public AbstractStringBuilder replace(int start, int end, String str) {
		if (start < 0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if (start > count) {
			throw new StringIndexOutOfBoundsException("start > length()");
		}
		if (start > end) {
			throw new StringIndexOutOfBoundsException("start > end");
		}
		if (end > count) {
			end = count;
		}
		int len = str.length();
		int newCount = (count + len) - (end - start);
		ensureCapacityInternal(newCount);
		System.arraycopy(value, end, value, start + len, count - end);
		str.getChars(value, start);
		count = newCount;
		return this;
	}

	/**
	 * Returns a new {@code String} that contains a subsequence of characters
	 * currently contained in this character sequence. The substring begins at
	 * the specified index and extends to the end of this sequence.
	 *
	 * @param start
	 *            The beginning index, inclusive.
	 * @return The new string.
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code start} is less than zero, or greater than the
	 *             length of this object.
	 */
	public String substring(int start) {
		return substring(start, count);
	}

	/**
	 * Returns a new character sequence that is a subsequence of this sequence.
	 *
	 * <p>
	 * An invocation of this method of the form
	 *
	 * <pre>
	 * {@code
	 * sb.subSequence(begin,&nbsp;end)}
	 * </pre>
	 *
	 * behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 * {@code
	 * sb.substring(begin,&nbsp;end)}
	 * </pre>
	 *
	 * This method is provided so that this class can implement the
	 * {@link CharSequence} interface.
	 *
	 * @param start
	 *            the start index, inclusive.
	 * @param end
	 *            the end index, exclusive.
	 * @return the specified subsequence.
	 *
	 * @throws IndexOutOfBoundsException
	 *             if {@code start} or {@code end} are negative, if {@code end}
	 *             is greater than {@code length()}, or if {@code start} is
	 *             greater than {@code end} JSR-51
	 */
	@Override
	public CharSequence subSequence(int start, int end) {
		return substring(start, end);
	}

	/**
	 * Returns a new {@code String} that contains a subsequence of characters
	 * currently contained in this sequence. The substring begins at the
	 * specified {@code start} and extends to the character at index
	 * {@code end - 1}.
	 *
	 * @param start
	 *            The beginning index, inclusive.
	 * @param end
	 *            The ending index, exclusive.
	 * @return The new string.
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code start} or {@code end} are negative or greater than
	 *             {@code length()}, or {@code start} is greater than
	 *             {@code end}.
	 */
	public String substring(int start, int end) {
		if (start < 0) {
			throw new StringIndexOutOfBoundsException(start);
		}
		if (end > count) {
			throw new StringIndexOutOfBoundsException(end);
		}
		if (start > end) {
			throw new StringIndexOutOfBoundsException(end - start);
		}
		return new String(value, start, end - start);
	}

	/**
	 * Inserts the string representation of a subarray of the {@code str} array
	 * argument into this sequence. The subarray begins at the specified
	 * {@code offset} and extends {@code len} {@code char}s. The characters of
	 * the subarray are inserted into this sequence at the position indicated by
	 * {@code index}. The length of this sequence increases by {@code len}
	 * {@code char}s.
	 *
	 * @param index
	 *            position at which to insert subarray.
	 * @param str
	 *            A {@code char} array.
	 * @param offset
	 *            the index of the first {@code char} in subarray to be
	 *            inserted.
	 * @param len
	 *            the number of {@code char}s in the subarray to be inserted.
	 * @return This object
	 * @throws StringIndexOutOfBoundsException
	 *             if {@code index} is negative or greater than
	 *             {@code length()}, or {@code offset} or {@code len} are
	 *             negative, or {@code (offset+len)} is greater than
	 *             {@code str.length}.
	 */
	public AbstractStringBuilder insert(int index, char[] str, int offset, int len) {
		if ((index < 0) || (index > length())) {
			throw new StringIndexOutOfBoundsException(index);
		}
		if ((offset < 0) || (len < 0) || (offset > (str.length - len))) {
			throw new StringIndexOutOfBoundsException("offset " + offset + ", len " + len + ", str.length " + str.length);
		}
		ensureCapacityInternal(count + len);
		System.arraycopy(value, index, value, index + len, count - index);
		System.arraycopy(str, offset, value, index, len);
		count += len;
		return this;
	}

	/**
	 * Inserts the string representation of the {@code Object} argument into
	 * this character sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(Object)}, and the characters
	 * of that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param obj
	 *            an {@code Object}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, Object obj) {
		return insert(offset, String.valueOf(obj));
	}

	/**
	 * Inserts the string into this character sequence.
	 * <p>
	 * The characters of the {@code String} argument are inserted, in order,
	 * into this sequence at the indicated offset, moving up any characters
	 * originally above that position and increasing the length of this sequence
	 * by the length of the argument. If {@code str} is {@code null}, then the
	 * four characters {@code "null"} are inserted into this sequence.
	 * <p>
	 * The character at index <i>k</i> in the new character sequence is equal
	 * to:
	 * <ul>
	 * <li>the character at index <i>k</i> in the old character sequence, if
	 * <i>k</i> is less than {@code offset}
	 * <li>the character at index <i>k</i>{@code -offset} in the argument
	 * {@code str}, if <i>k</i> is not less than {@code offset} but is less than
	 * {@code offset+str.length()}
	 * <li>the character at index <i>k</i>{@code -str.length()} in the old
	 * character sequence, if <i>k</i> is not less than
	 * {@code offset+str.length()}
	 * </ul>
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param str
	 *            a string.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, String str) {
		if ((offset < 0) || (offset > length())) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		if (str == null) {
			str = "null";
		}
		int len = str.length();
		ensureCapacityInternal(count + len);
		System.arraycopy(value, offset, value, offset + len, count - offset);
		str.getChars(value, offset);
		count += len;
		return this;
	}

	/**
	 * Inserts the string representation of the {@code char} array argument into
	 * this sequence.
	 * <p>
	 * The characters of the array argument are inserted into the contents of
	 * this sequence at the position indicated by {@code offset}. The length of
	 * this sequence increases by the length of the argument.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(char[])}, and the characters
	 * of that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param str
	 *            a character array.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, char[] str) {
		if ((offset < 0) || (offset > length())) {
			throw new StringIndexOutOfBoundsException(offset);
		}
		int len = str.length;
		ensureCapacityInternal(count + len);
		System.arraycopy(value, offset, value, offset + len, count - offset);
		System.arraycopy(str, 0, value, offset, len);
		count += len;
		return this;
	}

	/**
	 * Inserts the specified {@code CharSequence} into this sequence.
	 * <p>
	 * The characters of the {@code CharSequence} argument are inserted, in
	 * order, into this sequence at the indicated offset, moving up any
	 * characters originally above that position and increasing the length of
	 * this sequence by the length of the argument s.
	 * <p>
	 * The result of this method is exactly the same as if it were an invocation
	 * of this object's {@link #insert(int,CharSequence,int,int)
	 * insert}(dstOffset, s, 0, s.length()) method.
	 *
	 * <p>
	 * If {@code s} is {@code null}, then the four characters {@code "null"} are
	 * inserted into this sequence.
	 *
	 * @param dstOffset
	 *            the offset.
	 * @param s
	 *            the sequence to be inserted
	 * @return a reference to this object.
	 * @throws IndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int dstOffset, CharSequence s) {
		if (s == null) {
			s = "null";
		}
		if (s instanceof String) {
			return this.insert(dstOffset, (String) s);
		}
		return this.insert(dstOffset, s, 0, s.length());
	}

	/**
	 * Inserts a subsequence of the specified {@code CharSequence} into this
	 * sequence.
	 * <p>
	 * The subsequence of the argument {@code s} specified by {@code start} and
	 * {@code end} are inserted, in order, into this sequence at the specified
	 * destination offset, moving up any characters originally above that
	 * position. The length of this sequence is increased by
	 * {@code end - start}.
	 * <p>
	 * The character at index <i>k</i> in this sequence becomes equal to:
	 * <ul>
	 * <li>the character at index <i>k</i> in this sequence, if <i>k</i> is less
	 * than {@code dstOffset}
	 * <li>the character at index <i>k</i>{@code +start-dstOffset} in the
	 * argument {@code s}, if <i>k</i> is greater than or equal to
	 * {@code dstOffset} but is less than {@code dstOffset+end-start}
	 * <li>the character at index <i>k</i>{@code -(end-start)} in this sequence,
	 * if <i>k</i> is greater than or equal to {@code dstOffset+end-start}
	 * </ul>
	 * <p>
	 * The {@code dstOffset} argument must be greater than or equal to
	 * {@code 0}, and less than or equal to the {@linkplain #length() length} of
	 * this sequence.
	 * <p>
	 * The start argument must be nonnegative, and not greater than {@code end}.
	 * <p>
	 * The end argument must be greater than or equal to {@code start}, and less
	 * than or equal to the length of s.
	 *
	 * <p>
	 * If {@code s} is {@code null}, then this method inserts characters as if
	 * the s parameter was a sequence containing the four characters
	 * {@code "null"}.
	 *
	 * @param dstOffset
	 *            the offset in this sequence.
	 * @param s
	 *            the sequence to be inserted.
	 * @param start
	 *            the starting index of the subsequence to be inserted.
	 * @param end
	 *            the end index of the subsequence to be inserted.
	 * @return a reference to this object.
	 * @throws IndexOutOfBoundsException
	 *             if {@code dstOffset} is negative or greater than
	 *             {@code this.length()}, or {@code start} or {@code end} are
	 *             negative, or {@code start} is greater than {@code end} or
	 *             {@code end} is greater than {@code s.length()}
	 */
	public AbstractStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
		if (s == null) {
			s = "null";
		}
		if ((dstOffset < 0) || (dstOffset > length())) {
			throw new IndexOutOfBoundsException("dstOffset " + dstOffset);
		}
		if ((start < 0) || (end < 0) || (start > end) || (end > s.length())) {
			throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", s.length() " + s.length());
		}
		int len = end - start;
		ensureCapacityInternal(count + len);
		System.arraycopy(value, dstOffset, value, dstOffset + len, count - dstOffset);
		for (int i = start; i < end; i++) {
			value[dstOffset++] = s.charAt(i);
		}
		count += len;
		return this;
	}

	/**
	 * Inserts the string representation of the {@code boolean} argument into
	 * this sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(boolean)}, and the
	 * characters of that string were then {@link #insert(int,String) inserted}
	 * into this character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param b
	 *            a {@code boolean}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, boolean b) {
		return insert(offset, String.valueOf(b));
	}

	/**
	 * Inserts the string representation of the {@code char} argument into this
	 * sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(char)}, and the character in
	 * that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param c
	 *            a {@code char}.
	 * @return a reference to this object.
	 * @throws IndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, char c) {
		ensureCapacityInternal(count + 1);
		System.arraycopy(value, offset, value, offset + 1, count - offset);
		value[offset] = c;
		count += 1;
		return this;
	}

	/**
	 * Inserts the string representation of the second {@code int} argument into
	 * this sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(int)}, and the characters of
	 * that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param i
	 *            an {@code int}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, int i) {
		return insert(offset, String.valueOf(i));
	}

	/**
	 * Inserts the string representation of the {@code long} argument into this
	 * sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(long)}, and the characters
	 * of that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param l
	 *            a {@code long}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, long l) {
		return insert(offset, String.valueOf(l));
	}

	/**
	 * Inserts the string representation of the {@code float} argument into this
	 * sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(float)}, and the characters
	 * of that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param f
	 *            a {@code float}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, float f) {
		return insert(offset, String.valueOf(f));
	}

	/**
	 * Inserts the string representation of the {@code double} argument into
	 * this sequence.
	 * <p>
	 * The overall effect is exactly as if the second argument were converted to
	 * a string by the method {@link String#valueOf(double)}, and the characters
	 * of that string were then {@link #insert(int,String) inserted} into this
	 * character sequence at the indicated offset.
	 * <p>
	 * The {@code offset} argument must be greater than or equal to {@code 0},
	 * and less than or equal to the {@linkplain #length() length} of this
	 * sequence.
	 *
	 * @param offset
	 *            the offset.
	 * @param d
	 *            a {@code double}.
	 * @return a reference to this object.
	 * @throws StringIndexOutOfBoundsException
	 *             if the offset is invalid.
	 */
	public AbstractStringBuilder insert(int offset, double d) {
		return insert(offset, String.valueOf(d));
	}

	/**
	 * Returns the index within this string of the first occurrence of the
	 * specified substring. The integer returned is the smallest value <i>k</i>
	 * such that:
	 *
	 * <pre>
	 * {@code
	 * this.toString().startsWith(str, <i>k</i>)
	 * }
	 * </pre>
	 *
	 * is {@code true}.
	 *
	 * @param str
	 *            any string.
	 * @return if the string argument occurs as a substring within this object,
	 *         then the index of the first character of the first such substring
	 *         is returned; if it does not occur as a substring, {@code -1} is
	 *         returned.
	 */
	public int indexOf(String str) {
		return indexOf(str, 0);
	}

	public int indexOf(String str, int fromIndex) {
		return String.indexOf(value, 0, count, str, fromIndex);
	}

	public int lastIndexOf(String str) {
		return lastIndexOf(str, count);
	}

	public int lastIndexOf(String str, int fromIndex) {
		return String.lastIndexOf(value, 0, count, str, fromIndex);
	}

	public AbstractStringBuilder reverse() {
		boolean hasSurrogates = false;
		int n = count - 1;
		for (int j = (n - 1) >> 1; j >= 0; j--) {
			int k = n - j;
			char cj = value[j];
			char ck = value[k];
			value[j] = ck;
			value[k] = cj;
			if (Character.isSurrogate(cj) || Character.isSurrogate(ck)) {
				hasSurrogates = true;
			}
		}
		if (hasSurrogates) {
			reverseAllValidSurrogatePairs();
		}
		return this;
	}

	private void reverseAllValidSurrogatePairs() {
		for (int i = 0; i < (count - 1); i++) {
			char c2 = value[i];
			if (Character.isLowSurrogate(c2)) {
				char c1 = value[i + 1];
				if (Character.isHighSurrogate(c1)) {
					value[i++] = c1;
					value[i] = c2;
				}
			}
		}
	}

	@Override
	public abstract String toString();

	final char[] getValue() {
		return value;
	}
}
