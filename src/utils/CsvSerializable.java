package utils;

/**
 * An interface defining the contract for objects that can be serialized to CSV format.
 * <p>
 * Any class implementing this interface must provide:
 * <ul>
 *     <li>A method to convert object data to a CSV-formatted string.</li>
 *     <li>A method to parse CSV data back into an object of the same type.</li>
 * </ul>
 * </p>
 * <br><br>This ensures consistency when saving and loading system data.
 * @param <T> The type of object that can be serialized and deserialized.
 */

public interface CsvSerializable<T> {
    /**
     * Converts the object to a CSV-formatted string.
     *
     * @return A string representation of the object in CSV format.
     */
    String toCsvFormat();

    /**
     * Parses a CSV-formatted string array and returns an object of type {@code T}.
     *
     * @param data A string array representing the CSV data.
     * @return An instance of {@code T} populated with the parsed data.
     */
    T fromCsvFormat(String[] data);
}
