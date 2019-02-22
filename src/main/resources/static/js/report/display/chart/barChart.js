$(function () {
    BarChartManage.init();
});

var BarChartManage = {
    init: function () {
        BarChartManageMVC.View.initControl();
        BarChartManageMVC.View.bindEvent();
        BarChartManageMVC.View.bindValidate();
    }
};

var BarChartManageMVC = {
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
            BarChartManageMVC.Service.setReportMetaColumn();

            QueryParameterManageMVC.Controller.createReportQueryParameter('chart-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });

            BarChartManageMVC.Controller.searchChartData()


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
            BarChartManageMVC.Controller.loadTableData();
        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            BarChartManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);
            $.post(BarChartManageMVC.URLs.QueryReportChartData.url + uid, BarChartManageMVC.Variable.formQueryParam, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    console.log(respData)
                    BarChartManageMVC.Variable.reportData = respData.reportPageInfo.rows;

                    QueryParameterManageMVC.Controller.setLegendMetaColumnsValue(BarChartManageMVC.Variable.legendMetaColumns, BarChartManageMVC.Variable.reportData);

                }

                console.log(result)
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

            var respData = ChartDataMVC.Service.getChartData(BarChartManageMVC.Variable.reportData, xName,
                yName, legendKeyArray[0], chartOptionParams[legendKeyArray[0]]);


            chartOptionParams.xNameText=_.where(BarChartManageMVC.Variable.xMetaColumns, {name: xName})[0].text
            chartOptionParams.yNameText=_.where(BarChartManageMVC.Variable.yMetaColumns, {name: yName})[0].text;

            BarChartManageMVC.Controller.initBarChart(respData.xData, respData.legendData, respData.yData, chartOptionParams);

        },

        /**
         * 实例化折线图
         * @param xData
         * @param legendData
         * @param yData
         * @param chartOptionParams
         */
        initBarChart: function (xData, legendData, yData, chartOptionParams) {

            var series = _.map(yData, function (val, key) {
                return {
                    name: key,
                    type: 'bar',
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
                    //boundaryGap: false,//坐标轴两边留白策略，类目轴和非类目轴的设置和表现不一样。
                    data: xData
                },
                yAxis: {
                    type: 'value',
                    name: chartOptionParams.yNameText
                },
                series: series
            };

            // 基于准备好的dom，初始化echarts图表
            var myChart = echarts.init(document.getElementById('barChart'));

            // 为echarts对象加载数据
            myChart.setOption(option, {notMerge: true});
        }
    },
    Service: {
        /**
         * 设置列类型
         */
        setReportMetaColumn: function () {
            BarChartManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            BarChartManageMVC.Variable.xMetaColumns = _.filter(BarChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            BarChartManageMVC.Variable.yMetaColumns = _.filter(BarChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            BarChartManageMVC.Variable.legendMetaColumns = _.filter(BarChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            QueryParameterManageMVC.Controller.createRadioInputs("chart-x-div", BarChartManageMVC.Variable.xMetaColumns,'xName');
            QueryParameterManageMVC.Controller.createSelects("chart-legend-div", BarChartManageMVC.Variable.legendMetaColumns,'legendName');
            QueryParameterManageMVC.Controller.createRadioInputs("chart-y-div", BarChartManageMVC.Variable.yMetaColumns,'yName');

        }
    }
}
