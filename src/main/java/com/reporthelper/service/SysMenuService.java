package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.bo.EasyUITreeNode;
import com.reporthelper.entity.SysMenu;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Myron
 * @since 2018-12-27
 */
public interface SysMenuService extends IService<SysMenu> {


    /**
     * @param modules
     * @param predicate
     * @return
     */
    List<EasyUITreeNode<SysMenu>> getSysMenuTree(List<SysMenu> modules, Predicate<SysMenu> predicate);

    /**
     * @param id
     * @param pid
     * @return
     */
    boolean remove(int id, int pid);

    boolean hasChildren(int id);

    List<SysMenu> getChildren(int parentId);

    @Transactional
    void move(int sourceId, int targetId, int sourcePid, String sourcePath);

    void rebuildAllPath();
}
