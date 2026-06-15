package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.MedicalRecordRequest.CreateMedicalRecordRequest;
import com.example.dto.request.MedicalRecordRequest.UpdateMedicalRecordRequest;
import com.example.dto.response.MedicalRecordResponse.MedicalRecordInfoResponse;

import java.util.List;

public interface MedicalRecordService {

    ServiceResult<String> createMedicalRecord(CreateMedicalRecordRequest request);

    ServiceResult<Void> updateMedicalRecord(UpdateMedicalRecordRequest request);

    ServiceResult<Void> deleteMedicalRecord(String id);

    ServiceResult<MedicalRecordInfoResponse> getMedicalRecordById(String id);

    ServiceResult<List<MedicalRecordInfoResponse>> getAllMedicalRecords();

    ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByPatient(String patientId);

    ServiceResult<List<MedicalRecordInfoResponse>> getMedicalRecordsByDoctor(String doctorId);
}
