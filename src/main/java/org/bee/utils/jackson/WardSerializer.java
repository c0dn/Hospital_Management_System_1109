package org.bee.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bee.hms.wards.*;

import java.io.IOException;
/**
 * Custom Jackson serializer for converting {@link Ward} objects into JSON format.
 *
 * <p>This serializer handles different types of {@link Ward} subclasses, such as {@link LabourWard},
 * {@link ICUWard}, {@link DaySurgeryWard}, and {@link GeneralWard}. It outputs type-specific information
 * along with common properties like name, daily rate, and inferred ward class type.</p>
 *
 */
public class WardSerializer extends JsonSerializer<Ward> {
    /**
     * Serializes a {@link Ward} object into JSON format.
     *
     * @param ward       The {@link Ward} object to be serialized.
     * @param gen        The {@link JsonGenerator} used to write the JSON output.
     * @param serializers The {@link SerializerProvider} that can be used to get serializers for serializing objects.
     * @throws IOException If an error occurs during serialization.
     */
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
    /**
     * Infers the {@link WardClassType} based on the concrete subclass of {@link Ward} and its daily rate.
     *
     * @param ward The {@link Ward} object for which the class type is inferred.
     * @return The inferred {@link WardClassType}, or {@code null} if no matching type is found.
     */
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