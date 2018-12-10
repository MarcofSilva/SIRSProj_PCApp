package Main;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class KeyManager {

    private byte[] _secretKey; //TODO isto esta aqui a balda, tem de ser guardado como deve ser e tem de ser guardada a chave secreta associando-a ao respetivo utilizador
    private PublicKey _publicKey;
    private byte[] _fileEncryptor;

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

    public void setPublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            _publicKey = publicKey;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ike){
            ike.printStackTrace();
        }
    }

    public void encrypt(String username){ //TODO encrypt with private key
        List<String> userFiles = Manager.getInstance().getUser(username).get_files();
        for (String filepath: userFiles) {
            File f = new File(filepath);
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, getFileEncriptor("AES"));
                byte[] encrypted = cipher.doFinal(Files.readAllBytes(f.toPath()));
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(encrypted);
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                }
                String s = new String(encrypted);
                System.out.println(s);
            } catch(NoSuchAlgorithmException nsa){
                nsa.printStackTrace();
            } catch(NoSuchPaddingException nsp){
                nsp.printStackTrace();
            } catch(InvalidKeyException ike){
                ike.printStackTrace();
            } catch(IOException ioe){
                ioe.printStackTrace();
            } catch(IllegalBlockSizeException ibs){
                ibs.printStackTrace();
            } catch(BadPaddingException bpe){
                bpe.printStackTrace();
            }
        }
    }

    public void decrypt(String username){
        List<String> userFiles = Manager.getInstance().getUser(username).get_files();
        for (String filepath: userFiles) {
            File f = new File(filepath);
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, getFileEncriptor("AES"));
                byte[] decrypted = cipher.doFinal(Files.readAllBytes(f.toPath()));
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(decrypted);
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                }
                String s = new String(decrypted);
                System.out.println(s);
            } catch (NoSuchAlgorithmException nsa) {
                nsa.printStackTrace();
            } catch (NoSuchPaddingException nsp) {
                nsp.printStackTrace();
            } catch (InvalidKeyException ike) {
                ike.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (IllegalBlockSizeException ibs) {
                ibs.printStackTrace();
            } catch (BadPaddingException bpe) {
                bpe.printStackTrace();
            }
        }
    }

    public SecretKey getSecretKey(String algorithm) {
        if(_secretKey == null) {
            return null;
        }
        return new SecretKeySpec(_secretKey, algorithm);
    }

    public SecretKey getFileEncriptor(String algorithm) {
        if(_fileEncryptor == null) {
            return null;
        }
        return new SecretKeySpec(_fileEncryptor, algorithm);
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

    public void generateFileEncryptor(String username){
            byte[] key = generateSecret(); //TODO key should not be byte[]
            _fileEncryptor = key;        //TODO associate with user
    }
}
