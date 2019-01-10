package com.myron.reporthelper.service;

import com.myron.reporthelper.bo.EasyUITreeNode;
import com.myron.reporthelper.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Predicate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 缪应江
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
