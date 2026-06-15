package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.DoctorRequest.CreateDoctorRequest;
import com.example.dto.request.DoctorRequest.UpdateDoctorRequest;
import com.example.dto.response.DoctorResponse.DoctorInfoResponse;

import java.util.List;

public interface DoctorService {

    ServiceResult<String> createDoctor(CreateDoctorRequest request);

    ServiceResult<Void> updateDoctor(UpdateDoctorRequest request);

    ServiceResult<Void> deleteDoctor(String id);

    ServiceResult<DoctorInfoResponse> getDoctorById(String id);

    ServiceResult<List<DoctorInfoResponse>> getAllDoctors();

    ServiceResult<List<DoctorInfoResponse>> getDoctorsByDepartment(String department);

    ServiceResult<List<DoctorInfoResponse>> searchDoctors(String name);
}
