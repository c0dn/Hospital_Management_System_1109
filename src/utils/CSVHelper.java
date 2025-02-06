package utils;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class CSVHelper {
    private static volatile CSVHelper instance;

    private CSVHelper() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the singleton instance");
        }
    }

    public static CSVHelper getInstance() {
        // Double-checked locking
        if (instance == null) {
            synchronized (CSVHelper.class) {
                if (instance == null) {
                    instance = new CSVHelper();
                }
            }
        }
        return instance;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton CSVHelper is not allowed");
    }

    public <T extends CsvSerializable<T>> void saveObject(T object, String fileName) {
        String csvLine = object.toCsvFormat() + "\n";
        writeToFile(fileName, csvLine);
    }

    public List<String[]> readCSV(String fileName) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return records;
    }

    private void writeToFile(String fileName, String content) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
