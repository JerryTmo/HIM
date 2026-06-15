package com.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.example.dto.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.example.dto.response.MedicalRecordResponse.MedicalRecordInfoResponse;
import com.example.entity.MedicalRecordEntity;
import com.example.repository.MedicalRecordRepository;
import com.example.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createMedicalRecord(CreateMedicalRecordRequest request) {
        MedicalRecordEntity record = MedicalRecordEntity.builder()
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorId(request.getDoctorId())
                .doctorName(request.getDoctorName())
                .department(request.getDepartment())
                .visitDate(request.getVisitDate())
                .chiefComplaint(request.getChiefComplaint())
                .diagnosis(request.getDiagnosis())
                .treatmentPlan(request.getTreatmentPlan())
                .prescriptions(request.getPrescriptions() != null ? request.getPrescriptions() : new ArrayList<>())
                .examinationRecords(request.getExaminationRecords() != null ? request.getExaminationRecords() : new ArrayList<>())
                .notes(request.getNotes())
                .status(request.getStatus())
                .build();
        record = medicalRecordRepository.save(record);
        return ServiceResult.success(record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateMedicalRecord(UpdateMedicalRecordRequest request) {
        Optional<MedicalRecordEntity> optionalRecord = medicalRecordRepository.findById(request.getId());
        if (optionalRecord.isEmpty()) {
            return ServiceResult.error("病历不存在");
        }
        MedicalRecordEntity record = optionalRecord.get();
        BeanUtils.copyProperties(request, record, "id");
        medicalRecordRepository.save(record);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteMedicalRecord(String id) {
        if (!medicalRecordRepository.existsById(id)) {
            return ServiceResult.error("病历不存在");
        }
        medicalRecordRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<MedicalRecordInfoResponse> getMedicalRecordById(String id) {
        Optional<MedicalRecordEntity> optionalRecord = medicalRecordRepository.findById(id);
        if (optionalRecord.isEmpty()) {
            return ServiceResult.error("病历不存在");
        }
        return ServiceResult.success(convertToResponse(optionalRecord.get()));
    }

    @Override
    public ServiceResult<List<MedicalRecordInfoResponse>> getAllMedicalRecords() {
        List<MedicalRecordEntity> records = medicalRecordRepository.findAll();
        if (CollectionUtils.isEmpty(records)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByPatient(String patientId) {
        List<MedicalRecordEntity> records = medicalRecordRepository.findByPatientId(patientId);
        if (CollectionUtils.isEmpty(records)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByDoctor(String doctorId) {
        List<MedicalRecordEntity> records = medicalRecordRepository.findByDoctorId(doctorId);
        if (CollectionUtils.isEmpty(records)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private MedicalRecordInfoResponse convertToResponse(MedicalRecordEntity entity) {
        MedicalRecordInfoResponse response = new MedicalRecordInfoResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
