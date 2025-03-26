package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bee.hms.wards.*;

import java.io.IOException;

public class WardSerializer extends JsonSerializer<Ward> {
    @Override
    public void serialize(Ward ward, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        // Write the type information that matches the names in @JsonSubTypes
        if (ward instanceof LabourWard) {
            gen.writeStringField("type", "labour");
        } else if (ward instanceof ICUWard) {
            gen.writeStringField("type", "icu");
        } else if (ward instanceof DaySurgeryWard) {
            gen.writeStringField("type", "daySurgery");
        } else if (ward instanceof GeneralWard) {
            gen.writeStringField("type", "general");
        }

        // Write basic properties
        gen.writeStringField("name", ward.getWardName());
        gen.writeNumberField("dailyRate", ward.getDailyRate());

        // Determine the WardClassType based on ward type and daily rate
        WardClassType inferredClassType = inferWardClassType(ward);
        if (inferredClassType != null) {
            gen.writeStringField("classType", inferredClassType.name());
        }

        // Write beds information if needed
        // ... additional serialization logic

        gen.writeEndObject();
    }

    private WardClassType inferWardClassType(Ward ward) {
        // Infer WardClassType based on the concrete class and daily rate
        double rate = ward.getDailyRate();

        switch (ward) {
            case LabourWard ignored1 -> {
                if (rate == 1500) return WardClassType.LABOUR_CLASS_A;
                if (rate == 1000) return WardClassType.LABOUR_CLASS_B1;
                if (rate == 500) return WardClassType.LABOUR_CLASS_B2;
                if (rate == 250) return WardClassType.LABOUR_CLASS_C;
            }
            case ICUWard ignored -> {
                return WardClassType.ICU;
            }
            case DaySurgeryWard ignored -> {
                if (rate == 300) return WardClassType.DAYSURGERY_CLASS_SEATER;
                if (rate == 250) return WardClassType.DAYSURGERY_CLASS_COHORT;
                if (rate == 200) return WardClassType.DAYSURGERY_CLASS_SINGLE;
            }
            case GeneralWard ignored -> {
                if (rate == 500) return WardClassType.GENERAL_CLASS_A;
                if (rate == 250) return WardClassType.GENERAL_CLASS_B1;
                if (rate == 200) return WardClassType.GENERAL_CLASS_B2;
                if (rate == 150) return WardClassType.GENERAL_CLASS_C;
            }
            default -> {
            }
        }

        return null;
    }
}