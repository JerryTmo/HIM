package com.example.repository;

import com.example.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, String> {

    boolean existsByRoute(String route);

    List<MenuEntity> findAllByOrderBySortOrderAsc();

    List<MenuEntity> findByParentIsNullOrderBySortOrderAsc();

    List<MenuEntity> findByModule(String module);

    boolean existsByTitleAndParentId(String title, String parentId);
}
