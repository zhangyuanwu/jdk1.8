package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider extends LocaleServiceProvider {
	protected TimeZoneNameProvider() {
	}

	public abstract String getDisplayName(String ID, boolean daylight, int style, Locale locale);

	/**
	 * Returns a generic name for the given time zone {@code ID} that's suitable
	 * for presentation to the user in the specified {@code locale}. Generic
	 * time zone names are neutral from standard time and daylight saving time.
	 * For example, "PT" is the short generic name of time zone ID {@code
	 * America/Los_Angeles}, while its short standard time and daylight saving
	 * time names are "PST" and "PDT", respectively. Refer to
	 * {@link #getDisplayName(String, boolean, int, Locale) getDisplayName} for
	 * valid time zone IDs.
	 *
	 * <p>
	 * The default implementation of this method returns {@code null}.
	 *
	 * @param ID
	 *            a time zone ID string
	 * @param style
	 *            either {@link java.util.TimeZone#LONG TimeZone.LONG} or
	 *            {@link java.util.TimeZone#SHORT TimeZone.SHORT}
	 * @param locale
	 *            the desired locale
	 * @return the human-readable generic name of the given time zone in the
	 *         given locale, or {@code null} if it's not available.
	 * @exception IllegalArgumentException
	 *                if <code>style</code> is invalid, or <code>locale</code>
	 *                isn't one of the locales returned from
	 *                {@link LocaleServiceProvider#getAvailableLocales()
	 *                getAvailableLocales()}.
	 * @exception NullPointerException
	 *                if <code>ID</code> or <code>locale</code> is {@code null}
	 * @since 1.8
	 */
	public String getGenericDisplayName(String ID, int style, Locale locale) {
		return null;
	}
}
