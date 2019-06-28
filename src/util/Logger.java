package util;

import javax.swing.*;
import java.io.PrintWriter;

public class Logger {

    private JTextPane logDisp;
    private PrintWriter writer;

    public Logger(JTextPane tf){
        this.logDisp = tf;
       // writer = new PrintWriter("log.txt", "UTF-8");
    }

    public void writeToLogFile(String s){
        //TODO: Log to file here
    }

    public void closeLogFile(){
        writer.close();
    }

    public void writeToLogDisplay(String s) {
        logDisp.setText(logDisp.getText() + s + '\n');
    }

    public void clearLogDisplay() {
        logDisp.setText("");
    }
}
