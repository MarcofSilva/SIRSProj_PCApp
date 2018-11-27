import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;

public class Cryptokey_Home {
    private JTextPane pleaseInsertThisCodeTextPane;
    private JPanel panelMain;
    private JTextField keyField;
    private JLabel qrcodeLabel;

    public Cryptokey_Home(){
        SecretKey key = Manager.getInstance().generateKey();
        try {
            ImageIcon imageIcon = new ImageIcon(Manager.getInstance().generateQRcode(Base64.getEncoder().encodeToString(key.getEncoded()), 200, 200));
            Image image = imageIcon.getImage(); // transform it
            Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            imageIcon = new ImageIcon(newimg);  // transform it back
            qrcodeLabel = new JLabel(imageIcon);
            keyField.setText(Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public JPanel getPanel(){
        return panelMain;
    }
}
