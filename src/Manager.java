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
import java.util.Base64;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Manager {

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap();

    private static class SingletonHolder {
        private static final Manager instance = new Manager();
    }

    public Manager(){}

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

    public SecretKey generateKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey key = keyGen.generateKey();

            return key;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public byte[] generateQRcode(String text, int width, int height)throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();
        return pngData;
        }

        public boolean validateCode(String code){
        //Insert TOTP validation here
            System.out.println("TOTP validated!");
            return true;
        }
}


