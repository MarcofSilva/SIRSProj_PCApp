package Main;

import com.sun.deploy.uitoolkit.impl.awt.AWTPluginUIToolkit;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class KeyManager {

    private byte[] _secretKey; //TODO isto esta aqui a balda, tem de ser guardado como deve ser e tem de ser guardada a chave secreta associando-a ao respetivo utilizador
    private PublicKey _publicKey;
    private byte[] _fileEncryptor;
    private PrivateKey _privateKey;


    private static final String MAC_ALGORITHM = "HmacSHA256";

    private MACHandler macHandler;
    private AuthorizationHandler authHandler;

    private static class SingletonHolder {
        private static final KeyManager instance = new KeyManager();
    }

    private KeyManager() {
        _secretKey = null;
        macHandler = new MACHandler();
        authHandler = new AuthorizationHandler();

    }

    public static synchronized KeyManager getInstance() {
        return SingletonHolder.instance;
    }


    public void encrypt(String username){ //TODO encrypt with private key
        System.out.println("encrypting...");
        List<String> userFiles = Manager.getInstance().getUser(username).get_files();
        for (String filepath: userFiles) {
            File f = new File(filepath);
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, getFileEncryptor("AES"));
                byte[] encrypted = cipher.doFinal(Files.readAllBytes(f.toPath()));
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(encrypted);
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                }
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
        encryptFileDecryptor();
    }

    public void decrypt(String username){
        System.out.println("decrypting...");
        decryptFileDecryptor(username);
        List<String> userFiles = Manager.getInstance().getUser(username).get_files();
        for (String filepath: userFiles) {
            File f = new File(filepath);
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, getFileEncryptor("AES"));
                byte[] decrypted = cipher.doFinal(Files.readAllBytes(f.toPath()));
                try (FileOutputStream fos = new FileOutputStream(filepath)) {
                    fos.write(decrypted);
                    //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
                }
                String s = new String(decrypted);
                System.out.println("decrypted :" + s);
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

    public SecretKey getSecretKey(String algorithm) { //esta e a do qrcode
        if(_secretKey == null) {
            return null;
        }
        return new SecretKeySpec(_secretKey, algorithm);
    }

    public void setSecretKey(byte[] secretKey) {
        _secretKey = secretKey;
    }

    public SecretKey getFileEncryptor(String algorithm) {
        if(_fileEncryptor == null) {
            return null;
        }
        return new SecretKeySpec(_fileEncryptor, algorithm);
    }

    public void generateFileEncryptor(String username){
            _fileEncryptor = generateSecret(16); //TODO key should not be byte[]
    }

    private SecretKeySpec decryptFileDecryptor(String username){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, _privateKey);
            byte[] encryptor = cipher.doFinal(_fileEncryptor);
            return new SecretKeySpec(encryptor, "AES");
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();
        } catch (NoSuchPaddingException nsp) {
            nsp.printStackTrace();
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
        } catch (IllegalBlockSizeException ibs) {
            ibs.printStackTrace();
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
        }
        return null;
    }

    private void encryptFileDecryptor(){
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
            _fileEncryptor = cipher.doFinal(_fileEncryptor);//TODO associate with user
        } catch (NoSuchAlgorithmException nsa) {
            nsa.printStackTrace();
        } catch (NoSuchPaddingException nsp) {
            nsp.printStackTrace();
        } catch (InvalidKeyException ike) {
            ike.printStackTrace();
        } catch (IllegalBlockSizeException ibs) {
            ibs.printStackTrace();
        } catch (BadPaddingException bpe) {
            bpe.printStackTrace();
        }
    }

    public byte[] generateSecret(int numbytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[numbytes]; //TODO number of bytes of secret generated
        secureRandom.nextBytes(key);
        return key;
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

    public void setPrivateKey(byte[] privateKeyBytes) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            _privateKey = privateKey;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (InvalidKeySpecException ike){
            ike.printStackTrace();
        }
    }

    //Prepare the message to send with all the security procedures necessary for the context
    public byte[] prepareMessageToSend(long sessionNumber) throws Exception {
        ByteBuffer byteBuffer;
        byte[] msg = new byte[0];
        //Apply the security procedures
        //TODO

        //Encrypt the MAC
        byte[] content = authHandler.addTimestampAndSessionNumber(msg, sessionNumber);
        /*byte[] IVandEncryptedMsg = encrypt(content, getSecretKey("AES")); //TODO the key need to be reviewed
        byte[] mac = macHandler.addMAC(IVandEncryptedMsg, getSecretKey(MAC_ALGORITHM));
        //Concatenate encrypted message and mac of message
        byte[] secureMsg = new byte[IVandEncryptedMsg.length + mac.length];
        System.arraycopy(IVandEncryptedMsg, 0, secureMsg, 0, IVandEncryptedMsg.length);
        System.arraycopy(mac, 0, secureMsg, IVandEncryptedMsg.length, mac.length);
        return secureMsg;*/
        return content;
    }

    //Returns true if message is valid and false otherwise
    public boolean validateMessageReceived(byte[] input, long sessionNumber) throws Exception {
        byte[] IVandEncrypted;
        byte[] decrypted;
        byte[] msg;
        //if((IVandEncrypted = macHandler.validateMAC(input, getSecretKey(MAC_ALGORITHM))) != null) {
            //if((decrypted = decrypt(IVandEncrypted, getSecretKey("AES"))) != null) { //TODO the key need to be reviewed
                if((msg = authHandler.validateTimestampAndSessionNumber(input, sessionNumber)) != null) { //Incremented the session number TODO input e na verdade decrypt
                    //All security requirements validated
                    return true;
                }
        //    }
        //}
        //Reject message and connection
        //return false;
        return false; //TODO isto tem de ser false...so para debug
    }

    public byte[] encrypt(byte[] array, SecretKey secretKey) throws Exception {
        // Generate IV.
        int ivSize = 16;
        byte[] iv = generateSecret(ivSize);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Encrypt.
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(array);

        // Concatenate IV and encrypted part.
        byte[] IVandEncryptedMsg = new byte[ivSize + encrypted.length];
        System.arraycopy(iv, 0, IVandEncryptedMsg, 0, ivSize);
        System.arraycopy(encrypted, 0, IVandEncryptedMsg, ivSize, encrypted.length);

        return IVandEncryptedMsg;
    }

    public byte[] decrypt(byte[] IVandEncryptedMsg, SecretKey secretKey) throws Exception {
        int ivSize = 16;
        int keySize = 16;

        // Extract IV.
        byte[] iv = new byte[ivSize];
        System.arraycopy(IVandEncryptedMsg, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted part.
        int encryptedSize = IVandEncryptedMsg.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(IVandEncryptedMsg, ivSize, encryptedBytes, 0, encryptedSize);

        // Decrypt.
        Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);

        return decrypted;
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
