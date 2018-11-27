import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CryptoKey_Login {
    private JButton registerButton;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel panelMain;

    public JPanel getPanel() {
        return panelMain;
    }

    public CryptoKey_Login(JFrame frame) {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int login = Manager.getInstance().userCheck(usernameField.getText(), new String(passwordField.getPassword()));
                System.out.println(login);
                if(login == 1){ //logged in
                    CryptoKey_TOTP totpScreen = new CryptoKey_TOTP(frame);
                    frame.setContentPane(totpScreen.getPanel());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                }
                else if(login == 0){ //registered & logged in
                    CryptoKey_QRCode qrcodeScreen = new CryptoKey_QRCode(frame);
                    frame.setContentPane(qrcodeScreen.getPanel());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                }
                else{
                    System.out.println("Erro a registar utilizador");
                }


            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CryptoKey");
        frame.setContentPane(new CryptoKey_Login(frame).panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
