import Main.Manager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CryptoKey_Home {
    private JButton logOutButton;
    private JPanel panelMain;
    private JButton chooseFilesButton;
    private JList filesList;
    private JButton removeButton;
    private DefaultListModel<String> listModel;
    private JFrame _frame;
    // private JFileChooser fileChooser;

    public CryptoKey_Home(JFrame frame, String username) {
        _frame = frame;
        panelMain = new JPanel();
        listModel = new DefaultListModel<>();
        List<String> userFiles = Manager.getInstance().askFiles(username);
        for (String file : userFiles) {
            listModel.addElement(file);
        }
        filesList = new JList(listModel);
        filesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        filesList.setLayoutOrientation(JList.VERTICAL);
        filesList.setVisible(true);
        panelMain.add(filesList);

        logOutButton = new JButton("Log out");
        chooseFilesButton = new JButton("Choose File");
        removeButton = new JButton("Remove");
        panelMain.add(removeButton);
        panelMain.add(chooseFilesButton);
        panelMain.add(logOutButton);
        panelMain.add(new JScrollPane(filesList));
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
                            fc.getSelectedFile().getAbsolutePath());
                    if(!listModel.contains(fc.getSelectedFile().getAbsolutePath())) {
                        listModel.addElement(fc.getSelectedFile().getAbsolutePath());
                        Manager.getInstance().addFile(username, fc.getSelectedFile().getAbsolutePath()); //Might be handier to store filepath for encription
                    }
                }

            }
        });
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = filesList.getSelectedIndex();
                Manager.getInstance().removeFile(username, listModel.get(index));
                listModel.remove(index);

                int size = listModel.getSize();

                if (size == 0) { //Nobody's left, disable firing.
                    removeButton.setEnabled(false);

                } else { //Select an index.
                    if (index == listModel.getSize()) {
                        //removed item in last position
                        index--;
                    }

                    filesList.setSelectedIndex(index);
                    filesList.ensureIndexIsVisible(index);
                }
            }
        });
    }

    public JPanel getPanel() {
        return panelMain;
    }
}
