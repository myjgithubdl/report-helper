$(function () {
    PieChartManage.init();
});

var PieChartManage = {
    init: function () {
        PieChartManageMVC.View.initControl();
        PieChartManageMVC.View.bindEvent();
        PieChartManageMVC.View.bindValidate();
    }
};

var PieChartManageMVC = {
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
            PieChartManageMVC.Service.setReportMetaColumn();

            QueryParameterManageMVC.Controller.createReportQueryParameter('chart-report-params-form', uid, {
                selectParamChangeFunName: 'QueryParameterManageMVC.Controller.reloadSelectParamOption'
            });

            PieChartManageMVC.Controller.searchChartData()


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
            PieChartManageMVC.Controller.loadTableData();
        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            PieChartManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);
            $.post(PieChartManageMVC.URLs.QueryReportChartData.url + uid, PieChartManageMVC.Variable.formQueryParam, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    console.log(respData)
                    PieChartManageMVC.Variable.reportData = respData.reportPageInfo.rows;

                    QueryParameterManageMVC.Controller.setLegendMetaColumnsValue(PieChartManageMVC.Variable.legendMetaColumns, PieChartManageMVC.Variable.reportData);

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
            //设置列名对应的名称
            chartOptionParams.xNameText=_.where(PieChartManageMVC.Variable.xMetaColumns, {name: xName})[0].text
            chartOptionParams.yNameText=_.where(PieChartManageMVC.Variable.yMetaColumns, {name: yName})[0].text;


            var respData = ChartDataMVC.Service.getChartData(PieChartManageMVC.Variable.reportData, xName,
                yName, legendKeyArray[0], chartOptionParams[legendKeyArray[0]]);

            PieChartManageMVC.Controller.initPieChart(respData.xData, respData.legendData, respData.yData, chartOptionParams);

        },

        /**
         * 实例化折线图
         * @param xData
         * @param legendData
         * @param yData
         * @param chartOptionParams
         */
        initPieChart: function (xData, legendData, yData, chartOptionParams) {

            var pieChartNum = _.size(legendData);//饼图圆环个数
            var pieChartSizeAvg = parseInt(80 / (2 * pieChartNum - 1));//饼图圆环个数

            var seriesArray = [];

            var pieIndex = 0;
            _.each(yData, function (v, k) {
                var series = {
                    name: k,
                    type: 'pie',
                    radius: [(pieIndex * pieChartSizeAvg) + '%', ((pieIndex + 1) * pieChartSizeAvg) + '%']
                };
                var data = [];
                _.each(xData, function (xDataVal, xDataIndex) {
                    data.push({name: xDataVal, value: v[xDataIndex]})
                })
                series.data = data;
                seriesArray.push(series)
                pieIndex += 2;

            })

            console.log(seriesArray)


            var option = {

                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c} ({d}%)"
                },
                label: {
                    normal: {
                        //formatter: '{a|{a}}{abg|}\n{hr|}\n {b|{b} : }{c} {per|{d}%} ',
                        formatter: '{a|{a}}{abg|}\n{hr|}\n {b|{b} : }{c}{per|({d}%)}',
                        //backgroundColor: '#eee',
                        borderColor: '#aaa',
                        borderWidth: 1,
                        borderRadius: 4,
                        rich: {
                            a: {
                                //color: '#999',
                                lineHeight: 18,
                                align: 'center'
                            },
                            hr: {
                                borderColor: '#aaa',
                                width: '100%',
                                borderWidth: 0.5,
                                height: 0
                            },
                            b: {
                                //fontSize: 15,
                                lineHeight: 22
                            },
                            per: {
                                //color: '#eee',
                                //backgroundColor: '#334455',
                                padding: [2, 4],
                                borderRadius: 2
                            }
                        }
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
            var myChart = echarts.init(document.getElementById('pieChart'));

            // 为echarts对象加载数据
            myChart.setOption(option, {notMerge: true});
        }
    },
    Service: {
        /**
         * 设置列类型
         */
        setReportMetaColumn: function () {
            PieChartManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            PieChartManageMVC.Variable.xMetaColumns = _.filter(PieChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            PieChartManageMVC.Variable.yMetaColumns = _.filter(PieChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            PieChartManageMVC.Variable.legendMetaColumns = _.filter(PieChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            QueryParameterManageMVC.Controller.createRadioInputs("chart-x-div", PieChartManageMVC.Variable.xMetaColumns, 'xName');
            QueryParameterManageMVC.Controller.createSelects("chart-legend-div", PieChartManageMVC.Variable.legendMetaColumns, 'legendName');
            QueryParameterManageMVC.Controller.createRadioInputs("chart-y-div", PieChartManageMVC.Variable.yMetaColumns, 'yName');

        }
    }
}
