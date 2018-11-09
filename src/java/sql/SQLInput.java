package java.sql;

public interface SQLInput {
	String readString() throws SQLException;

	boolean readBoolean() throws SQLException;

	byte readByte() throws SQLException;

	short readShort() throws SQLException;

	int readInt() throws SQLException;

	long readLong() throws SQLException;

	float readFloat() throws SQLException;

	double readDouble() throws SQLException;

	java.math.BigDecimal readBigDecimal() throws SQLException;

	byte[] readBytes() throws SQLException;

	java.sql.Date readDate() throws SQLException;

	java.sql.Time readTime() throws SQLException;

	java.sql.Timestamp readTimestamp() throws SQLException;

	java.io.Reader readCharacterStream() throws SQLException;

	java.io.InputStream readAsciiStream() throws SQLException;

	java.io.InputStream readBinaryStream() throws SQLException;

	Object readObject() throws SQLException;

	Ref readRef() throws SQLException;

	Blob readBlob() throws SQLException;

	Clob readClob() throws SQLException;

	Array readArray() throws SQLException;

	boolean wasNull() throws SQLException;

	// ---------------------------- JDBC 3.0 -------------------------
	/**
	 * Reads an SQL <code>DATALINK</code> value from the stream and returns it
	 * as a <code>java.net.URL</code> object in the Java programming language.
	 *
	 * @return a <code>java.net.URL</code> object.
	 * @exception SQLException
	 *                if a database access error occurs, or if a URL is
	 *                malformed
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.4
	 */
	java.net.URL readURL() throws SQLException;

	// ---------------------------- JDBC 4.0 -------------------------
	/**
	 * Reads an SQL <code>NCLOB</code> value from the stream and returns it as a
	 * <code>NClob</code> object in the Java programming language.
	 *
	 * @return a <code>NClob</code> object representing data of the SQL
	 *         <code>NCLOB</code> value at the head of the stream;
	 *         <code>null</code> if the value read is SQL <code>NULL</code>
	 * @exception SQLException
	 *                if a database access error occurs
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.6
	 */
	NClob readNClob() throws SQLException;

	/**
	 * Reads the next attribute in the stream and returns it as a
	 * <code>String</code> in the Java programming language. It is intended for
	 * use when accessing <code>NCHAR</code>,<code>NVARCHAR</code> and
	 * <code>LONGNVARCHAR</code> columns.
	 *
	 * @return the attribute; if the value is SQL <code>NULL</code>, returns
	 *         <code>null</code>
	 * @exception SQLException
	 *                if a database access error occurs
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.6
	 */
	String readNString() throws SQLException;

	SQLXML readSQLXML() throws SQLException;

	RowId readRowId() throws SQLException;

	/**
	 * Reads the next attribute in the stream and returns it as an
	 * {@code Object} in the Java programming language. The actual type of the
	 * object returned is determined by the specified Java data type, and any
	 * customizations present in this stream's type map.
	 *
	 * <P>
	 * A type map is registered with the stream by the JDBC driver before the
	 * stream is passed to the application.
	 *
	 * <P>
	 * When the attribute at the head of the stream is an SQL {@code NULL} the
	 * method returns {@code null}. If the attribute is an SQL structured or
	 * distinct type, it determines the SQL type of the attribute at the head of
	 * the stream. If the stream's type map has an entry for that SQL type, the
	 * driver constructs an object of the appropriate class and calls the method
	 * {@code SQLData.readSQL} on that object, which reads additional data from
	 * the stream, using the protocol described for that method.
	 * <p>
	 * The default implementation will throw
	 * {@code SQLFeatureNotSupportedException}
	 *
	 * @param <T>
	 *            the type of the class modeled by this Class object
	 * @param type
	 *            Class representing the Java data type to convert the attribute
	 *            to.
	 * @return the attribute at the head of the stream as an {@code Object} in
	 *         the Java programming language;{@code null} if the attribute is
	 *         SQL {@code NULL}
	 * @exception SQLException
	 *                if a database access error occurs
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.8
	 */
	default <T> T readObject(Class<T> type) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
