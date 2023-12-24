package tlecuyer.RSA;

import java.math.BigInteger;

public class ASCIIConvert {

	//Method converts a string message to ASCII integers
	public BigInteger toInts(String s) {
	    char[] cArray = s.toCharArray();
	    StringBuilder sBuild = new StringBuilder();
	    
	    for (char c : cArray) {
	        int tmp = (int) c;
	        //Puts ASCII values to 3 digits
	        String padded = String.format("%03d", tmp);
	        sBuild.append(padded);
	    }
	    return new BigInteger(sBuild.toString());
	}
	
	//Message converts ASCII integers to their correct letters
	public String toLetters(BigInteger s, boolean leadingZero) {
	    StringBuilder sBuild = new StringBuilder();
	    //Counter if a zero has been added as BigInteger does not allow leading zeroes
	    boolean addedZero = true;
	    String sString = s.toString();
	    //If there is a leading zero in the ASCII value then once needs to be added in the first iteration
	    if(leadingZero == true) {
	    	addedZero = false;
	    }
	    //Loop runs for every number in the Big Integer, taking chunks of 3 at a time
	    for (int i = 0; i < sString.length(); i += 3) {
	    	//If there is a leading zero and no zero has been added
	        if (leadingZero && !addedZero) {
	        	//Grabs only the first two instead of 3 as there is missing a leading zero
	            String chunk = sString.substring(i, Math.min(i + 2, sString.length()));
	            String strippedValue = chunk.replaceFirst("^0+", "");
	            if (!strippedValue.isEmpty()) {
	                int asciiValue = Integer.parseInt(strippedValue);
	                String zero = "0" + asciiValue;
	                int zeroFormatted = Integer.parseInt(zero);
	                char tmp = (char) zeroFormatted;
	                sBuild.append(tmp);
	                addedZero = true;
	                //Subtract 1 from the i iteration as it is 2 and not 3 long
	                i = i-1;
	            }
	            //Base case if there is no leading zero
	        } else {
	        	//takes every 3 integers in a chunk
	            String chunk = sString.substring(i, Math.min(i + 3, sString.length()));  
	            
	            	//Converts the integers to chars
	                int asciiValue = Integer.parseInt(chunk);
	                char tmp = (char) asciiValue;
	                sBuild.append(tmp);
	            }
	        }
	    return sBuild.toString();
	}

		
}

