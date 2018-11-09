package java.sql;

import java.math.BigDecimal;
import java.util.Calendar;
import java.io.Reader;
import java.io.InputStream;

public interface PreparedStatement extends Statement {
	ResultSet executeQuery() throws SQLException;

	int executeUpdate() throws SQLException;

	void setNull(int parameterIndex, int sqlType) throws SQLException;

	void setBoolean(int parameterIndex, boolean x) throws SQLException;

	void setByte(int parameterIndex, byte x) throws SQLException;

	void setShort(int parameterIndex, short x) throws SQLException;

	void setInt(int parameterIndex, int x) throws SQLException;

	void setLong(int parameterIndex, long x) throws SQLException;

	void setFloat(int parameterIndex, float x) throws SQLException;

	void setDouble(int parameterIndex, double x) throws SQLException;

	void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException;

	void setString(int parameterIndex, String x) throws SQLException;

	void setBytes(int parameterIndex, byte x[]) throws SQLException;

	void setDate(int parameterIndex, java.sql.Date x) throws SQLException;

	void setTime(int parameterIndex, java.sql.Time x) throws SQLException;

	void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException;

	void setAsciiStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException;

	@Deprecated
	void setUnicodeStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException;

	void setBinaryStream(int parameterIndex, java.io.InputStream x, int length) throws SQLException;

	void clearParameters() throws SQLException;

	// ----------------------------------------------------------------------
	// Advanced features:
	/**
	 * Sets the value of the designated parameter with the given object.
	 *
	 * This method is similar to
	 * {@link #setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)},
	 * except that it assumes a scale of zero.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type (as defined in java.sql.Types) to be sent to the
	 *            database
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                PreparedStatement
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support the specified
	 *                targetSqlType
	 * @see Types
	 */
	void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException;

	/**
	 * <p>
	 * Sets the value of the designated parameter using the given object.
	 *
	 * <p>
	 * The JDBC specification specifies a standard mapping from Java
	 * <code>Object</code> types to SQL types. The given argument will be
	 * converted to the corresponding SQL type before being sent to the
	 * database.
	 *
	 * <p>
	 * Note that this method may be used to pass datatabase- specific abstract
	 * data types, by using a driver-specific Java type.
	 *
	 * If the object is of a class implementing the interface
	 * <code>SQLData</code>, the JDBC driver should call the method
	 * <code>SQLData.writeSQL</code> to write it to the SQL data stream. If, on
	 * the other hand, the object is of a class implementing <code>Ref</code>,
	 * <code>Blob</code>, <code>Clob</code>, <code>NClob</code>,
	 * <code>Struct</code>, <code>java.net.URL</code>, <code>RowId</code>,
	 * <code>SQLXML</code> or <code>Array</code>, the driver should pass it to
	 * the database as a value of the corresponding SQL type.
	 * <P>
	 * <b>Note:</b> Not all databases allow for a non-typed Null to be sent to
	 * the backend. For maximum portability, the <code>setNull</code> or the
	 * <code>setObject(int parameterIndex, Object x, int sqlType)</code> method
	 * should be used instead of
	 * <code>setObject(int parameterIndex, Object x)</code>.
	 * <p>
	 * <b>Note:</b> This method throws an exception if there is an ambiguity,
	 * for example, if the object is of a class implementing more than one of
	 * the interfaces named above.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs; this method is called on a closed
	 *                <code>PreparedStatement</code> or the type of the given
	 *                object is ambiguous
	 */
	void setObject(int parameterIndex, Object x) throws SQLException;

	/**
	 * Executes the SQL statement in this <code>PreparedStatement</code> object,
	 * which may be any kind of SQL statement. Some prepared statements return
	 * multiple results; the <code>execute</code> method handles these complex
	 * statements as well as the simpler form of statements handled by the
	 * methods <code>executeQuery</code> and <code>executeUpdate</code>.
	 * <P>
	 * The <code>execute</code> method returns a <code>boolean</code> to
	 * indicate the form of the first result. You must call either the method
	 * <code>getResultSet</code> or <code>getUpdateCount</code> to retrieve the
	 * result; you must call <code>getMoreResults</code> to move to any
	 * subsequent result(s).
	 *
	 * @return <code>true</code> if the first result is a <code>ResultSet</code>
	 *         object; <code>false</code> if the first result is an update count
	 *         or there is no result
	 * @exception SQLException
	 *                if a database access error occurs; this method is called
	 *                on a closed <code>PreparedStatement</code> or an argument
	 *                is supplied to this method
	 * @throws SQLTimeoutException
	 *             when the driver has determined that the timeout value that
	 *             was specified by the {@code setQueryTimeout} method has been
	 *             exceeded and has at least attempted to cancel the currently
	 *             running {@code Statement}
	 * @see Statement#execute
	 * @see Statement#getResultSet
	 * @see Statement#getUpdateCount
	 * @see Statement#getMoreResults
	 *
	 */
	boolean execute() throws SQLException;

	// --------------------------JDBC 2.0-----------------------------
	/**
	 * Adds a set of parameters to this <code>PreparedStatement</code> object's
	 * batch of commands.
	 *
	 * @exception SQLException
	 *                if a database access error occurs or this method is called
	 *                on a closed <code>PreparedStatement</code>
	 * @see Statement#addBatch
	 * @since 1.2
	 */
	void addBatch() throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The data
	 * will be read from the stream as needed until end-of-file is reached. The
	 * JDBC driver will do any necessary conversion from UNICODE to the database
	 * char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            the <code>java.io.Reader</code> object that contains the
	 *            Unicode data
	 * @param length
	 *            the number of characters in the stream
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.2
	 */
	void setCharacterStream(int parameterIndex, java.io.Reader reader, int length) throws SQLException;

	/**
	 * Sets the designated parameter to the given
	 * <code>REF(&lt;structured-type&gt;)</code> value. The driver converts this
	 * to an SQL <code>REF</code> value when it sends it to the database.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an SQL <code>REF</code> value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.2
	 */
	void setRef(int parameterIndex, Ref x) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.Blob</code>
	 * object. The driver converts this to an SQL <code>BLOB</code> value when
	 * it sends it to the database.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            a <code>Blob</code> object that maps an SQL <code>BLOB</code>
	 *            value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.2
	 */
	void setBlob(int parameterIndex, Blob x) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.Clob</code>
	 * object. The driver converts this to an SQL <code>CLOB</code> value when
	 * it sends it to the database.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            a <code>Clob</code> object that maps an SQL <code>CLOB</code>
	 *            value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.2
	 */
	void setClob(int parameterIndex, Clob x) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.Array</code>
	 * object. The driver converts this to an SQL <code>ARRAY</code> value when
	 * it sends it to the database.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            an <code>Array</code> object that maps an SQL
	 *            <code>ARRAY</code> value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.2
	 */
	void setArray(int parameterIndex, Array x) throws SQLException;

	/**
	 * Retrieves a <code>ResultSetMetaData</code> object that contains
	 * information about the columns of the <code>ResultSet</code> object that
	 * will be returned when this <code>PreparedStatement</code> object is
	 * executed.
	 * <P>
	 * Because a <code>PreparedStatement</code> object is precompiled, it is
	 * possible to know about the <code>ResultSet</code> object that it will
	 * return without having to execute it. Consequently, it is possible to
	 * invoke the method <code>getMetaData</code> on a
	 * <code>PreparedStatement</code> object rather than waiting to execute it
	 * and then invoking the <code>ResultSet.getMetaData</code> method on the
	 * <code>ResultSet</code> object that is returned.
	 * <P>
	 * <B>NOTE:</B> Using this method may be expensive for some drivers due to
	 * the lack of underlying DBMS support.
	 *
	 * @return the description of a <code>ResultSet</code> object's columns or
	 *         <code>null</code> if the driver cannot return a
	 *         <code>ResultSetMetaData</code> object
	 * @exception SQLException
	 *                if a database access error occurs or this method is called
	 *                on a closed <code>PreparedStatement</code>
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support this method
	 * @since 1.2
	 */
	ResultSetMetaData getMetaData() throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.Date</code>
	 * value, using the given <code>Calendar</code> object. The driver uses the
	 * <code>Calendar</code> object to construct an SQL <code>DATE</code> value,
	 * which the driver then sends to the database. With a <code>Calendar</code>
	 * object, the driver can calculate the date taking into account a custom
	 * timezone. If no <code>Calendar</code> object is specified, the driver
	 * uses the default timezone, which is that of the virtual machine running
	 * the application.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the date
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.2
	 */
	void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.Time</code>
	 * value, using the given <code>Calendar</code> object. The driver uses the
	 * <code>Calendar</code> object to construct an SQL <code>TIME</code> value,
	 * which the driver then sends to the database. With a <code>Calendar</code>
	 * object, the driver can calculate the time taking into account a custom
	 * timezone. If no <code>Calendar</code> object is specified, the driver
	 * uses the default timezone, which is that of the virtual machine running
	 * the application.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the time
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.2
	 */
	void setTime(int parameterIndex, java.sql.Time x, Calendar cal) throws SQLException;

	/**
	 * Sets the designated parameter to the given
	 * <code>java.sql.Timestamp</code> value, using the given
	 * <code>Calendar</code> object. The driver uses the <code>Calendar</code>
	 * object to construct an SQL <code>TIMESTAMP</code> value, which the driver
	 * then sends to the database. With a <code>Calendar</code> object, the
	 * driver can calculate the timestamp taking into account a custom timezone.
	 * If no <code>Calendar</code> object is specified, the driver uses the
	 * default timezone, which is that of the virtual machine running the
	 * application.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @param cal
	 *            the <code>Calendar</code> object the driver will use to
	 *            construct the timestamp
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.2
	 */
	void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal) throws SQLException;

	/**
	 * Sets the designated parameter to SQL <code>NULL</code>. This version of
	 * the method <code>setNull</code> should be used for user-defined types and
	 * REF type parameters. Examples of user-defined types include: STRUCT,
	 * DISTINCT, JAVA_OBJECT, and named array types.
	 *
	 * <P>
	 * <B>Note:</B> To be portable, applications must give the SQL type code and
	 * the fully-qualified SQL type name when specifying a NULL user-defined or
	 * REF parameter. In the case of a user-defined type the name is the type
	 * name of the parameter itself. For a REF parameter, the name is the type
	 * name of the referenced type. If a JDBC driver does not need the type code
	 * or type name information, it may ignore it.
	 *
	 * Although it is intended for user-defined and Ref parameters, this method
	 * may be used to set a null parameter of any JDBC type. If the parameter
	 * does not have a user-defined or REF type, the given typeName is ignored.
	 *
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param sqlType
	 *            a value from <code>java.sql.Types</code>
	 * @param typeName
	 *            the fully-qualified name of an SQL user-defined type; ignored
	 *            if the parameter is not a user-defined type or REF
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @exception SQLFeatureNotSupportedException
	 *                if <code>sqlType</code> is a <code>ARRAY</code>,
	 *                <code>BLOB</code>, <code>CLOB</code>,
	 *                <code>DATALINK</code>, <code>JAVA_OBJECT</code>,
	 *                <code>NCHAR</code>, <code>NCLOB</code>,
	 *                <code>NVARCHAR</code>, <code>LONGNVARCHAR</code>,
	 *                <code>REF</code>, <code>ROWID</code>, <code>SQLXML</code>
	 *                or <code>STRUCT</code> data type and the JDBC driver does
	 *                not support this data type or if the JDBC driver does not
	 *                support this method
	 * @since 1.2
	 */
	void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException;

	// ------------------------- JDBC 3.0 -----------------------------------
	/**
	 * Sets the designated parameter to the given <code>java.net.URL</code>
	 * value. The driver converts this to an SQL <code>DATALINK</code> value
	 * when it sends it to the database.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the <code>java.net.URL</code> object to be set
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.4
	 */
	void setURL(int parameterIndex, java.net.URL x) throws SQLException;

	/**
	 * Retrieves the number, types and properties of this
	 * <code>PreparedStatement</code> object's parameters.
	 *
	 * @return a <code>ParameterMetaData</code> object that contains information
	 *         about the number, types and properties for each parameter marker
	 *         of this <code>PreparedStatement</code> object
	 * @exception SQLException
	 *                if a database access error occurs or this method is called
	 *                on a closed <code>PreparedStatement</code>
	 * @see ParameterMetaData
	 * @since 1.4
	 */
	ParameterMetaData getParameterMetaData() throws SQLException;

	// ------------------------- JDBC 4.0 -----------------------------------
	/**
	 * Sets the designated parameter to the given <code>java.sql.RowId</code>
	 * object. The driver converts this to a SQL <code>ROWID</code> value when
	 * it sends it to the database
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the parameter value
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs or
	 *             this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setRowId(int parameterIndex, RowId x) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>String</code> object.
	 * The driver converts this to a SQL <code>NCHAR</code> or
	 * <code>NVARCHAR</code> or <code>LONGNVARCHAR</code> value (depending on
	 * the argument's size relative to the driver's limits on
	 * <code>NVARCHAR</code> values) when it sends it to the database.
	 *
	 * @param parameterIndex
	 *            of the first parameter is 1, the second is 2, ...
	 * @param value
	 *            the parameter value
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the driver does not support national
	 *             character sets; if the driver can detect that a data
	 *             conversion error could occur; if a database access error
	 *             occurs; or this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setNString(int parameterIndex, String value) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The
	 * <code>Reader</code> reads the data till end-of-file is reached. The
	 * driver does the necessary conversion from Java character format to the
	 * national character set in the database.
	 *
	 * @param parameterIndex
	 *            of the first parameter is 1, the second is 2, ...
	 * @param value
	 *            the parameter value
	 * @param length
	 *            the number of characters in the parameter data.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the driver does not support national
	 *             character sets; if the driver can detect that a data
	 *             conversion error could occur; if a database access error
	 *             occurs; or this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>java.sql.NClob</code> object.
	 * The driver converts this to a SQL <code>NCLOB</code> value when it sends
	 * it to the database.
	 *
	 * @param parameterIndex
	 *            of the first parameter is 1, the second is 2, ...
	 * @param value
	 *            the parameter value
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the driver does not support national
	 *             character sets; if the driver can detect that a data
	 *             conversion error could occur; if a database access error
	 *             occurs; or this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setNClob(int parameterIndex, NClob value) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The reader
	 * must contain the number of characters specified by length otherwise a
	 * <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setCharacterStream (int, Reader, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>CLOB</code>. When the <code>setCharacterStream</code> method
	 * is used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a <code>LONGVARCHAR</code>
	 * or a <code>CLOB</code>
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @param length
	 *            the number of characters in the parameter data.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs; this
	 *             method is called on a closed <code>PreparedStatement</code>
	 *             or if the length specified is less than zero.
	 *
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setClob(int parameterIndex, Reader reader, long length) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>InputStream</code> object. The
	 * inputstream must contain the number of characters specified by length
	 * otherwise a <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setBinaryStream (int, InputStream, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>BLOB</code>. When the <code>setBinaryStream</code> method is
	 * used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a
	 * <code>LONGVARBINARY</code> or a <code>BLOB</code>
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param inputStream
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @param length
	 *            the number of bytes in the parameter data.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs; this
	 *             method is called on a closed <code>PreparedStatement</code>;
	 *             if the length specified is less than zero or if the number of
	 *             bytes in the inputstream does not match the specified length.
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The reader
	 * must contain the number of characters specified by length otherwise a
	 * <code>SQLException</code> will be generated when the
	 * <code>PreparedStatement</code> is executed. This method differs from the
	 * <code>setCharacterStream (int, Reader, int)</code> method because it
	 * informs the driver that the parameter value should be sent to the server
	 * as a <code>NCLOB</code>. When the <code>setCharacterStream</code> method
	 * is used, the driver may have to do extra work to determine whether the
	 * parameter data should be sent to the server as a
	 * <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @param length
	 *            the number of characters in the parameter data.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the length specified is less than
	 *             zero; if the driver does not support national character sets;
	 *             if the driver can detect that a data conversion error could
	 *             occur; if a database access error occurs or this method is
	 *             called on a closed <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setNClob(int parameterIndex, Reader reader, long length) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>java.sql.SQLXML</code>
	 * object. The driver converts this to an SQL <code>XML</code> value when it
	 * sends it to the database.
	 * <p>
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param xmlObject
	 *            a <code>SQLXML</code> object that maps an SQL <code>XML</code>
	 *            value
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs; this
	 *             method is called on a closed <code>PreparedStatement</code>
	 *             or the <code>java.xml.transform.Result</code>,
	 *             <code>Writer</code> or <code>OutputStream</code> has not been
	 *             closed for the <code>SQLXML</code> object
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException;

	/**
	 * <p>
	 * Sets the value of the designated parameter with the given object.
	 *
	 * If the second argument is an <code>InputStream</code> then the stream
	 * must contain the number of bytes specified by scaleOrLength. If the
	 * second argument is a <code>Reader</code> then the reader must contain the
	 * number of characters specified by scaleOrLength. If these conditions are
	 * not true the driver will generate a <code>SQLException</code> when the
	 * prepared statement is executed.
	 *
	 * <p>
	 * The given Java object will be converted to the given targetSqlType before
	 * being sent to the database.
	 *
	 * If the object has a custom mapping (is of a class implementing the
	 * interface <code>SQLData</code>), the JDBC driver should call the method
	 * <code>SQLData.writeSQL</code> to write it to the SQL data stream. If, on
	 * the other hand, the object is of a class implementing <code>Ref</code>,
	 * <code>Blob</code>, <code>Clob</code>, <code>NClob</code>,
	 * <code>Struct</code>, <code>java.net.URL</code>, or <code>Array</code>,
	 * the driver should pass it to the database as a value of the corresponding
	 * SQL type.
	 *
	 * <p>
	 * Note that this method may be used to pass database-specific abstract data
	 * types.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type (as defined in java.sql.Types) to be sent to the
	 *            database. The scale argument may further qualify this type.
	 * @param scaleOrLength
	 *            for <code>java.sql.Types.DECIMAL</code> or
	 *            <code>java.sql.Types.NUMERIC types</code>, this is the number
	 *            of digits after the decimal point. For Java Object types
	 *            <code>InputStream</code> and <code>Reader</code>, this is the
	 *            length of the data in the stream or reader. For all other
	 *            types, this value will be ignored.
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs; this method is called on a closed
	 *                <code>PreparedStatement</code> or if the Java Object
	 *                specified by x is an InputStream or Reader object and the
	 *                value of the scale parameter is less than zero
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support the specified
	 *                targetSqlType
	 * @see Types
	 *
	 */
	void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException;

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large ASCII value is input to
	 * a <code>LONGVARCHAR</code> parameter, it may be more practical to send it
	 * via a <code>java.io.InputStream</code>. Data will be read from the stream
	 * as needed until end-of-file is reached. The JDBC driver will do any
	 * necessary conversion from ASCII to the database char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the Java input stream that contains the ASCII parameter value
	 * @param length
	 *            the number of bytes in the stream
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.6
	 */
	void setAsciiStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException;

	/**
	 * Sets the designated parameter to the given input stream, which will have
	 * the specified number of bytes. When a very large binary value is input to
	 * a <code>LONGVARBINARY</code> parameter, it may be more practical to send
	 * it via a <code>java.io.InputStream</code> object. The data will be read
	 * from the stream as needed until end-of-file is reached.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the binary parameter
	 *            value
	 * @param length
	 *            the number of bytes in the stream
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.6
	 */
	void setBinaryStream(int parameterIndex, java.io.InputStream x, long length) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>Reader</code> object,
	 * which is the given number of characters long. When a very large UNICODE
	 * value is input to a <code>LONGVARCHAR</code> parameter, it may be more
	 * practical to send it via a <code>java.io.Reader</code> object. The data
	 * will be read from the stream as needed until end-of-file is reached. The
	 * JDBC driver will do any necessary conversion from UNICODE to the database
	 * char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            the <code>java.io.Reader</code> object that contains the
	 *            Unicode data
	 * @param length
	 *            the number of characters in the stream
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @since 1.6
	 */
	void setCharacterStream(int parameterIndex, java.io.Reader reader, long length) throws SQLException;

	// -----
	/**
	 * Sets the designated parameter to the given input stream. When a very
	 * large ASCII value is input to a <code>LONGVARCHAR</code> parameter, it
	 * may be more practical to send it via a <code>java.io.InputStream</code>.
	 * Data will be read from the stream as needed until end-of-file is reached.
	 * The JDBC driver will do any necessary conversion from ASCII to the
	 * database char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setAsciiStream</code>
	 * which takes a length parameter.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the Java input stream that contains the ASCII parameter value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setAsciiStream(int parameterIndex, java.io.InputStream x) throws SQLException;

	/**
	 * Sets the designated parameter to the given input stream. When a very
	 * large binary value is input to a <code>LONGVARBINARY</code> parameter, it
	 * may be more practical to send it via a <code>java.io.InputStream</code>
	 * object. The data will be read from the stream as needed until end-of-file
	 * is reached.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setBinaryStream</code>
	 * which takes a length parameter.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the java input stream which contains the binary parameter
	 *            value
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setBinaryStream(int parameterIndex, java.io.InputStream x) throws SQLException;

	/**
	 * Sets the designated parameter to the given <code>Reader</code> object.
	 * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
	 * parameter, it may be more practical to send it via a
	 * <code>java.io.Reader</code> object. The data will be read from the stream
	 * as needed until end-of-file is reached. The JDBC driver will do any
	 * necessary conversion from UNICODE to the database char format.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>setCharacterStream</code> which takes a length parameter.
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            the <code>java.io.Reader</code> object that contains the
	 *            Unicode data
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setCharacterStream(int parameterIndex, java.io.Reader reader) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. The
	 * <code>Reader</code> reads the data till end-of-file is reached. The
	 * driver does the necessary conversion from Java character format to the
	 * national character set in the database.
	 *
	 * <P>
	 * <B>Note:</B> This stream object can either be a standard Java stream
	 * object or your own subclass that implements the standard interface.
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of
	 * <code>setNCharacterStream</code> which takes a length parameter.
	 *
	 * @param parameterIndex
	 *            of the first parameter is 1, the second is 2, ...
	 * @param value
	 *            the parameter value
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the driver does not support national
	 *             character sets; if the driver can detect that a data
	 *             conversion error could occur; if a database access error
	 *             occurs; or this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setNCharacterStream(int parameterIndex, Reader value) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. This
	 * method differs from the <code>setCharacterStream (int, Reader)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>CLOB</code>. When the
	 * <code>setCharacterStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGVARCHAR</code> or a <code>CLOB</code>
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setClob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs; this
	 *             method is called on a closed <code>PreparedStatement</code>or
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement
	 *
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 * @since 1.6
	 */
	void setClob(int parameterIndex, Reader reader) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>InputStream</code> object. This
	 * method differs from the <code>setBinaryStream (int, InputStream)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>BLOB</code>. When the
	 * <code>setBinaryStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGVARBINARY</code> or a <code>BLOB</code>
	 *
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setBlob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param inputStream
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if a database access error occurs; this
	 *             method is called on a closed <code>PreparedStatement</code>
	 *             or if parameterIndex does not correspond to a parameter
	 *             marker in the SQL statement,
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setBlob(int parameterIndex, InputStream inputStream) throws SQLException;

	/**
	 * Sets the designated parameter to a <code>Reader</code> object. This
	 * method differs from the <code>setCharacterStream (int, Reader)</code>
	 * method because it informs the driver that the parameter value should be
	 * sent to the server as a <code>NCLOB</code>. When the
	 * <code>setCharacterStream</code> method is used, the driver may have to do
	 * extra work to determine whether the parameter data should be sent to the
	 * server as a <code>LONGNVARCHAR</code> or a <code>NCLOB</code>
	 * <P>
	 * <B>Note:</B> Consult your JDBC driver documentation to determine if it
	 * might be more efficient to use a version of <code>setNClob</code> which
	 * takes a length parameter.
	 *
	 * @param parameterIndex
	 *            index of the first parameter is 1, the second is 2, ...
	 * @param reader
	 *            An object that contains the data to set the parameter value
	 *            to.
	 * @throws SQLException
	 *             if parameterIndex does not correspond to a parameter marker
	 *             in the SQL statement; if the driver does not support national
	 *             character sets; if the driver can detect that a data
	 *             conversion error could occur; if a database access error
	 *             occurs or this method is called on a closed
	 *             <code>PreparedStatement</code>
	 * @throws SQLFeatureNotSupportedException
	 *             if the JDBC driver does not support this method
	 *
	 * @since 1.6
	 */
	void setNClob(int parameterIndex, Reader reader) throws SQLException;

	// ------------------------- JDBC 4.2 -----------------------------------
	/**
	 * <p>
	 * Sets the value of the designated parameter with the given object.
	 *
	 * If the second argument is an {@code InputStream} then the stream must
	 * contain the number of bytes specified by scaleOrLength. If the second
	 * argument is a {@code Reader} then the reader must contain the number of
	 * characters specified by scaleOrLength. If these conditions are not true
	 * the driver will generate a {@code SQLException} when the prepared
	 * statement is executed.
	 *
	 * <p>
	 * The given Java object will be converted to the given targetSqlType before
	 * being sent to the database.
	 *
	 * If the object has a custom mapping (is of a class implementing the
	 * interface {@code SQLData}), the JDBC driver should call the method
	 * {@code SQLData.writeSQL} to write it to the SQL data stream. If, on the
	 * other hand, the object is of a class implementing {@code Ref},
	 * {@code Blob}, {@code Clob}, {@code NClob}, {@code Struct},
	 * {@code java.net.URL}, or {@code Array}, the driver should pass it to the
	 * database as a value of the corresponding SQL type.
	 *
	 * <p>
	 * Note that this method may be used to pass database-specific abstract data
	 * types.
	 * <P>
	 * The default implementation will throw
	 * {@code SQLFeatureNotSupportedException}
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type to be sent to the database. The scale argument
	 *            may further qualify this type.
	 * @param scaleOrLength
	 *            for {@code java.sql.JDBCType.DECIMAL} or
	 *            {@code java.sql.JDBCType.NUMERIC types}, this is the number of
	 *            digits after the decimal point. For Java Object types
	 *            {@code InputStream} and {@code Reader}, this is the length of
	 *            the data in the stream or reader. For all other types, this
	 *            value will be ignored.
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                {@code PreparedStatement} or if the Java Object specified
	 *                by x is an InputStream or Reader object and the value of
	 *                the scale parameter is less than zero
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support the specified
	 *                targetSqlType
	 * @see JDBCType
	 * @see SQLType
	 * @since 1.8
	 */
	default void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		throw new SQLFeatureNotSupportedException("setObject not implemented");
	}

	/**
	 * Sets the value of the designated parameter with the given object.
	 *
	 * This method is similar to
	 * {@link #setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)},
	 * except that it assumes a scale of zero.
	 * <P>
	 * The default implementation will throw
	 * {@code SQLFeatureNotSupportedException}
	 *
	 * @param parameterIndex
	 *            the first parameter is 1, the second is 2, ...
	 * @param x
	 *            the object containing the input parameter value
	 * @param targetSqlType
	 *            the SQL type to be sent to the database
	 * @exception SQLException
	 *                if parameterIndex does not correspond to a parameter
	 *                marker in the SQL statement; if a database access error
	 *                occurs or this method is called on a closed
	 *                {@code PreparedStatement}
	 * @exception SQLFeatureNotSupportedException
	 *                if the JDBC driver does not support the specified
	 *                targetSqlType
	 * @see JDBCType
	 * @see SQLType
	 * @since 1.8
	 */
	default void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
		throw new SQLFeatureNotSupportedException("setObject not implemented");
	}

	/**
	 * Executes the SQL statement in this <code>PreparedStatement</code> object,
	 * which must be an SQL Data Manipulation Language (DML) statement, such as
	 * <code>INSERT</code>, <code>UPDATE</code> or <code>DELETE</code>; or an
	 * SQL statement that returns nothing, such as a DDL statement.
	 * <p>
	 * This method should be used when the returned row count may exceed
	 * {@link Integer#MAX_VALUE}.
	 * <p>
	 * The default implementation will throw
	 * {@code UnsupportedOperationException}
	 *
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * @exception SQLException
	 *                if a database access error occurs; this method is called
	 *                on a closed <code>PreparedStatement</code> or the SQL
	 *                statement returns a <code>ResultSet</code> object
	 * @throws SQLTimeoutException
	 *             when the driver has determined that the timeout value that
	 *             was specified by the {@code setQueryTimeout} method has been
	 *             exceeded and has at least attempted to cancel the currently
	 *             running {@code Statement}
	 * @since 1.8
	 */
	default long executeLargeUpdate() throws SQLException {
		throw new UnsupportedOperationException("executeLargeUpdate not implemented");
	}
}
