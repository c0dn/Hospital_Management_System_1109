package org.bee.hms.utils;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides utility functions for handling CSV file operations.
 * <br><br>This class offers methods for:
 * <br>- Reading data from CSV files
 * <br>- Writing data to CSV files
 * <br>- Saving objects that implement {@link CsvSerializable}
 *
 * <br><br>This helps in exporting and importing medical records and billing details.
 */

public class CSVHelper {
    /** Singleton instance of {@code CSVHelper}. */
    private static volatile CSVHelper instance;

    /**
     * Private constructor to prevent instantiation.
     * Uses the Singleton pattern to ensure a single instance of the class.
     *
     * @throws RuntimeException if an instance already exists.
     */
    private CSVHelper() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the singleton instance");
        }
    }

    /**
     * Returns the singleton instance of {@code CSVHelper}.
     * Uses double-checked locking for thread-safe initialization.
     *
     * @return The singleton instance of {@code CSVHelper}
     */
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

    /**
     * Prevents cloning of the singleton instance.
     *
     * @throws CloneNotSupportedException Always thrown to prevent cloning
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning of singleton CSVHelper is not allowed");
    }

    /**
     * Saves an object implementing {@link CsvSerializable} to a CSV file.
     *
     * @param object   The object to save
     * @param fileName The name of the CSV file
     * @param <T>      The type of the object, which must implement {@link CsvSerializable}
     */
    public <T extends CsvSerializable<T>> void saveObject(T object, String fileName) {
        String csvLine = object.toCsvFormat() + "\n";
        writeToFile(fileName, csvLine);
    }

    /**
     * Reads data from a CSV file and returns a list of string arrays,
     * where each array represents a row in the CSV.
     *
     * @param fileName The name of the CSV file to read
     * @return A list of string arrays representing rows in the file
     */
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

    /**
     * Writes content to a CSV file, appending data if the file exists.
     *
     * @param fileName The name of the file to write to
     * @param content  The content to write
     */
    private void writeToFile(String fileName, String content) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
