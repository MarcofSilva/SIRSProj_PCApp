import Main.Manager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CryptoKey_QRCode {
    private JTextArea pleaseScanThisQRCodeTextArea;
    private JTextField keyField;
    private JButton continueButton;
    private JPanel panelMain;
    private JLabel qrcodelabel;

    private Manager manager;

    public CryptoKey_QRCode(JFrame frame, String username) {
        manager = Manager.getInstance();

        byte[] key = manager.generateSecret();
        manager.storeSecretKey(key);

        ImageIcon qrcodeIcon;
        try {
            qrcodeIcon = new ImageIcon(manager.generateQRcode(manager.byteArrayToHexString(key), 400, 400));
            qrcodelabel.setIcon(qrcodeIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        keyField.setText(manager.byteArrayToHexString(key));
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CryptoKey_TOTP totpScreen = new CryptoKey_TOTP(frame, username);
                frame.setContentPane(totpScreen.getPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public JPanel getPanel() {
        return panelMain;
    }
}

