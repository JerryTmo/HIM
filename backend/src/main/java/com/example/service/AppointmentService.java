package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.AppointmentRequest.CreateAppointmentRequest;
import com.example.dto.request.AppointmentRequest.UpdateAppointmentRequest;
import com.example.dto.response.AppointmentResponse.AppointmentInfoResponse;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {

    ServiceResult<String> createAppointment(CreateAppointmentRequest request);

    ServiceResult<Void> updateAppointment(UpdateAppointmentRequest request);

    ServiceResult<Void> deleteAppointment(String id);

    ServiceResult<AppointmentInfoResponse> getAppointmentById(String id);

    ServiceResult<List<AppointmentInfoResponse>> getAllAppointments();

    ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByPatient(String patientId);

    ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDoctor(String doctorId);

    ServiceResult<List<AppointmentInfoResponse>> getAppointmentsByDate(LocalDate date);
}
