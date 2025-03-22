package org.bee.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bee.hms.humans.*;

public class JSONHelper {
    private static JSONHelper instance;
    private final ObjectMapper objectMapper;

    private JSONHelper() {
        objectMapper = new ObjectMapper();
        
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE);


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
        objectMapper.registerModule(module);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        
    }

    public static JSONHelper getInstance() {
        if (instance == null) {
            instance = new JSONHelper();
        }
        return instance;
    }

    public <T extends JSONWritable> void saveToJsonFile(List<T> objects, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writeValue(writer, objects);
        }
    }

    public <T extends JSONWritable> void saveToJsonFile(T object, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            objectMapper.writeValue(writer, object);
        }
    }

    public <T extends JSONReadable> List<T> loadListFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(reader, listType);
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

    public <T extends JSONReadable> T loadFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return objectMapper.readValue(reader, clazz);
        }
    }

    public <T extends JSONWritable> String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    public <T extends JSONReadable> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }
}
