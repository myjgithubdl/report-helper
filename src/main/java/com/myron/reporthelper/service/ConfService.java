package com.myron.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myron.reporthelper.entity.Conf;

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
     * @param parentId
     * @return
     */
    List<Conf> getByParentId(Integer parentId);

    List<Conf> selectByParentKey(String key);

}
