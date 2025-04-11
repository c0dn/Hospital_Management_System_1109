package org.bee.utils;

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

public final class CSVHelper {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private CSVHelper() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Saves an object implementing {@link CsvSerializable} to a CSV file.
     *
     * @param object   The object to save
     * @param fileName The name of the CSV file
     * @param <T>      The type of the object, which must implement {@link CsvSerializable}
     */
    public static <T extends CsvSerializable<T>> void saveObject(T object, String fileName) {
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
    public static List<String[]> readCSV(String fileName) {
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
    private static void writeToFile(String fileName, String content) {
        try (FileWriter fw = new FileWriter(fileName, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(content);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}