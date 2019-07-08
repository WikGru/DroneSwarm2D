package util;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {

    private JTextPane logDisp;
    private PrintWriter writer;

    public enum _msgType{
        ERR,
        WAR,
        MSG,
        NONE
    }


    public Logger(JTextPane tf){
        this.logDisp = tf;
       // writer = new PrintWriter("log.txt", "UTF-8");
    }

    public void writeToLogFile(_msgType type, String s){
        String msg = "";
        if(type == _msgType.ERR) msg = "[ERR]\t";
        else if(type == _msgType.WAR) msg = "[WAR]\t";
        else if(type == _msgType.MSG) msg = "[MSG]\t";
        else msg = "";

        msg += s;

        try(FileWriter fw = new FileWriter("log.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw))
        {
            out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
