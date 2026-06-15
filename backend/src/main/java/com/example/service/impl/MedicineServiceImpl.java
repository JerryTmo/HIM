package com.example.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.example.common.ServiceResult;
import com.example.dto.request.MedicineRequest.CreateMedicineRequest;
import com.example.dto.request.MedicineRequest.UpdateMedicineRequest;
import com.example.dto.response.MedicineResponse.MedicineInfoResponse;
import com.example.entity.MedicineEntity;
import com.example.repository.MedicineRepository;
import com.example.service.MedicineService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<String> createMedicine(CreateMedicineRequest request) {
        MedicineEntity medicine = MedicineEntity.builder()
                .name(request.getName())
                .category(request.getCategory())
                .specification(request.getSpecification())
                .manufacturer(request.getManufacturer())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .productionDate(request.getProductionDate())
                .expiryDate(request.getExpiryDate())
                .usage(request.getUsage())
                .contraindication(request.getContraindication())
                .status(request.getStatus())
                .build();
        medicine = medicineRepository.save(medicine);
        return ServiceResult.success(medicine.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> updateMedicine(UpdateMedicineRequest request) {
        Optional<MedicineEntity> optionalMedicine = medicineRepository.findById(request.getId());
        if (optionalMedicine.isEmpty()) {
            return ServiceResult.error("药品不存在");
        }
        MedicineEntity medicine = optionalMedicine.get();
        BeanUtils.copyProperties(request, medicine, "id");
        medicineRepository.save(medicine);
        return ServiceResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<Void> deleteMedicine(String id) {
        if (!medicineRepository.existsById(id)) {
            return ServiceResult.error("药品不存在");
        }
        medicineRepository.deleteById(id);
        return ServiceResult.success();
    }

    @Override
    public ServiceResult<MedicineInfoResponse> getMedicineById(String id) {
        Optional<MedicineEntity> optionalMedicine = medicineRepository.findById(id);
        if (optionalMedicine.isEmpty()) {
            return ServiceResult.error("药品不存在");
        }
        return ServiceResult.success(convertToResponse(optionalMedicine.get()));
    }

    @Override
    public ServiceResult<List<MedicineInfoResponse>> getAllMedicines() {
        List<MedicineEntity> medicines = medicineRepository.findAll();
        if (CollectionUtils.isEmpty(medicines)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(medicines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<MedicineInfoResponse>> getMedicinesByCategory(String category) {
        List<MedicineEntity> medicines = medicineRepository.findByCategory(category);
        if (CollectionUtils.isEmpty(medicines)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(medicines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<MedicineInfoResponse>> searchMedicines(String name) {
        List<MedicineEntity> medicines = medicineRepository.searchByName(name);
        if (CollectionUtils.isEmpty(medicines)) {
            return ServiceResult.success(Collections.emptyList());
        }
        return ServiceResult.success(medicines.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList()));
    }

    private MedicineInfoResponse convertToResponse(MedicineEntity entity) {
        MedicineInfoResponse response = new MedicineInfoResponse();
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
