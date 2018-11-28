import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Manager {

    //These two must be in sync with the ones in the android app
    private static final int NUMBER_OF_DIGITS_IN_OTP = 6; //n belongs to [0, 9] (One time password size)
    private static final int TIME_RANGE_PASSWORD = 15; //For how long is a one time password valid until a new gets

    private KeyManager keyManager;
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap();

    private static class SingletonHolder {
        private static final Manager instance = new Manager();
    }

    private Manager(){
        keyManager = KeyManager.getInstance();
    }

    public static synchronized Manager getInstance() {
        return SingletonHolder.instance;
    }

    public int userCheck(String username, String password){
        if (username != null && password != null) {
            if (users.contains(username)){
                users.get(username).set_isLoggedIn(true);
                return 1;
            }
            else {
                User user = new User(username, password);
                users.put(username, user);
                users.get(username).set_isLoggedIn(true);
                return 0;
            }
        }
        return -1;
    }

    public byte[] generateSecret() {
        return keyManager.generateSecret();
    }

    public void storeSecretKey(byte[] secretKey) {
        keyManager.setSecretKey(secretKey);
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

    public boolean validateOneTimePassword(String otp){
            TOTP totp = new TOTP(NUMBER_OF_DIGITS_IN_OTP, TIME_RANGE_PASSWORD);
            String validOneTimePassword = totp.generateOTP();
            if(otp.equals(validOneTimePassword)) {
                System.out.println("TOTP valid! -> " + otp + "==" + validOneTimePassword);
                return true;
            }
            System.out.println("TOTP invalid! -> " + otp + "!=" + validOneTimePassword);
            return false;
    }

    public String byteArrayToHexString(byte[] byteArray) {
        return keyManager.byteArrayToHexString(byteArray);
    }
}


