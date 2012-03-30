package ch.rgw.crypt;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

import ch.rgw.tools.TimeTool;

/**
 * Class to handle X.509 certificates
 * 
 * @author gerry
 * 
 */
public class X509Cert {
	X509Certificate cert;
	
	/**
	 * Read a Certificate from an InputStream
	 * 
	 * @param in
	 */
	public static X509Cert load(InputStream in){
		X509Cert ret = null;
		try {
			CertificateFactory certificatefactory = CertificateFactory.getInstance("X.509");
			ret = new X509Cert();
			ret.cert = (X509Certificate) certificatefactory.generateCertificate(in);
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return ret;
	}
	
	public String getType(){
		
		return cert.getType();
	}
	
	public int getVersion(){
		return cert.getVersion();
	}
	
	public String getName(){
		return cert.getSubjectDN().getName();
	}
	
	public TimeTool getValidFrom(){
		Date val = cert.getNotBefore();
		TimeTool ret = new TimeTool(val.getTime());
		return ret;
	}
	
	public TimeTool getValidUntil(){
		Date val = cert.getNotAfter();
		TimeTool ret = new TimeTool(val.getTime());
		return ret;
	}
	
	public String getIssuer(){
		return cert.getIssuerDN().getName();
	}
	
	public String getSigAlgorithm(){
		return cert.getSigAlgName();
	}
	
	public String getPublicKeyAlgorith(){
		return cert.getPublicKey().getAlgorithm();
	}
	
	public boolean isValid(){
		/*
		 * int nSize = collectionX509CertificateChain.size(); X509Certificate[] arx509certificate =
		 * new X509Certificate[nSize]; collectionX509CertificateChain.toArray(arx509certificate); //
		 * Working down the chain, for every certificate in the chain, // verify that the subject of
		 * the certificate is the issuer of the // next certificate in the chain. Principal
		 * principalLast = null; for (int i = 0; i < nSize; i++) { X509Certificate x509certificate =
		 * arx509certificate[i]; Principal principalIssuer = x509certificate.getIssuerDN();
		 * Principal principalSubject = x509certificate.getSubjectDN(); if (principalLast != null) {
		 * if (principalIssuer.equals(principalLast)) { try { PublicKey publickey =
		 * arx509certificate[i - 1].getPublicKey(); arx509certificate[i].verify(publickey); } catch
		 * (GeneralSecurityException generalsecurityexception) {
		 * System.out.println("signature verification failed"); return false; } } else {
		 * System.out.println("subject/issuer verification failed"); return false; } } principalLast
		 * = principalSubject; } // Verify that the the first certificate in the chain was issued //
		 * by a third-party that the client trusts. try { PublicKey publickey =
		 * x509certificateRoot.getPublicKey(); arx509certificate[0].verify(publickey); } catch
		 * (GeneralSecurityException generalsecurityexception) {
		 * System.out.println("signature verification failed"); return false; } // Verify that the
		 * last certificate in the chain corresponds to // the server we desire to authenticate.
		 * Principal principalSubject = arx509certificate[nSize - 1].getSubjectDN(); if
		 * (!stringTarget.equals(principalSubject.getName())) {
		 * System.out.println("target verification failed"); return false; } // For every
		 * certificate in the chain, verify that the certificate // is valid at the current time.
		 * Date date = new Date(); for (int i = 0; i < nSize; i++) { try {
		 * arx509certificate[i].checkValidity(date); } catch (GeneralSecurityException
		 * generalsecurityexception) { System.out.println("invalid date"); return false; } }
		 */
		return true;
	}
}
