package utils;

public interface CsvSerializable<T> {
    String toCsvFormat();

    T fromCsvFormat(String[] data);
}
