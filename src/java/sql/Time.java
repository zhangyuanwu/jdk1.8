package java.sql;

import java.time.Instant;
import java.time.LocalTime;

public class Time extends java.util.Date {
	@Deprecated
	public Time(int hour, int minute, int second) {
		super(70, 0, 1, hour, minute, second);
	}

	public Time(long time) {
		super(time);
	}

	public void setTime(long time) {
		super.setTime(time);
	}

	public static Time valueOf(String s) {
		int hour;
		int minute;
		int second;
		int firstColon;
		int secondColon;
		if (s == null) {
			throw new java.lang.IllegalArgumentException();
		}
		firstColon = s.indexOf(':');
		secondColon = s.indexOf(':', firstColon + 1);
		if ((firstColon > 0) & (secondColon > 0) & (secondColon < (s.length() - 1))) {
			hour = Integer.parseInt(s.substring(0, firstColon));
			minute = Integer.parseInt(s.substring(firstColon + 1, secondColon));
			second = Integer.parseInt(s.substring(secondColon + 1));
		} else {
			throw new java.lang.IllegalArgumentException();
		}
		return new Time(hour, minute, second);
	}

	@SuppressWarnings("deprecation")
	public String toString() {
		int hour = super.getHours();
		int minute = super.getMinutes();
		int second = super.getSeconds();
		String hourString;
		String minuteString;
		String secondString;
		if (hour < 10) {
			hourString = "0" + hour;
		} else {
			hourString = Integer.toString(hour);
		}
		if (minute < 10) {
			minuteString = "0" + minute;
		} else {
			minuteString = Integer.toString(minute);
		}
		if (second < 10) {
			secondString = "0" + second;
		} else {
			secondString = Integer.toString(second);
		}
		return (hourString + ":" + minuteString + ":" + secondString);
	}

	// Override all the date operations inherited from java.util.Date;
	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a year component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 * @see #setYear
	 */
	@Deprecated
	public int getYear() {
		throw new java.lang.IllegalArgumentException();
	}

	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a month component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 * @see #setMonth
	 */
	@Deprecated
	public int getMonth() {
		throw new java.lang.IllegalArgumentException();
	}

	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a day component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 */
	@Deprecated
	public int getDay() {
		throw new java.lang.IllegalArgumentException();
	}

	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a date component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 * @see #setDate
	 */
	@Deprecated
	public int getDate() {
		throw new java.lang.IllegalArgumentException();
	}

	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a year component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 * @see #getYear
	 */
	@Deprecated
	public void setYear(int i) {
		throw new java.lang.IllegalArgumentException();
	}

	/**
	 * This method is deprecated and should not be used because SQL
	 * <code>TIME</code> values do not have a month component.
	 *
	 * @deprecated
	 * @exception java.lang.IllegalArgumentException
	 *                if this method is invoked
	 * @see #getMonth
	 */
	@Deprecated
	public void setMonth(int i) {
		throw new java.lang.IllegalArgumentException();
	}

	@Deprecated
	public void setDate(int i) {
		throw new java.lang.IllegalArgumentException();
	}

	static final long serialVersionUID = 8397324403548013681L;

	/**
	 * Obtains an instance of {@code Time} from a {@link LocalTime} object with
	 * the same hour, minute and second time value as the given
	 * {@code LocalTime}.
	 *
	 * @param time
	 *            a {@code LocalTime} to convert
	 * @return a {@code Time} object
	 * @exception NullPointerException
	 *                if {@code time} is null
	 * @since 1.8
	 */
	@SuppressWarnings("deprecation")
	public static Time valueOf(LocalTime time) {
		return new Time(time.getHour(), time.getMinute(), time.getSecond());
	}

	/**
	 * Converts this {@code Time} object to a {@code LocalTime}.
	 * <p>
	 * The conversion creates a {@code LocalTime} that represents the same hour,
	 * minute, and second time value as this {@code Time}.
	 *
	 * @return a {@code LocalTime} object representing the same time value
	 * @since 1.8
	 */
	@SuppressWarnings("deprecation")
	public LocalTime toLocalTime() {
		return LocalTime.of(getHours(), getMinutes(), getSeconds());
	}

	@Override
	public Instant toInstant() {
		throw new java.lang.UnsupportedOperationException();
	}
}
