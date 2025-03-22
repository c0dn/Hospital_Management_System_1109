package org.bee.utils.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bee.hms.medical.DiagnosticCode;

import java.io.IOException;

public class DiagnosticCodeAdapter extends TypeAdapter<DiagnosticCode> {

    @Override
    public void write(JsonWriter out, DiagnosticCode value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getDiagnosisCode());
    }

    @Override
    public DiagnosticCode read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (in.peek() == JsonToken.STRING) {
            String code = in.nextString();
            return DiagnosticCode.createFromCode(code);
        }

        throw new IOException("Expected STRING or OBJECT for DiagnosticCode, but was " + in.peek());
    }
}