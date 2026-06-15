package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.PatientRequest.CreatePatientRequest;
import com.example.dto.request.PatientRequest.UpdatePatientRequest;
import com.example.dto.response.PatientResponse.PatientInfoResponse;

import java.util.List;

public interface PatientService {

    ServiceResult<String> createPatient(CreatePatientRequest request);

    ServiceResult<Void> updatePatient(UpdatePatientRequest request);

    ServiceResult<Void> deletePatient(String id);

    ServiceResult<PatientInfoResponse> getPatientById(String id);

    ServiceResult<List<PatientInfoResponse>> getAllPatients();

    ServiceResult<List<PatientInfoResponse>> searchPatients(String name);
}
