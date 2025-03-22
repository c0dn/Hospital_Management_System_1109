package org.bee.utils.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bee.hms.medical.DiagnosticCode;

import java.io.IOException;
import java.math.BigDecimal;

public class DiagnosticCodeAdapter extends TypeAdapter<DiagnosticCode> {

    @Override
    public void write(JsonWriter out, DiagnosticCode value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("fullCode").value(value.getDiagnosisCode());
        out.name("abbreviatedDescription").value(value.getBillItemDescription());
        out.name("cost").value(value.getCharges().toString());
        out.endObject();
    }

    @Override
    public DiagnosticCode read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String categoryCode = null;
        String diagnosisCode = null;
        String fullCode = null;
        String abbreviatedDescription = null;
        String fullDescription = null;
        String categoryTitle = null;
        BigDecimal cost = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "categoryCode":
                    categoryCode = in.nextString();
                    break;
                case "diagnosisCode":
                    diagnosisCode = in.nextString();
                    break;
                case "fullCode":
                    fullCode = in.nextString();
                    break;
                case "abbreviatedDescription":
                    abbreviatedDescription = in.nextString();
                    break;
                case "fullDescription":
                    fullDescription = in.nextString();
                    break;
                case "categoryTitle":
                    categoryTitle = in.nextString();
                    break;
                case "cost":
                    cost = new BigDecimal(in.nextString());
                    break;
            }
        }
        in.endObject();

        return new DiagnosticCode(categoryCode, diagnosisCode, fullCode, abbreviatedDescription, fullDescription, categoryTitle);
    }

}
