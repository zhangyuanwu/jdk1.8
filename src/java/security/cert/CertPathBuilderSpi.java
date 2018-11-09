package java.security.cert;

import java.security.InvalidAlgorithmParameterException;

public abstract class CertPathBuilderSpi {

	public CertPathBuilderSpi() {
	}

	public abstract CertPathBuilderResult engineBuild(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException;

	/**
	 * Returns a {@code CertPathChecker} that this implementation uses to check
	 * the revocation status of certificates. A PKIX implementation returns
	 * objects of type {@code PKIXRevocationChecker}.
	 *
	 * <p>
	 * The primary purpose of this method is to allow callers to specify
	 * additional input parameters and options specific to revocation checking.
	 * See the class description of {@code CertPathBuilder} for an example.
	 *
	 * <p>
	 * This method was added to version 1.8 of the Java Platform Standard
	 * Edition. In order to maintain backwards compatibility with existing
	 * service providers, this method cannot be abstract and by default throws
	 * an {@code UnsupportedOperationException}.
	 *
	 * @return a {@code CertPathChecker} that this implementation uses to check
	 *         the revocation status of certificates
	 * @throws UnsupportedOperationException
	 *             if this method is not supported
	 * @since 1.8
	 */
	public CertPathChecker engineGetRevocationChecker() {
		throw new UnsupportedOperationException();
	}
}
