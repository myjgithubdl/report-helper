/**
 * Created by bdf on 2019/10/23.
 */

var PublishReportChartMVC = {
    /**
     * 显示图表
     * @param reportCompose
     * @param showContent
     * @param dataList
     */
    showReportComposeChart: function (reportCompose, showContent, dataList) {
        if (!reportCompose) {
            return
        }
        var metaColumns = JSON.parse(reportCompose.metaColumns);
        var layoutMetaColumn = _.findWhere(metaColumns, {type: '1'});//布局列
        var dimensionMetaColumn = _.findWhere(metaColumns, {type: '2'});//维度列
        var statisticalMetaColumn = _.findWhere(metaColumns, {type: '3'});//统计列
        if (!layoutMetaColumn || !dimensionMetaColumn || !statisticalMetaColumn) {
            return
        }
        var xAxisName = layoutMetaColumn.name;
        var legendName = dimensionMetaColumn.name;
        var yAxisName = statisticalMetaColumn.name;
        var xAxisData = PublishReportChartMVC.getDistinctSortValues(xAxisName, dataList);
        var legendDataArray = PublishReportChartMVC.getDistinctSortValues(legendName, dataList);
        var legendAndyAxisArray = PublishReportChartMVC.getyAxisData(yAxisName, dataList, xAxisName, xAxisData, legendName, legendDataArray);
        var chatType = PublishReportChartMVC.getEchartType(showContent);
        var options = null;
        if (chatType == "pie" || chatType == "funnel") {
            options = PublishReportChartMVC.getPieChartOption(layoutMetaColumn.text, xAxisData,
                dimensionMetaColumn.text, legendDataArray,
                statisticalMetaColumn.text, legendAndyAxisArray,
                chatType);
        } else {
            options = PublishReportChartMVC.getChartOption(layoutMetaColumn.text, xAxisData,
                dimensionMetaColumn.text, legendDataArray,
                statisticalMetaColumn.text, legendAndyAxisArray,
                chatType);
        }
        PublishReportChartMVC.addChartOptionTitle(options, reportCompose)
        var chartId = PublishReportMVC.Util.getReportComposeChartDivId(reportCompose.uid);
        var echartInstance = echarts.init(document.getElementById(chartId));

        console.log(options)
        console.log(JSON.stringify(options))
        echartInstance.setOption(options, {notMerge: true});
    },

    /**
     * 显示画板
     * @param reportCompose
     * @param showContent
     * @param dataList
     */
    showReportComposeBoardChart: function (reportCompose, showContent, dataList) {
        if (!reportCompose) {
            return
        }
        var metaColumns = JSON.parse(reportCompose.metaColumns);
        var layoutMetaColumn = _.findWhere(metaColumns, {type: '1'});//布局列
        var dimensionMetaColumn = _.findWhere(metaColumns, {type: '2'});//维度列
        var statisticalMetaColumn = _.findWhere(metaColumns, {type: '3'});//统计列
        if (!layoutMetaColumn || !dimensionMetaColumn || !statisticalMetaColumn) {
            return
        }
        var xAxisName = layoutMetaColumn.name;
        var legendName = dimensionMetaColumn.name;
        var yAxisName = statisticalMetaColumn.name;
        var xAxisData = PublishReportChartMVC.getDistinctSortValues(xAxisName, dataList);
        var legendDataArray = PublishReportChartMVC.getDistinctSortValues(legendName, dataList);
        var legendAndyAxisArray = PublishReportChartMVC.getyAxisData(yAxisName, dataList, xAxisName, xAxisData, legendName, legendDataArray);
        console.log(xAxisData)
        console.log(legendDataArray)
        console.log(legendAndyAxisArray)
        var chatType = PublishReportChartMVC.getEchartType(showContent);
        var composeDivId = PublishReportMVC.Util.getReportComposeId(reportCompose.uid);
        var chartId = PublishReportMVC.Util.getReportComposeChartDivId(reportCompose.uid);
        var boardWidth = parseInt($("#" + composeDivId).css("width"))
        console.log("画板图表宽度："+boardWidth)
        if (!boardWidth) {
            boardWidth = document.body.scrollWidth
        }
        boardWidth = parseInt((boardWidth-50) / 2);
        console.log("画板图表宽度："+boardWidth)

        $("#" + chartId).empty();

        _.each(legendDataArray, function (legend, index) {
            var legendAndyAxisArrayTmpl = {};
            legendAndyAxisArrayTmpl[legend] = legendAndyAxisArray[legend];
            var options = null;
            if (chatType == "pie" || chatType == "funnel") {
                options = PublishReportChartMVC.getPieChartOption(layoutMetaColumn.text, xAxisData,
                    dimensionMetaColumn.text, legendDataArray,
                    "", legendAndyAxisArrayTmpl,
                    chatType);
            } else {
                options = PublishReportChartMVC.getChartOption(layoutMetaColumn.text, xAxisData,
                    dimensionMetaColumn.text, legendDataArray,""
                    , legendAndyAxisArrayTmpl,
                    chatType);
            }
            var newChartId = chartId + "-" + index;
            console.log("画板图表宽度："+boardWidth)
            $("#" + chartId).append('<div class="boardDiv"><div class="boardChart" id="' + newChartId + '"></div></div>');
            $("#" + newChartId).css({width: boardWidth + 'px'});

            options.title = {
                text: legend,
                link: "javascript:PublishReportMVC.Controller.showChartTableData('" + reportCompose.uid + "','"+legendName+"','"+legend+"')",
                target: 'self',
                //left:'center',
                top: '-5'
            }
            options.xAxis["axisTick"]={show:false};
            options.xAxis["splitLine"]=true;
            options.xAxis["name"]=null;
            options.series[0]["itemStyle"]={
                normal:{
                    color:"#333333"
                }
            }
            console.log(options)
            var echartInstance = echarts.init(document.getElementById(newChartId));
            echartInstance.setOption(options, {notMerge: true});
        })
        return;


    },

    /**
     * 获取数组对象中某一列去重后的排序值、对应于echart中的X轴、图例等
     * @param colName  JSON的key值
     * @param dataList  数组对象
     * @returns {*}
     */
    getDistinctSortValues: function (colName, dataList) {
        var allValues = _.map(dataList, function (obj) {
            return obj[colName];
        })
        var sortUniq = _.sortBy(_.uniq(allValues), function (num) {
            return num
        });
        return sortUniq;
    },
    /**
     * 获取Y轴的数据、返回json值，key为图列的值、value为图列和X轴的值所对用的Y值数组
     * @param yColName
     * @param dataList
     * @param xAxisColName
     * @param xAxisData
     * @param legendColName
     * @param legendDataArray
     * @returns {{}}
     */
    getyAxisData: function (yColName, dataList, xAxisColName, xAxisData, legendColName, legendDataArray) {
        var groupByLegendValueData = _.groupBy(dataList, function (obj) {
            return obj[legendColName];
        });//按照图例值分组的数据
        var retData = {};//可以为 legendDataArray 的值，值为legendColName列名为legendDataArray中的项对应 yColName的值
        _.each(legendDataArray, function (legendVal) {
            retData[legendVal] = [];
            var legendAllObjData = groupByLegendValueData[legendVal];//包含该图例值的所有数据
            var condition = {};
            condition[legendColName] = legendVal;
            _.each(xAxisData, function (xAxisVal) {//遍历取值
                condition[xAxisColName] = xAxisVal;
                var correctDataObj = _.findWhere(legendAllObjData, condition);
                if (correctDataObj) {
                    retData[legendVal].push({name: xAxisVal, value: correctDataObj[yColName]})
                } else {
                    retData[legendVal].push(null)
                }
            })
        });
        console.log(retData);
        return retData;
    },
    getSeries: function (legendAndyAxisArray, chatType) {
        var series = [];
        _.each(legendAndyAxisArray, function (seriesVal, seriesName) {
            series.push({name: seriesName, type: chatType, data: seriesVal})
        })
        return series;
    },
    getChartOption: function (xAxisName, xAxisData, legendName, legendDataArray, yAxisName, legendAndyAxisArray, chatType) {
        var option = {
            tooltip: {
                show: true,
                trigger: 'item'
            },
            grid: {
                show: false,
                borderWidth: 0,
                y: 50,
                y2: 50,
                x: 80,
                x2: 55
            },
            calculable: true,
            toolbox: {
                show: true,
                feature: {
                    saveAsImage: {show: true}//保存图片
                }
            },
            calculable: true
        };

        var yAxis = {
            type: 'value',
            splitLine: false,
            min: function (value) {
                if (value.max > 1) {
                    return Math.floor(value.min);
                } else {
                    return new Number(new String(value.min - 0.0001).substring(0, 6));
                }
            }
        };
        if (yAxisName) {
            yAxis["name"] = yAxisName;
        }
        var series = PublishReportChartMVC.getSeries(legendAndyAxisArray, chatType);

        option.yAxis = yAxis;
        option.grid.x = 80;
        option.legend = {data: legendDataArray, top: 'bottom'};
        option.yAxis = yAxis;
        option.series = series;
        var xAxis = {type: 'category', data: xAxisData, name: xAxisName};
        xAxis.splitLine = false;
        option.xAxis = xAxis;
        return option;
    },
    /**
     *
     * @param xAxisName X轴名称
     * @param xAxisData X轴数据
     * @param legendName    图例名称
     * @param legendDataArray   图例数据
     * @param yAxisName Y轴名称
     * @param legendAndyAxisArray   图例和数据
     * @param chatType  图表类型
     * @returns {{calculable: boolean, grid: {top: number, borderWidth: number, bottom: number, show: boolean}, tooltip: {formatter: string, show: boolean, trigger: string}, toolbox: {feature: {saveAsImage: {show: boolean}, restore: {show: boolean}, magicType: {show: boolean, type: [string, string], option: {funnel: {max: number, x: string, width: string, funnelAlign: string}}}, mark: {show: boolean}}, show: boolean}, label: {normal: {formatter: string, borderColor: string, borderRadius: number, borderWidth: number, rich: {a: {lineHeight: number, align: string}, b: {lineHeight: number}, hr: {borderColor: string, borderWidth: number, width: string, height: number}, per: {padding: number[], borderRadius: number}}}}}}
     */
    getPieChartOption: function (xAxisName, xAxisData, legendName, legendDataArray, yAxisName, legendAndyAxisArray, chatType) {
        var option = {
            tooltip: {
                show: true,
                trigger: 'item',
                formatter: "{a}<br/> {b}&nbsp;:&nbsp;{c} &nbsp;({d}%)"
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
            grid: {
                show: true,
                borderWidth: 0,
                top: 100,
                bottom: 50
            },
            toolbox: {
                show: true,
                feature: {
                    mark: {show: true},
                    //dataView: {show: true, readOnly: false},
                    magicType: {
                        show: true,
                        type: ['pie', 'funnel'],
                        option: {
                            funnel: {
                                x: '25%',
                                width: '50%',
                                funnelAlign: 'left',
                                max: 1548
                            }
                        }
                    },
                    restore: {show: false},
                    saveAsImage: {show: true}
                }
            },
            calculable: true
        };
        var series = PublishReportChartMVC.getSeries(legendAndyAxisArray, chatType);
        var pieNum = series.length;//饼图个数
        var avg = parseInt(88 / (2 * pieNum - 1));

        _.each(series, function (data, index) {
            console.log(data, index)
            if (chatType == 'pie') {
                data["center"] = ['50%', '55%'];
                var xCenter = (2 * index * avg);
                var yCenter = (((2 * index + 1) * avg));
                data["radius"] = [xCenter + '%', yCenter + '%'];
            } else if (chatType == 'funnel') {
                data["width"] = '75%';
                data["left"] = '10%';
            }

        })
        option["series"] = series;

        var newLegendData = [];
        if (legendDataArray.length > 1) {
            _.each(legendDataArray, function (leng) {
                newLegendData.push(leng)
            })
        }
        _.each(xAxisData, function (leng) {
            newLegendData.push(leng)
        })

        option["legend"] = {
            orient: 'vertical',
            left: 'left',
            top: '8%',
            //data: legendDataArray
            //data: xAxisData,
            data: newLegendData,
        };

        return option;
    },
    /**
     * 根据展示内容获取图表类型
     * @param showContent
     * @returns {*}
     */
    getEchartType: function (showContent) {
        if (!showContent)
            return 'line';

        if (showContent == 11 || showContent == 21) {
            return 'line';
        } else if (showContent == 12 || showContent == 22) {
            return 'bar';
        } else if (showContent == 13 || showContent == 23) {
            return 'pie';
        } else if (showContent == 14 || showContent == 24) {
            return 'funnel';
        } else if (showContent == 14 || showContent == 25) {
            return 'scatter';
        } else {
            return 'line';
        }
    },

    addChartOptionTitle: function (options, reportCompose) {
        if (reportCompose.name) {
            options.title = {
                text: reportCompose.name,
                link: "javascript:PublishReportMVC.Controller.showChartTableData('" + reportCompose.uid + "')",
                target: 'self',
                left: 'center',
                top: '-5'
            }
        }
    }
}