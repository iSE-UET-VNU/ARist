package utils.logging;

import config.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    static FileWriter fw;

    public static void write(String line) {
        write(line, "log.txt");
    }

    public static void error(String line) {
        System.err.println("ERROR: "+ line);
        write(line, "error.txt");
    }
    public static void warning(String line) {
        System.err.println("WARNING: "+ line);
        write(line, "warning.txt");
    }

    public static void write(String line, String filename) {
        File output = new File(Config.LOG_DIR + filename);
        try {
            FileWriter fileWriter = new FileWriter(output, true);
            fileWriter.append(line + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initDebug(String debugName){
        try {
            fw = new FileWriter(Config.LOG_DIR + debugName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void logDebug(Object obj){
        try {
            fw.append(obj +"\r\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeDebug(){
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the given object to console.
     */
    public static void log(Object obj){
        System.out.println(obj);
    }
}
