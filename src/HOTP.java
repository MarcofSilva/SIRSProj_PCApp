/*import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;*/

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

//RFC4226
class HOTP {

    private static final String MAC_ALGORITHM = "HmacSHA1"; //HmacSHA1 returns a 160bit (20bytes) message digest
    private String _secretKey;
    private int _numberDigitsOTP;

    public HOTP(String secretKey, int numberDigitsOTP) {
        _secretKey = secretKey;
        _numberDigitsOTP = numberDigitsOTP;
    }

    public void set_secretKey(String _secretKey) {
        this._secretKey = _secretKey;
    }

    protected String generateOTP(long counter) {
        if (_secretKey == null) {
            return ""; //The UI activity will interpret this as no key was already created
        }

        //TODO transformation of key and counter for byte array
        byte[] key = hexStringToBytes(_secretKey); //TODO ter em atencao a
        byte[] counterByte = ByteBuffer.allocate(8).putLong(counter).array(); //Long to byte array

        byte[] hmacHash = hmacHash(key, counterByte);

        // This piece of code is used in TOTP for truncate de hash value of 20 bytes generated by de hmacSHA-1
        // This truncated value will be the final one time password
        int offset = hmacHash[hmacHash.length-1] & 0xf;
        int truncatedHash = (hmacHash[offset++] & 0x7f) << 24 |
                (hmacHash[offset++] & 0xff) << 16 |
                (hmacHash[offset++] & 0xff) << 8 |
                (hmacHash[offset] & 0xff);
        int otp = (truncatedHash % (int)(Math.pow(10, _numberDigitsOTP)));

        String finalOTP = Integer.toString(otp);
        //Get to the desired size in those rare cases where the modulo had given us one digit less
        while(finalOTP.length() < _numberDigitsOTP) {
            finalOTP = "0" + finalOTP;
        }

        return finalOTP;
    }

    private byte[] hmacHash(byte[] secret, byte[] counter) {
        byte[] digest = null;

        Mac mac = null;
        try {
            mac = Mac.getInstance(MAC_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(secret, "RAW");

            mac.init(secretKeySpec);

            digest = mac.doFinal(counter);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //TODO deal with exception
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            //TODO deal with exception
        }

        return digest;
    }

    //Base64 Encoder and Decoder don't work for Android with API < 26 and the android smartphone with the lowest API version has API 23
    //As so the hexStringToBytes and byteArrayToHexString help us to convert byte arrays to strings and vice versa
    public byte[] hexStringToBytes(String hexInputString){

        byte[] byteArray = new byte[hexInputString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexInputString.substring(2*i, 2*i+2), 16);
        }

        return byteArray;
    }

    public String byteArrayToHexString(byte[] byteArray) {

        StringBuffer buffer = new StringBuffer();

        for(int i =0; i < byteArray.length; i++){
            String hex = Integer.toHexString(0xff & byteArray[i]);

            if(hex.length() == 1)
                buffer.append("0");

            buffer.append(hex);
        }
        return buffer.toString();
    }
}