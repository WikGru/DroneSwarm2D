package pl.edu.utp.util;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        try (Scanner myScanner = new Scanner(new File("config.json"))) {
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "[%1$tF %1$tH:%1$tM:%1$tS.%1$tL] [%4$-7s] %5$s %n");

            LOGGER = Logger.getLogger(MyLogger.class.getName());
            String myJson = myScanner.useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            String logDir = "";
            try {
                logDir = obj.getString("logDir");
            } catch (Exception e) {
                logDir = System.getProperty("user.home");
                LOGGER.log(Level.WARNING, e.getMessage());
            }
            if (Files.notExists(Paths.get(logDir))) {
                logDir = System.getProperty("user.home");
                LOGGER.log(Level.WARNING, "logDir does not exist");
            }
            logDir += "/log_" + String.valueOf(new Timestamp(System.currentTimeMillis())).replaceAll("[:;.]", "").replace(' ', '_') + ".txt";
            try {
                fileHandler = new FileHandler(logDir);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, e.getMessage());
            }
            SimpleFormatter formatter = new SimpleFormatter();
            assert fileHandler != null;
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private static Logger getLogger() {
        if (LOGGER == null) {
            new MyLogger();
        }
        return LOGGER;
    }

    public static void log(Level level, String msg) {
        getLogger().log(level, msg);
    }
}
