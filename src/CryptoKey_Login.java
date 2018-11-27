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
                Manager.getInstance().registerUser(usernameField.getName(), new String(passwordField.getPassword()));
                frame.setContentPane(new Cryptokey_Home().getPanel());
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
