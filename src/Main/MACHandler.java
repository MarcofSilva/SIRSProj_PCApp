package Main;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MACHandler {
    private static final String MAC_ALGORITHM = "HmacSHA256"; //HmacSHA256 returns a 256bit (32bytes) message digest

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

    public byte[] addMAC(byte[] msg, SecretKey key){
        byte[] mac = getMAC(msg,key);
        ByteBuffer byteBuffer = ByteBuffer.allocate(32  + msg.length);
        byteBuffer.put(msg);
        byteBuffer.put(mac);

        return byteBuffer.array();
    }

    //Returns null if invalid and msg without the mac in the end
    public byte[] validateMAC(byte[] messageMac, SecretKey key) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(messageMac);
        byte[] IVandEncryptedMsg = new byte[messageMac.length- 32];
        byte[] mac = new byte[32];

        byteBuffer.get(IVandEncryptedMsg);
        byteBuffer.get(mac);

        byte[] calculatedMac = getMAC( IVandEncryptedMsg, key );
        if(!Arrays.equals(mac, calculatedMac)) {
            System.out.println("Error, MAC validation failed. Corrupedted message");
            System.out.println("Mac received = " + KeyManager.getInstance().byteArrayToHexString(mac) + " -- Mac calculated = " + KeyManager.getInstance().byteArrayToHexString(calculatedMac));
            return null;
        }
        System.out.println("MAC validated");
        //returns msg without mac
        return IVandEncryptedMsg;
    }

}
