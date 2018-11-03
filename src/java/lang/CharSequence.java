package java.lang;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public interface CharSequence {
	int length();

	char charAt(int index);

	CharSequence subSequence(int start, int end);

	@Override
	public String toString();

	/**
	 * Returns a stream of {@code int} zero-extending the {@code char} values
	 * from this sequence. Any char which maps to a
	 * <a href="{@docRoot}/java/lang/Character.html#unicode">surrogate code
	 * point</a> is passed through uninterpreted.
	 *
	 * <p>
	 * If the sequence is mutated while the stream is being read, the result is
	 * undefined.
	 *
	 * @return an IntStream of char values from this sequence
	 * @since 1.8
	 */
	public default IntStream chars() {
		class CharIterator implements PrimitiveIterator.OfInt {
			int cur = 0;

			@Override
			public boolean hasNext() {
				return cur < length();
			}

			@Override
			public int nextInt() {
				if (hasNext()) {
					return charAt(cur++);
				} else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void forEachRemaining(IntConsumer block) {
				for (; cur < length(); cur++) {
					block.accept(charAt(cur));
				}
			}
		}
		return StreamSupport.intStream(() -> Spliterators.spliterator(new CharIterator(), length(), Spliterator.ORDERED), Spliterator.SUBSIZED | Spliterator.SIZED | Spliterator.ORDERED, false);
	}

	/**
	 * Returns a stream of code point values from this sequence. Any surrogate
	 * pairs encountered in the sequence are combined as if by
	 * {@linkplain Character#toCodePoint Character.toCodePoint} and the result
	 * is passed to the stream. Any other code units, including ordinary BMP
	 * characters, unpaired surrogates, and undefined code units, are
	 * zero-extended to {@code int} values which are then passed to the stream.
	 *
	 * <p>
	 * If the sequence is mutated while the stream is being read, the result is
	 * undefined.
	 *
	 * @return an IntStream of Unicode code points from this sequence
	 * @since 1.8
	 */
	public default IntStream codePoints() {
		class CodePointIterator implements PrimitiveIterator.OfInt {
			int cur = 0;

			@Override
			public void forEachRemaining(IntConsumer block) {
				final int length = length();
				int i = cur;
				try {
					while (i < length) {
						char c1 = charAt(i++);
						if (!Character.isHighSurrogate(c1) || (i >= length)) {
							block.accept(c1);
						} else {
							char c2 = charAt(i);
							if (Character.isLowSurrogate(c2)) {
								i++;
								block.accept(Character.toCodePoint(c1, c2));
							} else {
								block.accept(c1);
							}
						}
					}
				} finally {
					cur = i;
				}
			}

			@Override
			public boolean hasNext() {
				return cur < length();
			}

			@Override
			public int nextInt() {
				final int length = length();
				if (cur >= length) {
					throw new NoSuchElementException();
				}
				char c1 = charAt(cur++);
				if (Character.isHighSurrogate(c1) && (cur < length)) {
					char c2 = charAt(cur);
					if (Character.isLowSurrogate(c2)) {
						cur++;
						return Character.toCodePoint(c1, c2);
					}
				}
				return c1;
			}
		}
		return StreamSupport.intStream(() -> Spliterators.spliteratorUnknownSize(new CodePointIterator(), Spliterator.ORDERED), Spliterator.ORDERED, false);
	}
}
