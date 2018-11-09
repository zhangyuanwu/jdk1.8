package java.sql;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class BatchUpdateException extends SQLException {
	public BatchUpdateException(String reason, String SQLState, int vendorCode, int[] updateCounts) {
		super(reason, SQLState, vendorCode);
		this.updateCounts = (updateCounts == null) ? null : Arrays.copyOf(updateCounts, updateCounts.length);
		longUpdateCounts = (updateCounts == null) ? null : copyUpdateCount(updateCounts);
	}

	public BatchUpdateException(String reason, String SQLState, int[] updateCounts) {
		this(reason, SQLState, 0, updateCounts);
	}

	public BatchUpdateException(String reason, int[] updateCounts) {
		this(reason, null, 0, updateCounts);
	}

	public BatchUpdateException(int[] updateCounts) {
		this(null, null, 0, updateCounts);
	}

	public BatchUpdateException() {
		this(null, null, 0, null);
	}

	public BatchUpdateException(Throwable cause) {
		this((cause == null ? null : cause.toString()), null, 0, (int[]) null, cause);
	}

	public BatchUpdateException(int[] updateCounts, Throwable cause) {
		this((cause == null ? null : cause.toString()), null, 0, updateCounts, cause);
	}

	public BatchUpdateException(String reason, int[] updateCounts, Throwable cause) {
		this(reason, null, 0, updateCounts, cause);
	}

	public BatchUpdateException(String reason, String SQLState, int[] updateCounts, Throwable cause) {
		this(reason, SQLState, 0, updateCounts, cause);
	}

	public BatchUpdateException(String reason, String SQLState, int vendorCode, int[] updateCounts, Throwable cause) {
		super(reason, SQLState, vendorCode, cause);
		this.updateCounts = (updateCounts == null) ? null : Arrays.copyOf(updateCounts, updateCounts.length);
		longUpdateCounts = (updateCounts == null) ? null : copyUpdateCount(updateCounts);
	}

	/**
	 * Retrieves the update count for each update statement in the batch update
	 * that executed successfully before this exception occurred. A driver that
	 * implements batch updates may or may not continue to process the remaining
	 * commands in a batch when one of the commands fails to execute properly.
	 * If the driver continues processing commands, the array returned by this
	 * method will have as many elements as there are commands in the batch;
	 * otherwise, it will contain an update count for each command that executed
	 * successfully before the <code>BatchUpdateException</code> was thrown.
	 * <P>
	 * The possible return values for this method were modified for the Java 2
	 * SDK, Standard Edition, version 1.3. This was done to accommodate the new
	 * option of continuing to process commands in a batch update after a
	 * <code>BatchUpdateException</code> object has been thrown.
	 *
	 * @return an array of <code>int</code> containing the update counts for the
	 *         updates that were executed successfully before this error
	 *         occurred. Or, if the driver continues to process commands after
	 *         an error, one of the following for every command in the batch:
	 *         <OL>
	 *         <LI>an update count
	 *         <LI><code>Statement.SUCCESS_NO_INFO</code> to indicate that the
	 *         command executed successfully but the number of rows affected is
	 *         unknown
	 *         <LI><code>Statement.EXECUTE_FAILED</code> to indicate that the
	 *         command failed to execute successfully
	 *         </OL>
	 * @since 1.3
	 * @see #getLargeUpdateCounts()
	 */
	public int[] getUpdateCounts() {
		return (updateCounts == null) ? null : Arrays.copyOf(updateCounts, updateCounts.length);
	}

	/**
	 * Constructs a <code>BatchUpdateException</code> object initialized with a
	 * given <code>reason</code>, <code>SQLState</code>, <code>vendorCode</code>
	 * <code>cause</code> and <code>updateCounts</code>.
	 * <p>
	 * This constructor should be used when the returned update count may exceed
	 * {@link Integer#MAX_VALUE}.
	 * <p>
	 *
	 * @param reason
	 *            a description of the error
	 * @param SQLState
	 *            an XOPEN or SQL:2003 code identifying the exception
	 * @param vendorCode
	 *            an exception code used by a particular database vendor
	 * @param updateCounts
	 *            an array of <code>long</code>, with each element indicating
	 *            the update count, <code>Statement.SUCCESS_NO_INFO</code> or
	 *            <code>Statement.EXECUTE_FAILED</code> for each SQL command in
	 *            the batch for JDBC drivers that continue processing after a
	 *            command failure; an update count or
	 *            <code>Statement.SUCCESS_NO_INFO</code> for each SQL command in
	 *            the batch prior to the failure for JDBC drivers that stop
	 *            processing after a command failure
	 * @param cause
	 *            the underlying reason for this <code>SQLException</code>
	 *            (which is saved for later retrieval by the
	 *            <code>getCause()</code> method); may be null indicating the
	 *            cause is non-existent or unknown.
	 * @since 1.8
	 */
	public BatchUpdateException(String reason, String SQLState, int vendorCode, long[] updateCounts, Throwable cause) {
		super(reason, SQLState, vendorCode, cause);
		longUpdateCounts = (updateCounts == null) ? null : Arrays.copyOf(updateCounts, updateCounts.length);
		this.updateCounts = (longUpdateCounts == null) ? null : copyUpdateCount(longUpdateCounts);
	}

	/**
	 * Retrieves the update count for each update statement in the batch update
	 * that executed successfully before this exception occurred. A driver that
	 * implements batch updates may or may not continue to process the remaining
	 * commands in a batch when one of the commands fails to execute properly.
	 * If the driver continues processing commands, the array returned by this
	 * method will have as many elements as there are commands in the batch;
	 * otherwise, it will contain an update count for each command that executed
	 * successfully before the <code>BatchUpdateException</code> was thrown.
	 * <p>
	 * This method should be used when {@code Statement.executeLargeBatch} is
	 * invoked and the returned update count may exceed
	 * {@link Integer#MAX_VALUE}.
	 * <p>
	 *
	 * @return an array of <code>long</code> containing the update counts for
	 *         the updates that were executed successfully before this error
	 *         occurred. Or, if the driver continues to process commands after
	 *         an error, one of the following for every command in the batch:
	 *         <OL>
	 *         <LI>an update count
	 *         <LI><code>Statement.SUCCESS_NO_INFO</code> to indicate that the
	 *         command executed successfully but the number of rows affected is
	 *         unknown
	 *         <LI><code>Statement.EXECUTE_FAILED</code> to indicate that the
	 *         command failed to execute successfully
	 *         </OL>
	 * @since 1.8
	 */
	public long[] getLargeUpdateCounts() {
		return (longUpdateCounts == null) ? null : Arrays.copyOf(longUpdateCounts, longUpdateCounts.length);
	}

	/**
	 * The array that describes the outcome of a batch execution.
	 *
	 * @serial
	 * @since 1.2
	 */
	private int[] updateCounts;
	/*
	 * Starting with Java SE 8, JDBC has added support for returning an update
	 * count > Integer.MAX_VALUE. Because of this the following changes were
	 * made to BatchUpdateException: <ul> <li>Add field longUpdateCounts</li>
	 * <li>Add Constructorr which takes long[] for update counts</li> <li>Add
	 * getLargeUpdateCounts method</li> </ul> When any of the constructors are
	 * called, the int[] and long[] updateCount fields are populated by copying
	 * the one array to each other.
	 *
	 * As the JDBC driver passes in the updateCounts, there has always been the
	 * possiblity for overflow and BatchUpdateException does not need to account
	 * for that, it simply copies the arrays.
	 *
	 * JDBC drivers should always use the constructor that specifies long[] and
	 * JDBC application developers should call getLargeUpdateCounts.
	 */
	/**
	 * The array that describes the outcome of a batch execution.
	 *
	 * @serial
	 * @since 1.8
	 */
	private long[] longUpdateCounts;
	private static final long serialVersionUID = 5977529877145521757L;

	private static long[] copyUpdateCount(int[] uc) {
		long[] copy = new long[uc.length];
		for (int i = 0; i < uc.length; i++) {
			copy[i] = uc[i];
		}
		return copy;
	}

	private static int[] copyUpdateCount(long[] uc) {
		int[] copy = new int[uc.length];
		for (int i = 0; i < uc.length; i++) {
			copy[i] = (int) uc[i];
		}
		return copy;
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		ObjectInputStream.GetField fields = s.readFields();
		int[] tmp = (int[]) fields.get("updateCounts", null);
		long[] tmp2 = (long[]) fields.get("longUpdateCounts", null);
		if ((tmp != null) && (tmp2 != null) && (tmp.length != tmp2.length)) {
			throw new InvalidObjectException("update counts are not the expected size");
		}
		if (tmp != null) {
			updateCounts = tmp.clone();
		}
		if (tmp2 != null) {
			longUpdateCounts = tmp2.clone();
		}
		if ((updateCounts == null) && (longUpdateCounts != null)) {
			updateCounts = copyUpdateCount(longUpdateCounts);
		}
		if ((longUpdateCounts == null) && (updateCounts != null)) {
			longUpdateCounts = copyUpdateCount(updateCounts);
		}
	}

	private void writeObject(ObjectOutputStream s) throws IOException, ClassNotFoundException {
		ObjectOutputStream.PutField fields = s.putFields();
		fields.put("updateCounts", updateCounts);
		fields.put("longUpdateCounts", longUpdateCounts);
		s.writeFields();
	}
}
