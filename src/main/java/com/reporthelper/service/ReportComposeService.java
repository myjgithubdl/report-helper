package com.reporthelper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reporthelper.entity.ReportCompose;

import java.util.List;

public interface ReportComposeService  extends IService<ReportCompose>  {


    /**
     * 查询指定报表下的报表组成
     * @param reportId
     * @return
     */
    List<ReportCompose> getReportComposeList(Integer reportId);


    /**
     * 删除报表下的报表组成
     * @param reportId
     * @return
     */
    int deleteByReportId(Integer reportId);

}
