package java.security.cert;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import sun.security.jca.*;
import sun.security.jca.GetInstance.Instance;

public class CertPathBuilder {
	private static final String CPB_TYPE = "certpathbuilder.type";
	private final CertPathBuilderSpi builderSpi;
	private final Provider provider;
	private final String algorithm;

	protected CertPathBuilder(CertPathBuilderSpi builderSpi, Provider provider, String algorithm) {
		this.builderSpi = builderSpi;
		this.provider = provider;
		this.algorithm = algorithm;
	}

	public static CertPathBuilder getInstance(String algorithm) throws NoSuchAlgorithmException {
		Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, algorithm);
		return new CertPathBuilder((CertPathBuilderSpi) instance.impl, instance.provider, algorithm);
	}

	public static CertPathBuilder getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
		Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, algorithm, provider);
		return new CertPathBuilder((CertPathBuilderSpi) instance.impl, instance.provider, algorithm);
	}

	public static CertPathBuilder getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
		Instance instance = GetInstance.getInstance("CertPathBuilder", CertPathBuilderSpi.class, algorithm, provider);
		return new CertPathBuilder((CertPathBuilderSpi) instance.impl, instance.provider, algorithm);
	}

	public final Provider getProvider() {
		return provider;
	}

	public final String getAlgorithm() {
		return algorithm;
	}

	public final CertPathBuilderResult build(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException {
		return builderSpi.engineBuild(params);
	}

	public final static String getDefaultType() {
		String cpbtype = AccessController.doPrivileged((PrivilegedAction<String>) () -> Security.getProperty(CPB_TYPE));
		return (cpbtype == null) ? "PKIX" : cpbtype;
	}

	/**
	 * Returns a {@code CertPathChecker} that the encapsulated
	 * {@code CertPathBuilderSpi} implementation uses to check the revocation
	 * status of certificates. A PKIX implementation returns objects of type
	 * {@code PKIXRevocationChecker}. Each invocation of this method returns a
	 * new instance of {@code CertPathChecker}.
	 *
	 * <p>
	 * The primary purpose of this method is to allow callers to specify
	 * additional input parameters and options specific to revocation checking.
	 * See the class description for an example.
	 *
	 * @return a {@code CertPathChecker}
	 * @throws UnsupportedOperationException
	 *             if the service provider does not support this method
	 * @since 1.8
	 */
	public final CertPathChecker getRevocationChecker() {
		return builderSpi.engineGetRevocationChecker();
	}
}
