package java.sql;

public interface SQLOutput {
	void writeString(String x) throws SQLException;

	void writeBoolean(boolean x) throws SQLException;

	void writeByte(byte x) throws SQLException;

	void writeShort(short x) throws SQLException;

	void writeInt(int x) throws SQLException;

	void writeLong(long x) throws SQLException;

	void writeFloat(float x) throws SQLException;

	void writeDouble(double x) throws SQLException;

	void writeBigDecimal(java.math.BigDecimal x) throws SQLException;

	void writeBytes(byte[] x) throws SQLException;

	void writeDate(java.sql.Date x) throws SQLException;

	void writeTime(java.sql.Time x) throws SQLException;

	void writeTimestamp(java.sql.Timestamp x) throws SQLException;

	void writeCharacterStream(java.io.Reader x) throws SQLException;

	void writeAsciiStream(java.io.InputStream x) throws SQLException;

	void writeBinaryStream(java.io.InputStream x) throws SQLException;

	void writeObject(SQLData x) throws SQLException;

	void writeRef(Ref x) throws SQLException;

	void writeBlob(Blob x) throws SQLException;

	void writeClob(Clob x) throws SQLException;

	void writeStruct(Struct x) throws SQLException;

	void writeArray(Array x) throws SQLException;

	// --------------------------- JDBC 3.0 ------------------------
	/**
	 * Writes a SQL <code>DATALINK</code> value to the stream.
	 *
	 * @param x
	 *            a <code>java.net.URL</code> object representing the data of
	 *            SQL DATALINK type
	 *
	 * @exception SQLException
	 *                if a database access error occurs
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.4
	 */
	void writeURL(java.net.URL x) throws SQLException;

	// --------------------------- JDBC 4.0 ------------------------
	/**
	 * Writes the next attribute to the stream as a <code>String</code> in the
	 * Java programming language. The driver converts this to a SQL
	 * <code>NCHAR</code> or <code>NVARCHAR</code> or <code>LONGNVARCHAR</code>
	 * value (depending on the argument's size relative to the driver's limits
	 * on <code>NVARCHAR</code> values) when it sends it to the stream.
	 *
	 * @param x
	 *            the value to pass to the database
	 * @exception SQLException
	 *                if a database access error occurs
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void writeNString(String x) throws SQLException;

	void writeNClob(NClob x) throws SQLException;

	void writeRowId(RowId x) throws SQLException;

	void writeSQLXML(SQLXML x) throws SQLException;

	/**
	 * Writes to the stream the data contained in the given object. The object
	 * will be converted to the specified targetSqlType before being sent to the
	 * stream.
	 * <p>
	 * When the {@code object} is {@code null}, this method writes an SQL
	 * {@code NULL} to the stream.
	 * <p>
	 * If the object has a custom mapping (is of a class implementing the
	 * interface {@code SQLData}), the JDBC driver should call the method
	 * {@code SQLData.writeSQL} to write it to the SQL data stream. If, on the
	 * other hand, the object is of a class implementing {@code Ref},
	 * {@code Blob}, {@code Clob}, {@code NClob}, {@code Struct},
	 * {@code java.net.URL}, or {@code Array}, the driver should pass it to the
	 * database as a value of the corresponding SQL type.
	 * <P>
	 * The default implementation will throw
	 * {@code SQLFeatureNotSupportedException}
	 *
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type to be sent to the database.
	 * @exception SQLException
	 *                if a database access error occurs or if the Java Object
	 *                specified by x is an InputStream or Reader object and the
	 *                value of the scale parameter is less than zero
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this data type
	 * @see JDBCType
	 * @see SQLType
	 * @since 1.8
	 */
	default void writeObject(Object x, SQLType targetSqlType) throws SQLException {
		throw new SQLFeatureNotSupportedException();
	}
}
