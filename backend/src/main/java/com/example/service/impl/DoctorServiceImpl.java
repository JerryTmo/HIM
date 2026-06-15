package com.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.DoctorRequest.CreateDoctorRequest;
import com.example.dto.request.DoctorRequest.UpdateDoctorRequest;
import com.example.dto.response.DoctorResponse.DoctorInfoResponse;
import com.example.entity.DoctorEntity;
import com.example.repository.DoctorRepository;
import com.example.service.DoctorService;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createDoctor(CreateDoctorRequest request) {
        DoctorEntity doctor = DoctorEntity.builder()
                .name(request.getName())
                .department(request.getDepartment())
                .title(request.getTitle())
                .phone(request.getPhone())
                .specialty(request.getSpecialty())
                .description(request.getDescription())
                .availableTimes(request.getAvailableTimes() != null ? request.getAvailableTimes() : new ArrayList<>())
                .status(request.getStatus())
                .build();
        doctor = doctorRepository.save(doctor);
        return ServiceResult.success(doctor.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateDoctor(UpdateDoctorRequest request) {
        Optional<DoctorEntity> optionalDoctor = doctorRepository.findById(request.getId());
        if (optionalDoctor.isEmpty()) {
            return ServiceResult.error("医生不存在");
        }
        DoctorEntity doctor = optionalDoctor.get();
        BeanUtils.copyProperties(request, doctor, "id");
        doctorRepository.save(doctor);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteDoctor(String id) {
        if (!doctorRepository.existsById(id)) {
            return ServiceResult.error("医生不存在");
        }
        doctorRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<DoctorInfoResponse> getDoctorById(String id) {
        Optional<DoctorEntity> optionalDoctor = doctorRepository.findById(id);
        if (optionalDoctor.isEmpty()) {
            return ServiceResult.error("医生不存在");
        }
        return ServiceResult.success(convertToResponse(optionalDoctor.get()));
    }

    @Override
    public ServiceResult<List<DoctorInfoResponse>> getAllDoctors() {
        List<DoctorEntity> doctors = doctorRepository.findAll();
        if (CollectionUtils.isEmpty(doctors)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(doctors.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<DoctorInfoResponse>> getDoctorsByDepartment(String department) {
        List<DoctorEntity> doctors = doctorRepository.findByDepartment(department);
        if (CollectionUtils.isEmpty(doctors)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(doctors.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<DoctorInfoResponse>> searchDoctors(String name) {
        List<DoctorEntity> doctors = doctorRepository.searchByName(name);
        if (CollectionUtils.isEmpty(doctors)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(doctors.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private DoctorInfoResponse convertToResponse(DoctorEntity entity) {
        DoctorInfoResponse response = new DoctorInfoResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
