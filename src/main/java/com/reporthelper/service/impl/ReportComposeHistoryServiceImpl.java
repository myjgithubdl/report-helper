package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reporthelper.entity.ReportCompose;
import com.reporthelper.entity.ReportComposeHistory;
import com.reporthelper.mapper.ReportComposeHistoryMapper;
import com.reporthelper.service.ReportComposeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Myron
 */
@Service
public class ReportComposeHistoryServiceImpl extends ServiceImpl<ReportComposeHistoryMapper, ReportComposeHistory> implements ReportComposeHistoryService {

    @Autowired
    private ReportComposeHistoryMapper reportComposeHistoryMapper;

    @Override
    public List<ReportCompose> getReportComposeList(Integer reportId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("report_id", reportId);
        queryWrapper.orderByAsc("sequence");
        return reportComposeHistoryMapper.selectList(queryWrapper);
    }

    @Override
    public int deleteByReportId(Integer reportId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("report_id", reportId);
        return reportComposeHistoryMapper.delete(queryWrapper);
    }
}
