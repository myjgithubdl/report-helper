package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.entity.Conf;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 缪应江
 * @since 2018-12-27
 */
public interface ConfService extends IService<Conf> {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    int getReportCount(Map<String, Object> params);




    /**
     * @param parentId
     * @return
     */
    List<Conf> getByParentId(Integer parentId);

    List<Conf> selectByParentKey(String key);

}
