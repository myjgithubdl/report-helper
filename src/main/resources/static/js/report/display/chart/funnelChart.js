$(function () {
    FunnelChartManage.init();
});

var FunnelChartManage = {
    init: function () {
        FunnelChartManageMVC.View.initControl();
        FunnelChartManageMVC.View.bindEvent();
        FunnelChartManageMVC.View.bindValidate();
    }
};

var FunnelChartManageMVC = {
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
            FunnelChartManageMVC.Service.setReportMetaColumn();

            QueryParameterManageMVC.Controller.createReportQueryParameter('chart-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });

            FunnelChartManageMVC.Controller.searchChartData()


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
            FunnelChartManageMVC.Controller.loadTableData();
        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            FunnelChartManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);
            $.post(FunnelChartManageMVC.URLs.QueryReportChartData.url + uid, FunnelChartManageMVC.Variable.formQueryParam, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    console.log(respData)
                    FunnelChartManageMVC.Variable.reportData = respData.reportPageInfo.rows;

                    QueryParameterManageMVC.Controller.setLegendMetaColumnsValue(FunnelChartManageMVC.Variable.legendMetaColumns, FunnelChartManageMVC.Variable.reportData);

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

            var respData = ChartDataMVC.Service.getChartData(FunnelChartManageMVC.Variable.reportData, xName,
                yName, legendKeyArray[0], chartOptionParams[legendKeyArray[0]]);
            console.log(respData);

            FunnelChartManageMVC.Controller.initFunnelChart(respData.xData, respData.legendData, respData.yData, chartOptionParams);

        },

        /**
         * 实例化漏斗图
         * @param xData
         * @param legendData
         * @param yData
         * @param chartOptionParams
         */
        initFunnelChart: function (xData, legendData, yData, chartOptionParams) {

            var seriesArray = [];

            _.each(yData, function (v, k) {
                var series = {
                    name: k,
                    type:'funnel',
                    label: {
                        show: true,
                        position: 'inside',
                        normal: {
                            formatter: '{b} : {c} ({d}%)',
                        }
                    },
                    labelLine: {
                        length: 10,
                        lineStyle: {
                            width: 1,
                            type: 'solid'
                        }
                    },
                    itemStyle: {
                        borderColor: '#fff',
                        borderWidth: 1
                    },
                    emphasis: {
                        label: {
                            fontSize: 20
                        }
                    },
                };
                var data = [];
                _.each(xData, function (xDataVal, xDataIndex) {
                    data.push({name: xDataVal, value: v[xDataIndex]})
                })
                series.data = data;
                seriesArray.push(series)

            })

            var option = {
                type:'funnel',
                left: '10%',
                top: 60,
                //x2: 80,
                bottom: 60,
                width: '80%',
                // height: {totalHeight} - y - y2,
                min: 0,
                max: 100,
                minSize: '0%',
                maxSize: '100%',
                sort: 'descending',
                gap: 2,
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                toolbox: {
                    feature: {
                        dataView: {readOnly: false},
                        restore: {},
                        saveAsImage: {}
                    }
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    top: '8%',
                    data: xData
                },
                series: seriesArray
            };

            // 基于准备好的dom，初始化echarts图表
            var myChart = echarts.init(document.getElementById('funnelChart'));

            // 为echarts对象加载数据
            myChart.setOption(option, {notMerge: true});
        }
    },
    Service: {
        /**
         * 设置列类型
         */
        setReportMetaColumn: function () {
            FunnelChartManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            FunnelChartManageMVC.Variable.xMetaColumns = _.filter(FunnelChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            FunnelChartManageMVC.Variable.yMetaColumns = _.filter(FunnelChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            FunnelChartManageMVC.Variable.legendMetaColumns = _.filter(FunnelChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            QueryParameterManageMVC.Controller.createRadioInputs("chart-x-div", FunnelChartManageMVC.Variable.xMetaColumns, 'xName');
            QueryParameterManageMVC.Controller.createSelects("chart-legend-div", FunnelChartManageMVC.Variable.legendMetaColumns, 'legendName');
            QueryParameterManageMVC.Controller.createRadioInputs("chart-y-div", FunnelChartManageMVC.Variable.yMetaColumns, 'yName');

        }
    }
}
