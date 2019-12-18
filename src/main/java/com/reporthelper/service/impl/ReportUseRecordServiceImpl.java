package com.reporthelper.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reporthelper.entity.ReportUseRecord;
import com.reporthelper.mapper.ReportUseRecordMapper;
import com.reporthelper.service.ReportUseRecordService;
import org.springframework.stereotype.Service;

/**
 * 报表访问记录实现类
 */
@Service
public class ReportUseRecordServiceImpl extends ServiceImpl<ReportUseRecordMapper, ReportUseRecord> implements ReportUseRecordService {
}
