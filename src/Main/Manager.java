package Main;

import Bluetooth_Java.BluetoothManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import java.security.MessageDigest;


public class Manager {

    //These two must be in sync with the ones in the android app
    private static final int NUMBER_OF_DIGITS_IN_OTP = 6; //n belongs to [0, 9] (One time password size)
    private static final int TIME_RANGE_PASSWORD = 20; //For how long is a one time password valid until a new gets

    private KeyManager keyManager;
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap(); //prob doesnt need concurrent
    BluetoothManager client; //remote object (phone)

    private static class SingletonHolder {
        private static final Manager instance = new Manager();
    }

    private Manager(){
        keyManager = KeyManager.getInstance();
    }

    public static synchronized Manager getInstance() {
        return SingletonHolder.instance;
    }

    public User getUser(String username){
        if(users.containsKey(username)){
            return users.get(username);
        }
        else return null;
    }

    public int userCheck(String username, String password){

        if (username != null && password != null) {
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            messageDigest.update(password.getBytes());
            String hashedPass = new String(messageDigest.digest());

            if (users.containsKey(username) && users.get(username).get_password().equals(hashedPass)){
                users.get(username).set_isLoggedIn(true);
                return 1;
            }
            else {
                User user = new User(username, hashedPass);
                users.put(username, user);
                users.get(username).set_isLoggedIn(true);
                return 0;
            }
        }
        return -1;
    }

    public void logOut(String username){

        users.get(username).set_isLoggedIn(false);
        encrypt(username);
        client.closeConnection();

    }

    public byte[] generateSecret() {
        return keyManager.generateSecret(16);
    }

    public void storeSecretKey(byte[] secretKey) {
        keyManager.setSecretKey(secretKey);
    }

    public void storePublicKey(byte[] publicKey) {
        keyManager.setPublicKey(publicKey);
    }

    public void storePrivateKey(byte[] privateKey) {
        keyManager.setPrivateKey(privateKey);
    }

    public SecretKey getKey(String algorithm){
        return keyManager.getSecretKey(algorithm);
    }
    public byte[] generateQRcode(String text, int width, int height)throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    //    BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
    }

    public boolean validateOneTimePassword(String otp, String username){
            TOTP totp = new TOTP(NUMBER_OF_DIGITS_IN_OTP, TIME_RANGE_PASSWORD);
            String validOneTimePassword = totp.generateOTP();
            if(otp.equals(validOneTimePassword)) {
                System.out.println("TOTP valid! -> " + otp + "==" + validOneTimePassword);
                users.get(username).incSessionNumber();
                byte[] privateKey = keyRequest(username);
                if(users.get(username).getSessionNumber() != 1){
                    storePrivateKey(privateKey);
                    decrypt(username);
                }
                KeyManager.getInstance().generateFileEncryptor(username); //this will be the key to encrypt the user's files
                return true;
            }
            System.out.println("TOTP invalid! -> " + otp + "!=" + validOneTimePassword);
            return false;
    }

    public String byteArrayToHexString(byte[] byteArray) {
        return keyManager.byteArrayToHexString(byteArray);
    }

    public byte[] keyRequest(String username){
        client = new BluetoothManager();
        return client.run(username);
    }

    public void addFile(String username, String filepath){
        users.get(username).add_file(filepath);
    }

    public void removeFile(String username, String filepath){
        users.get(username).remove_file(filepath);
    }

    public List<String> askFiles(String username){
        return users.get(username).get_files();
    }

    public void encrypt(String username){
        KeyManager.getInstance().encrypt((username));
    }

    public void decrypt(String username){
        KeyManager.getInstance().decrypt((username));
    }

}


