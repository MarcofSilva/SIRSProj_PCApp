import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Base64;


public class CryptoKey_QRCode {
    private JTextArea pleaseScanThisQRCodeTextArea;
    private JTextField keyField;
    private JButton continueButton;
    private JPanel panelMain;
    private JLabel qrcodelabel;

    public CryptoKey_QRCode(JFrame frame) {
        SecretKey key = Manager.getInstance().generateKey();
        //try {
            /*ImageIcon imageIcon = new ImageIcon(Manager.getInstance().generateQRcode(Base64.getEncoder().encodeToString(key.getEncoded()), 200, 200));
            Image image = imageIcon.getImage(); // transform it
            System.out.println(image);
            Image newimg = image.getScaledInstance(200, 200,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            imageIcon = new ImageIcon(newimg);  // transform it back
            System.out.println(imageIcon);
            qrcodeLabel = new JLabel("QRCODE", imageIcon, JLabel.CENTER);
            System.out.println(qrcodeLabel);
            ImageIcon imageIcon = new ImageIcon("QR.png");
            JLabel label = new JLabel(imageIcon);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        keyField.setText(byteArrayToHexString(key.getEncoded()));
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CryptoKey_TOTP totpScreen = new CryptoKey_TOTP(frame);
                frame.setContentPane(totpScreen.getPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
        }

    public JLabel getQrcodeLabel() {
        return qrcodelabel;
    }

    public JPanel getPanel() {
        return panelMain;
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

