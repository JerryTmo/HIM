package com.example.cache;

import io.swagger.client.model.MenuDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 菜单缓存管理器
 */
@Slf4j
public class MenuCache {

    private static volatile MenuCache instance;
    private final Map<String, MenuDTO> menuMap = new ConcurrentHashMap<>();
    private List<MenuDTO> menuTree;

    private MenuCache() {
    }

    public static MenuCache getInstance() {
        if (instance == null) {
            synchronized (MenuCache.class) {
                if (instance == null) {
                    instance = new MenuCache();
                }
            }
        }
        return instance;
    }

    /**
     * 缓存菜单树
     */
    public void cacheMenuTree(List<MenuDTO> menus) {
        this.menuTree = menus;
        buildMenuMap(menus);
        log.info("菜单缓存已更新，共 {} 个菜单项", menuMap.size());
    }

    /**
     * 构建菜单映射
     */
    private void buildMenuMap(List<MenuDTO> menus) {
        menuMap.clear();
        menus.forEach(menu -> {
            menuMap.put(menu.getId(), menu);
            if (menu.getChildren() != null) {
                buildMenuMap(menu.getChildren());
            }
        });
    }

    /**
     * 获取菜单树
     */
    public List<MenuDTO> getMenuTree() {
        return menuTree;
    }

    /**
     * 获取菜单项
     */
    public MenuDTO getMenu(String id) {
        return menuMap.get(id);
    }

    /**
     * 清空缓存
     */
    public void clear() {
        menuMap.clear();
        menuTree = null;
        log.info("菜单缓存已清空");
    }
}