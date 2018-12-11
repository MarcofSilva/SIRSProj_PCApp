import Main.Manager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CryptoKey_TOTP {
    private JTextField textField1;
    private JPanel panelMain;
    private JTextArea insertCodeTextArea;
    private JButton submitButton;

    public JPanel getPanel() {
        return panelMain;
    }

    public CryptoKey_TOTP(JFrame frame, String username) {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Manager.getInstance().validateOneTimePassword(textField1.getText(), username)){
                    JOptionPane.showMessageDialog(frame, "TOTP validated!");
                    CryptoKey_Home homeScreen = new CryptoKey_Home(frame, username);
                    frame.setContentPane(homeScreen.getPanel());
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);

                }
                else{
                    JOptionPane.showMessageDialog(frame, "Failed TOTP validation!");
                }
            }
        });
    }
}
