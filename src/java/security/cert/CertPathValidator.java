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

public class CertPathValidator {
	private static final String CPV_TYPE = "certpathvalidator.type";
	private final CertPathValidatorSpi validatorSpi;
	private final Provider provider;
	private final String algorithm;

	protected CertPathValidator(CertPathValidatorSpi validatorSpi, Provider provider, String algorithm) {
		this.validatorSpi = validatorSpi;
		this.provider = provider;
		this.algorithm = algorithm;
	}

	public static CertPathValidator getInstance(String algorithm) throws NoSuchAlgorithmException {
		Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, algorithm);
		return new CertPathValidator((CertPathValidatorSpi) instance.impl, instance.provider, algorithm);
	}

	public static CertPathValidator getInstance(String algorithm, String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
		Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, algorithm, provider);
		return new CertPathValidator((CertPathValidatorSpi) instance.impl, instance.provider, algorithm);
	}

	public static CertPathValidator getInstance(String algorithm, Provider provider) throws NoSuchAlgorithmException {
		Instance instance = GetInstance.getInstance("CertPathValidator", CertPathValidatorSpi.class, algorithm, provider);
		return new CertPathValidator((CertPathValidatorSpi) instance.impl, instance.provider, algorithm);
	}

	public final Provider getProvider() {
		return provider;
	}

	public final String getAlgorithm() {
		return algorithm;
	}

	public final CertPathValidatorResult validate(CertPath certPath, CertPathParameters params) throws CertPathValidatorException, InvalidAlgorithmParameterException {
		return validatorSpi.engineValidate(certPath, params);
	}

	public final static String getDefaultType() {
		String cpvtype = AccessController.doPrivileged((PrivilegedAction<String>) () -> Security.getProperty(CPV_TYPE));
		return (cpvtype == null) ? "PKIX" : cpvtype;
	}

	/**
	 * Returns a {@code CertPathChecker} that the encapsulated
	 * {@code CertPathValidatorSpi} implementation uses to check the revocation
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
		return validatorSpi.engineGetRevocationChecker();
	}
}
