
package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.MenuEntity;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, String> {

    /**
     * 檢查菜單路由是否存在
     * 
     * @param route 路由
     * @return 是否
     */
    boolean existsByRoute(String route);
}