package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reporthelper.entity.ReportCompose;
import com.reporthelper.mapper.ReportComposeMapper;
import com.reporthelper.service.ReportComposeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Myron
 */
@Service
public class ReportComposeServiceImpl extends ServiceImpl<ReportComposeMapper, ReportCompose> implements ReportComposeService {

    @Autowired
    private ReportComposeMapper reportComposeMapper;

    @Override
    public List<ReportCompose> getReportComposeList(Integer reportId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("report_id", reportId);
        queryWrapper.orderByAsc("sequence");
        return reportComposeMapper.selectList(queryWrapper);
    }

    @Override
    public int deleteByReportId(Integer reportId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("report_id", reportId);
        return reportComposeMapper.delete(queryWrapper);
    }
}
