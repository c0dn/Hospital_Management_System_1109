package org.bee.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bee.hms.humans.Doctor;
import org.bee.hms.humans.Human;
import org.bee.hms.humans.Nurse;
import org.bee.hms.humans.Patient;
import org.bee.hms.humans.Staff;
import org.bee.hms.medical.Medication;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.wards.*;
import org.bee.utils.typeAdapters.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONHelper {
    private static JSONHelper instance;
    private final Gson gson;

    public JSONHelper() {
        RuntimeTypeAdapterFactory<Human> humanTypeFactory = RuntimeTypeAdapterFactory
                .of(Human.class, "type")
                .registerSubtype(Patient.class, "patient")
                .registerSubtype(Staff.class, "staff")
                .registerSubtype(Doctor.class, "doctor")
                .registerSubtype(Nurse.class, "nurse");


        RuntimeTypeAdapterFactory<Ward> wardTypeFactory = RuntimeTypeAdapterFactory
                .of(Ward.class, "type")
                .registerSubtype(LabourWard.class, "labour")
                .registerSubtype(ICUWard.class, "icu")
                .registerSubtype(DaySurgeryWard.class, "daySurgery")
                .registerSubtype(GeneralWard.class, "general");


        Type medicationMapType = new TypeToken<Map<Medication, Integer>>(){}.getType();

        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
                .registerTypeAdapter(medicationMapType, new MedicationMapAdapter())
                .registerTypeAdapter(VisitStatus.class, new VisitStatusAdapter())
                .registerTypeAdapterFactory(wardTypeFactory)
                .registerTypeAdapterFactory(humanTypeFactory)
                .setPrettyPrinting()
                .create();
    }

    public static JSONHelper getInstance() {
        if (instance == null) {
            instance = new JSONHelper();
        }
        return instance;
    }

    public <T extends JSONWritable> void saveToJsonFile(List<T> objects, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(objects, writer);
        }
    }

    public <T extends JSONWritable> void saveToJsonFile(T object, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
        }
    }

    public <T extends JSONReadable> List<T> loadListFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();
            List<T> objects = gson.fromJson(reader, listType);
            return objects != null ? objects : new ArrayList<>();
        }
    }

    public <T extends JSONReadable> T loadFromJsonFile(String filePath, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, clazz);
        }
    }

    public <T extends JSONWritable> String toJson(T object) {
        return gson.toJson(object);
    }

    public <T extends JSONReadable> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public <T extends JSONReadable> List<T> fromJsonArray(String json, Class<T> clazz) {
        Type listType = TypeToken.getParameterized(ArrayList.class, clazz).getType();
        return gson.fromJson(json, listType);
    }
}
