package pl.edu.utp.util;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private static Logger LOGGER;
    private FileHandler fileHandler;

    private MyLogger() {
        try {
            LOGGER = Logger.getLogger(MyLogger.class.getName());
            String configInput = "config.json";
            String myJson = new Scanner(new File(configInput)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            String logDir = obj.getString("logDir");
            logDir += "/log_" + String.valueOf(new Timestamp(System.currentTimeMillis())).replaceAll("[:;.]", "").replace(' ', '_') + ".txt";
            try {
                fileHandler = new FileHandler(logDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SimpleFormatter formatter = new SimpleFormatter();
            assert fileHandler != null;
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Logger getLogger(){
        if(LOGGER == null){
            new MyLogger();
        }
        return LOGGER;
    }

    public static void log(Level level, String msg) {
        getLogger().log(level, msg);
    }
}
