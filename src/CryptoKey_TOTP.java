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

    public CryptoKey_TOTP(JFrame frame) {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Manager.getInstance().validateCode(textField1.getText())){
                    JOptionPane.showMessageDialog(frame, "TOTP validated!");
                }
                else{
                    JOptionPane.showMessageDialog(frame, "Failed TOTP validation!");
                }
            }
        });
    }
}
