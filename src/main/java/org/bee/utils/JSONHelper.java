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
import java.util.regex.Pattern;

import org.bee.hms.billing.BillableItem;
import org.bee.hms.claims.ClaimStatus;
import org.bee.hms.humans.*;
import org.bee.hms.insurance.GovernmentProvider;
import org.bee.hms.insurance.InsuranceProvider;
import org.bee.hms.insurance.PrivateProvider;
import org.bee.hms.medical.DiagnosticCode;
import org.bee.hms.medical.Medication;
import org.bee.hms.medical.MedicationBillableItem;
import org.bee.hms.medical.ProcedureCode;
import org.bee.hms.medical.VisitStatus;
import org.bee.hms.medical.WardStay;
import org.bee.hms.policy.BaseCoverage;
import org.bee.hms.policy.CompositeCoverage;
import org.bee.hms.policy.Coverage;
import org.bee.hms.policy.HeldInsurancePolicy;
import org.bee.hms.policy.InsurancePolicy;
import org.bee.hms.telemed.AppointmentStatus;
import org.bee.hms.telemed.SessionStatus;
import org.bee.hms.wards.DaySurgeryWard;
import org.bee.hms.wards.GeneralWard;
import org.bee.hms.wards.ICUWard;
import org.bee.hms.wards.LabourWard;
import org.bee.hms.wards.Ward;
import org.bee.utils.typeAdapters.*;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JSONHelper {
    private static JSONHelper instance;
    private final Gson gson;

    private JSONHelper() {
        RuntimeTypeAdapterFactory<Human> humanTypeFactory = RuntimeTypeAdapterFactory
                .of(Human.class, "type")
                .registerSubtype(Patient.class, "patient")
                .registerSubtype(Staff.class, "staff")
                .registerSubtype(Clerk.class, "clerk")
                .registerSubtype(Doctor.class, "doctor")
                .registerSubtype(Nurse.class, "nurse");


        RuntimeTypeAdapterFactory<Ward> wardTypeFactory = RuntimeTypeAdapterFactory
                .of(Ward.class, "type")
                .registerSubtype(LabourWard.class, "labour")
                .registerSubtype(ICUWard.class, "icu")
                .registerSubtype(DaySurgeryWard.class, "daySurgery")
                .registerSubtype(GeneralWard.class, "general");

        RuntimeTypeAdapterFactory<BillableItem> billableItemFactory = RuntimeTypeAdapterFactory
                .of(BillableItem.class, "type")
                .registerSubtype(MedicationBillableItem.class, "medication")
                .registerSubtype(ProcedureCode.class, "procedure")
                .registerSubtype(DiagnosticCode.class, "diagnostic")
                .registerSubtype(WardStay.class, "wardStay");


        RuntimeTypeAdapterFactory<InsuranceProvider> insuranceProviderFactory = RuntimeTypeAdapterFactory
                .of(InsuranceProvider.class, "type")
                .registerSubtype(GovernmentProvider.class, "government")
                .registerSubtype(PrivateProvider.class, "private");


        RuntimeTypeAdapterFactory<InsurancePolicy> insurancePolicyFactory = RuntimeTypeAdapterFactory
                .of(InsurancePolicy.class, "type")
                .registerSubtype(HeldInsurancePolicy.class, "held");


        RuntimeTypeAdapterFactory<Coverage> coverageFactory = RuntimeTypeAdapterFactory
                .of(Coverage.class, "type")
                .registerSubtype(BaseCoverage.class, "base")
                .registerSubtype(CompositeCoverage.class, "composite");


        Type medicationMapType = new TypeToken<Map<Medication, Integer>>() {}.getType();
        Type supportingDocumentsType = new TypeToken<Map<LocalDateTime, String>>() {}.getType();

        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
                .registerTypeAdapter(medicationMapType, new MedicationMapAdapter())
                .registerTypeAdapter(supportingDocumentsType, new SupportingDocumentsAdapter())
                .registerTypeAdapter(VisitStatus.class, new VisitStatusAdapter())
                .registerTypeAdapter(AppointmentStatus.class, new AppointmentStatusAdapter())
                .registerTypeAdapter(SessionStatus.class, new SessionStatusAdapter())
                .registerTypeAdapter(ClaimStatus.class, new ClaimStatusAdapter())
                .registerTypeAdapter(Pattern.class, new PatternTypeAdapter())
                .registerTypeAdapter(WardStay.class, new WardStayTypeAdapter())
                .registerTypeAdapterFactory(wardTypeFactory)
                .registerTypeAdapterFactory(humanTypeFactory)
                .registerTypeAdapterFactory(billableItemFactory)
                .registerTypeAdapterFactory(insuranceProviderFactory)
                .registerTypeAdapterFactory(insurancePolicyFactory)
                .registerTypeAdapterFactory(coverageFactory)
                .registerTypeAdapter(ProcedureCode.class, new ProcedureCodeAdapter())
                .registerTypeAdapter(DiagnosticCode.class, new DiagnosticCodeAdapter())
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
