package com.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.PatientRequest.CreatePatientRequest;
import com.example.dto.request.PatientRequest.UpdatePatientRequest;
import com.example.dto.response.PatientResponse.PatientInfoResponse;
import com.example.entity.PatientEntity;
import com.example.repository.PatientRepository;
import com.example.service.PatientService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createPatient(CreatePatientRequest request) {
        PatientEntity patient = PatientEntity.builder()
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .phone(request.getPhone())
                .idCard(request.getIdCard())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .emergencyPhone(request.getEmergencyPhone())
                .registrationDate(request.getRegistrationDate())
                .medicalHistory(request.getMedicalHistory() != null ? request.getMedicalHistory() : new ArrayList<>())
                .status(request.getStatus())
                .build();
        patient = patientRepository.save(patient);
        return ServiceResult.success(patient.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updatePatient(UpdatePatientRequest request) {
        Optional<PatientEntity> optionalPatient = patientRepository.findById(request.getId());
        if (optionalPatient.isEmpty()) {
            return ServiceResult.error("患者不存在");
        }
        PatientEntity patient = optionalPatient.get();
        BeanUtils.copyProperties(request, patient, "id");
        patientRepository.save(patient);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            return ServiceResult.error("患者不存在");
        }
        patientRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<PatientInfoResponse> getPatientById(String id) {
        Optional<PatientEntity> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isEmpty()) {
            return ServiceResult.error("患者不存在");
        }
        return ServiceResult.success(convertToResponse(optionalPatient.get()));
    }

    @Override
    public ServiceResult<List<PatientInfoResponse>> getAllPatients() {
        List<PatientEntity> patients = patientRepository.findAll();
        if (CollectionUtils.isEmpty(patients)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(patients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<PatientInfoResponse>> searchPatients(String name) {
        List<PatientEntity> patients = patientRepository.searchByName(name);
        if (CollectionUtils.isEmpty(patients)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(patients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private PatientInfoResponse convertToResponse(PatientEntity entity) {
        PatientInfoResponse response = new PatientInfoResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
