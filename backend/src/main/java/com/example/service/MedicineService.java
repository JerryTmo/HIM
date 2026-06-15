package com.example.service;

import com.example.common.ServiceResult;
import com.example.dto.request.MedicineRequest.CreateMedicineRequest;
import com.example.dto.request.MedicineRequest.UpdateMedicineRequest;
import com.example.dto.response.MedicineResponse.MedicineInfoResponse;

import java.util.List;

public interface MedicineService {

    ServiceResult<String> createMedicine(CreateMedicineRequest request);

    ServiceResult<Void> updateMedicine(UpdateMedicineRequest request);

    ServiceResult<Void> deleteMedicine(String id);

    ServiceResult<MedicineInfoResponse> getMedicineById(String id);

    ServiceResult<List<MedicineInfoResponse>> getAllMedicines();

    ServiceResult<List<MedicineInfoResponse>> getMedicinesByCategory(String category);

    ServiceResult<List<MedicineInfoResponse>> searchMedicines(String name);
}
