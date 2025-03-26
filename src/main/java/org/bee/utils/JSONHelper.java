package org.bee.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Utility class providing JSON serialization and deserialization operations.
 * Handles conversion between JSON and Java objects using Jackson.
 */
public final class JSONHelper {
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private JSONHelper() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Initializes and configures the ObjectMapper with required settings.
     *
     * @return A configured ObjectMapper instance
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Pattern.class, new JsonSerializer<>() {
            @Override
            public void serialize(Pattern value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                if (value == null) {
                    gen.writeNull();
                    return;
                }
                gen.writeString(value.pattern());
            }
        });
        module.addDeserializer(Pattern.class, new JsonDeserializer<>() {
            @Override
            public Pattern deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.currentToken() == JsonToken.VALUE_NULL) {
                    return null;
                }
                return Pattern.compile(p.getText());
            }
        });
        mapper.registerModule(module);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);

        return mapper;
    }

    /**
     * Saves a list of objects implementing JSONWritable to a JSON file.
     *
     * @param objects  The list of objects to save
     * @param filePath The path of the JSON file
     * @param <T>      The type of the objects, which must implement JSONWritable
     * @throws IOException If there is an error writing to the file
     */
    public static <T extends JSONSerializable> void saveToJsonFile(List<T> objects, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            OBJECT_MAPPER.writeValue(writer, objects);
        }
    }

    /**
     * Saves an object implementing JSONWritable to a JSON file.
     *
     * @param object   The object to save
     * @param filePath The path of the JSON file
     * @param <T>      The type of the object, which must implement JSONWritable
     * @throws IOException If there is an error writing to the file
     */
    public static <T extends JSONSerializable> void saveToJsonFile(T object, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            OBJECT_MAPPER.writeValue(writer, object);
        }
    }

    /**
     * Loads a list of objects implementing JSONReadable from a JSON file.
     *
     * @param filePath The path of the JSON file
     * @param clazz    The class of the objects to load
     * @param <T>      The type of the objects, which must implement JSONReadable
     * @return A list of objects of type T
     * @throws IOException If there is an error reading from the file
     */
    public static <T extends JSONSerializable> List<T> loadListFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JavaType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return OBJECT_MAPPER.readValue(reader, listType);
        } catch (InvalidTypeIdException e) {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                StringBuilder sb = new StringBuilder("JSON content starts with: ");
                for (int i = 0; i < 5 && br.ready(); i++) {
                    sb.append(br.readLine()).append("\n");
                }
                System.err.println(sb.toString());
            }
            throw e;
        }
    }

    /**
     * Loads an object implementing JSONReadable from a JSON file.
     *
     * @param filePath The path of the JSON file
     * @param clazz    The class of the object to load
     * @param <T>      The type of the object, which must implement JSONReadable
     * @return An object of type T
     * @throws IOException If there is an error reading from the file
     */
    public static <T extends JSONSerializable> T loadFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return OBJECT_MAPPER.readValue(reader, clazz);
        }
    }

    /**
     * Converts an object implementing JSONWritable to a JSON string.
     *
     * @param object The object to convert
     * @param <T>    The type of the object, which must implement JSONWritable
     * @return A JSON string representation of the object
     * @throws RuntimeException If there is an error converting the object to JSON
     */
    public static <T extends JSONSerializable> String toJson(T object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object implementing JSONReadable.
     *
     * @param json  The JSON string to convert
     * @param clazz The class of the object to create
     * @param <T>   The type of the object, which must implement JSONReadable
     * @return An object of type T
     * @throws RuntimeException If there is an error converting the JSON to an object
     */
    public static <T extends JSONSerializable> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
}