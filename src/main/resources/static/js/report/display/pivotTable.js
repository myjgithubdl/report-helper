$(function () {
    PivotTableManage.init();
});


var PivotTableManage = {
    init: function () {
        PivotTableManageMVC.View.initControl();
        PivotTableManageMVC.View.bindEvent();
        PivotTableManageMVC.View.bindValidate();
    }
};


var PivotTableManageMVC = {
    /**
     *变量
     */
    Variable: {
        formQueryParam: {},//查询表单参数
        metaColumns: [],//报表所有元数据列
        rowMetaColumns: [],//透视表中行的元数据列，对应于元数据列的布局列
        valMetaColumns: [],//透视表中值元数据列，对应于元数据列的统计列
        colMetaColumns: [],//透视表中列元数据列，对应于元数据列的维度列
        reportData: [],//报表返回的数据
    },
    URLs: {
        QueryReportTableData: {
            url: ReportHelper.ctxPath + '/report/preview/queryPivotTableData/uid/',
            method: 'post'
        },
        ExportPivotTableData: {
            url: ReportHelper.ctxPath + '/report/preview/exportPivotTableData/uid/',
            method: 'post'
        },

    },
    View: {
        /**
         * 初始化操作
         */
        initControl: function () {

            QueryParameterManageMVC.Controller.createReportQueryParameter('pivot-table-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });

            PivotTableManageMVC.Service.setReportMetaColumn();

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

            PivotTableManageMVC.Controller.loadTableData();

        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            var params = PivotTableManageMVC.Controller.getQueryParams();
            if(!params){
                return ;
            }

            $.post(PivotTableManageMVC.URLs.QueryReportTableData.url + uid, params, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    var htmlTable = respData.htmlTable;
                    var reportExplain = respData.reportExplain;
                    PivotTableManageMVC.Variable.pageInfo = respData.reportPageInfo;

                    $("#pivot-table-report-table-data-div").empty();
                    $("#pivot-table-report-table-data-div").html(htmlTable);
                    $("#static-table").hide();

                    PivotTableManageMVC.Service.setReportExplain(reportExplain);
                    PivotTableManageMVC.Service.reInitLayuiTable();

                }

                console.log(result)
            }, 'json');

        },
        /**
         * 获取查询表单的参数
         */
        getQueryParams: function () {

            PivotTableManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#pivot-table-report-params-form"), true);
            var pivotTableAttrForm=QueryParameterManageMVC.Service.getQueryParams($("#pivot-table-attr-form"), true);
            console.log(pivotTableAttrForm);

            if(!pivotTableAttrForm.rowColNames){
                showZuiErrorMessager("请选择行");
                return false;
            }
            PivotTableManageMVC.Variable.formQueryParam.rowColNames=pivotTableAttrForm.rowColNames;

            if(!pivotTableAttrForm.colColNames){
                showZuiErrorMessager("请选择列");
                return false;
            }
            PivotTableManageMVC.Variable.formQueryParam.colColNames=pivotTableAttrForm.colColNames;

            if(!pivotTableAttrForm.valColNames){
                showZuiErrorMessager("请选择值");
                return false;
            }
            PivotTableManageMVC.Variable.formQueryParam.valColNames=pivotTableAttrForm.valColNames;

            if(!pivotTableAttrForm.func){
                showZuiErrorMessager("请选择函数");
                return false;
            }
            PivotTableManageMVC.Variable.formQueryParam.func=pivotTableAttrForm.func;


            PivotTableManageMVC.Variable.formQueryParam


            return PivotTableManageMVC.Variable.formQueryParam;
        },
        /**
         * 导出表格数据
         * @param exportFileType
         */
        exportReportData: function (exportFileType) {
            var params = PivotTableManageMVC.Controller.getQueryParams();
            if(!params){
                return false;
            }
            params.exportFileType = exportFileType;
            //params.charsetName = 'UTF-8';office乱码
            params.charsetName = 'GB2312';
            console.log(params)

            var url = PivotTableManageMVC.URLs.ExportPivotTableData.url + uid;
            $("#exportDataBtnGroup").addClass("disabled");
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
         * 设置列类型
         */
        setReportMetaColumn: function () {
            PivotTableManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            PivotTableManageMVC.Variable.rowMetaColumns = _.filter(PivotTableManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            PivotTableManageMVC.Variable.valMetaColumns = _.filter(PivotTableManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            PivotTableManageMVC.Variable.colMetaColumns = _.filter(PivotTableManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            QueryParameterManageMVC.Controller.createCheckboxOptions("pivot-table-row-div", PivotTableManageMVC.Variable.rowMetaColumns,'rowColNames');
            QueryParameterManageMVC.Controller.createRadioInputs("pivot-table-col-div", PivotTableManageMVC.Variable.colMetaColumns,'colColNames');
            QueryParameterManageMVC.Controller.createRadioInputs("pivot-table-val-div", PivotTableManageMVC.Variable.valMetaColumns,'valColNames');

            //创建使用的函数
            QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','SUM','求和');
            QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','COUNT','计数');
            QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','AVG','均值');
            QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','MAX','最大值');
            QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','MIN','最小值');
            //QueryParameterManageMVC.Controller.createRadioInput('pivot-table-fun-div','func','PRODUCT','乘积');

        },
        /**
         * 实例化数据表格为layui table
         */
        reInitLayuiTable: function () {
            var formHeight = $("#pivot-table-report-params-div").height();
            var pivotTableAttrHeight = $("#pivot-table-attr-form").height();
            var reportExplainHeight = $(".report-explain").height();
            console.log(formHeight)
            layui.use('table', function () {
                var table = layui.table;
                table.init('static-table', {
                    tbodyIsSetField: true,
                    height: 'full-' + (formHeight +pivotTableAttrHeight + reportExplainHeight + 30),
                    page: false,
                    groups: 3,//连续出现的页码个数
                    limit: 999999999,
                    isSetTbodyTdFieldName: true
                })
            });
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
                    $("#pivot-table-report-table-data-div").after(content);
                } else {
                    $("#pivot-table-report-table-data-div").before(content);
                }
            }
        }
    }
}
