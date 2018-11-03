package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.BuddhistCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

public abstract class Calendar implements Serializable, Cloneable, Comparable<Calendar> {
	public final static int ERA = 0;
	public final static int YEAR = 1;
	public final static int MONTH = 2;
	public final static int WEEK_OF_YEAR = 3;
	public final static int WEEK_OF_MONTH = 4;
	public final static int DATE = 5;
	public final static int DAY_OF_MONTH = 5;
	public final static int DAY_OF_YEAR = 6;
	public final static int DAY_OF_WEEK = 7;
	public final static int DAY_OF_WEEK_IN_MONTH = 8;
	public final static int AM_PM = 9;
	public final static int HOUR = 10;
	public final static int HOUR_OF_DAY = 11;
	public final static int MINUTE = 12;
	public final static int SECOND = 13;
	public final static int MILLISECOND = 14;
	public final static int ZONE_OFFSET = 15;
	public final static int DST_OFFSET = 16;
	public final static int FIELD_COUNT = 17;
	public final static int SUNDAY = 1;
	public final static int MONDAY = 2;
	public final static int TUESDAY = 3;
	public final static int WEDNESDAY = 4;
	public final static int THURSDAY = 5;
	public final static int FRIDAY = 6;
	public final static int SATURDAY = 7;
	public final static int JANUARY = 0;
	public final static int FEBRUARY = 1;
	public final static int MARCH = 2;
	public final static int APRIL = 3;
	public final static int MAY = 4;
	public final static int JUNE = 5;
	public final static int JULY = 6;
	public final static int AUGUST = 7;
	public final static int SEPTEMBER = 8;
	public final static int OCTOBER = 9;
	public final static int NOVEMBER = 10;
	public final static int DECEMBER = 11;
	public final static int UNDECIMBER = 12;
	public final static int AM = 0;
	public final static int PM = 1;
	public static final int ALL_STYLES = 0;
	static final int STANDALONE_MASK = 0x8000;
	public static final int SHORT = 1;
	public static final int LONG = 2;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,指示用于格式的窄名称. 窄名称通常是单个字符串,例如星期一的"M".
	 *
	 * @see #NARROW_STANDALONE
	 * @see #SHORT_FORMAT
	 * @see #LONG_FORMAT
	 * @since 1.8
	 */
	public static final int NARROW_FORMAT = 4;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,独立地表示一个窄名称. 窄名称通常是单个字符串,例如星期一的"M".
	 *
	 * @see #NARROW_FORMAT
	 * @see #SHORT_STANDALONE
	 * @see #LONG_STANDALONE
	 * @since 1.8
	 */
	public static final int NARROW_STANDALONE = NARROW_FORMAT | STANDALONE_MASK;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,指示用于格式的短名称.
	 *
	 * @see #SHORT_STANDALONE
	 * @see #LONG_FORMAT
	 * @see #LONG_STANDALONE
	 * @since 1.8
	 */
	public static final int SHORT_FORMAT = 1;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,指示用于格式的长名称.
	 *
	 * @see #LONG_STANDALONE
	 * @see #SHORT_FORMAT
	 * @see #SHORT_STANDALONE
	 * @since 1.8
	 */
	public static final int LONG_FORMAT = 2;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,表示独立使用的短名称,例如月份缩写作为日历标题.
	 *
	 * @see #SHORT_FORMAT
	 * @see #LONG_FORMAT
	 * @see #LONG_STANDALONE
	 * @since 1.8
	 */
	public static final int SHORT_STANDALONE = SHORT | STANDALONE_MASK;
	/**
	 * {@link #getDisplayName(int,int,Locale)
	 * getDisplayName}和{@link #getDisplayNames(int,int,Locale)
	 * getDisplayNames}的样式说明符,表示独立使用的长名称,例如月份名称作为日历标题.
	 *
	 * @see #LONG_FORMAT
	 * @see #SHORT_FORMAT
	 * @see #SHORT_STANDALONE
	 * @since 1.8
	 */
	public static final int LONG_STANDALONE = LONG | STANDALONE_MASK;
	@SuppressWarnings("ProtectedField")
	protected int fields[];
	@SuppressWarnings("ProtectedField")
	protected boolean isSet[];
	transient private int stamp[];
	@SuppressWarnings("ProtectedField")
	protected long time;
	@SuppressWarnings("ProtectedField")
	protected boolean isTimeSet;
	@SuppressWarnings("ProtectedField")
	protected boolean areFieldsSet;
	transient boolean areAllFieldsSet;
	private boolean lenient = true;
	private TimeZone zone;
	transient private boolean sharedZone = false;
	private int firstDayOfWeek;
	private int minimalDaysInFirstWeek;
	private static final ConcurrentMap<Locale, int[]> cachedLocaleData = new ConcurrentHashMap<>(3);
	private static final int UNSET = 0;
	private static final int COMPUTED = 1;
	private static final int MINIMUM_USER_STAMP = 2;
	static final int ALL_FIELDS = (1 << FIELD_COUNT) - 1;
	private int nextStamp = MINIMUM_USER_STAMP;
	static final int currentSerialVersion = 1;
	private int serialVersionOnStream = currentSerialVersion;
	static final long serialVersionUID = -1807547505821590642L;
	@SuppressWarnings("PointlessBitwiseExpression")
	final static int ERA_MASK = (1 << ERA);
	final static int YEAR_MASK = (1 << YEAR);
	final static int MONTH_MASK = (1 << MONTH);
	final static int WEEK_OF_YEAR_MASK = (1 << WEEK_OF_YEAR);
	final static int WEEK_OF_MONTH_MASK = (1 << WEEK_OF_MONTH);
	final static int DAY_OF_MONTH_MASK = (1 << DAY_OF_MONTH);
	final static int DATE_MASK = DAY_OF_MONTH_MASK;
	final static int DAY_OF_YEAR_MASK = (1 << DAY_OF_YEAR);
	final static int DAY_OF_WEEK_MASK = (1 << DAY_OF_WEEK);
	final static int DAY_OF_WEEK_IN_MONTH_MASK = (1 << DAY_OF_WEEK_IN_MONTH);
	final static int AM_PM_MASK = (1 << AM_PM);
	final static int HOUR_MASK = (1 << HOUR);
	final static int HOUR_OF_DAY_MASK = (1 << HOUR_OF_DAY);
	final static int MINUTE_MASK = (1 << MINUTE);
	final static int SECOND_MASK = (1 << SECOND);
	final static int MILLISECOND_MASK = (1 << MILLISECOND);
	final static int ZONE_OFFSET_MASK = (1 << ZONE_OFFSET);
	final static int DST_OFFSET_MASK = (1 << DST_OFFSET);

	/**
	 * {@code Calendar.Builder}用于从各种日期时间参数创建{@code Calendar}.
	 * <p>
	 * 有两种方法可以将{@code Calendar}设置为日期时间值.
	 * 一种是将instant参数设置为距<a href="Calendar.html#Epoch">Epoch</a>毫秒的偏移量.
	 * 另一种是将各个字段参数(例如{@link Calendar #YEAR YEAR})设置为所需的值. 这两种方式不能混为一谈.
	 * 尝试设置即时字段和单个字段将导致抛出{@link IllegalStateException}. 但是,允许覆盖即时或字段参数的先前值.
	 * <p>
	 * 如果没有给出足够的字段参数来确定日期和/或时间,则在构建{@code日历}时会使用日历特定的默认值.
	 * 例如,如果没有为公历提供{@link Calendar #YEAR YEAR}值,则将使用1970.
	 * 如果字段参数之间存在任何冲突,则会应用<a href="Calendar.html#resolution">解决规则</a>.
	 * 因此,场设置的顺序很重要.
	 * <p>
	 * 除了日期时间参数,{@linkplain #setLocale(Locale)
	 * locale},{@linkplain #setTimeZone(TimeZone) time
	 * zone},{@linkplain #setWeekDefinition(int,int) week
	 * definition}和{@linkplain #setLenient(boolean) leniency mode}参数可以设置.
	 * <p>
	 * <b>例子</b>
	 * <p>
	 * 以下是示例用法. 示例代码假定{@code Calendar}常量是静态导入的.
	 * <p>
	 * 以下代码生成{@code Calendar},日期为2012-12-31(Gregorian),因为星期一是<a href=
	 * "GregorianCalendar.html#iso8601_compatible_setting"> ISO
	 * 8601兼容周参数的一周的第一天</a>.
	 *
	 * <pre>
	 * Calendar cal = new Calendar.Builder().setCalendarType("iso8601").setWeekDate(2013, 1, MONDAY).build();
	 * </pre>
	 * <p>
	 * 以下代码生成日语{@code Calendar},日期为1989-01-08(Gregorian),假设默认的{@link Calendar#ERA
	 * ERA}是当天开始的<em> Heisei </em>.
	 *
	 * <pre>
	 * Calendar cal = new Calendar.Builder().setCalendarType("japanese").setFields(YEAR, 1, DAY_OF_YEAR, 1).build();
	 * </pre>
	 *
	 * @since 1.8
	 * @see Calendar#getInstance(TimeZone, Locale)
	 * @see Calendar#fields
	 */
	public static class Builder {
		private static final int NFIELDS = FIELD_COUNT + 1;
		private static final int WEEK_YEAR = FIELD_COUNT;
		private long instant;
		private int[] fields;
		private int nextStamp;
		private int maxFieldIndex;
		private String type;
		private TimeZone zone;
		private boolean lenient = true;
		private Locale locale;
		private int firstDayOfWeek, minimalDaysInFirstWeek;

		/**
		 * Constructs a {@code Calendar.Builder}.
		 */
		public Builder() {
		}

		/**
		 * Sets the instant parameter to the given {@code instant} value that is
		 * a millisecond offset from <a href="Calendar.html#Epoch">the
		 * Epoch</a>.
		 *
		 * @param instant
		 *            a millisecond offset from the Epoch
		 * @return this {@code Calendar.Builder}
		 * @throws IllegalStateException
		 *             if any of the field parameters have already been set
		 * @see Calendar#setTime(Date)
		 * @see Calendar#setTimeInMillis(long)
		 * @see Calendar#time
		 */
		public Builder setInstant(long instant) {
			if (fields != null) {
				throw new IllegalStateException();
			}
			this.instant = instant;
			nextStamp = COMPUTED;
			return this;
		}

		/**
		 * Sets the instant parameter to the {@code instant} value given by a
		 * {@link Date}. This method is equivalent to a call to
		 * {@link #setInstant(long) setInstant(instant.getTime())}.
		 *
		 * @param instant
		 *            a {@code Date} representing a millisecond offset from the
		 *            Epoch
		 * @return this {@code Calendar.Builder}
		 * @throws NullPointerException
		 *             if {@code instant} is {@code null}
		 * @throws IllegalStateException
		 *             if any of the field parameters have already been set
		 * @see Calendar#setTime(Date)
		 * @see Calendar#setTimeInMillis(long)
		 * @see Calendar#time
		 */
		public Builder setInstant(Date instant) {
			return setInstant(instant.getTime());
		}

		/**
		 * Sets the {@code field} parameter to the given {@code value}.
		 * {@code field} is an index to the {@link Calendar#fields}, such as
		 * {@link Calendar#DAY_OF_MONTH DAY_OF_MONTH}. Field value validation is
		 * not performed in this method. Any out of range values are either
		 * normalized in lenient mode or detected as an invalid value in
		 * non-lenient mode when building a {@code Calendar}.
		 *
		 * @param field
		 *            an index to the {@code Calendar} fields
		 * @param value
		 *            the field value
		 * @return this {@code Calendar.Builder}
		 * @throws IllegalArgumentException
		 *             if {@code field} is invalid
		 * @throws IllegalStateException
		 *             if the instant value has already been set, or if fields
		 *             have been set too many (approximately
		 *             {@link Integer#MAX_VALUE}) times.
		 * @see Calendar#set(int, int)
		 */
		public Builder set(int field, int value) {
			if ((field < 0) || (field >= FIELD_COUNT)) {
				throw new IllegalArgumentException("field is invalid");
			}
			if (isInstantSet()) {
				throw new IllegalStateException("instant has been set");
			}
			allocateFields();
			internalSet(field, value);
			return this;
		}

		/**
		 * Sets field parameters to their values given by
		 * {@code fieldValuePairs} that are pairs of a field and its value. For
		 * example,
		 *
		 * <pre>
		 * setFeilds(Calendar.YEAR, 2013, Calendar.MONTH, Calendar.DECEMBER, Calendar.DAY_OF_MONTH, 23);
		 * </pre>
		 *
		 * is equivalent to the sequence of the following {@link #set(int, int)
		 * set} calls:
		 *
		 * <pre>
		 * set(Calendar.YEAR, 2013).set(Calendar.MONTH, Calendar.DECEMBER).set(Calendar.DAY_OF_MONTH, 23);
		 * </pre>
		 *
		 * @param fieldValuePairs
		 *            field-value pairs
		 * @return this {@code Calendar.Builder}
		 * @throws NullPointerException
		 *             if {@code fieldValuePairs} is {@code null}
		 * @throws IllegalArgumentException
		 *             if any of fields are invalid, or if
		 *             {@code fieldValuePairs.length} is an odd number.
		 * @throws IllegalStateException
		 *             if the instant value has been set, or if fields have been
		 *             set too many (approximately {@link Integer#MAX_VALUE})
		 *             times.
		 */
		public Builder setFields(int... fieldValuePairs) {
			int len = fieldValuePairs.length;
			if ((len % 2) != 0) {
				throw new IllegalArgumentException();
			}
			if (isInstantSet()) {
				throw new IllegalStateException("instant has been set");
			}
			if ((nextStamp + (len / 2)) < 0) {
				throw new IllegalStateException("stamp counter overflow");
			}
			allocateFields();
			for (int i = 0; i < len;) {
				int field = fieldValuePairs[i++];
				if ((field < 0) || (field >= FIELD_COUNT)) {
					throw new IllegalArgumentException("field is invalid");
				}
				internalSet(field, fieldValuePairs[i++]);
			}
			return this;
		}

		/**
		 * Sets the date field parameters to the values given by {@code year},
		 * {@code month}, and {@code dayOfMonth}. This method is equivalent to a
		 * call to:
		 *
		 * <pre>
		 * setFields(Calendar.YEAR, year, Calendar.MONTH, month, Calendar.DAY_OF_MONTH, dayOfMonth);
		 * </pre>
		 *
		 * @param year
		 *            the {@link Calendar#YEAR YEAR} value
		 * @param month
		 *            the {@link Calendar#MONTH MONTH} value (the month
		 *            numbering is <em>0-based</em>).
		 * @param dayOfMonth
		 *            the {@link Calendar#DAY_OF_MONTH DAY_OF_MONTH} value
		 * @return this {@code Calendar.Builder}
		 */
		public Builder setDate(int year, int month, int dayOfMonth) {
			return setFields(YEAR, year, MONTH, month, DAY_OF_MONTH, dayOfMonth);
		}

		/**
		 * Sets the time of day field parameters to the values given by
		 * {@code hourOfDay}, {@code minute}, and {@code second}. This method is
		 * equivalent to a call to:
		 *
		 * <pre>
		 * setTimeOfDay(hourOfDay, minute, second, 0);
		 * </pre>
		 *
		 * @param hourOfDay
		 *            the {@link Calendar#HOUR_OF_DAY HOUR_OF_DAY} value
		 *            (24-hour clock)
		 * @param minute
		 *            the {@link Calendar#MINUTE MINUTE} value
		 * @param second
		 *            the {@link Calendar#SECOND SECOND} value
		 * @return this {@code Calendar.Builder}
		 */
		public Builder setTimeOfDay(int hourOfDay, int minute, int second) {
			return setTimeOfDay(hourOfDay, minute, second, 0);
		}

		/**
		 * Sets the time of day field parameters to the values given by
		 * {@code hourOfDay}, {@code minute}, {@code second}, and
		 * {@code millis}. This method is equivalent to a call to:
		 *
		 * <pre>
		 * setFields(Calendar.HOUR_OF_DAY, hourOfDay, Calendar.MINUTE, minute, Calendar.SECOND, second, Calendar.MILLISECOND, millis);
		 * </pre>
		 *
		 * @param hourOfDay
		 *            the {@link Calendar#HOUR_OF_DAY HOUR_OF_DAY} value
		 *            (24-hour clock)
		 * @param minute
		 *            the {@link Calendar#MINUTE MINUTE} value
		 * @param second
		 *            the {@link Calendar#SECOND SECOND} value
		 * @param millis
		 *            the {@link Calendar#MILLISECOND MILLISECOND} value
		 * @return this {@code Calendar.Builder}
		 */
		public Builder setTimeOfDay(int hourOfDay, int minute, int second, int millis) {
			return setFields(HOUR_OF_DAY, hourOfDay, MINUTE, minute, SECOND, second, MILLISECOND, millis);
		}

		/**
		 * Sets the week-based date parameters to the values with the given date
		 * specifiers - week year, week of year, and day of week.
		 *
		 * <p>
		 * If the specified calendar doesn't support week dates, the
		 * {@link #build() build} method will throw an
		 * {@link IllegalArgumentException}.
		 *
		 * @param weekYear
		 *            the week year
		 * @param weekOfYear
		 *            the week number based on {@code weekYear}
		 * @param dayOfWeek
		 *            the day of week value: one of the constants for the
		 *            {@link Calendar#DAY_OF_WEEK DAY_OF_WEEK} field:
		 *            {@link Calendar#SUNDAY SUNDAY}, ...,
		 *            {@link Calendar#SATURDAY SATURDAY}.
		 * @return this {@code Calendar.Builder}
		 * @see Calendar#setWeekDate(int, int, int)
		 * @see Calendar#isWeekDateSupported()
		 */
		public Builder setWeekDate(int weekYear, int weekOfYear, int dayOfWeek) {
			allocateFields();
			internalSet(WEEK_YEAR, weekYear);
			internalSet(WEEK_OF_YEAR, weekOfYear);
			internalSet(DAY_OF_WEEK, dayOfWeek);
			return this;
		}

		/**
		 * Sets the time zone parameter to the given {@code zone}. If no time
		 * zone parameter is given to this {@code Caledar.Builder}, the
		 * {@linkplain TimeZone#getDefault() default <code>TimeZone</code>} will
		 * be used in the {@link #build() build} method.
		 *
		 * @param zone
		 *            the {@link TimeZone}
		 * @return this {@code Calendar.Builder}
		 * @throws NullPointerException
		 *             if {@code zone} is {@code null}
		 * @see Calendar#setTimeZone(TimeZone)
		 */
		public Builder setTimeZone(TimeZone zone) {
			if (zone == null) {
				throw new NullPointerException();
			}
			this.zone = zone;
			return this;
		}

		/**
		 * Sets the lenient mode parameter to the value given by
		 * {@code lenient}. If no lenient parameter is given to this
		 * {@code Calendar.Builder}, lenient mode will be used in the
		 * {@link #build() build} method.
		 *
		 * @param lenient
		 *            {@code true} for lenient mode; {@code false} for
		 *            non-lenient mode
		 * @return this {@code Calendar.Builder}
		 * @see Calendar#setLenient(boolean)
		 */
		public Builder setLenient(boolean lenient) {
			this.lenient = lenient;
			return this;
		}

		/**
		 * Sets the calendar type parameter to the given {@code type}. The
		 * calendar type given by this method has precedence over any explicit
		 * or implicit calendar type given by the {@linkplain #setLocale(Locale)
		 * locale}.
		 *
		 * <p>
		 * In addition to the available calendar types returned by the
		 * {@link Calendar#getAvailableCalendarTypes()
		 * Calendar.getAvailableCalendarTypes} method, {@code "gregorian"} and
		 * {@code "iso8601"} as aliases of {@code "gregory"} can be used with
		 * this method.
		 *
		 * @param type
		 *            the calendar type
		 * @return this {@code Calendar.Builder}
		 * @throws NullPointerException
		 *             if {@code type} is {@code null}
		 * @throws IllegalArgumentException
		 *             if {@code type} is unknown
		 * @throws IllegalStateException
		 *             if another calendar type has already been set
		 * @see Calendar#getCalendarType()
		 * @see Calendar#getAvailableCalendarTypes()
		 */
		public Builder setCalendarType(String type) {
			if (type.equals("gregorian")) {
				type = "gregory";
			}
			if (!Calendar.getAvailableCalendarTypes().contains(type) && !type.equals("iso8601")) {
				throw new IllegalArgumentException("unknown calendar type: " + type);
			}
			if (this.type == null) {
				this.type = type;
			} else {
				if (!this.type.equals(type)) {
					throw new IllegalStateException("calendar type override");
				}
			}
			return this;
		}

		/**
		 * Sets the locale parameter to the given {@code locale}. If no locale
		 * is given to this {@code Calendar.Builder}, the
		 * {@linkplain Locale#getDefault(Locale.Category) default
		 * <code>Locale</code>} for {@link Locale.Category#FORMAT} will be used.
		 *
		 * <p>
		 * If no calendar type is explicitly given by a call to the
		 * {@link #setCalendarType(String) setCalendarType} method, the
		 * {@code Locale} value is used to determine what type of
		 * {@code Calendar} to be built.
		 *
		 * <p>
		 * If no week definition parameters are explicitly given by a call to
		 * the {@link #setWeekDefinition(int,int) setWeekDefinition} method, the
		 * {@code Locale}'s default values are used.
		 *
		 * @param locale
		 *            the {@link Locale}
		 * @throws NullPointerException
		 *             if {@code locale} is {@code null}
		 * @return this {@code Calendar.Builder}
		 * @see Calendar#getInstance(Locale)
		 */
		public Builder setLocale(Locale locale) {
			if (locale == null) {
				throw new NullPointerException();
			}
			this.locale = locale;
			return this;
		}

		/**
		 * Sets the week definition parameters to the values given by
		 * {@code firstDayOfWeek} and {@code minimalDaysInFirstWeek} that are
		 * used to determine the <a href="Calendar.html#First_Week">first
		 * week</a> of a year. The parameters given by this method have
		 * precedence over the default values given by the
		 * {@linkplain #setLocale(Locale) locale}.
		 *
		 * @param firstDayOfWeek
		 *            the first day of a week; one of {@link Calendar#SUNDAY} to
		 *            {@link Calendar#SATURDAY}
		 * @param minimalDaysInFirstWeek
		 *            the minimal number of days in the first week (1..7)
		 * @return this {@code Calendar.Builder}
		 * @throws IllegalArgumentException
		 *             if {@code firstDayOfWeek} or
		 *             {@code minimalDaysInFirstWeek} is invalid
		 * @see Calendar#getFirstDayOfWeek()
		 * @see Calendar#getMinimalDaysInFirstWeek()
		 */
		public Builder setWeekDefinition(int firstDayOfWeek, int minimalDaysInFirstWeek) {
			if (!isValidWeekParameter(firstDayOfWeek) || !isValidWeekParameter(minimalDaysInFirstWeek)) {
				throw new IllegalArgumentException();
			}
			this.firstDayOfWeek = firstDayOfWeek;
			this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
			return this;
		}

		/**
		 * Returns a {@code Calendar} built from the parameters set by the
		 * setter methods. The calendar type given by the
		 * {@link #setCalendarType(String) setCalendarType} method or the
		 * {@linkplain #setLocale(Locale) locale} is used to determine what
		 * {@code Calendar} to be created. If no explicit calendar type is
		 * given, the locale's default calendar is created.
		 *
		 * <p>
		 * If the calendar type is {@code "iso8601"}, the
		 * {@linkplain GregorianCalendar#setGregorianChange(Date) Gregorian
		 * change date} of a {@link GregorianCalendar} is set to
		 * {@code Date(Long.MIN_VALUE)} to be the <em>proleptic</em> Gregorian
		 * calendar. Its week definition parameters are also set to be <a href=
		 * "GregorianCalendar.html#iso8601_compatible_setting">compatible with
		 * the ISO 8601 standard</a>. Note that the
		 * {@link GregorianCalendar#getCalendarType() getCalendarType} method of
		 * a {@code GregorianCalendar} created with {@code "iso8601"} returns
		 * {@code "gregory"}.
		 *
		 * <p>
		 * The default values are used for locale and time zone if these
		 * parameters haven't been given explicitly.
		 *
		 * <p>
		 * Any out of range field values are either normalized in lenient mode
		 * or detected as an invalid value in non-lenient mode.
		 *
		 * @return a {@code Calendar} built with parameters of this {@code
		 *         Calendar.Builder}
		 * @throws IllegalArgumentException
		 *             if the calendar type is unknown, or if any invalid field
		 *             values are given in non-lenient mode, or if a week date
		 *             is given for the calendar type that doesn't support week
		 *             dates.
		 * @see Calendar#getInstance(TimeZone, Locale)
		 * @see Locale#getDefault(Locale.Category)
		 * @see TimeZone#getDefault()
		 */
		public Calendar build() {
			if (locale == null) {
				locale = Locale.getDefault();
			}
			if (zone == null) {
				zone = TimeZone.getDefault();
			}
			Calendar cal;
			if (type == null) {
				type = locale.getUnicodeLocaleType("ca");
			}
			if (type == null) {
				if ((locale.getCountry() == "TH") && (locale.getLanguage() == "th")) {
					type = "buddhist";
				} else {
					type = "gregory";
				}
			}
			switch (type) {
			case "gregory":
				cal = new GregorianCalendar(zone, locale, true);
				break;
			case "iso8601":
				GregorianCalendar gcal = new GregorianCalendar(zone, locale, true);
				gcal.setGregorianChange(new Date(Long.MIN_VALUE));
				setWeekDefinition(MONDAY, 4);
				cal = gcal;
				break;
			case "buddhist":
				cal = new BuddhistCalendar(zone, locale);
				cal.clear();
				break;
			case "japanese":
				cal = new JapaneseImperialCalendar(zone, locale, true);
				break;
			default:
				throw new IllegalArgumentException("unknown calendar type: " + type);
			}
			cal.setLenient(lenient);
			if (firstDayOfWeek != 0) {
				cal.setFirstDayOfWeek(firstDayOfWeek);
				cal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
			}
			if (isInstantSet()) {
				cal.setTimeInMillis(instant);
				cal.complete();
				return cal;
			}
			if (fields != null) {
				boolean weekDate = isSet(WEEK_YEAR) && (fields[WEEK_YEAR] > fields[YEAR]);
				if (weekDate && !cal.isWeekDateSupported()) {
					throw new IllegalArgumentException("week date is unsupported by " + type);
				}
				for (int stamp = MINIMUM_USER_STAMP; stamp < nextStamp; stamp++) {
					for (int index = 0; index <= maxFieldIndex; index++) {
						if (fields[index] == stamp) {
							cal.set(index, fields[NFIELDS + index]);
							break;
						}
					}
				}
				if (weekDate) {
					int weekOfYear = isSet(WEEK_OF_YEAR) ? fields[NFIELDS + WEEK_OF_YEAR] : 1;
					int dayOfWeek = isSet(DAY_OF_WEEK) ? fields[NFIELDS + DAY_OF_WEEK] : cal.getFirstDayOfWeek();
					cal.setWeekDate(fields[NFIELDS + WEEK_YEAR], weekOfYear, dayOfWeek);
				}
				cal.complete();
			}
			return cal;
		}

		private void allocateFields() {
			if (fields == null) {
				fields = new int[NFIELDS * 2];
				nextStamp = MINIMUM_USER_STAMP;
				maxFieldIndex = -1;
			}
		}

		private void internalSet(int field, int value) {
			fields[field] = nextStamp++;
			if (nextStamp < 0) {
				throw new IllegalStateException("stamp counter overflow");
			}
			fields[NFIELDS + field] = value;
			if ((field > maxFieldIndex) && (field < WEEK_YEAR)) {
				maxFieldIndex = field;
			}
		}

		private boolean isInstantSet() {
			return nextStamp == COMPUTED;
		}

		private boolean isSet(int index) {
			return (fields != null) && (fields[index] > UNSET);
		}

		private boolean isValidWeekParameter(int value) {
			return (value > 0) && (value <= 7);
		}
	}

	protected Calendar() {
		this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
		sharedZone = true;
	}

	protected Calendar(TimeZone zone, Locale aLocale) {
		fields = new int[FIELD_COUNT];
		isSet = new boolean[FIELD_COUNT];
		stamp = new int[FIELD_COUNT];
		this.zone = zone;
		setWeekCountData(aLocale);
	}

	public static Calendar getInstance() {
		return createCalendar(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
	}

	public static Calendar getInstance(TimeZone zone) {
		return createCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
	}

	public static Calendar getInstance(Locale aLocale) {
		return createCalendar(TimeZone.getDefault(), aLocale);
	}

	public static Calendar getInstance(TimeZone zone, Locale aLocale) {
		return createCalendar(zone, aLocale);
	}

	private static Calendar createCalendar(TimeZone zone, Locale aLocale) {
		CalendarProvider provider = LocaleProviderAdapter.getAdapter(CalendarProvider.class, aLocale).getCalendarProvider();
		if (provider != null) {
			try {
				return provider.getInstance(zone, aLocale);
			} catch (IllegalArgumentException iae) {
			}
		}
		Calendar cal = null;
		if (aLocale.hasExtensions()) {
			String caltype = aLocale.getUnicodeLocaleType("ca");
			if (caltype != null) {
				switch (caltype) {
				case "buddhist":
					cal = new BuddhistCalendar(zone, aLocale);
					break;
				case "japanese":
					cal = new JapaneseImperialCalendar(zone, aLocale);
					break;
				case "gregory":
					cal = new GregorianCalendar(zone, aLocale);
					break;
				}
			}
		}
		if (cal == null) {
			if ((aLocale.getLanguage() == "th") && (aLocale.getCountry() == "TH")) {
				cal = new BuddhistCalendar(zone, aLocale);
			} else if ((aLocale.getVariant() == "JP") && (aLocale.getLanguage() == "ja") && (aLocale.getCountry() == "JP")) {
				cal = new JapaneseImperialCalendar(zone, aLocale);
			} else {
				cal = new GregorianCalendar(zone, aLocale);
			}
		}
		return cal;
	}

	public static synchronized Locale[] getAvailableLocales() {
		return DateFormat.getAvailableLocales();
	}

	protected abstract void computeTime();

	protected abstract void computeFields();

	public final Date getTime() {
		return new Date(getTimeInMillis());
	}

	public final void setTime(Date date) {
		setTimeInMillis(date.getTime());
	}

	public long getTimeInMillis() {
		if (!isTimeSet) {
			updateTime();
		}
		return time;
	}

	public void setTimeInMillis(long millis) {
		if ((time == millis) && isTimeSet && areFieldsSet && areAllFieldsSet && (zone instanceof ZoneInfo) && !((ZoneInfo) zone).isDirty()) {
			return;
		}
		time = millis;
		isTimeSet = true;
		areFieldsSet = false;
		computeFields();
		areAllFieldsSet = areFieldsSet = true;
	}

	public int get(int field) {
		complete();
		return internalGet(field);
	}

	protected final int internalGet(int field) {
		return fields[field];
	}

	final void internalSet(int field, int value) {
		fields[field] = value;
	}

	public void set(int field, int value) {
		if (areFieldsSet && !areAllFieldsSet) {
			computeFields();
		}
		internalSet(field, value);
		isTimeSet = false;
		areFieldsSet = false;
		isSet[field] = true;
		stamp[field] = nextStamp++;
		if (nextStamp == Integer.MAX_VALUE) {
			adjustStamp();
		}
	}

	public final void set(int year, int month, int date) {
		set(YEAR, year);
		set(MONTH, month);
		set(DATE, date);
	}

	public final void set(int year, int month, int date, int hourOfDay, int minute) {
		set(YEAR, year);
		set(MONTH, month);
		set(DATE, date);
		set(HOUR_OF_DAY, hourOfDay);
		set(MINUTE, minute);
	}

	public final void set(int year, int month, int date, int hourOfDay, int minute, int second) {
		set(YEAR, year);
		set(MONTH, month);
		set(DATE, date);
		set(HOUR_OF_DAY, hourOfDay);
		set(MINUTE, minute);
		set(SECOND, second);
	}

	public final void clear() {
		for (int i = 0; i < fields.length;) {
			stamp[i] = fields[i] = 0;
			isSet[i++] = false;
		}
		areAllFieldsSet = areFieldsSet = false;
		isTimeSet = false;
	}

	public final void clear(int field) {
		fields[field] = 0;
		stamp[field] = UNSET;
		isSet[field] = false;
		areAllFieldsSet = areFieldsSet = false;
		isTimeSet = false;
	}

	public final boolean isSet(int field) {
		return stamp[field] != UNSET;
	}

	public String getDisplayName(int field, int style, Locale locale) {
		if (!checkDisplayNameParams(field, style, SHORT, NARROW_FORMAT, locale, ERA_MASK | MONTH_MASK | DAY_OF_WEEK_MASK | AM_PM_MASK)) {
			return null;
		}
		String calendarType = getCalendarType();
		int fieldValue = get(field);
		if (isStandaloneStyle(style) || isNarrowFormatStyle(style)) {
			String val = CalendarDataUtility.retrieveFieldValueName(calendarType, field, fieldValue, style, locale);
			if (val == null) {
				if (isNarrowFormatStyle(style)) {
					val = CalendarDataUtility.retrieveFieldValueName(calendarType, field, fieldValue, toStandaloneStyle(style), locale);
				} else if (isStandaloneStyle(style)) {
					val = CalendarDataUtility.retrieveFieldValueName(calendarType, field, fieldValue, getBaseStyle(style), locale);
				}
			}
			return val;
		}
		DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
		String[] strings = getFieldStrings(field, style, symbols);
		if (strings != null) {
			if (fieldValue < strings.length) {
				return strings[fieldValue];
			}
		}
		return null;
	}

	public Map<String, Integer> getDisplayNames(int field, int style, Locale locale) {
		if (!checkDisplayNameParams(field, style, ALL_STYLES, NARROW_FORMAT, locale, ERA_MASK | MONTH_MASK | DAY_OF_WEEK_MASK | AM_PM_MASK)) {
			return null;
		}
		String calendarType = getCalendarType();
		if ((style == ALL_STYLES) || isStandaloneStyle(style) || isNarrowFormatStyle(style)) {
			Map<String, Integer> map;
			map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field, style, locale);
			if (map == null) {
				if (isNarrowFormatStyle(style)) {
					map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field, toStandaloneStyle(style), locale);
				} else if (style != ALL_STYLES) {
					map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field, getBaseStyle(style), locale);
				}
			}
			return map;
		}
		return getDisplayNamesImpl(field, style, locale);
	}

	private Map<String, Integer> getDisplayNamesImpl(int field, int style, Locale locale) {
		DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
		String[] strings = getFieldStrings(field, style, symbols);
		if (strings != null) {
			Map<String, Integer> names = new HashMap<>();
			for (int i = 0; i < strings.length; i++) {
				if (strings[i].length() == 0) {
					continue;
				}
				names.put(strings[i], i);
			}
			return names;
		}
		return null;
	}

	boolean checkDisplayNameParams(int field, int style, int minStyle, int maxStyle, Locale locale, int fieldMask) {
		int baseStyle = getBaseStyle(style);
		if ((field < 0) || (field >= fields.length) || (baseStyle < minStyle) || (baseStyle > maxStyle)) {
			throw new IllegalArgumentException();
		}
		if (locale == null) {
			throw new NullPointerException();
		}
		return isFieldSet(fieldMask, field);
	}

	private String[] getFieldStrings(int field, int style, DateFormatSymbols symbols) {
		int baseStyle = getBaseStyle(style);
		if (baseStyle == NARROW_FORMAT) {
			return null;
		}
		String[] strings = null;
		switch (field) {
		case ERA:
			strings = symbols.getEras();
			break;
		case MONTH:
			strings = (baseStyle == LONG) ? symbols.getMonths() : symbols.getShortMonths();
			break;
		case DAY_OF_WEEK:
			strings = (baseStyle == LONG) ? symbols.getWeekdays() : symbols.getShortWeekdays();
			break;
		case AM_PM:
			strings = symbols.getAmPmStrings();
			break;
		}
		return strings;
	}

	protected void complete() {
		if (!isTimeSet) {
			updateTime();
		}
		if (!areFieldsSet || !areAllFieldsSet) {
			computeFields();
			areAllFieldsSet = areFieldsSet = true;
		}
	}

	final boolean isExternallySet(int field) {
		return stamp[field] >= MINIMUM_USER_STAMP;
	}

	final int getSetStateFields() {
		int mask = 0;
		for (int i = 0; i < fields.length; i++) {
			if (stamp[i] != UNSET) {
				mask |= 1 << i;
			}
		}
		return mask;
	}

	final void setFieldsComputed(int fieldMask) {
		if (fieldMask == ALL_FIELDS) {
			for (int i = 0; i < fields.length; i++) {
				stamp[i] = COMPUTED;
				isSet[i] = true;
			}
			areFieldsSet = areAllFieldsSet = true;
		} else {
			for (int i = 0; i < fields.length; i++) {
				if ((fieldMask & 1) == 1) {
					stamp[i] = COMPUTED;
					isSet[i] = true;
				} else {
					if (areAllFieldsSet && !isSet[i]) {
						areAllFieldsSet = false;
					}
				}
				fieldMask >>>= 1;
			}
		}
	}

	final void setFieldsNormalized(int fieldMask) {
		if (fieldMask != ALL_FIELDS) {
			for (int i = 0; i < fields.length; i++) {
				if ((fieldMask & 1) == 0) {
					stamp[i] = fields[i] = 0;
					isSet[i] = false;
				}
				fieldMask >>= 1;
			}
		}
		areFieldsSet = true;
		areAllFieldsSet = false;
	}

	final boolean isPartiallyNormalized() {
		return areFieldsSet && !areAllFieldsSet;
	}

	final boolean isFullyNormalized() {
		return areFieldsSet && areAllFieldsSet;
	}

	final void setUnnormalized() {
		areFieldsSet = areAllFieldsSet = false;
	}

	static boolean isFieldSet(int fieldMask, int field) {
		return (fieldMask & (1 << field)) != 0;
	}

	final int selectFields() {
		int fieldMask = YEAR_MASK;
		if (stamp[ERA] != UNSET) {
			fieldMask |= ERA_MASK;
		}
		int dowStamp = stamp[DAY_OF_WEEK];
		int monthStamp = stamp[MONTH];
		int domStamp = stamp[DAY_OF_MONTH];
		int womStamp = aggregateStamp(stamp[WEEK_OF_MONTH], dowStamp);
		int dowimStamp = aggregateStamp(stamp[DAY_OF_WEEK_IN_MONTH], dowStamp);
		int doyStamp = stamp[DAY_OF_YEAR];
		int woyStamp = aggregateStamp(stamp[WEEK_OF_YEAR], dowStamp);
		int bestStamp = domStamp;
		if (womStamp > bestStamp) {
			bestStamp = womStamp;
		}
		if (dowimStamp > bestStamp) {
			bestStamp = dowimStamp;
		}
		if (doyStamp > bestStamp) {
			bestStamp = doyStamp;
		}
		if (woyStamp > bestStamp) {
			bestStamp = woyStamp;
		}
		if (bestStamp == UNSET) {
			womStamp = stamp[WEEK_OF_MONTH];
			dowimStamp = Math.max(stamp[DAY_OF_WEEK_IN_MONTH], dowStamp);
			woyStamp = stamp[WEEK_OF_YEAR];
			bestStamp = Math.max(Math.max(womStamp, dowimStamp), woyStamp);
			if (bestStamp == UNSET) {
				bestStamp = domStamp = monthStamp;
			}
		}
		if ((bestStamp == domStamp) || ((bestStamp == womStamp) && (stamp[WEEK_OF_MONTH] >= stamp[WEEK_OF_YEAR])) || ((bestStamp == dowimStamp) && (stamp[DAY_OF_WEEK_IN_MONTH] >= stamp[WEEK_OF_YEAR]))) {
			fieldMask |= MONTH_MASK;
			if (bestStamp == domStamp) {
				fieldMask |= DAY_OF_MONTH_MASK;
			} else {
				assert ((bestStamp == womStamp) || (bestStamp == dowimStamp));
				if (dowStamp != UNSET) {
					fieldMask |= DAY_OF_WEEK_MASK;
				}
				if (womStamp == dowimStamp) {
					if (stamp[WEEK_OF_MONTH] >= stamp[DAY_OF_WEEK_IN_MONTH]) {
						fieldMask |= WEEK_OF_MONTH_MASK;
					} else {
						fieldMask |= DAY_OF_WEEK_IN_MONTH_MASK;
					}
				} else {
					if (bestStamp == womStamp) {
						fieldMask |= WEEK_OF_MONTH_MASK;
					} else {
						assert (bestStamp == dowimStamp);
						if (stamp[DAY_OF_WEEK_IN_MONTH] != UNSET) {
							fieldMask |= DAY_OF_WEEK_IN_MONTH_MASK;
						}
					}
				}
			}
		} else {
			assert ((bestStamp == doyStamp) || (bestStamp == woyStamp) || (bestStamp == UNSET));
			if (bestStamp == doyStamp) {
				fieldMask |= DAY_OF_YEAR_MASK;
			} else {
				assert (bestStamp == woyStamp);
				if (dowStamp != UNSET) {
					fieldMask |= DAY_OF_WEEK_MASK;
				}
				fieldMask |= WEEK_OF_YEAR_MASK;
			}
		}
		int hourOfDayStamp = stamp[HOUR_OF_DAY];
		int hourStamp = aggregateStamp(stamp[HOUR], stamp[AM_PM]);
		bestStamp = (hourStamp > hourOfDayStamp) ? hourStamp : hourOfDayStamp;
		if (bestStamp == UNSET) {
			bestStamp = Math.max(stamp[HOUR], stamp[AM_PM]);
		}
		if (bestStamp != UNSET) {
			if (bestStamp == hourOfDayStamp) {
				fieldMask |= HOUR_OF_DAY_MASK;
			} else {
				fieldMask |= HOUR_MASK;
				if (stamp[AM_PM] != UNSET) {
					fieldMask |= AM_PM_MASK;
				}
			}
		}
		if (stamp[MINUTE] != UNSET) {
			fieldMask |= MINUTE_MASK;
		}
		if (stamp[SECOND] != UNSET) {
			fieldMask |= SECOND_MASK;
		}
		if (stamp[MILLISECOND] != UNSET) {
			fieldMask |= MILLISECOND_MASK;
		}
		if (stamp[ZONE_OFFSET] >= MINIMUM_USER_STAMP) {
			fieldMask |= ZONE_OFFSET_MASK;
		}
		if (stamp[DST_OFFSET] >= MINIMUM_USER_STAMP) {
			fieldMask |= DST_OFFSET_MASK;
		}
		return fieldMask;
	}

	int getBaseStyle(int style) {
		return style & ~STANDALONE_MASK;
	}

	private int toStandaloneStyle(int style) {
		return style | STANDALONE_MASK;
	}

	private boolean isStandaloneStyle(int style) {
		return (style & STANDALONE_MASK) != 0;
	}

	private boolean isNarrowFormatStyle(int style) {
		return style == NARROW_FORMAT;
	}

	private static int aggregateStamp(int stamp_a, int stamp_b) {
		if ((stamp_a == UNSET) || (stamp_b == UNSET)) {
			return UNSET;
		}
		return (stamp_a > stamp_b) ? stamp_a : stamp_b;
	}

	/**
	 * Returns an unmodifiable {@code Set} containing all calendar types
	 * supported by {@code Calendar} in the runtime environment. The available
	 * calendar types can be used for the
	 * <a href="Locale.html#def_locale_extension">Unicode locale extensions</a>.
	 * The {@code Set} returned contains at least {@code "gregory"}. The
	 * calendar types don't include aliases, such as {@code "gregorian"} for
	 * {@code "gregory"}.
	 *
	 * @return an unmodifiable {@code Set} containing all available calendar
	 *         types
	 * @since 1.8
	 * @see #getCalendarType()
	 * @see Calendar.Builder#setCalendarType(String)
	 * @see Locale#getUnicodeLocaleType(String)
	 */
	public static Set<String> getAvailableCalendarTypes() {
		return AvailableCalendarTypes.SET;
	}

	private static class AvailableCalendarTypes {
		private static final Set<String> SET;
		static {
			Set<String> set = new HashSet<>(3);
			set.add("gregory");
			set.add("buddhist");
			set.add("japanese");
			SET = Collections.unmodifiableSet(set);
		}

		private AvailableCalendarTypes() {
		}
	}

	/**
	 * 返回此{@code Calendar}的日历类型。 日历类型由<em> Unicode区域设置数据标记语言（LDML）</em>规范定义。
	 *
	 * <p>
	 * The default implementation of this method returns the class name of this
	 * {@code Calendar} instance. Any subclasses that implement LDML-defined
	 * calendar systems should override this method to return appropriate
	 * calendar types.
	 *
	 * @return the LDML-defined calendar type or the class name of this
	 *         {@code Calendar} instance
	 * @since 1.8
	 * @see <a href="Locale.html#def_extensions">Locale extensions</a>
	 * @see Locale.Builder#setLocale(Locale)
	 * @see Locale.Builder#setUnicodeLocaleKeyword(String, String)
	 */
	public String getCalendarType() {
		return this.getClass().getName();
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		try {
			Calendar that = (Calendar) obj;
			return (compareTo(getMillisOf(that)) == 0) && (lenient == that.lenient) && (firstDayOfWeek == that.firstDayOfWeek) && (minimalDaysInFirstWeek == that.minimalDaysInFirstWeek) && zone.equals(that.zone);
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public int hashCode() {
		int otheritems = (lenient ? 1 : 0) | (firstDayOfWeek << 1) | (minimalDaysInFirstWeek << 4) | (zone.hashCode() << 7);
		long t = getMillisOf(this);
		return (int) t ^ (int) (t >> 32) ^ otheritems;
	}

	public boolean before(Object when) {
		return (when instanceof Calendar) && (compareTo((Calendar) when) < 0);
	}

	public boolean after(Object when) {
		return (when instanceof Calendar) && (compareTo((Calendar) when) > 0);
	}

	@Override
	public int compareTo(Calendar anotherCalendar) {
		return compareTo(getMillisOf(anotherCalendar));
	}

	abstract public void add(int field, int amount);

	abstract public void roll(int field, boolean up);

	public void roll(int field, int amount) {
		while (amount > 0) {
			roll(field, true);
			amount--;
		}
		while (amount < 0) {
			roll(field, false);
			amount++;
		}
	}

	public void setTimeZone(TimeZone value) {
		zone = value;
		sharedZone = false;
		areAllFieldsSet = areFieldsSet = false;
	}

	public TimeZone getTimeZone() {
		if (sharedZone) {
			zone = (TimeZone) zone.clone();
			sharedZone = false;
		}
		return zone;
	}

	TimeZone getZone() {
		return zone;
	}

	void setZoneShared(boolean shared) {
		sharedZone = shared;
	}

	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	public boolean isLenient() {
		return lenient;
	}

	public void setFirstDayOfWeek(int value) {
		if (firstDayOfWeek == value) {
			return;
		}
		firstDayOfWeek = value;
		invalidateWeekFields();
	}

	public int getFirstDayOfWeek() {
		return firstDayOfWeek;
	}

	public void setMinimalDaysInFirstWeek(int value) {
		if (minimalDaysInFirstWeek == value) {
			return;
		}
		minimalDaysInFirstWeek = value;
		invalidateWeekFields();
	}

	public int getMinimalDaysInFirstWeek() {
		return minimalDaysInFirstWeek;
	}

	public boolean isWeekDateSupported() {
		return false;
	}

	public int getWeekYear() {
		throw new UnsupportedOperationException();
	}

	public void setWeekDate(int weekYear, int weekOfYear, int dayOfWeek) {
		throw new UnsupportedOperationException();
	}

	public int getWeeksInWeekYear() {
		throw new UnsupportedOperationException();
	}

	abstract public int getMinimum(int field);

	abstract public int getMaximum(int field);

	abstract public int getGreatestMinimum(int field);

	abstract public int getLeastMaximum(int field);

	public int getActualMinimum(int field) {
		int fieldValue = getGreatestMinimum(field);
		int endValue = getMinimum(field);
		if (fieldValue == endValue) {
			return fieldValue;
		}
		Calendar work = (Calendar) clone();
		work.setLenient(true);
		int result = fieldValue;
		do {
			work.set(field, fieldValue);
			if (work.get(field) != fieldValue) {
				break;
			} else {
				result = fieldValue;
				fieldValue--;
			}
		} while (fieldValue >= endValue);
		return result;
	}

	public int getActualMaximum(int field) {
		int fieldValue = getLeastMaximum(field);
		int endValue = getMaximum(field);
		if (fieldValue == endValue) {
			return fieldValue;
		}
		Calendar work = (Calendar) clone();
		work.setLenient(true);
		if ((field == WEEK_OF_YEAR) || (field == WEEK_OF_MONTH)) {
			work.set(DAY_OF_WEEK, firstDayOfWeek);
		}
		int result = fieldValue;
		do {
			work.set(field, fieldValue);
			if (work.get(field) != fieldValue) {
				break;
			} else {
				result = fieldValue;
				fieldValue++;
			}
		} while (fieldValue <= endValue);
		return result;
	}

	@Override
	public Object clone() {
		try {
			Calendar other = (Calendar) super.clone();
			other.fields = new int[FIELD_COUNT];
			other.isSet = new boolean[FIELD_COUNT];
			other.stamp = new int[FIELD_COUNT];
			for (int i = 0; i < FIELD_COUNT; i++) {
				other.fields[i] = fields[i];
				other.stamp[i] = stamp[i];
				other.isSet[i] = isSet[i];
			}
			other.zone = (TimeZone) zone.clone();
			return other;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	private static final String[] FIELD_NAME = { "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH", "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR", "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET" };

	static String getFieldName(int field) {
		return FIELD_NAME[field];
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder(800);
		buffer.append(getClass().getName()).append('[');
		appendValue(buffer, "time", isTimeSet, time);
		buffer.append(",areFieldsSet=").append(areFieldsSet);
		buffer.append(",areAllFieldsSet=").append(areAllFieldsSet);
		buffer.append(",lenient=").append(lenient);
		buffer.append(",zone=").append(zone);
		appendValue(buffer, ",firstDayOfWeek", true, firstDayOfWeek);
		appendValue(buffer, ",minimalDaysInFirstWeek", true, minimalDaysInFirstWeek);
		for (int i = 0; i < FIELD_COUNT; ++i) {
			buffer.append(',');
			appendValue(buffer, FIELD_NAME[i], isSet(i), fields[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	private static void appendValue(StringBuilder sb, String item, boolean valid, long value) {
		sb.append(item).append('=');
		if (valid) {
			sb.append(value);
		} else {
			sb.append('?');
		}
	}

	private void setWeekCountData(Locale desiredLocale) {
		int[] data = cachedLocaleData.get(desiredLocale);
		if (data == null) {
			data = new int[2];
			data[0] = CalendarDataUtility.retrieveFirstDayOfWeek(desiredLocale);
			data[1] = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(desiredLocale);
			cachedLocaleData.putIfAbsent(desiredLocale, data);
		}
		firstDayOfWeek = data[0];
		minimalDaysInFirstWeek = data[1];
	}

	private void updateTime() {
		computeTime();
		isTimeSet = true;
	}

	private int compareTo(long t) {
		long thisTime = getMillisOf(this);
		return (thisTime > t) ? 1 : (thisTime == t) ? 0 : -1;
	}

	private static long getMillisOf(Calendar calendar) {
		if (calendar.isTimeSet) {
			return calendar.time;
		}
		Calendar cal = (Calendar) calendar.clone();
		cal.setLenient(true);
		return cal.getTimeInMillis();
	}

	private void adjustStamp() {
		int max = MINIMUM_USER_STAMP;
		int newStamp = MINIMUM_USER_STAMP;
		for (;;) {
			int min = Integer.MAX_VALUE;
			for (int v : stamp) {
				if ((v >= newStamp) && (min > v)) {
					min = v;
				}
				if (max < v) {
					max = v;
				}
			}
			if ((max != min) && (min == Integer.MAX_VALUE)) {
				break;
			}
			for (int i = 0; i < stamp.length; i++) {
				if (stamp[i] == min) {
					stamp[i] = newStamp;
				}
			}
			newStamp++;
			if (min == max) {
				break;
			}
		}
		nextStamp = newStamp;
	}

	private void invalidateWeekFields() {
		if ((stamp[WEEK_OF_MONTH] != COMPUTED) && (stamp[WEEK_OF_YEAR] != COMPUTED)) {
			return;
		}
		Calendar cal = (Calendar) clone();
		cal.setLenient(true);
		cal.clear(WEEK_OF_MONTH);
		cal.clear(WEEK_OF_YEAR);
		if (stamp[WEEK_OF_MONTH] == COMPUTED) {
			int weekOfMonth = cal.get(WEEK_OF_MONTH);
			if (fields[WEEK_OF_MONTH] != weekOfMonth) {
				fields[WEEK_OF_MONTH] = weekOfMonth;
			}
		}
		if (stamp[WEEK_OF_YEAR] == COMPUTED) {
			int weekOfYear = cal.get(WEEK_OF_YEAR);
			if (fields[WEEK_OF_YEAR] != weekOfYear) {
				fields[WEEK_OF_YEAR] = weekOfYear;
			}
		}
	}

	private synchronized void writeObject(ObjectOutputStream stream) throws IOException {
		if (!isTimeSet) {
			try {
				updateTime();
			} catch (IllegalArgumentException e) {
			}
		}
		TimeZone savedZone = null;
		if (zone instanceof ZoneInfo) {
			SimpleTimeZone stz = ((ZoneInfo) zone).getLastRuleInstance();
			if (stz == null) {
				stz = new SimpleTimeZone(zone.getRawOffset(), zone.getID());
			}
			savedZone = zone;
			zone = stz;
		}
		stream.defaultWriteObject();
		stream.writeObject(savedZone);
		if (savedZone != null) {
			zone = savedZone;
		}
	}

	private static class CalendarAccessControlContext {
		private static final AccessControlContext INSTANCE;
		static {
			RuntimePermission perm = new RuntimePermission("accessClassInPackage.sun.util.calendar");
			PermissionCollection perms = perm.newPermissionCollection();
			perms.add(perm);
			INSTANCE = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, perms) });
		}

		private CalendarAccessControlContext() {
		}
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final ObjectInputStream input = stream;
		input.defaultReadObject();
		stamp = new int[FIELD_COUNT];
		if (serialVersionOnStream >= 2) {
			isTimeSet = true;
			if (fields == null) {
				fields = new int[FIELD_COUNT];
			}
			if (isSet == null) {
				isSet = new boolean[FIELD_COUNT];
			}
		} else if (serialVersionOnStream >= 0) {
			for (int i = 0; i < FIELD_COUNT; ++i) {
				stamp[i] = isSet[i] ? COMPUTED : UNSET;
			}
		}
		serialVersionOnStream = currentSerialVersion;
		ZoneInfo zi = null;
		try {
			zi = AccessController.doPrivileged((PrivilegedExceptionAction<ZoneInfo>) () -> (ZoneInfo) input.readObject(), CalendarAccessControlContext.INSTANCE);
		} catch (PrivilegedActionException pae) {
			Exception e = pae.getException();
			if (!(e instanceof OptionalDataException)) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				} else if (e instanceof IOException) {
					throw (IOException) e;
				} else if (e instanceof ClassNotFoundException) {
					throw (ClassNotFoundException) e;
				}
				throw new RuntimeException(e);
			}
		}
		if (zi != null) {
			zone = zi;
		}
		if (zone instanceof SimpleTimeZone) {
			String id = zone.getID();
			TimeZone tz = TimeZone.getTimeZone(id);
			if ((tz != null) && tz.hasSameRules(zone) && tz.getID().equals(id)) {
				zone = tz;
			}
		}
	}

	/**
	 * 将此对象转换为{@link Instant}。
	 * <p>
	 * The conversion creates an {@code Instant} that represents the same point
	 * on the time-line as this {@code Calendar}.
	 *
	 * @return the instant representing the same point on the time-line
	 * @since 1.8
	 */
	public final Instant toInstant() {
		return Instant.ofEpochMilli(getTimeInMillis());
	}
}
