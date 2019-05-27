package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.EmptyWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reporthelper.entity.ReportRole;
import com.reporthelper.entity.SysRole;
import com.reporthelper.entity.User;
import com.reporthelper.mapper.ReportRoleMapper;
import com.reporthelper.service.ReportRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reporthelper.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 缪应江
 * @since 2019-01-05
 */
@Service
public class ReportRoleServiceImpl extends ServiceImpl<ReportRoleMapper, ReportRole> implements ReportRoleService {

    @Autowired
    ReportRoleMapper reportRoleMapper;


    @Override
    public int getReportRoleCount(Map<String, Object> params) {
        return this.reportRoleMapper.getReportRoleCount(params);
    }

    @Override
    public List<Map<String, Object>> getReportRoleList(Map<String, Object> params) {
        return this.reportRoleMapper.getReportRoleList(params);
    }


    @Override
    public List<ReportRole> getReportListByReportRoles(String reportRoles) {
        if(StringUtils.isNotEmpty(reportRoles)){
            QueryWrapper<ReportRole> wrapper=new QueryWrapper<>();
            wrapper.in("id", reportRoles.split(","));
            return this.list(wrapper);
        }

        return null;
    }
}
