import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class KeyManager {

    private byte[] _secretKey; //TODO isto esta aqui a balda, tem de ser guardado como deve ser e tem de ser guardada a chave secreta associando-a ao respetivo utilizador

    private static class SingletonHolder {
        private static final KeyManager instance = new KeyManager();
    }

    private KeyManager() {
        _secretKey = null;
    }

    public static synchronized KeyManager getInstance() {
        return SingletonHolder.instance;
    }

    public void setSecretKey(byte[] secretKey) {
        _secretKey = secretKey;
    }

    public SecretKey getSecretKey(String algorithm) {
        if(_secretKey == null) {
            return null;
        }
        return new SecretKeySpec(_secretKey, algorithm);
    }

    public byte[] generateSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32]; //TODO number of bytes of secret generated
        secureRandom.nextBytes(key);
        return key;
    }

    //Base64 Encoder and Decoder don't work for Android with API < 26 and the android used for testing with the lowest API version has API 23
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
