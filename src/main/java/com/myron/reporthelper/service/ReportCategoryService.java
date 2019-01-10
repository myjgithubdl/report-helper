package com.myron.reporthelper.service;

import com.myron.reporthelper.entity.ReportCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 报表类别表 服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface ReportCategoryService extends IService<ReportCategory> {
    /**
     * @param id
     * @param pid
     * @return
     */
    boolean remove(int id, int pid);

    boolean hasChildren(int id);


    /**
     * @param sourceId
     * @param targetId
     * @return
     */
    ReportCategory paste(int sourceId, int targetId);


    /**
     * @param sourceId
     * @param targetId
     * @param sourcePid
     * @param sourcePath
     */
    void move(int sourceId, int targetId, int sourcePid, String sourcePath);

    /**
     *
     */
    void rebuildAllPath();
}
