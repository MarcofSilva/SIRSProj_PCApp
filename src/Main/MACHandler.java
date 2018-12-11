package Main;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MACHandler {
    private static final String MAC_ALGORITHM = "HmacSHA256";

    public byte[] getMAC(byte[] message, SecretKey key){
        byte[] digest = null;
        Mac mac;

        try {
            mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(key);
            digest = mac.doFinal(message);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //TODO deal with exception
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            //TODO deal with exception
        }
        return digest;
    }

    //Returns null if invalid and msg without the mac in the end
    public byte[] validateMAC(byte[] messageMac, SecretKey key) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(messageMac);
        byte[] message = new byte[messageMac.length- 256];
        byte[] mac = new byte[256];

        byteBuffer.get(message);
        byteBuffer.get(mac);

        byte[] calculatedMac = getMAC( message, key );
        //TODO podemos comparar 2 arrays desta forma?
        if(mac != calculatedMac) {
            return null;
        }
        //return parte inicial da msg sem o mac
        return message;
    }

}
