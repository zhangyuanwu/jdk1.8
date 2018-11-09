package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider {
	protected LocaleServiceProvider() {
	}

	public abstract Locale[] getAvailableLocales();

	/**
	 * Returns {@code true} if the given {@code locale} is supported by this
	 * locale service provider. The given {@code locale} may contain
	 * <a href="../Locale.html#def_extensions">extensions</a> that should be
	 * taken into account for the support determination.
	 *
	 * <p>
	 * The default implementation returns {@code true} if the given
	 * {@code locale} is equal to any of the available {@code Locale}s returned
	 * by {@link #getAvailableLocales()} with ignoring any extensions in both
	 * the given {@code locale} and the available locales. Concrete locale
	 * service provider implementations should override this method if those
	 * implementations are {@code Locale} extensions-aware. For example,
	 * {@code DecimalFormatSymbolsProvider} implementations will need to check
	 * extensions in the given {@code locale} to see if any numbering system is
	 * specified and can be supported. However, {@code CollatorProvider}
	 * implementations may not be affected by any particular numbering systems,
	 * and in that case, extensions for numbering systems should be ignored.
	 *
	 * @param locale
	 *            a {@code Locale} to be tested
	 * @return {@code true} if the given {@code locale} is supported by this
	 *         provider; {@code false} otherwise.
	 * @throws NullPointerException
	 *             if the given {@code locale} is {@code null}
	 * @see Locale#hasExtensions()
	 * @see Locale#stripExtensions()
	 * @since 1.8
	 */
	public boolean isSupportedLocale(Locale locale) {
		locale = locale.stripExtensions();
		for (Locale available : getAvailableLocales()) {
			if (locale.equals(available.stripExtensions())) {
				return true;
			}
		}
		return false;
	}
}
