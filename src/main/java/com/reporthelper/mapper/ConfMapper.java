package com.reporthelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reporthelper.entity.Conf;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 配置表 Mapper 接口
 * </p>
 *
 * @author Myron Miao
 * @since 2018-12-27
 */
public interface ConfMapper extends BaseMapper<Conf> {

    /**
     * 查询列表
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getReportList(Map<String, Object> params);


    int getReportCount(Map<String, Object> params);


    /**
     * 根据可以查询所有的孩子
     *
     * @param key
     * @return
     */
    List<Conf> selectByParentKey(String key);

}
