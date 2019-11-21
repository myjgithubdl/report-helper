/**
 * Created by bdf on 2019/10/21.
 */
var reportCommonSuffix = 'common';//报表公共部分id后缀
var layer;

$(function () {
    PublishReport.init();
});

var PublishReport = {
    //初始化方法
    init: function () {
        layui.use(["layer"], function () {//layui加载完成
            layer = layui.layer;
            PublishReport.setReportComposeCssAndUids()
            PublishReport.createReportQueryParams();

            PublishReport.initAllReportQueryParamVals();

            PublishReportMVC.Controller.submitQueryReport();

            PublishReportMVC.Variables.isInitPageLoadData=false;//初始化完成重置为false
        });
    },
    /**
     * 设置Css属性和获取uid
     */
    setReportComposeCssAndUids: function () {
        console.log(reportObj)
        var reportComposeList = reportObj.reportComposeList;
        console.log(reportComposeList)
        if (reportComposeList) {
            _.each(reportComposeList, function (reportCompose) {
                var uid = reportCompose.uid;
                PublishReportMVC.Variables.reportComposeMap[uid] = reportCompose;
                PublishReportMVC.Variables.reportComposeUids.push(uid);
                var reportComposeId = PublishReportMVC.Util.getReportComposeId(uid);
                if (reportCompose.options) {
                    var options = JSON.parse(reportCompose.options);
                    PublishReportMVC.Variables.reportComposeOptions[uid] = options;
                    var showContent = options.showContent;
                    var composeShowId = PublishReportMVC.Util.getReportComposeShowId(uid);
                    if (showContent == 1) {//数据表格
                        $("#" + composeShowId).append('<div id="' + PublishReportMVC.Util.getReportComposeTableDivId(uid) + '"></div>');
                        if (options.enablePage) {//启用分页
                            $("#" + composeShowId).append('<div id="' + PublishReportMVC.Util.getReportComposePageDivId(uid) + '"></div>');
                        }
                    } else if (showContent == 4) {//透视表
                        PublishReportMVC.Util.createReportPivotTableDiv(uid, reportCompose);
                    } else if (_.indexOf(PublishReportMVC.Variables.showContentChartVals, showContent) != -1) {
                        PublishReportMVC.Util.createReportComposeChartDiv(uid);
                    } else if (_.indexOf(PublishReportMVC.Variables.showContentBoardChartVals, showContent) != -1) {
                        PublishReportMVC.Util.createReportComposeBoardChartDiv(uid);
                    }
                    PublishReportMVC.Util.addReportExplainHtml(uid);//增加报表说明
                    var css = {};
                    var floatCss = options.floatCss;
                    if (floatCss && $.trim(floatCss).length > 0) {
                        if (floatCss != 'none') {
                            css["float"] = floatCss;
                        } else {
                            css["clear"] = 'both';
                        }
                    }
                    var reportWidth = options.reportWidth;
                    if (reportWidth && $.trim(reportWidth).length > 0) {
                        css["width"] = reportWidth + options.widthUnit;
                    }
                    var reportHeight = options.reportHeight;
                    if (reportHeight && $.trim(reportHeight).length > 0) {
                        css["height"] = reportHeight + options.heightUnit;
                    }
                    if (options.paddingBottom && $.trim(options.paddingBottom).length > 0) {
                        css["padding-bottom"] = options.paddingBottom + options.paddingUnit;
                    }
                    if (options.paddingLeft && $.trim(options.paddingLeft).length > 0) {
                        css["padding-left"] = options.paddingLeft + options.paddingUnit;
                    }
                    if (options.paddingRight && $.trim(options.paddingRight).length > 0) {
                        css["padding-right"] = options.paddingRight + options.paddingUnit;
                    }
                    if (options.paddingTop && $.trim(options.paddingTop).length > 0) {
                        css["padding-top"] = options.paddingTop + options.paddingUnit;
                    }
                    if (options.marginBottom && $.trim(options.marginBottom).length > 0) {
                        css["margin-bottom"] = options.marginBottom + options.marginUnit;
                    }
                    if (options.marginLeft && $.trim(options.marginLeft).length > 0) {
                        css["margin-left"] = options.marginLeft + options.marginUnit;
                    }
                    if (options.marginRight && $.trim(options.marginRight).length > 0) {
                        css["margin-right"] = options.marginRight + options.marginUnit;
                    }
                    if (options.marginTop && $.trim(options.marginTop).length > 0) {
                        css["margin-top"] = options.marginTop + options.marginUnit;
                    }
                    if (css) {
                        $("#" + reportComposeId).css(css);
                    }
                }
            })
        }
    },
    /**
     * 实例化报表查询参数
     */
    createReportQueryParams: function () {
        console.log(PublishReportMVC)
        console.log(PublishReportMVC.URLs)
        $.ajax({
            type: "POST",
            async: false,
            url: PublishReportMVC.URLs.getReportQueryParam.url + reportUid,
            success: function (data) {
                PublishReportMVC.Variables.reportQueryParams = data.respData;
                var paramShare = data.respData.paramShare;
                if (paramShare == 1) {
                    $("#" + PublishReportMVC.Util.getReportParamDivId(reportCommonSuffix)).show();
                    queryFormUtils.createQueryForm(PublishReportMVC.Util.getReportParamFormId(reportCommonSuffix), data.respData["params_" + reportCommonSuffix]["queryElements"]);
                    PublishReport.resetReportQueryParamsCss()
                } else {
                    //创建每一个报表组成的查询参数
                    _.each(PublishReportMVC.Variables.reportComposeUids, function (uid) {
                        $("#" + PublishReportMVC.Util.getReportParamDivId(uid)).show();
                        queryFormUtils.createQueryForm(PublishReportMVC.Util.getReportParamFormId(uid), data.respData["params_" + uid]["queryElements"]);
                        PublishReport.resetReportQueryParamsCss(uid)
                    })
                }
            }
        })
    },
    /**
     * 实例化所有报表查询参数以及值
     */
    initAllReportQueryParamVals: function () {
        var commonParams = PublishReportMVC.Controller.getReportQueryFormParams(reportCommonSuffix);
        PublishReportMVC.Variables.reportFormQueryParam[reportCommonSuffix] = {
            isChangeFormParam: true,//是否有改变表单参数、
            formParam: commonParams,//表单参数
        };

        var reportComposeList = reportObj.reportComposeList;
        if (reportComposeList) {
            _.each(reportComposeList, function (reportCompose) {
                var uid = reportCompose.uid;
                var reportComposeParams = PublishReportMVC.Controller.getReportQueryFormParams(uid);
                PublishReportMVC.Variables.reportFormQueryParam[uid] = {
                    isChangeFormParam: true,//是否有改变表单参数、
                    formParam: reportComposeParams,//表单参数
                    pageInfo: null//报表分页信息
                };
            });
        }
    },
    resetReportQueryParamsCss: function (composeUid) {
        //不控制高度，原本控制高度的目的是计算数据展示区的高度，
        // 修改为在获取参数高度、注释高度、透视表参数时给div加class overflow-hidden,获取到后remove该class即可
        return;
        var paramDiv = $("#" + PublishReportMVC.Util.getReportParamDivId(composeUid));
        if (paramDiv.size() == 1) {
            var paramHeight = parseInt(paramDiv.css("height")) + 2;//参数div需要先设置overflow-hidden，把高度计算出现再删除属性，不删除选择框会有隐藏
            paramDiv.css({height: paramHeight + 'px'}).removeClass("overflow-hidden");
        }
    }
}

var PublishReportMVC = {
    /**
     * 变量
     */
    Variables: {
        isInitPageLoadData:true,//是否初始化页面加载数据，因为初始化时数据不完整不报错，页面加载完成因查询报表参数不完整需要报错
        reportComposeMap: {},//报表组成对象，key为uid属性
        reportComposeUids: [],//报表组成的UID
        reportQueryParams: {},//报表设置的查询参数
        reportComposeOptions: {},//报表组成option选项,key为报表组成的uid

        showContentChartVals: ['11', '12', '13', '14', '15'],//图表的选项值
        showContentBoardChartVals: ['21', '22', '23', '24', '25'],//画板的选项值

        reportFormQueryParam: {},//保存报表查询的相关信息，key为报表的uid，值包括参数是否改变(isChangeFormParam)、表单参数(formParam)、分页信息(pageInfo)
        curPageTableComposeUid: '',//点击分页数据表格的uid
        queryReportComposeUid: null,//操作的报表组ID,如点击分页按钮等

        reportQueryData: {},//报表查询数据，key为reportCompose的uid，value为JSON 数据

        zuiMessager:null,//zui 提示
    },
    /**
     * 地址集
     */
    URLs: {
        getReportQueryParam: {
            url: ReportHelper.ctxPath + '/report/preview/queryReportParameter/uid/',
            method: 'POST'
        },
        GetReportData: {
            url: ReportHelper.ctxPath + '/report/preview/getReportData/',
            method: 'POST'
        },
        QueryReportParameter: {
            url: ReportHelper.ctxPath + '',
            method: 'post'
        },
        ReloadSelectParamOption: {
            url: ReportHelper.ctxPath + '/report/preview/reloadSelectParamOption/uid/',
            method: 'post'
        },
        ExportToFile: {
            url:ReportHelper.ctxPath + '/report/preview/exportToFile/' ,
            method: 'post'
        },
    },
    /**
     * 控制器
     */
    Controller: {
        /**
         * 获取指定报表下的查询参数、其中包括链接中的参数
         * @param cuid
         * @returns {*}
         */
        getReportQueryFormParams: function (cuid) {
            //如果没有指定获取那一部分参数则获取公共参数
            if(cuid == null || cuid==undefined){
                cuid=reportCommonSuffix;
            }
            var retParams = $("#" + PublishReportMVC.Util.getReportParamFormId(cuid)).serializeObject()
            var requestParam = $("#request-param-form").serializeObject(); //链接参数
            if (requestParam) {
                _.extend(retParams, requestParam)
            }

            var finalParam = PublishReportMVC.Util.filterQueryFormParam(retParams);
            return finalParam;
        },
        /**
         * 获取透视表参数
         * @param cuid
         */
        getPivotTableFormParams: function (cuid) {
            var returnParams = {};
            var reportComposeList = reportObj.reportComposeList;
            if (cuid != reportCommonSuffix) {
                reportComposeList = _.where(reportComposeList, {uid: cuid});
            }
            if (reportComposeList && reportComposeList.length > 0) {
                _.each(reportComposeList, function (reportCompose) {
                    var uid = reportCompose.uid;
                    if (reportCompose.options) {
                        var options = JSON.parse(reportCompose.options);
                        var showContent = options.showContent;
                        if (showContent == 4) {//透视表
                            var pivotTableParamFormId = PublishReportMVC.Util.getReportPivotTableParamFormId(uid);
                            var formDom = $("#" + pivotTableParamFormId);
                            if (formDom.size() == 1) {
                                var pivotTableFormParam = formDom.serializeObject(); //透视表表单
                                console.log(pivotTableFormParam)
                                PublishReportMVC.Variables.reportFormQueryParam[uid].pivotTableFormParam = pivotTableFormParam;
                                returnParams[uid]=pivotTableFormParam;
                            }
                        }
                    }
                })
            }
            return returnParams;
        },
        /**
         * 提交查询、如果有cuid参数则仅查询一个报表
         * @param cuid
         */
        submitQueryReport: function (cuid) {
            var cuidTmpl = PublishReportMVC.Util.getOptReportUid(cuid);

            PublishReportMVC.Util.setIsChangeFormVal(cuid, true);//设置所有报表查询参数都为改变状态

            if (cuid) {
                PublishReportMVC.Variables.queryReportComposeUid = cuid;
            } else {
                PublishReportMVC.Variables.queryReportComposeUid = null;
            }
            //重置当前页为第一页
            if (PublishReportMVC.Variables.reportFormQueryParam[cuidTmpl].pageInfo
                && PublishReportMVC.Variables.reportFormQueryParam[cuidTmpl].pageInfo.curr > 0) {
                PublishReportMVC.Variables.reportFormQueryParam[cuidTmpl].pageInfo.curr = 1;
            }

            var finalParams = PublishReportMVC.Controller.getReportQueryFormParams(cuidTmpl);
            PublishReportMVC.Variables.reportFormQueryParam[cuidTmpl].formParam = finalParams;

            PublishReportMVC.Data.loadReportData();
        },
        /**
         * 显示表格的图表数据
         * @param uid
         */
        showChartTableData: function (cuid, filterKey, filterValue) {
            var reportCompose = PublishReportMVC.Variables.reportComposeMap[cuid];
            var data = PublishReportMVC.Variables.reportQueryData[cuid].data;
            var metaColumns = JSON.parse(reportCompose.metaColumns);
            if (filterKey && filterValue) {//如果有过滤条件、则过滤数据。在画板中使用到
                var where = {};
                where[filterKey] = filterValue;
                data = _.where(data, where)
            }

            var layoutMetaColumn = _.findWhere(metaColumns, {type: '1'});//布局列
            var dimensionMetaColumn = _.findWhere(metaColumns, {type: '2'});//维度列
            var statisticalMetaColumn = _.findWhere(metaColumns, {type: '3'});//统计列
            if (!layoutMetaColumn || !dimensionMetaColumn || !statisticalMetaColumn) {
                return
            }

            layer.open({
                type: 1,
                title: reportCompose.name,
                content: $("#chartDataTableDlg"),
                btn: '关闭',
                area: ['510px', '500px'],
                btnAlign: 'c',
                yes: function () {
                    layer.closeAll();
                }
            });
            layui.use("table", function () {
                var table = layui.table;
                table.render({
                    elem: "#chartDataTable",
                    width: 480,
                    limit: 2000,
                    cols: [[
                        {field: layoutMetaColumn.name, title: layoutMetaColumn.text, width: 150},
                        {field: dimensionMetaColumn.name, title: dimensionMetaColumn.text, width: 150},
                        {field: statisticalMetaColumn.name, title: statisticalMetaColumn.text, width: 150}
                    ]],
                    data: data
                });
            });
        },

        /**
         * 出发指定参数加载选项
         * @param formId
         * @param triggerParamName
         * @param thisVal
         */
        triggerParamReloadSelectOption: function (formId, triggerParamName, thisVal) {
            console.log(formId, triggerParamName, thisVal)
            if (formId && triggerParamName) {//reportComposeUid
                var selectObj = $("#" + formId).find("select[name='" + triggerParamName + "']");
                if (selectObj.size() < 1) {
                    return;
                }
                var paramShare = reportObj.paramShare;//是否参数共享
                var queryParams = [];
                var reportComposeUid = null;
                if (paramShare == 1) {//共享参数
                    if (reportObj.queryParams) {
                        queryParams = JSON.parse(reportObj.queryParams);
                    }
                } else {
                    reportComposeUid = $("#" + formId).find("input[name='reportComposeUid']").val();//报表UID
                    if (reportComposeUid && PublishReportMVC.Variables.reportComposeMap[reportComposeUid]
                        && PublishReportMVC.Variables.reportComposeMap[reportComposeUid].queryParams) {
                        queryParams = JSON.parse(PublishReportMVC.Variables.reportComposeMap[reportComposeUid].queryParams);
                    }
                }

                if (queryParams && queryParams.length > 0) {//查找参数
                    var queryParam = _.findWhere(queryParams, {name: triggerParamName});//重新加载值得列
                    $(selectObj).empty();
                    if (thisVal) {//有值
                        var queryParam = _.findWhere(queryParams, {name: triggerParamName});//布局列
                        if (queryParam && (queryParam.formElement == 'select' || queryParam.formElement == 'selectMul')
                            && queryParam.dataSource == 'sql') {//是选择框、且来源是SQL则加载值
                            var finalParams = PublishReportMVC.Controller.getReportQueryFormParams(reportComposeUid);
                            finalParams["queryReportComposeUid"] = reportComposeUid;
                            finalParams["triggerParamName"] = triggerParamName;
                            $.ajax({
                                type: "POST",
                                url:PublishReportMVC.URLs.ReloadSelectParamOption.url + reportUid,
                                data: finalParams,
                                dataType: "json",
                                beforeSend: function () {
                                    //$.messager.progress({title: '请稍后...',text: '报表正在生成中...'});
                                },
                                success: function (result) {
                                    if (result.respCode =='100') {
                                        var respData = result.respData;
                                        var options = '';
                                        if (respData && respData.length > 0) {
                                            for (var i in respData) {
                                                options += '<option  value="' + respData[i].value + '">' + respData[i].text + '</option>';
                                            }
                                        }
                                        $(selectObj).append(options);

                                        if (queryParam.formElement == 'selectMul') {
                                            selectObj.trigger("chosen:updated")
                                        }
                                    } else {
                                        $.messager.alert('操作提示', result.respDesc, 'error');
                                    }
                                },
                                complete: function () {
                                    //$.messager.progress("close");
                                }
                            });
                        }
                    } else {
                        if (queryParam.formElement == 'selectMul') {
                            selectObj.trigger("chosen:updated")
                        }
                    }
                }
            }
        },
        /**
         * 下载文件
         */
        exportToFile: function (composeUid, fileType, fileCharset) {
            PublishReportMVC.Variables.queryReportComposeUid = null;
            if (composeUid) {
                PublishReportMVC.Variables.queryReportComposeUid = composeUid;
            }
            PublishReportMVC.Variables.queryReportComposeUid = composeUid;
            var params = PublishReportMVC.Util.getLoadDataReportFormParams();
            params['uid'] = reportUid;
            params["fileType"] = fileType;
            params["fileCharset"] = fileCharset;
            var paramStr = JSON.stringify(params);
            console.log(paramStr)

            var layerLoad = layer.load(1, {shade: [0.3, '#ccc']});

            var url = PublishReportMVC.URLs.ExportToFile.url + reportUid;
            $.fileDownload(url, {
                httpMethod: "POST",
                data: {paramBody: paramStr},
                successCallback: function () {
                    console.info("====success")
                    layer.close(layerLoad);
                },
                failCallback: function () {
                    console.info("====fail")
                    layer.close(layerLoad);
                }
            })
        }

    },
    /**
     * 数据
     */
    Data: {
        /**
         * 加载页面数据
         */
        loadReportData: function () {
            var params = PublishReportMVC.Util.getLoadDataReportFormParams();
            params['uid'] = reportUid;
            console.log('提交查询参数', params)
            var paramStr = JSON.stringify(params);
            var layerLoad = layer.load(1, {shade: [0.3, '#ccc']});
            $.ajax({
                type: "POST",
                async: false,
                //data: params,
                data: {paramBody: paramStr},
                url: PublishReportMVC.URLs.GetReportData.url+reportUid,
                //url: ReportHelper.ctxPath + '/lakalaReport/getReportData' ,
                //contentType : 'application/json;charset=UTF-8',
                success: function (respData) {
                    if (respData.respCode !='100') {
                        layer.close(layerLoad);
                        layer.msg(respData.msg, {icon: 2, time: 4000})
                        return;
                    } else {
                        var data = respData.respData;
                        _.each(data, function (v, cuid) {
                            PublishReportMVC.Variables.reportQueryData[cuid] = v;
                            var reportCompose = PublishReportMVC.Variables.reportComposeMap[cuid];
                            var showContent = PublishReportMVC.Variables.reportComposeOptions[cuid].showContent;

                            if (v.explain) {
                                PublishReportMVC.Util.addReportExplainHtml(cuid, v.explain);//增加报表说明
                            }

                            if (showContent == 1 || showContent == 4) {
                                var divId = PublishReportMVC.Util.getReportComposeTableDivId(cuid)
                                $("#" + divId).empty();
                                $("#" + divId).append(v.tableHtml);

                                if(showContent == 1 ){
                                    PublishReportMVC.Util.initTablePageInfo(cuid,v.count,v.pageSize);
                                    PublishReportMVC.Util.initTableGrid(cuid);
                                }else{
                                    PublishReportMVC.Util.initPivotTable(cuid);
                                }
                            }  else if (_.indexOf(PublishReportMVC.Variables.showContentChartVals, showContent) != -1) {
                                PublishReportMVC.Util.setReportComposeChartHeight(cuid);
                                PublishReportChartMVC.showReportComposeChart(reportCompose, showContent, v.data);
                            } else if (_.indexOf(PublishReportMVC.Variables.showContentBoardChartVals, showContent) != -1) {
                                PublishReportChartMVC.showReportComposeBoardChart(reportCompose, showContent, v.data);
                            }
                        })
                        PublishReportMVC.Util.setIsChangeFormVal(PublishReportMVC.Variables.curPageTableComposeUid, false);
                        PublishReportMVC.Variables.curPageTableComposeUid = null;
                        //PublishReportMVC.Variables.reportFormQueryParam[composeUid].pageInfo = null;
                    }
                    layer.close(layerLoad);
                }
            })
        }
    },
    /**
     * 工具方法
     */
    Util: {
        /**
         * 获得操作的报表ID
         * @param cuid
         * @returns {*}
         */
        getOptReportUid: function (cuid) {
            if (cuid) {
                return cuid;
            }
            return reportCommonSuffix;
        },
        /**
         * 获取报表组成最顶层ID
         * @param cuid
         * @returns {string}
         */
        getReportComposeId: function (cuid) {
            return "report-compose-" + cuid;
        },
        /**
         * 获取报表数据展示部分ID  里面装在数据表格的列表和分页信息、图表
         * @param cuid
         * @returns {string}
         */
        getReportComposeShowId: function (cuid) {
            return "report-compose-show-" + cuid;
        },
        /**
         * 获取数据表格ID
         * @param cuid
         * @returns {string}
         */
        getReportComposeTableDivId: function (cuid) {
            return "report-compose-table-html-" + cuid;
        },
        /**
         * 获取图表ID
         * @param cuid
         * @returns {string}
         */
        getReportComposeChartDivId: function (cuid) {
            return "report-compose-chart-" + cuid;
        },
        /**
         * 获取透视表ID
         * @param cuid
         * @returns {string}
         */
        getReportComposePivotTableDivId: function (cuid) {
            return "report-compose-pivot-table-" + cuid;
        },
        /**
         * 获取报表分页div ID
         * @param cuid
         * @returns {string}
         */
        getReportComposePageDivId: function (cuid) {
            return "report-compose-page-" + cuid;
        },
        /**
         * 获取报表说明ID
         * @param cuid
         * @returns {string}
         */
        getReportComposeExplainDivId: function (cuid) {
            return "report-compose-explain-" + cuid;
        },
        /**
         * 获取报表查询DIV ID
         * @param cuid
         * @returns {string}
         */
        getReportParamDivId: function (cuid) {
            return "param-div-" + cuid;
        },
        /**
         * 获取报表查询表单ID
         * @param cuid
         * @returns {string}
         */
        getReportParamFormId: function (cuid) {
            return "param-form-" + cuid;
        },
        /**
         * 获取透视表设置的表单ID
         * @param cuid
         * @returns {string}
         */
        getReportPivotTableParamFormId: function (cuid) {
            return 'pivot-table-' + PublishReportMVC.Util.getReportParamFormId(cuid);
        },

        /**
         * 获取展示数据区域的高度
         * @param composeUid
         */
        getShowDataAreaHeight:function(composeUid){
            //计算高度
            var reportHeight = PublishReportMVC.Variables.reportComposeOptions[composeUid].reportHeight;
            if (PublishReportMVC.Variables.reportComposeOptions[composeUid].heightUnit == 'px') {
                reportHeight = parseInt(reportHeight) - 60;//减60已经很准确  请别乱动啊
            }
            //检查是否有报表参数、有的话减去高度
            var paramDiv = $("#" + PublishReportMVC.Util.getReportParamDivId(composeUid));
            if (paramDiv.size() == 1) {
                var hasClassOverflowHidden=paramDiv.hasClass("overflow-hidden");
                if(!hasClassOverflowHidden){
                    paramDiv.addClass("overflow-hidden");
                }
                var paramHeight = parseInt(paramDiv.css("height"));//参数div需要先设置overflow-hidden，把高度计算出现再删除属性，不删除选择框会有隐藏
                reportHeight = reportHeight - paramHeight;
                console.log("参数高度："+paramHeight)
                if(!hasClassOverflowHidden){
                    paramDiv.removeClass("overflow-hidden");
                }
            }

            //检查是否有报表注释、有的话减去高度
            var explainDiv = $("#" + PublishReportMVC.Util.getReportComposeExplainDivId(composeUid));
            if (explainDiv.size() == 1) {
                var explainHeight = parseInt(explainDiv.css("height"));
                console.log("报表注释高度："+paramHeight)
                reportHeight = reportHeight - explainHeight;
            }
            //是否是透视表，是透视表进去透视表参数部分高度

            var minHeight = 200
            if (reportHeight < minHeight) {
                reportHeight = minHeight;
                console.log("计算出数据表格高度" + reportHeight + "小于" + minHeight + "，置为" + minHeight)
            }
            console.log(composeUid+"计算出数据表格高度:" + reportHeight)
            return reportHeight;
        },
        /**
         * 实例化数据表格
         * @param composeUid
         */
        initTableGrid: function (composeUid) {
            var reportHeight=PublishReportMVC.Util.getShowDataAreaHeight(composeUid);
            //console.log("数据表格高度:"+reportHeight)
            var layFilter = 'static-table-' + composeUid;
            layui.use('table', function () {
                var table = layui.table;
                table.init(layFilter, {
                    tbodyIsSetField: true,
                    height: reportHeight,
                    page: false,
                    limit: 999999999
                })
            });
        },
        initPivotTable:function(composeUid){
            var reportHeight=PublishReportMVC.Util.getShowDataAreaHeight(composeUid);
            var layFilter = 'static-table-' + composeUid;
            layui.use('table', function(){
                var table = layui.table;
                table.init(layFilter,{
                    height: reportHeight,
                    page:false,
                    limit:500000
                });
            });
        },
        /**
         * 实例化分页信息
         */
        initTablePageInfo: function (composeUid, count,pageSize) {
            if (PublishReportMVC.Variables.reportFormQueryParam[composeUid].isChangeFormParam) {
                layui.use('laypage', function () {
                    var limit = 10;
                    if(pageSize > 0){
                        limit=pageSize;
                    }else if (PublishReportMVC.Variables.reportFormQueryParam[composeUid].pageInfo
                        && PublishReportMVC.Variables.reportFormQueryParam[composeUid].pageInfo.limit > 0) {
                        limit = PublishReportMVC.Variables.reportFormQueryParam[composeUid].pageInfo.limit;
                    }

                    var laypage = layui.laypage;
                    var divId = PublishReportMVC.Util.getReportComposePageDivId(composeUid)
                    laypage.render({
                        elem: divId,
                        count: count,
                        limit: limit,
                        groups: 3, //连续出现的页码个数
                        layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
                        limits: [10, 30, 50, 100],
                        jump: function (obj, isFirst) {
                            console.log(obj, isFirst)
                            PublishReportMVC.Variables.queryReportComposeUid = obj.elem.substring(20);
                            console.log("是否首次：" + (isFirst == true))
                            if (!isFirst) {
                                console.log("改变分页");
                                PublishReportMVC.Variables.reportFormQueryParam[composeUid].pageInfo = obj;
                                //PublishReportMVC.Variables.reportFormQueryParam.enablePage = 1;

                                PublishReportMVC.Util.addPageParams(PublishReportMVC.Variables.queryReportComposeUid);

                                PublishReportMVC.Variables.queryReportComposeUid = composeUid;

                                PublishReportMVC.Data.loadReportData();
                            }
                            //加载表格数据
                            return false;
                        }
                    })
                });
            }
        },
        filterQueryFormParam: function (params) {
            //过滤掉值为空的key
            var returnParam = {};
            if (params) {
                _.each(params, function (value, key) {
                    value = $.trim(value);
                    if (value && (value + "").length > 0) {
                        returnParam[key] = value;
                    }
                })
            }
            return returnParam;
        },
        /**
         * 增加分页参数
         */
        addPageParams: function (cuid) {
            if (cuid) {
                var pageInfo = PublishReportMVC.Variables.reportFormQueryParam[cuid].pageInfo;
                if (pageInfo && pageInfo.curr > 0) {
                    PublishReportMVC.Variables.reportFormQueryParam[cuid]["formParam"]["pageSize"] = PublishReportMVC.Variables.reportFormQueryParam[cuid].pageInfo.limit;
                    PublishReportMVC.Variables.reportFormQueryParam[cuid]["formParam"]["pageIndex"] = PublishReportMVC.Variables.reportFormQueryParam[cuid].pageInfo.curr;
                } else {
                    PublishReportMVC.Variables.reportFormQueryParam[cuid]["formParam"]["pageSize"] = 10;
                    PublishReportMVC.Variables.reportFormQueryParam[cuid]["formParam"]["pageIndex"] = 1;
                }
            }
        },
        /**
         * 设置每一个报表的参数的isChangeForm值
         */
        setIsChangeFormVal: function (composeUid, isChangeForm) {
            if (composeUid) {
                PublishReportMVC.Variables.reportFormQueryParam[composeUid]["isChangeFormParam"] = isChangeForm;
            } else {
                var reportComposeList = reportObj.reportComposeList;
                if (reportComposeList) {
                    _.each(reportComposeList, function (reportCompose) {
                        PublishReportMVC.Variables.reportFormQueryParam[reportCompose.uid]["isChangeFormParam"] = isChangeForm;
                    });
                }
            }
        },
        /**
         * 增加报表说明
         * @param cuid
         * @param explainHtmlTmp 如果不为空则使用该参数的说明内容。否则使用报表设置的参数explainHtml
         */
        addReportExplainHtml: function (cuid, explainHtmlTmp) {
            var reportCompose = PublishReportMVC.Variables.reportComposeMap[cuid]
            if (!reportCompose) {
                return
            }
            var explains = reportCompose.reportExplain ? JSON.parse(reportCompose.reportExplain) : {};
            var position = explains.position;
            var explainContent = '';
            if (explainHtmlTmp) {
                explainContent = explainHtmlTmp;
            } else if (explains.explainHtml) {//有报表说明、添加说明内容
                explainContent = explains.explainHtml;
            }
            if (explainContent) {
                var explainDivId = PublishReportMVC.Util.getReportComposeExplainDivId(cuid);
                $("#" + explainDivId).remove();
                var composeShowId = PublishReportMVC.Util.getReportComposeShowId(cuid);
                var explainContent = '<div class="pt5 pb5 pb2 cl-b" id="' + explainDivId + '">' + explainContent + '</div>';
                if (position == "bottom") {
                    $("#" + composeShowId).after(explainContent);
                } else {//顶部
                    $("#" + composeShowId).before(explainContent);
                }
            }
        },
        /**
         * 创建图表div
         * @param cuid
         */
        createReportComposeChartDiv: function (composeUid) {
            var chartId = PublishReportMVC.Util.getReportComposeChartDivId(composeUid);
            var composeShowId = PublishReportMVC.Util.getReportComposeShowId(composeUid);
            if($("#" + chartId).size()<1){
                $("#" + composeShowId).append('<div class="pt10 pr2 pb10 pl2"><div id="' + chartId + '"></div></div>');
            }
            PublishReportMVC.Util.setReportComposeChartHeight(composeUid);
        },
        /**
         * 设置图表显示高度
         * @param composeUid
         */
        setReportComposeChartHeight:function(composeUid){
            var chartId = PublishReportMVC.Util.getReportComposeChartDivId(composeUid);
            var reportHeight=PublishReportMVC.Util.getShowDataAreaHeight(composeUid);
            $("#" + chartId).css({height: reportHeight + 'px'});
        },
        /**
         * 创建画板展示DIV
         * @param composeUid
         */
        createReportComposeBoardChartDiv: function (composeUid) {
            var composeShowId = PublishReportMVC.Util.getReportComposeShowId(composeUid);
            var chartId = PublishReportMVC.Util.getReportComposeChartDivId(composeUid);
            $("#" + composeShowId).append('<div id="' + chartId + '"></div>');
        },
        /**
         * 创建透视表
         * @param composeUid
         */
        createReportPivotTableDiv: function (composeUid, reportCompose) {
            var composeShowId = PublishReportMVC.Util.getReportComposeShowId(composeUid);
            var pivotTableId = PublishReportMVC.Util.getReportComposePivotTableDivId(composeUid);
            var paramFormId = PublishReportMVC.Util.getReportParamFormId(composeUid);
            var paramDivId=PublishReportMVC.Util.getReportParamDivId(composeUid)
            $("#" +paramDivId ).show();
            var paramShare = reportObj.paramShare;
            //如果参数共用 则隐藏查询按钮
            if (paramShare == 1) {
                $("#" + paramDivId).find(".report-compose-btn-group").hide();
                $("#" + paramDivId).removeClass("overflow-hidden");
            }

            var pivotTableParamFormId = PublishReportMVC.Util.getReportPivotTableParamFormId(composeUid);

            $("#" + paramFormId).after('<form id="' + pivotTableParamFormId + '" class="cl-b"></form>');

            $("#" + composeShowId).append('<div id="' + pivotTableId + '"></div>');
            var metaColumns = JSON.parse(reportCompose.metaColumns);

            $("#" + composeShowId).append('<div id="' + PublishReportMVC.Util.getReportComposeTableDivId(composeUid) + '"></div>');

            //创建透视表行、列、值
            var pivotTableRowValue = [];
            _.each(metaColumns, function (item, index) {
                var obj = {value: item.name, text: item.text};
                pivotTableRowValue.push(obj);
            })
            console.log(pivotTableRowValue)
            var pivotTableRow = {name: 'pivotTableRow', text: '透视表-行', optionList: pivotTableRowValue, multiple: true};
            queryFormUtils.createComboboxDom(pivotTableParamFormId, pivotTableRow);
            var pivotTableCol = {name: 'pivotTableColumn', text: '透视表-列', optionList: pivotTableRowValue};
            queryFormUtils.createComboboxDom(pivotTableParamFormId, pivotTableCol);
            var pivotTableVal = {name: 'pivotTableValue', text: '透视表-值', optionList: pivotTableRowValue, nameWidth: 65};
            queryFormUtils.createComboboxDom(pivotTableParamFormId, pivotTableVal);
            //增加值选项
            var pivotTableValCalFunValue = [
                {text: '求和', value: 'SUM'},
                {text: '平均', value: 'AVG'},
                {text: '统计', value: 'COUNT'},
                {text: '最大', value: 'MAX'},
                {text: '最小', value: 'MIN'},
            ];
            var options = '';
            _.each(pivotTableValCalFunValue, function (value) {
                options += '<option  value="' + value.value + '">' + value.text + '</option>';
            })
            $("#" + pivotTableParamFormId).find("select[name='pivotTableValue']").after('<select class="br-none pl5 wi55 h25" name="pivotTableAggfunc" style="border-left: 1px solid #e6e6e6" >' + options + '</select>');
        },
        /**
         * 获取加载数据的参数
         * @returns {{}}
         */
        getLoadDataReportFormParams: function () {
            var reportComposeList = reportObj.reportComposeList;
            var reportComposeUidTmpl = PublishReportMVC.Variables.queryReportComposeUid;//查询指定报表
            var respParams = {};
            //公共参数都返回
            var commonFormParam = PublishReportMVC.Variables.reportFormQueryParam[reportCommonSuffix].formParam;
            respParams[reportCommonSuffix] = commonFormParam;
            var paramShare = reportObj.paramShare;
            //报表共享参数
            if (paramShare == 1) {
                if (reportComposeUidTmpl) {//如果参数共享且加载特定的报表
                    respParams["queryReportComposeUid"] = reportComposeUidTmpl;
                    var composeFormParam = PublishReportMVC.Variables.reportFormQueryParam[reportComposeUidTmpl].formParam;
                    respParams[reportCommonSuffix] = _.extend(commonFormParam, composeFormParam);
                }
            } else {
                //报表参数不共用
                if (reportComposeUidTmpl) {//如果查询指定报表的查询参数
                    var composeFormParam = PublishReportMVC.Variables.reportFormQueryParam[reportComposeUidTmpl].formParam;
                    respParams[reportComposeUidTmpl] = _.extend(_.clone(commonFormParam), composeFormParam);
                    respParams["queryReportComposeUid"] = reportComposeUidTmpl;
                } else {//返回全部报表的参数
                    if (reportComposeList) {
                        _.each(reportComposeList, function (reportCompose) {
                            var uid = reportCompose.uid;
                            var composeFormParam = PublishReportMVC.Variables.reportFormQueryParam[uid].formParam;
                            respParams[uid] = _.extend(_.clone(commonFormParam), composeFormParam);
                        });
                    }
                }
            }

            var pivotTableComposeUid=reportComposeUidTmpl? reportComposeUidTmpl : reportCommonSuffix ;
            var pivotTableParamObj=PublishReportMVC.Controller.getPivotTableFormParams(pivotTableComposeUid);
            console.log('透视表参数',pivotTableComposeUid,pivotTableParamObj);
            _.each(pivotTableParamObj,function (pivotTableParam, pivotTableUid) {
                //需校验数据完整性
                if(!PublishReportMVC.Variables.isInitPageLoadData){
                    if(!pivotTableParam.pivotTableRow){
                        if(PublishReportMVC.Variables.zuiMessager){
                            PublishReportMVC.Variables.zuiMessager.destroy();
                        }
                        PublishReportMVC.Variables.zuiMessager=new $.zui.Messager("透视表-行未选择",{type:'danger'});
                        PublishReportMVC.Variables.zuiMessager.show();
                        throw "透视表-行未选择";
                    }
                }
                if(!respParams[pivotTableUid]){
                    respParams[pivotTableUid]={};
                }
                respParams[pivotTableUid]['pivotTableParam']=pivotTableParam;
            })

            console.log("加载报表数据查询参数", respParams)
            return respParams;
        }
    }
}