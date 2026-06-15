package com.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.AppointmentRequest.CreateAppointmentRequest;
import com.example.dto.request.AppointmentRequest.UpdateAppointmentRequest;
import com.example.dto.response.AppointmentResponse.AppointmentInfoResponse;
import com.example.entity.AppointmentEntity;
import com.example.repository.AppointmentRepository;
import com.example.service.AppointmentService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createAppointment(CreateAppointmentRequest request) {
        AppointmentEntity appointment = AppointmentEntity.builder()
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .doctorId(request.getDoctorId())
                .doctorName(request.getDoctorName())
                .department(request.getDepartment())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .appointmentType(request.getAppointmentType())
                .symptoms(request.getSymptoms())
                .status(request.getStatus())
                .notes(request.getNotes())
                .build();
        appointment = appointmentRepository.save(appointment);
        return ServiceResult.success(appointment.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateAppointment(UpdateAppointmentRequest request) {
        Optional<AppointmentEntity> optionalAppointment = appointmentRepository.findById(request.getId());
        if (optionalAppointment.isEmpty()) {
            return ServiceResult.error("预约不存在");
        }
        AppointmentEntity appointment = optionalAppointment.get();
        BeanUtils.copyProperties(request, appointment, "id");
        appointmentRepository.save(appointment);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteAppointment(String id) {
        if (!appointmentRepository.existsById(id)) {
            return ServiceResult.error("预约不存在");
        }
        appointmentRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<AppointmentInfoResponse> getAppointmentById(String id) {
        Optional<AppointmentEntity> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isEmpty()) {
            return ServiceResult.error("预约不存在");
        }
        return ServiceResult.success(convertToResponse(optionalAppointment.get()));
    }

    @Override
    public ServiceResult<List<AppointmentInfoResponse>> getAllAppointments() {
        List<AppointmentEntity> appointments = appointmentRepository.findAll();
        if (CollectionUtils.isEmpty(appointments)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByPatient(String patientId) {
        List<AppointmentEntity> appointments = appointmentRepository.findByPatientId(patientId);
        if (CollectionUtils.isEmpty(appointments)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDoctor(String doctorId) {
        List<AppointmentEntity> appointments = appointmentRepository.findByDoctorId(doctorId);
        if (CollectionUtils.isEmpty(appointments)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDate(LocalDate date) {
        List<AppointmentEntity> appointments = appointmentRepository.findByDate(date);
        if (CollectionUtils.isEmpty(appointments)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private AppointmentInfoResponse convertToResponse(AppointmentEntity entity) {
        AppointmentInfoResponse response = new AppointmentInfoResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
