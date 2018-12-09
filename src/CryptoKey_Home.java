import Main.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CryptoKey_Home {
    private JButton logOutButton;
    private JPanel panelMain;
    private JButton chooseFilesButton;
    private JList filesList;
    private JButton addButton;
    private DefaultListModel<String> listModel;
    private JFrame _frame;
    // private JFileChooser fileChooser;

    public CryptoKey_Home(JFrame frame, String username) {
        _frame = frame;
        //panelMain = getPanel();
        /*logOutButton = new JButton("Log out");
        chooseFilesButton = new JButton("Choose File");
        addButton = new JButton("Add");
        panelMain.add(addButton);
        panelMain.add(chooseFilesButton);
        panelMain.add(logOutButton);*/
        listModel = new DefaultListModel<>();
        listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");
        filesList = new JList(listModel); //data has type Object[]
        filesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        filesList.setLayoutOrientation(JList.VERTICAL);
        filesList.setVisible(true);
        //panelMain.add(filesList); // AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
        frame.pack();
        frame.setVisible(true);

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Manager.getInstance().logOut(username);
                CryptoKey_Login loginScreen = new CryptoKey_Login(frame);
                frame.setContentPane(loginScreen.getPanel());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });


        chooseFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showDialog(frame, "Select");
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    System.out.println("You chose to open this file: " +
                            fc.getSelectedFile().getName());
                }

            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public JPanel getPanel() {
        return panelMain;
    }
}
