package java.security.cert;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import sun.security.x509.X509CertImpl;

public abstract class X509Certificate extends Certificate implements X509Extension {
	private static final long serialVersionUID = -2491127588187038216L;
	private transient X500Principal subjectX500Principal, issuerX500Principal;

	protected X509Certificate() {
		super("X.509");
	}

	public abstract void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException;

	public abstract void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException;

	public abstract int getVersion();

	public abstract BigInteger getSerialNumber();

	public abstract Principal getIssuerDN();

	public X500Principal getIssuerX500Principal() {
		if (issuerX500Principal == null) {
			issuerX500Principal = X509CertImpl.getIssuerX500Principal(this);
		}
		return issuerX500Principal;
	}

	public abstract Principal getSubjectDN();

	public X500Principal getSubjectX500Principal() {
		if (subjectX500Principal == null) {
			subjectX500Principal = X509CertImpl.getSubjectX500Principal(this);
		}
		return subjectX500Principal;
	}

	public abstract Date getNotBefore();

	public abstract Date getNotAfter();

	public abstract byte[] getTBSCertificate() throws CertificateEncodingException;

	public abstract byte[] getSignature();

	/**
	 * Gets the signature algorithm name for the certificate signature
	 * algorithm. An example is the string "SHA256withRSA". The ASN.1 definition
	 * for this is:
	 *
	 * <pre>
	 * signatureAlgorithm   AlgorithmIdentifier
	 *
	 * AlgorithmIdentifier  ::=  SEQUENCE  {
	 *     algorithm               OBJECT IDENTIFIER,
	 *     parameters              ANY DEFINED BY algorithm OPTIONAL  }
	 *                             -- contains a value of the type
	 *                             -- registered for use with the
	 *                             -- algorithm object identifier value
	 * </pre>
	 *
	 * <p>
	 * The algorithm name is determined from the {@code algorithm} OID string.
	 *
	 * @return the signature algorithm name.
	 */
	public abstract String getSigAlgName();

	/**
	 * Gets the signature algorithm OID string from the certificate. An OID is
	 * represented by a set of nonnegative whole numbers separated by periods.
	 * For example, the string "1.2.840.10040.4.3" identifies the SHA-1 with DSA
	 * signature algorithm defined in
	 * <a href="http://www.ietf.org/rfc/rfc3279.txt">RFC 3279: Algorithms and
	 * Identifiers for the Internet X.509 Public Key Infrastructure Certificate
	 * and CRL Profile</a>.
	 *
	 * <p>
	 * See {@link #getSigAlgName() getSigAlgName} for relevant ASN.1
	 * definitions.
	 *
	 * @return the signature algorithm OID string.
	 */
	public abstract String getSigAlgOID();

	/**
	 * Gets the DER-encoded signature algorithm parameters from this
	 * certificate's signature algorithm. In most cases, the signature algorithm
	 * parameters are null; the parameters are usually supplied with the
	 * certificate's public key. If access to individual parameter values is
	 * needed then use {@link java.security.AlgorithmParameters
	 * AlgorithmParameters} and instantiate with the name returned by
	 * {@link #getSigAlgName() getSigAlgName}.
	 *
	 * <p>
	 * See {@link #getSigAlgName() getSigAlgName} for relevant ASN.1
	 * definitions.
	 *
	 * @return the DER-encoded signature algorithm parameters, or null if no
	 *         parameters are present.
	 */
	public abstract byte[] getSigAlgParams();

	/**
	 * Gets the {@code issuerUniqueID} value from the certificate. The issuer
	 * unique identifier is present in the certificate to handle the possibility
	 * of reuse of issuer names over time. RFC 3280 recommends that names not be
	 * reused and that conforming certificates not make use of unique
	 * identifiers. Applications conforming to that profile should be capable of
	 * parsing unique identifiers and making comparisons.
	 *
	 * <p>
	 * The ASN.1 definition for this is:
	 *
	 * <pre>
	 * issuerUniqueID  [1]  IMPLICIT UniqueIdentifier OPTIONAL
	 *
	 * UniqueIdentifier  ::=  BIT STRING
	 * </pre>
	 *
	 * @return the issuer unique identifier or null if it is not present in the
	 *         certificate.
	 */
	public abstract boolean[] getIssuerUniqueID();

	/**
	 * Gets the {@code subjectUniqueID} value from the certificate.
	 *
	 * <p>
	 * The ASN.1 definition for this is:
	 *
	 * <pre>
	 * subjectUniqueID  [2]  IMPLICIT UniqueIdentifier OPTIONAL
	 *
	 * UniqueIdentifier  ::=  BIT STRING
	 * </pre>
	 *
	 * @return the subject unique identifier or null if it is not present in the
	 *         certificate.
	 */
	public abstract boolean[] getSubjectUniqueID();

	/**
	 * Gets a boolean array representing bits of the {@code KeyUsage} extension,
	 * (OID = 2.5.29.15). The key usage extension defines the purpose (e.g.,
	 * encipherment, signature, certificate signing) of the key contained in the
	 * certificate. The ASN.1 definition for this is:
	 *
	 * <pre>
	 * KeyUsage ::= BIT STRING {
	 *     digitalSignature        (0),
	 *     nonRepudiation          (1),
	 *     keyEncipherment         (2),
	 *     dataEncipherment        (3),
	 *     keyAgreement            (4),
	 *     keyCertSign             (5),
	 *     cRLSign                 (6),
	 *     encipherOnly            (7),
	 *     decipherOnly            (8) }
	 * </pre>
	 *
	 * RFC 3280 recommends that when used, this be marked as a critical
	 * extension.
	 *
	 * @return the KeyUsage extension of this certificate, represented as an
	 *         array of booleans. The order of KeyUsage values in the array is
	 *         the same as in the above ASN.1 definition. The array will contain
	 *         a value for each KeyUsage defined above. If the KeyUsage list
	 *         encoded in the certificate is longer than the above list, it will
	 *         not be truncated. Returns null if this certificate does not
	 *         contain a KeyUsage extension.
	 */
	public abstract boolean[] getKeyUsage();

	/**
	 * Gets an unmodifiable list of Strings representing the OBJECT IDENTIFIERs
	 * of the {@code ExtKeyUsageSyntax} field of the extended key usage
	 * extension, (OID = 2.5.29.37). It indicates one or more purposes for which
	 * the certified public key may be used, in addition to or in place of the
	 * basic purposes indicated in the key usage extension field. The ASN.1
	 * definition for this is:
	 *
	 * <pre>
	 * ExtKeyUsageSyntax ::= SEQUENCE SIZE (1..MAX) OF KeyPurposeId
	 *
	 * KeyPurposeId ::= OBJECT IDENTIFIER
	 * </pre>
	 *
	 * Key purposes may be defined by any organization with a need. Object
	 * identifiers used to identify key purposes shall be assigned in accordance
	 * with IANA or ITU-T Rec. X.660 | ISO/IEC/ITU 9834-1.
	 * <p>
	 * This method was added to version 1.4 of the Java 2 Platform Standard
	 * Edition. In order to maintain backwards compatibility with existing
	 * service providers, this method is not {@code abstract} and it provides a
	 * default implementation. Subclasses should override this method with a
	 * correct implementation.
	 *
	 * @return the ExtendedKeyUsage extension of this certificate, as an
	 *         unmodifiable list of object identifiers represented as Strings.
	 *         Returns null if this certificate does not contain an
	 *         ExtendedKeyUsage extension.
	 * @throws CertificateParsingException
	 *             if the extension cannot be decoded
	 * @since 1.4
	 */
	public List<String> getExtendedKeyUsage() throws CertificateParsingException {
		return X509CertImpl.getExtendedKeyUsage(this);
	}

	public abstract int getBasicConstraints();

	public Collection<List<?>> getSubjectAlternativeNames() throws CertificateParsingException {
		return X509CertImpl.getSubjectAlternativeNames(this);
	}

	public Collection<List<?>> getIssuerAlternativeNames() throws CertificateParsingException {
		return X509CertImpl.getIssuerAlternativeNames(this);
	}

	/**
	 * Verifies that this certificate was signed using the private key that
	 * corresponds to the specified public key. This method uses the signature
	 * verification engine supplied by the specified provider. Note that the
	 * specified Provider object does not have to be registered in the provider
	 * list.
	 *
	 * This method was added to version 1.8 of the Java Platform Standard
	 * Edition. In order to maintain backwards compatibility with existing
	 * service providers, this method is not {@code abstract} and it provides a
	 * default implementation.
	 *
	 * @param key
	 *            the PublicKey used to carry out the verification.
	 * @param sigProvider
	 *            the signature provider.
	 *
	 * @exception NoSuchAlgorithmException
	 *                on unsupported signature algorithms.
	 * @exception InvalidKeyException
	 *                on incorrect key.
	 * @exception SignatureException
	 *                on signature errors.
	 * @exception CertificateException
	 *                on encoding errors.
	 * @exception UnsupportedOperationException
	 *                if the method is not supported
	 * @since 1.8
	 */
	public void verify(PublicKey key, Provider sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		X509CertImpl.verify(this, key, sigProvider);
	}
}
