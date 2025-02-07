package utils;

/**
 * An interface defining the contract for objects that can be serialized to CSV format.
 * <br><br>Any class implementing this interface must provide:
 * <br>- A method to convert object data to a CSV string
 * <br>- A method to parse CSV data back into an object
 *
 * <br><br>This ensures consistency when saving and loading system data.
 * @param <T> The type of object that can be serialized and deserialized.
 */

public interface CsvSerializable<T> {
    String toCsvFormat();

    T fromCsvFormat(String[] data);
}
