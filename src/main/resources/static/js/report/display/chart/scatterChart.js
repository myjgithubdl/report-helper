$(function () {
    ScatterChartManage.init();
});

var ScatterChartManage = {
    init: function () {
        ScatterChartManageMVC.View.initControl();
        ScatterChartManageMVC.View.bindEvent();
        ScatterChartManageMVC.View.bindValidate();
    }
};

var ScatterChartManageMVC = {
    Variable: {
        formQueryParam: {},//查询表单参数
        metaColumns: [],//报表所有元数据列
        xMetaColumns: [],//x轴的列，对应于元数据列的布局列
        yMetaColumns: [],//y轴的列，对应于元数据列的统计列
        legendMetaColumns: [],//图例，对应于元数据列的维度列
        reportData: [],//报表返回的数据
    },
    URLs: {
        QueryReportChartData: {
            url: ReportHelper.ctxPath + '/report/preview/queryReportChartData/uid/',
            method: 'post'
        },
    },
    View: {
        /**
         * 初始化操作
         */
        initControl: function () {
            ScatterChartManageMVC.Service.setReportMetaColumn();

            QueryParameterManageMVC.Controller.createReportQueryParameter('table-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });

            ScatterChartManageMVC.Controller.searchChartData()


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
         * 查询图表数据
         */
        searchChartData: function () {
            ScatterChartManageMVC.Controller.loadTableData();
        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            ScatterChartManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);
            $.post(ScatterChartManageMVC.URLs.QueryReportChartData.url + uid, ScatterChartManageMVC.Variable.formQueryParam, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    ScatterChartManageMVC.Variable.reportData = respData.reportPageInfo.rows;

                    QueryParameterManageMVC.Controller.setLegendMetaColumnsValue(ScatterChartManageMVC.Variable.legendMetaColumns, ScatterChartManageMVC.Variable.reportData);
                }
            }, 'json');
        },
        /**
         * 创建图表
         */
        createChart: function () {
            //选择的所有值
            var chartOptionParams = QueryParameterManageMVC.Service.getQueryParams($("#chart-form"), true);
            //将选择的维度单选的值转为数组，方便后面处理
            chartOptionParams = _.mapObject(chartOptionParams, function (val, key) {
                if (key == 'xName' || key == 'yName' || _.isArray(val)) {
                    return val
                }
                return [val];
            });

            //选择值的图例的列名
            var legendKeyArray = _.filter(_.keys(chartOptionParams), function (keyName) {
                return _.indexOf(['xName', 'yName'], keyName) == -1;
            });

            if (_.isUndefined(chartOptionParams.xName)) {
                showZuiErrorMessager('请选择X轴');
                return false;
            }

            if (_.isUndefined(chartOptionParams.yName)) {
                showZuiErrorMessager('请选择Y轴');
                return false;
            }

            if (_.size(legendKeyArray) != 1) {
                showZuiErrorMessager('只能选择一个维度列的值');
                return false;
            }
            var xName = chartOptionParams.xName;//X轴的列名
            var yName = chartOptionParams.yName;//Y轴的列名
            //设置列名对应的名称
            chartOptionParams.xNameText=_.where(ScatterChartManageMVC.Variable.xMetaColumns, {name: xName})[0].text
            chartOptionParams.yNameText=_.where(ScatterChartManageMVC.Variable.yMetaColumns, {name: yName})[0].text;


            var respData = ChartDataMVC.Service.getChartData(ScatterChartManageMVC.Variable.reportData, xName,
                yName, legendKeyArray[0], chartOptionParams[legendKeyArray[0]]);

            ScatterChartManageMVC.Controller.initScatterChart(respData.xData, respData.legendData, respData.yData, chartOptionParams);
        },

        /**
         * 实例化折线图
         * @param xData
         * @param legendData
         * @param yData
         * @param chartOptionParams
         */
        initScatterChart: function (xData, legendData, yData, chartOptionParams) {
            var series = _.map(yData, function (val, key) {
                return {
                    name: key,
                    type: 'scatter',
                    data: val
                }
            });

            var option = {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    data: legendData
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: xData
                },
                yAxis: {
                    type: 'value',
                    name: chartOptionParams.yNameText
                },
                series: series
            };

            // 基于准备好的dom，初始化echarts图表
            var myChart = echarts.init(document.getElementById('scatterChart'));

            // 为echarts对象加载数据
            myChart.setOption(option, {notMerge: true});
        }

    },
    Service: {
        /**
         * 设置列类型
         */
        setReportMetaColumn: function () {
            ScatterChartManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            ScatterChartManageMVC.Variable.xMetaColumns = _.filter(ScatterChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            ScatterChartManageMVC.Variable.yMetaColumns = _.filter(ScatterChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            ScatterChartManageMVC.Variable.legendMetaColumns = _.filter(ScatterChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            QueryParameterManageMVC.Controller.createRadioInputs("chart-x-div", ScatterChartManageMVC.Variable.xMetaColumns,'xName');
            QueryParameterManageMVC.Controller.createSelects("chart-legend-div", ScatterChartManageMVC.Variable.legendMetaColumns,'legendName');
            QueryParameterManageMVC.Controller.createRadioInputs("chart-y-div", ScatterChartManageMVC.Variable.yMetaColumns,'yName');


        },
    }
}
