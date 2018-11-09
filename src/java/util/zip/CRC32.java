package java.util.zip;

import java.nio.ByteBuffer;
import sun.nio.ch.DirectBuffer;

public class CRC32 implements Checksum {
	private int crc;

	public CRC32() {
	}

	public void update(int b) {
		crc = update(crc, b);
	}

	public void update(byte[] b, int off, int len) {
		if (b == null) {
			throw new NullPointerException();
		}
		if ((off < 0) || (len < 0) || (off > (b.length - len))) {
			throw new ArrayIndexOutOfBoundsException();
		}
		crc = updateBytes(crc, b, off, len);
	}

	public void update(byte[] b) {
		crc = updateBytes(crc, b, 0, b.length);
	}

	/**
	 * Updates the checksum with the bytes from the specified buffer.
	 *
	 * The checksum is updated using buffer.{@link java.nio.Buffer#remaining()
	 * remaining()} bytes starting at buffer.{@link java.nio.Buffer#position()
	 * position()} Upon return, the buffer's position will be updated to its
	 * limit; its limit will not have been changed.
	 *
	 * @param buffer
	 *            the ByteBuffer to update the checksum with
	 * @since 1.8
	 */
	public void update(ByteBuffer buffer) {
		int pos = buffer.position();
		int limit = buffer.limit();
		assert (pos <= limit);
		int rem = limit - pos;
		if (rem <= 0) {
			return;
		}
		if (buffer instanceof DirectBuffer) {
			crc = updateByteBuffer(crc, ((DirectBuffer) buffer).address(), pos, rem);
		} else if (buffer.hasArray()) {
			crc = updateBytes(crc, buffer.array(), pos + buffer.arrayOffset(), rem);
		} else {
			byte[] b = new byte[rem];
			buffer.get(b);
			crc = updateBytes(crc, b, 0, b.length);
		}
		buffer.position(limit);
	}

	public void reset() {
		crc = 0;
	}

	public long getValue() {
		return crc & 0xffffffffL;
	}

	private native static int update(int crc, int b);

	private native static int updateBytes(int crc, byte[] b, int off, int len);

	private native static int updateByteBuffer(int adler, long addr, int off, int len);
}
