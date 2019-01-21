$(function () {
    PreviewManage.init();
});


var PreviewManage = {
    init: function () {
        PreviewManageMVC.View.initControl();
        PreviewManageMVC.View.bindEvent();
        PreviewManageMVC.View.bindValidate();
    }
};


var PreviewManageMVC = {
    Variable: {
        queryFormSelectParam: {},//选择框参数
        formQueryParam: {},//查询表单参数
        isChangeFormQueryParam: true,//是否改变了表单
        pageInfo: {pageIndex: 1, totalRows: -1}  //分页信息,totalRows值为-1或空在启用份额与的情况下触发查询总数
    },
    URLs: {
        QueryReportTableData: {
            url: ReportHelper.ctxPath + '/report/preview/queryReportTableData/uid/',
            method: 'post'
        },
        ExportReportTableData: {
            url: ReportHelper.ctxPath + '/report/preview/exportReportTableData/uid/',
            method: 'post'
        },

    },
    View: {
        /**
         * 初始化操作
         */
        initControl: function () {

            QueryParameterManageMVC.Controller.createReportQueryParameter('table-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });


        },

        /**
         * 绑定事件
         */
        bindEvent: function () {


        },
        bindValidate: function () {

        }
    },
    Controller: {
        /**
         * 搜索数据
         */
        searchTableData: function () {
            PreviewManageMVC.Variable.isChangeFormQueryParam = true;

            PreviewManageMVC.Variable.pageInfo.pageIndex = 1;
            PreviewManageMVC.Variable.pageInfo.totalRows = "-1";


            PreviewManageMVC.Controller.loadTableData();

        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            var params = PreviewManageMVC.Controller.getQueryParams();
            $.post(PreviewManageMVC.URLs.QueryReportTableData.url + uid, params, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    var htmlTable = respData.htmlTable;
                    var reportExplain = respData.reportExplain;
                    PreviewManageMVC.Variable.pageInfo = respData.reportPageInfo;

                    $("#table-report-table-data-div").empty();
                    $("#table-report-table-data-div").html(htmlTable);
                    $("#static-table").hide();

                    PreviewManageMVC.Service.setReportExplain(reportExplain);
                    PreviewManageMVC.Service.reInitLayuiTable();
                    PreviewManageMVC.Service.initTablePageInfo();

                    PreviewManageMVC.Variable.isChangeFormQueryParam = false;
                }

                console.log(result)
            }, 'json');

        },
        clearQueryParams: function () {


        },
        /**
         * 获取查询表单的参数
         */
        getQueryParams: function () {

            PreviewManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);

            //增加分页信息
            PreviewManageMVC.Variable.formQueryParam.pageSize = PreviewManageMVC.Variable.pageInfo.pageSize;
            PreviewManageMVC.Variable.formQueryParam.pageIndex = PreviewManageMVC.Variable.pageInfo.pageIndex;
            //为null  或者小于0都会出发查询总数操作
            PreviewManageMVC.Variable.formQueryParam.totalRows = PreviewManageMVC.Variable.pageInfo.totalRows;
            return PreviewManageMVC.Variable.formQueryParam;
        },
        /**
         * 导出表格数据
         * @param exportFileType
         */
        exportReportData: function (exportFileType) {
            var params = PreviewManageMVC.Controller.getQueryParams();
            params.exportFileType = exportFileType;
            //params.charsetName = 'UTF-8';office乱码
            params.charsetName = 'GB2312';
            console.log(params)

            var url = PreviewManageMVC.URLs.ExportReportTableData.url + uid;
            $("#exportDataBtnGroup").addClass("disabled ");
            $.fileDownload(url, {
                httpMethod: "POST",
                data: params,
                successCallback: function () {
                    $("#exportDataBtnGroup").removeClass("disabled ");
                },
                failCallback: function () {
                    $("#exportDataBtnGroup").removeClass("disabled ");
                }
            })
        }

    },
    Service: {

        /**
         * 实例化数据表格为layui table
         */
        reInitLayuiTable: function () {
            var formHeight = $("#table-report-params-div").height();
            var reportExplainHeight = $(".report-explain").height();
            console.log(formHeight)
            layui.use('table', function () {
                var table = layui.table;
                table.init('static-table', {
                    tbodyIsSetField: true,
                    height: 'full-' + (formHeight + reportExplainHeight + 80),
                    page: false,
                    groups: 3,//连续出现的页码个数
                    limit: 999999999,
                    isSetTbodyTdFieldName: true
                })
            });
        },
        /**
         * 实例化分页信息
         */
        initTablePageInfo: function () {
            if (PreviewManageMVC.Variable.pageInfo.enablePage
                && PreviewManageMVC.Variable.isChangeFormQueryParam) {
                console.log("初始化分页");
                $("#table-report-table-data-page-div").show();
                layui.use('laypage', function () {
                    var limit = 10;
                    if (PreviewManageMVC.Variable.pageInfo && PreviewManageMVC.Variable.pageInfo.pageSize > 0) {
                        limit = PreviewManageMVC.Variable.pageInfo.pageSize;
                    }

                    var laypage = layui.laypage;
                    laypage.render({
                        elem: 'table-report-table-data-page-div',
                        count: PreviewManageMVC.Variable.pageInfo.totalRows,
                        limit: limit,
                        layout: ['count', 'prev', 'page', 'next', 'limit', 'skip'],
                        limits: [10, 30, 50, 100],
                        jump: function (obj, isFirst) {
                            console.log("是否首次改变分页：" + (isFirst == true));
                            console.log(obj);//curr
                            if (!isFirst) {
                                PreviewManageMVC.Variable.pageInfo.pageIndex = obj.curr;
                                PreviewManageMVC.Variable.pageInfo.pageSize = obj.limit;

                                PreviewManageMVC.Controller.loadTableData();
                            }
                            //加载表格数据
                            return false;
                        }
                    })
                });
            }
        },
        /**
         * 设置报表提示内容
         * @param reportExplain
         */
        setReportExplain: function (reportExplain) {
            $(".report-explain").remove();
            if (reportExplain && reportExplain.html) {
                var position = reportExplain.position;
                var content = '<div class="report-explain" style="padding: 1px 10px;">' + reportExplain.html + '</div>';
                if (position == 'bottom') {
                    $("#table-report-data-div").append(content);
                } else {
                    $("#table-report-table-data-div").before(content);
                }
            }
        }

    }

}
