package org.bee.utils.typeAdapters;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatternTypeAdapter extends TypeAdapter<Pattern> {
    @Override
    public void write(JsonWriter out, Pattern pattern) throws IOException {
        if (pattern == null) {
            out.nullValue();
        } else {
            out.value(pattern.pattern());
        }
    }

    @Override
    public Pattern read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        return Pattern.compile(in.nextString());
    }
}