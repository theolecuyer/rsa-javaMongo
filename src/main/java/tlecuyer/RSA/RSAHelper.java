package tlecuyer.RSA;

import java.math.BigInteger;
import java.util.Random;

//TOdo find a way to discard p q and d once found for calculations

public class RSAHelper {
	//New instances of classes
	Random rand = new Random();
	EncryptionHelper enc = new EncryptionHelper();
	//Storage of all required variables for encryption
	private BigInteger p;
	private BigInteger q;
	private BigInteger lambda;
	private BigInteger d;
	public BigInteger e = BigInteger.valueOf(65537);
	public BigInteger n;
	public boolean leadingZero = false;
	
	//Generates a public key, n, finding large primes with 512 bits each, p, and q
	public BigInteger generatePublicKey() {
	    do {
	    	p = BigInteger.probablePrime(512, rand);
	    	q = BigInteger.probablePrime(512, rand);
	        n = p.multiply(q);
	    } while (!isConditionMet(p, q, n));
	    return n;
	}
	
	public BigInteger sendToEncryption(String text, BigInteger publicKey) {
		//Sees if the text would have a leading zero and takes care of it
		leadingZero = false;
		int firstCharAscii = (int) text.charAt(0);
		if (firstCharAscii < 100) {
			//Would result in leading zero
		    leadingZero = true;
		    }
		return enc.encrypt(text, publicKey, e);
	}
	
	public String recieveFromEncryption(BigInteger encryptedText, BigInteger privateKey, BigInteger publicModulus) {
		return enc.decrypt(encryptedText, privateKey, publicModulus, leadingZero);
	}
	
	//Calculates lambda using Carmichael's totient function
	public void calculateLambda() {
		BigInteger phiP = p.subtract(BigInteger.ONE);
		BigInteger phiQ = q.subtract(BigInteger.ONE);
		lambda = LCM(phiP, phiQ);
		
		if(lambda.gcd(e).equals(BigInteger.ONE)) {
			d = e.modInverse(lambda);
		}
		else {
			System.out.println(lambda.gcd(e));
			System.err.println("Lambda(n) and e are not coprime");
		}
	}

	//Calculates the least common multiple of two Big Integers
	public BigInteger LCM(BigInteger a, BigInteger b) {
		BigInteger gcd = a.gcd(b);
		BigInteger top = (a.abs().multiply(b.abs()));
		BigInteger LCM = top.divide(gcd);
		return LCM;
	}
	
	//Calculates d, the modular multiplicative inverse of e modulo lambda(n)
	public BigInteger calculatePrivateKey() {
		if(lambda.gcd(e).equals(BigInteger.ONE)) {
			d = e.modInverse(lambda);
		}
		else {
			System.out.println(lambda.gcd(e));
			System.err.println("Lambda(n) and e are not coprime");
		}
		return d;
	}
	
	//Checks if the conditions for p, q, n have been met
	public static boolean isConditionMet(BigInteger p, BigInteger q, BigInteger n) {
		BigInteger largerValue = p.max(q);
		//Threshold for 20%+ difference in p and q
		BigInteger tHold = largerValue.multiply(BigInteger.TEN).divide(BigInteger.valueOf(50));
		//Calculates the absolute difference between p and q
		BigInteger absDiff = p.subtract(q).abs();
        return p.bitLength() == 512 &&
               q.bitLength() == 512 &&
               n.bitLength() == 1024 &&
               //Ensures a large difference between p and q (the difference in the tHold var)
               absDiff.compareTo(tHold)>=0;
    }
	
	//Checks if the given integer is prime, not used in this program but for testing
	static boolean isPrime(BigInteger num) {
		if (num.compareTo(BigInteger.TWO) < 0) {
            return false;
        }
        for (BigInteger i = BigInteger.valueOf(2); i.compareTo(num.divide(BigInteger.valueOf(2))) <= 0; i = i.add(BigInteger.ONE)) {
            if (num.remainder(i).equals(BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }

}
