$(function () {
    LineChartManage.init();
});


var LineChartManage = {
    init: function () {
        LineChartManageMVC.View.initControl();
        LineChartManageMVC.View.bindEvent();
        LineChartManageMVC.View.bindValidate();
    }
};


var LineChartManageMVC = {
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
            LineChartManageMVC.Service.setReportMetaColumn();

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
         * 查询图表数据
         */
        searchChartData: function () {
            LineChartManageMVC.Controller.loadTableData();
        },
        /**
         * 加载表格数据
         */
        loadTableData: function () {
            LineChartManageMVC.Variable.formQueryParam = QueryParameterManageMVC.Service.getQueryParams($("#table-report-params-form"), true);
            $.post(LineChartManageMVC.URLs.QueryReportChartData.url + uid, LineChartManageMVC.Variable.formQueryParam, function (result) {
                if (result.respCode == '100') {

                    var respData = result.respData;
                    console.log(respData)
                    LineChartManageMVC.Variable.reportData = respData.reportPageInfo.rows;

                    LineChartManageMVC.Service.setLegendMetaColumnsValue();

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
            console.log(chartOptionParams)

            //选择值的图例的列名
            var legendKeyArray = _.filter(_.keys(chartOptionParams), function (keyName) {
                return _.indexOf(['xName', 'yName'], keyName) == -1;
            });


            var xName = chartOptionParams.xName;//X轴的列名
            var yName = chartOptionParams.yName;//Y轴的列名
            console.log("xName:" + xName);
            console.log("yName:" + yName);
            console.log("legendKeyArray:" + legendKeyArray);



            var reData =[];
            var allData=[];
            var prevLevelData=[];
            var prevLevelData=[];
            var prevLevelValKeys='';//上一级的value对应的key
            var curLevelValKeys='';
            var key='';

            //遍历选择的维度列
            _.each(legendKeyArray, function (legendKey,legendKeyIndex) {
                var legendKeyVal=chartOptionParams[legendKey];
                if(legendKeyIndex == 0){
                    prevLevelData =LineChartManageMVC.Variable.reportData;
                    key='';
                }



                var curLevelData=[];

                _.each(legendKeyVal,function (v,legendValIndex) {
                    curLevelValKeys =prevLevelValKeys.length >0 ?  prevLevelValKeys+'-'+v : v;


                    filterData=_.filter(prevLevelData,function (rowData) {
                        var mactObj={};
                        mactObj[legendKey]=v;
                        console.log(rowData , mactObj , _.isMatch(rowData, mactObj))
                        return _.isMatch(rowData, mactObj);
                        
                    });


                    curLevelData.push(filterData);
                    curLevelData=_.flatten(curLevelData);

                    if(legendValIndex == _.size(legendKeyVal) -1){
                        prevLevelData=curLevelData;
                        prevLevelValKeys=curLevelValKeys;
                    }

                    console.log(legendKeyIndex , legendKeyIndex == _.size(legendKeyArray) -1)
                    if(legendKeyIndex == _.size(legendKeyArray) -1 && _.size(filterData) > 0){
                        console.log("==",filterData);
                        var curObj={};
                        curObj[curLevelValKeys]=filterData;
                        allData.push(curObj)
                        console.log(curLevelValKeys)
                    }

                });



            })

            console.log(allData);
            allData=_.flatten(allData)
            console.log(allData)
            return;





            //根据选择的维度过略出数据
            var filterData = _.filter(filterData, function (data) {
                var newVal = {};
                //遍历选择的维度列
                _.each(legendKey, function (paramKey) {
                    //
                    newVal[paramKey] = data[paramKey];
                })
                console.log(data)
                console.log(newVal)
                return _.isEqual(data, newVal);

                if (_.size(legendKey) > 1) {
                    var newVal = {};
                    _.each(legendKey, function (paramKey) {
                        newVal[paramKey] = data[paramKey];
                    })
                    return _.isEqual(data, newVal);
                } else {
                    if (_.isArray(chartOptionParams[legendKey[0]])) {
                        return _.indexOf(chartOptionParams[legendKey[0]], data[legendKey[0]]) >= 0
                    } else {
                        return chartOptionParams[legendKey[0]] == data[legendKey[0]];
                    }
                }
            });

            console.log(filterData)
            return

            //X轴数据
            var xData = _.uniq(_.map(filterData, xName));
            var legendData = chartOptionParams[legendKey];
            console.log("xData------------" + xName + ":" + xData)

            var groupData = _.groupBy(filterData, legendKey);

            var yData = {};
            _.each(groupData, function (val, key) {
                console.log(val, key);
                var data = [];
                _.each(xData, function (x) {
                    var s = _.map([_.find(val, function (o) {
                        return o[xName] == x;
                    })], function (o) {
                        if (_.isUndefined(o[yName]) || _.isNull(o[yName])) {
                            return 0;
                        }
                        return o[yName];
                    });
                    data.push(s[0])
                });
                yData[key] = data
            })

            console.log(xData)
            console.log(legendData)
            console.log(yData)

            LineChartManageMVC.Controller.initLineChart(xData, legendData, yData);


        },
        initLineChart: function (xData, legendData, yData) {

            var series = _.map(yData, function (val, key) {
                return {
                    name: key,
                    type: 'line',
                    data: val
                }
            });

            console.log(series)


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
                    name: 'ss'
                },
                series: series
            };

            // 基于准备好的dom，初始化echarts图表
            var myChart = echarts.init(document.getElementById('main'));

            // 为echarts对象加载数据
            myChart.setOption(option);

        }

    },
    Service: {
        /**
         * 设置列类型
         */
        setReportMetaColumn: function () {
            LineChartManageMVC.Variable.metaColumns = JSON.parse(report.metaColumns);
            LineChartManageMVC.Variable.xMetaColumns = _.filter(LineChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 1;
            });
            LineChartManageMVC.Variable.yMetaColumns = _.filter(LineChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 3;
            });
            LineChartManageMVC.Variable.legendMetaColumns = _.filter(LineChartManageMVC.Variable.metaColumns, function (meta) {
                return meta.metaColumnType == 2;
            });

            console.log(LineChartManageMVC.Variable.xMetaColumns)
            console.log(LineChartManageMVC.Variable.yMetaColumns)
            console.log(LineChartManageMVC.Variable.legendMetaColumns)


            _.each(LineChartManageMVC.Variable.xMetaColumns, function (metaColumns) {
                var label = '<label class="radio-inline ml10 pt5"><input type="radio" name="xName" value="' + metaColumns.name + '">' + metaColumns.text + '</label>';
                $("#chart-x-div").append(label);
            });

            _.each(LineChartManageMVC.Variable.legendMetaColumns, function (metaColumn) {

                var dom = '<div class="fl mr10 legend-item"><div class="fl legend-label">' + metaColumn.text + '</div>';
                dom += '<div class="legend-value-div fl">';
                dom += '<select class="chosen-select form-control legend-value-select " id="legend-' + metaColumn.name + '" name="' + metaColumn.name + '" multiple="" '
                    + ' style="width:120px;"';
                dom += '></select>';
                dom += '</div></div>';


                //var label='<label class="radio-inline ml10 pt5"><input type="radio" name="legendName">'+metaColumns.text+'</label>';
                $("#chart-legend-div").append(dom);

                $('#legend-' + metaColumn.name).chosen({
                    no_results_text: '没有找到',    // 当检索时没有找到匹配项时显示的提示文本
                    disable_search_threshold: 10, // 10 个以下的选择项则不显示检索框
                    search_contains: true         // 从任意位置开始检索
                });

            });

            _.each(LineChartManageMVC.Variable.yMetaColumns, function (metaColumns) {
                var label = '<label class="radio-inline ml10 pt5"><input type="radio" name="yName" value="' + metaColumns.name + '">' + metaColumns.text + '</label>';
                $("#chart-y-div").append(label);
            });

        },

        setLegendMetaColumnsValue: function () {
            _.each(LineChartManageMVC.Variable.legendMetaColumns, function (metaColumn) {
                $("#legend-" + metaColumn.name).html('');
                var optionstr = '';

                _.each(_.uniq(_.map(LineChartManageMVC.Variable.reportData, metaColumn.name)), function (i) {
                    optionstr += '<option value="' + i + '">' + i + '</option>';
                });


                $("#legend-" + metaColumn.name).html(optionstr);

                $('#legend-' + metaColumn.name).trigger('chosen:updated');

            });
        }
    }
}
