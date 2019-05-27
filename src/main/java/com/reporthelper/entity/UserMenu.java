package com.reporthelper.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * 用户的菜单类（系统菜单或报表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMenu {

    private String id;

    /**
     * 系统模块父标识
     */
    private String pid;

    /**
     * 系统模块父标识
     */
    private String name;

    /**
     * 系统模块显示图标
     */
    private String icon;

    /**
     * 系统模块对应的页面地址
     */
    private String url;

    /**
     * 菜单打开方式，目前有none：无，iframe：tab嵌入内容，blank：在浏览器新窗口中打开
     * URL链接的target(_blank,_top等)
     */
    private String target;

    /**
     * 是否存在子模块,0否,1 是
     */
    private Integer hasChild = 0;

    /**
     * 菜单层级
     */
    private int level = 1;

    /**
     * 排序
     */
    private int sequence;

    /**
     * 设置level字段的值，并重排序
     *
     * @param userMenus
     * @return
     */
    public static List<UserMenu> resetMenuLevel(List<UserMenu> userMenus) {
        if (userMenus == null || userMenus.size() == 0) {
            return null;
        }

        //转为map结构
        Map<String, UserMenu> userMenuMap = new HashMap<>();
        userMenus.stream().forEach(m -> {
            userMenuMap.put(m.getId(), m);
        });

        //设置所有子节点的登记
        for (UserMenu m : userMenus) {
            if (userMenuMap.get(m.getPid()) == null) {
                m.setLevel(1);
                setChildrenLevel(m, userMenus);
            }
        }

        //排序
        userMenus.stream().sorted(Comparator.comparingInt(UserMenu::getLevel).thenComparing(UserMenu::getSequence).thenComparing(UserMenu::getId));
        return userMenus;
    }


    /**
     * 设置孩子的level
     *
     * @param parentMenu
     * @param userMenus
     */
    public static void setChildrenLevel(UserMenu parentMenu, List<UserMenu> userMenus) {
        if (parentMenu == null || userMenus == null) {
            return;
        }
        for (UserMenu m : userMenus) {
            if (parentMenu.getId().equals(m.getPid())) {
                parentMenu.setHasChild(1);
                m.setLevel(parentMenu.getLevel() + 1);

                setChildrenLevel(m, userMenus);
            }
        }
    }

    /**
     * 排序
     *
     * @param userMenus
     */
    public static void resortMenu(List<UserMenu> userMenus) {
        userMenus.stream().sorted(Comparator.comparing(UserMenu::getLevel).thenComparing(UserMenu::getSequence).thenComparing(UserMenu::getId));
    }


}
