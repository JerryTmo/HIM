package com.example.service;

import java.util.List;

import com.example.common.ServiceResult;
import com.example.dto.request.MenuRequest;
import com.example.service.impl.MenuServiceImpl.MenuDTO;

public interface MenuService {

    /**
     * 查詢菜單
     * 
     * @param username 用戶名
     * @return 菜單信息
     */
    ServiceResult<List<MenuDTO>> findByMenu(String username);

    ServiceResult<Integer> insertMenus(MenuRequest menuRequest);

}
