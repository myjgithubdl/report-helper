/**
 * Echart图表计算
 * @type {{Service: {}}}
 */
var ChartDataMVC = {
    Controller: {
        /**
         * 显示表格数据
         */
        showTableData: function () {
            var url = ReportHelper.ctxPath + '/report/preview/uid/' + uid + '?chartToTabel=Y';
            window.location.href = url
        }

    },
    Service: {
        /**
         * 根据给定的数据转化出图形数据（适用于有x轴、y轴的图形，如折线图、饼图）
         * @param allData   array，所有数据
         * @param xColName  string，x轴的列名称
         * @param yColName  string，y轴的列名称
         * @param legendColName string，图例列名称
         * @param legendData    array，图例的数据，会在allData中过略出legendColName对应的值在legendData中的数据
         * @returns {{xData: (Array|*), legendData: Array, yData}}
         */
        getChartData: function (allData, xColName, yColName, legendColName, legendData) {
            var filterAllData = [];//根据选择的维度列的值legendData过滤出的数据
            _.each(legendData, function (v, legendValIndex) {
                var filterData = _.filter(allData, function (rowData) {
                    var mactObj = {};
                    mactObj[legendColName] = v;
                    return _.isMatch(rowData, mactObj);
                });
                filterAllData.push(filterData);
            });

            filterAllData = _.flatten(filterAllData);

            var groupData = {};//按照选择的维度对数据分组，key是维度列选择的值，值是对象，其对象的key是X轴名称对应的值，对象的值是Y轴名称对应的值

            _.each(filterAllData, function (rowData) {
                var xValue = rowData[xColName];
                var yValue = rowData[yColName];
                var legendValue = rowData[legendColName];
                if (_.isUndefined(groupData[legendValue])) {
                    var obj = {};
                    obj[xValue] = yValue;
                    groupData[legendValue] = obj
                } else {
                    groupData[legendValue][xValue] = yValue
                }
            });

            var xData = _.uniq(_.map(filterAllData, xColName));//X轴数据，数组

            var legendData = [];//图例的数据
            var yData = {};//key是图例中的值，值是xData对应的值
            _.each(groupData, function (val, key) {
                console.log(val)
                legendData.push(key);
                var data = [];
                _.each(xData, function (x) {
                    if (_.isUndefined(val[x]) || _.isNull(val[x]) || !_.isNumber(val[x])) {
                        data.push(0)
                    } else {
                        data.push(val[x])
                    }

                });
                yData[key] = data
            })
            return {xData: xData, legendData: legendData, yData: yData};
        },

    }
}
