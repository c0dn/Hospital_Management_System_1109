package org.bee.utils.typeAdapters;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * TypeAdapter for serializing and deserializing Map<LocalDateTime, String> used for supporting documents.
 */
public class SupportingDocumentsAdapter extends TypeAdapter<Map<LocalDateTime, String>> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, Map<LocalDateTime, String> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (Map.Entry<LocalDateTime, String> entry : value.entrySet()) {
            out.beginObject();
            out.name("timestamp");
            out.value(entry.getKey().format(FORMATTER));
            out.name("document");
            out.value(entry.getValue());
            out.endObject();
        }
        out.endArray();
    }

    @Override
    public Map<LocalDateTime, String> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return new HashMap<>();
        }

        Map<LocalDateTime, String> result = new HashMap<>();
        in.beginArray();
        while (in.hasNext()) {
            in.beginObject();
            
            LocalDateTime timestamp = null;
            String document = null;
            
            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals("timestamp")) {
                    String timestampStr = in.nextString();
                    timestamp = LocalDateTime.parse(timestampStr, FORMATTER);
                } else if (name.equals("document")) {
                    document = in.nextString();
                } else {
                    in.skipValue();
                }
            }
            
            if (timestamp != null && document != null) {
                result.put(timestamp, document);
            }
            
            in.endObject();
        }
        in.endArray();
        
        return result;
    }
}
