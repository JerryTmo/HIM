package com.example.service;

import com.example.config.ApiClientConfig;
import com.example.exception.ApiServiceException;
import com.example.model.*;
import com.example.util.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/**
 * 医疗管理API服务 - 直接调用后端REST API
 */
public class MedicalApiService {

    private static volatile MedicalApiService instance;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    private MedicalApiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.baseUrl = ApiClientConfig.getBaseUrl();
    }

    public static MedicalApiService getInstance() {
        if (instance == null) {
            synchronized (MedicalApiService.class) {
                if (instance == null) {
                    instance = new MedicalApiService();
                }
            }
        }
        return instance;
    }

    // ==================== 患者管理 ====================

    public void getAllPatients(Consumer<List<Patient>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/patients", null,
                new TypeReference<ServiceResult<List<Patient>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createPatient(Patient patient, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/patients", patient,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updatePatient(Patient patient, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/patients", patient,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deletePatient(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/patients/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 医生管理 ====================

    public void getAllDoctors(Consumer<List<Doctor>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/doctors", null,
                new TypeReference<ServiceResult<List<Doctor>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createDoctor(Doctor doctor, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/doctors", doctor,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updateDoctor(Doctor doctor, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/doctors", doctor,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deleteDoctor(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/doctors/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 预约管理 ====================

    public void getAllAppointments(Consumer<List<Appointment>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/appointments", null,
                new TypeReference<ServiceResult<List<Appointment>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getAppointmentsByPatient(String patientId, Consumer<List<Appointment>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/appointments/patient/" + patientId, null,
                new TypeReference<ServiceResult<List<Appointment>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getAppointmentsByDoctor(String doctorId, Consumer<List<Appointment>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/appointments/doctor/" + doctorId, null,
                new TypeReference<ServiceResult<List<Appointment>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createAppointment(Appointment appointment, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/appointments", appointment,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updateAppointment(Appointment appointment, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/appointments", appointment,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deleteAppointment(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/appointments/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 病历管理 ====================

    public void getAllMedicalRecords(Consumer<List<MedicalRecord>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/medical-records", null,
                new TypeReference<ServiceResult<List<MedicalRecord>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void getMedicalRecordsByPatient(String patientId, Consumer<List<MedicalRecord>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/medical-records/patient/" + patientId, null,
                new TypeReference<ServiceResult<List<MedicalRecord>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createMedicalRecord(MedicalRecord record, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/medical-records", record,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updateMedicalRecord(MedicalRecord record, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/medical-records", record,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deleteMedicalRecord(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/medical-records/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 药品管理 ====================

    public void getAllMedicines(Consumer<List<Medicine>> onSuccess, Consumer<Throwable> onError) {
        callAsync("GET", "/api/medicines", null,
                new TypeReference<ServiceResult<List<Medicine>>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void createMedicine(Medicine medicine, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        callAsync("POST", "/api/medicines", medicine,
                new TypeReference<ServiceResult<String>>() {},
                result -> onSuccess.accept(result.getData()),
                onError);
    }

    public void updateMedicine(Medicine medicine, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("PUT", "/api/medicines", medicine,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    public void deleteMedicine(String id, Consumer<Void> onSuccess, Consumer<Throwable> onError) {
        callAsync("DELETE", "/api/medicines/" + id, null,
                new TypeReference<ServiceResult<Void>>() {},
                result -> onSuccess.accept(null),
                onError);
    }

    // ==================== 私有方法 ====================

    private <T> void callAsync(String method, String path, Object body,
                               TypeReference<ServiceResult<T>> responseType,
                               Consumer<ServiceResult<T>> onSuccess,
                               Consumer<Throwable> onError) {
        Task<ServiceResult<T>> task = new Task<>() {
            @Override
            protected ServiceResult<T> call() throws Exception {
                return callSync(method, path, body, responseType);
            }
        };

        task.setOnSucceeded(e -> {
            ServiceResult<T> result = task.getValue();
            if (result.isSuccess()) {
                Platform.runLater(() -> onSuccess.accept(result));
            } else {
                Platform.runLater(() -> onError.accept(new ApiServiceException(result.getMessage())));
            }
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            Platform.runLater(() -> onError.accept(error));
        });

        new Thread(task).start();
    }

    private <T> ServiceResult<T> callSync(String method, String path, Object body,
                                          TypeReference<ServiceResult<T>> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");

        // 添加Authorization头
        String token = UserSession.getAuthToken();
        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        if ("GET".equalsIgnoreCase(method)) {
            requestBuilder.GET();
        } else if ("POST".equalsIgnoreCase(method)) {
            String jsonBody = objectMapper.writeValueAsString(body);
            requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else if ("PUT".equalsIgnoreCase(method)) {
            String jsonBody = objectMapper.writeValueAsString(body);
            requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
        } else if ("DELETE".equalsIgnoreCase(method)) {
            requestBuilder.DELETE();
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), responseType);
        } else if (response.statusCode() == 401 || response.statusCode() == 403) {
            throw new ApiServiceException("权限不足或未登录: " + response.statusCode());
        } else {
            throw new ApiServiceException("HTTP Error: " + response.statusCode());
        }
    }

    // ==================== 内部类 - 通用响应包装 ====================

    public static class ServiceResult<T> {
        private boolean success;
        private String message;
        private T data;
        private Integer code;
        private Long timestamp;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public Integer getCode() { return code; }
        public void setCode(Integer code) { this.code = code; }
        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }
}
