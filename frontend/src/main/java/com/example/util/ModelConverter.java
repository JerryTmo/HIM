package com.example.util;

import com.example.model.*;
import io.swagger.client.model.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DTO 转换工具 - Swagger 生成的 Response/Request DTO 与前端业务 Model 互转
 * <p>
 * 统一使用 ApiService + Swagger DefaultApi 后，所有后端数据以 Swagger DTO 形式到达，
 * 通过此工具类转换为前端 UI 层使用的业务 Model。
 */
public class ModelConverter {

    private ModelConverter() {
    }

    // ==================== Patient 转换 ====================

    public static Patient toPatient(PatientInfoResponse dto) {
        if (dto == null) return null;
        Patient p = new Patient();
        p.setId(dto.getId());
        p.setName(dto.getName());
        p.setGender(dto.getGender());
        p.setAge(dto.getAge() != null ? dto.getAge() : 0);
        p.setPhone(dto.getPhone());
        p.setIdCard(dto.getIdCard());
        p.setAddress(dto.getAddress());
        p.setEmergencyContact(dto.getEmergencyContact());
        p.setEmergencyPhone(dto.getEmergencyPhone());
        p.setRegistrationDate(dto.getRegistrationDate());
        p.setMedicalHistory(dto.getMedicalHistory());
        p.setStatus(dto.getStatus());
        return p;
    }

    public static List<Patient> toPatientList(List<PatientInfoResponse> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(ModelConverter::toPatient)
                .collect(Collectors.toList());
    }

    public static CreatePatientRequest toCreateRequest(Patient p) {
        if (p == null) return null;
        CreatePatientRequest req = new CreatePatientRequest();
        req.setName(p.getName());
        req.setGender(p.getGender());
        req.setAge(p.getAge());
        req.setPhone(p.getPhone());
        req.setIdCard(p.getIdCard());
        req.setAddress(p.getAddress());
        req.setEmergencyContact(p.getEmergencyContact());
        req.setEmergencyPhone(p.getEmergencyPhone());
        req.setRegistrationDate(p.getRegistrationDate());
        req.setMedicalHistory(p.getMedicalHistory());
        return req;
    }

    public static UpdatePatientRequest toUpdateRequest(Patient p) {
        if (p == null) return null;
        UpdatePatientRequest req = new UpdatePatientRequest();
        req.setId(p.getId());
        req.setName(p.getName());
        req.setGender(p.getGender());
        req.setAge(p.getAge());
        req.setPhone(p.getPhone());
        req.setIdCard(p.getIdCard());
        req.setAddress(p.getAddress());
        req.setEmergencyContact(p.getEmergencyContact());
        req.setEmergencyPhone(p.getEmergencyPhone());
        req.setRegistrationDate(p.getRegistrationDate());
        req.setMedicalHistory(p.getMedicalHistory());
        return req;
    }

    // ==================== Doctor 转换 ====================

    public static Doctor toDoctor(DoctorInfoResponse dto) {
        if (dto == null) return null;
        Doctor d = new Doctor();
        d.setId(dto.getId());
        d.setName(dto.getName());
        d.setDepartment(dto.getDepartment());
        d.setTitle(dto.getTitle());
        d.setPhone(dto.getPhone());
        d.setSpecialty(dto.getSpecialty());
        d.setDescription(dto.getDescription());
        d.setAvailableTimes(dto.getAvailableTimes());
        d.setStatus(dto.getStatus());
        return d;
    }

    public static List<Doctor> toDoctorList(List<DoctorInfoResponse> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(ModelConverter::toDoctor)
                .collect(Collectors.toList());
    }

    public static CreateDoctorRequest toCreateRequest(Doctor d) {
        if (d == null) return null;
        CreateDoctorRequest req = new CreateDoctorRequest();
        req.setName(d.getName());
        req.setDepartment(d.getDepartment());
        req.setTitle(d.getTitle());
        req.setPhone(d.getPhone());
        req.setSpecialty(d.getSpecialty());
        req.setDescription(d.getDescription());
        req.setAvailableTimes(d.getAvailableTimes());
        return req;
    }

    public static UpdateDoctorRequest toUpdateRequest(Doctor d) {
        if (d == null) return null;
        UpdateDoctorRequest req = new UpdateDoctorRequest();
        req.setId(d.getId());
        req.setName(d.getName());
        req.setDepartment(d.getDepartment());
        req.setTitle(d.getTitle());
        req.setPhone(d.getPhone());
        req.setSpecialty(d.getSpecialty());
        req.setDescription(d.getDescription());
        req.setAvailableTimes(d.getAvailableTimes());
        return req;
    }

    // ==================== Appointment 转换 ====================

    public static Appointment toAppointment(AppointmentInfoResponse dto) {
        if (dto == null) return null;
        Appointment a = new Appointment();
        a.setId(dto.getId());
        a.setPatientId(dto.getPatientId());
        a.setPatientName(dto.getPatientName());
        a.setDoctorId(dto.getDoctorId());
        a.setDoctorName(dto.getDoctorName());
        a.setDepartment(dto.getDepartment());
        a.setAppointmentDate(dto.getAppointmentDate());
        // ModelLocalTime → LocalTime
        if (dto.getAppointmentTime() != null) {
            a.setAppointmentTime(LocalTime.of(
                    dto.getAppointmentTime().getHour(),
                    dto.getAppointmentTime().getMinute(),
                    dto.getAppointmentTime().getSecond(),
                    dto.getAppointmentTime().getNano()
            ));
        }
        a.setAppointmentType(dto.getAppointmentType());
        a.setSymptoms(dto.getSymptoms());
        a.setStatus(dto.getStatus());
        a.setNotes(dto.getNotes());
        // OffsetDateTime → LocalDateTime
        if (dto.getCreatedAt() != null) {
            a.setCreatedAt(dto.getCreatedAt().toLocalDateTime());
        }
        return a;
    }

    public static List<Appointment> toAppointmentList(List<AppointmentInfoResponse> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(ModelConverter::toAppointment)
                .collect(Collectors.toList());
    }

    public static CreateAppointmentRequest toCreateRequest(Appointment a) {
        if (a == null) return null;
        CreateAppointmentRequest req = new CreateAppointmentRequest();
        req.setPatientId(a.getPatientId());
        req.setPatientName(a.getPatientName());
        req.setDoctorId(a.getDoctorId());
        req.setDoctorName(a.getDoctorName());
        req.setDepartment(a.getDepartment());
        req.setAppointmentDate(a.getAppointmentDate());
        if (a.getAppointmentTime() != null) {
            req.setAppointmentTime(new ModelLocalTime()
                    .hour(a.getAppointmentTime().getHour())
                    .minute(a.getAppointmentTime().getMinute())
                    .second(a.getAppointmentTime().getSecond())
                    .nano(a.getAppointmentTime().getNano()));
        }
        req.setAppointmentType(a.getAppointmentType());
        req.setSymptoms(a.getSymptoms());
        req.setNotes(a.getNotes());
        return req;
    }

    public static UpdateAppointmentRequest toUpdateRequest(Appointment a) {
        if (a == null) return null;
        UpdateAppointmentRequest req = new UpdateAppointmentRequest();
        req.setId(a.getId());
        req.setPatientId(a.getPatientId());
        req.setPatientName(a.getPatientName());
        req.setDoctorId(a.getDoctorId());
        req.setDoctorName(a.getDoctorName());
        req.setDepartment(a.getDepartment());
        req.setAppointmentDate(a.getAppointmentDate());
        if (a.getAppointmentTime() != null) {
            req.setAppointmentTime(new ModelLocalTime()
                    .hour(a.getAppointmentTime().getHour())
                    .minute(a.getAppointmentTime().getMinute())
                    .second(a.getAppointmentTime().getSecond())
                    .nano(a.getAppointmentTime().getNano()));
        }
        req.setAppointmentType(a.getAppointmentType());
        req.setSymptoms(a.getSymptoms());
        req.setNotes(a.getNotes());
        return req;
    }

    // ==================== MedicalRecord 转换 ====================

    public static MedicalRecord toMedicalRecord(MedicalRecordInfoResponse dto) {
        if (dto == null) return null;
        MedicalRecord r = new MedicalRecord();
        r.setId(dto.getId());
        r.setPatientId(dto.getPatientId());
        r.setPatientName(dto.getPatientName());
        r.setDoctorId(dto.getDoctorId());
        r.setDoctorName(dto.getDoctorName());
        r.setDepartment(dto.getDepartment());
        r.setVisitDate(dto.getVisitDate());
        r.setChiefComplaint(dto.getChiefComplaint());
        r.setDiagnosis(dto.getDiagnosis());
        r.setTreatmentPlan(dto.getTreatmentPlan());
        r.setPrescriptions(dto.getPrescriptions());
        r.setExaminationRecords(dto.getExaminationRecords());
        r.setNotes(dto.getNotes());
        r.setStatus(dto.getStatus());
        if (dto.getCreatedAt() != null) {
            r.setCreatedAt(dto.getCreatedAt().toLocalDateTime());
        }
        return r;
    }

    public static List<MedicalRecord> toMedicalRecordList(List<MedicalRecordInfoResponse> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(ModelConverter::toMedicalRecord)
                .collect(Collectors.toList());
    }

    public static CreateMedicalRecordRequest toCreateRequest(MedicalRecord r) {
        if (r == null) return null;
        CreateMedicalRecordRequest req = new CreateMedicalRecordRequest();
        req.setPatientId(r.getPatientId());
        req.setPatientName(r.getPatientName());
        req.setDoctorId(r.getDoctorId());
        req.setDoctorName(r.getDoctorName());
        req.setDepartment(r.getDepartment());
        req.setVisitDate(r.getVisitDate());
        req.setChiefComplaint(r.getChiefComplaint());
        req.setDiagnosis(r.getDiagnosis());
        req.setTreatmentPlan(r.getTreatmentPlan());
        req.setPrescriptions(r.getPrescriptions());
        req.setExaminationRecords(r.getExaminationRecords());
        req.setNotes(r.getNotes());
        return req;
    }

    public static UpdateMedicalRecordRequest toUpdateRequest(MedicalRecord r) {
        if (r == null) return null;
        UpdateMedicalRecordRequest req = new UpdateMedicalRecordRequest();
        req.setId(r.getId());
        req.setPatientId(r.getPatientId());
        req.setPatientName(r.getPatientName());
        req.setDoctorId(r.getDoctorId());
        req.setDoctorName(r.getDoctorName());
        req.setDepartment(r.getDepartment());
        req.setVisitDate(r.getVisitDate());
        req.setChiefComplaint(r.getChiefComplaint());
        req.setDiagnosis(r.getDiagnosis());
        req.setTreatmentPlan(r.getTreatmentPlan());
        req.setPrescriptions(r.getPrescriptions());
        req.setExaminationRecords(r.getExaminationRecords());
        req.setNotes(r.getNotes());
        return req;
    }

    // ==================== Medicine 转换 ====================

    public static Medicine toMedicine(MedicineInfoResponse dto) {
        if (dto == null) return null;
        Medicine m = new Medicine();
        m.setId(dto.getId());
        m.setName(dto.getName());
        m.setCategory(dto.getCategory());
        m.setSpecification(dto.getSpecification());
        m.setManufacturer(dto.getManufacturer());
        m.setPrice(dto.getPrice() != null ? dto.getPrice() : 0.0);
        m.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
        m.setProductionDate(dto.getProductionDate());
        m.setExpiryDate(dto.getExpiryDate());
        m.setUsage(dto.getUsage());
        m.setContraindication(dto.getContraindication());
        m.setStatus(dto.getStatus());
        return m;
    }

    public static List<Medicine> toMedicineList(List<MedicineInfoResponse> dtos) {
        if (dtos == null) return Collections.emptyList();
        return dtos.stream()
                .filter(Objects::nonNull)
                .map(ModelConverter::toMedicine)
                .collect(Collectors.toList());
    }

    public static CreateMedicineRequest toCreateRequest(Medicine m) {
        if (m == null) return null;
        CreateMedicineRequest req = new CreateMedicineRequest();
        req.setName(m.getName());
        req.setCategory(m.getCategory());
        req.setSpecification(m.getSpecification());
        req.setManufacturer(m.getManufacturer());
        req.setPrice(m.getPrice());
        req.setStockQuantity(m.getStockQuantity());
        req.setProductionDate(m.getProductionDate());
        req.setExpiryDate(m.getExpiryDate());
        req.setUsage(m.getUsage());
        req.setContraindication(m.getContraindication());
        return req;
    }

    public static UpdateMedicineRequest toUpdateRequest(Medicine m) {
        if (m == null) return null;
        UpdateMedicineRequest req = new UpdateMedicineRequest();
        req.setId(m.getId());
        req.setName(m.getName());
        req.setCategory(m.getCategory());
        req.setSpecification(m.getSpecification());
        req.setManufacturer(m.getManufacturer());
        req.setPrice(m.getPrice());
        req.setStockQuantity(m.getStockQuantity());
        req.setProductionDate(m.getProductionDate());
        req.setExpiryDate(m.getExpiryDate());
        req.setUsage(m.getUsage());
        req.setContraindication(m.getContraindication());
        return req;
    }

    // ==================== User / Role / Permission 转换 ====================

    /**
     * 从 UserInfoResponse 提取权限集（用于 UserSession）
     */
    public static java.util.Set<String> extractPermissions(UserInfoResponse dto) {
        if (dto == null || dto.getPermissions() == null) return new java.util.HashSet<>();
        return new java.util.HashSet<>(dto.getPermissions());
    }

    /**
     * 从 UserInfoResponse 提取角色集
     */
    public static java.util.Set<String> extractRoles(UserInfoResponse dto) {
        if (dto == null || dto.getRoles() == null) return new java.util.HashSet<>();
        return new java.util.HashSet<>(dto.getRoles());
    }

    // ==================== MenuDTO 转换（Swagger ↔ 前端通用） ====================

    /**
     * Swagger 的 MenuDTO 与前端通用的 MenuDTO（io.swagger.client.model.MenuDTO）字段完全一致，
     * 可以直接使用 Swagger 版本，无需转换。
     * 此方法仅为类型安全地复制树结构（如果需要解耦）。
     */
    public static io.swagger.client.model.MenuDTO copyMenu(io.swagger.client.model.MenuDTO src) {
        if (src == null) return null;
        io.swagger.client.model.MenuDTO dest = new io.swagger.client.model.MenuDTO();
        dest.setId(src.getId());
        dest.setTitle(src.getTitle());
        dest.setRoute(src.getRoute());
        dest.setIcon(src.getIcon());
        dest.setSortOrder(src.getSortOrder());
        dest.setModule(src.getModule());
        if (src.getChildren() != null) {
            dest.setChildren(src.getChildren().stream()
                    .map(ModelConverter::copyMenu)
                    .collect(Collectors.toList()));
        }
        return dest;
    }
}
