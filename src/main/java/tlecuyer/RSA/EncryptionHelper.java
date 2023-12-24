package tlecuyer.RSA;

import java.math.BigInteger;

public class EncryptionHelper {

	ASCIIConvert ASCII = new ASCIIConvert();
	
	/**
	 * Returns an encrypted message from the text
	 * @param text - the users text to be encrypted
	 * @param modulus - the values of the modulus, n
	 * @param e - the value of e
	 * @return - A BigInteger that is encrypted using the public key and e
	 */
	public BigInteger encrypt(String text, BigInteger modulus, BigInteger e) {
		BigInteger asciiInts = ASCII.toInts(text);
		BigInteger encrypted = asciiInts.modPow(e, modulus);
		return encrypted;
	}
	
	/**
	 * Returns a decrypted message from the encrypted text
	 * @param encryptedText - text that is encrypted
	 * @param privateKey - the users private key, d
	 * @param modulus - the value of the modulus, n
	 * @return
	 */
	public String decrypt(BigInteger encryptedText, BigInteger privateKey, BigInteger modulus, boolean leadingZero) {
		BigInteger decryptedInt = encryptedText.modPow(privateKey, modulus);
		String messageDecrypted = ASCII.toLetters(decryptedInt, leadingZero);
		return messageDecrypted;
	}
}
