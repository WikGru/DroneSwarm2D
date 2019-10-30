package pl.edu.utp;

import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class EditConfiguration extends JDialog {
    private JPanel contentPane;
    private JButton buttonSave;
    private JButton buttonDiscard;
    private JTextPane configTextPane;

    EditConfiguration() {

        configTextPane.setText(loadConfig());
        setContentPane(contentPane);
        setModal(true);
        setTitle("Edit configuration");
        pack();
        getRootPane().setDefaultButton(buttonSave);

        buttonSave.addActionListener(e -> onSave());

        buttonDiscard.addActionListener(e -> onDiscard());

        // call onDiscard() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onDiscard();
            }
        });

        // call onDiscard() on ESCAPE
        contentPane.registerKeyboardAction(e -> onDiscard(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onSave() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("config.json"));
            writer.write(configTextPane.getText());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onDiscard() {
        dispose();
    }

    private String loadConfig(){
        try {
            return new Scanner(new File("config.json")).useDelimiter("\\Z").next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
